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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeatherActivity extends AppCompatActivity {

	private static final String TAG = "WeatherActivity";
	private static boolean hasShowAbout = false;
	private SharedPreferences preferences;
	private WeatherPresenter presenter;

	private List<String> cityNames = new ArrayList<>();

	private BaseAdapter sideListAdapter;
	private MainPagerAdapter pagerAdapter;

	@Bind(R.id.syh_viewpager)
	ViewPager viewPager;
//	@Bind(R.id.refreshable_view)
//	RefreshableView refreshableView;
	@Bind(R.id.city_name)
	TextView cityNameTV;
	@Bind(R.id.left_drawer)
	ListView mDrawList;
	@Bind(R.id.drawer_layout)
	DrawerLayout mDrawerLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		ButterKnife.bind(this);

;

		presenter = new WeatherPresenter(this);

		initViews();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
		if (!prefs.getBoolean("dontShow", false) && !hasShowAbout) {
			showAboutDialog();
			hasShowAbout = true;
		}
	}

	private void initViews() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int weatherNum = preferences.getInt("weather_num", 0);
		for (int i = 0; i < weatherNum; i++) {
			String cityName = preferences.getString("weather" + i, null);
			if (cityName != null) cityNames.add(cityName);
		}

		if (cityNames.isEmpty()) {
			cityNames.add("北京");
			cityNames.add("天津");
			cityNames.add("青岛");
		}

		cityNameTV.setText(cityNames.get(0));

//		refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
//			public void onRefresh() {
//				try {
//					Thread.sleep(300);
////					refreshWeather();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				refreshableView.finishRefreshing();
//			}
//		}, 0);

		// init ViewPager
		FragmentManager fm = getSupportFragmentManager();
		pagerAdapter = new MainPagerAdapter(fm, cityNames);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				String cityName = pagerAdapter.cityNames.get(position);
				cityNameTV.setText(cityName);
				// TODO: 2016/5/14 会卡
		//		startForeGoundService(info);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		// init ListView in DrawerLayout
		sideListAdapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return cityNames.size();
			}

			@Override
			public Object getItem(int i) {
				return cityNames.get(i);
			}

			@Override
			public long getItemId(int i) {
				return i;
			}

			@Override
			public View getView(final int i, View view, ViewGroup viewGroup) {
				LayoutInflater inflater = LayoutInflater.from(WeatherActivity.this);
				view = inflater.inflate(R.layout.list_item, viewGroup, false);
				TextView tv = (TextView) view.findViewById(R.id.list_item_city_name);
				tv.setText(cityNames.get(i));
				view.findViewById(R.id.list_item_delete).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						removeWeather(i);
					}
				});
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
							viewPager.setCurrentItem(i);
							mDrawerLayout.closeDrawer(GravityCompat.START);
					}
				});
				return view;
			}
		};
		mDrawList.setAdapter(sideListAdapter);



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
		if (!TextUtils.isEmpty(cityName) && !cityNames.contains(cityName)) {
			cityNames.add(cityName);
			pagerAdapter.updateCities(cityNames);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("weather_num", cityNames.size());
		for (int i = 0; i < cityNames.size(); i++) {
			editor.putString("weather" + i, cityNames.get(i));
		}
		editor.apply();
	}

	@OnClick({R.id.switch_city, R.id.add_city})
	void clickButtons(View v) {
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

	/**
	 * 刷新当前天气
	 */
	private void refreshWeather() {
	}

	private void removeWeather(int positon) {
		cityNames.remove(positon);
		sideListAdapter.notifyDataSetChanged();
		pagerAdapter.updateCities(cityNames);
		if (viewPager.getCurrentItem() == positon) {
			int newPosition = positon == cityNames.size() ? positon - 1 : positon;
			viewPager.setCurrentItem(newPosition);
			cityNameTV.setText(cityNames.get(newPosition));
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



}