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


public class SpeedPrompt extends ActionBarActivity {
    private GoogleMap map;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_prompt);
        final TextView promptText = (TextView) findViewById(R.id.textView2);

        // get the current location from SetLocation activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentLocation = new LatLng(extras.getDouble("latitude"), extras.getDouble("longitude"));
        }
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        //if(currentLocation == null)
           //System.out.println("currentLocation is null!");
        //if(promptText == null)
            //System.out.println("promptText is null!");
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



}
