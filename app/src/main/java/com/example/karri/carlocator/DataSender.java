package com.example.karri.carlocator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Karri on 21.11.2016.
 */

public class DataSender{

    private static final String URL = "http://www.students.oamk.fi/~t4pama01/insertData.php";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";

    Context c;
    CoordinateInterface cInterface;

    public void setRajapinta(CoordinateInterface c){
        this.cInterface = c;
    }


    public DataSender(Context c){
        this.c = c;
    }

    public void sendData(final Double la, final Double lo)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        Toast.makeText(c,response,Toast.LENGTH_LONG).show();
                        showNotification("CarLocator","Car location registered successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(c,error.toString(),Toast.LENGTH_LONG).show();
                        showNotification("CarLocator","Error on registering car location");
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(LATITUDE, la.toString().trim());
                params.put(LONGITUDE, lo.toString().trim());
                Log.v("Log",la.toString() + " " + lo.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);
        cInterface.updateMapNewLocation();
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(c, MapsActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(c, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(c)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

}
