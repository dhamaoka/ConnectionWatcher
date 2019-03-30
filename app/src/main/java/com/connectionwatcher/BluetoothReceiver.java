package com.connectionwatcher;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static android.bluetooth.BluetoothClass.Device.Major.PHONE;

public class BluetoothReceiver extends BroadcastReceiver {
    private boolean IsBluetoothConnected = false;
    private final static String TAG = "BTReceiver";
    private BluetoothDevice foundedDevice;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        foundedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            IsBluetoothConnected = false;
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Disconnected");
            }
        } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Founded = " + foundedDevice.getName());
            }
            Log.d(TAG, String.valueOf(foundedDevice.getBluetoothClass().getMajorDeviceClass()));
            if (foundedDevice.getBluetoothClass().getMajorDeviceClass()==PHONE) {
                String displayChar = context.getResources().getString(R.string.Connected);
                Toast toast = Toast.makeText(context, displayChar, Toast.LENGTH_LONG);
                toast.show();
            }
            IsBluetoothConnected = true;
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Connected");
            }
        }
    }

    public BluetoothDevice FoundedDevice() {
        return foundedDevice;
    }
    public boolean IsConnected() {
        return IsBluetoothConnected;
    }
}