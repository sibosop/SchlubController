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

class GetHostInfoTask extends AsyncTask<Void,ArrayList<SchlubHost>,Boolean>{
    MainActivity mActivity;
    String tag;
    String host;
    public GetHostInfoTask(MainActivity mainActivity) {
        mActivity = mainActivity;
        tag = getClass().getSimpleName();
        host = "";
    }

    public GetHostInfoTask(MainActivity mainActivity, String host_) {
        mActivity = mainActivity;
        tag = getClass().getSimpleName();
        host = host_;
    }

    @Override
    protected Boolean doInBackground(Void... v) {
        Boolean rval = Boolean.FALSE;
        try {
            String subnet = mActivity.getSubnet();
            if ( subnet.isEmpty() )
                return Boolean.FALSE;

            ArrayList<String> ids = mActivity.getItemList(host);
            ArrayList<SchlubHost> schlubHosts = new ArrayList<SchlubHost>();
            Gson gson = new Gson();
            for(int i = 0; i < ids.size();++i) {
                        String id = ids.get(i);
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
        final TextView volumeValue = (TextView) mActivity.findViewById(R.id.VolumeValue);
        volumeValue.setText(s.vol.toString());
        final TextView SoundValue = (TextView) mActivity.findViewById(R.id.SoundValue);
        SoundValue.setText(s.sound);
        final TextView PhraseValue = (TextView) mActivity.findViewById(R.id.PhraseValue);
        PhraseValue.setText(s.phrase);
        updateServant(s);
    }

    private void updateServant(SchlubHost s){
        Log.i(tag, "updating servant " + s.toString());
        final TextView volumeValue = (TextView) mActivity.findViewById(R.id.LocalVolumeValue);
        volumeValue.setText(s.vol.toString());
        final TextView SoundValue = (TextView) mActivity.findViewById(R.id.LocalSoundValue);
        SoundValue.setText(s.sound);
        final TextView PhraseValue = (TextView) mActivity.findViewById(R.id.LocalPhraseValue);
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
