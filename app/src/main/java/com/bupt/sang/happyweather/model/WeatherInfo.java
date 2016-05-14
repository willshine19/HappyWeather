package com.bupt.sang.happyweather.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sang on 2016/5/11.
 */
public class WeatherInfo {
    private String city;    // cityname
    private String cityid; // weatherId
    private String temp1;
    private String temp2;
    private String img1;
    private String img2;
    private String weather; // weatherDesp
    private String ptime; // publishtime

    public WeatherInfo(JSONObject json) {
        try {
            city = json.getString("city");
            cityid = json.getString("cityid");
            temp1 = json.getString("temp1");
            temp2 = json.getString("temp2");
            weather = json.getString("weather");
            ptime = json.getString("ptime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public WeatherInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject json = jsonObject.getJSONObject("weatherinfo");
            city = json.getString("city");
            cityid = json.getString("cityid");
            temp1 = json.getString("temp1");
            temp2 = json.getString("temp2");
            weather = json.getString("weather");
            ptime = json.getString("ptime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCity() {
        return city;
    }

    public String getPtime() {
        return ptime;
    }

    public String getWeather() {

        return weather;
    }

    public String getTemp1() {

        return temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public String getCityid() {

        return cityid;
    }

    @Override
    public String toString() {
        return city + cityid + weather + temp1 + temp2 + ptime;
    }
}
