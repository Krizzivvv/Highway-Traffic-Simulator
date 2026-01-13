package interfaces;

import exception.InsufficientFuelException;
import exception.InvalidOperationException;

public interface FuelConsumable {
    void refuel(double amount) throws InvalidOperationException;
    double getFuelLevel();
    double consumeFuel(double distance) throws InsufficientFuelException;
}