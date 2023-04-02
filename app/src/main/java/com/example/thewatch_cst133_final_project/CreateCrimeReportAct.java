package com.example.thewatch_cst133_final_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationListenerCompat;

import com.google.android.material.snackbar.Snackbar;

/**
 * Purpose: The purpose of this class is to handle the creating a crime report activity and contains other methods on placing it to the database
 * @author CST133
 */
public class CreateCrimeReportAct extends AppCompatActivity implements View.OnClickListener
{
    public EditText txtDescriptionCrime, txtDescriptionSuspect, txtDate, txtTime;
    public Spinner spinOffence, spinNeighborhood;
    public ImageView imgPreview;
    public CheckBox chkLocation;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public CrimeReportDBHelper db;

    //Attributes
    public double latitude;
    public double longtitude;
    public Bitmap obReported;

    /**
     * Upon creating the activity on launch:
     *  - It initialized the required attributes up above
     *  - Initialize the location manager and its listener that listens any changes to our location
     *  - Filling up the list with a series of string arrays located in string.xml and place it to the spinners.
     *  - initialize the checkbox listener
     *  - initialize the db.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.create_crime_layout);

        //INITIALIZATION OF METHODS
        txtDescriptionCrime = findViewById(R.id.txtDescriptionCrime);
        txtDescriptionSuspect = findViewById(R.id.txtDescriptionSuspect);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        spinOffence = findViewById(R.id.spinOffence);
        spinNeighborhood = findViewById(R.id.spinNeighborhood);
        chkLocation = findViewById(R.id.chkLocation);
        imgPreview = findViewById(R.id.imgPreview);

        //SETTING UP VALUES FOR THE GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                designateLocation(location);
            }
        };

        //register the listner with the manager to receives updates
        try {
            //note time and distance (set to zero here) should be set to values that make sense for your app
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {

        }

        //ARRAYADAPTERS FOR THE SPINNERS
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.neighborhoods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNeighborhood.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.offences, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinOffence.setAdapter(adapter2);

        //Setting up location for the check box location
        chkLocation.setOnClickListener(this);

        //Initializing the db
        db = new CrimeReportDBHelper(this);

    }

    /**
     * This function basically does these things:
     *  - Opens the database
     *  - grabs all of the information from the comboboxes and text views
     *  - Creates a snackbar notification if the system detects if you have not filled the fields or took any photos.
     *  - If you do filled however, it uses one constructor if you checked the "Use mu location" that contains long and lat.
     *  - if unchecked, it will use another constructor that contains just the neighborhood args.
     * @param view
     */
    public void submitReport(View view)
    {
        db.open();
        String des1 = txtDescriptionCrime.getText().toString();
        String offence = spinOffence.getSelectedItem().toString();
        String neigh = spinNeighborhood.getSelectedItem().toString();
        String time = txtTime.getText().toString();
        String date = txtDate.getText().toString();
        String des2 = txtDescriptionSuspect.getText().toString();
        Bitmap obMap = obReported;

        if (des1.equals("") || offence.equals("") || neigh.equals("") || time.equals("") || date.equals("") || des2.equals("") || obMap == null)
        {
            Snackbar.make(findViewById(R.id.createReportLayout), "Provide all information to ensure proper report is documented", Snackbar.LENGTH_SHORT).show();
        }

        else
        {
            if (chkLocation.isChecked())
            {
                CrimeReport obReportObj = new CrimeReport(des1, offence,latitude, longtitude, time, date, des2,obMap);
                db.addCrimeReport(obReportObj);
            }

            else
            {
                CrimeReport obReportObj = new CrimeReport(des1, offence,neigh, time, date, des2,obMap);
                db.addCrimeReport(obReportObj);
            }

            Toast.makeText(this, "Report successfully submitted!", Toast.LENGTH_SHORT).show();
            finish();
            db.close();
        }


    }

    /**
     * This function disables the neighborhood combo box if you checked the "Use my location" checkbox.
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (chkLocation.isChecked()) {
            spinNeighborhood.setEnabled(false);
        } else {
            spinNeighborhood.setEnabled(true);
        }
    }

    /**
     * As it detects new location, it designates the global variable for the latitude and longtitude so that one of the constructors can reference it later on.
     * @param location
     */
    protected void designateLocation(Location location) {
        latitude = location.getLatitude();
        longtitude = location.getLongitude();

    }

    protected void onStop() {
        locationManager.removeUpdates(locationListener);
        super.onStop();
    }

    protected void onResume() {
        try {
            //note time and distance (set to zero here) should be set to values that make sense for your app
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {

        }

        super.onResume();
    }

    /**
     * THIS IS THE CAMERA PORTION
     */

    /**
     * This opens up an activity or intent for the camera app.
     * @param view
     */
    public void takePhoto(View view) {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 12);
    }

    /**
     * After you took a photo, it grabs the photo data, place it as a bitmap variable, and set the imageview's bitmap as the photo.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        try
        {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 12) {
                obReported = (Bitmap) data.getExtras().get("data");
                imgPreview.setImageBitmap(obReported);
            }

        }
        catch (NullPointerException exp)
            {
                Toast.makeText(this, "Photo evidence is needed to verify report.", Toast.LENGTH_SHORT).show();
            }



    }




}

