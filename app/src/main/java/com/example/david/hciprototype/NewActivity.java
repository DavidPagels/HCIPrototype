package com.example.david.hciprototype;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;


public class NewActivity extends ActionBarActivity {
    AutoCompleteTextView aCTV = null;
    EventHash.LocationHash locations;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        final TextView eventTitle = (TextView) findViewById(R.id.eventTitle);
        aCTV = (AutoCompleteTextView) findViewById(R.id.locMenu);

        // Add event info and return to calling activity
        Button createEvent = (Button) findViewById(R.id.createEvent);
        createEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // If the event name is taken
                if (!((EventHash) getApplication()).events.containsKey(eventTitle.getText())) {
                    // Add the event info
                    String eventName = eventTitle.getText().toString();
                    Calendar timeOfEvent = null;
                    String eventLocation = aCTV.getText().toString();
                    ((EventHash) getApplication()).addEvent(eventName, timeOfEvent, eventLocation);

                    // code to test distance to event function
                   /* AlertDialog alertDialog = new AlertDialog.Builder(NewActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage(((EventHash)getApplication()).distanceToEvent(eventName));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();*/
                    finish();
                }
            }
        });

        // Allow users to enter new location in add event activity
        Button addLocation = (Button) findViewById(R.id.addLocation);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(NewActivity.this, SetLocation.class);
                startActivityForResult(myIntent, 1);
            }
        });

        setAutoComplete();
    }

    protected void onActivityResult(int a, int b, Intent intent) {
        System.out.println("inside activity result");
        setAutoComplete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new, menu);
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


    private void setAutoComplete() {

        locations = ((EventHash) getApplication()).locations;
        ArrayList<String> allLocations = locations.getAllLocations();
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allLocations);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        aCTV.setAdapter(adapter);

        aCTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String selected = (String) arg0.getAdapter().getItem(arg2);
                LatLng selectedCoors = locations.getCoordinates(selected);
                Toast.makeText(NewActivity.this, "Coordinates:" + selectedCoors.latitude + "," + selectedCoors.longitude, Toast.LENGTH_SHORT).show();
            }

        });
    }
}
