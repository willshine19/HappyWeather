package com.bupt.sang.happyweather.activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bupt.sang.happyweather.app.AppController;
import com.bupt.sang.happyweather.model.WeatherGson;
import com.bupt.sang.happyweather.model.WeatherInfo;
import com.bupt.sang.happyweather.service.ForegroundService;
import com.bupt.sang.happyweather.util.RefreshableView;
import com.bupt.sang.happyweather.util.StringUTF8Request;
import com.bupt.sang.happyweather.util.Utility;
import com.bupt.sang.happyweather.R;
import com.google.gson.Gson;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeatherActivity extends FragmentActivity implements OnClickListener, ViewPager.OnPageChangeListener{

	private static boolean hasShowAbout = false;
	private static final String TAG = "[syh]WeatherActivity";
	private ViewPager viewPager;
	private PagerAdapter fragmentAdapter;
	private RefreshableView refreshableView;
	/**
	 * 一组View，将会被添加到ViewPager中
	 */
	private List<View> viewList;
	private List<String> weatherIdList =  new ArrayList<String>(); // 待显示的所有天气id
	private Map<String, View> weatherIdViewMap;
	public static Map<String, WeatherInfo> weatherIdWeatherInfoMap = new HashMap<>();

	private TextView cityNameTV;
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	private Button addCity;
	private boolean loadOncd = false;

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO: 2016/5/9
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent: start");
		String countyCode = intent.getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			queryWeatherCode(countyCode);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		initWeatherIdList();
//		weatherIdViewMap = initMap();
//		viewList = initList(weatherIdList.size());
		initViews();

		String weatherCode = getIntent().getStringExtra("weather_code");
		if (!TextUtils.isEmpty(weatherCode)) {
//			queryWeatherInfo(weatherCode);
			Log.e(TAG, "onCreate: weatherCode in intent");
		}

		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// 有县级代号时就去查询天气
//			publishText.setText("同步中...");
//			weatherInfoLayout.setVisibility(View.INVISIBLE);
//			cityNameTV.setVisibility(View.INVISIBLE);
			weatherIdList.add(countyId2WeatherId(countyCode));
			queryWeatherCode(countyCode);
		}

		loadWeather();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
		if (!prefs.getBoolean("dontShow", false) && !hasShowAbout) {
			showAboutDialog();
			hasShowAbout = true;
		}
	}

	/**
	 * 开始加载所有天气
	 */
	private void loadWeather() {
		for (String weatherId : weatherIdList) {
			queryWeatherInfo(weatherId);
		}
	}

	private void initViews() {
		cityNameTV = (TextView) findViewById(R.id.city_name);
		cityNameTV.setText("北京"); // TODO: 2016/5/11

		switchCity = (Button) findViewById(R.id.switch_city);
		switchCity.setOnClickListener(this);

		addCity = (Button) findViewById(R.id.add_city);
		addCity.setOnClickListener(this);

		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
			public void onRefresh() {
				try {
					Thread.sleep(300);
//					refreshWeather();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
			}
		}, 0);

		viewPager = (ViewPager) findViewById(R.id.syh_viewpager);
		FragmentManager fm = getSupportFragmentManager();
		fragmentAdapter = new FragmentStatePagerAdapter(fm) {

			@Override
			public int getCount() {
				return weatherIdList.size();
			}

			@Override
			public android.support.v4.app.Fragment getItem(int position) {
				return WeatherFragment.newInstance(weatherIdList.get(position));
			}
		};
		viewPager.setAdapter(fragmentAdapter);
		viewPager.setOnPageChangeListener(this);
	}

	/**
	 * 天气列表中默认有北上广三个城市的天气id
	 */
	private void initWeatherIdList() {
		weatherIdList.add("101010100"); // 北京
		weatherIdList.add("101020100"); // 上海
		weatherIdList.add("101280101"); // 广州
	}

	/**
	 * 初始化viewList，一组View，这些View将被添加到ViewPager中，View的个数等于待显示天气的个数
	 * 暂时没用
	 * @param num
	 * @return
     */
	private List<View> initList(int num) {
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> list = new ArrayList<View>();
		for (int i = 0; i < num; i++) {
			final RefreshableView refreshableView = (RefreshableView) inflater.inflate(R.layout.viewpager_content, null);

			refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
				public void onRefresh() {
					try {
						Thread.sleep(500);
//					refreshWeather();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					refreshableView.finishRefreshing();
				}
			}, 0);
			list.add(refreshableView);
		}
		return list;
	}

	/**
	 * 初始化weatherIdViewMap
	 * 暂时没用
	 * @return
     */
	private Map<String, View> initMap() {
		Map<String, View> map = new HashMap<String, View>();
		for (int i = 0; i < weatherIdList.size(); i++) {
			map.put(weatherIdList.get(i), viewList.get(i));
		}
		return map;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.add_city:
			Intent intent2 = new Intent(this, ChooseAreaActivity.class);
			intent2.putExtra("from_weather_activity", true);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	// 更新天气
	private void refreshWeather() {
		// TODO: 2016/5/11
//		publishText.setText("同步中...");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		if (!TextUtils.isEmpty(weatherCode)) {
			queryWeatherInfo(weatherCode);
		}
	}

	/**
	 * 首次启动app时，显示一个About对话框
	 */
	private void showAboutDialog() {
		final CheckBox checkBox = new CheckBox(WeatherActivity.this);//勾选
		checkBox.setText("不在显示");//不再显示
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("关于");
		builder.setMessage("乐天天气是由sangyaohui开发的一款的开源天气预报软件，本软件主要作为学习和交流使用。");
		builder.setView(checkBox);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
				edit.putBoolean("dontShow", checkBox.isChecked());
				edit.commit();
			}
		});
		builder.setNegativeButton("关于作者", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String url = "http://blog.csdn.net/willshine19";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		builder.setNeutralButton("关于demo", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String url = "https://github.com/willshine19/HappyWeather";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		builder.show();
	}

	/**
	 * 查询县级代号所对应的天气代号。
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		StringRequest request = new StringUTF8Request(address, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (!TextUtils.isEmpty(response)) {
					// 从服务器返回的数据中解析出天气代号
					String[] array = response.split("\\|");
					if (array != null && array.length == 2) {
						String weatherCode = array[1];
						if (!weatherIdList.contains(weatherCode)) {
							weatherIdList.add(weatherCode);
						}
						queryWeatherInfo(weatherCode);
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(WeatherActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
			}
		});
		AppController.getInstance().addToRequestQueue(request);
	}

	/**
	 * 查询天气代号所对应的天气。
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		StringRequest request = new StringUTF8Request(address, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// 处理服务器返回的天气信息
//				Utility.handleWeatherResponse(WeatherActivity.this, response);
				Log.d(TAG, "下载天气" + response);
				Gson gson = new Gson();
				WeatherGson weatherGson = gson.fromJson(response, WeatherGson.class);
				addWeatherInfo(weatherGson.weatherinfo);
				showWeather();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(WeatherActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
			}
		});
		AppController.getInstance().addToRequestQueue(request);
	}
	
	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
	 */
	private void queryFromServer(final String address, final String type) {
		StringRequest request = new StringUTF8Request(address, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// 处理服务器返回的天气信息
//					Utility.handleWeatherResponse(WeatherActivity.this, response);
					Log.d(TAG, "下载天气" + response);
					Gson gson = new Gson();
					WeatherGson weatherGson = gson.fromJson(response, WeatherGson.class);
					addWeatherInfo(weatherGson.weatherinfo);
					showWeather();
//						showWeather(response);
					}
				}
			}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(WeatherActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
			}
		});
		AppController.getInstance().addToRequestQueue(request);

	}


	public void addWeatherInfo(WeatherInfo info) {
		if (weatherIdList.contains(info.getCityid())) {
			weatherIdWeatherInfoMap.put(info.getCityid(), info);
			Log.d(TAG, "addWeatherInfo: info name is " + info.getCity());
			Log.d(TAG, "addWeatherInfo: map大小" + weatherIdWeatherInfoMap.size() + " list大小" + weatherIdList.size());
		} else {
			Log.d(TAG, "addWeatherInfo: weatherIdList中没有这个天气" + info.getCity());
		}
	}

	/**
	 * 显示天气
	 * 暂时没用
	 * @param response JSON字符串
     */
	private void showWeather(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject json = jsonObject.getJSONObject("weatherinfo");
			String cityName = json.getString("city"); // TODO: 2016/5/9
			String weatherCode = json.getString("cityid");
			String temp1 = json.getString("temp1");
			String temp2 = json.getString("temp2");
			String weatherDesp = json.getString("weather");
			String publishTime = json.getString("ptime");

			RefreshableView refreshableView = (RefreshableView) weatherIdViewMap.get(weatherCode);
			if (refreshableView == null) {
				// TODO: 2016/5/9
			}
			RelativeLayout relativeLayout = (RelativeLayout) refreshableView.getChildAt(1);
			TextView publishTimeTv = (TextView) relativeLayout.getChildAt(0);
			LinearLayout linearLayout = (LinearLayout) relativeLayout.getChildAt(1);
			TextView currentDateTv = (TextView) linearLayout.getChildAt(0);
			TextView weatherDespTv = (TextView) linearLayout.getChildAt(1);
			LinearLayout linearLayoutHorizental = (LinearLayout) linearLayout.getChildAt(2);
			TextView cityNameTv = (TextView) linearLayout.getChildAt(3); // TODO: 2016/5/9  
			TextView temp1Tv = (TextView) linearLayoutHorizental.getChildAt(0);
			TextView temp2Tv = (TextView) linearLayoutHorizental.getChildAt(2);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
			publishTimeTv.setText("今天" + publishTime + "发布");
			currentDateTv.setText(sdf.format(new Date()));
			weatherDespTv.setText(weatherDesp);
			temp1Tv.setText(temp1);
			temp2Tv.setText(temp2);
			cityNameTv.setText(cityName);

			WeatherInfo weatherInfo = new WeatherInfo(json);
			weatherIdWeatherInfoMap.put(weatherCode, weatherInfo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从所有WeatherInfo中读取天气信息，并显示到界面上。
	 * 暂时没用
	 */
	private void showWeather() {
		viewPager.setAdapter(fragmentAdapter); // TODO: 2016/5/11 stupid
		// 第一次showWeather时打开一个前台服务
		if (weatherIdWeatherInfoMap.size() > 0 && !loadOncd) {
			WeatherInfo info = weatherIdWeatherInfoMap.get(weatherIdList.get(0));
			if (info == null) {
				return;
			}
			startForeGoundService(info);
			loadOncd = true;
		}
		// TODO: 2016/5/11 自动更新
//		Intent intent = new Intent(this, AutoUpdateService.class);
//		startService(intent);
	}

	private void startForeGoundService(final WeatherInfo info) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				Intent intent = new Intent(WeatherActivity.this, ForegroundService.class);
				intent.putExtra("city_name", info.getCity());
				intent.putExtra("temp1", info.getTemp1());
				intent.putExtra("temp2", info.getTemp2());
				intent.putExtra("weather_desp", info.getWeather());
				intent.putExtra("publish_time", info.getPtime());
				intent.putExtra("weather_code", info.getCityid());
				startService(intent);
			}
		}.start();
	}

	private String countyId2WeatherId(String str) {
		// TODO: 2016/5/9  讲一个县的id转为一个天气的id
		return new String("todo");
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		String weatherId = weatherIdList.get(viewPager.getCurrentItem());
		WeatherInfo info = weatherIdWeatherInfoMap.get(weatherId);
		if (info == null) {
			Log.e(TAG, "onPageSelected: 无法显示天气");
		}
		if (cityNameTV != null && info != null) {
			cityNameTV.setText(info.getCity());
		}
		// TODO: 2016/5/14 会卡 
//		startForeGoundService(info); 
	}

	@Override
	public void onPageScrollStateChanged(int state) {}
}