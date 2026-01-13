Transportation Fleet Management System 
Assignment: AP-Assignment 2
Author: Krishiv Vats 
Roll Number: 2024308 
 
1. OOP Concepts in the Code 
 
Inheritance 
    Vehicle (abstract) -> root class. 
    LandVehicle, AirVehicle, WaterVehicle (abstract) -> extend Vehicle and add attributes (numWheels, maxAltitude, hasSail). 
    Concrete classes (Car, Truck, Bus, Airplane, CargoShip) -> extend abstract parents and inherit shared functionality. 
 
Polymorphism 
    FleetManager methods (startAllJourneys, getTotalFuelConsumption, maintainAll, generateReport, sortFleetByEfficiency) operate on generic Vehicle references. 
    Dynamic dispatch ensures runtime-specific behavior (Car.move, Airplane.move, etc.). 
    Filtering with instanceof enables type-based search. 
 
Abstract Classes 
    Vehicle defines core structure (move, calculateFuelEfficiency, estimateJourneyTime) + shared logic (displayInfo, getCurrentMileage). 
    LandVehicle, AirVehicle, WaterVehicle provide domain-specific estimateJourneyTime, leaving concrete classes to define movement and fuel logic. 
 
Interfaces 
    FuelConsumable -> fuel operations. 
    CargoCarrier -> cargo handling. 
    PassengerCarrier -> passenger handling. 
    Maintainable -> maintenance lifecycle.   
 
Examples: (constructor) 
    Car -> FuelConsumable, PassengerCarrier, Maintainable. 
    CargoShip -> CargoCarrier, Maintainable, optionally FuelConsumable. 

2. Compilation & Execution Instructions 
    Compilation 
        cd AP_ASSIGNMENT 
        javac src/exception/*.java src/fleet/*.java src/interfaces/*.java 		src/main/*.java src/vehicle/*.java

    Execution 

        cd src 
        then use 
        java main.Main 
        Demo Phase:   
        Creates one sample vehicle of each 
        type.  Simulates a 100 km journey. 
        Generates a fleet report. 
        Saves data to fleet_demo.csv. 
        After Demo CLI Apprears

    Command Line Interface

        Menu Options === Fleet Management Menu === 
        1.Add Vehicle
        2.Remove Vehicle
        3.Start Journey
        4.Refuel All
        5.Perform Maintenance
        6.Generate Report
        7. Save Fleet 
        8. Load Fleet 
        9. Search by Type 
        10. List Vehicles Needing Maintenance
        11. Edit Passenger Count
        12. Edit Cargo Load
        13. Sort Vehicles
        14. Exit
 
    Menu Functionality   
 
        Add Vehicle -> Choose type (Car, Truck, Bus, Airplane, CargoShip) + properties. 
        Remove Vehicle -> Enter vehicle ID. 
        Start Journey -> Enter distance; all vehicles travel. 
        Refuel All -> Add fuel to all fuel-consuming vehicles. 
        Perform Maintenance -> Run maintenance on vehicles needing it. 
        Generate Report -> Show fleet summary (counts, efficiency, mileage, maintenance). 
        Save Fleet -> Save current fleet state to CSV. 
        Load Fleet -> Load fleet state from CSV (e.g., fleet_demo.csv). 
        Search by Type -> List vehicles of a selected type. 
        List Vehicles Needing Maintenance -> Show IDs/types requiring service. 
        Allows updating passenger numbers interactively for all passenger-carrying vehicles (Car, Bus, Airplane).
        Enables adjusting cargo weights interactively for cargo-carrying vehicles (Truck, Bus, Airplane, CargoShip).
        Provides sorting options for displaying ordered fleet data by:
            Vehicle ID
            Model Name
            Maximum Speed
            Vehicle Type
            Fuel Level
            Mileage

        Exit -> Quit program. 
 
    4. Persistence with CSV 
        Demo automatically creates fleet_demo.csv. 
        To test loading:   
    
        Run java main.Main. 
        After demo, select Option 8 (Load Fleet). 
        Enter filename: fleet_demo.csv. 
        Verify with Option 6 (Generate Report). 
        You can also create/edit custom CSV files. 
    
    5. Demo Walkthrough & Expected Output 
    
        Demo Phase Output:   
    
                === Demo: Creating sample fleet === 
                Messages confirming the addition of Car, Truck, Bus, 			Airplane, CargoShip. 
    
            --- Simulating journey of 100 km --- 
                Driving on road: Car C001 moved 100.0 km.   
                Flying at 10000.0 meters: Airplane A001 traveled 100.0 				km.   
                ... (other vehicles)   
    
            --- Generating report ---   
                Total Vehicles: 5   
                By Type: Car=1, Truck=1, Bus=1, Airplane=1, CargoShip=1   
                Average Efficiency: X   
                Total Mileage: 500 km   
                Vehicles Needing Maintenance:  
    
    6. Important Notes 
    
        Vehicle Initialization 
            • All newly created vehicles start with currentMileage = 0.0 			km 
            • Mileage accumulates as vehicles perform journeys via the 			move() method 
            • Maintenance is required when mileage exceeds 10,000 km 
    
        CSV Data Format 
            • When saving/loading from CSV, mileage is preserved 
            • Demo vehicles start with 0.0 mileage and accumulate 100.0 			km after demo journey 
        
        Maintenance Handling Options: 
    
            1. Reset Mileage After Maintenance 
                Description: Set the vehicle's mileage to 0 after 				performing maintenance. 
                Effect: Vehicles that have undergone maintenance will no 			longer appear in the "List Vehicles Needing 					Maintenance". 
    
                How to enable: Uncomment the line setMileage(0.0); in the 			respective concrete class: 
                    Car: line 77 
                    Truck: line 84 
                    Bus: line 97 
                    Airplane: line 94 
                    CargoShip: line 91 
    
            2. Keep Mileage Intact After Maintenance 
                Description: Do not change the mileage after maintenance. 
                Effect: Vehicles may continue to appear in the "List 			Vehicles Needing Maintenance" even if maintenance has 			been done. 
            
            * How to enable: Ensure the line setMileage(0.0); remains 				commented in the respective concrete class. 

7. Collections, Sorting, and Data Management

    1,Dynamic Collections

        Vehicles are stored using dynamic collections to allow resizing, iteration, and removal operations efficiently:
        ArrayList<Vehicle> - Used for the main fleet to provide resizable and ordered storage.
        HashSet<String> - Used to ensure unique Vehicle IDs, preventing duplicates during addition or file loading.
        HashMap<String, Vehicle> - Enables O(1) lookup and removal operations by Vehicle ID.
        These collections maintain encapsulation and are private within the FleetManager class.

    2.Ordering and Sorting (Collections.sort with Comparators)

        To meet the ordering requirement, flexible sorting options are implemented using Collections.sort() with custom comparators instead of TreeSet.
        This design allows sorting on demand while preserving the flexibility of dynamic collections.

        All sorting functionality is encapsulated in the FleetManager class and accessible from the CLI through Menu Option 13 – Sort Vehicles.

        Available sorting options:
            Sort by Vehicle ID (ascending)
            Sort by Model Name (alphabetical order)
            Sort by Maximum Speed (descending)
            Sort by Vehicle Type (Car, Truck, Bus, etc.)
            Sort by Fuel Level (highest first)
            Sort by Mileage (highest first)

        This approach demonstrates effective use of comparators with Collections.sort(), fulfilling the “Ordering” requirement in the assignment guidelines.

    3.File I/O and Reliability

        Fleet data is saved and loaded in CSV format using standard Java I/O (BufferedReader, FileWriter).
        All file operations are enclosed within try-with-resources, ensuring automatic file closure (the modern equivalent of try-catch-finally).
        Each line in the CSV corresponds to a vehicle's serialized data including its type, ID, model, mileage, fuel level, and maintenance status.

    4.Data Handling and Reliability Enhancements

        Uniqueness & Integrity:
            HashSet enforces unique IDs, preventing duplicate entries.

        Optimized Lookups:
            HashMap enables constant-time access for add/remove/search operations.

        Batch Operations:
            maintainAll() and refuelAll() allow performing actions on all 		 eligible vehicles simultaneously.

        Robust Input Validation:
            All user inputs (speed, passengers, cargo) are validated for 		range and format, with detailed exception handling in the 		CLI.


8. Multithreaded Highway Simulator (Assignment-3 Upgrade)
    This module extends your Fleet System with:

    Purpose
        A Swing GUI that demonstrates:
        Multi-threaded vehicle movement
        Race conditions when writing shared data
        Fix using ReentrantLock

    What the GUI Shows
        Live Vehicle Table (ID, Type, Mileage, Fuel, Status)
        Highway Counters:
        Total Distance
        Expected Distance
        Data Loss (%)
        Controls:
            Start / Pause / Resume / Stop
            Refuel All
            Synchronization Toggle

    Thread Logic

        Each vehicle runs in a separate VehicleThread:
        Moves 1 km/sec
        Consumes fuel
        Updates expectedDistance
        Updates shared distance counter:
        Unsynchronized (Race Condition):
            int t = highwayDistanceUnsync;
            Thread.sleep(1);
            highwayDistanceUnsync = t + 1;
            
        Synchronized (Safe):
            lock.lock();
            highwayDistanceSync++;
            lock.unlock();

    Race Condition Demo
        Unsync mode -> lost increments -> Data Loss rises
        Sync mode -> clean counter -> 0% loss
        GUI color changes: green -> orange -> red

    Integration with Assignment-2
        Uses same FleetManager & Vehicle classes
        Table reads mileage, fuel, type, ID directly
        No code duplication
        Refuel, maintenance, movement still use your interfaces

    Extra GUI Features
        Auto sample fleet (Car, Truck, Bus)
        File/input validation for refueling
        Auto-resume vehicles when fuel restored
        Graceful thread stopping + joining
        Mode-change popups

    CLI->GUI Launch System-> The GUI does NOT ask for a CSV file.

    Instead:
        Execution (GUI Launcher – Updated)
        java simulator.HighwaySimulatorGUI
        -> CLI prints: Enter CSV filename to load:
        -> User enters e.g. fleet_demo.csv
        -> GUI opens and loads the file automatically