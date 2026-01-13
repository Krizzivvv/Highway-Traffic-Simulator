package main;

import java.util.*;

import fleet.FleetManager;
import interfaces.CargoCarrier;
import vehicle.*;
import exception.*;

public class Main {

    public static void main(String[] args) {
        FleetManager fleetManager = new FleetManager();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Demo: Creating sample fleet ===");
        try {
            Car car = new Car("C001", "Toyota", 180, 4);
            car.boardPassengers(4);

            Truck truck = new Truck("T001", "Volvo", 120, 6);
            truck.loadCargo(3000);

            Bus bus = new Bus("B001", "Mercedes", 100, 6);
            bus.boardPassengers(30);
            bus.loadCargo(499);

            Airplane plane = new Airplane("A001", "Boeing 747", 900, 10000);
            plane.boardPassengers(180);
            plane.loadCargo(9999);

            CargoShip ship = new CargoShip("S001", "Maersk", 40, true);
            ship.loadCargo(49999);

            fleetManager.addVehicle(car);
            fleetManager.addVehicle(truck);
            fleetManager.addVehicle(bus);
            fleetManager.addVehicle(plane);
            fleetManager.addVehicle(ship);

            System.out.println("\n--- Simulating journey of 100 km ---");
            fleetManager.startAllJourneys(100);

            System.out.println("\n--- Generating report ---");
            System.out.println(fleetManager.generateReport());

            String demoCSV = "fleet_demo.csv";
            fleetManager.saveToFile(demoCSV);
            System.out.println("Demo fleet saved to file: " + demoCSV);

        } catch (InvalidOperationException | OverloadException e) {
            System.out.println("Demo Error: " + e.getMessage());
        }

        while (true) {
            showMenu();
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Please enter a choice!");
                continue;
            }

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1 -> addVehicleCLI(sc, fleetManager);
                    case 2 -> removeVehicleCLI(sc, fleetManager);
                    case 3 -> startJourneyCLI(sc, fleetManager);
                    case 4 -> refuelAllCLI(sc, fleetManager);
                    case 5 -> fleetManager.maintainAll();
                    case 6 -> System.out.println(fleetManager.generateReport());
                    case 7 -> saveFleetCLI(sc, fleetManager);
                    case 8 -> loadFleetCLI(sc, fleetManager);
                    case 9 -> searchByTypeCLI(sc, fleetManager);
                    case 10 -> listMaintenanceVehicles(fleetManager);
                    case 11 -> editPassengersCLI(sc, fleetManager);
                    case 12 -> editCargoCLI(sc, fleetManager);
                    case 13 -> sortVehiclesCLI(sc, fleetManager);
                    case 14 -> {
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Invalid menu choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        }

    private static void showMenu() {
        System.out.println("\n=== Fleet Management Menu ===");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Remove Vehicle");
        System.out.println("3. Start Journey");
        System.out.println("4. Refuel All");
        System.out.println("5. Perform Maintenance");
        System.out.println("6. Generate Report");
        System.out.println("7. Save Fleet");
        System.out.println("8. Load Fleet");
        System.out.println("9. Search by Type");
        System.out.println("10. List Vehicles Needing Maintenance");
        System.out.println("11. Edit Passenger Count");
        System.out.println("12. Edit Cargo Load");
        System.out.println("13. Sort Vehicles");
        System.out.println("14. Exit");
        System.out.print("Enter choice: ");
    }



    private static void addVehicleCLI(Scanner sc, FleetManager fleetManager) {
        try {
            System.out.println("\nSelect vehicle type to add:");
            System.out.println("1. Car");
            System.out.println("2. Truck");
            System.out.println("3. Bus");
            System.out.println("4. Airplane");
            System.out.println("5. CargoShip");
            System.out.print("Enter number (1-5): ");
            int typeChoice = Integer.parseInt(sc.nextLine().trim());
            
            if (typeChoice < 1 || typeChoice > 5) {
                System.out.println("Invalid vehicle type choice!");
                return;
            }
            System.out.print("Enter vehicle ID: "); 
            String id = sc.nextLine().trim();

            System.out.print("Enter model name: ");
            String model = sc.nextLine().trim();

            System.out.print("Enter max speed (must be positive): ");
            double maxSpeed = Double.parseDouble(sc.nextLine().trim());
            if (maxSpeed <= 0) {
                System.out.println("Error: Max speed must be positive!");
                return;
            }

            switch (typeChoice) {
                case 1 -> {
                    System.out.print("Enter number of wheels:");
                    String wheelsStr = sc.nextLine().trim();
                    int wheels;
                    try {
                        wheels = Integer.parseInt(wheelsStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid number of wheels format for Car.");
                        return;
                    }
                    if (wheels <= 0 ) {
                        System.out.println("Error: Number of wheels for Car must be between 1 and 18.");
                    }
                    Car car = new Car(id, model, maxSpeed, wheels);

                    System.out.print("Enter passengers (0-5): ");
                    String passengersStr = sc.nextLine().trim();

                    if (!passengersStr.isEmpty()) {
                        try {
                            int passengers = Integer.parseInt(passengersStr);

                            if (passengers < 0 || passengers > 5) {
                                System.out.println("Error: Invalid passenger count for Car. Must be between 0 and 5.");
                                return;
                            }

                            if (passengers > 0) {
                                car.boardPassengers(passengers);
                                System.out.println("Boarded " + passengers + " passengers.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid passenger count format for Car.");
                            return;
                        } catch (OverloadException | InvalidOperationException e) {
                            System.out.println("Error: Failed to board passengers despite validation - " + e.getMessage() + ". Car not added.");
                        }
                    }
                    fleetManager.addVehicle(car);
                    System.out.println("Car added successfully!");
                }
                case 2 -> {
                    System.out.print("Enter number of wheels ");
                    String wheelsStr = sc.nextLine().trim();
                    int wheels;
                    try {
                        wheels = Integer.parseInt(wheelsStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid number of wheels format for Truck.");
                        return;
                    }
                    if (wheels <= 0) {
                        System.out.println("Error: Number of wheels for Truck must be between 1 and 18.");
                        return;
                    }
                    Truck truck = new Truck(id, model, maxSpeed, wheels);
                    System.out.print("Enter cargo load (kg, 0-5000): ");
                    String cargoStr = sc.nextLine().trim();
                    if (!cargoStr.isEmpty()) {
                        try {
                            double cargo = Double.parseDouble(cargoStr);
                            if (cargo < 0 || cargo > 5000) {
                                System.out.println("Error: Invalid cargo load for Truck. Must be between 0 and 5000 kg.");
                                return;
                            }
                            if (cargo > 0) {
                                truck.loadCargo(cargo);
                                System.out.println("Loaded " + cargo + " kg of cargo.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid cargo load format for Truck.");
                            return;
                        } catch (OverloadException | InvalidOperationException e) {
                            System.out.println("Error: Failed to load cargo despite validation - " + e.getMessage() + ". Truck not added.");
                            return;
                        }
                    }
                    fleetManager.addVehicle(truck);
                    System.out.println("Truck added successfully!");
                }
                case 3 -> {
                    System.out.print("Enter number of wheels : ");
                    String wheelsStr = sc.nextLine().trim();
                    int wheels;
                    try {
                        wheels = Integer.parseInt(wheelsStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid number of wheels format for Bus.");
                        return;
                    }
                    if (wheels <= 0) {
                        System.out.println("Error: Number of wheels for Bus must be between");
                        return;
                    }
                    Bus bus = new Bus(id, model, maxSpeed, wheels);
                    System.out.print("Enter passengers (0-50): ");
                    String passengersStr = sc.nextLine().trim();
                    if (!passengersStr.isEmpty()) {
                        try {
                            int passengers = Integer.parseInt(passengersStr);
                            if (passengers < 0 || passengers > 50) {
                                System.out.println("Error: Invalid passenger count for Bus. Must be between 0 and 50.");
                                return;
                            }
                            if (passengers > 0) {
                                bus.boardPassengers(passengers);
                                System.out.println("Boarded " + passengers + " passengers.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid passenger count format for Bus.");
                            return;
                        } catch (OverloadException | InvalidOperationException e) {
                            System.out.println("Error: Failed to board passengers despite validation - " + e.getMessage() + ". Bus not added.");
                            return;
                        }
                    }
                    System.out.print("Enter cargo load (kg, 0-500): ");
                    String cargoStr = sc.nextLine().trim();

                    if (!cargoStr.isEmpty()) {
                        try {
                            double cargo = Double.parseDouble(cargoStr);
                            if (cargo < 0 || cargo > 500) {
                                System.out.println("Error: Invalid cargo load for Bus. Must be between 0 and 500 kg.");
                                return;
                            }
                            if (cargo > 0) {
                                bus.loadCargo(cargo);
                                System.out.println("Loaded " + cargo + " kg of cargo.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid cargo load format for Bus.");
                            return;
                        } catch (OverloadException | InvalidOperationException e) {
                            System.out.println("Error: Failed to load cargo despite validation - " + e.getMessage() + ". Bus not added.");
                            return;
                        }
                    }
                    fleetManager.addVehicle(bus);
                    System.out.println("Bus added successfully!");
                }
                case 4 -> {
                        System.out.print("Enter max altitude (meters, must be non-negative): ");
                        String maxAltitudeStr = sc.nextLine().trim();
                        double maxAltitude;
                        try {
                            maxAltitude = Double.parseDouble(maxAltitudeStr);
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid max altitude format.");
                            return; 
                        }
                        if (maxAltitude < 0) {
                            System.out.println("Error: Max altitude cannot be negative.");
                            return;
                        }
                        Airplane plane = new Airplane(id, model, maxSpeed, maxAltitude);
                        System.out.print("Enter passengers (0-200): ");
                        String passengersStr = sc.nextLine().trim();
                        if (!passengersStr.isEmpty()) {
                            try {
                                int passengers = Integer.parseInt(passengersStr);
                                if (passengers < 0 || passengers > 200) {
                                    System.out.println("Error: Invalid passenger count for Airplane (0-200). Vehicle not added.");
                                    return;
                                }

                                if (passengers > 0) {
                                    plane.boardPassengers(passengers);
                                    System.out.println("Boarded " + passengers + " passengers.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Invalid passenger count format for Airplane. Vehicle not added.");
                            } catch (OverloadException | InvalidOperationException e) {
                                System.out.println("Error: Could not board passengers despite validation - " + e.getMessage() + ". Vehicle not added.");
                                return;
                            }
                        }
                        fleetManager.addVehicle(plane);
                        System.out.println("Airplane added successfully!");
                    }
                case 5 -> {
                    System.out.print("Does it have sails? (Enter 'true' or 'false'): ");
                    String hasSailStr = sc.nextLine().trim();

                    boolean hasSail;
                    if (hasSailStr.equalsIgnoreCase("true")) {
                        hasSail = true;
                    }
                    else if (hasSailStr.equalsIgnoreCase("false")) {
                        hasSail = false;
                    }
                    else {
                        System.out.println("Error: Invalid input for 'has sails'. Please enter exactly 'true' or 'false'.");
                        return;
                    }
                    CargoShip ship = new CargoShip(id, model, maxSpeed, hasSail);

                    System.out.print("Enter cargo load (kg, 0-50000): ");
                    String cargoStr = sc.nextLine().trim();

                    if (!cargoStr.isEmpty()) {
                        try {
                            double cargo = Double.parseDouble(cargoStr);
                            if (cargo < 0 || cargo > 50000) {
                                System.out.println("Error: Invalid cargo load for CargoShip. Must be between 0 and 50000 kg.");
                                return; 
                            }
                            if (cargo > 0) {
                                ship.loadCargo(cargo);
                                System.out.println("Loaded " + cargo + " kg of cargo.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid cargo load format for CargoShip.");
                            return;
                        } catch (OverloadException | InvalidOperationException e) {
                            System.out.println("Error: Failed to load cargo despite validation - " + e.getMessage() + ". CargoShip not added.");
                            return;
                        }
                    }
                    fleetManager.addVehicle(ship);
                    System.out.println("CargoShip added successfully!");
                }
                default -> System.out.println("Invalid vehicle type choice!");
            }
        } catch (InvalidOperationException | NumberFormatException e) {
            System.out.println("Error adding vehicle: " + e.getMessage());
        } catch (Exception e) {
             System.out.println("Unexpected error adding vehicle: " + e.getMessage());
        }
    }

    private static void searchByTypeCLI(Scanner sc, FleetManager fleetManager) {
        System.out.println("\nSearch by vehicle type:");
        System.out.println("1. Car");
        System.out.println("2. Truck");
        System.out.println("3. Bus");
        System.out.println("4. Airplane");
        System.out.println("5. CargoShip");
        System.out.print("Enter number: ");
        int typeChoice = Integer.parseInt(sc.nextLine().trim());

        Class<?> type = switch (typeChoice) {
            case 1 -> Car.class;
            case 2 -> Truck.class;
            case 3 -> Bus.class;
            case 4 -> Airplane.class;
            case 5 -> CargoShip.class;
            default -> null;
        };

        if (type != null) {
            List<Vehicle> results = fleetManager.searchByType(type);
            System.out.println("Found " + results.size() + " vehicle(s):");
            for (Vehicle v : results) {
                System.out.println("- " + v.getClass().getSimpleName() + " ID: " + v.getId());
            }
        } else {
            System.out.println("Invalid type choice!");
        }
    }

    private static void removeVehicleCLI(Scanner sc, FleetManager fleetManager) throws InvalidOperationException {
        System.out.print("Enter vehicle ID to remove: ");
        String id = sc.nextLine().trim();
        fleetManager.removeVehicle(id);
        System.out.println("Vehicle removed successfully!");
    }

    private static void startJourneyCLI(Scanner sc, FleetManager fleetManager) {
        System.out.print("Enter distance to travel: ");
        try {
            double distance = Double.parseDouble(sc.nextLine().trim());
            if (distance <= 0) {
                System.out.println("Distance must be positive.");
                return;
            }
            fleetManager.startAllJourneys(distance);
        } catch (NumberFormatException e) {
            System.out.println("Invalid distance format.");
        }
    }

    private static void refuelAllCLI(Scanner sc, FleetManager fleetManager) {
        System.out.print("Enter amount to refuel each vehicle: ");
        try {
            double amount = Double.parseDouble(sc.nextLine().trim());
            fleetManager.refuelAll(amount);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
        } catch (Exception e) {
             System.out.println("Error during refueling: " + e.getMessage());
        }
    }

    private static void saveFleetCLI(Scanner sc, FleetManager fleetManager) {
        System.out.print("Enter filename to save: ");
        String filename = sc.nextLine().trim();
        fleetManager.saveToFile(filename);
    }

    private static void loadFleetCLI(Scanner sc, FleetManager fleetManager) {
        System.out.print("Enter filename to load: ");
        String filename = sc.nextLine().trim();
        fleetManager.loadFromFile(filename);
    }

    private static void listMaintenanceVehicles(FleetManager fleetManager) {
        List<Vehicle> maintenanceList = fleetManager.getVehiclesNeedingMaintenance();
        System.out.println("Vehicles needing maintenance: " + maintenanceList.size());
        if (maintenanceList.isEmpty()) {
            System.out.println("No vehicles currently need maintenance.");
        } else {
             for (Vehicle v : maintenanceList) {
                System.out.println("- " + v.getClass().getSimpleName() + " ID: " + v.getId());
            }
        }
    }

    
    private static void editPassengersCLI(Scanner sc, FleetManager fleetManager) {
        List<Vehicle> passengerVehicles = new ArrayList<>();
        for (Vehicle v : fleetManager.getFleet()) {
            if (v instanceof interfaces.PassengerCarrier) {
                passengerVehicles.add(v);
            }
        }

        if (passengerVehicles.isEmpty()) {
            System.out.println("No vehicles in the fleet can carry passengers.");
            return;
        }

        System.out.println("\n=== Passenger-Carrying Vehicles ===");
        for (int i = 0; i < passengerVehicles.size(); i++) {
            Vehicle v = passengerVehicles.get(i);
            System.out.println((i + 1) + ". " + v.getClass().getSimpleName() + " (ID: " + v.getId() + ")");
        }

        System.out.print("Select a vehicle to edit passengers (1-" + passengerVehicles.size() + "): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return;
        }

        if (choice < 1 || choice > passengerVehicles.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Vehicle v = passengerVehicles.get(choice - 1);
        if (v instanceof interfaces.PassengerCarrier carrier) {
            System.out.println("Current passengers: " + carrier.getCurrentPassengers());
            System.out.println("Passenger capacity: " + carrier.getPassengerCapacity());
            System.out.print("Enter new passenger count: ");
            try {
                int newCount = Integer.parseInt(sc.nextLine().trim());
                int diff = newCount - carrier.getCurrentPassengers();

                if (diff > 0) carrier.boardPassengers(diff);
                else if (diff < 0) carrier.disembarkPassengers(-diff);

                System.out.println("Passenger count updated successfully!");
            } catch (Exception e) {
                System.out.println("Error updating passengers: " + e.getMessage());
            }
        } else {
            System.out.println("This vehicle type does not carry passengers.");
        }
    }


    private static void sortVehiclesCLI(Scanner sc, FleetManager fleetManager) {
        System.out.println("\n=== Sorting Options ===");
        System.out.println("1. Sort by Vehicle ID");
        System.out.println("2. Sort by Model Name");
        System.out.println("3. Sort by Max Speed");
        System.out.println("4. Sort by Type");
        System.out.println("5. Sort by Fuel Level");
        System.out.println("6. Sort by Mileage");
        System.out.print("Enter sorting choice (1-9): ");
        
        String input = sc.nextLine().trim();
        int sortChoice;
        try {
            sortChoice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format!");
            return;
        }

        switch (sortChoice) {
            case 1 -> fleetManager.sortById();
            case 2 -> fleetManager.sortByModel();
            case 3 -> fleetManager.sortByMaxSpeed();
            case 5 -> fleetManager.sortByType();
            case 6 -> fleetManager.sortByFuelLevel();
            case 8 -> fleetManager.sortByMileage();
            default -> {
                System.out.println("Invalid sorting option!");
                return;
            }
        }

        System.out.println("Vehicles sorted successfully!");
        System.out.println("\nSorted Fleet (Type and ID):");
        System.out.println("---------------------------");

        for (Vehicle v : fleetManager.getFleet()) {
            System.out.println(v.basicInfo());
        }
    }

    private static void editCargoCLI(Scanner sc, FleetManager fleetManager) {
        try {
            List<Vehicle> fleet = fleetManager.getFleet();
            List<Vehicle> cargoVehicles = new ArrayList<>();
            for (Vehicle v : fleet) {
                if (v instanceof CargoCarrier) {
                    cargoVehicles.add(v);
                }
            }

            if (cargoVehicles.isEmpty()) {
                System.out.println("No vehicles in the fleet can carry cargo.");
                return;
            }

            System.out.println("\nCargo-capable vehicles:");
            for (int i = 0; i < cargoVehicles.size(); i++) {
                Vehicle v = cargoVehicles.get(i);
                System.out.println((i + 1) + ". " + v.getId() + " - " + v.getModel() + " (" + v.getClass().getSimpleName() + ")");
            }

            System.out.print("Select a vehicle to edit cargo (1-" + cargoVehicles.size() + "): ");
            String input = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format.");
                return;
            }

            if (choice < 1 || choice > cargoVehicles.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Vehicle selected = cargoVehicles.get(choice - 1);
            CargoCarrier cargoVehicle = (CargoCarrier) selected;

            System.out.print("Enter new cargo load (kg): ");
            String cargoStr = sc.nextLine().trim();

            double cargo;
            try {
                cargo = Double.parseDouble(cargoStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format for cargo load.");
                return;
            }

            cargoVehicle.loadCargo(cargo);
            System.out.println("Cargo updated successfully for " + selected.getModel() + ".");

        } catch (OverloadException | InvalidOperationException e) {
            System.out.println("Error updating cargo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while editing cargo: " + e.getMessage());
        }
    }


}