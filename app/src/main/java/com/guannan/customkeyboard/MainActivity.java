package com.guannan.customkeyboard;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editText = (EditText) findViewById(R.id.edt_text);
        editText.requestFocus();
        MotionEvent me = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
        editText.onTouchEvent(me);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        CustomKeyBoard customKeyBoard = new CustomKeyBoard(this, editText);

    }
}
