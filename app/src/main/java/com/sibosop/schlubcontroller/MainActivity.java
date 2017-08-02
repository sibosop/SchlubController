package com.sibosop.schlubcontroller;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String tag;
    HashMap<String,SchlubHost> hostInfo = new HashMap<String,SchlubHost>();
    Object getHostFromType(String key, String type) {
        if (type.equals("volume"))
            return hostInfo.get(key).vol;
        else if (type.equals("threads"))
            return hostInfo.get(key).threads;
        else if (type.equals("sound"))
            return hostInfo.get(key).sound;
        else if (type.equals("phrase"))
            return hostInfo.get(key).phrase;
        return 0;
    }
    void setHostFromType(String key, String type, Object val) {
        if (type.equals("volume"))
            hostInfo.get(key).vol = (Integer)val;
        else if (type.equals("threads"))
            hostInfo.get(key).threads = (Integer)val;
        else if (type.equals("sound"))
            hostInfo.get(key).sound = (String)val;
        else if (type.equals("phrase"))
            hostInfo.get(key).phrase = (String)val;
    }

    MainActivity() {
        super();
        tag = this.getClass().getSimpleName();
    }

    public ArrayList<String> getItemList(String item) {
        ArrayList<String> ids = new ArrayList<>();
        if (!item.equals("none")) {
            if ( item.equals("all")) {
                final Spinner hostSpinner = (Spinner) findViewById(R.id.StatusHostSpinner);
                SpinnerAdapter spinnerAdapter = hostSpinner.getAdapter();
                int n = spinnerAdapter.getCount();
                for (int i = 0; i < n; i++) {
                    String spinnerItem = (String)spinnerAdapter.getItem(i);
                    if ( !spinnerItem.equals("all"))
                        ids.add(spinnerItem);
                }
            }
            else {
                ids.add(item);
            }
        }
        return ids;
    }
    public String getSubnet() {
        final TextView subnetView = (TextView) findViewById(R.id.SubnetValue);
        String subnet = subnetView.getText().toString();
        if ( subnet == getString(R.string.subnetDefault))
            subnet = "";
        return subnet;
    }
    public String getMaster() {
        final TextView masterView = (TextView) findViewById(R.id.MasterValue);
        String master = masterView.getText().toString();
        if ( master == getString(R.string.neverSet) )
            master = "";
        return master;
    }

    public String getControlHost() {
        final Spinner hostSpinner = (Spinner) findViewById(R.id.ControlHostSpinner);
        String item = (String) hostSpinner.getSelectedItem().toString();
        return item;
    }

    public String getStatusHost() {
        final Spinner statusSpinner = (Spinner) findViewById(R.id.StatusHostSpinner);
        String item = (String) statusSpinner.getSelectedItem().toString();
        return item;
    }

    private AlertDialog setValueDialog;
    private void doSetValueDialog(final String hostCmd,final String host,final String type) {
        if ( setValueDialog != null ) {
            setValueDialog.dismiss();
            setValueDialog = null;
        }


        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View setValueAlertView = inflater.inflate(R.layout.value_set_alert, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        Integer initValue = 0;
        Integer max = 100;
        if (  type.equals("volume")) {
            initValue = hostInfo.get(host).vol;
            max = 100;
        }
        else if ( type.equals("threads")) {
            initValue = hostInfo.get(host).threads;
            max = 10;
        }

        SeekBar seek=(SeekBar)setValueAlertView.findViewById(R.id.ValueSetSeekbar);
        seek.setProgress(initValue);
        seek.setMax(max);
        TextView textView = (TextView)setValueAlertView.findViewById(R.id.ValueSetValue);
        textView.setText(String.valueOf(initValue));
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                TextView textView = (TextView)setValueAlertView.findViewById(R.id.ValueSetValue);
                textView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        alert.setView(setValueAlertView);
        alert.setPositiveButton("Set",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                TextView textView = (TextView)setValueAlertView.findViewById(R.id.ValueSetValue);
                String val = textView.getText().toString();
                Log.i(tag,"got value"+val);
                String endCmd = hostCmd + val;
                String setHost = host;
                if ( setHost.equals(getString(R.string.all)))
                    setHost = "";
                new SendCmdTask(MainActivity.this,setHost).execute(endCmd);
                if ( type.equals("volume"))
                    hostInfo.get(host).vol = Integer.parseInt(val);
                else if ( type.equals("threads"))
                    hostInfo.get(host).vol = Integer.parseInt(val);

            }
        });

        alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {

            }
        });
        setValueDialog = alert.create();
        setValueDialog.show();
    }
    private AlertDialog setStringDialog;
    private void doSetStringDialog(final String cmd, final String host, final String title) {
        if ( setStringDialog != null ) {
            setStringDialog.dismiss();
            setStringDialog = null;
        }


        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View setStringAlertView = inflater.inflate(R.layout.string_set_alert, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);


        TextView titleView = (TextView)setStringAlertView.findViewById(R.id.StringSetTitle);
        titleView.setText(title);

        final EditText valueView = (EditText)setStringAlertView.findViewById(R.id.StringTextValue);
        valueView.setText((String)getHostFromType(host,title));

        alert.setView(setStringAlertView);
        alert.setPositiveButton("Set",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                TextView textView = (TextView)setStringAlertView.findViewById(R.id.StringTextValue);
                String endCmd = cmd + textView.getText().toString();
                if ( title.equals("sound") )
                    endCmd += ".wav";
                new SendCmdTask(MainActivity.this,host).execute(endCmd);
            }
        });

        alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {

            }
        });
        setValueDialog = alert.create();
        setValueDialog.show();
    }

    public Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable hostInfoRefresh = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.i(tag, "hostInfoRefresh called on main thread");
            // Repeat this the same runnable code block again another 2 seconds
            // 'this' is referencing the Runnable object
            ArrayList<String> hosts = new ArrayList<String>();
            if ( getMaster().isEmpty() ) {
                hosts = getItemList("all");
            } else {
                String host = getStatusHost();
                if (!host.isEmpty()) {
                    hosts.add(host);
                }
            }
            new GetHostInfoTask(MainActivity.this, hosts).execute();
            handler.postDelayed(this, 10000);
        }
    };



    public void startHostInfoRefresh() {
        handler.removeCallbacks(hostInfoRefresh);
        handler.postDelayed(hostInfoRefresh,1000);
    }
    public void stopHostInfoRefresh() {
        handler.removeCallbacks(hostInfoRefresh);
    }
    @Override
    public void onClick(View v) {
        // do something when the button is clicked
        // Yes we will handle click here but which button clicked??? We don't know
        String[] cmd = new String[1];
        String host = getControlHost();
        // So we will make
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.VolumeButton:
                doSetValueDialog(getString(R.string.volumeCmd),host,"volume");
                return;

            case R.id.PhraseButton:
                doSetStringDialog(getString(R.string.phraseCmd),host,"phrase");
                return;

            case R.id.SoundButton:
                doSetStringDialog(getString(R.string.soundCmd),host,"sound");
                return;

            case R.id.ThreadsButton:
                doSetValueDialog(getString(R.string.threadsCmd),host,"threads");
                return;


            case R.id.ShutdownButton:
                Log.i(tag,"click on host shutdown");
                stopHostInfoRefresh();
                cmd[0] = "poweroff";
                break;

            case R.id.RebootButton:
                Log.i(tag,"click on reboot");
                stopHostInfoRefresh();
                cmd[0] = "reboot";
                break;

            case R.id.UpgradeButton:
                Log.i(tag,"click on host upgrade");
                stopHostInfoRefresh();
                cmd[0] = "upgrade";
                break;

            case R.id.RefreshButton:
                Log.i(tag,"click on host refresh");
                new HostRefreshTask(this).execute(getBaseContext());

            default:
                return;
        }
        if ( host.equals(getString(R.string.all)))
            host = "";
        new SendCmdTask(MainActivity.this,host).execute(cmd);
    }
    private int[] clickList = new int[] {
            R.id.VolumeButton
            ,R.id.SoundButton
            ,R.id.PhraseButton
            ,R.id.ThreadsButton
            ,R.id.RebootButton
            ,R.id.UpgradeButton
            ,R.id.ShutdownButton
            ,R.id.RefreshButton
    };
    private void setButtons() {
        for (int i = 0; i < clickList.length; ++i ) {
            Button b = (Button)findViewById(clickList[i]);
            b.setOnClickListener(this);
        }
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
        hostInfo.put("all",new SchlubHost());
        setButtons();
        CheckBox autoPlay   = ( CheckBox ) findViewById( R.id.AutoPlay );
        autoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String master = getMaster();
                if (master.isEmpty())
                    return;
                if ( ((CheckBox)v).isChecked() ) {
                    new SendCmdTask(MainActivity.this,master).execute(getString(R.string.autoCmd));
                } else {
                    new SendCmdTask(MainActivity.this,master).execute(getString(R.string.manualCmd));
                }
            }
        });
        final Spinner statusHostSpinner =  (Spinner) findViewById(R.id.StatusHostSpinner);
        statusHostSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                new GetHostInfoTask(MainActivity.this,getItemList(item)).execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        final Spinner controlHostSpinner = (Spinner) findViewById(R.id.ControlHostSpinner);
        controlHostSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                new GetHostInfoTask(MainActivity.this,getItemList(item)).execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

}
