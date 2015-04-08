package com.example.david.hciprototype;


import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


public class SpeedPrompt extends ActionBarActivity {
    private GoogleMap map;
    private LatLng currentLocation;
    EventHash eventHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_prompt);
        Bundle extras = getIntent().getExtras();
        eventHash = ((EventHash) getApplication());
        final String event = extras.getString("event");

        final TextView promptText = (TextView) findViewById(R.id.textView2);
        final View thisView = promptText.getRootView();



        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        promptText.setText("Begin Walking");
        currentLocation = new LatLng(eventHash.getCurrentLocation().getLatitude(), eventHash.getCurrentLocation().getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));

        map.setMyLocationEnabled(true);
        String theLocation = eventHash.events.get(event).getLocation();
        LatLng destLocation = eventHash.locations.getCoordinates(theLocation);
        Marker destMarker = map.addMarker(new MarkerOptions()
                .position(destLocation)
                .title(theLocation));



        // Google map location update stuff -- in progress
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                // map.addMarker(new MarkerOptions().position(loc));
                //if(map != null){
                //  map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                // }
            }
        };
        map.setOnMyLocationChangeListener(myLocationChangeListener);



        // Play ringtone for notification
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Set up runnable for updates
        if(savedInstanceState == null) {
            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(!setPrompt(eventHash, thisView, event)){
                        finish();
                    }
                    handler.postDelayed(this, 10000);
                }
            };

            handler.postDelayed(runnable,10000);
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

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    //--> A method to set the prompt and background color
    public boolean setPrompt(EventHash eventHash, View thisView, String event){
        TextView promptText = (TextView) findViewById(R.id.textView2);
        Double average = eventHash.getAverageSpeed(event);

        if(average > 5){
            promptText.setText("Run or you will be late!");
            thisView.setBackgroundColor(Color.RED);
        }
        else if(average > 3.5) {
            promptText.setText("Walk quickly!");
            thisView.setBackgroundColor(Color.YELLOW);
        }
        else if (average > 2) {
            promptText.setText("You will be on time!");
            thisView.setBackgroundColor(Color.GREEN);
        }
        else {
            promptText.setText("You have some time to spare");
            thisView.setBackgroundColor(Color.GREEN);
        }

        // If the user is within 1/20 of  a mile, end the speed prompt
        if(eventHash.distanceToEvent(event) < .05){
            promptText.setText("You made it to your event");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }


}
