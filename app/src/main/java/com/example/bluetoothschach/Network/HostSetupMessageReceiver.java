package com.example.bluetoothschach.Network;

import android.app.Activity;
import android.util.Log;

import com.example.bluetoothschach.BluetoothSchach;
import com.example.bluetoothschach.Activities.HostGameActivity;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPMessages;
import net.sharksystem.asap.apps.ASAPMessageReceivedListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

import Model.Spiellogik.Color;

public class HostSetupMessageReceiver implements ASAPMessageReceivedListener {
    private byte MESSAGE = 100;
    private final int BLACK = 1;
    private final int WHITE = 2;
    private Color chosenColor;
    public HostSetupMessageReceiver(Color chosenColor){
        BluetoothSchach.registerMessageReceivedListener(this);
        this.chosenColor = chosenColor;
    }

    @Override
    public void asapMessagesReceived(ASAPMessages asapMessages) throws IOException {
        Log.d("ListenerTriggered", "Listener got triggered");
        Iterator<byte[]> iterator;
        try {
            iterator = asapMessages.getMessages();
        } catch (IOException e) {
            Log.d("HostSetupError", "Something went wrong trying to setup the game");
            return;
        }

        byte[] message = iterator.next();
        String receivedIdentifier = asapMessages.getURI().toString();

        Activity activity = BluetoothSchach.getInstance().getActivity();
        if (activity instanceof HostGameActivity){
            HostGameActivity hostGameActivity = (HostGameActivity)activity;
            Log.d("CurrentActivity", "Current Activity: " + activity.toString() + "Current URI: "+ receivedIdentifier + "Expected URI: " +
                    hostGameActivity.getDynamicIdentifier());

            String appName = BluetoothSchach.ASAP_APPNAME;
            String receivedAppName = asapMessages.getFormat().toString();
            if (receivedIdentifier.equals(((HostGameActivity) activity).getDynamicIdentifier()) && appName.equals(receivedAppName)){
                hostGameActivity.hostStartGameActivity();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream daos = new DataOutputStream(baos);
                if (chosenColor == Color.Black){
                    daos.writeInt(BLACK);
                } else {
                    daos.writeInt(WHITE);
                }
                try {
                    hostGameActivity.sendASAPMessage(BluetoothSchach.ASAP_APPNAME, receivedIdentifier, baos.toByteArray(), true);
                } catch (ASAPException e) {
                    Log.e("FailSendingFromReceiver", "Something went wrong sending the Message after Receiving a GameRequest");
                }
                Log.d("MessageReceived", "Message Received");
            }else {
                Log.d("Wrong URI", "Received URI isnt expected URI");
            }
        }
    }
}
