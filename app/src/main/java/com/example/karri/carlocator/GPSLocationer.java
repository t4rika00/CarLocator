package com.example.karri.carlocator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Karri on 21.11.2016.
 */

public class GPSLocationer {
    Context context;
    CoordinateInterface cInterface;
    GPSAsyncDestination asyncDestination;
    GPSAsyncOrigin asyncOrigin;


    void setInterface(CoordinateInterface i) {
        this.cInterface = i;
    }

    public GPSLocationer(Context c) {
        this.context = c;
    }

    public void getDestinationGPS() {
        asyncDestination = new GPSAsyncDestination();
        asyncDestination.execute();
    }

    public void getOriginGPS(){
        asyncOrigin = new GPSAsyncOrigin();
        asyncOrigin.execute();
    }

    class GPSAsyncDestination extends AsyncTask<Void, Void, Void> {

        LocationManager lm;
        LocationListener listener;
        Location location;

        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("Log", "onPreExecute");
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.v("Log", "onPostExecute");
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    //lm.requestLocationUpdates("gps", 0, 0, listener);
                    lm.requestSingleUpdate("gps", listener, Looper.myLooper());
                    if(location != null) {
                        Double lon = location.getLongitude();
                        Double lat = location.getLatitude();
                        Log.v("onLocationChanged", "Longitude: " + lon.toString() + " Latitude: " + lat.toString());
                        cInterface.receivedCoordinatesDestination(lat, lon);
                        lm.removeUpdates(listener);
                    }
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }
                @Override
                public void onProviderEnabled(String s) {
                }
                @Override
                public void onProviderDisabled(String s) {
                }
            };
            listener.onLocationChanged(location);
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.v("Log","doInBackground");
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return null;
        }
    }

    class GPSAsyncOrigin extends AsyncTask<Void, Void, Void> {

        LocationManager lm;
        LocationListener listener;
        Location location;

        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("Log", "onPreExecute");
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.v("Log", "onPostExecute");
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    //lm.requestLocationUpdates("gps", 3000, 0, listener);
                    lm.requestSingleUpdate("gps", listener, Looper.myLooper());
                    if(location != null) {
                        Double lon = location.getLongitude();
                        Double lat = location.getLatitude();
                        Log.v("UserLocation", "Longitude: " + lon.toString() + " Latitude: " + lat.toString());
                        cInterface.receivedCoordinatesOrigin(lat, lon);
                        //lm.removeUpdates(listener);
                    }
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }
                @Override
                public void onProviderEnabled(String s) {
                }
                @Override
                public void onProviderDisabled(String s) {
                }
            };
            listener.onLocationChanged(location);
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.v("Log","doInBackground");
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return null;
        }
    }
}
