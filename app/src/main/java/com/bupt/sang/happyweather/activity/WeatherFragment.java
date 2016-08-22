package com.bupt.sang.happyweather.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bupt.sang.happyweather.R;

import com.bupt.sang.happyweather.model.WeatherInfo;
import com.bupt.sang.happyweather.network.ApiClient;
import com.bupt.sang.happyweather.network.data.DailyResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sang on 2016/5/11.
 */
public class WeatherFragment extends Fragment {
    public static final String EXTRA_CITY_NAME = "city_name";
    private static final String TAG = "WeatherFragment";

    @Bind(R.id.publish_text)
    TextView publishTime;
    @Bind(R.id.current_date)
    TextView date;
    @Bind(R.id.weather_desp)
    TextView weatherDesp;
    @Bind(R.id.temp_low)
    TextView temperatureLow;
    @Bind(R.id.temp_high)
    TextView temperatureHigh;

    public String cityName;
    WeatherInfo weatherInfo;

    public static WeatherFragment newInstance(String cityName) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CITY_NAME, cityName);

        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cityName = getArguments().getString(EXTRA_CITY_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreateView(inflater, parent, savedInstanceState);
        View root = inflater.inflate(R.layout.viewpager_content, parent, false);
        ButterKnife.bind(this, root);
        publishTime.setText("syh");
        date.setText("2016");
        ApiClient.getInstance().getDaily(cityName).enqueue(new Callback<DailyResponse>() {
            @Override
            public void onResponse(Call<DailyResponse> call, Response<DailyResponse> response) {
                WeatherInfo weatherInfo = new WeatherInfo(response.body());
                updateWeather(weatherInfo);
            }

            @Override
            public void onFailure(Call<DailyResponse> call, Throwable t) {

            }
        });
        return root;
    }

    public void updateWeather(WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            Log.e(TAG, "updateWeather: 没有天气可以显示");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        publishTime.setText("今天" + weatherInfo.getPtime() + "发布");
        date.setText(sdf.format(new Date()));
        weatherDesp.setText(weatherInfo.getWeather());
        temperatureLow.setText(weatherInfo.getTemp1());
        temperatureHigh.setText(weatherInfo.getTemp2());
    }
}
