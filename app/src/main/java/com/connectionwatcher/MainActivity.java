package com.connectionwatcher;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends WearableActivity {
    SharedPreferences data;
    int intervals = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NumberPicker numPicker = findViewById(R.id.numPicker);
        final TextView textSecLabel = findViewById(R.id.textView4);

        // idがswitchButtonのSwitchを取得
        final Switch switchButton = findViewById(R.id.switch1);

        data = getSharedPreferences("intervals", MODE_PRIVATE);
        // 監視間隔の読み込み
        intervals = data.getInt("Intervals", 20);

        // Flavorで処理分岐。
        if (BuildConfig.FLAVOR.equals("ForFree")) {
            numPicker.setValue(intervals);
            numPicker.setVisibility(View.INVISIBLE);
            textSecLabel.setVisibility(View.INVISIBLE);
        } else {
            numPicker.setMaxValue(60);
            numPicker.setMinValue(10);
        }

        //サービスが既に起動している場合は、Trueに。
        if (isServiceWorking()) {
            switchButton.setChecked(true);
            switchButton.setText(getResources().getText(R.string.SwitchOn));
        } else {
            switchButton.setChecked(false);
            switchButton.setText(getResources().getText(R.string.SwitchOff));
        }

        numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Log.d("MainActivity", "oldVal = " + String.valueOf(oldVal));
                //Log.d("MainActivity", "newVal = " + String.valueOf(newVal));
                SharedPreferences.Editor editor = data.edit();
                editor.putInt("Intervals", newVal);
                //editor.commit();
                editor.apply();
            }
        });

        // switchButtonのオンオフが切り替わった時の処理を設定
        switchButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton comButton, boolean isChecked) {
                        // 表示する文字列をスイッチのオンオフで変える
                        String displayChar;
                        // オンなら
                        if (isChecked) {
                            Intent sv = new Intent(getBaseContext(), ConnectionWatchService.class);
                            sv.putExtra("intervals",numPicker.getValue());
                            startForegroundService(sv);
                            switchButton.setText(getResources().getText(R.string.SwitchOn));
                            finish();
                        }
                        // オフなら
                        else {
                            displayChar = getResources().getString(R.string.SwitchOff);
                            stopService(new Intent(getBaseContext(), ConnectionWatchService.class));
                            switchButton.setText(displayChar);
                            Toast toast = Toast.makeText(MainActivity.this, displayChar, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
        );

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        final NumberPicker numPicker = findViewById(R.id.numPicker);
        intervals = numPicker.getValue();

        SharedPreferences.Editor editor = data.edit();
        editor.putInt("Intervals", intervals);

        //editor.commit();
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 監視間隔の読み込み＆再設定
        intervals = data.getInt("Intervals", 20);
        final NumberPicker numPicker = findViewById(R.id.numPicker);
        numPicker.setValue(intervals);

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
