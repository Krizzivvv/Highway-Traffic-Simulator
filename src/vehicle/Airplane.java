package vehicle;

import exception.InsufficientFuelException;
import exception.InvalidOperationException;
import exception.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
import interfaces.PassengerCarrier;

public class Airplane extends AirVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {

    private double fuelLevel = 0.0;
    private final int passengerCapacity = 200;
    private int currentPassengers = 0;
    private final double cargoCapacity = 10000.0;
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;

    public Airplane(String id, String model, double maxSpeed, double maxAltitude) throws InvalidOperationException {
        super(id, model, maxSpeed, maxAltitude);
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance <= 0) throw new InvalidOperationException("Distance must be positive.");

        consumeFuel(distance);

        addMileage(distance);
        System.out.println("Flying at " + getMaxAltitude() + " meters: Airplane " 
                        + getId() + " traveled " + distance + " km.");
    }


    @Override
    public double calculateFuelEfficiency() {
        return 5.0;
    }
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive.");
        this.fuelLevel += amount;
    }
    @Override
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
        if (currentPassengers + count > passengerCapacity) throw new OverloadException("Capacity exceeded.");
        currentPassengers += count;
    }
    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count <= 0) throw new InvalidOperationException("Passenger count must be positive.");
        if (count > currentPassengers) throw new InvalidOperationException("Not enough passengers.");
        currentPassengers -= count;
    }
    @Override
    public int getPassengerCapacity() { return passengerCapacity; }
    @Override
    public int getCurrentPassengers() { return currentPassengers; }
    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive.");
        if (currentCargo + weight > cargoCapacity) throw new OverloadException("Cargo capacity exceeded.");
        currentCargo += weight;
    }
    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive.");
        if (weight > currentCargo) throw new InvalidOperationException("Cannot unload more than current cargo.");
        currentCargo -= weight;
    }
    @Override
    public double getCargoCapacity() { return cargoCapacity; }
    @Override
    public double getCurrentCargo() { return currentCargo; }
    @Override
    public void scheduleMaintenance() { maintenanceNeeded = true; }
    @Override
    public boolean needsMaintenance() { return maintenanceNeeded || getCurrentMileage() > 10000; }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        // setMileage(0.0);
        System.out.println("Airplane " + getId() + " maintenance performed.");
    }
}