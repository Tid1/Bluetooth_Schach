package com.example.bluetoothschach.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bluetoothschach.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends InitActivity {
    private String hostColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Main activity started");
        handleButtonListeners();
        //Button button = (Button)findViewById(R.id.startRanActivity);
        //Button button = (Button)findViewById(R.id.startRanActivity);
        //button.setOnClickListener(v -> startRandomButtonActivity());
    }

    private void startHostActivity(){
        this.finish();
        startActivity(new Intent(this, HostGameActivity.class));
    }

    private void startJoinActivity(){
        this.finish();
        startActivity(new Intent(this, JoinGameActivity.class));
    }

    private void startLoadGameActivity(){
        this.finish();
        startActivity(new Intent(this, LoadGameActivity.class));
    }

    private void startLocalActivity(){
        this.finish();
        startActivity(new Intent(this, LocalBoardActivity.class));
    }


    private void handleButtonListeners(){
        Button hostButton = (Button)findViewById(R.id.hostButton);
        hostButton.setOnClickListener(v-> startHostActivity());
        Button joinButton = (Button)findViewById(R.id.joinButton);
        joinButton.setOnClickListener(v -> startJoinActivity());
        Button loadButton = (Button)findViewById(R.id.loadGame);
        loadButton.setOnClickListener(v -> startLoadGameActivity());
        Button localGame = (Button)findViewById(R.id.startGameButton);
        localGame.setOnClickListener(v -> startLocalActivity());
    }
}