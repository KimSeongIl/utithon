package com.example.hanjiyeon.maptest;

/**
 * Created by HanJiYeon on 2015-08-08.
 */
public class ScheduleData {
    private String title;
    private String placeName;
    private double lat;
    private double lng;
    private int time;

    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
    public void setPlaceName(String placeName){
        this.placeName=placeName;
    }
    public String getPlaceName(){
        return placeName;
    }
    public void setLat(double lat){
        this.lat=lat;
    }
    public double getLat(){
        return lat;
    }
    public void setLng(double lng){
        this.lng=lng;
    }
    public double getLng(){
        return lng;
    }
    public void setTime(int time){
        this.time=time;
    }
    public int getTime(){
        return time;
    }
}
