package vehicle;

import exception.InsufficientFuelException;
import exception.InvalidOperationException;
import exception.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;

public class Bus extends LandVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {

    private double fuelLevel = 0.0;
    private final int passengerCapacity = 50;
    private int currentPassengers = 0;
    private final double cargoCapacity = 500.0;
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;

    public Bus(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed, numWheels);
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance <= 0) throw new InvalidOperationException("Distance must be positive.");

        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > getFuelLevel()) throw new InsufficientFuelException("Not enough fuel to move.");
        consumeFuel(distance);

        addMileage(distance);
        System.out.println("Transporting passengers and cargo: Bus " + getId() + " moved " + distance + " km with " + currentPassengers + " passengers and " + currentCargo + " kg cargo.");
    }

    @Override
    public double calculateFuelEfficiency() {
        double efficiency = 10.0;
        if (currentPassengers > (passengerCapacity / 2) || currentCargo > (cargoCapacity / 2)) {
            efficiency *= 0.9;
        }
        return efficiency;
    }

    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive.");
        this.fuelLevel += amount;
    }


    
    public double getFuelLevel() {
        return this.fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > this.fuelLevel) {
            throw new InsufficientFuelException("Not enough fuel.");
        }
        this.fuelLevel -= fuelNeeded;
        return fuelNeeded;
    }

    @Override
    public void boardPassengers(int count) throws OverloadException, InvalidOperationException {
        if (count <= 0) throw new InvalidOperationException("Passenger count must be positive.");
        if (currentPassengers + count > passengerCapacity) throw new OverloadException("Capacity exceeded");
        currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count <= 0) throw new InvalidOperationException("Passenger count must be positive.");
        if (count > currentPassengers) throw new InvalidOperationException("Not enough passengers");
        currentPassengers -= count;
    }
    @Override
    public int getPassengerCapacity() { return passengerCapacity; }
    @Override
    public int getCurrentPassengers() { return currentPassengers; }
    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive.");
        if (currentCargo + weight > cargoCapacity) throw new OverloadException("Cargo capacity exceeded");
        currentCargo += weight;
    }
    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive.");
        if (weight > currentCargo) throw new InvalidOperationException("Cannot unload more than current cargo");
        currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity() { return cargoCapacity; }
    @Override
    public double getCurrentCargo() { return currentCargo; }
    @Override
    public void scheduleMaintenance() { maintenanceNeeded = true; }

    public boolean needsMaintenance() { return getCurrentMileage() > 10000 || maintenanceNeeded; }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        // setMileage(0.0);
        System.out.println("Bus " + getId() + " maintenance performed.");
    }
}