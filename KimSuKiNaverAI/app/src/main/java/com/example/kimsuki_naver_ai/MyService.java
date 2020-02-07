package com.example.kimsuki_naver_ai;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private int count = 0;
    private Timer T = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        // 서비스 초기설정 (실행되고있었으면 실행되지 않음)
        Log.d("test", "서비스의 onCreate");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("test", "서비스의 onStartCommand");

        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.e("service counter start: ", String.valueOf(count));
                count++;
            }
        }, 1000, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        T.cancel();
        Log.d("test", "서비스의 onDestroy");
    }
}