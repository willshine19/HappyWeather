package com.bupt.sang.happyweather.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupt.sang.happyweather.R;

import com.bupt.sang.happyweather.model.WeatherInfo;
import com.bupt.sang.happyweather.util.RefreshableView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sang on 2016/5/11.
 */
public class WeatherFragment extends Fragment {
    public static final String EXTRA_WEATHER_ID = "weatherid";
    private static final String TAG = "WeatherFragment";

    private String weatherId;
    private TextView publishTimeTv;
    private TextView currentDateTv;
    private TextView weatherDespTv;
    private TextView temp1Tv;
    private TextView temp2Tv;

    public static WeatherFragment newInstance(String weatherId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_WEATHER_ID, weatherId);

        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherId = getArguments().getString(EXTRA_WEATHER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewpager_content, parent, false);
        publishTimeTv = (TextView)v.findViewById(R.id.publish_text);
        publishTimeTv.setText("syh" + weatherId);
        currentDateTv = (TextView) v.findViewById(R.id.current_date);
        weatherDespTv = (TextView) v.findViewById(R.id.weather_desp);
        temp1Tv = (TextView) v.findViewById(R.id.temp1);
        temp2Tv = (TextView) v.findViewById(R.id.temp2);
        showWeather();
        return v;
    }

    public void showWeather() {
        WeatherInfo weatherInfo = WeatherActivity.weatherIdWeatherInfoMap.get(weatherId);
        if (weatherInfo == null) {
            Log.e(TAG, "showWeather: 没有天气可以显示");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        publishTimeTv.setText("今天" + weatherInfo.getPtime() + "发布");
        currentDateTv.setText(sdf.format(new Date()));
        weatherDespTv.setText(weatherInfo.getWeather());
        temp1Tv.setText(weatherInfo.getTemp1());
        temp2Tv.setText(weatherInfo.getTemp2());
    }
}
