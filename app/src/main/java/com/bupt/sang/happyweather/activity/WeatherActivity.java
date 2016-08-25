package com.bupt.sang.happyweather.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bupt.sang.happyweather.R;
import com.bupt.sang.happyweather.model.WeatherInfo;
import com.bupt.sang.happyweather.network.ApiClient;
import com.bupt.sang.happyweather.network.data.DailyResponse;

import java.util.HashMap;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

public class WeatherActivity extends AppCompatActivity {

	private static final String TAG = "WeatherActivity";
	private static boolean hasShowAbout = false;
	private SharedPreferences preferences;
	public WeatherPresenter presenter;
	public WeatherScreen screen;

	// 保存已下载的天气信息
	public HashMap<String, WeatherInfo> weatherMap = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		ButterKnife.bind(this);
		FragmentManager fm = getSupportFragmentManager();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		presenter = new WeatherPresenter(this);
		screen = new WeatherScreen(this, fm);

		if (!preferences.getBoolean("dontShow", false) && !hasShowAbout) {
			showAboutDialog();
			hasShowAbout = true;
		}

		presenter.startForeGroundService();

		screen.addCityEvent.subscribe(new Action1<Void>() {
			@Override
			public void call(Void aVoid) {
				startChooseWeatherActivity();
			}
		});
		screen.removeWeatherEvent.subscribe(new Action1<Integer>() {
			@Override
			public void call(Integer position) {
				removeWeather(position);
			}
		});
		screen.refreshWeatherEvent.subscribe(new Action1<Void>() {
			@Override
			public void call(Void aVoid) {
				refreshWeather();
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent: start");
		String cityName = intent.getStringExtra("city_name");
		if (!TextUtils.isEmpty(cityName) && !screen.cityNames.contains(cityName)) {
			screen.cityNames.add(cityName);
			screen.sideListAdapter.notifyDataSetChanged();
			screen.pagerAdapter.updateCities(screen.cityNames);
			if (screen.refreshableView.getVisibility() == View.GONE) {
				screen.viewPager.setAdapter(screen.pagerAdapter); // 这一步很关键，强制刷新adapter
				screen.refreshableView.setVisibility(View.VISIBLE);
			}
			if (screen.cityNames.size() == 1) {
				screen.cityNameTV.setText(screen.cityNames.get(0));
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("weather_num", screen.cityNames.size());
		for (int i = 0; i < screen.cityNames.size(); i++) {
			editor.putString("weather" + i, screen.cityNames.get(i));
		}
		editor.apply();
	}

	/**
	 * 刷新所有天气
	 */
	private void refreshWeather() {
		weatherMap.clear();
		for (String cityName : screen.cityNames) {
			ApiClient.getInstance().getDaily(cityName).enqueue(new Callback<DailyResponse>() {
				@Override
				public void onResponse(Call<DailyResponse> call, Response<DailyResponse> response) {
					WeatherInfo info = new WeatherInfo(response.body());
					weatherMap.put(info.getCity(), info);
					if (weatherMap.size() == screen.cityNames.size()) {
						// TODO: 16/8/24 会自动回到第一页
						int tempPosition = screen.viewPager.getCurrentItem();
						screen.viewPager.setAdapter(screen.pagerAdapter);
						screen.viewPager.setCurrentItem(tempPosition);
						screen.refreshableView.finishRefreshing();
					}
				}

				@Override
				public void onFailure(Call<DailyResponse> call, Throwable t) {
					screen.refreshableView.finishRefreshing();

					// TODO: 16/8/24 会提示很多次
					Toast.makeText(WeatherActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	private void removeWeather(int position) {
		if (screen.cityNames.size() == 1) {
			screen.refreshableView.setVisibility(View.GONE);
			screen.cityNameTV.setText("请选择城市");
		}
		screen.cityNames.remove(position);
		screen.sideListAdapter.notifyDataSetChanged();
		screen.pagerAdapter.updateCities(screen.cityNames);
		if (screen.cityNames.size() == 0) {
			return;
		}
		if (screen.viewPager.getCurrentItem() == position) {
			int newPosition = position == screen.cityNames.size() ? position - 1 : position;
			screen.viewPager.setCurrentItem(newPosition);
			screen.cityNameTV.setText(screen.cityNames.get(newPosition));
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
				edit.apply();
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
	 * 从所有WeatherInfo中读取天气信息，并显示到界面上。
	 * 暂时没用
	 */
//	private void showWeather() {
//
//		updateListAdapter();
//		int temp = viewPager.getCurrentItem();
//		viewPager.setAdapter(pagerAdapter); // TODO: 2016/5/11 stupid
//		viewPager.setCurrentItem(temp);
//		// 第一次showWeather时打开一个前台服务
//		if (weatherIdWeatherInfoMap.size() > 0 && !loadOnce) {
//			WeatherInfo info = weatherIdWeatherInfoMap.get(weatherIdList.get(0));
//			if (info == null) {
//				return;
//			}
//			presenter.startForeGroundService(info);
//			loadOnce = true;
//		}
//		if (cityNameList.size() > 0) {
//			cityNameTV.setText(cityNameList.get(viewPager.getCurrentItem()));
//		} else {
//			cityNameTV.setText("请添加天气");
//		}
//		// TODO: 2016/5/11 自动更新
////		Intent intent = new Intent(this, AutoUpdateService.class);
////		startService(intent);
//	}


	public void startChooseWeatherActivity() {
		Intent intent2 = new Intent(this, ChooseAreaActivity.class);
		intent2.putExtra("from_weather_activity", true);
		startActivity(intent2);
	}

}