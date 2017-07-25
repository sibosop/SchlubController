package com.sibosop.schlubcontroller;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runOnUiThread(new Runnable() {
            public void run() {
                setContentView(R.layout.activity_main);
            }
        });
        new HostRefreshTask(this).execute(getBaseContext());
        final View button = findViewById(R.id.refreshHosts);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new HostRefreshTask(MainActivity.this).execute(getBaseContext());
            }
        });

        Spinner hostSpinner = (Spinner) findViewById(R.id.HostSpinner);
        hostSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String item = (String)  parent.getItemAtPosition(position);
                final TextView subnetView = (TextView)MainActivity.this.findViewById(R.id.Subnet);
                String subnet = subnetView.getText().toString();
                String[] params = new String[] {subnet,item};
                new GetHostInfoTask(MainActivity.this).execute(params);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }
}
