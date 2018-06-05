package com.konka.kkhotkey;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnKeyListener{
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: ");
                return false;
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: keycode is " + keyCode + ", " + KeyEvent.FLAG_LONG_PRESS);
        if ((event.getFlags() & KeyEvent.FLAG_LONG_PRESS) != 0) {
            Log.d(TAG, "onKeyUp: =======");
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: " + keyCode + ", " + event.getAction());
        if ((event.getFlags() & KeyEvent.FLAG_LONG_PRESS) != 0) {
            Log.d(TAG, "onKeyDown: =======");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyLongPress: " + keyCode + ", " + event.getAction());
        if (keyCode == 382) {
            Log.d(TAG, "onKeyLongPress: ");
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d(TAG, "onKey: keycode is " + keyCode + ", " + event.getAction());
        return false;
    }


}
