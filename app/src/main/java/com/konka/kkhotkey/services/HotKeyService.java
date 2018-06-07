package com.konka.kkhotkey.services;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.konka.kkhotkey.R;

public class HotKeyService extends Service {

    private static final String TAG  = "HotKeyService";
    private static final String ACTION_KEYEVENT_HOTKEY = "com.konka.hotkeyservice.action.KEYDOWN";

    private HotKeyBinder mBind = new HotKeyBinder();
    private WindowManager mWindowManager;
    private int position;
    private ImageButton mVolumePlus;
    private ImageButton mVolumeMinus;
    private ImageButton mPower;
    private ImageButton mChannelMinus;
    private ImageButton mChannelPlus;
    private ImageButton mInput;
    private AudioManager audioManager;
    private int mVolume;
    private Button back;
    private View mView;

    public HotKeyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.gravity = Gravity.START | Gravity.BOTTOM;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = 430;
        layoutParams.height = 183;
        mView = LayoutInflater.from(this).inflate(R.layout.hot_key_layout, null);
        Button shortPress = mView.findViewById(R.id.short_press);
        Button longPress = mView.findViewById(R.id.long_press);
        mPower = mView.findViewById(R.id.imb_power);
        mVolumeMinus = mView.findViewById(R.id.imb_vol_minus);
        mVolumePlus = mView.findViewById(R.id.imb_vol_plus);
        mChannelMinus = mView.findViewById(R.id.imb_channel_minus);
        mChannelPlus = mView.findViewById(R.id.imb_channel_plus);
        mInput = mView.findViewById(R.id.imb_input);
        back = mView.findViewById(R.id.back);
        mPower.setScaleX(1.2f);
        mPower.setScaleY(1.2f);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mView != null) {
                    mWindowManager.removeView(mView);
                }
                stopSelf();
            }
        });
        shortPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: short press");

                position++;
                if (position == 6) {
                    position = 0;
                }
                switch (position) {
                    case 0 :
                        mPower.setScaleX(1.2f);
                        mPower.setScaleY(1.2f);
                        mVolumeMinus.setScaleX(1.0f);
                        mVolumeMinus.setScaleY(1.0f);
                        mVolumePlus.setScaleX(1.0f);
                        mVolumePlus.setScaleY(1.0f);
                        mChannelMinus.setScaleX(1.0f);
                        mChannelMinus.setScaleY(1.0f);
                        mChannelPlus.setScaleX(1.0f);
                        mChannelPlus.setScaleY(1.0f);
                        mInput.setScaleX(1.0f);
                        mInput.setScaleY(1.0f);
                        break;
                    case 1 :
                        mVolumeMinus.setScaleX(1.2f);
                        mVolumeMinus.setScaleY(1.2f);
                        mPower.setScaleX(1.0f);
                        mPower.setScaleY(1.0f);
                        mVolumePlus.setScaleX(1.0f);
                        mVolumePlus.setScaleY(1.0f);
                        mChannelMinus.setScaleX(1.0f);
                        mChannelMinus.setScaleY(1.0f);
                        mChannelPlus.setScaleX(1.0f);
                        mChannelPlus.setScaleY(1.0f);
                        mInput.setScaleX(1.0f);
                        mInput.setScaleY(1.0f);
                        break;
                    case 2 :
                        mVolumePlus.setScaleX(1.2f);
                        mVolumePlus.setScaleY(1.2f);
                        mPower.setScaleX(1.0f);
                        mPower.setScaleY(1.0f);
                        mVolumeMinus.setScaleX(1.0f);
                        mVolumeMinus.setScaleY(1.0f);
                        mChannelMinus.setScaleX(1.0f);
                        mChannelMinus.setScaleY(1.0f);
                        mChannelPlus.setScaleX(1.0f);
                        mChannelPlus.setScaleY(1.0f);
                        mInput.setScaleX(1.0f);
                        mInput.setScaleY(1.0f);
                        break;
                    case 3 :
                        mChannelMinus.setScaleX(1.2f);
                        mChannelMinus.setScaleY(1.2f);
                        mPower.setScaleX(1.0f);
                        mPower.setScaleY(1.0f);
                        mVolumeMinus.setScaleX(1.0f);
                        mVolumeMinus.setScaleY(1.0f);
                        mVolumePlus.setScaleX(1.0f);
                        mVolumePlus.setScaleY(1.0f);
                        mChannelPlus.setScaleX(1.0f);
                        mChannelPlus.setScaleY(1.0f);
                        mInput.setScaleX(1.0f);
                        mInput.setScaleY(1.0f);
                        break;
                    case 4 :
                        mChannelPlus.setScaleX(1.2f);
                        mChannelPlus.setScaleY(1.2f);
                        mPower.setScaleX(1.0f);
                        mPower.setScaleY(1.0f);
                        mVolumeMinus.setScaleX(1.0f);
                        mVolumeMinus.setScaleY(1.0f);
                        mVolumePlus.setScaleX(1.0f);
                        mVolumePlus.setScaleY(1.0f);
                        mChannelMinus.setScaleX(1.0f);
                        mChannelMinus.setScaleY(1.0f);
                        mInput.setScaleX(1.0f);
                        mInput.setScaleY(1.0f);
                        break;
                    case 5 :
                        mInput.setScaleX(1.2f);
                        mInput.setScaleY(1.2f);
                        mPower.setScaleX(1.0f);
                        mPower.setScaleY(1.0f);
                        mVolumeMinus.setScaleX(1.0f);
                        mVolumeMinus.setScaleY(1.0f);
                        mVolumePlus.setScaleX(1.0f);
                        mVolumePlus.setScaleY(1.0f);
                        mChannelMinus.setScaleX(1.0f);
                        mChannelMinus.setScaleY(1.0f);
                        mChannelPlus.setScaleX(1.0f);
                        mChannelPlus.setScaleY(1.0f);

                        Intent inputIntent = new Intent(ACTION_KEYEVENT_HOTKEY);
                        inputIntent.putExtra("keyevent", KeyEvent.ACTION_DOWN);
                        inputIntent.putExtra("keyCode", KeyEvent.KEYCODE_DPAD_RIGHT);
                        sendBroadcast(inputIntent);
                        break;
                }

            }
        });
        longPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: long press");
                switch (position) {
                    case 0 :
                        Log.d(TAG, "onClick: control power");
                        Intent i=new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                        i.putExtra("android.intent.extra.KEY_CONFIRM", false);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(i);
                        break;
                    case 1 :
                        Log.d(TAG, "onClick: volume --");
                        if (audioManager == null) {
                            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                        }
                        mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1;
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, AudioManager.FLAG_VIBRATE|AudioManager.FLAG_SHOW_UI);
                        break;
                    case 2 :
                        Log.d(TAG, "onClick: volume ++");
                        if (audioManager == null) {
                            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                        }
                        mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1;
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, AudioManager.FLAG_VIBRATE|AudioManager.FLAG_SHOW_UI);

                        break;
                    case 3 :
                        Log.d(TAG, "onClick: channel--");
                        Intent chDownIntent = new Intent(ACTION_KEYEVENT_HOTKEY);
                        chDownIntent.putExtra("keyevent", KeyEvent.ACTION_DOWN);
                        chDownIntent.putExtra("keyCode", KeyEvent.KEYCODE_CHANNEL_DOWN);
                        sendBroadcast(chDownIntent);
                        break;
                    case 4 :
                        Log.d(TAG, "onClick: channel++");
                        Intent chUpIntent = new Intent(ACTION_KEYEVENT_HOTKEY);
                        chUpIntent.putExtra("keyevent", KeyEvent.ACTION_DOWN);
                        chUpIntent.putExtra("keyCode", KeyEvent.KEYCODE_CHANNEL_UP);
                        sendBroadcast(chUpIntent);
                        break;
                    case 5 :
                        Log.d(TAG, "onClick: input");
                        Intent inputIntent = new Intent(ACTION_KEYEVENT_HOTKEY);
                        inputIntent.putExtra("keyevent", KeyEvent.ACTION_DOWN);
                        inputIntent.putExtra("keyCode", KeyEvent.KEYCODE_TV_INPUT);
                        sendBroadcast(inputIntent);
                        break;
                }
            }
        });
        mWindowManager.addView(mView, layoutParams);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class HotKeyBinder extends Binder {

    }
}
