package com.example.smartanticurruptionbeuroplatform;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

    public class AirplaneModeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {

                boolean isAirplaneModeOn =
                        Settings.Global.getInt(
                                context.getContentResolver(),
                                Settings.Global.AIRPLANE_MODE_ON,
                                0
                        ) != 0;

                String message = isAirplaneModeOn
                        ? "Airplane Mode is ON"
                        : "Airplane Mode is OFF";

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

