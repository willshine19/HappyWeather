package com.bupt.sang.happyweather.network;

import com.bupt.sang.happyweather.network.data.DailyResponse;
import com.bupt.sang.happyweather.network.data.NowResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sangyaohui on 16/8/21.
 */
public interface NetworkApi {
    @GET("weather/now.json?key=hiqlnyhnkxfvcok1&location=北京&language=zh-Hans&unit=c")
    Call<NowResponse> getWeather();

    @GET("weather/now.json")
    Call<NowResponse> getWeather(@Query("key") String key,
                                 @Query("location") String location,
                                 @Query("language") String language,
                                 @Query("unit") String unit);

//    https://api.thinkpage.cn/v3/weather/daily.json?key=hiqlnyhnkxfvcok1&location=beijing&language=zh-Hans&unit=c&start=0&days=5
    @GET("weather/daily.json")
    Call<DailyResponse> getDaily(@Query("key") String key,
                                 @Query("location") String location,
                                 @Query("languaage") String language,
                                 @Query("unit") String unit,
                                 @Query("shart") int start,
                                 @Query("days") int days);
}

