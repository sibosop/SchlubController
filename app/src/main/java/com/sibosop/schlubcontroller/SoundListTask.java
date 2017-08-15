package com.sibosop.schlubcontroller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by brian on 8/14/17.
 */

public class SoundListTask extends AsyncTask<Void,Void,Boolean> {
    MainActivity mActivity;
    ProgressDialog pDialog;
    private String tag;
    SoundListTask(MainActivity ma) {
        mActivity = ma;
        tag = getClass().getSimpleName();
    }

    @Override
    protected void onPreExecute()
    {
        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Retrieving song list...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... v) {
        try {
            String subnet = mActivity.getSubnet();
            if ( subnet.isEmpty() )
                return Boolean.FALSE;

            Gson gson = new Gson();
            String master = mActivity.getMaster();
            if ( master.isEmpty() )
                return Boolean.FALSE;


            SchlubCmd schlubCmd = new SchlubCmd("SoundList");
            String response = new SclubRequest(mActivity,subnet,master).send(schlubCmd.getJson());
            mActivity.soundList  = gson.fromJson(response, SoundList.class);
            publishProgress();
        }
        catch ( Exception e) {
            Log.e(tag,e.toString());
        }

        return Boolean.TRUE;
    }
    @Override
    protected void onProgressUpdate(Void... v){
        Log.i(tag,"onProgressUpdate");
        super.onProgressUpdate(v);
        //pDialog.setMessage("Found:"+id[0]);
    }


    @Override
    protected void onPostExecute(Boolean s) {
        Log.i(tag,"onPostExecute");
        pDialog.dismiss();
        super.onPostExecute(s);
    }
}
