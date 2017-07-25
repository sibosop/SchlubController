package com.sibosop.schlubcontroller;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by brian on 7/24/17.
 */

class GetHostInfoTask extends AsyncTask<String,SchlubHost,Boolean>{
    Activity mActivity;
    String tag;
    public GetHostInfoTask(MainActivity mainActivity) {
        mActivity = mainActivity;
        tag = getClass().getSimpleName();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Boolean rval = Boolean.FALSE;
        try {
            if (params.length == 2) {
                if ( params[0] == mActivity.getResources().getString(R.string.subnet))
                    return rval;
                if ( params[1] == mActivity.getResources().getString(R.string.all))
                    return rval;

                String response = new SclubRequest(params[0], params[1]).probe();

                SchlubHost schlubHost = new Gson().fromJson(response, SchlubHost.class);
                Log.i(tag,schlubHost.toString());
                rval = Boolean.TRUE;
            } else {
                Log.e(tag, "illegal param length:" + Integer.valueOf(params.length));
            }
        }
        catch ( Exception e) {
            Log.e(tag,e.toString());
        }
        return rval;
    }
}
