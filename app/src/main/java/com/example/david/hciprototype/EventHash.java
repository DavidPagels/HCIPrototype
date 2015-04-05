package com.example.david.hciprototype;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class EventHash extends Application {
    public HashMap<String, Event> events;
    public LocationHash locations;

    public void onCreate(){
        events = new HashMap<String, Event>();
        locations = new LocationHash();
    }

    public void addEvent(String name, Calendar time, String location){
        events.put(name, new Event(time, location));
    }

    public String distanceToEvent(String event){
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = new Location(""); // hard coding morris, mn for now
        location.setLatitude(45.5861);
        location.setLongitude(-95.9139);//lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Only execute if gps location was found and the location hashmap doesn't contain label
        if(location != null) {
            // format the coordinates for Google
            LatLng fromCoords = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng toCoords = locations.getCoordinates(events.get(event).location);
            String coords = makeCoords(fromCoords, toCoords);

            // Get Google's directions
            GoogleDirections directions = new GoogleDirections();
            try {
                String theDirections = directions.execute(coords).get();
                JSONObject jsonDirections = new JSONObject(theDirections);

                // Get the distance between points
                return jsonDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").get("text").toString();
            } catch (Exception e) {
                System.err.println("direction error " + e);
            }
        }
        return null;
    }


    //--> returns a string of the format "fromlat,fromlong tolat,tolong"
    private String makeCoords(LatLng fromCoords, LatLng toCoords){
        String fromLat = Double.toString(fromCoords.latitude);
        String fromLong = Double.toString(fromCoords.longitude);
        String toLat = Double.toString(toCoords.latitude);
        String toLong = Double.toString(toCoords.longitude);
        return fromLat + "," + fromLong + " " + toLat + "," + toLong;

    }



    //--> Event class to store event time and location name
    private class Event{
        public Calendar eventTime;
        public String location;

        public Event(Calendar eventTime, String location){
            this.eventTime = eventTime;
            this.location = location;
        }
    }



    //--> LocationHash holds locations and their names
    public class LocationHash{
        private HashMap<String, LatLng> locations;

        public LocationHash(){
            locations = new HashMap<String, LatLng>();
            locations.put("Roseville", new LatLng(40.7127837, -74.00594130000002) );
            locations.put("Morris",new LatLng(45.5919444, -95.91888890000001) );
        }

        public void addLocation(String location, double latitude, double longitude) {
            locations.put(location, new LatLng(latitude, longitude));
        }

        public LatLng getCoordinates(String location) {
            return locations.get(location);
        }

        public HashMap<String,double[]> getLocationHash(){ return (HashMap<String, double[]>) locations.clone();}

        public ArrayList<String> getAllLocations() {
            ArrayList<String> allLocs = new ArrayList<String>();
            for(String key: locations.keySet()) {
                allLocs.add(key);
            }
            return allLocs;
        }
    }
}

