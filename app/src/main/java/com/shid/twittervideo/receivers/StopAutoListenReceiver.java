package com.shid.twittervideo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Shid on 23/12/2018.
 */

public class StopAutoListenReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        context.getApplicationContext().stopService(new Intent(context,AutoListenService.class));

    }
}
