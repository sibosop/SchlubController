package com.sibosop.schlubcontroller;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements View.OnClickListener {
    public SoundList soundList = new SoundList();
    public CollectionList collectionList = new CollectionList();
    public String currentCollection = null;
    private String tag;
    public SoundList.ListItem currentItem = null;
    public HostInfo hostRefreshInfo = null;
    HashMap<String,SchlubHost> hostInfo = new HashMap<String,SchlubHost>();
    Object getHostFromType(String key, int type) {
        switch (type) {
            case R.string.volumeCmd:
                return hostInfo.get(key).vol;

            case R.string.threadsCmd:
                return hostInfo.get(key).threads;

            case R.string.soundCmd:
                return hostInfo.get(key).sound;

            case R.string.phraseCmd:
                return hostInfo.get(key).phrase;
        }
        return 0;
    }
    void setHostFromType(String key, int type, Object val) {
        switch ( type ) {
            case R.string.volumeCmd:
                hostInfo.get(key).vol = (Integer)val;
                break;
            case R.string.threadsCmd:
                hostInfo.get(key).threads = (Integer)val;
                break;
            case R.string.soundCmd:
                hostInfo.get(key).sound = (String)val;
                break;
            case R.string.phraseCmd:
                hostInfo.get(key).phrase = (String)val;
                break;
        }
    }

    MainActivity() {
        super();
        tag = this.getClass().getSimpleName();
    }
    public void uiLog(String line)
    {
        ScrollView sv = (ScrollView)findViewById(R.id.LogScrollView);
        TextView tv = (TextView)findViewById(R.id.UiLogText);
        sv.scrollTo(0, sv.getBottom());
        tv.append("\n"+line);
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
    private void doSetValueDialog(final int  hostCmd,final String host) {
        if ( setValueDialog != null ) {
            setValueDialog.dismiss();
            setValueDialog = null;
        }


        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View setValueAlertView = inflater.inflate(R.layout.value_set_alert, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        Integer initValue = 0;
        Integer max = 100;
        if (  hostCmd == R.string.volumeCmd) {
            initValue = hostInfo.get(host).vol;
            max = 100;
        }
        else if ( hostCmd==R.string.threadsCmd) {
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
                SchlubCmd endCmd = new SchlubCmd(getString(hostCmd));
                endCmd.putArg(val);
                String setHost = host;
                if ( setHost.equals(getString(R.string.all)))
                    setHost = "";

                new SendCmdTask(MainActivity.this,setHost).execute(endCmd.getJson());
                if ( hostCmd == R.string.volumeCmd)
                    hostInfo.get(host).vol = Integer.parseInt(val);
                else if ( hostCmd == R.string.threadsCmd)
                    hostInfo.get(host).threads = Integer.parseInt(val);

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
    private void doSetStringDialog(final int cmd, final String host) {
        if ( setStringDialog != null ) {
            setStringDialog.dismiss();
            setStringDialog = null;
        }


        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View setStringAlertView = inflater.inflate(R.layout.string_set_alert, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);


        TextView titleView = (TextView)setStringAlertView.findViewById(R.id.StringSetTitle);
        titleView.setText(getString(cmd));

        final EditText valueView = (EditText)setStringAlertView.findViewById(R.id.StringTextValue);

        valueView.setText((String)getHostFromType(host,cmd));
        valueView.setSelectAllOnFocus(true);
        valueView.requestFocus();

        alert.setView(setStringAlertView);
        alert.setPositiveButton("Set",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                TextView textView = (TextView)setStringAlertView.findViewById(R.id.StringTextValue);
                String tvs = textView.getText().toString();
                SchlubCmd endCmd = new SchlubCmd(getString(cmd));
                if ( cmd ==R.string.soundCmd )
                    tvs += ".wav";
                endCmd.putArg(tvs);
                new SendCmdTask(MainActivity.this,host).execute(endCmd.getJson());
            }
        });

        alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {

            }
        });
        setStringDialog = alert.create();
        setStringDialog.show();
    }

    private AlertDialog soundChoiceDialog;
    private void doSoundChoiceDialog(final String host) {
        if ( soundChoiceDialog != null ) {
            soundChoiceDialog.dismiss();
            soundChoiceDialog = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        final View view = inflater.inflate(R.layout.sound_list_dialog, null);
        final GridView gl = (GridView)view.findViewById(R.id.SoundList);
        final TextView soundChoice = (TextView)view.findViewById(R.id.SoundChoice);
        soundChoice.setText("");
        final ArrayAdapter<SoundList.ListItem> adapter = new ArrayAdapter<SoundList.ListItem>(this
                ,android.R.layout.simple_list_item_1
                ,soundList.sounds) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView)super.getView(position, convertView, parent);
                String t = view.getText().toString();
                String[] v = t.split(":");
                Log.i(tag,"got text "+v[0]+" enabled:"+v[1]);
                if ( v[1].equals("1"))
                    view.setBackgroundColor(getResources().getColor(R.color.enabledColor));
                else
                    view.setBackgroundColor(getResources().getColor(R.color.disabledColor));


                view.setText(v[0]);
                return view;
            }
        };
        gl.setAdapter(adapter);

        gl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                soundChoice.setText(((TextView) v).getText());
                String choice = soundChoice.getText().toString();
                if ( !choice.isEmpty()) {
                    SchlubCmd endCmd = new SchlubCmd(getString(R.string.soundCmd));
                    endCmd.putArg(choice);
                    currentItem = adapter.getItem(position);
                    new SendCmdTask(MainActivity.this, host).execute(endCmd.getJson());
                }
            }
        });
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.enable, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    //Do nothing here because we override this button later to change the close behaviour.
                    //However, we still need this because on older versions of Android unless we
                    //pass a handler the button doesn't get instantiated
                        /*
                    String choice = soundChoice.getText().toString();
                    if (!choice.isEmpty()) {
                        Toast.makeText(MainActivity.this, "enable:"+choice, Toast.LENGTH_SHORT).show();
                        SchlubCmd endCmd = new SchlubCmd(getString(R.string.soundEnableCmd));
                        endCmd.putArg(choice);
                        endCmd.putArg("True");
                        new SendCmdTask(MainActivity.this, getMaster()).execute(endCmd.getJson());
                    } */
                    }
                })
                .setNegativeButton(R.string.disable,new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    /*
                    String choice = soundChoice.getText().toString();
                    if (!choice.isEmpty()) {
                        Toast.makeText(MainActivity.this, "enable:"+choice, Toast.LENGTH_SHORT).show();
                        SchlubCmd endCmd = new SchlubCmd(getString(R.string.soundEnableCmd));
                        endCmd.putArg(choice);
                        endCmd.putArg("False");
                        new SendCmdTask(MainActivity.this, getMaster()).execute(endCmd.getJson());
                    }
                    */
                    }
                })
                .setNeutralButton(R.string.done,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                });

        soundChoiceDialog = builder.create();
        soundChoiceDialog.show();

        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        soundChoiceDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String choice = soundChoice.getText().toString();
                if (!choice.isEmpty()) {
                    Toast.makeText(MainActivity.this, "enable:"+choice, Toast.LENGTH_SHORT).show();
                    SchlubCmd endCmd = new SchlubCmd(getString(R.string.soundEnableCmd));
                    endCmd.putArg(choice);
                    endCmd.putArg("True");
                    if ( currentItem != null )
                        currentItem.enabled = "1";
                    new SendCmdTask(MainActivity.this, getMaster()).execute(endCmd.getJson());
                    adapter.notifyDataSetChanged();
                    gl.invalidate();
                }
            }
        });
        soundChoiceDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                String choice = soundChoice.getText().toString();
                if (!choice.isEmpty()) {
                    Toast.makeText(MainActivity.this, "disable:"+choice, Toast.LENGTH_SHORT).show();
                    SchlubCmd endCmd = new SchlubCmd(getString(R.string.soundEnableCmd));
                    endCmd.putArg(choice);
                    endCmd.putArg("False");
                    if ( currentItem != null )
                        currentItem.enabled = "0";

                    new SendCmdTask(MainActivity.this, getMaster()).execute(endCmd.getJson());
                    adapter.notifyDataSetChanged();
                    gl.invalidate();
                }
            }
        });

    }
    private AlertDialog collectionDialog;
    private void doCollectionDialog(final String host) {
        if ( collectionDialog != null ) {
            collectionDialog.dismiss();
            collectionDialog = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        final View view = inflater.inflate(R.layout.collection_dialog, null);
        final GridView gl = (GridView)view.findViewById(R.id.CollectionList);
        final TextView collectionChoice = (TextView)view.findViewById(R.id.CollectionChoice);
        collectionChoice.setText(currentCollection);


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this
                ,android.R.layout.simple_list_item_1
                ,collectionList.collections ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView)super.getView(position, convertView, parent);
                String t = view.getText().toString();
                Log.i(tag,"got text "+t);
                t = t.replace(".csv","");
                view.setBackgroundColor(getResources().getColor(R.color.enabledColor));
                view.setText(t);
                return view;
            }
        };
        gl.setAdapter(adapter);

        gl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                collectionChoice.setText(((TextView) v).getText());
                String choice = collectionChoice.getText().toString();
                if ( !choice.isEmpty()) {
                    String setVal = choice+".csv";
                    SchlubCmd endCmd = new SchlubCmd(getString(R.string.collectionCmd));
                    endCmd.putArg(setVal);
                    currentCollection = adapter.getItem(position);
                    final TextView collectionValue = (TextView)findViewById(R.id.CollectionValue);
                    collectionValue.setText(choice);
                    new SendCmdTask(MainActivity.this, host).execute(endCmd.getJson());
                }
            }
        });
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.i(tag,"dismiss collection dialog");
                        collectionDialog.dismiss();
                        collectionDialog = null;

                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                        /*
                    String choice = soundChoice.getText().toString();
                    if (!choice.isEmpty()) {
                        Toast.makeText(MainActivity.this, "enable:"+choice, Toast.LENGTH_SHORT).show();
                        SchlubCmd endCmd = new SchlubCmd(getString(R.string.soundEnableCmd));
                        endCmd.putArg(choice);
                        endCmd.putArg("True");
                        new SendCmdTask(MainActivity.this, getMaster()).execute(endCmd.getJson());
                    } */
                    }
                });


        collectionDialog = builder.create();
        collectionDialog.show();
    }

    public Handler handler = null;
    // Define the code block to be executed
    private Runnable hostInfoRefresh = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.i(tag, "hostInfoRefresh called on main thread");
            // Repeat this the same runnable code block again another 2 seconds
            // 'this' is referencing the Runnable object
            ArrayList<String> hosts = new ArrayList<String>();
            String master = getMaster();
            if ( master.isEmpty() ) {
                hosts = getItemList("all");
            } else {
                String host = getStatusHost();
                if (!host.isEmpty()) {
                    hosts.add(host);
                }
                if ( host != master )
                    hosts.add(master);
            }
            new GetHostInfoTask(MainActivity.this, hosts).execute();
            handler.postDelayed(this, 10000);
        }
    };

    public void stopHostInfoRefresh() {
        if ( handler != null ) {
            handler.removeCallbacks(hostInfoRefresh);
            handler = null;
        }
    }
    public void startHostInfoRefresh() {
        stopHostInfoRefresh();
        handler = new Handler();
        handler.postDelayed(hostInfoRefresh,1000);
    }

    @Override
    public void onClick(View v) {
        // do something when the button is clicked
        // Yes we will handle click here but which button clicked??? We don't know

        SchlubCmd schlubCmd = new SchlubCmd();
        String host = getControlHost();
        // So we will make
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.VolumeButton:
                doSetValueDialog(R.string.volumeCmd,host);
                return;

            case R.id.PhraseButton:
                doSetStringDialog(R.string.phraseCmd,host);
                return;

            case R.id.SoundButton:
                try {
                   // if (soundList.isEmpty())
                    new SoundListTask(this).execute().get();
                    doSoundChoiceDialog(host);
                } catch (Exception e){
                    Log.i("get sound list",e.toString());
                }
                return;


            case R.id.CollectionButton:
                try {
                    String master = getMaster();
                    if (master.isEmpty())
                        return;
                    // if (soundList.isEmpty())
                    new CollectionListTask(this).execute().get();
                    doCollectionDialog(master);
                } catch (Exception e){
                    Log.i("get collection list",e.toString());
                }
                return;

            case R.id.ThreadsButton:
                doSetValueDialog(R.string.threadsCmd,host);
                return;


            case R.id.ShutdownButton:
                Log.i(tag,"click on host shutdown");
                stopHostInfoRefresh();
                schlubCmd = new SchlubCmd("Poweroff");
                break;

            case R.id.RebootButton:
                Log.i(tag,"click on reboot");
                stopHostInfoRefresh();
                schlubCmd = new SchlubCmd("Reboot");
                break;

            case R.id.UpgradeButton:
                Log.i(tag,"click on host upgrade");
                stopHostInfoRefresh();
                schlubCmd = new SchlubCmd("Upgrade");
                break;

            case R.id.RefreshButton:
                Log.i(tag,"click on host refresh");
                new HostRefreshTask(this).execute(getBaseContext());

            default:
                return;
        }
        if ( host.equals(getString(R.string.all)))
            host = "";


        new SendCmdTask(MainActivity.this,host).execute(schlubCmd.getJson());
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
            ,R.id.CollectionButton
    };
    private void setButtons() {
        for (int i = 0; i < clickList.length; ++i ) {
            Button b = (Button)findViewById(clickList[i]);
            b.setOnClickListener(this);
        }
    }
    public void updateHostRefreshInfo(HostInfo update)
    {
        final TextView subnetView = (TextView) findViewById(R.id.SubnetValue);
        subnetView.setText(update.subnet);
       hostRefreshInfo = update.fuckingDeepCopy();

        Spinner statusHostSpinner = (Spinner) findViewById(R.id.StatusHostSpinner);
        ArrayAdapter<String> statusSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, hostRefreshInfo.ids);
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusHostSpinner.setAdapter(statusSpinnerAdapter);

        update.ids.add("all");
        Spinner controlHostSpinner = (Spinner) findViewById(R.id.ControlHostSpinner);
        ArrayAdapter<String> controlSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, update.ids);
        controlSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controlHostSpinner.setAdapter(controlSpinnerAdapter);
        controlHostSpinner.setSelection(update.ids.size() - 1);
    }
    @Override
    protected  void onSaveInstanceState (Bundle outState)
    {
        Log.i(tag,"saving instance");
        Gson gson = new Gson();
        String jsonHostRefreshInfo = gson.toJson(hostRefreshInfo,HostInfo.class);
        Log.i(tag,"saving refresh host info");
        outState.putString("hostRefreshInfo",jsonHostRefreshInfo);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    SchlubCmd cmd = new SchlubCmd(getString(R.string.autoCmd));
                    new SendCmdTask(MainActivity.this,master).execute(cmd.getJson());
                } else {
                    SchlubCmd cmd = new SchlubCmd(getString(R.string.manualCmd));
                    new SendCmdTask(MainActivity.this,master).execute(cmd.getJson());
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

        if ( savedInstanceState != null ) {
            Log.i(tag, "restoring instance without running info refresh task");
            Gson gson = new Gson();
            String save = savedInstanceState.getString("hostRefreshInfo");
            HostInfo tmp = gson.fromJson(save, HostInfo.class);
            updateHostRefreshInfo(tmp);
            startHostInfoRefresh();
        } else {
            new HostRefreshTask(this).execute(getBaseContext());
        }
    }



}
