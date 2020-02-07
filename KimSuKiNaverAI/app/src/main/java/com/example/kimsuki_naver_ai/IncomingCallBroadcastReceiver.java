package com.example.kimsuki_naver_ai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "PHONE STATE";
    private static String mLastState;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

//    TelephonyManager.EXTRA_STATE_IDLE: 통화종료 혹은 통화벨 종료
//
//    TelephonyManager.EXTRA_STATE_RINGING: 통화벨 울리는중
//
//    TelephonyManager.EXTRA_STATE_OFFHOOK: 통화중
//
//
//
//    출처: https://gun0912.tistory.com/46 [박상권의 삽질블로그]
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "onReceive()"); /** * http://mmarvick.github.io/blog/blog/lollipop-multiple-broadcastreceiver-call-state/ * 2번 호출되는 문제 해결 */
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state.equals(mLastState)) {
            return;
        } else {
            mLastState = state;
        }

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            final String phone_number = PhoneNumberUtils.formatNumber(incomingNumber);

            Log.e("phone_call_state",phone_number);
//            Intent serviceIntent = new Intent(context, CallingService.class);
//            serviceIntent.putExtra(CallingService.EXTRA_CALL_NUMBER, phone_number);
//            context.startService(serviceIntent);
        }
    }
}

