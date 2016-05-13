package com.bupt.sang.happyweather.util;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * 因为volley中的StringRequest会导致中文乱码。这个类是对StringRequest类的扩展，使其支持中文
 * Created by sang on 2016/5/13.
 */

public class StringUTF8Request extends StringRequest {

    public StringUTF8Request(String url, Response.Listener<String> listener,
                                Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String type = response.headers.get("Content-Type");
            if (type == null) {
                type = "charset=UTF-8";
                response.headers.put("Content-Type", type);
            } else if (!type.contains("UTF-8")) {
                type += ";" + "charset=UTF-8";
                response.headers.put("Content-Type", type);
            }
        } catch (Exception e) {
            // print stacktrace e.g.
        }
        return super.parseNetworkResponse(response);
    }

}
