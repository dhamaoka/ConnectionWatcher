package com.connectionwatcher;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends WearableActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // idがswitchButtonのSwitchを取得
        final Switch switchButton = findViewById(R.id.switch1);

        //サービスが既に起動している場合は、Trueに。
        if (isServiceWorking()) {
            switchButton.setChecked(true);
            switchButton.setText(getResources().getText(R.string.SwitchOn));
        } else {
            switchButton.setChecked(false);
            switchButton.setText(getResources().getText(R.string.SwitchOff));
        }

        // switchButtonのオンオフが切り替わった時の処理を設定
        switchButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton comButton, boolean isChecked) {
                        // 表示する文字列をスイッチのオンオフで変える
                        String displayChar;
                        // オンなら
                        if (isChecked) {
                            displayChar = getResources().getString(R.string.SwitchOn);
                            Intent sv = new Intent(getBaseContext(), ConnectionWatchService.class);
                            startService(sv);
                            switchButton.setText(getResources().getText(R.string.SwitchOn));
                        }
                        // オフなら
                        else {
                            displayChar = getResources().getString(R.string.SwitchOff);
                            stopService(new Intent(getBaseContext(), ConnectionWatchService.class));
                            switchButton.setText(getResources().getText(R.string.SwitchOff));
                        }
                        Toast toast = Toast.makeText(MainActivity.this, displayChar, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        );

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // idがswitchButtonのSwitchを取得
        final Switch switchButton = findViewById(R.id.switch1);

        //サービスが既に起動している場合は、Trueに。
        if (isServiceWorking()) {
            switchButton.setChecked(true);
            switchButton.setText(getResources().getText(R.string.SwitchOn));
        } else {
            switchButton.setChecked(false);
            switchButton.setText(getResources().getText(R.string.SwitchOff));
        }
    }

    //サービスが起動してるかの確認。
    private boolean isServiceWorking() {
        ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ConnectionWatchService.class.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
