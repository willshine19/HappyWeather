package com.bupt.sang.happyweather.util;

public interface HttpCallbackListener {

	void onFinish(String response);

	void onError(Exception e);

}
