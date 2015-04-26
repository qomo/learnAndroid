package com.example.liaozongmeng.sensorslist;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SensorsList extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_list);

        // wire up the fragments so selector
        // can call display

        SensorDisplayFragment sensorDisplay = (SensorDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.frag_sensor_view);
        SensorSelectorFragment sensorSelect = (SensorSelectorFragment) getSupportFragmentManager().findFragmentById(R.id.frag_sensor_select);

        sensorSelect.setSensorDisplay(sensorDisplay);

    }
}
