package com.example.karri.carlocator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Sovellus tarvitsee paikannusluvat toimiakseen, muuten kartta ei näytä mitään.
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, CoordinateInterface {

    private GoogleMap mMap;
    private Double lat = 0.0;
    private Double lon = 0.0;
    private BeaconManager beaconManager;
    private String origin;
    private String destination;
    Marker m;
    TextView textViewDistance;
    TextView textViewDuration;
    TextView textViewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        DataGetter dg = new DataGetter(this);
        dg.setInterface(this);
        dg.getData();

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        18494, 17034));
            }
        });
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                showNotification(
                        "MIJ-646",
                        "Entering beacon range");
                Log.v("Entering beacon range", "Entering beacon range");
            }

            @Override
            public void onExitedRegion(Region region) {
                showNotification(
                        "MIJ-646",
                        "Exiting beacon range");
                Log.v("Exiting beacon range", "Exiting beacon range");
                get_GPS_Location();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 16));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title("Destination"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void getCoordinatesDestination(Double latitude, Double longitude) {
        lat = latitude;
        lon = longitude;
        destination = latitude.toString() + "," + longitude.toString();
        Log.v("Log", latitude + " " + latitude);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GPSLocationer locationer = new GPSLocationer(this);
        locationer.setInterface(this);
        locationer.getOriginGPS();
    }

    public void receivedCoordinatesDestination(Double latitude, Double longitude) {
        DataSender ds = new DataSender(this);
        ds.setRajapinta(this);
        if (longitude != null && latitude != null) {
            ds.sendData(latitude, longitude);
        }
    }

    public void receivedCoordinatesOrigin(Double latitude, Double longitude) {
        origin = latitude.toString() + "," + longitude.toString();
        sendDirectionRequest();
    }

    public void updateMapNewLocation() {
        DataGetter dg = new DataGetter(this);
        dg.setInterface(this);
        dg.getData();
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MapsActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    public void get_GPS_Location() {
        GPSLocationer g = new GPSLocationer(this);
        g.setInterface(this);
        g.getDestinationGPS();
    }

    private void sendDirectionRequest() {
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void onDirectionFinderSuccess(Route r) {

        mMap.clear();

        textViewAddress = (TextView) findViewById(R.id.textViewDestinationAddress);
        textViewDistance = (TextView) findViewById(R.id.textViewDistance);
        textViewDuration = (TextView) findViewById(R.id.textViewTime);

        Log.v("Address", r.endAddress.toString());
        Log.v("Duration", r.duration.toString());
        Log.v("Distance", r.distance.toString());

        textViewAddress.setText(r.endAddress);
        textViewDistance.setText("Distance: " + r.distance.text.toString());
        textViewDuration.setText("Time: " + r.duration.text.toString());

        MarkerOptions a = new MarkerOptions().position(new LatLng(r.startLocation.latitude, r.startLocation.longitude));
        m = mMap.addMarker(a);
        m.setPosition(new LatLng(r.endLocation.latitude, r.endLocation.longitude));

        PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.BLUE).width(10);
        Polyline poly = mMap.addPolyline(polylineOptions);
        List<LatLng> points = new ArrayList<>();
        for (int i = 0; i < r.points.size(); i++) {
            points.add(r.points.get(i));
        }
        poly.setPoints(points);

    }
}