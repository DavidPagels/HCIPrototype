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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        Button submitButton = (Button) findViewById(R.id.locButton);
        final TextView editText = (TextView) findViewById(R.id.editText);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                Location location = new Location(""); // hard coding morris, mn for now
                location.setLatitude(45.5861);
                location.setLongitude(-95.9139);//lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null){
                    editText.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
                    Intent myIntent = new Intent(SetLocation.this, SpeedPrompt.class);
                    myIntent.putExtra("latitude", location.getLatitude()); // Transferring coordinates between activities
                    myIntent.putExtra("longitude", location.getLongitude());
                    startActivity(myIntent);
                }
                System.out.println("location was null");

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
