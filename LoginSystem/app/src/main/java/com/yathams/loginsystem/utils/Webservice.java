package com.yathams.loginsystem.utils;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Webservice {

    private static final int TIME_OUT = 30*1000; //30 sec
    public static final String BASE = "http://www.google.com/";

    public static final String LOGIN = "login.php";
    public static final String SIGN_UP = "sign_up.php";
    public static final String RESET_PASSWORD = "reset_password.php";
    public static final String FORGOT_PASSWORD = "forgot_password.php";
    public static final String STORE_GPLUS_INFO = "save_gplus_info.php";

    public static String callPostService(String urlString, String requestString) {

        String response = "";
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE+urlString);
            Log.d("yvr", "The request : " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT /* milliseconds */);
            conn.setConnectTimeout(TIME_OUT /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            // Starts the query
//            conn.connect();

            //Send request
            DataOutputStream wr = new DataOutputStream (conn.getOutputStream());
            wr.writeBytes(requestString);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            Log.d("yvr", "The response responseCode: " + responseCode);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            response = s.hasNext() ? s.next() : "";
            Log.d("yvr", "The response is :" + response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null){
                conn.disconnect();
            }
        }
        return response;

    }

    public static String callGetService(String urlString) {
        String response = "";
        InputStream is = null;
        HttpURLConnection conn = null;
        URL url = null;
        try {
            url = new URL(BASE+urlString);
            Log.d("yvr", "The request is: " + url);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT /* milliseconds */);
            conn.setConnectTimeout(TIME_OUT /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d("yvr", "The response code is: " + responseCode);
            is = conn.getInputStream();
            // Convert the InputStream into a string
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            response = s.hasNext() ? s.next() : "";
            Log.d("yvr", "The response is :"+response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null){
                conn.disconnect();
            }
        }
        return response;

    }

}
