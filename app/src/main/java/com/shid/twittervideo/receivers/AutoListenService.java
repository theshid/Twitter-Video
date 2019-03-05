package com.shid.twittervideo.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.shid.twittervideo.R;
import com.shid.twittervideo.util.Constant;
import com.shid.twittervideo.util.ServiceUtil;
import com.shid.twittervideo.views.MainActivity;

public class AutoListenService extends Service {

    private static final String GENERAL_CHANNEL = "general channel";
    private Context context;
    private NotificationManager notificationManager;
    private Notification vNotification;
    private android.support.v4.app.NotificationCompat.Builder vNotification1;
    private ClipboardManager mClipboard;
    private ClipboardManager.OnPrimaryClipChangedListener listener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent openActivityIntent = new Intent(context, MainActivity.class);
        openActivityIntent.putExtra("service on",true);
        openActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent openActivityPIntent = PendingIntent.getActivity(context, Constant.AUTO_REQUEST_CODE,openActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // FLAG_UPDATE_CURRENT = if the described PendingIntent already exists,
        // then keep it but its replace its extra data with what is in this new Intent.

        Intent stopAutoIntent = new Intent(context,StopAutoListenReceiver.class);
        stopAutoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent stopAutoPIntent = PendingIntent.getBroadcast(context,Constant.REQUEST_CODE,stopAutoIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Icon icon = Icon.createWithResource(context,R.drawable.ic_stop_black);
            Notification.Action.Builder builder = new Notification.Action.Builder(icon,"STOP",stopAutoPIntent);

            vNotification = new Notification.Builder(context,GENERAL_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bitmap)
                    .setContentTitle("AutoListen now activated")
                    .setContentText("Copy tweet URL to trigger download")
                    .setContentIntent(openActivityPIntent)
                    .setAutoCancel(true)
                    .addAction(builder.build())
                    .build();

            vNotification.flags = Notification.FLAG_NO_CLEAR;

            notificationManager.notify(Constant.NOTI_IDENTIFIER,vNotification);
            startForeground(Constant.NOTI_IDENTIFIER,vNotification);
        }else{//for old devices
            vNotification1 = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bitmap)
                    .setContentTitle("AutoListen now activated")
                    .setContentText("Copy tweet URL to trigger download")
                    .setContentIntent(openActivityPIntent)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .addAction(R.drawable.ic_stop_black,"STOP",stopAutoPIntent);

            notificationManager.notify(Constant.NOTI_IDENTIFIER,vNotification1.build());



        }

        mClipboard.addPrimaryClipChangedListener(listener);
        //Toast.makeText(context,"TwitterVideo AutoListen enabled...",Toast.LENGTH_SHORT).show();


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        context = getApplicationContext();
        mClipboard = (ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE); 
        createNotificationChannel();

        listener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                performClipboardCheck();
            }
        };


    }


    @Override
    public void onDestroy() {
        sendMessage();
        if (notificationManager!=null) {
            notificationManager.cancel(Constant.NOTI_IDENTIFIER);
        }


        if (mClipboard!=null && listener!=null){
            this.mClipboard.removePrimaryClipChangedListener(listener);
            Toast.makeText(context, "TwitterVideo AutoListen Disabled, reset to use again", Toast.LENGTH_SHORT).show();

        }





        super.onDestroy();
    }

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("service-destroyed");
        // You can also include some extra data.
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void performClipboardCheck() {
     if (mClipboard.hasPrimaryClip()){
         String copiedURL = mClipboard.getPrimaryClip().getItemAt(0).toString();
         ServiceUtil.fetchTweet(copiedURL,context);
     }
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(GENERAL_CHANNEL,
                    getResources().getString(R.string.general_channel),
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setShowBadge(true);
            notificationChannel.setLightColor(Color.MAGENTA);
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public AutoListenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
