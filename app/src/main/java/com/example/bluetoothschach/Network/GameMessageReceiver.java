package com.example.bluetoothschach.Network;

import android.app.Activity;
import android.util.Log;

import com.example.bluetoothschach.Activities.BluetoothGameActivity;
import com.example.bluetoothschach.BluetoothSchach;

import net.sharksystem.asap.ASAPMessages;
import net.sharksystem.asap.apps.ASAPMessageReceivedListener;


import java.io.IOException;
import java.util.Iterator;

public class GameMessageReceiver implements ASAPMessageReceivedListener {
    private String uri;
    public GameMessageReceiver(String uri){
        //Hier Protokollmaschine einbinden
        this.uri = uri;
        BluetoothSchach.registerMessageReceivedListener(this);
    }

    @Override
    public void asapMessagesReceived(ASAPMessages asapMessages) {
        Log.d("ReceivedGameMessage", "Received a Game message");
        Iterator<byte[]> iterator;
        try {
            iterator = asapMessages.getMessages();
        } catch (IOException e) {
            Log.d("ErrorReceivingMessage", "Something went wrong while receiving Messages");
            return;
        }

        byte[] message = null;
        while (iterator.hasNext()){
            message = iterator.next();
            System.out.println("Game Receiver received message length: " + message.length);
            System.out.println("Does iterator have next? " + iterator.hasNext());
        }

        String receivedUri = asapMessages.getURI().toString();
        String receivedAppName = asapMessages.getFormat().toString();

        if (receivedUri.equals(uri) && receivedAppName.equals(BluetoothSchach.ASAP_APPNAME)){
            Activity activity = BluetoothSchach.getInstance().getActivity();

            if (activity instanceof BluetoothGameActivity){
                BluetoothGameActivity bActivity = (BluetoothGameActivity)activity;
                bActivity.receiveTurn(message);
            }
        }
        //Hier Protokollmaschine einbinden mit updateBais
    }
}
