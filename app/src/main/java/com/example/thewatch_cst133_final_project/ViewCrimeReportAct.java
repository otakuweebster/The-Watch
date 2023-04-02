package com.example.thewatch_cst133_final_project;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Purpose: the purpose of this class is to view a series of crimes that has been stored to the database already.
 * @author CST133
 */
public class ViewCrimeReportAct extends AppCompatActivity
{
    public Spinner spinViewOffence, spinViewNeighbor, spinViewID;
    public ImageView imgPreview2;
    public TextView txtViewDate, txtViewTime;
    public EditText txtViewDescriptionSuspect, txtViewDescriptionCrime;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public CrimeReportDBHelper db;
    public Cursor cursor1;
    private ArrayList<CrimeReport> allCrimesReport = new ArrayList<>();

    //Attributes
    public double latitude;
    public double longtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.view_report_layout);

        //Initialization
        spinViewOffence = findViewById(R.id.spinViewOffence);
        spinViewNeighbor = findViewById(R.id.spinViewNeighbor);
        spinViewID = findViewById(R.id.spinViewID);
        imgPreview2 = findViewById(R.id.imgPreview2);
        txtViewDate = findViewById(R.id.txtViewDate);
        txtViewTime = findViewById(R.id.txtViewTime);
        txtViewDescriptionSuspect = findViewById(R.id.txtViewDescriptionSuspect);
        txtViewDescriptionCrime = findViewById(R.id.txtViewDescriptionCrime);

        //Initializing the db
        db = new CrimeReportDBHelper(this);

        //SETTING UP VALUES FOR THE GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getLocation(location);
            }
        };

        try {
            //note time and distance (set to zero here) should be set to values that make sense for your app
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) { };

        //ARRAYADAPTERS FOR THE SPINNERS
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.neighborhoods2, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinViewNeighbor.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.offences, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinViewOffence.setAdapter(adapter2);

        //Turns the entire database cursor object into a manageable arraylist that we can use to stream into.
        cursorToArrayList();

        //This is a sort of workaround. Since the GPS has not got the information yet for the latitude and longtitude, this set the selection as "Adelaide" or something as a precautionary.
        //As you fire back to "Use current location", it will grab the location.
        spinViewNeighbor.setSelection(1);



        spinViewOffence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             * This portion filters out all of the crime reports based on the offence that they have committed.
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                ArrayList<CrimeReport> filteredReports = new ArrayList<>();

                //JUST A FAILSAFE IF IT DOES NOT GRABS THE LONGTITUDE OR LATITUDE.
                if (longtitude <= 0)
                {
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            getLocation(location);
                        }
                    };
                }

                //If you chose the "Use my location" option, the list filters out crimes that happend around 200m range of your
                //current position and that offence,
                if (spinViewNeighbor.getSelectedItem().toString().equals("Use my current location"))
                {
                    filteredReports = allCrimesReport.stream().filter(x -> (x.latitude <= latitude + 0.002 && x.latitude >= latitude - 0.002) && (x.longtitude <=  longtitude + 0.002 && x.longtitude >= longtitude - 0.0002) && x.longtitude != 0 && (x.offence.equals(spinViewOffence.getSelectedItem().toString()))).collect(Collectors.toCollection(ArrayList::new));
                }

                //If you selected any neighborhoods, then it filters out based on the neighboorhod and that offence.
                else
                {
                    filteredReports = allCrimesReport.stream().filter(x -> x.neighborhood.equals(spinViewNeighbor.getSelectedItem().toString()) && x.offence.equals(spinViewOffence.getSelectedItem().toString())).collect(Collectors.toCollection(ArrayList::new));
                }

                //It turns that filtered arraylist with all of the contents of the crime commited with that offence.
                //This updates everytime when you choose the offence combobox, neighbourhood combobox, or both. Basically
                //trying to make it dynamic.
                ArrayAdapter<CrimeReport> adapter =
                        new ArrayAdapter<CrimeReport>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, filteredReports);
                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                spinViewID.setAdapter(adapter);
                showEntry();
            }

            @Override public void onNothingSelected(AdapterView<?> parent)
            {
                //Idk. This does nothing. Useless piece of stuff but it complains if we dont add it.
            }
        });

        /**
         * This portion filters out all of the crime reports based on the neighboorhood they committed on
         * @param parent
         * @param view
         * @param position
         * @param id
         */
        spinViewNeighbor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                ArrayList<CrimeReport> filteredReports = new ArrayList<>();

                //JUST A FAILSAFE IF IT DOES NOT GRABS THE LONGTITUDE OR LATITUDE.
                if (longtitude <= 0)
                {
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            getLocation(location);
                        }
                    };
                }

                //If you chose the "Use my location" option, the list filters out crimes that happend around 200m range of your
                //current position.
                if (spinViewNeighbor.getSelectedItem().toString().equals("Use my current location"))
                {
                    filteredReports = allCrimesReport.stream().filter(x -> (x.latitude <= latitude + 0.002 && x.latitude >= latitude - 0.002) && (x.longtitude <=  longtitude + 0.002 && x.longtitude >= longtitude - 0.0002) && x.longtitude != 0 && (x.offence.equals(spinViewOffence.getSelectedItem().toString()))).collect(Collectors.toCollection(ArrayList::new));
                }

                //If you selected any neighborhoods, then it filters out based on the neighboorhod.
                else
                {
                    filteredReports = allCrimesReport.stream().filter(x -> x.neighborhood.equals(spinViewNeighbor.getSelectedItem().toString()) && x.offence.equals(spinViewOffence.getSelectedItem().toString())).collect(Collectors.toCollection(ArrayList::new));
                }

                //It turns that filtered arraylist with all of the contents of the crime commited with that offence.
                //This updates everytime when you choose the offence combobox, neighbourhood combobox, or both. Basically
                //trying to make it dynamic.
                ArrayAdapter<CrimeReport> adapter =
                        new ArrayAdapter<CrimeReport>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, filteredReports);
                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                spinViewID.setAdapter(adapter);
                showEntry();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // IDK
            }
        });

        //When you select a certain date of crimes committed in the combobox, this grabs the object in the combobox
        //and place it into the ux as a usefull info.
        spinViewID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showEntry();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * This method does these things in order:
     *  - Grabs the CrimeReport object from the combobox,
     *  - if the object is null, then clear all entries text in the view.
     *   - If its not null, then it sets the images and textviews with the connected information
     */
    public void showEntry()
    {
        CrimeReport viewCrimeReference = (CrimeReport) spinViewID.getSelectedItem();

        if (viewCrimeReference == null)
        {
            clearEntry();
        }

        else
        {
            imgPreview2.setImageBitmap(viewCrimeReference.incidentPhoto);
            txtViewTime.setText(viewCrimeReference.time);
            txtViewDate.setText(viewCrimeReference.date);
            txtViewDescriptionCrime.setText(viewCrimeReference.descriptionCrime);
            txtViewDescriptionSuspect.setText(viewCrimeReference.descriptionSuspect);
        }
    }

    /**
     * This basically clears all the textviewers with just blanks and replace the photo with a placeholder for now.
     */
    public void clearEntry()
    {
        Bitmap placeholder = BitmapFactory.decodeResource(this.getResources(), R.drawable.img_placeholder);
        imgPreview2.setImageBitmap(placeholder);
        txtViewTime.setText("");
        txtViewDate.setText("");
        txtViewDescriptionCrime.setText("");
        txtViewDescriptionSuspect.setText("");
    }

    protected void getLocation(Location location)
    {
        latitude = location.getLatitude();
        longtitude = location.getLongitude();
    }

    protected void onStop() {
        locationManager.removeUpdates(locationListener);
        super.onStop();
    }

    protected void onResume()
    {
        try {
            //note time and distance (set to zero here) should be set to values that make sense for your app
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {

        }

        super.onResume();
    }

    /**
     * This method is quite special, useful, and took two hours to figure it out sadly.
     * So this portion, it clears all of the crimesreport arraylist to avoid adding repeated objects,
     *  - grabs the cursor object from the db.getAlLCrimeReport()
     *  - and based from the size of the cursor1, add an instance of that object into the alLCrimesReport arraylist.
     *  - the i == 1 is meant to include the first entry since everytime we say moveToNext at first, it does not grab
     *    the first entry in the database. So this part helps get that.
     */
    public void cursorToArrayList()
    {
        db.open();
        allCrimesReport.clear();

        Cursor cursor1 = db.getAllCrimeReports();

        for (int i = 1; i <= cursor1.getCount(); i++)
        {
            if (i == 1)
            {
                cursor1.moveToFirst();
                String des1 = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.DESCRIPTION_CRIME));
                String offence = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.OFFENCE));
                String neigh = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.NEIGBOURHOOD));
                String time = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.TIME));
                String date = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.DATE));
                String des2 = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.DESCRIPTION_SUSPECT));
                double latitudeRef = cursor1.getDouble(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.LATITUDE));
                double longtitudeRef = cursor1.getDouble(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.LONGTITUDE));
                Bitmap obMap = CrimeReportDBHelper.DbBitmapUtility.getImage(cursor1.getBlob(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.PHOTO)));

                if (latitudeRef <= 0 && longtitudeRef <= 0)
                {
                    allCrimesReport.add(new CrimeReport(des1, offence,neigh, time, date, des2,obMap));
                }

                else
                {
                    allCrimesReport.add(new CrimeReport(des1, offence,latitudeRef, longtitudeRef, time, date, des2,obMap));
                }
            }

            else
            {
                cursor1.moveToNext();
                String des1 = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.DESCRIPTION_CRIME));
                String offence = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.OFFENCE));
                String neigh = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.NEIGBOURHOOD));
                String time = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.TIME));
                String date = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.DATE));
                String des2 = cursor1.getString(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.DESCRIPTION_SUSPECT));
                double latitudeRef = cursor1.getDouble(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.LATITUDE));
                double longtitudeRef = cursor1.getDouble(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.LONGTITUDE));
                Bitmap obMap = CrimeReportDBHelper.DbBitmapUtility.getImage(cursor1.getBlob(cursor1.getColumnIndexOrThrow(CrimeReportDBHelper.PHOTO)));

                if (latitudeRef <= 0 && longtitudeRef <= 0)
                {
                    allCrimesReport.add(new CrimeReport(des1, offence,neigh, time, date, des2,obMap));
                }

                else
                {
                    allCrimesReport.add(new CrimeReport(des1, offence,latitudeRef, longtitudeRef, time, date, des2,obMap));
                }
            }
        }

        db.close();
    }

//    //This is to properly rotate the camera intent's result
//    //
//    public static Bitmap rotateImage(Bitmap source, float angle)
//    {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
//                matrix, true);
//    }
}
