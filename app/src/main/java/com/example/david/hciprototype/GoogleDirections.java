package com.example.david.hciprototype;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleDirections extends AsyncTask<String, Void, String> {
        String returnedJSON = "";
        protected String doInBackground(String ... params){
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + params[0].split(" ")[0] + "&destination=" + params[0].split(" ")[1];
            System.out.println(url);
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                return response.toString();
            } catch(Exception e){
                System.out.println("caught exception");
                return null;
            }

        }

}
