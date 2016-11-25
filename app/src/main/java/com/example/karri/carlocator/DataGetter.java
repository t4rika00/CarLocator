package com.example.karri.carlocator;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Karri on 20.11.2016.
 */

public class DataGetter {

    public static final String DATA_URL = "http://www.students.oamk.fi/~t4pama01/getData.php?id=";
    public static final String KEY_LATITUDE = "Latitude";
    public static final String KEY_LONGITUDE = "Longitude";
    public static final String JSON_ARRAY = "result";

    private CoordinateInterface cInterface;
    private Context context;
    //private MapsActivity mapsActivity;

    public DataGetter(Context c) {
        this.context = c;
    }

    public void setInterface(CoordinateInterface in) {
        this.cInterface = in;
    }

    public void getData() {

        String url = DATA_URL;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        String latitude = "";
        String longitude = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.v("JSONObject", jsonObject.toString());
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
            JSONObject userData = result.getJSONObject(0);
            latitude = userData.getString(KEY_LATITUDE);
            longitude = userData.getString(KEY_LONGITUDE);
            Log.v("Log", latitude + " " + longitude);

            Double lon = Double.parseDouble(longitude);
            Double lat = Double.parseDouble(latitude);
            Log.v("Log", lat + " " + lon);

            cInterface.getCoordinatesDestination(lat, lon);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
