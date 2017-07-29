package com.sibosop.schlubcontroller;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 7/24/17.
 */

class GetHostInfoTask extends AsyncTask<ArrayList<String>,ArrayList<SchlubHost>,Boolean>{
    Activity mActivity;
    String tag;
    public GetHostInfoTask(MainActivity mainActivity) {
        mActivity = mainActivity;
        tag = getClass().getSimpleName();
    }

    @Override
    protected Boolean doInBackground(ArrayList<String>... params) {
        Boolean rval = Boolean.FALSE;
        try {
            if (params.length == 1) {
                if (params[0].size() == 0) {
                    Log.i(tag,"exiting on empty params");
                    return rval;
                }
                String subnet = params[0].get(0);
                ArrayList<SchlubHost> schlubHosts = new ArrayList<SchlubHost>();
                Gson gson = new Gson();
                for(int i = 1; i < params[0].size();++i) {
                        String id = params[0].get(i);
                        String response = new SclubRequest(subnet,id).send("probe");
                        SchlubHost s = gson.fromJson(response, SchlubHost.class);
                        s.id = id;
                        schlubHosts.add(s);
                        rval = Boolean.TRUE;
                }
                publishProgress(schlubHosts);
            } else {
                Log.e(tag, "illegal param length:" + Integer.valueOf(params.length));
            }
        }
        catch ( Exception e) {
            Log.e(tag,e.toString());
        }
        return rval;
    }
    private void updateMaster(SchlubHost s){
        Log.i(tag, "updating Master " + s.toString());
        final TextView masterValue = (TextView) mActivity.findViewById(R.id.MasterValue);
        masterValue.setText(s.id);
        updateServant(s);
    }

    private void updateServant(SchlubHost s){
        Log.i(tag, "updating servant " + s.toString());
        final TextView volumeValue = (TextView) mActivity.findViewById(R.id.volumeValue);
        volumeValue.setText(s.vol.toString());
        final TextView SoundValue = (TextView) mActivity.findViewById(R.id.SoundValue);
        SoundValue.setText(s.sound);
        final TextView PhraseValue = (TextView) mActivity.findViewById(R.id.PhraseValue);
        PhraseValue.setText(s.phrase);
    }

    @Override
    protected void onProgressUpdate(ArrayList<SchlubHost>... update) {
        if (update.length == 1) {
            for ( SchlubHost s : update[0]) {
                Log.i(tag, "onProgress:" + s.toString());
                if (s.isMaster) {
                    updateMaster(s);
                } else {
                    updateServant(s);
                }
            }
        }
    }
}
