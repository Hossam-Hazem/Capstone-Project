package com.HossamHazem.DishFinder.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyConnection {
    final static String LOG_TAG = MyConnection.class.getSimpleName();

    public static String connect(String uriString) {
        String resultJSONString;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        URL url = null;
        InputStream inputStream = null;
        try {
            url = new URL(uriString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            if (urlConnection != null) {
                // urlConnection.disconnect();
            }
        }

        // Read the input stream into a String

        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            return null;
        }
        resultJSONString = buffer.toString();

        if (urlConnection != null) {
            urlConnection.disconnect();
        }

        return resultJSONString;
    }
}
