package com.konka.kkhotkey.receiver;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.konka.kkhotkey.services.HotKeyService;

public class HotKeyReceiver extends BroadcastReceiver {

    private final String HOT_KEY_UP = "com.konka.android.intent.action.hot.key.up";
    private final String HOT_KEY_DOWN = "com.konka.android.intent.action.hot.key.down";
    private final String HOT_KEY_LONG_PRESS = "com.konka.android.intent.action.hot.key.long.press";
    private static final String TAG = "HotKeyReceiver";
    private static long mDownTime;
    private static boolean isUp = true;
    private Instrumentation mInstrumentation;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        boolean showInput = intent.getBooleanExtra("showInput", false);
        boolean needConversion = intent.getBooleanExtra("needConversion", true);
        if (TextUtils.equals(HOT_KEY_DOWN, action) && isUp) {
            mDownTime = System.currentTimeMillis();
            isUp = false;
        } else if (TextUtils.equals(HOT_KEY_UP, action)) {
            isUp = true;
        } else if (TextUtils.equals(HOT_KEY_LONG_PRESS, action)) {
            mDownTime = -1;
            if (showInput) {
                if (mInstrumentation == null) {
                    mInstrumentation = new Instrumentation();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
                    }
                }).start();
            } else {
                Intent longPressIntent = new Intent(context, HotKeyService.class);
                longPressIntent.putExtra("needConversion", needConversion);
                longPressIntent.putExtra("isShortPress", false);
                context.startService(longPressIntent);
            }
        }
        if (mDownTime > 0 && isUp) {
            if (showInput) {
                if (mInstrumentation == null) {
                    mInstrumentation = new Instrumentation();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
                    }
                }).start();
            } else {
                Intent shortPressIntent = new Intent(context, HotKeyService.class);
                shortPressIntent.putExtra("needConversion", needConversion);
                shortPressIntent.putExtra("isShortPress", true);
                context.startService(shortPressIntent);
            }

        }
    }
}
