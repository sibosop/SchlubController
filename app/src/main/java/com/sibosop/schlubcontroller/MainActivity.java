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
        if (!item.equals("none")) {
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
    public String getSubnet() {
        final TextView subnetView = (TextView) findViewById(R.id.Subnet);
        String subnet = subnetView.getText().toString();
        if ( subnet == getString(R.string.subnetDefault))
            subnet = "";
        return subnet;
    }
    public String getMaster() {
        final TextView masterView = (TextView) findViewById(R.id.MasterValue);
        String master = masterView.getText().toString();
        if ( master == getString(R.string.defMaster) )
            master = "";
        return master;
    }
    public String getLocal() {
        final Spinner hostSpinner = (Spinner) findViewById(R.id.HostSpinner);
        String item = (String) hostSpinner.getSelectedItem().toString();
        return item;
    }


    private AlertDialog setValueDialog;
    private void doSetValueDialog(Boolean isGlobal,final String hostCmd,final TextView tv) {
        if ( setValueDialog != null ) {
            setValueDialog.dismiss();
            setValueDialog = null;
        }
        final String host;
        if(isGlobal)
            host = "";
        else
            host = getLocal();


        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View setValueAlertView = inflater.inflate(R.layout.value_set_alert, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        SeekBar seek=(SeekBar)setValueAlertView.findViewById(R.id.ValueSetSeekbar);
        seek.setProgress(Integer.parseInt(tv.getText().toString()));
        TextView textView = (TextView)setValueAlertView.findViewById(R.id.ValueSetValue);
        textView.setText(tv.getText());
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
                new SendCmdTask(MainActivity.this,host).execute(endCmd);
                tv.setText(textView.getText());
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
    private void doSetStringDialog(Boolean isGlobal,final int cmd,
                    final TextView value,String title) {
        if ( setStringDialog != null ) {
            setStringDialog.dismiss();
            setStringDialog = null;
        }
        final String host;
        if(isGlobal)
            host = "";
        else
            host = getLocal();


        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View setStringAlertView = inflater.inflate(R.layout.string_set_alert, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);


        TextView titleView = (TextView)setStringAlertView.findViewById(R.id.StringSetTitle);
        titleView.setText(title);

        final EditText valueView = (EditText)setStringAlertView.findViewById(R.id.StringTextValue);
        valueView.setText(value.getText());


        alert.setView(setStringAlertView);
        alert.setPositiveButton("Set",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                TextView textView = (TextView)setStringAlertView.findViewById(R.id.StringTextValue);
                String endCmd = getString(cmd) + textView.getText().toString();
                if ( cmd == R.string.soundCmd )
                    endCmd += ".wav";
                new SendCmdTask(MainActivity.this,host).execute(endCmd);
                value.setText(textView.getText());
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
            String host = getLocal();
            if (!host.isEmpty()) {
                hosts.add(host);
            }
            new GetHostInfoTask(MainActivity.this,hosts).execute();
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
        String host = "";
        boolean isGlobal = false;

        // So we will make
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.VolumeValue:
                isGlobal = true;
            case R.id.LocalVolumeValue:
                Log.i(tag,"VolumeValue change request");
                doSetValueDialog(isGlobal,getString(R.string.volumeCmd),(TextView)v);
                return;

            case R.id.PhraseValue:
                isGlobal = true;
            case R.id.LocalPhraseLabel:
                doSetStringDialog(isGlobal,R.string.phraseCmd,(TextView)v,"Set Phrase");
                return;

            case R.id.SoundValue:
                isGlobal = true;
            case R.id.LocalSoundValue:
                doSetStringDialog(isGlobal,R.string.soundCmd,(TextView)v,"Set Sound");
                return;

            case R.id.AutoPlay:
                boolean isChecked = ((CheckBox) findViewById(R.id.AutoPlay)).isChecked();
                if (isChecked) {
                    cmd[0] = "auto";
                } else {
                    cmd[0] = "manual";
                }
                host = getMaster();
                Log.i(tag,"click on Auto Play"+cmd[0]);
                break;

            case R.id.shutdown:
                Log.i(tag,"click on host shutdown");
                stopHostInfoRefresh();
                cmd[0] = "poweroff";
                break;

            case R.id.reboot:
                Log.i(tag,"click on reboot");
                stopHostInfoRefresh();
                cmd[0] = "reboot";
                break;

            case R.id.upgrade:
                Log.i(tag,"click on host upgrade");
                stopHostInfoRefresh();
                cmd[0] = "upgrade";
                break;

            case R.id.LocalShutdown:
                cmd[0] = "poweroff";
                stopHostInfoRefresh();
                host = getLocal();
                Log.i(tag,"click on host shutdown"+host);
                break;

            case R.id.LocalReboot:
                cmd[0] = "reboot";
                stopHostInfoRefresh();
                host = getLocal();
                Log.i(tag,"click on reboot"+host);
                break;

            case R.id.LocalUpgrade:
                cmd[0] = "upgrade";
                stopHostInfoRefresh();
                host = getLocal();
                Log.i(tag,"click on host upgrade"+host);
                break;

            case R.id.refreshHosts:
                Log.i(tag,"click on host refresh");
                new HostRefreshTask(this).execute(getBaseContext());

            default:
                return;
        }
        new SendCmdTask(MainActivity.this,host).execute(cmd);
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


        final TextView volText = (TextView) findViewById(R.id.VolumeValue);
        volText.setOnClickListener(this);

        final TextView localVolText = (TextView) findViewById(R.id.LocalVolumeValue);
        localVolText.setOnClickListener(this);


        final TextView soundText = (TextView) findViewById(R.id.SoundValue);
        soundText.setOnClickListener(this);

        final TextView phraseText = (TextView) findViewById(R.id.PhraseValue);
        phraseText.setOnClickListener(this);

        final TextView localSoundText = (TextView) findViewById(R.id.LocalSoundValue);
        localSoundText.setOnClickListener(this);

        final TextView localPhraseText = (TextView) findViewById(R.id.LocalPhraseValue);
        localPhraseText.setOnClickListener(this);

        final Button refreshButton = (Button)findViewById(R.id.refreshHosts);
        refreshButton.setOnClickListener(this);

        final Button rebootButton = (Button)findViewById(R.id.reboot);
        rebootButton.setOnClickListener(this);

        final Button upgradeButton = (Button)findViewById(R.id.upgrade);
        upgradeButton.setOnClickListener(this);

        final Button shutdownButton = (Button)findViewById(R.id.shutdown);
        shutdownButton.setOnClickListener(this);

        final Button localRebootButton = (Button)findViewById(R.id.LocalReboot);
        localRebootButton.setOnClickListener(this);

        final Button localUpgradeButton = (Button)findViewById(R.id.LocalUpgrade);
        localUpgradeButton.setOnClickListener(this);

        final Button localShutdownButton = (Button)findViewById(R.id.LocalShutdown);
        localShutdownButton.setOnClickListener(this);

        final CheckBox autoBox = (CheckBox) findViewById(R.id.AutoPlay);
        autoBox.setOnClickListener(this);

        final Spinner hostSpinner = (Spinner) findViewById(R.id.HostSpinner);
        hostSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
