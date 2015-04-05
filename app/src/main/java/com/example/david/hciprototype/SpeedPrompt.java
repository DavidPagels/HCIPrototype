package com.example.david.hciprototype;


import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SpeedPrompt extends ActionBarActivity {
    private GoogleMap map;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_prompt);
        final TextView promptText = (TextView) findViewById(R.id.textView2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentLocation = new LatLng(extras.getDouble("latitude"), extras.getDouble("longitude"));
        }
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if(currentLocation == null)
           System.out.println("currentLocation is null!");
        if(promptText == null)
            System.out.println("promptText is null!");
        promptText.setText("Latitude: " + currentLocation.latitude + ", Longitude: " + currentLocation.longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));

        // Add the markers to the map
        Marker currentMarker = map.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("Current Location"));
        // Other location hard coded to Alexandria, MN for now
        LatLng destLocation = new LatLng(45.8852, -95.3772);
        Marker alexMarker = map.addMarker(new MarkerOptions()
                .position(destLocation)
                .title("Alexandria"));

        // Form the coordinate string for the get request
        String coords = makeCoords(currentLocation, destLocation);
        GoogleDirections directions = new GoogleDirections();
        try {
            String theDirections = directions.execute(coords).get();
            System.out.println(theDirections);
            JSONObject jsonDirections = new JSONObject(theDirections);
            // Get the distance between points
            String numMiles = jsonDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").get("text").toString();
            promptText.setText("Distance: " + numMiles);
        } catch (Exception e) {
            System.err.println("direction error " + e);
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speed_prompt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class GoogleDirections extends AsyncTask<String, Void, String> {
        String returnedJSON = "";
        protected String doInBackground(String ... params){
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + params[0].split(" ")[0] + "&destination=" + params[0].split(" ")[1];
            System.out.println(url);
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                return response.toString();
            } catch(Exception e){
                System.out.println("caught exception");
                return null;
            }

        }

    }

    //--> returns a string of the format "fromlat,fromlong tolat,tolong"
    private String makeCoords(LatLng fromCoords, LatLng toCoords){
        String fromLat = Double.toString(fromCoords.latitude);
        String fromLong = Double.toString(fromCoords.longitude);
        String toLat = Double.toString(toCoords.latitude);
        String toLong = Double.toString(toCoords.longitude);
        return fromLat + "," + fromLong + " " + toLat + "," + toLong;

    }
}
