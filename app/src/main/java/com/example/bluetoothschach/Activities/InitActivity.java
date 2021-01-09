package com.example.bluetoothschach.Activities;

import android.os.Bundle;

import com.example.bluetoothschach.BluetoothSchach;

import net.sharksystem.asap.android.apps.ASAPActivity;

public class InitActivity extends ASAPActivity {
    public InitActivity() {
        super(BluetoothSchach.getInstance());
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }*/
}
