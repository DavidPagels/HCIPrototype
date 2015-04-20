package com.example.david.hciprototype;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    Calendar savedDate;

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
                    String eventLocation = aCTV.getText().toString();
                    eventHash.addEvent(eventName, savedDate, eventLocation);

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

        savedDate = Calendar.getInstance();

        final TextView theTime = (TextView) findViewById(R.id.theTime);
        // savedDate.add(Calendar.HOUR_OF_DAY, 1); maybe in the future

        setTime(theTime, savedDate.get(Calendar.HOUR_OF_DAY), savedDate.get(Calendar.MINUTE));

        final TextView theDate = (TextView) findViewById(R.id.theDate);
        setDate(theDate, savedDate.get(Calendar.YEAR), savedDate.get(Calendar.MONTH), savedDate.get(Calendar.DAY_OF_MONTH));

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
                savedDate = Calendar.getInstance();
                Double dist = eventHash.predictTime(selectedCoors);
                Toast.makeText(AddNewEvent.this, "Coordinates:" + dist, Toast.LENGTH_SHORT).show();
                final TextView theTime = (TextView) findViewById(R.id.theTime);
                final TextView theDate = (TextView) findViewById(R.id.theDate);
                savedDate.add(Calendar.MINUTE, dist.intValue());
                setDate(theDate, savedDate.get(Calendar.YEAR), savedDate.get(Calendar.MONTH), savedDate.get(Calendar.DAY_OF_MONTH));
                setTime(theTime, savedDate.get(Calendar.HOUR_OF_DAY), savedDate.get(Calendar.MINUTE));
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(aCTV.getWindowToken(), 0);

            }

        });
    }

    private void setTime(TextView theTime, int hour, int minute){

        savedDate.set(savedDate.get(Calendar.YEAR), savedDate.get(Calendar.MONTH), savedDate.get(Calendar.DAY_OF_MONTH), hour, minute);
        int clockHour;
        if (savedDate.get(Calendar.HOUR) == 0) {
            clockHour = savedDate.get(Calendar.HOUR) + 12;
        } else {
            clockHour = savedDate.get(Calendar.HOUR);
        }
        theTime.setText(clockHour + ":" + (savedDate.get(Calendar.MINUTE) < 10 ? "0": "")+ savedDate.get(Calendar.MINUTE) + " " + (savedDate.get(Calendar.AM_PM) == 0 ? "AM":"PM"));

    }

    private void setDate(TextView theDate, int year, int month, int day){
        savedDate.set(year, month, day);

        theDate.setText((month + 1) + "/" + day + "/" + year);

    }
    public void setDateDialog(View v){
        final TextView theDate = (TextView) findViewById(R.id.theDate);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.date_picker);
        dialog.setTitle("Set Date");
        final DatePicker dp = (DatePicker) dialog.findViewById(R.id.datePicker1);
        dp.updateDate(savedDate.get(Calendar.YEAR), savedDate.get(Calendar.MONTH), savedDate.get(Calendar.DAY_OF_MONTH));
        Button saveTime = (Button) dialog.findViewById(R.id.saveTime);
        saveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vw) {

                setDate(theDate, dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void setTimeDialog(View v){
        final TextView theTime = (TextView) findViewById(R.id.theTime);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.time_picker);
        dialog.setTitle("Set Time");
        final TimePicker tp = (TimePicker) dialog.findViewById(R.id.timePicker1);
        tp.setCurrentHour(savedDate.get(Calendar.HOUR_OF_DAY));
        tp.setCurrentMinute(savedDate.get(Calendar.MINUTE));

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
