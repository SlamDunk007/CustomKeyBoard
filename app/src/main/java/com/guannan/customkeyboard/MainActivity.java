package com.guannan.customkeyboard;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;

import com.guannan.library.CustomKeyBoardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editText = (EditText) findViewById(R.id.edt_text);
        editText.requestFocus();
        //添加一下代码解决自定义键盘切换到系统键盘，再切回来，光标错位的问题
        MotionEvent me = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
        editText.onTouchEvent(me);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        CustomKeyBoardView keyBoardView = (CustomKeyBoardView) findViewById(R.id.keyboard_view);
        //EditText和keyboardVie做绑定
        keyBoardView.bindEditText(editText);

    }
}
