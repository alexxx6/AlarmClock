package com.alexxx.alarmclock.providers;

import com.alexxx.alarmclock.constants.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public class WoeidProvider {
    private static final String PARAMS_PLACEHOLDER = "{parameters}";
    private static final String REQUEST_URL = "https://api.flickr.com/services/rest/?method=flickr.places.findByLatLon&api_key=f7ed8bdf833e7124ff05fde3ed836db5&lat={parameters}&format=json&nojsoncallback=1";

    public static String getWoeiid(double latitude, double longitude) {
        String woeiid = Constants.EMPTY_STRING;
        try {
            String urlParams = String.format(Locale.ENGLISH, "%.11f", latitude) + "&lon=" + String.format(Locale.ENGLISH, "%.11f", longitude);
            URL url = new URL(WoeidProvider.REQUEST_URL.replace(WoeidProvider.PARAMS_PLACEHOLDER, urlParams));
            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            woeiid = convertInputStreamToString(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return woeiid;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String response = Constants.EMPTY_STRING;
        while ((line = bufferedReader.readLine()) != null) {
            response += line;
        }

        inputStream.close();
        String result = Constants.EMPTY_STRING;
        String[] tokens = response.split("woeid");
        if (tokens.length != 0) {
            tokens = tokens[1].split("\"", 4);
            if (tokens.length > 1) {
                result = tokens[2];
            }
        }
        return result;
    }
}