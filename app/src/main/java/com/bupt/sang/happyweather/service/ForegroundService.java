package com.bupt.sang.happyweather.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.bupt.sang.happyweather.R;
import com.bupt.sang.happyweather.activity.WeatherActivity;


/**
 * Created by sang on 16-4-17.
 */
public class ForegroundService extends Service{
    private static final String TAG = "syh";
    private String mWeatherCode;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: 开始service");
        stopForeground(true);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.launcher);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.notification_weather_info, intent.getStringExtra("weather_desp"));
        remoteViews.setTextViewText(R.id.notification_weather_min_temp, intent.getStringExtra("temp1"));
        remoteViews.setTextViewText(R.id.notification_weather_max_temp, intent.getStringExtra("temp2"));
        remoteViews.setTextViewText(R.id.notification_weather_city, intent.getStringExtra("city_name"));
        remoteViews.setTextViewText(R.id.notification_weather_time, "今天" + intent.getStringExtra("publish_time") + "发布");
        mWeatherCode = intent.getStringExtra("weather_code");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContent(remoteViews)
                .setOngoing(true)
                .setSmallIcon(R.drawable.launcher)
                .setLargeIcon(icon);
        Intent resultIntent = new Intent(this, WeatherActivity.class);
        resultIntent.putExtra("weather_code", mWeatherCode);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(WeatherActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        Notification notification = builder.build();
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        stopForeground(true);
    }
}
