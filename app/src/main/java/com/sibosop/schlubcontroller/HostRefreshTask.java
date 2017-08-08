package com.sibosop.schlubcontroller;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class HostRefreshTask extends AsyncTask<Context,String,HostInfo> {
    private Locator loc;
    Vector scopes = new Vector();
    MainActivity mActivity;
    String service;
    HostRefreshTask(MainActivity activity) {
        super();
        mActivity = activity;
        service = mActivity.getResources().getString(R.string.service);
    }
    private ProgressDialog pDialog;
    @Override
    protected void onPreExecute()
    {
        pDialog = new ProgressDialog(mActivity);
        pDialog.setMessage("Gathering host data...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        super.onPreExecute();
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
        HostInfo hostInfo = new HostInfo();
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
                    if ( hostInfo.subnet.isEmpty() ) {
                        int i = parse.lastIndexOf("//");
                        if ( i == -1 ) {
                            Log.e("finding subnet","subnet delimiter // not found!!");
                            break;
                        }
                        hostInfo.subnet = parse.substring(i+2,j);
                    }
                    String hostId = parse.substring(j+1);
                    hostInfo.ids.add(hostId);
                    publishProgress(hostId);
                }
            }

        }
        catch (com.solers.slp.ServiceLocationException e){
            Log.i("slp",e.toString());
        }
        hostInfo.sort();

        Log.i(r.getString(R.string.matag), r.getString(R.string.returnHostRefreshMessage) + ":" +hostInfo.toString());
        return hostInfo;
    }
    @Override
    protected void onProgressUpdate(String...id){
        super.onProgressUpdate(id);
        //pDialog.setMessage("Found:"+id[0]);
    }


    @Override
    protected void onPostExecute(HostInfo update) {

        mActivity.uiLog("onPostExecute" +
                "" +
                ""+ update.toString());
        final TextView subnetView = (TextView)mActivity.findViewById(R.id.SubnetValue);
        subnetView.setText(update.subnet);
        HostInfo hostInfo = update.fuckingDeepCopy();

        Spinner statusHostSpinner = (Spinner) mActivity.findViewById(R.id.StatusHostSpinner);
        ArrayAdapter<String> statusSpinnerAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, hostInfo.ids);
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusHostSpinner.setAdapter(statusSpinnerAdapter);

        update.ids.add("all");
        Spinner controlHostSpinner = (Spinner) mActivity.findViewById(R.id.ControlHostSpinner);
        ArrayAdapter<String> controlSpinnerAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, update.ids);
        controlSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controlHostSpinner.setAdapter(controlSpinnerAdapter);

        pDialog.dismiss();
        mActivity.startHostInfoRefresh();
        super.onPostExecute(update);

    }
}
