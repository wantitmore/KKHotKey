package com.konka.kkhotkey.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konka.kkhotkey.R;
import com.mstar.android.tv.TvCommonManager;

public class HotKeyService extends Service {

    private static final String TAG  = "HotKeyService";
    private static final String ACTION_KEYEVENT_HOTKEY = "com.konka.hotkeyservice.action.KEYDOWN";

    private HotKeyBinder mBind = new HotKeyBinder();
    private WindowManager mWindowManager;
    private int mPosition = -1;
    private int mTotalSize = 6;
    private ImageButton mVolumePlus;
    private ImageButton mVolumeMinus;
    private ImageButton mPower;
    private ImageButton mChannelMinus;
    private ImageButton mChannelPlus;
    private ImageButton mInput;
    private AudioManager audioManager;
    private int mVolume;
    private View mView;
    private LinearLayout mChMinusLayout, mChPlusLayout, mInputLayout;
    private boolean mHideChannel;
    private CountDownTimer mDownTimer;
    private TextView mPowerTx, mVolMTx, mVolPTx, mChMTx, mChPTx, mInputTx;


    public HotKeyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        Log.d(TAG, "onCreate: inputs is " + inputSource);
        if (!(inputSource == TvCommonManager.INPUT_SOURCE_DTV
                || inputSource == TvCommonManager.INPUT_SOURCE_ATV)) {
            mHideChannel = true;
        }
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.START | Gravity.BOTTOM;
        layoutParams.x = 0;
        layoutParams.y = 20;
        layoutParams.width = 660;
        layoutParams.height = 366;
        mView = LayoutInflater.from(this).inflate(R.layout.hot_key_layout, null);
        mPower = mView.findViewById(R.id.imb_power);
        mVolumeMinus = mView.findViewById(R.id.imb_vol_minus);
        mVolumePlus = mView.findViewById(R.id.imb_vol_plus);
        mChannelMinus = mView.findViewById(R.id.imb_channel_minus);
        mChannelPlus = mView.findViewById(R.id.imb_channel_plus);
        mInput = mView.findViewById(R.id.imb_input);
        mChMinusLayout = mView.findViewById(R.id.ll_ch_minus);
        mChPlusLayout = mView.findViewById(R.id.ll_ch_plus);
        mInputLayout = mView.findViewById(R.id.ll_input);
        mPowerTx = mView.findViewById(R.id.power);
        mVolMTx = mView.findViewById(R.id.vol_m);
        mVolPTx = mView.findViewById(R.id.vol_p);
        mChMTx = mView.findViewById(R.id.ch_m);
        mChPTx = mView.findViewById(R.id.ch_p);
        mInputTx = mView.findViewById(R.id.input);
        mPower.setScaleX(1.2f);
        mPower.setScaleY(1.2f);
        mWindowManager.addView(mView, layoutParams);

    }

    private void removeView() {
        if (mView != null && mWindowManager != null) {
            mWindowManager.removeView(mView);
            mWindowManager = null;
        }
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mDownTimer != null) {
            mDownTimer.cancel();
        }
        boolean needConversion = intent.getBooleanExtra("needConversion", true);
        boolean isShortPress = intent.getBooleanExtra("isShortPress", true);
        if (needConversion) {
            mChMinusLayout.setVisibility(View.GONE);
            mChPlusLayout.setVisibility(View.GONE);
            mInputLayout.setVisibility(View.GONE);
            mTotalSize = 3;
        } else if (mHideChannel) {
            Log.d(TAG, "onStartCommand: ---");
            mChMinusLayout.setVisibility(View.GONE);
            mChPlusLayout.setVisibility(View.GONE);
            mInputLayout.setVisibility(View.VISIBLE);
            mTotalSize = 4;
        }
        if (isShortPress) {
            handleShortPress();
        } else {
            handleLongpress();
        }
        mDownTimer = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                removeView();
                mDownTimer = null;
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleLongpress() {
        switch (mPosition) {
            case 0 :
                Log.d(TAG, "onClick: control power");
                Intent i=new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                i.putExtra("android.intent.extra.KEY_CONFIRM", false);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                break;
            case 1 :
                if (audioManager == null) {
                    audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                }
                mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, AudioManager.FLAG_VIBRATE|AudioManager.FLAG_SHOW_UI);
                break;
            case 2 :
                if (audioManager == null) {
                    audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                }
                mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, AudioManager.FLAG_VIBRATE|AudioManager.FLAG_SHOW_UI);

                break;
            case 3 :
                Intent chDownIntent = new Intent(ACTION_KEYEVENT_HOTKEY);
                chDownIntent.putExtra("keyevent", KeyEvent.ACTION_DOWN);
                chDownIntent.putExtra("keyCode", KeyEvent.KEYCODE_CHANNEL_DOWN);
                sendBroadcast(chDownIntent);
                break;
            case 4 :
                Intent chUpIntent = new Intent(ACTION_KEYEVENT_HOTKEY);
                chUpIntent.putExtra("keyevent", KeyEvent.ACTION_DOWN);
                chUpIntent.putExtra("keyCode", KeyEvent.KEYCODE_CHANNEL_UP);
                sendBroadcast(chUpIntent);
                break;
            case 5 :
                Log.d(TAG, "handleLongpress:mDownTimer " + mDownTimer);
                if (mDownTimer != null) {
                    Log.d(TAG, "handleLongpress: mDownTimer==");
                    mDownTimer.cancel();
                    mDownTimer = null;
                }
                Intent inputIntent = new Intent(ACTION_KEYEVENT_HOTKEY);
                inputIntent.putExtra("keyevent", KeyEvent.ACTION_DOWN);
                inputIntent.putExtra("keyCode", KeyEvent.KEYCODE_TV_INPUT);
                sendBroadcast(inputIntent);
                removeView();
                break;
        }
    }

    private void handleShortPress() {
        mPosition++;
        if (mPosition == mTotalSize) {
            mPosition = 0;
        }
        Log.d(TAG, "handleShortPress: pos is " + mPosition);
        if (mPosition == 0) {
            mPowerTx.setTextColor(Color.parseColor("#FFFFFF"));
            mVolMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mInputTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mPower.setPressed(true);
            mVolumeMinus.setPressed(false);
            mVolumePlus.setPressed(false);
            mChannelMinus.setPressed(false);
            mChannelPlus.setPressed(false);
            mInput.setPressed(false);
            mPower.setScaleX(1.2f);
            mPower.setScaleY(1.2f);
            mPower.setBackgroundResource(R.drawable.power_sel);
            mVolumeMinus.setBackgroundResource(R.drawable.bg_uns);
            mVolumePlus.setBackgroundResource(R.drawable.bg_uns);
            mChannelMinus.setBackgroundResource(R.drawable.bg_uns);
            mInput.setBackgroundResource(R.drawable.bg_uns);
            mChannelPlus.setBackgroundResource(R.drawable.bg_uns);
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
        } else if (mPosition == 1) {
            mPowerTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolMTx.setTextColor(Color.parseColor("#FFFFFF"));
            mVolPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mInputTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mPower.setPressed(false);
            mVolumeMinus.setPressed(true);
            mVolumePlus.setPressed(false);
            mChannelMinus.setPressed(false);
            mChannelPlus.setPressed(false);
            mInput.setPressed(false);
            mPower.setBackgroundResource(R.drawable.power_uns);
            mVolumeMinus.setBackgroundResource(R.drawable.bg_sel);
            mVolumePlus.setBackgroundResource(R.drawable.bg_uns);
            mChannelMinus.setBackgroundResource(R.drawable.bg_uns);
            mInput.setBackgroundResource(R.drawable.bg_uns);
            mChannelPlus.setBackgroundResource(R.drawable.bg_uns);
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
        } else if (mPosition == 2) {
            mPowerTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolPTx.setTextColor(Color.parseColor("#FFFFFF"));
            mChMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mInputTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mPower.setPressed(false);
            mVolumeMinus.setPressed(false);
            mVolumePlus.setPressed(true);
            mChannelMinus.setPressed(false);
            mChannelPlus.setPressed(false);
            mInput.setPressed(false);
            mPower.setBackgroundResource(R.drawable.power_uns);
            mVolumeMinus.setBackgroundResource(R.drawable.bg_uns);
            mVolumePlus.setBackgroundResource(R.drawable.bg_sel);
            mChannelMinus.setBackgroundResource(R.drawable.bg_uns);
            mInput.setBackgroundResource(R.drawable.bg_uns);
            mChannelPlus.setBackgroundResource(R.drawable.bg_uns);
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
        } else if (mPosition == (mTotalSize - 1)) {
            mPowerTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mInputTx.setTextColor(Color.parseColor("#FFFFFF"));
            mPower.setPressed(false);
            mVolumeMinus.setPressed(false);
            mVolumePlus.setPressed(false);
            mChannelMinus.setPressed(false);
            mChannelPlus.setPressed(false);
            mInput.setPressed(true);
            mPower.setBackgroundResource(R.drawable.power_uns);
            mVolumeMinus.setBackgroundResource(R.drawable.bg_uns);
            mVolumePlus.setBackgroundResource(R.drawable.bg_uns);
            mChannelMinus.setBackgroundResource(R.drawable.bg_uns);
            mChannelPlus.setBackgroundResource(R.drawable.bg_uns);
            mInput.setBackgroundResource(R.drawable.bg_sel);
            mChannelMinus.setScaleX(1.0f);
            mChannelMinus.setScaleY(1.0f);
            mPower.setScaleX(1.0f);
            mPower.setScaleY(1.0f);
            mVolumeMinus.setScaleX(1.0f);
            mVolumeMinus.setScaleY(1.0f);
            mVolumePlus.setScaleX(1.0f);
            mVolumePlus.setScaleY(1.0f);
            mChannelPlus.setScaleX(1.0f);
            mChannelPlus.setScaleY(1.0f);
            mInput.setScaleX(1.2f);
            mInput.setScaleY(1.2f);
        } else if (mPosition == 3) {
            mPowerTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChMTx.setTextColor(Color.parseColor("#FFFFFF"));
            mChPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mInputTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mPower.setPressed(false);
            mVolumeMinus.setPressed(false);
            mVolumePlus.setPressed(false);
            mChannelMinus.setPressed(true);
            mChannelPlus.setPressed(false);
            mInput.setPressed(false);
            mPower.setBackgroundResource(R.drawable.power_uns);
            mVolumeMinus.setBackgroundResource(R.drawable.bg_uns);
            mVolumePlus.setBackgroundResource(R.drawable.bg_uns);
            mChannelMinus.setBackgroundResource(R.drawable.bg_sel);
            mChannelPlus.setBackgroundResource(R.drawable.bg_uns);
            mInput.setBackgroundResource(R.drawable.bg_uns);
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
        } else if (mPosition == 4) {
            mPowerTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mVolPTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChMTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mChPTx.setTextColor(Color.parseColor("#FFFFFF"));
            mInputTx.setTextColor(Color.parseColor("#BFFFFFFF"));
            mPower.setPressed(false);
            mVolumeMinus.setPressed(false);
            mVolumePlus.setPressed(false);
            mChannelMinus.setPressed(false);
            mChannelPlus.setPressed(true);
            mInput.setPressed(false);
            mPower.setBackgroundResource(R.drawable.power_uns);
            mVolumeMinus.setBackgroundResource(R.drawable.bg_uns);
            mVolumePlus.setBackgroundResource(R.drawable.bg_uns);
            mChannelMinus.setBackgroundResource(R.drawable.bg_uns);
            mChannelPlus.setBackgroundResource(R.drawable.bg_sel);
            mInput.setBackgroundResource(R.drawable.bg_uns);
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
        }
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
