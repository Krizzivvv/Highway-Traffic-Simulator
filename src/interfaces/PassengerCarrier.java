package interfaces;

import exception.OverloadException;
import exception.InvalidOperationException;

public interface PassengerCarrier {
    void boardPassengers(int count) throws OverloadException, InvalidOperationException;
    void disembarkPassengers(int count) throws InvalidOperationException;
    int getPassengerCapacity();
    int getCurrentPassengers();
}