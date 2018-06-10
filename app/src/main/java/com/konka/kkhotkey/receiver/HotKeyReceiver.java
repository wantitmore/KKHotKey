package com.konka.kkhotkey.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.konka.kkhotkey.services.HotKeyService;

public class HotKeyReceiver extends BroadcastReceiver {

    private final String HOT_KEY_UP = "com.konka.android.intent.action.hot.key.up";
    private final String HOT_KEY_DOWN = "com.konka.android.intent.action.hot.key.down";
    private final String HOT_KEY_LONG_PRESS = "com.konka.android.intent.action.hot.key.long.press";
    private static final String TAG = "HotKeyReceiver";
    private static long mDownTime;
    private static boolean isUp = true;
    private static long mUpTime;
    private static final long LONG_PRESS_INTEREVAL  = 100;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        boolean needConversion = intent.getBooleanExtra("needConversion", true);
        Log.d(TAG, "onReceive: needConversion " + needConversion);
        if (TextUtils.equals(HOT_KEY_DOWN, action) && isUp) {
            mDownTime = System.currentTimeMillis();
            isUp = false;
        } else if (TextUtils.equals(HOT_KEY_UP, action)) {
            mUpTime = System.currentTimeMillis();
            isUp = true;
        }
        Log.d(TAG, "onReceive: =======  " + (mUpTime - mDownTime));
        if (mUpTime > 0 && mUpTime - mDownTime < LONG_PRESS_INTEREVAL && isUp) {
            Log.d(TAG, "onReceive: 按键短按");
            Intent shortPressIntent = new Intent(context, HotKeyService.class);
            shortPressIntent.putExtra("needConversion", needConversion);
            shortPressIntent.putExtra("isShortPress", true);
            context.startService(shortPressIntent);
        } else if (mUpTime - mDownTime >= LONG_PRESS_INTEREVAL){
            Log.d(TAG, "onReceive: 按键长按");
            Intent longPressIntent = new Intent(context, HotKeyService.class);
            longPressIntent.putExtra("needConversion", needConversion);
            longPressIntent.putExtra("isShortPress", false);
            context.startService(longPressIntent);
        }

    }
}
