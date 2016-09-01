package com.bupt.sang.happyweather.floating;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bupt.sang.happyweather.R;
import com.bupt.sang.happyweather.activity.WeatherActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FloatWindowBigView extends LinearLayout {

    private int viewWidth;
    private int viewHeight;
    private Context context;

    public FloatWindowBigView(final Context context) {
        super(context);
        this.context = context;
        View root = LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        ButterKnife.bind(this, root);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    @OnClick(R.id.enter_app)
    void onClickEnter() {
        // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
//        MyWindowManager.removeBigWindow(context);
//        MyWindowManager.removeSmallWindow(context);
//        Intent intent = new Intent(getContext(), FloatWindowService.class);
//        context.stopService(intent);
        openApp();
    }

    @OnClick(R.id.back)
    void onClickBack() {
        // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
        MyWindowManager.removeBigWindow(context);
        MyWindowManager.createSmallWindow(context);
    }

    private void openApp() {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
