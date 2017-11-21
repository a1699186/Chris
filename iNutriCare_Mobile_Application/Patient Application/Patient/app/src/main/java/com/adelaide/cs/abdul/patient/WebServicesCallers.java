package com.adelaide.cs.abdul.patient;

import android.util.Log;

import com.google.common.io.CharStreams;

import org.json.simple.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by abdul on 4/20/2016.
 */
public class WebServicesCallers {

    public static String baseURL = "http://192.168.0.100:8080";
    public String Post(String serviceURL, JSONObject JSON) {

        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(serviceURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            byte[] outputBytes = JSON.toString().getBytes();
            OutputStream out = urlConnection.getOutputStream();
            out.write(outputBytes);
            out.flush();
            out.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200)
                return statusCode + "";

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = CharStreams.toString(new InputStreamReader(in));
            if (urlConnection != null)
                urlConnection.disconnect();
            return result;

        } catch (IOException e) {
            return e.getMessage().toString();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    public String Get(String serviceURL) {

        HttpURLConnection urlConnection = null;
        try {
            //Log.i("GET WebServiceCallers ", " =============================== >Started");
            URL url = new URL(serviceURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            //Log.i("GET WebServiceCallers ", " =============================== >After open connection");
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            urlConnection.setRequestMethod("GET");
            //urlConnection.setRequestProperty("Content-Type", "application/json");
            //Log.i("GET WebServiceCallers ", " =============================== >Before Connect");
            urlConnection.connect();
            //Log.i("GET WebServiceCallers ", " =============================== >After Connect");

            int statusCode = urlConnection.getResponseCode();
            //Log.i("GET WebServiceCallers ", " =============================== >Status Code = "+statusCode);
            if (statusCode != 200)
                return statusCode + "";

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = CharStreams.toString(new InputStreamReader(in));
            if (urlConnection != null)
                urlConnection.disconnect();
            return result;

        } catch (IOException e) {
            //Log.i("GET WebServiceCallers ", " =============================== >IO E msg = "+e.getMessage());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
