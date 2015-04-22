package com.example.david.hciprototype;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EventHash extends Application {
    public static HashMap<String, Event> events;
    public LocationHash locations;
    private int prepTime;
    public static Location locat;
    public static JSONObject jsonDirections;

    public void onCreate(){
        events = new HashMap<String, Event>();
        locations = new LocationHash();
        prepTime = 600000;
        startGPSListener();
    }

    public void addPrepTime(int time2Add) {
        prepTime += time2Add;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public int getPrepMin() {
        return prepTime / 60000;
    }

    public void addEvent(String name, Calendar time, String location){
        events.put(name, new Event(time, location));
    }

    // Returns average speed needed to be on time
    public double getAverageSpeed(String event){
        Double dist = distanceToEvent(event);
        Calendar timeOfEvent = events.get(event).eventTime();
        Double hoursDiff = getTimeDiff(event);
        double average = dist / hoursDiff;
        return average;
    }


    public double predictTime(LatLng loc){
        double dist = distanceToEvent(loc);

        return dist * 60.0 / 2.5 + (prepTime / 60000.0);
    }

    // Returns average speed needed to be on time with extra time (in minutes) to get ready
    public double getAverageForNotification(String event){
        Double dist = distanceToEvent(event);
        Double hoursDiff = getTimeDiff(event)-(prepTime/3600000.0);
        double average = dist.doubleValue() / hoursDiff.doubleValue();

        return average;
    }

    public double getTimeDiff(String event) {
        Calendar timeOfEvent = events.get(event).eventTime();
        Double hoursDiff = (timeOfEvent.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 3600000.0;
        return hoursDiff;
    }

    public boolean checkPrepDone(String event) {
        return false;
    }

    public ArrayList<LatLng> getDirection() {
        String encoded = "";
        try {
            encoded = jsonDirections.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Decode the overview_polyline
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }



    public Double distanceToEvent(String event){

        Location location = getCurrentLocation();
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
                jsonDirections = new JSONObject(theDirections);

                // Get the distance between points
                String distStringMeters = jsonDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").get("value").toString();
                Double dist = Double.parseDouble(distStringMeters.split(" ")[0].replaceAll(",",""));
                return dist*0.000621371;
            } catch (Exception e) {
                System.err.println("direction error " + e);
            }
        }
        return null;
    }

    public Double distanceToEvent(LatLng toCoords){

        Location location = getCurrentLocation();
        // Only execute if gps location was found and the location hashmap doesn't contain label
        if(location != null) {
            // format the coordinates for Google
            LatLng fromCoords = new LatLng(location.getLatitude(), location.getLongitude());

            String coords = makeCoords(fromCoords, toCoords);

            // Get Google's directions
            GoogleDirections directions = new GoogleDirections();
            try {
                String theDirections = directions.execute(coords).get();
                jsonDirections = new JSONObject(theDirections);

                // Get the distance between points

                String distStringMeters = jsonDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").get("value").toString();
                Double dist = Double.parseDouble(distStringMeters.split(" ")[0].replaceAll(",",""));

                return dist*0.000621371;
            } catch (Exception e) {
                System.err.println("direction error " + e);
            }
        }
        return null;
    }

    public Location getCurrentLocation(){
        return locat;
    }

    public void startGPSListener() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                locat = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
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
    public class Event{
        private Calendar eventTime;
        private String location;

        public Event(Calendar eventTime, String location){
            this.eventTime = eventTime;
            this.location = location;
        }

        public Calendar eventTime(){
            return eventTime;
        }

        public String getLocation(){
            return location;
        }
    }



    //--> LocationHash holds locations and their names
    public class LocationHash{
        private HashMap<String, LatLng> locations;

        public LocationHash(){
            locations = new HashMap<String, LatLng>();
            locations.put("Roseville", new LatLng(45.0061, -93.1567) );
            locations.put("Spooner",new LatLng(45.589321, -95.900281) );
            locations.put("HCI",new LatLng(45.589257, -95.902834) );
            locations.put("Big Cat",new LatLng(45.586419, -95.899508) );
            locations.put("Old Number One",new LatLng(45.585689, -95.913506) );
            locations.put("Willie's",new LatLng(45.588596, -95.914915) );
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

