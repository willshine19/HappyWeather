package com.bupt.sang.happyweather.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.sang.happyweather.R;
import com.bupt.sang.happyweather.model.WeatherInfo;
import com.bupt.sang.happyweather.util.RefreshableView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by sangyaohui on 16/8/23.
 */
public class WeatherScreen {

    private static final String TAG = "WeatherScreen";
    @Bind(R.id.syh_viewpager)
    ViewPager viewPager;
    @Bind(R.id.refreshable_view)
    RefreshableView refreshableView;
    @Bind(R.id.city_name)
    TextView cityNameTV;
    @Bind(R.id.left_drawer)
    ListView mDrawList;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    FragmentManager fragmentManager;
    BaseAdapter sideListAdapter;
    MainPagerAdapter pagerAdapter;
    List<String> cityNames = new ArrayList<>();
    private WeatherActivity activity;

    public PublishSubject<Integer> removeWeatherEvent = PublishSubject.create();
    public PublishSubject<Void> clickHomeEvent = PublishSubject.create();
    public PublishSubject<Void> addCityEvent = PublishSubject.create();
    public PublishSubject<Integer> selectCityEvent = PublishSubject.create();

    public WeatherScreen(WeatherActivity activity, FragmentManager fm) {
        this.activity = activity;
        this.fragmentManager = fm;
        ButterKnife.bind(this, activity);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
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
        initViews();

        clickHomeEvent.subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                openSlideMenuAction();
            }
        });
        selectCityEvent.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                changeCityAction(integer);
            }
        });
    }

    public void initViews() {
        cityNameTV.setText(cityNames.get(0));

        pagerAdapter = new MainPagerAdapter(fragmentManager, cityNames);

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
                LayoutInflater inflater = LayoutInflater.from(activity);
                view = inflater.inflate(R.layout.list_item, viewGroup, false);
                TextView tv = (TextView) view.findViewById(R.id.list_item_city_name);
                tv.setText(cityNames.get(i));
                view.findViewById(R.id.list_item_delete).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        removeWeatherEvent.onNext(i);
                    }
                });
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectCityEvent.onNext(i);
                    }
                });
                return view;
            }
        };
        mDrawList.setAdapter(sideListAdapter);

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

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                String cityName = pagerAdapter.cityNames.get(position);
                cityNameTV.setText(cityName);
                // 会卡
                // TODO: 16/8/23 info is null
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick({R.id.button_home, R.id.add_city})
    void clickButtons(View v) {
        switch (v.getId()) {
            case R.id.button_home:
                clickHomeEvent.onNext(null);
                break;
            case R.id.add_city:
                addCityEvent.onNext(null);
                break;
            default:
                break;
        }
    }

    private void openSlideMenuAction() {
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void changeCityAction(int position) {
        viewPager.setCurrentItem(position);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
}
