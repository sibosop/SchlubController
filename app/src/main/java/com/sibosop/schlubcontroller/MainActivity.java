package com.sibosop.schlubcontroller;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    HostInfo hostInfo;
    private String tag;

    MainActivity() {
        super();
        tag = this.getClass().getSimpleName();
    }

    public ArrayList<String> getItemList(String item) {
        ArrayList<String> ids = new ArrayList<>();
        final TextView subnetView = (TextView) MainActivity.this.findViewById(R.id.Subnet);
        String subnet = subnetView.getText().toString();
        if (!item.equals("none")
                && !subnet.equals(getResources().getString(R.string.subnet))) {
            ids.add(subnet);
            if ( item.isEmpty()) {
                final Spinner hostSpinner = (Spinner) findViewById(R.id.HostSpinner);
                SpinnerAdapter spinnerAdapter = hostSpinner.getAdapter();
                int n = spinnerAdapter.getCount();
                for (int i = 0; i < n; i++) {
                    ids.add( (String)spinnerAdapter.getItem(i));
                }
            }
            else {

                ids.add(item);
            }
        }
        return ids;
    }
    @Override
    public void onClick(View v) {
        // do something when the button is clicked
        // Yes we will handle click here but which button clicked??? We don't know
        String[] cmd = new String[1];
        // So we will make
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.shutdown:
                Log.i(tag,"click on host shutdown");
                cmd[0] = "poweroff";
                break;
            case R.id.reboot:
                Log.i(tag,"click on reboot");
                cmd[0] = "reboot";
                break;
            case R.id.upgrade:
                Log.i(tag,"click on host upgrade");
                cmd[0] = "upgrade";
                break;

            case R.id.refreshHosts:
                // do something when the corky3 is clicked
                Log.i(tag,"click on host refresh");
                new HostRefreshTask(this).execute(getBaseContext());

            default:
                return;
        }
        new SendCmdTask(MainActivity.this).execute(cmd);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new HostRefreshTask(this).execute(getBaseContext());

        runOnUiThread(new Runnable() {
            public void run() {
                setContentView(R.layout.activity_main);
            }
        });

        final Button refreshButton = (Button)findViewById(R.id.refreshHosts);
        refreshButton.setOnClickListener(this);


        final Button rebootButton = (Button)findViewById(R.id.reboot);
        rebootButton.setOnClickListener(this);

        final Button upgradeButton = (Button)findViewById(R.id.upgrade);
        upgradeButton.setOnClickListener(this);

        final Button shutdownButton = (Button)findViewById(R.id.shutdown);
        shutdownButton.setOnClickListener(this);

        final Spinner hostSpinner = (Spinner) findViewById(R.id.HostSpinner);
        hostSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                ArrayList<String> ids = getItemList(item);
                new GetHostInfoTask(MainActivity.this).execute(ids);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


    }

}
