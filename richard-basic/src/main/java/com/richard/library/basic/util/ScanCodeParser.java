package com.richard.library.basic.util;

import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author: Richard
 * @createDate: 2024/4/15 17:36
 * @version: 1.0
 * @description: 扫码解析
 * 监听键盘事件,除了返回事件都将它拦截,使用我们自定义的拦截器处理该事件
 * @Override public boolean dispatchKeyEvent(KeyEvent event) {
 * if (event.getKeyCode() != KeyEvent.KEYCODE_BACK) {
 * scanCodeParser.parseKeyEvent(event);
 * return true;
 * }
 * return super.dispatchKeyEvent(event);
 * }
 */

public final class ScanCodeParser {

    public int keyInterval = 100;
    private long startKeyTime;
    private long lastKeyTime;
    private long currentTime;

    private final StringBuilder resultBuilder = new StringBuilder();
    public Callback callback;
    private boolean isShift;
    private long lastClickTime;
    //扫码频率限制时间(单位:毫秒)小于等于0时代表不限制
    private long limitFreqTime = 0;
    private TextView inputView;//当前值赋值或输入框控件

    /**
     * 设置当前可接收的赋值或输入框控件
     */
    public void setInputView(TextView inputView) {
        this.inputView = inputView;
    }

    /**
     * 设置每次前后两次按键的最大间隔时间毫秒数，找过该数值会被判断为手动输入，否则为扫码枪扫入
     */
    public void setKeyInterval(int keyInterval) {
        this.keyInterval = keyInterval;
    }

    /**
     * 设置扫码结果回调
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        /**
         * 当扫码成功时回调
         *
         * @param isScan 是否属于扫码枪扫入
         * @param code   扫入或输入的码
         */
        void onScanCodeSuccess(boolean isScan, String code);

    }

    /**
     * 重置
     */
    public void reset() {
        this.resultBuilder.setLength(0);
    }

    /**
     * 设置扫码频率限制时间(单位:毫秒)
     */
    public void setLimitFreqTime(long limitFreqTime) {
        this.limitFreqTime = limitFreqTime;
    }

    /**
     * 解析按键事件
     */
    public void parseKeyEvent(KeyEvent event) {
        if(inputView != null && !inputView.isFocused()){
            this.reset();
            return;
        }

        currentTime = event.getDownTime();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() != KeyEvent.KEYCODE_ENTER
                    && event.getKeyCode() != KeyEvent.KEYCODE_NUMPAD_ENTER
                    && (currentTime - lastKeyTime) >= keyInterval) {
                startKeyTime = currentTime;
            }
            lastKeyTime = currentTime;
        }

        this.checkLetterStatus(event);

        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return;
        }

        char aChar = getInputCode(isShift, event.getKeyCode());

        if (aChar != 0) {
            resultBuilder.append(aChar);
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            if (callback != null && (limitFreqTime <= 0 || !fastClickCheck(limitFreqTime))) {
                int length = resultBuilder.length();
                boolean isScan = ((currentTime - startKeyTime) / Math.max(length, 1)) <= 30;
                String code = resultBuilder.toString();

                if(inputView != null){
                    inputView.setText(code);
                    if(inputView instanceof EditText){
                        inputView.postDelayed(() -> {
                            ((EditText)inputView).selectAll();
                        }, 10);
                    }
                }

                callback.onScanCodeSuccess(isScan, code);
            }
            resultBuilder.delete(0, resultBuilder.length());
        }
    }

    /**
     * 判断大小写
     */
    private void checkLetterStatus(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            isShift = event.getAction() == KeyEvent.ACTION_DOWN;
        }
    }

    /**
     * 将keyCode转为char
     *
     * @param isShift 是不是大写
     * @param keyCode 按键
     * @return 按键对应的char
     */
    private char getInputCode(boolean isShift, int keyCode) {
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            return (char) ((isShift ? 'A' : 'a') + keyCode - KeyEvent.KEYCODE_A);
        } else {
            return keyValue(isShift, keyCode);
        }
    }

    /**
     * 按键对应的char表
     */
    private char keyValue(boolean caps, int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                return caps ? ')' : '0';
            case KeyEvent.KEYCODE_1:
                return caps ? '!' : '1';
            case KeyEvent.KEYCODE_2:
                return caps ? '@' : '2';
            case KeyEvent.KEYCODE_3:
                return caps ? '#' : '3';
            case KeyEvent.KEYCODE_4:
                return caps ? '$' : '4';
            case KeyEvent.KEYCODE_5:
                return caps ? '%' : '5';
            case KeyEvent.KEYCODE_6:
                return caps ? '^' : '6';
            case KeyEvent.KEYCODE_7:
                return caps ? '&' : '7';
            case KeyEvent.KEYCODE_8:
                return caps ? '*' : '8';
            case KeyEvent.KEYCODE_9:
                return caps ? '(' : '9';
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
                return '-';
            case KeyEvent.KEYCODE_MINUS:
                return '_';
            case KeyEvent.KEYCODE_EQUALS:
                return '=';
            case KeyEvent.KEYCODE_NUMPAD_ADD:
                return '+';
            case KeyEvent.KEYCODE_GRAVE:
                return caps ? '~' : '`';
            case KeyEvent.KEYCODE_BACKSLASH:
                return caps ? '|' : '\\';
            case KeyEvent.KEYCODE_LEFT_BRACKET:
                return caps ? '{' : '[';
            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                return caps ? '}' : ']';
            case KeyEvent.KEYCODE_SEMICOLON:
                return caps ? ':' : ';';
            case KeyEvent.KEYCODE_APOSTROPHE:
                return caps ? '"' : '\'';
            case KeyEvent.KEYCODE_COMMA:
                return caps ? '<' : ',';
            case KeyEvent.KEYCODE_PERIOD:
                return caps ? '>' : '.';
            case KeyEvent.KEYCODE_SLASH:
                return caps ? '?' : '/';
            case KeyEvent.KEYCODE_SPACE:
                return ' ';
            default:
                return 0;
        }
    }

    /**
     * 验证是否为快速点击
     *
     * @param millisecond 多少毫秒内算快速点击
     * @return true:快速点击,false:慢速点击
     */
    private boolean fastClickCheck(long millisecond) {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= millisecond) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }
}
