package com.zhao.seller.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zhao.seller.service.FindNewFormService;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Receiver!");
        Intent it = new Intent(context,FindNewFormService.class);
        context.startService(it);
    }
}
