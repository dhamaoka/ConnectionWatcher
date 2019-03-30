package com.connectionwatcher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class BootCompletedReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences data;
        data = context.getSharedPreferences("settings", MODE_PRIVATE);
        boolean bootOn = data.getBoolean("WhenBootOn",false );
        if (bootOn) {
            try{
                Thread.sleep(30000); //30秒ほど待ってからサービス開始。
            }catch(InterruptedException ignored){}

            context.startForegroundService(new Intent(context, ConnectionWatchService.class));
        }
    }
}
