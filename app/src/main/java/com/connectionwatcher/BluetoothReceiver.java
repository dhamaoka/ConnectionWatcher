package com.connectionwatcher;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {
    private boolean IsBluetoothConnected = true;
    private final static String TAG = "BTReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            IsBluetoothConnected = false;
        } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            String displayChar = context.getResources().getString(R.string.Connected);
            Toast toast = Toast.makeText(context, displayChar, Toast.LENGTH_SHORT);
            toast.show();
            IsBluetoothConnected = true;
        }
        Log.d(TAG, String.valueOf(IsBluetoothConnected));
    }

    public boolean IsConnected() {
        return IsBluetoothConnected;
    }
}