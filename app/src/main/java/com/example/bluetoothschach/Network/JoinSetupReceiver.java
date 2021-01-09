package com.example.bluetoothschach.Network;

import android.app.Activity;
import android.util.Log;

import com.example.bluetoothschach.BluetoothSchach;
import com.example.bluetoothschach.Activities.JoinGameActivity;

import net.sharksystem.asap.ASAPMessages;
import net.sharksystem.asap.apps.ASAPMessageReceivedListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;

import Model.Spiellogik.Color;

public class JoinSetupReceiver implements ASAPMessageReceivedListener {
    private final String DYNAMIC_URI;
    private final String APP_NAME = BluetoothSchach.ASAP_APPNAME;
    private final int BLACK = 1;
    private final int WHITE = 2;
    private Color assignedColor;

    public JoinSetupReceiver(String dynamicUri){
        BluetoothSchach.registerMessageReceivedListener(this);
        this.DYNAMIC_URI = dynamicUri;
    }

    @Override
    public void asapMessagesReceived(ASAPMessages asapMessages) throws IOException {
        Iterator<byte[]> iterator;
        try {
            iterator = asapMessages.getMessages();
        } catch (IOException e) {
            Log.d("JoinSetupError", "Something went wrong trying to setup the game");
            return;
        }

        byte[] message = iterator.next();
        String receivedUri = (String) asapMessages.getURI();
        String receivedAppName = (String)asapMessages.getFormat();

        if (receivedUri.equals(DYNAMIC_URI) && receivedAppName.equals(APP_NAME)){
            Activity activity = BluetoothSchach.getInstance().getActivity();
            if (activity instanceof JoinGameActivity){
                ByteArrayInputStream bais = new ByteArrayInputStream(message);
                DataInputStream dais = new DataInputStream(bais);

                int receivedColorInt = dais.readInt();
                if (receivedColorInt == BLACK){
                    assignedColor = Color.White;
                } else {
                    assignedColor = Color.Black;
                }

                ((JoinGameActivity) activity).joinStartGameActivity(assignedColor, DYNAMIC_URI);
            } else {
                Log.d("JoinErrorStartingGame", "Received URI isn't expected URI");
            }
        }
    }
}
