package com.example.thewatch_cst133_final_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Purpose: This serves as the landing page activity class for the entire program that listens any click handlers to the
 * buttons to help direct to correct location.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * A series of the String that contains the needed perms for the program.
     */
    private static final String[] REQPERMISSION =  {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //This portion checks if the required permision has been set as true. If they are not, the program prompts us a permission to enable access to GPS and camera
        if(!(checkPerms(this, REQPERMISSION)))
        {
            ActivityCompat.requestPermissions(this,REQPERMISSION,PERMISSION);
        }

    }

    /**
     * Click handler to switch to the create report activity intent
     * @param v
     */
    public void switchReportActivity(View v)
    {
        Intent intent = new Intent(this, CreateCrimeReportAct.class);
        this.startActivity(intent);
    }

    /**
     * Click handler to switch to the view report activity intent
     * @param v
     */
    public void switchViewReportActivity(View v)
    {
        Intent intent = new Intent(this, ViewCrimeReportAct.class);
        this.startActivity(intent);
    }

    /**
     * This method grabs the arraylist of string that holds the required permission,
     * and every string, it checks if their permission has been enabled. If not, it returns false.
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPerms(Context context, String[] permission)
    {
        for (String indPerm : permission)
        {
            if (ActivityCompat.checkSelfPermission(context, indPerm) != PackageManager.PERMISSION_GRANTED)
            {
                return false;
            }
        }

        return true;
    }
}