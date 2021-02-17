package com.example.bank_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    TextView  app_name;
    ImageView logo;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/spacemono.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        logo = findViewById(R.id.logo);
        app_name = findViewById(R.id.app_name);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startEnterAnimation();
            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), UserList.class));
                finish();
            }
        }, 5500);
    }

    private void startEnterAnimation() {
        app_name.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom));
        logo.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.p_in));

        logo.setVisibility(View.VISIBLE);
        app_name.setVisibility(View.VISIBLE);
    }
}

