package com.example.david.hciprototype;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;


public class NewActivity extends ActionBarActivity {
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
                Intent myIntent = new Intent(NewActivity.this, SetLocation.class);
                startActivityForResult(myIntent, 1);
            }
        });

        final TextView theDate = (TextView) findViewById(R.id.theDate);
        theDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.date_picker);
                    dialog.setTitle("Title...");
                    final TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker1);

                    Button saveTime = (Button) dialog.findViewById(R.id.saveTime);
                    saveTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            theDate.setFocusable(false);
                            theDate.setFocusable(true);
                            setTime(theDate, tp.getCurrentHour(), tp.getCurrentMinute());
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });


        final TextView theTime = (TextView) findViewById(R.id.theTime);
        theTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.time_picker);
                    dialog.setTitle("Title...");
                    final TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker1);

                    Button saveTime = (Button) dialog.findViewById(R.id.saveTime);
                    saveTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            theTime.setFocusable(false);
                            theTime.setFocusable(true);
                            setTime(theTime, tp.getCurrentHour(), tp.getCurrentMinute());
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });


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
                Toast.makeText(NewActivity.this, "Coordinates:" + selectedCoors.latitude + "," + selectedCoors.longitude, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setTime(TextView theTime, int hour, int minute){
        theTime.setText( (hour - 1 % 12) + 1 + ":" + minute + " " + (hour/12 != 0? "PM": "AM"));

    }
}
