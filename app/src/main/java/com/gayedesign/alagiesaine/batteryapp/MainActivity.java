package com.gayedesign.alagiesaine.batteryapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView chargeTextView,stateTextView,batteryTextView,temperatureTextView;
    BatteryManager batteryManager;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeTextViewInstanceVariables(); //method to initialize instance variables
    }

    private void initializeTextViewInstanceVariables(){
        imageView = (ImageView) findViewById(R.id.imageView);
        chargeTextView = (TextView) findViewById(R.id.chargeTextView);
        stateTextView = (TextView) findViewById(R.id.stateTextView);
        batteryTextView = (TextView) findViewById(R.id.batteryTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(250,250);
        layoutParams.setMargins(250,40,200,8);
        imageView.setLayoutParams(layoutParams);
    }

    private void chargingState(){
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, filter);
        int state = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);

        boolean charging = state == BatteryManager.BATTERY_STATUS_CHARGING;
        boolean notCharging = state == BatteryManager.BATTERY_STATUS_NOT_CHARGING;
        boolean disCharging = state == BatteryManager.BATTERY_STATUS_DISCHARGING;
        boolean full = state == BatteryManager.BATTERY_STATUS_FULL;
        boolean unknown = state == BatteryManager.BATTERY_STATUS_UNKNOWN;

        stateTextView.setTypeface(null, Typeface.ITALIC);

        if (charging){
            stateTextView.setText("CHARGING");
        }else if (notCharging || disCharging){
            stateTextView.setText("NOT CHARGING");
        }else if (full){
            stateTextView.setText("FULLY CHARGED");
        }else if (unknown){
            stateTextView.setText("UNKNOWN");
        }
    }
    private void batteryHealth(){
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null,filter);
        int health  = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

        boolean good = health == BatteryManager.BATTERY_HEALTH_GOOD;
        boolean dead = health == BatteryManager.BATTERY_HEALTH_DEAD;
        boolean cold = health == BatteryManager.BATTERY_HEALTH_COLD;
        boolean overheat = health == BatteryManager.BATTERY_HEALTH_OVERHEAT;
        boolean over_voltage = health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE;
        boolean unspecified = health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE;
        boolean unknown = health == BatteryManager.BATTERY_HEALTH_UNKNOWN;

        batteryTextView.setTypeface(null,Typeface.ITALIC);

        if (good)
            batteryTextView.setText("GOOD");
        else if (dead)
            batteryTextView.setText("DEAD");
        else if (cold)
            batteryTextView.setText("COLD");
        else if (overheat)
            batteryTextView.setText("OVERHEAT");
        else if (over_voltage)
            batteryTextView.setText("OVERHEAT");
        else if (unspecified)
            batteryTextView.setText("UNSPECIFIED FAILURE");
        else if (unknown)
            batteryTextView.setText("UNKNOWN");
    }
    public void batteryTemperature(){
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null,filter);
        double temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
        double temperature = temp / 10;
        temperatureTextView.setTypeface(null,Typeface.ITALIC);
        temperatureTextView.setText(String.valueOf(temperature));
    }
    public void batteryLevel(){
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null,filter);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        chargeTextView.setText(String.valueOf(level)+ "%");
    }

    public void displayChargingImageBattery() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, filter);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;

        if (isCharging){
            if (level >= 80)
                imageView.setImageResource(R.drawable.battery_charging_100);
            else if (level >= 70 && level <= 79)
                imageView.setImageResource(R.drawable.battery_charging_80);
            else if (level >= 55 && level <= 69)
                imageView.setImageResource(R.drawable.battery_charging_60);
            else if (level >= 40 && level <= 54)
                imageView.setImageResource(R.drawable.battery_charging_40);
            else
                imageView.setImageResource(R.drawable.battery_charging_40);
        }else {
            if (level >= 80)
                imageView.setImageResource(R.drawable.full_battery_unplug);
            else if (level >= 60 && level <= 79)
                imageView.setImageResource(R.drawable.almost_full);
            else if (level >= 31 && level <= 59)
                imageView.setImageResource(R.drawable.half_charged);
            else if (level >= 5 && level <= 30)
                imageView.setImageResource(R.drawable.low_battery);
            else
                imageView.setImageResource(R.drawable.battery_empty);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        chargingState(); //method to get the battery's charging state
        batteryHealth();  //method to get the battery's health state
        batteryTemperature();   //method to get the battery;s temperature
        batteryLevel(); //method to get the battery current level
        displayChargingImageBattery();  //display battery charging image view
        sendNotification(); //show notification
    }
    public void sendNotification(){
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null,filter);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.main_icon);
        builder.setColor(Color.GREEN);
        builder.setContentText("Battery charged remaining " + level + "%");
        builder.setContentTitle("Charge remaining");
        builder.setAutoCancel(false);

        notificationManager.notify(0,builder.build());
    }
}
