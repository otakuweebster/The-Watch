package com.example.thewatch_cst133_final_project;

import android.graphics.Bitmap;

public class CrimeReport
{
    public long id;
    public String descriptionCrime, offence, neighborhood, time, date, descriptionSuspect;
    public double latitude, longtitude;
    public Bitmap incidentPhoto;

    /**
     * Basically this is a constructor that creates a new object if they do not want to use their precise location
     * @param descriptionCrime
     * @param offence
     * @param neighborhood
     * @param time
     * @param date
     * @param descriptionSuspect
     * @param incidentPhoto
     */
    public CrimeReport(String descriptionCrime, String offence, String neighborhood, String time, String date, String descriptionSuspect, Bitmap incidentPhoto)
    {
        this.descriptionCrime = descriptionCrime;
        this.offence = offence;
        this.neighborhood = neighborhood;
        this.time = time;
        this.date = date;
        this.descriptionSuspect = descriptionSuspect;
        this.incidentPhoto = incidentPhoto;
        this.latitude = 0;
        this.longtitude = 0;
    }

    /**
     * Basically the same thing but when they chose to get their location instead
     * @param descriptionCrime
     * @param offence
     * @param time
     * @param date
     * @param descriptionSuspect
     * @param incidentPhoto
     */
    public CrimeReport(String descriptionCrime, String offence, double latitude, double longtitude, String time, String date, String descriptionSuspect, Bitmap incidentPhoto)
    {
        this.descriptionCrime = descriptionCrime;
        this.offence = offence;
        this.time = time;
        this.date = date;
        this.descriptionSuspect = descriptionSuspect;
        this.incidentPhoto = incidentPhoto;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.neighborhood = "";
    }

    public String toString()
    {
        return date;
    }
}
