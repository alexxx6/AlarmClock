package com.alexxx.alarmclock.providers;

import android.util.Log;

import com.alexxx.alarmclock.constants.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WeatherConditionProvider {
    private static final String WOEID_PLACEHOLDER = "{woeid}";
    private static final String REQUEST_URL = "https://query.yahooapis.com/v1/public/yql?q=select%20item.condition.code%20from%20weather.forecast%20where%20woeid%20%3D%20{woeid}&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    public static String getConditionCode(String woeid) {
        String code = Constants.EMPTY_STRING;
        try {
            URL url = new URL(WeatherConditionProvider.REQUEST_URL.replace(WeatherConditionProvider.WOEID_PLACEHOLDER, woeid));
            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            code = convertInputStreamToString(in);
        } catch (IOException e) {
            Log.e(Constants.WEATHER_CONDITION_PROVIDER_LOG_TAG, e.getMessage());
        }

        return code;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String response = Constants.EMPTY_STRING;
        while((line = bufferedReader.readLine()) != null) {
            response += line;
        }

        inputStream.close();
        String result = Constants.EMPTY_STRING;
        String[] tokens = response.split("code");
        if (tokens.length != 0) {
            tokens = tokens[1].split("\"");
            if (tokens.length != 0) {
                result = tokens[2];

            }
        }

        return result;
    }
}
