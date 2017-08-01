package com.sibosop.schlubcontroller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.solers.slp.Locator;
import com.solers.slp.ServiceLocationEnumeration;
import com.solers.slp.ServiceLocationManager;
import com.solers.slp.ServiceType;
import com.solers.slp.ServiceURL;


import java.util.Properties;
import java.util.Vector;


/*
 * Created by brian on 7/23/17.
 */

public class HostRefreshTask extends AsyncTask<Context,HostInfo,HostInfo> {
    private Locator loc;
    Vector scopes = new Vector();
    MainActivity mActivity;
    String service;
    HostRefreshTask(MainActivity activity) {
        super();
        mActivity = activity;
        service = mActivity.getResources().getString(R.string.service);
    }
    @Override
    protected HostInfo doInBackground(Context... contexts) {
        org.apache.log4j.BasicConfigurator.configure();
        org.apache.log4j.Category.getRoot().setPriority(org.apache.log4j.Priority.WARN);
        Properties p = new Properties();
        p.put("net.slp.traceDATraffic", "true");
        p.put("net.slp.traceMsg", "true");
        p.put("net.slp.traceDrop", "true");
        p.put("net.slp.traceReg", "true");
        p.put("net.slp.multicastMaximumWait","20000");
        // The following are necessary for secure use
        //p.put("net.slp.securityEnabled", "true");
        // p.put("net.slp.publicKey.myspi", "path to der public key");
        // p.put("net.slp.spi", "myspi");
        ServiceLocationManager.init(p);
        try {
            loc = ServiceLocationManager.getLocator(java.util.Locale.getDefault());
        } catch (com.solers.slp.ServiceLocationException e){
            Log.i("slp",e.toString());
        }
        scopes.add("default");

        Resources r = contexts[0].getResources();
        HostInfo rval = new HostInfo();
        Log.i(r.getString(R.string.matag), r.getString(R.string.hostRefreshMessage));
        try {
            String selector = "";
            ServiceLocationEnumeration sleenum =
                    loc.findServices(new ServiceType(service), scopes, selector);

            ServiceURL sUrl = null;
            while (sleenum.hasMoreElements()) {
                sUrl = (com.solers.slp.ServiceURL) sleenum.next();

                if (sUrl != null) {
                    String parse = sUrl.toString();
                    Log.i("serviceLocation", "Found URL: " + parse);
                    int j = parse.lastIndexOf(".");
                    if ( j == - 1){
                        Log.e("finding host Id","error in parse:"+parse);
                        break;
                    }
                    if ( rval.subnet.isEmpty() ) {
                        int i = parse.lastIndexOf("//");
                        if ( i == -1 ) {
                            Log.e("finding subnet","subnet delimiter // not found!!");
                            break;
                        }
                        rval.subnet = parse.substring(i+2,j);
                    }
                    rval.ids.add(parse.substring(j+1));
                }
            }

        }
        catch (com.solers.slp.ServiceLocationException e){
            Log.i("slp",e.toString());
        }
        rval.sort();
        publishProgress(rval);
        Log.i(r.getString(R.string.matag), r.getString(R.string.returnHostRefreshMessage) + ":" +rval.toString());
        return rval;
    }
    @Override
    protected void onProgressUpdate(HostInfo... update) {
        if (update.length > 0) {
            Log.i("onProgressUpate", update[0].toString());
            final TextView subnetView = (TextView)mActivity.findViewById(R.id.SubnetValue);
            subnetView.setText(update[0].subnet);

            Spinner statusHostSpinner = (Spinner) mActivity.findViewById(R.id.StatusHostSpinner);
            ArrayAdapter<String> statusSpinnerAdapter = new ArrayAdapter<String>(mActivity,
                    android.R.layout.simple_spinner_item, update[0].ids);
            statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusHostSpinner.setAdapter(statusSpinnerAdapter);

            update[0].ids.add("all");
            Spinner controlHostSpinner = (Spinner) mActivity.findViewById(R.id.ControlHostSpinner);
            ArrayAdapter<String> controlSpinnerAdapter = new ArrayAdapter<String>(mActivity,
                    android.R.layout.simple_spinner_item, update[0].ids);
            controlSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            controlHostSpinner.setAdapter(controlSpinnerAdapter);


            mActivity.startHostInfoRefresh();
        }
    }
}
