package com.sibosop.schlubcontroller;

import android.util.Log;

import com.google.gson.Gson;


import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
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


        String url = "http://"+hostAddr+":"+port;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");
            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            Log.i(tag,"sending cmd:"+cmd);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(cmd);
            wr.flush();
            int HttpResult = con.getResponseCode();
            StringBuilder sb = new StringBuilder();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                System.out.println("" + sb.toString());
            } else {
                System.out.println(con.getResponseMessage());
            }

            rval = sb.toString();
        }
        catch (Exception e) {
            Log.e(tag,e.toString());
        }
        Log.i(tag,rval);
        return rval;
    }

}
