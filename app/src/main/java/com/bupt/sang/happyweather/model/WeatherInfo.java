package com.bupt.sang.happyweather.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sang on 2016/5/11.
 */
public class WeatherInfo {
    private String cityName;
    private String weatherCode;
    private String temp1;
    private String temp2;
    private String weatherDesp;
    private String publishTime;

    public WeatherInfo(JSONObject json) {
        try {
            cityName = json.getString("city");
            weatherCode = json.getString("cityid");
            temp1 = json.getString("temp1");
            temp2 = json.getString("temp2");
            weatherDesp = json.getString("weather");
            publishTime = json.getString("ptime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public WeatherInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject json = jsonObject.getJSONObject("weatherinfo");
            cityName = json.getString("city");
            weatherCode = json.getString("cityid");
            temp1 = json.getString("temp1");
            temp2 = json.getString("temp2");
            weatherDesp = json.getString("weather");
            publishTime = json.getString("ptime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCityName() {
        return cityName;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getWeatherDesp() {

        return weatherDesp;
    }

    public String getTemp1() {

        return temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public String getWeatherCode() {

        return weatherCode;
    }
}
