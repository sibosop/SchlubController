package com.sibosop.schlubcontroller;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 7/24/17.
 */

class GetHostInfoTask extends AsyncTask<Void,ArrayList<SchlubHost>,Boolean>{
    private MainActivity mActivity;
    private String tag;
    private ArrayList<String> hostList;
    public GetHostInfoTask(MainActivity mainActivity) {
        mActivity = mainActivity;
        tag = getClass().getSimpleName();
        hostList = mActivity.getItemList("");
    }

    public GetHostInfoTask(MainActivity mainActivity, ArrayList<String> hostList_ ) {
        mActivity = mainActivity;
        tag = getClass().getSimpleName();
        hostList = hostList_;
    }

    @Override
    protected Boolean doInBackground(Void... v) {
        Boolean rval = Boolean.FALSE;
        try {
            String subnet = mActivity.getSubnet();
            if ( subnet.isEmpty() )
                return Boolean.FALSE;

            ArrayList<SchlubHost> schlubHosts = new ArrayList<SchlubHost>();
            Gson gson = new Gson();

            for(int i = 0; i < hostList.size();++i) {
                String id = hostList.get(i);
                String response = new SclubRequest(subnet,id).send("probe");
                SchlubHost s = gson.fromJson(response, SchlubHost.class);
                s.id = id;
                schlubHosts.add(s);
                rval = Boolean.TRUE;
            }
            publishProgress(schlubHosts);
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
        final TextView volumeValue = (TextView) mActivity.findViewById(R.id.VolumeValue);
        volumeValue.setText(s.vol.toString());
        final TextView SoundValue = (TextView) mActivity.findViewById(R.id.SoundValue);
        SoundValue.setText(s.sound);
        final TextView PhraseValue = (TextView) mActivity.findViewById(R.id.PhraseValue);
        PhraseValue.setText(s.phrase);
        final TextView threadsValue = (TextView) mActivity.findViewById(R.id.ThreadsValue);
        threadsValue.setText(s.threads.toString());
        final TextView speakerValue = (TextView) mActivity.findViewById(R.id.SpeakerValue);
        speakerValue.setText(s.speaker);
        final CheckBox masterCheckBox = (CheckBox) mActivity.findViewById(R.id.MasterCheckBox);
        masterCheckBox.setChecked(s.isMaster);
        mActivity.hostInfo.put(s.id,s);
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
