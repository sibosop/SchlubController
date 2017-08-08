package com.sibosop.schlubcontroller;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;


import javax.net.ssl.HttpsURLConnection;
/**
 * Created by brian on 7/24/17.
 */

class SclubRequest {
    MainActivity mainActivity;
    private String hostAddr;
    private String tag;
    private final String port="8080";
    private final String USER_AGENT = "Mozilla/5.0";
    public SclubRequest(MainActivity mActivity, String subnet, String host) {
        hostAddr = subnet+"."+host;
        tag = this.getClass().getSimpleName();
        mainActivity = mActivity;
    }




    public String send(String cmd) {
        Log.i(tag,hostAddr);
        String rval = "{}";


        String url = "http://"+hostAddr+":"+port+"/"+cmd;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            Log.i(tag,"Sending 'GET' request to URL : " + url);
            //mainActivity.uiLog("Sending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            rval = response.toString();
        }
        catch (Exception e) {
            Log.e(tag,e.toString());
        }
        Log.i(tag,rval);
        return rval;
    }

}
