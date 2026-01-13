package vehicle;

import exception.InvalidOperationException;

public abstract class WaterVehicle extends Vehicle {
    private boolean hasSail;

    public WaterVehicle(String id, String model, double maxSpeed, boolean hasSail) throws InvalidOperationException {
        super(id, model, maxSpeed);
        this.hasSail = hasSail;
    }

    public boolean hasSail() { return hasSail; }
    
    @Override
    public double estimateJourneyTime(double distance) throws InvalidOperationException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative.");
        double baseTime = distance / getMaxSpeed();
        return baseTime * 1.15;
    }

}