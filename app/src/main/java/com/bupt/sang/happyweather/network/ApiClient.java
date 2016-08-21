package com.bupt.sang.happyweather.network;

import com.bupt.sang.happyweather.Constants;
import com.bupt.sang.happyweather.network.data.DailyResponse;
import com.bupt.sang.happyweather.network.data.NowResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;

/**
 * Created by sangyaohui on 16/8/21.
 */
public class ApiClient {
    private NetworkApi networkApi;
    private static ApiClient sInstance = new ApiClient();
    
    public static ApiClient getInstance() {
        return sInstance;
    }
    
    private ApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.thinkpage.cn/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        networkApi = retrofit.create(NetworkApi.class);
    }
    

    public Call<NowResponse> getWeather(String location) {
        return networkApi.getWeather(Constants.KEY, location, Constants.LANGUAGE, Constants.UNIT_TEMPRATURE);
    }

    public Call<DailyResponse> getDaily(@Query("key") String key, @Query("location") String location, @Query("languaage") String language, @Query("unit") String unit, @Query("shart") int start, @Query("days") int days) {
        return networkApi.getDaily(Constants.KEY, location, Constants.LANGUAGE, Constants.UNIT_TEMPRATURE, 0, 5);
    }
}
