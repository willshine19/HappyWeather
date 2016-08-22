package com.bupt.sang.happyweather.network.data;

import java.util.List;

/**
 * Created by sangyaohui on 16/8/21.
 */
public final class DailyResponse {
    public List<Result> results;

    public class Result {
        public Location location;
        public List<Daily> daily;
    }

    public class Daily {
        public String data;
        public String text_day;
        public String code_day;
        public String text_night;
        public String code_nigth;
        public String high;
        public String low;
        public String precip;
        public String wind_direction;
        public String wind_direction_degree;
        public String wind_speed;
        public String wind_scale;
    }
}


/*
{
    "results": [
        {
            "location": {
                "id": "WX4FBXXFKE4F",
                "name": "北京",
                "country": "CN",
                "path": "北京,北京,中国",
                "timezone": "Asia/Shanghai",
                "timezone_offset": "+08:00"
            },
            "daily": [
                {
                    "date": "2016-08-21",
                    "text_day": "晴",
                    "code_day": "0",
                    "text_night": "多云",
                    "code_night": "4",
                    "high": "32",
                    "low": "23",
                    "precip": "",
                    "wind_direction": "无持续风向",
                    "wind_direction_degree": "0",
                    "wind_speed": "10",
                    "wind_scale": "2"
                },
                {
                    "date": "2016-08-22",
                    "text_day": "多云",
                    "code_day": "4",
                    "text_night": "阴",
                    "code_night": "9",
                    "high": "31",
                    "low": "23",
                    "precip": "",
                    "wind_direction": "无持续风向",
                    "wind_direction_degree": "",
                    "wind_speed": "10",
                    "wind_scale": "2"
                },
                {
                    "date": "2016-08-23",
                    "text_day": "阵雨",
                    "code_day": "10",
                    "text_night": "阵雨",
                    "code_night": "10",
                    "high": "28",
                    "low": "23",
                    "precip": "",
                    "wind_direction": "无持续风向",
                    "wind_direction_degree": "",
                    "wind_speed": "10",
                    "wind_scale": "2"
                }
            ],
            "last_update": "2016-08-21T18:00:00+08:00"
        }
    ]
}
 */
