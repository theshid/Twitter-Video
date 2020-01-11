package com.shid.twittervideo.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.shid.twittervideo.R;
import com.shid.twittervideo.util.SharePref;

public class AboutActivity extends AppCompatActivity {
    SharePref sharePref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharePref = new SharePref(this);
        if (sharePref.loadNightMode()){
            setTheme(R.style.DarkTheme);
        } else{
            setTheme(R.style.DayTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setTitle("About");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
