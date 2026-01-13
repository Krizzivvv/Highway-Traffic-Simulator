package vehicle;

import exception.InsufficientFuelException;
import exception.InvalidOperationException;
import exception.OverloadException;
import interfaces.CargoCarrier;
import interfaces.FuelConsumable;
import interfaces.Maintainable;
public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {
    private final double cargoCapacity = 50000.0;
    private double currentCargo = 0.0;
    private boolean maintenanceNeeded = false;
    private double fuelLevel = 0.0;

    public CargoShip(String id, String model, double maxSpeed, boolean hasSail) throws InvalidOperationException {
        super(id, model, maxSpeed, hasSail);
    }

    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance <= 0) throw new InvalidOperationException("Distance must be positive.");
        if (!hasSail()) {
            double fuelNeeded = distance / calculateFuelEfficiency();
            if (fuelNeeded > getFuelLevel()) {
                throw new InsufficientFuelException("Not enough fuel to sail.");
            }
            consumeFuel(distance);
        }

        addMileage(distance);
        System.out.println("Sailing with cargo: CargoShip " + getId() + " moved " + distance + " km with " + currentCargo + " kg cargo.");
    }

    @Override
    public double calculateFuelEfficiency() {
        return hasSail() ? 0.0 : 4.0;
    }
    
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (!hasSail()) {
            if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive.");
            this.fuelLevel += amount;
        }
    }
    @Override
    public double getFuelLevel() {
        if (!hasSail()) {
            return this.fuelLevel;
        }
        return 0.0;
    }
    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        if (!hasSail()) {
            double fuelNeeded = distance / calculateFuelEfficiency();
            if (fuelNeeded > this.fuelLevel) {
                throw new InsufficientFuelException("Not enough fuel.");
            }
            this.fuelLevel -= fuelNeeded;
            return fuelNeeded;
        }
        return 0.0;
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
    public boolean needsMaintenance() { return maintenanceNeeded || getCurrentMileage() > 50000; }

    @Override
    public void performMaintenance() {
        maintenanceNeeded = false;
        // setMileage(0.0);
        System.out.println("CargoShip " + getId() + " maintenance performed.");
    }
}