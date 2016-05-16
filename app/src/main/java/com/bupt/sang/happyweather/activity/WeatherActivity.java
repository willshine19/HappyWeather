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
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherActivity extends FragmentActivity implements OnClickListener, ViewPager.OnPageChangeListener{

	private static boolean hasShowAbout = false;
	private static final String TAG = "[syh]WeatherActivity";
	private ViewPager mViewPager;
	private PagerAdapter fragmentAdapter;
	private RefreshableView refreshableView;
	/**
	 * 一组View，将会被添加到ViewPager中
	 */
	private List<View> viewList;
	private List<String> weatherIdList =  new ArrayList<String>(); // 待显示的所有天气id
	private List<String> cityNameList = new ArrayList<String>();
	private Map<String, View> weatherIdViewMap;
	public static Map<String, WeatherInfo> weatherIdWeatherInfoMap = new HashMap<>();

	private TextView cityNameTV;
	private ListView mDrawList;
	private DrawerLayout mDrawerLayout;
	private boolean loadOncd = false;
	private BaseAdapter mListAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

//		weatherIdViewMap = initMap();
//		viewList = initList(weatherIdList.size());

		initWeatherIdList();
		initViews();
		loadWeather();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
		if (!prefs.getBoolean("dontShow", false) && !hasShowAbout) {
			showAboutDialog();
			hasShowAbout = true;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent: start");
		String countyCode = intent.getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			queryWeatherCode(countyCode);
		}
	}


	/**
	 * 天气列表中默认有北上广三个城市的天气id
	 */
	private void initWeatherIdList() {
		weatherIdList.add("101010100"); // 北京
		weatherIdList.add("101020100"); // 上海
		weatherIdList.add("101280101"); // 广州
	}

	private void initViews() {
		cityNameTV = (TextView) findViewById(R.id.city_name);
		cityNameTV.setText("北京"); // TODO: 2016/5/11

		findViewById(R.id.switch_city).setOnClickListener(this);;
		findViewById(R.id.add_city).setOnClickListener(this);

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

		mViewPager = (ViewPager) findViewById(R.id.syh_viewpager);
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
		mViewPager.setAdapter(fragmentAdapter);
		mViewPager.setOnPageChangeListener(this);

		mDrawList = (ListView) findViewById(R.id.left_drawer);
		mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, cityNameList);
		mDrawList.setAdapter(mListAdapter);
		mDrawList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				mViewPager.setCurrentItem(position);
				mDrawerLayout.closeDrawer(GravityCompat.START);
			}
		});

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	}


	/**
	 * 开始加载所有天气
	 */
	private void loadWeather() {
		for (String weatherId : weatherIdList) {
			queryWeatherInfo(weatherId);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
				mDrawerLayout.openDrawer(GravityCompat.START);
			}
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
	
	public void addWeatherInfo(WeatherInfo info) {
		if (info == null || TextUtils.isEmpty(info.getCityid())) {
			Log.e(TAG, "addWeatherInfo: 失败");
			return;
		}
		if (!weatherIdList.contains(info.getCityid())) {
			weatherIdList.add(info.getCityid());
		}
		weatherIdWeatherInfoMap.put(info.getCityid(), info);
		if (weatherIdList.size() == weatherIdWeatherInfoMap.size()) {
			Log.d(TAG, "addWeatherInfo: size = " + weatherIdList.size());
		} else {
			Log.e(TAG, "addWeatherInfo: size不等");
		}
	}


	/**
	 * 从所有WeatherInfo中读取天气信息，并显示到界面上。
	 * 暂时没用
	 */
	private void showWeather() {

		updateListAdapter();
		mViewPager.setAdapter(fragmentAdapter); // TODO: 2016/5/11 stupid
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

	private void updateListAdapter() {
		cityNameList.clear();
		for (int i = 0; i < weatherIdList.size(); i++) {
			WeatherInfo info = weatherIdWeatherInfoMap.get(weatherIdList.get(i));
			if (info != null) {
				cityNameList.add(info.getCity());
			}
		}
		mListAdapter.notifyDataSetChanged();
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
		String weatherId = weatherIdList.get(mViewPager.getCurrentItem());
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