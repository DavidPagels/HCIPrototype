package com.example.david.hciprototype;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


public class SetLocation extends ActionBarActivity {
    EventHash eventHash = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        eventHash = ((EventHash)getApplication());
        Button submitButton = (Button) findViewById(R.id.locButton);
        final TextView editText = (TextView) findViewById(R.id.editText);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                Location location = new Location(""); // hard coding morris, mn for now
                location.setLatitude(45.5861);
                location.setLongitude(-95.9139);//lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // Only execute if gps location was found and the location hashmap doesn't contain label
                if(location != null || !eventHash.locations.getLocationHash().containsKey(editText.getText().toString())){

                    String eventLocation = editText.getText().toString();
                    eventHash.locations.addLocation(eventLocation, location.getLatitude(), location.getLongitude());
                    finish();
                } else {
                    System.out.println("location was null");
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_location, menu);
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
}
