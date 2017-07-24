package com.sibosop.schlubcontroller;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
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
    HostRefreshTask() {
        super();
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
    }
    @Override
    protected HostInfo doInBackground(Context... contexts) {
        HostInfo rval = new HostInfo();
        Resources r = contexts[0].getResources();
        Log.i(r.getString(R.string.matag), r.getString(R.string.hostRefreshMessage));
        try {
            String selector = "";
            ServiceLocationEnumeration sleenum =
                    loc.findServices(new ServiceType("service:schlub.x"), scopes, selector);

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
        if (update.length > 0)
            Log.i("onProgressUpate",update[0].toString());
    }
}
