package vehicle;

import exception.InvalidOperationException;
import exception.InsufficientFuelException;

public abstract class Vehicle implements Comparable<Vehicle> {
    private String id;
    private String model;
    private double maxSpeed;
    private double currentMileage;

    public Vehicle(String id, String model, double maxSpeed) throws InvalidOperationException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidOperationException("Vehicle ID cannot be null or empty.");
        }

        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = 0.0;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getCurrentMileage() {
        return currentMileage;
    }

    public void displayInfo() {
        System.out.printf("ID: %s, Model: %s, MaxSpeed: %.2f km/h, Mileage: %.2f km%n",
                id, model, maxSpeed, currentMileage);
    }

    public abstract void move(double distance) throws InvalidOperationException, InsufficientFuelException;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance) throws InvalidOperationException;

    @Override
    public int compareTo(Vehicle other) {
        if (other == null) return 1;
        return Double.compare(this.calculateFuelEfficiency(), other.calculateFuelEfficiency());
    }

    public void setMileage(double mileage) throws InvalidOperationException {
        if (mileage < 0) throw new InvalidOperationException("Mileage cannot be negative.");
        this.currentMileage = mileage;
    }


    public void addMileage(double distance) throws InvalidOperationException {
        if (distance < 0) {
            throw new InvalidOperationException("Distance cannot be negative.");
        }
        this.currentMileage += distance;
    }

    public String basicInfo() {
        return String.format("%-12s %-8s", this.getClass().getSimpleName(), getId());
    }

}