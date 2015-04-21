package com.example.david.hciprototype;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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

                Location location = eventHash.getCurrentLocation();

                // Only execute if gps location was found and the location hashmap doesn't contain label
                if(location != null || !eventHash.locations.getLocationHash().containsKey(editText.getText().toString())){

                    String eventLocation = editText.getText().toString();
                    eventHash.locations.addLocation(eventLocation, location.getLatitude(), location.getLongitude());
                    finish();
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
