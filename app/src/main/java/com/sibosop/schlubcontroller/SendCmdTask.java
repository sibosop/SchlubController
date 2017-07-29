package com.sibosop.schlubcontroller;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by brian on 7/28/17.
 */

public class SendCmdTask extends AsyncTask <String,String,Boolean> {
    String tag;
    MainActivity mActivity;
    SendCmdTask(MainActivity mainActivity){
        mActivity = mainActivity;
        tag = getClass().getSimpleName();
    }

    @Override
    protected Boolean doInBackground(String... cmds) {
        Boolean rval = Boolean.FALSE;
        try {
            if (cmds.length == 1) {
                String cmd = cmds[0];
                Gson gson = new Gson();
                ArrayList<String> ids = mActivity.getItemList("");
                String subnet = ids.get(0);
                for ( int i = 1; i < ids.size(); ++i ) {
                    String response = new SclubRequest(subnet, ids.get(i)).send(cmd);
                    SchlubStatus s = gson.fromJson(response, SchlubStatus.class);
                    Log.i(tag,"cmd:"+cmd+" status:"+s.toString());
                    rval = Boolean.TRUE;
                }
            } else {
                Log.e(tag, "illegal param length:" + Integer.valueOf(cmds.length));
            }
        }
        catch ( Exception e) {
            Log.e(tag,e.toString());
        }
        return rval;
    }
}
