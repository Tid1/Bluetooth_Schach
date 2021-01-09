package com.example.bluetoothschach.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bluetoothschach.BluetoothSchach;
import com.example.bluetoothschach.Network.JoinSetupReceiver;
import com.example.bluetoothschach.R;

import net.sharksystem.asap.ASAPException;

import Model.Spiellogik.Color;

public class JoinGameActivity extends InitActivity{
    private final int ID_LENGTH = 4;
    private JoinSetupReceiver joinSetupReceiver;
    private final byte MESSAGE = 100;
    private final String ERROR_MESSAGE = "Number must only contain 4 digits and can't be negative!";
    private final String DEFAULT_URI = BluetoothSchach.DEFAULT_URI;
    private String dynamicURIIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_game);
        handleButtonListener();
        setupNetwork();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        BluetoothSchach.deleteMessageReceivedListener(joinSetupReceiver);
    }

    private void joinOnClick() throws ASAPException {
        EditText editText = (EditText)findViewById(R.id.joinDynamicIDEnter);
        TextView errorText = (TextView) findViewById(R.id.errorText);
        String enteredID = editText.getText().toString();

        if (enteredID.length() != ID_LENGTH){
            errorText.setText(ERROR_MESSAGE);
            return;
        }

        byte[] message = {MESSAGE};
        joinSetupReceiver = new JoinSetupReceiver(DEFAULT_URI + enteredID);
        Log.d("PresendMessage", "Message about to be send");
        sendASAPMessage(BluetoothSchach.ASAP_APPNAME, DEFAULT_URI + enteredID,message, true);
        Log.d("PostSendMessage", "Message sent");
    }

    public void joinStartGameActivity(Color color, String uri){
        this.finish();
        Intent startOnlineGameIntent = new Intent(this, BluetoothGameActivity.class);
        startOnlineGameIntent.putExtra("Color", color);
        startOnlineGameIntent.putExtra("URI", uri);
        startActivity(startOnlineGameIntent);
    }

    private void setupNetwork(){
        super.startBluetooth();
        super.startBluetoothDiscoverable();
        super.startBluetoothDiscovery();
    }

    private void returnToMain(){
        this.finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void handleButtonListener(){
        Button returnButton = (Button)findViewById(R.id.returnFromJoin);
        returnButton.setOnClickListener(v -> returnToMain());
        Button joinButton  = (Button)findViewById(R.id.joinDynamicIDButton);
        joinButton.setOnClickListener(v -> {
            try {
                joinOnClick();
            } catch (ASAPException e) {
                Log.d("JoinError", e.getMessage() + "");
            }
        });
    }
}
