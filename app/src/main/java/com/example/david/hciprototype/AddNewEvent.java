package com.example.david.hciprototype;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;


public class AddNewEvent extends ActionBarActivity {
    AutoCompleteTextView aCTV = null;
    EventHash.LocationHash locations;
    EventHash eventHash = null;
    Context context = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        eventHash = ((EventHash) getApplication());
        final TextView eventTitle = (TextView) findViewById(R.id.eventTitle);
        aCTV = (AutoCompleteTextView) findViewById(R.id.locMenu);

        // Add event info and return to calling activity
        Button createEvent = (Button) findViewById(R.id.createEvent);
        createEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // If the event name is taken
                if (!eventHash.events.containsKey(eventTitle.getText())) {

                    // Add the event info
                    String eventName = eventTitle.getText().toString();
                    Calendar timeOfEvent = null;
                    String eventLocation = aCTV.getText().toString();
                    eventHash.addEvent(eventName, timeOfEvent, eventLocation);

                    finish();
                }
            }
        });

        // Allow users to enter new location in add event activity
        Button addLocation = (Button) findViewById(R.id.addLocation);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AddNewEvent.this, SetLocation.class);
                startActivityForResult(myIntent, 1);
            }
        });

        final TextView theDate = (TextView) findViewById(R.id.theDate);
        Calendar calendar = Calendar.getInstance();
        theDate.setText((calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR));

        final TextView theTime = (TextView) findViewById(R.id.theTime);
        setTime(theTime, calendar.HOUR_OF_DAY, calendar.MINUTE);
        setAutoComplete();

    }

// Repopulating auto complete when done adding new location

    protected void onActivityResult(int a, int b, Intent intent) {
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Method to populate autocomplete list
    private void setAutoComplete() {
        locations = eventHash.locations;
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
                Toast.makeText(AddNewEvent.this, "Coordinates:" + selectedCoors.latitude + "," + selectedCoors.longitude, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setTime(TextView theTime, int hour, int minute){
        String paddedMin;
        if(minute < 10) {
            paddedMin = "0" + minute;
        } else {
            paddedMin = "" + minute;
        }

        theTime.setText( ((hour - 1) % 12) + 1 + ":" + paddedMin + " " + (hour/12 != 0? "PM": "AM"));

    }

    private void setDate(TextView theDate, int month, int day, int year){
        theDate.setText( month + "/" + day + "/" + year);

    }
    public void setDateDialog(View v){
                final TextView theDate = (TextView) findViewById(R.id.theDate);

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.date_picker);
                dialog.setTitle("Title...");
                final DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker1);

                Button saveTime = (Button) dialog.findViewById(R.id.saveTime);
                saveTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View vw) {

                        setDate(theDate, dp.getMonth(), dp.getDayOfMonth(), dp.getYear());
                        dialog.dismiss();
                    }
                });
                dialog.show();
     }

    public void setTimeDialog(View v){
        final TextView theTime = (TextView) findViewById(R.id.theTime);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.time_picker);
        dialog.setTitle("Title...");
        final TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker1);

        Button saveTime = (Button) dialog.findViewById(R.id.saveTime);
        saveTime.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                setTime(theTime, tp.getCurrentHour(), tp.getCurrentMinute());
                dialog.dismiss();
            }
        });
        dialog.show();
        }


}
