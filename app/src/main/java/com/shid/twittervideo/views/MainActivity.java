package com.shid.twittervideo.views;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.rxdownloader.RxDownloader;
import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchListener;
import com.shid.twittervideo.R;
import com.shid.twittervideo.util.Constant;

import com.shid.twittervideo.receivers.AutoListenService;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.UnsupportedEncodingException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import it.grabz.grabzit.GrabzItClient;
import retrofit2.Call;


public class MainActivity extends AppCompatActivity {
    private Button btn_download, btn_paste;
    private ProgressDialog progressDialog;
    private TextView txt_url;
    private Switch swt_autolisten ;
    private DayNightSwitch swt_night;
    private ScrollView scrollView;
    private ClipboardManager clipboardManager;


    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Twitter.initialize(this);
        setContentView(R.layout.activity_main);


        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(Constant.TWITTER_KEY, Constant.TWITTER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);

        //The mode defines who has access to your app's preferences, Private means created file
        // will be accessed by only the calling application.
        sharedPreferences = this.getSharedPreferences("Theme", Context.MODE_PRIVATE);
        //scrollView.setBackgroundColor(sharedPreferences.getInt("Theme",Context.MODE_PRIVATE));

        btn_download = (Button)findViewById(R.id.btn_download);
        txt_url = (TextView)findViewById(R.id.txt_tweet_url);
        swt_autolisten =(Switch)findViewById(R.id.swt_autolisten);
        swt_night = (DayNightSwitch) findViewById(R.id.swt_night);
        scrollView = (ScrollView)findViewById(R.id.main_layout);
        btn_paste = (Button)findViewById(R.id.btn_paste);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Setting the day Night mode

        btn_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                try {
                    CharSequence textToPaste = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                    txt_url.setText(textToPaste);
                } catch (Exception e) {
                    return;
                }
            }
        });

        swt_night.setDuration(450);
        swt_night.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean isNight) {
                if(isNight){
                    Toast.makeText(MainActivity.this,"Night mode",Toast.LENGTH_SHORT).show();

                    scrollView.setBackgroundColor(getResources().getColor(R.color.colorBlack));
                    Drawable drawable = ContextCompat.getDrawable(MainActivity.this,R.drawable.baseline_close_black_18dp);
                    drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    txt_url.setTextColor(getResources().getColor(R.color.colorWhite));
                    txt_url.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);


                    editor.putInt("Theme",getResources().getColor(R.color.colorBlack));
                    editor.apply();




                }
                else{
                    Toast.makeText(MainActivity.this,"Day mode",Toast.LENGTH_SHORT).show();

                    scrollView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    txt_url.setTextColor(getResources().getColor(R.color.colorBlack));
                    txt_url.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
                    Drawable drawable = ContextCompat.getDrawable(MainActivity.this,R.drawable.baseline_close_black_18dp);
                    drawable.setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);


                    editor.putInt("Theme",getResources().getColor(R.color.colorWhite));
                    editor.apply();
                }
            }
        });


        /*if (getIntent().getBooleanExtra("service_on",false)){
            swt_autolisten.setChecked(true);
        }else swt_autolisten.setChecked(false);
        */

        //If we dont have permission we prompt the user
        if (!storageAllowed()) {

            ActivityCompat.requestPermissions(this,Constant.PERMISSION_STRORAGE,Constant.REQUEST_EXTERNAL_STORAGE);
        }

        txt_url.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (txt_url.getRight() - txt_url.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        txt_url.setText("");

                        return true;
                    }
                }
                return false;
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching video....");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        // Get intent, action and MIME type
        //Return the intent that started this activity, that is the case when we are sharing a tweet
        //from twitter
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null){
            
            if("text/plain".equals(type)){
                handleSharedText(intent);
            }
        }
        
        handleAutoListen();

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname;

                //Check if the tweet url field has text containing twitter.com/...
                if(txt_url.getText().length() >0 && txt_url.getText().toString().contains("twitter.com/")){

                    Long id = getTweetId(txt_url.getText().toString());
                    fname = String.valueOf(id);

                    //Call method to get tweet
                    if(id != null){
                        getTweet(id,fname);
                    }
                    else{
                        alertNoUrl();
                    }
                }
            }
        });





        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("service-destroyed"));









    }

    @Override
    protected void onResume() {

        super.onResume();
    }



    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "service-destroyed" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            handleAutoListen();

/*
            swt_autolisten.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        recreateService();
                        Toast.makeText(context, "TwitterVideo AutoListen Recreated", Toast.LENGTH_SHORT).show();
                    } else if(!(isChecked)) {
                        stopAutoService();
                        Toast.makeText(context, "TwitterVideo AutoListen Disabled, reset to use again", Toast.LENGTH_SHORT).show();
                    } else{
                        swt_autolisten.setChecked(false);
                    }
                }
            });
            */
            //swt_autolisten.setChecked(false);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();


/*


                swt_autolisten.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            recreateService();
                            //Toast.makeText(context, "TwitterVideo AutoListen Recreated", Toast.LENGTH_SHORT).show();
                        } else if(!(isChecked)) {
                            stopAutoService();
                           // Toast.makeText(context, "TwitterVideo AutoListen Disabled, reset to use again", Toast.LENGTH_SHORT).show();
                        } else{
                            swt_autolisten.setChecked(false);
                        }
                    }
                });
                */
                //swt_autolisten.setChecked(false);


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void getTweet(Long id, String fname) {
        progressDialog.show();

     TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
     StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> tweetCall = statusesService.show(id,null,null,null);
        tweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {

                //Check if media is present
                if(result.data.extendedEntities == null && result.data.entities.media == null){
                    alertNoMedia();
                }

                 else if (!(result.data.extendedEntities.media.get(0).type).equals("video") &&
                        !(result.data.extendedEntities.media.get(0).type).equals("animated_gif")){
                    alertNoVideo();
                }else{
                     String filename = fname;
                     String url;

                    //Set filename to gif or mp4
                    if((result.data.extendedEntities.media.get(0).type).equals("video") ||
                            (result.data.extendedEntities.media.get(0).type).equals("animated_gif")){
                        filename = filename + ".mp4";
                        Log.d("TAG","filenme for video is " + filename);


                    }

                    int i = 0;
                    url = result.data.extendedEntities.media.get(0).videoInfo.variants.get(i).url;

                    Log.d("TAG","url is " + url);

                    while (!url.contains(".mp4")){
                        try{
                            if(result.data.extendedEntities.media.get(0).videoInfo.variants.get(i) != null){
                                url = result.data.extendedEntities.media.get(0).videoInfo.variants.get(i).url;
                                Log.d("TAG","url2 is " + url);
                                i += 1;
                            }
                        } catch (IndexOutOfBoundsException e){
                            downloadVideo(url,filename);
                        }
                    }

                    downloadVideo(url,filename);
                }

            }



            @Override
            public void failure(TwitterException exception) {
              Toast.makeText(MainActivity.this,"Request failed, check your Internet connection",Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void downloadVideo(String url, String filename) {


        //Check if External Storage permission js allowed
        if (!storageAllowed()) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, Constant.PERMISSION_STRORAGE, Constant.REQUEST_EXTERNAL_STORAGE);
            progressDialog.hide();
            Toast.makeText(this, "Kindly grant the request and try again", Toast.LENGTH_SHORT).show();
        }else{
            RxDownloader rxDownloader = new RxDownloader(MainActivity.this);
            rxDownloader.download(url,filename,"video/*",true)
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String s) {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                            Toast.makeText(MainActivity.this, "Download Complete", Toast.LENGTH_SHORT).show();
                        }
                    });

            progressDialog.hide();
            Toast.makeText(this, "Download Started: Check Notification", Toast.LENGTH_LONG).show();



        }
    }



    private void alertNoVideo() {
     progressDialog.hide();
     Toast.makeText(this,"URL entered do not contain any video or gif",Toast.LENGTH_SHORT).show();
    }

    private void alertNoMedia() {
        progressDialog.hide();
        Toast.makeText(this,"The link entered do not contain any media",Toast.LENGTH_SHORT).show();
    }


    private Long getTweetId( String s) {
        Log.d("TAG","link is :" + s);

        try{
            String[] split = s.split("\\/");
            String id= split[5].split("\\?")[0];
            return  Long.parseLong(id);
        } catch (Exception e)
        {
            Log.d("TAG", "getTweetId: "+e.getLocalizedMessage());
            alertNoUrl();
            return null;
        }
    }

    private void alertNoUrl() {

        Toast.makeText(this,"Enter correct URL",Toast.LENGTH_LONG).show();
    }

    //Method call to handle AutoListen feature
    private void handleAutoListen() {
        swt_autolisten.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    startAutoService();
                } else {
                    stopAutoService();
                }
            }
        });
    }

    private void stopAutoService() {
        this.stopService(new Intent(MainActivity.this,AutoListenService.class));

    }

    private void startAutoService() {
       // this.startService(new Intent(MainActivity.this,AutoListenService.class));
        Intent serviceIntent = new Intent(MainActivity.this,AutoListenService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            MainActivity.this.startForegroundService(serviceIntent);
        } else{
            startService(serviceIntent);
        }
        Toast.makeText(MainActivity.this,"TwitterVideo AutoListen enabled...",Toast.LENGTH_SHORT).show();
    }

    private void recreateService(){
        //this.startService((new Intent(MainActivity.this,AutoListenService.class)));

        Intent serviceIntent = new Intent(MainActivity.this,AutoListenService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            MainActivity.this.startForegroundService(serviceIntent);
        } else{
            startService(serviceIntent);
        }

    }

    //Method handling pasting the tweet url into the field when Sharing the tweet url from the twitter app
    private void handleSharedText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.d("TAG","shared text is :" + sharedText);
        if(sharedText != null){
            try{
                // splits a String into an array of substrings given a specific delimiter.
                if(sharedText.split("\\ ").length > 1){
                    //sharedText is splitted into four elements into an array and returns
                    //the fourth elements of the array
                     txt_url.setText(sharedText.split("\\ ")[4]);

                } else{
                    txt_url.setText(sharedText.split("\\ ")[0]);
                }

            } catch (Exception e){
                Log.d("TAG", "handleSharedText: "+e);
            }
        }

    }


    //Method that checks the permission depending on the version of the phone
    private boolean storageAllowed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return permission == PackageManager.PERMISSION_GRANTED;
        }

        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.about){
            startActivity(new Intent(MainActivity.this,AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
