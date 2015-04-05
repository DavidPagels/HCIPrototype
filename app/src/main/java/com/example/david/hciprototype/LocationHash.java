package com.example.david.hciprototype;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 1 on 4/4/2015.
 */
public class LocationHash {
    private HashMap<String, double[]> locations;
    public LocationHash() {
        locations = new HashMap<String, double[]>();
        locations.put("Roseville", new double[] {40.7127837, -74.00594130000002} );
        locations.put("Morris",new double[] {45.5919444, -95.91888890000001} );
    }

    public void add(String location, double latitude, double longitude) {
        double[] coordinates = new double[2];
        coordinates[0] = latitude;
        coordinates[1] = longitude;
        locations.put(location, coordinates);
    }

    public double[] getCoor(String location) {
        return locations.get(location);
    }

    public ArrayList<String> getAllLocations() {
        ArrayList<String> allLocs = new ArrayList<String>();
        for(String key: locations.keySet()) {
            allLocs.add(key);
        }
        return allLocs;
    }
}
