package com.example.david.hciprototype;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.*;


public class MainActivity extends ActionBarActivity {
    boolean openNewSpeedPrompt = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submitButton = (Button) findViewById(R.id.newLoc);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SetLocation.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        Button newEvent = (Button) findViewById(R.id.newEvent);
        newEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AddNewEvent.class);
                startActivity(myIntent);
            }
        });

        Button checkUpcoming = (Button) findViewById(R.id.upcoming);
        checkUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upcomingCheck();
            }
        });

        if(savedInstanceState == null){
            final Runnable checkEvents = new Runnable() {
                public void run() {
                    System.out.println("Still running");
                    promptCheck();
                }
            };
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            final ScheduledFuture eventHandler = scheduler.scheduleWithFixedDelay(checkEvents, 0, 20, SECONDS);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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



    // Periodically check for upcoming events
    public void promptCheck(){
        System.out.println(EventHash.events.size());
        for (String event : EventHash.events.keySet()) {
            Double averageSpeed = ((EventHash)getApplication()).getAverageForNotification(event);
            ((EventHash)getApplication()).getAverageForNotification(event);
            if(averageSpeed >= 2.5 && openNewSpeedPrompt){
                Intent speedPrompt = new Intent(MainActivity.this, SpeedPrompt.class);
                speedPrompt.putExtra("event",event);
                pushNotification(speedPrompt, event);
                //startActivity(speedPrompt);
                openNewSpeedPrompt=false;
            }
        }
    }

    public boolean upcomingCheck(){
        Double timeToNextEvent = Double.MAX_VALUE;
        for (String event : EventHash.events.keySet()) {
            Double averageSpeed = ((EventHash)getApplication()).getAverageForNotification(event);
            ((EventHash)getApplication()).getAverageForNotification(event);
            Double timeDiff = ((EventHash) getApplication()).timeToEvent(event);
            if(averageSpeed >= 2.5){
                Intent speedPrompt = new Intent(MainActivity.this, SpeedPrompt.class);
                speedPrompt.putExtra("event", event);
                speedPrompt.putExtra("update", true);
                PendingIntent start = PendingIntent.getActivity(this, 0, speedPrompt, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    start.send(this, 0, speedPrompt);
                } catch (PendingIntent.CanceledException e) {
                    System.out.println( "Sending contentIntent failed: " );
                }
                openNewSpeedPrompt=false;
                return true;
            } else if(timeToNextEvent > timeDiff) {
                timeToNextEvent = timeDiff;
            }
        }
        if(EventHash.events.size() == 0) {
            Toast.makeText(MainActivity.this, "There are no upcoming events.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Next event is in approximately " + timeToNextEvent.intValue() + " minutes.", Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    // Allow the program to open the next event
    public void onActivityResult(int i, int j, Intent intent){
        openNewSpeedPrompt = true;
    }

    public void pushNotification(Intent speedIntent, String eventName) {

       // final countdown ringtone
        Uri soundUri = Uri.parse("android.resource://"
                + this.getPackageName() + "/" + R.raw.final_countdown);

        // final fantasy victory song in vibrate
        long[] vibrate = {0,50,100,50,100,50,100,400,100,300,100,350,50,200,100,100,50,600};

        PendingIntent start = PendingIntent.getActivity(this, 0, speedIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setTicker(eventName + " is starting soon...")
                .setContentTitle("Time to Go!")
                .setContentText("You should be preparing to leave NOW!")
                .setSmallIcon(R.drawable.ic_clock)
                .setPriority(2)
                .setSound(soundUri)
                .setVibrate(vibrate)
                .setAutoCancel(true)
                .setContentIntent(start);
        // Sets an ID for the notification
        int mNotificationId = 001;
        NotificationManager notifier = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        notifier.notify(mNotificationId, notification.build());


    }
}
