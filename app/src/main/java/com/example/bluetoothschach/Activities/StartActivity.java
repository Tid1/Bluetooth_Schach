package com.example.bluetoothschach.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetoothschach.BluetoothSchach;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        BluetoothSchach.initializeASAPExampleApplication(this);
        this.finish();
        this.startActivity(new Intent(this, MainActivity.class));
    }
}
