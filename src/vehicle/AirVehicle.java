package vehicle;

import exception.InvalidOperationException;

public abstract class AirVehicle extends Vehicle {
    private double maxAltitude;

    public AirVehicle(String id, String model, double maxSpeed, double maxAltitude) throws InvalidOperationException {
        super(id, model, maxSpeed);
        this.maxAltitude = maxAltitude;
    }

    public double getMaxAltitude() { return maxAltitude; }
    public void setMaxAltitude(double alt) throws InvalidOperationException {
        if (alt <= 0) throw new InvalidOperationException("Altitude must be positive.");
        this.maxAltitude = alt;
    }


    @Override
    public double estimateJourneyTime(double distance) throws InvalidOperationException {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative.");
        double baseTime = distance / getMaxSpeed();
        return baseTime * 0.95;
    }
}