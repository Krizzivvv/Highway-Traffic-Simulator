package vehicle;

import exception.InsufficientFuelException;
import exception.InvalidOperationException;
import exception.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {

    private double fuelLevel = 0.0;
    private final double cargoCapacity = 5000.0;
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;
    public Truck(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed, numWheels);
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance <= 0) throw new InvalidOperationException("Distance must be positive.");

        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > getFuelLevel()) throw new InsufficientFuelException("Not enough fuel to move.");
        consumeFuel(distance);

        addMileage(distance);
        System.out.println("Hauling cargo: Truck " + getId() + " moved " + distance + " km with " + currentCargo + " kg cargo.");
    }

    @Override
    public double calculateFuelEfficiency() {
        double efficiency = 8.0;
        if (currentCargo > (cargoCapacity / 2)) {
            efficiency *= 0.9;
        }
        return efficiency;
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
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive.");
        if (currentCargo + weight > cargoCapacity) throw new OverloadException("Exceeds cargo capacity.");
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
        System.out.println("Truck " + getId() + " maintenance performed. Mileage reset to 0.");
    }

}