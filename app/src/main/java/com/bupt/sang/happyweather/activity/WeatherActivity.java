package com.bupt.sang.happyweather.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.sang.happyweather.R;
import com.bupt.sang.happyweather.util.RefreshableView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class WeatherActivity extends AppCompatActivity {

	private static final String TAG = "WeatherActivity";
	private static boolean hasShowAbout = false;
	private SharedPreferences preferences;
	private WeatherPresenter presenter;
	private WeatherScreen screen;

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

		initViews();


		if (!preferences.getBoolean("dontShow", false) && !hasShowAbout) {
			showAboutDialog();
			hasShowAbout = true;
		}

		screen.addCityEvent.subscribe(new Action1<Void>() {
			@Override
			public void call(Void aVoid) {
				startChooseWeatherActivity();
			}
		});
	}

	private void initViews() {
//		toolbar = (Toolbar) findViewById(R.id.toolbar);
////		setSupportActionBar(toolbar);
//		toolbar.setTitle("北京");
//
//		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
//		mDrawerLayout.setDrawerListener(toggle);
//		toggle.syncState();
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent: start");
		String cityName = intent.getStringExtra("city_name");
		if (!TextUtils.isEmpty(cityName) && !screen.cityNames.contains(cityName)) {
			screen.cityNames.add(cityName);
			screen.pagerAdapter.updateCities(screen.cityNames);
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
	 * 刷新当前天气
	 */
	private void refreshWeather() {
	}

	private void removeWeather(int positon) {
		screen.cityNames.remove(positon);
		screen.sideListAdapter.notifyDataSetChanged();
		screen.pagerAdapter.updateCities(screen.cityNames);
		if (screen.viewPager.getCurrentItem() == positon) {
			int newPosition = positon == screen.cityNames.size() ? positon - 1 : positon;
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
//			presenter.startForeGoundService(info);
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

	/**
	 * 该adapter是左边栏里面的ListView的
	 */
//	private void updateListAdapter() {
//		cityNames.clear();
//		for (int i = 0; i < cityNames.size(); i++) {
//			WeatherInfo info = weatherIdWeatherInfoMap.get(weatherIdList.get(i));
//			if (info != null) {
//				cityNameList.add(info.getCity());
//			} else {
//				cityNameList.add(weatherIdList.get(i));
//			}
//		}
//		sideListAdapter.notifyDataSetChanged();
//	}

	public void startChooseWeatherActivity() {
		Intent intent2 = new Intent(this, ChooseAreaActivity.class);
		intent2.putExtra("from_weather_activity", true);
		startActivity(intent2);
	}

}