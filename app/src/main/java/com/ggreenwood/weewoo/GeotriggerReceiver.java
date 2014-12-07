package com.ggreenwood.weewoo;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.esri.android.geotrigger.GeotriggerBroadcastReceiver;

/**
 * Created by garrett on 12/6/14.
 */
public class GeotriggerReceiver extends GeotriggerBroadcastReceiver{
    @Override
    protected void onPushMessage(Context context, Bundle notification) {
        super.onPushMessage(context, notification);

        // The notification Bundle has these keys: 'text', 'url', 'sound', 'icon', 'data'
        String msg = String.format("Push Message Received: %s", notification.get("text"));
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
