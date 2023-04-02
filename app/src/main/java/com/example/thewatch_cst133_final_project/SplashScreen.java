package com.example.thewatch_cst133_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Puropse: This portion creates 1 scond splashscreen for the program.
 * @author CST133
 */
public class SplashScreen extends AppCompatActivity
{

    ImageView imgEye, imgTextLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashscreen);

        imgEye = findViewById(R.id.imgEye);
        imgTextLogo = findViewById(R.id.imgTextLogo);

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        imgEye.setAnimation(anim);

        //CREATES A HOLD STATEMENT THAT BASICALLY FREEZES THE ANIMATION FOR A WHILE BEFORE INITIATING A INTENT.
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            imgTextLogo.setAnimation(anim);

            Handler handler2 = new Handler();
            handler2.postDelayed(() -> {
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                finish();
            },1000);
        },500);

    }
}