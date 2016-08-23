package com.bupt.sang.happyweather.activity;

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
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
        publishTime.setText("正在加载");
        updateWeather();
        return root;
    }


    private void updateWeather() {
        Observable.fromCallable(new Callable<DailyResponse>() {
                                    @Override
                                    public DailyResponse call() throws Exception {
                                        Response<DailyResponse> response = ApiClient.getInstance().getDaily(cityName).execute();
                                        return response.body();
                                    }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DailyResponse>() {
                    @Override
                    public void call(DailyResponse response) {
                        WeatherInfo weatherInfo = new WeatherInfo(response);
                        bind(weatherInfo);

                    }
                });
    }

    private void updateRetrofit() {
        ApiClient.getInstance().getDaily(cityName).enqueue(new Callback<DailyResponse>() {
            @Override
            public void onResponse(Call<DailyResponse> call, Response<DailyResponse> response) {
                WeatherInfo weatherInfo = new WeatherInfo(response.body());
                bind(weatherInfo);
            }

            @Override
            public void onFailure(Call<DailyResponse> call, Throwable t) {

            }
        });
    }

    private void bind(WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            Log.e(TAG, "bind: 没有天气可以显示");
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
