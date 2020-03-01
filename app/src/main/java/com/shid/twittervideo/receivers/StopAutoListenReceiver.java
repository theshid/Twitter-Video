package com.shid.twittervideo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shid.twittervideo.util.SharePref;

/**
 * Created by Shid on 23/12/2018.
 */

public class StopAutoListenReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        context.getApplicationContext().stopService(new Intent(context,AutoListenService.class));
        SharePref sharedPref = new SharePref(context.getApplicationContext());
        sharedPref.setSwitch(false);
    }
}
