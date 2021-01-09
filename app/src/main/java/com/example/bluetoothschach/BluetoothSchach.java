package com.example.bluetoothschach;

import android.app.Activity;
import android.util.Log;

import net.sharksystem.asap.ASAP;
import net.sharksystem.asap.android.apps.ASAPApplication;
import net.sharksystem.asap.apps.ASAPMessageReceivedListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import Model.Spiellogik.BoardImpl;

public class BluetoothSchach extends ASAPApplication {
    public static final String ASAP_APPNAME = "applicationX-BluetoothSchach";
    public static final String DEFAULT_URI = "bluetoothschach://";
    private CharSequence id;
    private static BluetoothSchach instance = null;

    public static BluetoothSchach initializeASAPExampleApplication(Activity initialActivity) {
        if(BluetoothSchach.instance == null) {
            Collection<CharSequence> formats = new ArrayList<>();
            formats.add(ASAP_APPNAME);

            // create object - set up application side
            //TODO ihn fragen ob getASAPApplication() als bug zählt
            //BluetoothSchach.instance = new BluetoothSchach(formats, initialActivity);

            BluetoothSchach.instance = new BluetoothSchach(formats, initialActivity);
            // step 2 - launch ASAPService
            BluetoothSchach.instance.startASAPApplication();
        } // else - already initialized - nothing happens.

        return BluetoothSchach.instance;
    }

    public static BluetoothSchach getInstance(){
        return instance;
    }

    public static void registerMessageReceivedListener(ASAPMessageReceivedListener listener) throws NullPointerException{
        if (listener != null && instance != null){
            instance.addASAPMessageReceivedListener(ASAP_APPNAME,listener);
            Log.d("RegisterListener", "Listener registered");
        }
        else{
            throw new NullPointerException("Neither Listener nor instance can be null");
        }
    }

    public static void deleteMessageReceivedListener(ASAPMessageReceivedListener listener){
        if (listener != null && instance != null){
            instance.removeASAPMessageReceivedListener(ASAP_APPNAME, listener);
            Log.d("DeleteListener", "Listener deleted");
        }
        else{
            throw new NullPointerException("Neither Listener nor instance can be null");
        }
    }


    private BluetoothSchach(Collection<CharSequence> formats, Activity initialActivity) {
        super(formats, initialActivity);
        this.id = ASAP.createUniqueID();
    }

    public CharSequence getOwnerID(){
        return this.id;
    }

}