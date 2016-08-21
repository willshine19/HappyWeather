package com.bupt.sang.happyweather.network.data;

import java.util.List;

/**
 * Created by sangyaohui on 16/8/21.
 */
public final class NowResponse {
    public List<Result> results;


    public static class Result {
        public Location location;
        public Now now;
    }

    public static class Now {
        public String text;
        public String code;
        public String temperature;
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
      "now": {
        "text": "晴",
        "code": "0",
        "temperature": "32"
      },
      "last_update": "2016-08-21T16:05:00+08:00"
    }
  ]
}
 */