package com.example.kimsuki_naver_ai.Receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.kimsuki_naver_ai.Service.MyService;

public class MyReceiver extends BroadcastReceiver {

    private final static String TAG = "free_call";
    private AudioManager audioManager;

    // 전화번호
    private static String phoneNumber = "";

    // incoming 수신 플래그
    private static boolean incomingFlag = false;

    @Override
    public void onReceive(final Context context, Intent intent) {

        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // TODO 전화를 걸었을 떄이다. (안드로이드 낮은 버전에서는 여기로 들어온다)
            this.incomingFlag = false;
            this.phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }
        // 폰 상태 체크
        processPhoneState(intent, context);
    }


    private void processPhoneState(Intent intent, Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                // TODO 전화를 왔을때이다. (벨이 울릴때)
                this.incomingFlag = true;
                this.phoneNumber = intent.getStringExtra("incoming_number");
                Toast.makeText(context, "벨이 울리고 있다. " + phoneNumber, Toast.LENGTH_SHORT).show();
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (this.incomingFlag) {
                    // TODO 전화가 왔을때이다. (통화 시작)
                } else {
                    // TODO 안드로이드 8.0 으로 테스트했을때는 ACTION_NEW_OUTGOING_CALL 거치지 않고, 이쪽으로 바로 온다.
                    this.phoneNumber = intent.getStringExtra("incoming_number");
                }
                if (this.incomingFlag) {
                    // TODO 전화가 왔고, 통화를 시작했을때 그에 맞는 프로세스를 실행한다.

                    Toast.makeText(context, "전화가 와서 받았음 " + phoneNumber, Toast.LENGTH_SHORT).show();
                    // 서비스 시작하기
//                    Log.d("test", "리시버-서비스 시작버튼클릭");
//                    Intent intent2 = new Intent(
//                            context,//현재제어권자
//                            MyService.class); // 이동할 컴포넌트
//                    intent2.putExtra("number",phoneNumber);
//                    context.startService(intent2); // 서비스 시작
                } else {
                    // TODO 전화를 했을때 그에 맞는 프로세스를 실행한다.
                    Toast.makeText(context, "전화 했음 " + phoneNumber, Toast.LENGTH_SHORT).show();
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (this.incomingFlag) {
                    // TODO 전화가 왔을때이다. (전화를 끊었을 경우)
                    Toast.makeText(context, "전화가 왔고, 끊었음 " + phoneNumber, Toast.LENGTH_SHORT).show();
                    // 서비스 종료하기
//                    Log.d("test", "리시버-서비스 종료버튼클릭");
//                    Intent intent2 = new Intent(
//                            context,//현재제어권자
//                            MyService.class); // 이동할 컴포넌트
//                    context.stopService(intent2); // 서비스 종료
                } else {
                    // TODO 전화를 걸었을 떄이다. (전화를 끊었을 경우)
                    Toast.makeText(context, "전화를 했고, 끊었음 " + phoneNumber, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}