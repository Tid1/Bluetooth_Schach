package com.example.bluetoothschach.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bluetoothschach.BluetoothSchach;
import com.example.bluetoothschach.Network.HostSetupMessageReceiver;
import com.example.bluetoothschach.R;

import net.sharksystem.asap.ASAPException;

import java.util.Random;

import Model.Spiellogik.Color;

public class HostGameActivity extends InitActivity {
    private HostSetupMessageReceiver setUpReceiver;
    //TODO Variable noch an das Enum aus der Spiellogik anpassen
    private Color chosenColor;
    private final String DEFAULT_URI = BluetoothSchach.DEFAULT_URI;
    private String dynamicIdentifier = "";
    private String dynamicUri = DEFAULT_URI + dynamicIdentifier;
    private final int IDENTIFIER_LENGTH = 4;
    private final int DIGIT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setupNetwork();
        setContentView(R.layout.host_game);
        createDynamicIdentifier();
        Button returnButton = (Button)findViewById(R.id.returnFromHost);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(v -> sendMessage());
        returnButton.setOnClickListener(v -> returnToMain());
        TextView dynamicIDView = (TextView)findViewById(R.id.dynamicGameID);
        dynamicIDView.setText(dynamicIdentifier);
        inflateColorLayout();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        BluetoothSchach.deleteMessageReceivedListener(setUpReceiver);
    }

    private void createDynamicIdentifier(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < IDENTIFIER_LENGTH; i++){
            sb.append(random.nextInt(DIGIT));
        }
        dynamicIdentifier = sb.toString();
        dynamicUri = DEFAULT_URI + dynamicIdentifier;
    }

    private void sendMessage(){
        byte message = 10;
        byte[] messageArray = {message};
        Log.d("DummyMessage","Dummy message about to be send");
        try {
            sendASAPMessage(BluetoothSchach.ASAP_APPNAME, BluetoothSchach.DEFAULT_URI, messageArray, true);
        } catch (ASAPException e){

        }
        Log.d("DummyMessage", "Dummy message sent");
    }
    public String getDynamicIdentifier(){
        return dynamicUri;
    }

    public void hostStartGameActivity(){
        this.finish();
        Intent startOnlineGameIntent = new Intent(this, BluetoothGameActivity.class);
        startOnlineGameIntent.putExtra("Color", chosenColor);
        startOnlineGameIntent.putExtra("URI", dynamicUri);
        startActivity(startOnlineGameIntent);
    }

    private void setupNetwork(){
        super.startBluetooth();
        super.startBluetoothDiscoverable();
        super.startBluetoothDiscovery();
    }

    private void inflateColorLayout(){
        LayoutInflater inflater = getLayoutInflater();
        View showColors = inflater.inflate(R.layout.host_color_pick, null);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(showColors);
        dialog.setCancelable(false);

        handleHostPickColor(showColors, dialog);
    }

    private void handleHostPickColor(View view, Dialog dialog){
        Button colorWhiteButton = (Button)view.findViewById(R.id.colorWhiteButton);
        Button colorBlackButton = (Button)view.findViewById(R.id.colorBlackButton);

        colorBlackButton.setOnClickListener(v -> {
            this.chosenColor = Color.Black;
            if (setUpReceiver == null){
                this.setUpReceiver = new HostSetupMessageReceiver(chosenColor);
            }
            dialog.cancel();
        });

        colorWhiteButton.setOnClickListener(v -> {
            this.chosenColor = Color.White;
            if (setUpReceiver == null){
                this.setUpReceiver = new HostSetupMessageReceiver(chosenColor);
            }
            dialog.cancel();
        });

        dialog.show();

    }

    private void returnToMain(){
        this.finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
