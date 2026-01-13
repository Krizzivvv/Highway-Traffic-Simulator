package vehicle;

import exception.InsufficientFuelException;
import exception.InvalidOperationException;
import exception.OverloadException;
import interfaces.FuelConsumable;
import interfaces.PassengerCarrier;
import interfaces.Maintainable;

public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {

    private double fuelLevel = 0.0;
    private final int passengerCapacity = 5;
    private int currentPassengers = 0;
    private boolean maintenanceNeeded = false;

    public Car(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed, numWheels);
    }
    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance <= 0) throw new InvalidOperationException("Distance must be positive.");

        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > getFuelLevel()) throw new InsufficientFuelException("Not enough fuel to move.");
        consumeFuel(distance);

        addMileage(distance);
        System.out.println("Driving on road: Car " + getId() + " moved " + distance + " km.");
    }
    @Override
    public double calculateFuelEfficiency() {
        return 15.0;
    }
    @Override
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
    public void boardPassengers(int count) throws InvalidOperationException, OverloadException {
        if (count <= 0) throw new InvalidOperationException("Passenger count must be positive.");
        if (currentPassengers + count > passengerCapacity)
            throw new OverloadException("Exceeds passenger capacity.");
        currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count <= 0) throw new InvalidOperationException("Passenger count must be positive.");
        if (count > currentPassengers)
            throw new InvalidOperationException("Not enough passengers to disembark.");
        currentPassengers -= count;
    }

    @Override
    public int getPassengerCapacity() { return passengerCapacity; }
    @Override
    public int getCurrentPassengers() { return currentPassengers; }
    @Override
    public void scheduleMaintenance() { maintenanceNeeded = true; }
    @Override
    public boolean needsMaintenance() { return maintenanceNeeded || getCurrentMileage() > 10000; }
    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        // setMileage(0.0);
        System.out.println("Car " + getId() + " maintenance performed.");
    }
}