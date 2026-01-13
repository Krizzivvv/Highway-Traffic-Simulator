package fleet;

import java.util.*;
import java.io.*;
import vehicle.*;
import exception.*;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;
import interfaces.CargoCarrier;

public class FleetManager {
    private List<Vehicle> fleet;
    private Map<String, Vehicle> vehicleMap = new HashMap<>();
    private Set<String> idSet = new HashSet<>();



    public FleetManager() {
        fleet = new ArrayList<>();
    }

    private Vehicle createVehicleBaseFromCSV(String[] data) throws InvalidOperationException {
        if (data.length < 4) {
            throw new InvalidOperationException("Invalid CSV data for vehicle creation: insufficient fields.");
        }

        String type = data[0].trim();
        String id = data[1].trim();
        String model = data[2].trim();

        double maxSpeed;
        try {
            maxSpeed = Double.parseDouble(data[3]);
        } catch (NumberFormatException e) {
            throw new InvalidOperationException("Invalid maxSpeed in CSV data for " + type + " " + id + ": " + data[3]);
        }

        try {
            switch (type) {
                case "Car": {
                    if (data.length < 5) throw new InvalidOperationException("Insufficient data for Car " + id + ".");
                    int numWheels = Integer.parseInt(data[4]);
                    return new Car(id, model, maxSpeed, numWheels);
                }
                case "Truck": {
                    if (data.length < 5) throw new InvalidOperationException("Insufficient data for Truck " + id + ".");
                    int numWheels = Integer.parseInt(data[4]);
                    return new Truck(id, model, maxSpeed, numWheels);
                }
                case "Bus": {
                    if (data.length < 5) throw new InvalidOperationException("Insufficient data for Bus " + id + ".");
                    int numWheels = Integer.parseInt(data[4]);
                    return new Bus(id, model, maxSpeed, numWheels);
                }
                case "Airplane": {
                    if (data.length < 5) throw new InvalidOperationException("Insufficient data for Airplane " + id + ".");
                    double maxAltitude = Double.parseDouble(data[4]);
                    return new Airplane(id, model, maxSpeed, maxAltitude);
                }
                case "CargoShip": {
                    if (data.length < 5) throw new InvalidOperationException("Insufficient data for CargoShip " + id + ".");
                    boolean hasSail = Boolean.parseBoolean(data[4]);
                    return new CargoShip(id, model, maxSpeed, hasSail);
                }
                default:
                    throw new InvalidOperationException("Unknown vehicle type in CSV: " + type);
            }
        } catch (NumberFormatException e) {
            throw new InvalidOperationException("Number format error creating " + type + " " + id + ": " + e.getMessage());
        }
    }

    public void saveToFile(String filename) {
        if (!filename.toLowerCase().endsWith(".csv")) {
            filename += ".csv";
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Type,Id,Model,MaxSpeed,ExtraParam1,ExtraParam2,Passengers,Cargo,Mileage,FuelLevel,MaintenanceNeeded");
            writer.newLine();

            for (Vehicle v : fleet) {
                StringBuilder line = new StringBuilder();

                line.append(v.getClass().getSimpleName()).append(",");
                line.append(v.getId()).append(",");
                line.append("\"").append(v.getModel()).append("\"").append(",");
                line.append(String.format("%.2f", v.getMaxSpeed())).append(",");

                if (v instanceof LandVehicle) {
                    line.append(((LandVehicle) v).getNumWheels()).append(",");
                    line.append(",");
                } else if (v instanceof AirVehicle) {
                    line.append(((AirVehicle) v).getMaxAltitude()).append(",");
                    line.append(",");
                } else if (v instanceof WaterVehicle) {
                    line.append(((WaterVehicle) v).hasSail()).append(",");
                    line.append(",");
                } else {
                    line.append(",").append(",");
                }
                if (v instanceof PassengerCarrier pc) {
                    line.append(pc.getCurrentPassengers());
                }
                line.append(",");
                if (v instanceof CargoCarrier cc) {
                    line.append(String.format("%.2f", cc.getCurrentCargo()));
                }
                line.append(",");

                line.append(String.format("%.2f", v.getCurrentMileage())).append(",");

                if (v instanceof FuelConsumable fc) {
                    line.append(String.format("%.2f", fc.getFuelLevel()));
                }
                line.append(",");
                if (v instanceof Maintainable m) {
                    line.append(m.needsMaintenance());
                }

                writer.write(line.toString());
                writer.newLine();
            }

            System.out.println("Fleet saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving fleet to " + filename + ": " + e.getMessage());
        }
    }



    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            fleet.clear();
            idSet.clear();

            String line;
            boolean firstLine = true;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] data = parseCSVLine(line);

                if (data.length < 11) {
                    System.out.println("Warning: Skipping line " + lineNumber + " due to insufficient fields: " + line);
                    continue;
                }

                try {
                    String type = data[0].trim();
                    String id = data[1].trim();
                    String model = data[2].replace("\"", "").trim();
                    String extra1 = data[4].trim();
                    String passengersStr = data[6].trim();
                    String cargoStr = data[7].trim();
                    double mileage = Double.parseDouble(data[8]);
                    double fuelLevel = Double.parseDouble(data[9]);
                    boolean maintenanceNeeded = Boolean.parseBoolean(data[10]);
                    Vehicle v = createVehicleBaseFromCSV(new String[]{type, id, model, data[3], extra1});
                    if (v instanceof PassengerCarrier pc && !passengersStr.isEmpty()) {
                        try {
                            int passengers = Integer.parseInt(passengersStr);
                            if (passengers > pc.getCurrentPassengers()) {
                                pc.boardPassengers(passengers - pc.getCurrentPassengers());
                            } else if (passengers < pc.getCurrentPassengers()) {
                                pc.disembarkPassengers(pc.getCurrentPassengers() - passengers);
                            }
                        } catch (Exception e) {
                            System.out.println("Warning: Could not restore passengers for " + id + ": " + e.getMessage());
                        }
                    }
                    if (v instanceof CargoCarrier cc && !cargoStr.isEmpty()) {
                        try {
                            double cargo = Double.parseDouble(cargoStr);
                            if (cargo > cc.getCurrentCargo()) {
                                cc.loadCargo(cargo - cc.getCurrentCargo());
                            } else if (cargo < cc.getCurrentCargo()) {
                                cc.unloadCargo(cc.getCurrentCargo() - cargo);
                            }
                        } catch (Exception e) {
                            System.out.println("Warning: Could not restore cargo for " + id + ": " + e.getMessage());
                        }
                    }
                    if (mileage > 0) {
                        try {
                            v.addMileage(mileage);
                        } catch (InvalidOperationException e) {
                            System.out.println("Warning: Could not restore mileage for " + id + ": " + e.getMessage());
                        }
                    }
                    if (v instanceof FuelConsumable fc) {
                        try {
                            if (fuelLevel > 0) {
                                fc.refuel(fuelLevel);
                            }
                        } catch (InvalidOperationException e) {
                            System.out.println("Warning: Could not restore fuel for " + id + ": " + e.getMessage());
                        }
                    }
                    if (v instanceof Maintainable m && maintenanceNeeded) {
                        m.scheduleMaintenance();
                    }
                    fleet.add(v);
                    idSet.add(v.getId());

                } catch (Exception e) {
                    System.out.println("Skipping invalid line " + lineNumber + ": " + e.getMessage());
                }
            }

            System.out.println("Fleet loaded successfully from " + filename);

        } catch (IOException e) {
            System.out.println("Error loading fleet from " + filename + ": " + e.getMessage());
        }
    }


    private String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens.toArray(new String[0]);
    }


    public void addVehicle(Vehicle vehicle) throws InvalidOperationException {
    if (!idSet.add(vehicle.getId())) {
        throw new InvalidOperationException("Duplicate ID detected: Vehicle with ID " + vehicle.getId() + " already exists.");
    }

    fleet.add(vehicle);
    vehicleMap.put(vehicle.getId(), vehicle);
}


    public void removeVehicle(String id) throws InvalidOperationException {
        Vehicle removed = vehicleMap.remove(id);
        if (removed == null) {
            throw new InvalidOperationException("Vehicle with ID " + id + " not found.");
        }
        fleet.remove(removed);
        idSet.remove(id);
    }



    public void startAllJourneys(double distance) {
        for (Vehicle v : fleet) {
            try {
                v.move(distance);
            } catch (InvalidOperationException | InsufficientFuelException e) {
                System.out.println("Cannot move vehicle " + v.getId() + ": " + e.getMessage());
            }
        }
    }

    public double getTotalFuelConsumption(double distance) {
        double totalConsumed = 0.0;
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                totalConsumed += distance / v.calculateFuelEfficiency();
            }
        }
        return totalConsumed;
    }

    public void refuelAll(double amount) {
        if (amount <= 0) {
            System.out.println("Refuel amount must be positive.");
            return;
        }
        int refueledCount = 0;
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable fc) {
                try {
                    fc.refuel(amount);
                    refueledCount++;
                } catch (InvalidOperationException e) {
                    System.out.println("Could not refuel vehicle " + v.getId() + ": " + e.getMessage());
                }
            }
        }
        System.out.println(refueledCount + " fuel-consumable vehicles refueled with " + amount + " units.");
    }

    public void maintainAll() {
        int maintainedCount = 0;
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable m && m.needsMaintenance()) {
                m.performMaintenance();
                maintainedCount++;
            }
        }
        if (maintainedCount == 0) {
            System.out.println("No vehicles needed maintenance.");
        } else {
            System.out.println(maintainedCount + " vehicles maintained.");
        }
    }

    public List<Vehicle> searchByType(Class<?> type) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (type.isInstance(v)) {
                result.add(v);
            }
        }
        return result;
    }

    public List<Vehicle> getVehiclesNeedingMaintenance() {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable m && m.needsMaintenance()) {
                result.add(v);
            }
        }
        return result;
    }

    public void sortFleetByEfficiency() {
        Collections.sort(fleet);
    }
    

    public String generateReport() {
        StringBuilder report = new StringBuilder("=== Fleet Report ===\n");
        if (fleet.isEmpty()) {
            report.append("No vehicles in the fleet.\n");
            return report.toString();
        }

        report.append("Total vehicles: ").append(fleet.size()).append("\n");
        Set<String> distinctModels = new HashSet<>();
        for (Vehicle v : fleet) {
            distinctModels.add(v.getModel());
        }
        report.append("Distinct models: ").append(distinctModels.size()).append("\n");

        Map<String, Integer> countByType = new HashMap<>();
        for (Vehicle v : fleet) {
            String typeName = v.getClass().getSimpleName();
            countByType.put(typeName, countByType.getOrDefault(typeName, 0) + 1);
        }
        report.append("Vehicle counts by type:\n");
        for (Map.Entry<String, Integer> entry : countByType.entrySet()) {
            report.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        Vehicle fastest = Collections.max(fleet, Comparator.comparingDouble(Vehicle::getMaxSpeed));
        Vehicle slowest = Collections.min(fleet, Comparator.comparingDouble(Vehicle::getMaxSpeed));
        report.append("Fastest vehicle: ").append(fastest.getModel())
            .append(" (").append(fastest.getMaxSpeed()).append(" km/h)\n");
        report.append("Slowest vehicle: ").append(slowest.getModel())
            .append(" (").append(slowest.getMaxSpeed()).append(" km/h)\n");

        double totalEfficiency = 0.0;
        int fuelConsumableCount = 0;
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                totalEfficiency += v.calculateFuelEfficiency();
                fuelConsumableCount++;
            }
        }
        double avgEfficiency = 0.0;
        if (fuelConsumableCount > 0) {
            avgEfficiency = totalEfficiency / fuelConsumableCount;
        }
        report.append("Average fuel efficiency (for fuel-consumable vehicles): ")
            .append(String.format("%.2f", avgEfficiency)).append(" km/l\n");

        double totalMileage = 0.0;
        for (Vehicle v : fleet) {
            totalMileage += v.getCurrentMileage();
        }
        report.append("Total mileage: ").append(String.format("%.2f", totalMileage)).append(" km\n");

        int needsMaintenance = 0;
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable m && m.needsMaintenance()) {
                needsMaintenance++;
            }
        }
        report.append("Vehicles needing maintenance: ").append(needsMaintenance).append("\n");

        return report.toString();
    }
    public void sortById() {
        Collections.sort(fleet, Comparator.comparing(Vehicle::getId));
    }

    public void sortByModel() {
        Collections.sort(fleet, Comparator.comparing(Vehicle::getModel));
    }

    public void sortByMaxSpeed() {
        Collections.sort(fleet, Comparator.comparingDouble(Vehicle::getMaxSpeed).reversed());
    }

    public void sortByType() {
        Collections.sort(fleet, Comparator.comparing(v -> v.getClass().getSimpleName()));
    }

    public void sortByFuelLevel() {
        Collections.sort(fleet, (v1, v2) -> {
            double f1 = (v1 instanceof FuelConsumable) ? ((FuelConsumable) v1).getFuelLevel() : 0.0;
            double f2 = (v2 instanceof FuelConsumable) ? ((FuelConsumable) v2).getFuelLevel() : 0.0;
            return Double.compare(f2, f1);
        });
    }

    public void sortByMileage() {
        Collections.sort(fleet, Comparator.comparingDouble(Vehicle::getCurrentMileage).reversed());
    }

    public List<Vehicle> getFleet() { 
        return new ArrayList<>(fleet);
    }
}