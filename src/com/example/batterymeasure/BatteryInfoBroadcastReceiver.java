package com.example.batterymeasure;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryInfoBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

  
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Battery")
                    .setMessage(
                            "Battery Comsumption" + String.valueOf(level * 100 / scale)
                                    + "%")
                    .setNegativeButton("Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                }
                            }).create();
            dialog.show();
        }
    	

    }

}