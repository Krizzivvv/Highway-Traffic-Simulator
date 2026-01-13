package simulator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import vehicle.*;
import fleet.FleetManager;
import exception.*;

public class HighwaySimulatorGUI extends JFrame {
    private static int highwayDistanceUnsync = 0;
    private static int highwayDistanceSync = 0;
    private static int expectedDistance = 0;
    private static final ReentrantLock lock = new ReentrantLock();
    
    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private JLabel highwayCounterLabel;
    private JLabel expectedLabel;
    private JLabel dataLossLabel;
    private JButton startButton, pauseButton, resumeButton, stopButton;
    private JCheckBox syncCheckBox;
    
    private FleetManager fleetManager;
    private List<VehicleThread> vehicleThreads;
    private boolean isRunning = false;
    private boolean useSynchronization = false;
    
    public HighwaySimulatorGUI() {
        setTitle("Fleet Highway Simulator - Race Condition Demo");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        fleetManager = new FleetManager();
        vehicleThreads = new ArrayList<>();
        
        initializeComponents();
        createSampleFleet();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeComponents() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        JLabel titleLabel = new JLabel("Fleet Highway Simulator - Multithreading Demo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"Vehicle ID", "Type", "Model", "Mileage (km)", "Fuel (L)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        vehicleTable = new JTable(tableModel);
        vehicleTable.setFont(new Font("Monospaced", Font.PLAIN, 13));
        vehicleTable.setRowHeight(30);
        vehicleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        vehicleTable.getTableHeader().setBackground(new Color(200, 220, 240));
        
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel counterPanel = new JPanel();
        counterPanel.setLayout(new BoxLayout(counterPanel, BoxLayout.Y_AXIS));
        counterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Highway Statistics"),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        counterPanel.setBackground(new Color(245, 245, 250));
        
        highwayCounterLabel = new JLabel("Total Highway Distance: 0 km");
        highwayCounterLabel.setFont(new Font("Arial", Font.BOLD, 18));
        highwayCounterLabel.setForeground(new Color(0, 100, 0));
        highwayCounterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        expectedLabel = new JLabel("Expected Distance: 0 km");
        expectedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        expectedLabel.setForeground(new Color(80, 80, 80));
        expectedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dataLossLabel = new JLabel("Data Loss: 0 km (0.00%)");
        dataLossLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dataLossLabel.setForeground(Color.RED);
        dataLossLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        counterPanel.add(highwayCounterLabel);
        counterPanel.add(Box.createVerticalStrut(8));
        counterPanel.add(expectedLabel);
        counterPanel.add(Box.createVerticalStrut(8));
        counterPanel.add(dataLossLabel);
        
        centerPanel.add(counterPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setBackground(new Color(245, 245, 245));
        
        JPanel syncPanel = new JPanel();
        syncPanel.setLayout(new BoxLayout(syncPanel, BoxLayout.Y_AXIS));
        syncPanel.setBackground(new Color(245, 245, 245));
        syncPanel.setBorder(BorderFactory.createTitledBorder("Synchronization Mode"));
        
        syncCheckBox = new JCheckBox("Use Synchronization");
        syncCheckBox.setFont(new Font("Arial", Font.BOLD, 13));
        syncCheckBox.setBackground(new Color(245, 245, 245));
        syncCheckBox.addActionListener(e -> {
            useSynchronization = syncCheckBox.isSelected();
            if (syncCheckBox.isSelected()) {
                JOptionPane.showMessageDialog(this, 
                    "Synchronization ENABLED\nRace condition will be FIXED",
                    "Mode Changed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Synchronization DISABLED\nRace condition will occur!",
                    "Mode Changed", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JLabel syncInfo = new JLabel("<html><i>Toggle to see race condition</i></html>");
        syncInfo.setFont(new Font("Arial", Font.PLAIN, 11));
        
        syncPanel.add(syncCheckBox);
        syncPanel.add(Box.createVerticalStrut(5));
        syncPanel.add(syncInfo);
        
        controlPanel.add(syncPanel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        startButton = createStyledButton("▶ Start Simulation", new Color(34, 139, 34));
        pauseButton = createStyledButton("⏸ Pause", new Color(255, 140, 0));
        resumeButton = createStyledButton("▶ Resume", new Color(0, 128, 255));
        stopButton = createStyledButton("⏹ Stop", new Color(220, 20, 60));
        Dimension buttonSize = new Dimension(180, 40);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
        
        JButton refuelButton = createStyledButton("⛽ Refuel All Vehicles", new Color(70, 130, 180));
        refuelButton.setMaximumSize(buttonSize);
        
        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> pauseSimulation());
        resumeButton.addActionListener(e -> resumeSimulation());
        stopButton.addActionListener(e -> stopSimulation());
        refuelButton.addActionListener(e -> refuelAllVehicles());
        startButton.setMaximumSize(buttonSize);
        pauseButton.setMaximumSize(buttonSize);
        resumeButton.setMaximumSize(buttonSize);
        stopButton.setMaximumSize(buttonSize);
        
        controlPanel.add(startButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(pauseButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(resumeButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(stopButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(refuelButton);
        controlPanel.add(Box.createVerticalGlue());
        
        add(controlPanel, BorderLayout.EAST);
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void createSampleFleet() {
        try {
            Car car = new Car("C001", "Toyota Camry", 180, 4);
            car.refuel(100);
            
            Truck truck = new Truck("T001", "Volvo FH16", 120, 6);
            truck.refuel(150);
            
            Bus bus = new Bus("B001", "Mercedes Citaro", 100, 6);
            bus.refuel(120);
            
            fleetManager.addVehicle(car);
            fleetManager.addVehicle(truck);
            fleetManager.addVehicle(bus);
            
            updateVehicleTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error creating sample fleet: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateVehicleTable() {
        tableModel.setRowCount(0);
        for (Vehicle v : fleetManager.getFleet()) {
            String fuelLevel = "N/A";
            if (v instanceof interfaces.FuelConsumable) {
                fuelLevel = String.format("%.2f", ((interfaces.FuelConsumable) v).getFuelLevel());
            }
            
            String status = "Ready";
            VehicleThread vt = findThreadForVehicle(v.getId());
            if (vt != null) {
                status = vt.getStatus();
            }
            
            tableModel.addRow(new Object[]{
                v.getId(),
                v.getClass().getSimpleName(),
                v.getModel(),
                String.format("%.2f", v.getCurrentMileage()),
                fuelLevel,
                status
            });
        }
    }
    
    private VehicleThread findThreadForVehicle(String id) {
        for (VehicleThread vt : vehicleThreads) {
            if (vt.vehicle.getId().equals(id)) {
                return vt;
            }
        }
        return null;
    }
    
    private void startSimulation() {
        if (fleetManager.getFleet().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No vehicles in fleet!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        highwayDistanceUnsync = 0;
        highwayDistanceSync = 0;
        expectedDistance = 0;
        
        isRunning = true;
        
        vehicleThreads.clear();
        for (Vehicle v : fleetManager.getFleet()) {
            VehicleThread thread = new VehicleThread(v);
            vehicleThreads.add(thread);
            thread.start();
        }
        
        javax.swing.Timer updateTimer = new javax.swing.Timer(200, e -> {
            if (!isRunning) {
                ((javax.swing.Timer)e.getSource()).stop();
                return;
            }
            updateVehicleTable();
            updateCounterDisplay();
        });
        updateTimer.start();
        
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        
        String mode = useSynchronization ? "SYNCHRONIZED (No Race Condition)" : "UNSYNCHRONIZED (Race Condition Active!)";
        JOptionPane.showMessageDialog(this, 
            "Simulation Started!\n\nMode: " + mode + "\n\nWatch the Data Loss counter!",
            "Simulation Started", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void pauseSimulation() {
        for (VehicleThread vt : vehicleThreads) {
            vt.pauseVehicle();
        }
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(true);
    }
    
    private void resumeSimulation() {
        for (VehicleThread vt : vehicleThreads) {
            vt.resumeVehicle();
        }
        pauseButton.setEnabled(true);
        resumeButton.setEnabled(false);
    }
    
    private void stopSimulation() {
        isRunning = false;
        for (VehicleThread vt : vehicleThreads) {
            vt.stopVehicle();
        }
        
        for (VehicleThread vt : vehicleThreads) {
            try {
                vt.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        vehicleThreads.clear();
        
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
        
        updateVehicleTable();
        
        int currentCounter = useSynchronization ? highwayDistanceSync : highwayDistanceUnsync;
        int dataLoss = expectedDistance - currentCounter;
        double lossPercent = expectedDistance > 0 ? (dataLoss * 100.0 / expectedDistance) : 0;
        
        String message = String.format(
            "Simulation Stopped!\n\n" +
            "Final Statistics:\n" +
            "Expected Distance: %d km\n" +
            "Actual Distance: %d km\n" +
            "Data Loss: %d km (%.2f%%)\n\n" +
            "Mode: %s",
            expectedDistance, currentCounter, dataLoss, lossPercent,
            useSynchronization ? "Synchronized" : "Unsynchronized"
        );
        
        JOptionPane.showMessageDialog(this, message, "Simulation Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateCounterDisplay() {
        int currentCounter = useSynchronization ? highwayDistanceSync : highwayDistanceUnsync;
        
        highwayCounterLabel.setText("Total Highway Distance: " + currentCounter + " km");
        expectedLabel.setText("Expected Distance: " + expectedDistance + " km");
        
        int dataLoss = expectedDistance - currentCounter;
        double lossPercent = expectedDistance > 0 ? (dataLoss * 100.0 / expectedDistance) : 0;
        
        dataLossLabel.setText(String.format("Data Loss: %d km (%.2f%%)", dataLoss, lossPercent));
        
        if (dataLoss == 0) {
            dataLossLabel.setForeground(new Color(0, 128, 0));
        } else if (lossPercent < 5) {
            dataLossLabel.setForeground(new Color(255, 140, 0));
        } else {
            dataLossLabel.setForeground(Color.RED);
        }
    }
    
    private void refuelAllVehicles() {
        String amountStr = JOptionPane.showInputDialog(this, 
            "Enter fuel amount to add to ALL vehicles (liters):",
            "Refuel All Vehicles",
            JOptionPane.QUESTION_MESSAGE);
        
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Fuel amount must be positive!",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (amount > 1000) {
                    JOptionPane.showMessageDialog(this, 
                        "Fuel amount too large! Maximum 1000 liters per refuel.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int refueledCount = 0;
                StringBuilder report = new StringBuilder();
                
                for (Vehicle v : fleetManager.getFleet()) {
                    if (v instanceof interfaces.FuelConsumable) {
                        interfaces.FuelConsumable fc = (interfaces.FuelConsumable) v;
                        double oldFuel = fc.getFuelLevel();
                        fc.refuel(amount);
                        double newFuel = fc.getFuelLevel();
                        
                        report.append(String.format("%s: %.2f L → %.2f L\n", 
                            v.getId(), oldFuel, newFuel));
                        
                        // Resume vehicle if it was out of fuel
                        VehicleThread vt = findThreadForVehicle(v.getId());
                        if (vt != null && "Out of Fuel".equals(vt.getStatus())) {
                            vt.resumeVehicle();
                        }
                        
                        refueledCount++;
                    }
                }
                
                updateVehicleTable();
                
                JOptionPane.showMessageDialog(this, 
                    "✓ Refueled " + refueledCount + " vehicles with " + amount + " liters each!\n\n" + report.toString(),
                    "Refuel Complete", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid number format! Please enter a valid number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error refueling vehicles: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class VehicleThread extends Thread {
        private Vehicle vehicle;
        private volatile boolean running = true;
        private volatile boolean paused = false;
        private String status = "Running";
        
        public VehicleThread(Vehicle vehicle) {
            this.vehicle = vehicle;
            setName("VehicleThread-" + vehicle.getId());
        }
        
        @Override
        public void run() {
            while (running && isRunning) {
                if (!paused) {
                    try {
                        if (vehicle instanceof interfaces.FuelConsumable) {
                            interfaces.FuelConsumable fc = (interfaces.FuelConsumable) vehicle;
                            if (fc.getFuelLevel() <= 0.1) {
                                status = "Out of Fuel";
                                paused = true;
                                continue;
                            }
                        }
                        
                        double distance = 1.0;
                        vehicle.move(distance);
                        status = "Running";
                        
                        expectedDistance++;
                        
                        if (useSynchronization) {
                            lock.lock();
                            try {
                                highwayDistanceSync++;
                            } finally {
                                lock.unlock();
                            }
                        } else {
                            int temp = highwayDistanceUnsync;
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                            }
                            highwayDistanceUnsync = temp + 1;
                        }
                        
                        Thread.sleep(1000);
                        
                    } catch (InsufficientFuelException e) {
                        status = "Out of Fuel";
                        paused = true;
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        status = "Error";
                        System.err.println("Error in thread " + vehicle.getId() + ": " + e.getMessage());
                        paused = true;
                    }
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            status = "Stopped";
        }
        
        public void pauseVehicle() {
            paused = true;
            status = "Paused";
        }
        
        public void resumeVehicle() {
            paused = false;
            status = "Running";
        }
        
        public void stopVehicle() {
            running = false;
            interrupt();
        }
        
        public String getStatus() {
            return status;
        }
    }

    public void loadFleetFromFile(String filename) {
        try {
            fleetManager.loadFromFile(filename);
            updateVehicleTable();
            JOptionPane.showMessageDialog(this, "Loaded: " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load file: " + e.getMessage());
        }
    }

    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter CSV filename to load: ");
        String file = sc.nextLine().trim();
        SwingUtilities.invokeLater(() -> {
            HighwaySimulatorGUI gui = new HighwaySimulatorGUI();
            if (!file.isEmpty()) gui.loadFleetFromFile(file);
        sc.close();
        });
    }

}