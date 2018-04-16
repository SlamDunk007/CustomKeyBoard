package com.guannan.library;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.List;

/**
 * @author guannan
 * @date 2018/4/16 14:09
 */

public class CustomKeyBoardView extends FrameLayout implements View.OnTouchListener {

    private KeyboardView mKeyBoardView;
    //绑定的相应编辑框
    private EditText mEditText;
    //数字键盘布局
    private Keyboard mNumberKeyBoard;
    //字母键盘布局
    private Keyboard mCharacterKeyBoard;
    //清空
    public static final int KEYCODE_CLEAR = -100;
    //系统键盘
    public static final int KEYCODE_SYS = -101;
    //搜索
    public static final int KEYCODE_SEARCH = -102;
    //标记当前字母键盘的大小写切换
    private boolean isChange;
    //标记当前字母是否大小写
    private boolean isUpper;
    private Context mContext;

    public CustomKeyBoardView(@NonNull Context context) {
        this(context, null);
    }

    public CustomKeyBoardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomKeyBoardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initKeyBoard();
    }

    /**
     * 绑定EditText
     *
     * @param editText
     */
    public void bindEditText(EditText editText) {
        this.mEditText = editText;
        if(mEditText!=null){
            mEditText.setOnTouchListener(this);
        }
    }

    private void initKeyBoard() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mKeyBoardView = (KeyboardView) inflater.inflate(R.layout.content_keyboard, null);
        addView(mKeyBoardView);
        //数字键盘布局
        mNumberKeyBoard = new Keyboard(mContext, R.xml.number);
        //字母键盘
        mCharacterKeyBoard = new Keyboard(mContext, R.xml.character);
        //设置当前显示的键盘样式
        mKeyBoardView.setKeyboard(mNumberKeyBoard);
        mKeyBoardView.setEnabled(true);
        //设置显示类似toast样式的提示
        mKeyBoardView.setPreviewEnabled(true);
        //给按键设置监听
        mKeyBoardView.setOnKeyboardActionListener(keyboardListener);
        setKbvAnimation();
    }

    /**
     * 给键盘设置动画
     */
    public void setKbvAnimation(){
        TranslateAnimation animation = new TranslateAnimation(0, 0, mKeyBoardView.getKeyboard().getHeight(), 0);
        animation.setDuration(300);
        mKeyBoardView.startAnimation(animation);
    }

    /**
     * 获取KeyboardView，拿到了KeyboardView对象就可以设置键盘背景，按键字体大小颜色以及预览框样式等
     * @return
     */
    public KeyboardView getKeyBoardView(){
        if(mKeyBoardView!=null){
            return mKeyBoardView;
        }
        return null;
    }

    KeyboardView.OnKeyboardActionListener keyboardListener = new KeyboardView.OnKeyboardActionListener() {

        /**
         * 在onkey之前调用，primaryCode为按下的Ascci码值
         * @param primaryCode
         */
        @Override
        public void onPress(int primaryCode) {
            mKeyBoardView.setPreviewEnabled(primaryCode > 0);
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
            if (mEditText == null) {
                return;
            }
            int start = mEditText.getSelectionStart();
            Editable editable = mEditText.getText();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {   //回退删除
                if (!TextUtils.isEmpty(mEditText.getText())) {
                    editable.delete(editable.toString().length() - 1, editable.toString().length());
                }
            } else if (primaryCode == KEYCODE_CLEAR) {  //清空
                mEditText.setText("");
            } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {   //隐藏
                hideAndShowSysKeyBoard(false);
            } else if (primaryCode == KEYCODE_SYS) {  //系统键盘
                showSysKeyBoard();
            } else if (primaryCode == KEYCODE_SEARCH) {  //搜索
                if (mSearchListener != null) {
                    mSearchListener.onSearCh(primaryCode);
                }
            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) { //切换数字字母键盘
                if (!isChange) {
                    isChange = true;
                    mKeyBoardView.setKeyboard(mCharacterKeyBoard);
                } else {
                    isChange = false;
                    mKeyBoardView.setKeyboard(mNumberKeyBoard);
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {       //大小写切换
                changeKeysUpperOrLower();
                mKeyBoardView.setKeyboard(mCharacterKeyBoard);
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
     * 改变字母键盘大小写
     */
    private void changeKeysUpperOrLower() {

        List<Keyboard.Key> keys = mCharacterKeyBoard.getKeys();
        if (isUpper) {// 大写切换小写
            isUpper = false;
            for (Keyboard.Key key : keys) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] += 32;
                }
            }
        } else {// 小写切换大写
            isUpper = true;
            for (Keyboard.Key key : keys) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] -= 32;
                }
            }
        }
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

    /**
     * 显示系统键盘
     */
    public void showSysKeyBoard() {
        hideAndShowSysKeyBoard(true);
    }

    /**
     * 隐藏自定义键盘,显示系统键盘
     *
     * @param showSys
     */
    public void hideAndShowSysKeyBoard(boolean showSys) {
        if (mKeyBoardView.getVisibility() == View.VISIBLE) {
            mKeyBoardView.setVisibility(View.GONE);
        }
        if (showSys) {
            showSysKeyBoard(mContext, mEditText);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        showKeyBoard();
        mEditText.onTouchEvent(event);
        //这里设置输入类型为文本类型是为了让EditText继续获取焦点
        mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        return true;
    }

    /**
     * 显示自定义键盘
     */
    public void showKeyBoard() {
        mEditText.setInputType(InputType.TYPE_NULL);     //这里设置EditText的输入类型为null是为了不让其调起系统键盘
        hideInputMethod(mContext, mEditText);
        if (mKeyBoardView.getVisibility() == View.GONE || mKeyBoardView.getVisibility() == View.INVISIBLE) {
            mKeyBoardView.setVisibility(View.VISIBLE);
            setKbvAnimation();
        }
    }

    /**
     * 自定义键盘上按键搜索的监听
     */
    public interface SearchListener {

        void onSearCh(int primaryCode);
    }

    private SearchListener mSearchListener;

    public void addSearchListener(SearchListener searchListener) {
        this.mSearchListener = searchListener;
    }


}
