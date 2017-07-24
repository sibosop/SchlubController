package com.sibosop.schlubcontroller;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    }
}
