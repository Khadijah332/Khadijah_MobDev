package com.example.ahmad_khadijah_s2330453.model;

import android.util.Log;

public class Situation {

    //VARIABLES
    private String description;
    private String situationType;
    private String roadName;
    private String startTime;
    private String endTime;
    private String delaysType;
    private String feedType;
    private double latitude;
    private double longitude;

    //DEFAULT CONSTRUCTOR
    public Situation() {
    }


    //SETTERS & GETTERS
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
        Log.d("Situation", String.format(
                "Description set: %s", this.description));
    }

    public String getSituationType() {
        return situationType;
    }
    public void setSituationType(String situationType) {
        this.situationType = situationType;
        Log.d("Situation", String.format(
                "Situation Type set: %s", this.situationType));
    }

    public String getRoadName() {
        return roadName;
    }
    public void setRoadName(String roadName) {
        this.roadName = roadName;
        Log.d("Situation", String.format(
                "Road Name set: %s", this.roadName));
    }

    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
        Log.d("Situation", String.format(
                "Start Time set: %s", this.startTime));
    }

    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
        Log.d("Situation", String.format(
                "End Time set: %s", this.endTime));
    }

    public String getDelaysType() {
        return delaysType;
    }
    public void setDelaysType(String delaysType) {
        this.delaysType = delaysType;
        Log.d("Situation", String.format(
                "Delays Type set: %s", this.delaysType));
    }

    public String getFeedType() {
        return feedType;
    }
    public void setFeedType(String feedType) {
        this.feedType = feedType;
        Log.d("Situation", String.format(
                "Feed Type set: %s", this.feedType));
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
        Log.d("Situation", String.format(
                "Latitude set: %f", this.latitude));
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
        Log.d("Situation", String.format(
                "Longitude set: %f", this.longitude));
    }

    //TO STRING METHOD
    @Override
    public String toString() {
        return "Situation{" +
                "description='" + description + '\'' +
                ", situationType='" + situationType + '\'' +
                ", roadName='" + roadName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", delaysType='" + delaysType + '\'' +
                ", feedType='" + feedType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

}



