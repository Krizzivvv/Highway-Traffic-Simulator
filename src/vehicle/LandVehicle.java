package vehicle;

import exception.InvalidOperationException;

public abstract class LandVehicle extends Vehicle {
    private int numWheels;

    public LandVehicle(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed);
        if (numWheels <= 0) throw new InvalidOperationException("Number of wheels must be positive.");
        this.numWheels = numWheels;
    }

    public int getNumWheels() { 
        return numWheels; 
    }

    @Override
    public double estimateJourneyTime(double distance) throws InvalidOperationException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative.");
        double baseTime = distance / getMaxSpeed();
        return baseTime * 1.1;
    }
}
