package com.connectionwatcher;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionWatchService extends IntentService {
    private final static String TAG = "ConnectionWatchService";
    private Timer timer = new Timer();
    int INTERVAL_PERIOD = 20000;
    final BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();

    public ConnectionWatchService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        String displayChar = getResources().getString(R.string.Start_Watching);
        Toast toast = Toast.makeText(getApplicationContext(), displayChar, Toast.LENGTH_SHORT);
        toast.show();
        //通知のメッセージだわな。
        String message = getResources().getString(R.string.SwitchOn);
        showNotification(message);

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, bluetoothFilter);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Working...");

                if (bluetoothAdapter != null) {
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        /*
                        final IntentFilter bluetoothFilter = new IntentFilter();
                        bluetoothFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                        bluetoothFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                        registerReceiver(bluetoothReceiver, bluetoothFilter);
                        */

                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                                Log.d(TAG, device.getName());
                                if (!bluetoothReceiver.IsBluetoothConnected) {
                                    //通知のメッセージだわな。
                                    String message = getResources().getString(R.string.ConnectionLost);
                                    showNotification(message);
                                }
                                break;
                            }
                        }
                    }
                } else {
                    String displayChar = "No Bluetooth device";
                    Toast toast = Toast.makeText(getApplicationContext(), displayChar, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, 0, INTERVAL_PERIOD);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    public class BluetoothReceiver extends BroadcastReceiver {
        protected boolean IsBluetoothConnected = true;

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                IsBluetoothConnected = false;
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                String displayChar = getResources().getString(R.string.Connected);
                Toast toast = Toast.makeText(getApplicationContext(), displayChar, Toast.LENGTH_SHORT);
                toast.show();
                IsBluetoothConnected = true;
            }
            Log.d(TAG, String.valueOf(IsBluetoothConnected));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        unregisterReceiver(bluetoothReceiver);
        Log.d(TAG, "onDestroy");
    }

    /*
     * MainActivitiy.xmlから移動。
     */
    public void showNotification(String message) {
        // 適当にサンプルからだけど、ホントに好きな数字でええもんなんかね。
        // アプリ内でのユニーク？複数通知使わんかったらホンマになんでも良し？
        int NOTIFICATION_ID = 1020;

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        //任意のIDらしい。
        String CHANNEL_ID = "BT Connection Watcher";
        //任意の名前らしい。
        CharSequence name = getResources().getString(R.string.NotifyDescription);
        //必須じゃないらしい。NotifyDescription
        String Description = getResources().getString(R.string.NotifyDescription);
        //通知のタイトルだわな。
        String title = getResources().getString(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //デフォルトの重要度。Highっぽいぜ。
            int importance = NotificationManager.IMPORTANCE_HIGH;
            //チャンネル作るぜ。
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID, name, importance);
            //説明セット。
            mChannel.setDescription(Description);
            //光るぜ。多分要らないぜ。だってスマートウォッチだし。
            mChannel.enableLights(true);
            //赤いぜ。多分要らないぜ。だってスマートウォッチだし。
            mChannel.setLightColor(Color.RED);
            //震えるぜヒート。
            mChannel.enableVibration(true);
            //バイブパターンだぜ。これも拾ったままでよく分かってないぜ。
            mChannel.setVibrationPattern(new long[]{
                    100, 200, 300, 400, 500, 400, 300, 200, 400});
            //バッチはつけないぜ。
            mChannel.setShowBadge(false);
            //NotificationManagerのメソッドでCreateするぜ。
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.ic_stat_conn);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setContentIntent(resultPendingIntent);
        builder.setWhen(System.currentTimeMillis());
        Notification notification = builder.build();

        //いよいよ通知するぜ！
        notificationManager.notify(NOTIFICATION_ID, notification);
        startForeground(NOTIFICATION_ID, notification);
    }
}
