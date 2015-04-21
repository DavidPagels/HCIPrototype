package com.example.david.hciprototype;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    private double prevSpeed = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_prompt);
        Bundle extras = getIntent().getExtras();
        eventHash = ((EventHash) getApplication());
        final String event = extras.getString("event");

        final TextView promptText = (TextView) findViewById(R.id.textView2);
        final View thisView = promptText.getRootView();

        if(extras.containsKey("update") && extras.getBoolean("update")) {
            setPrompt(eventHash, thisView, event);
        }

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

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
        TextView speedText = (TextView) findViewById(R.id.speed_indicator);
        Double average = eventHash.getAverageSpeed(event);
        promptText.setTextColor(Color.BLACK);
        speedText.setTextColor(Color.BLACK);
        Uri soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;
        double storePrevSpeed = prevSpeed;
        if(average > 10){
            promptText.setText("Sprint or get on a bike!");
            speedText.setText("Pace: Dead Sprint");
            thisView.setBackgroundColor(Color.RED);
            soundUri = Uri.parse("android.resource://"
                    + this.getPackageName() + "/" + R.raw.trouble);
            prevSpeed = 1000;
        }
        else if(average > 8){
            promptText.setText("Run or you will be late!");
            speedText.setText("Pace: Fast Jog");
            thisView.setBackgroundColor(Color.rgb(255,90,0));
            soundUri = Uri.parse("android.resource://"
                    + this.getPackageName() + "/" + R.raw.trouble);
            prevSpeed = 10.0;
        }
        else if(average > 6){
            promptText.setText("Better start jogging...");
            speedText.setText("Pace: Light Jog");
            thisView.setBackgroundColor(Color.rgb(255,155,0));
            prevSpeed = 8.0;
        }
        else if(average > 4) {
            promptText.setText("Walk quickly!");
            speedText.setText("Pace: Power Walk");
            thisView.setBackgroundColor(Color.YELLOW);
            prevSpeed = 6.0;
        }
        else if (average > 2) {
            promptText.setText("You will be on time!");
            speedText.setText("Pace: Normal Walk");
            thisView.setBackgroundColor(Color.GREEN);
            prevSpeed = 4.0;
        }
        else {
            promptText.setText("You have some time to spare");
            speedText.setText("Pace: Casual Scroll");
            thisView.setBackgroundColor(Color.GREEN);
            prevSpeed = 2.0;
        }

        if(storePrevSpeed < average) {
            sendNotification(this, soundUri, event);
        }

        // If the user is within 1/20 of  a mile, end the speed prompt
        if(eventHash.distanceToEvent(event) < .05){
            promptText.setText("You made it to your event");
            speedText.setText("Hooray!");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public void sendNotification(Context context, Uri soundUri, String event) {
        long[] vibrate = {0,50,100,50, 120, 50, 140, 50, 150};
        Intent notificationIntent = new Intent(context, SpeedPrompt.class);
        notificationIntent.putExtra("event", event);
        notificationIntent.putExtra("update", true);
        PendingIntent start = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String message = "You need to change your speed or you might not make it to " + event + "! Click for details...";
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setTicker("Speed Alert!")
                .setContentTitle("Speed Alert!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_exclamation)
                .setPriority(1)
                .setSound(soundUri)
                .setVibrate(vibrate)
                .setContentIntent(start);
        // Sets an ID for the notification
        int mNotificationId = 002;
        NotificationManager notifier = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        notifier.notify(mNotificationId, notification.build());

    }
}
