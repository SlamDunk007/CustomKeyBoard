package com.guannan.customkeyboard;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author guannan
 * @date 2018/4/3 11:04
 */

public class CustomKeyBoard {

    //清空
    public static final int KEYCODE_CLEAR = -100;
    //系统键盘
    public static final int KEYCODE_SYS = -101;
    //搜索
    public static final int KEYCODE_SEARCH = -102;
    //当前编辑框
    private EditText mEdtText;
    //容纳KeyBoard的view
    private KeyboardView mKeyboardView;
    //数字键盘布局
    private Keyboard mNumberKeyBoard;
    private Activity mActivity;

    public CustomKeyBoard(Activity activity, EditText editText) {

        this.mActivity = activity;
        this.mEdtText = editText;
        initKeyBoard(activity);
    }

    /**
     * 初始化键盘
     *
     * @param activity
     */
    private void initKeyBoard(Activity activity) {

        //数字键盘布局
        mNumberKeyBoard = new Keyboard(activity, R.xml.number);
        mKeyboardView = (KeyboardView) activity.findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboard(mNumberKeyBoard);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(true);
        mKeyboardView.setOnKeyboardActionListener(keyboardListener);
        mEdtText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showKeyBoard();
                mEdtText.onTouchEvent(event);
                //这里设置输入类型为文本类型是为了让EditText继续获取焦点
                mEdtText.setInputType(InputType.TYPE_CLASS_TEXT);
                return true;
            }
        });
    }


    KeyboardView.OnKeyboardActionListener keyboardListener = new KeyboardView.OnKeyboardActionListener() {

        /**
         * 在onkey之前调用，primaryCode为按下的Ascci码值
         * @param primaryCode
         */
        @Override
        public void onPress(int primaryCode) {
            mKeyboardView.setPreviewEnabled(primaryCode > 0);
        }

        /**
         * 在onkey之后调用，primaryCode为按下的Ascci码值
         * @param primaryCode
         */
        @Override
        public void onRelease(int primaryCode) {

        }

        /**
         * @param primaryCode
         * @param keyCodes  codes字段定义的值
         */
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            int start = mEdtText.getSelectionStart();
            Editable editable = mEdtText.getText();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {   //回退删除
                if (!TextUtils.isEmpty(mEdtText.getText())) {
                    editable.delete(editable.toString().length() - 1, editable.toString().length());
                }
            } else if (primaryCode == KEYCODE_CLEAR) {  //清空
                mEdtText.setText("");
            } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {   //隐藏
                hideKeyBoard(false);
            } else if (primaryCode == KEYCODE_SYS) {  //系统键盘
                showSysKeyBoard();
            } else if (primaryCode == KEYCODE_SEARCH) {  //搜索

            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        // 如果之前在keyOutputText定义过数值，则按键之后会在此回调中进行响应
        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    /**
     * 显示自定义键盘
     */
    public void showKeyBoard() {
        mEdtText.setInputType(InputType.TYPE_NULL);     //这里设置EditText的输入类型为null是为了不让其调起系统键盘
        hideInputMethod(mActivity, mEdtText);
        if (mKeyboardView.getVisibility() == View.GONE || mKeyboardView.getVisibility() == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示系统键盘
     */
    public void showSysKeyBoard() {
//        mEdtText.setInputType(InputType.TYPE_CLASS_TEXT);
        hideKeyBoard(true);
    }

    /**
     * 隐藏自定义键盘显示系统键盘
     *
     * @param showSys
     */
    public void hideKeyBoard(boolean showSys) {
        if (mKeyboardView.getVisibility() == View.VISIBLE) {
            mKeyboardView.setVisibility(View.GONE);
        }
        if (showSys) {
            showSysKeyBoard(mActivity, mEdtText);
        }
    }

    /**
     * 显示系统键盘
     *
     * @param context
     * @param v
     */
    public void showSysKeyBoard(Context context, View v) {
        if (context == null || v == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(v, 0);
        }
    }

    /**
     * 隐藏系统键盘
     *
     * @param context
     * @param v
     * @return
     */
    public boolean hideInputMethod(Context context, View v) {
        if (context == null || v == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

}
