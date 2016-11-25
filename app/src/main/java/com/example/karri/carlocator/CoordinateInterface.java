package com.example.karri.carlocator;

import java.io.Serializable;

/**
 * Created by Karri on 20.11.2016.
 */

public interface CoordinateInterface extends Serializable {
    //from DataGetter
    void getCoordinatesDestination(Double longitude, Double latitude);

    //from GPSLocationer
    void receivedCoordinatesDestination(Double longitude, Double latitude);

    //from GPSLocationer
    void receivedCoordinatesOrigin(Double longitude, Double latitude);

    //from DataSender
    void updateMapNewLocation();

    //from DirectionFinder
    void onDirectionFinderSuccess(Route r);
}
