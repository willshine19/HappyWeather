package com.bupt.sang.happyweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.bupt.sang.happyweather.model.WeatherInfo;
import com.bupt.sang.happyweather.network.ApiClient;
import com.bupt.sang.happyweather.network.data.NowResponse;
import com.bupt.sang.happyweather.service.ForegroundService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sangyaohui on 16/8/21.
 */
public class WeatherPresenter {

    private Activity activity;
    public WeatherPresenter(Activity activity) {
        this.activity = activity;
    }

    public void queryWeather(final String cityName) {
        ApiClient.getInstance().getWeather(cityName).enqueue(new Callback<NowResponse>() {
            @Override
            public void onResponse(Call<NowResponse> call, Response<NowResponse> response) {
                Toast.makeText(activity, cityName + "当前温度:" + response.body().results.get(0).now.temperature, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<NowResponse> call, Throwable t) {

            }
        });
    }

    public void startForeGoundService(final WeatherInfo info) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Intent intent = new Intent(activity, ForegroundService.class);
                intent.putExtra("city_name", info.getCity());
                intent.putExtra("temp1", info.getTemp1());
                intent.putExtra("temp2", info.getTemp2());
                intent.putExtra("weather_desp", info.getWeather());
                intent.putExtra("publish_time", info.getPtime());
                intent.putExtra("weather_code", info.getCityid());
                activity.startService(intent);
            }
        }.start();
    }
}
