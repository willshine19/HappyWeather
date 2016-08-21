package com.bupt.sang.happyweather.activity;

import android.app.Activity;
import android.widget.Toast;

import com.bupt.sang.happyweather.network.ApiClient;
import com.bupt.sang.happyweather.network.data.NowResponse;

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
}
