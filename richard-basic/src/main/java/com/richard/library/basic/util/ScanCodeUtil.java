package com.richard.library.basic.util;

import android.annotation.SuppressLint;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;

/**
 * <pre>
 * Description : 扫码枪扫码处理
 * Author : admin-richard
 * Date : 2019-12-01 21:33
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-12-01 21:33     admin-richard         new file.
 * </pre>
 */
public final class ScanCodeUtil {

    public int keyInterval = 100;
    private long startKeyTime;
    private long lastKeyTime;
    private long currentTime;
    private long lastClickTime;
    private int originInputType;

    private boolean isAutoClearValue;
    private boolean isAutoRegainFocus;
    private boolean isClickShowKeyboard;
    private boolean isWhenShowKeyboardSelectAll;
    private boolean isEnteredSelectAll;
    private boolean isAutoChangeInputType = true;
    private long limitFreqTime;
    private Callback callback;

    private ScanCodeUtil() {
    }

    /**
     * 创建实例
     */
    public static ScanCodeUtil create() {
        return new ScanCodeUtil();
    }

    /**
     * 设置是否自动切换控件的inputType
     * 注：是否允许自动从InputType.TYPE_NULL和控件原始InputType之间切换(弹出键盘时为原始InputType，关闭软键盘时为InputType.TYPE_NULL，是为了解决扫码时因输入法而导致的相关问题)
     *
     * @param autoChangeInputType 是否自动切换控件的inputType
     */
    public ScanCodeUtil setAutoChangeInputType(boolean autoChangeInputType) {
        this.isAutoChangeInputType = autoChangeInputType;
        return this;
    }

    /**
     * 设置每次前后两次按键的最大间隔时间毫秒数，找过该数值会被判断为手动输入，否则为扫码枪扫入
     */
    public ScanCodeUtil setKeyInterval(int keyInterval) {
        this.keyInterval = keyInterval;
        return this;
    }

    /**
     * 设置是否自动清除编辑框中的内容
     */
    public ScanCodeUtil setAutoClearValue(boolean autoClearValue) {
        this.isAutoClearValue = autoClearValue;
        return this;
    }

    /**
     * 当前弹出键盘时编辑框中的文本内容是否全部选中
     */
    public ScanCodeUtil setWhenShowKeyboardSelectAll(boolean whenShowKeyboardSelectAll) {
        this.isWhenShowKeyboardSelectAll = whenShowKeyboardSelectAll;
        return this;
    }

    /**
     * 是否在扫描或输入完成(Enter)后全选输入的内容
     */
    public ScanCodeUtil setEnteredSelectAll(boolean enteredSelectAll) {
        this.isEnteredSelectAll = enteredSelectAll;
        return this;
    }

    /**
     * 设置是否自动重新获取焦点(若isAutoRegainFocus = true,则scanCodeResult.isAutoRegainFocus()条件将会被忽略,为false时则会已scanCodeResult.isAutoRegainFocus()为准)
     */
    public ScanCodeUtil setAutoRegainFocus(boolean autoRegainFocus) {
        isAutoRegainFocus = autoRegainFocus;
        return this;
    }

    /**
     * 设置点击编辑框是否弹出虚拟软键盘
     */
    public ScanCodeUtil setClickShowKeyboard(boolean clickShowKeyboard) {
        isClickShowKeyboard = clickShowKeyboard;
        return this;
    }

    /**
     * 设置扫码频率限制时间,单位：毫秒，为0时代表不限制，大于0代表具体限制时间毫秒数
     */
    public ScanCodeUtil setLimitFreqTime(long limitFreqTime) {
        this.limitFreqTime = limitFreqTime;
        return this;
    }

    /**
     * 设置回调
     */
    public ScanCodeUtil setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * 监听扫码事件
     *
     * @param editText 必填 监听控件
     */
    @SuppressLint("ClickableViewAccessibility")
    public void monitor(EditText editText) {
        originInputType = editText.getInputType();
        editText.setNextFocusUpId(editText.getId());
        editText.setNextFocusDownId(editText.getId());
        editText.setNextFocusLeftId(editText.getId());
        editText.setNextFocusRightId(editText.getId());
        editText.setNextFocusForwardId(editText.getId());
        editText.setFocusable(true);
        editText.setSingleLine(true);
        editText.setSelection(editText.length());
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        //获取焦点
        if ((isAutoRegainFocus || (callback != null && callback.isAutoRegainFocus())) && !editText.isFocused()) {
            editText.requestFocus();
        }

        if (isAutoChangeInputType) {
            editText.setInputType(InputType.TYPE_NULL);
            editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setCursorVisible(false);
            editText.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(@NonNull View v) {

                }

                @Override
                public void onViewDetachedFromWindow(@NonNull View v) {
                    editText.removeOnAttachStateChangeListener(this);
                    KeyboardUtil.unregisterSoftInputChangedListener(editText);
                }
            });

            KeyboardUtil.registerSoftInputChangedListener(editText, height -> {
                if (!isClickShowKeyboard || height == 0 && editText.getInputType() != InputType.TYPE_CLASS_NUMBER) {
                    editText.setInputType(InputType.TYPE_NULL);
                    editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setCursorVisible(false);
                } else if (height > 0 && editText.getInputType() != originInputType) {
                    editText.setInputType(originInputType);
                    editText.setCursorVisible(true);
                }
            });
        }

        //监听点击事件
        editText.setOnTouchListener((v, event) -> {
            if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
                if (isClickShowKeyboard) {
                    boolean isSoftInputVisible = KeyboardUtil.isSoftInputVisible(editText);
                    if (!isSoftInputVisible || isWhenShowKeyboardSelectAll && editText.length() > 0 && editText.getSelectionStart() == editText.getSelectionEnd()) {
                        if (isSoftInputVisible) {
                            EditTextUtil.selectAll(editText);
                        } else {
                            KeyboardUtil.showSoftInput(editText, 0, true, 10);
                        }
                    } else {
                        editText.setSelection(editText.getText().length());
                    }
                } else {
                    KeyboardUtil.hideSoftInput(editText);
                }
            }
            return false;
        });

        //监听按键key事件
        editText.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                currentTime = event.getDownTime();
                if (keyCode != KeyEvent.KEYCODE_ENTER
                        && keyCode != KeyEvent.KEYCODE_NUMPAD_ENTER
                        && (currentTime - lastKeyTime) >= keyInterval) {
                    startKeyTime = currentTime;
                }
                lastKeyTime = currentTime;
            }
            return false;
        });

        //监听编辑事件
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                return true;
            }

            int length = editText.length();
            boolean isScan = ((currentTime - startKeyTime) / Math.max(length, 1)) <= 30;

            KeyboardUtil.hideSoftInput(v);

            if (callback != null && !callback.isCanReceiveScanCode()) {
                editText.setText("");
                return true;
            }

            String code = v.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                return true;
            }

            if (isAutoClearValue) {
                editText.setText("");
            }

            if (limitFreqTime > 0 && fastClickCheck(limitFreqTime)) {
                return true;
            }

            if (isEnteredSelectAll && editText.length() > 0) {
                editText.selectAll();
            }

            if (editText.getSelectionStart() <= 0 && editText.getSelectionEnd() <= 0) {
                editText.setSelection(editText.length());
            }

            if (code.endsWith("\n")) {
                code = code.substring(0, code.length() - 1);
            }

            if (callback != null) {
                callback.onScanCodeSuccess(isScan, code);
            }
            return true;
        });

        //搜索框焦点变化事件
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            handleFocusChange(editText, hasFocus, isAutoRegainFocus);
            if (callback != null) {
                callback.onFocusChange(v, hasFocus);
            }
        });
    }

    /**
     * 处理焦点变化事件
     *
     * @param editText          编辑框控件
     * @param hasFocus          是否有焦点
     * @param isAutoRegainFocus 是否自动重新获取焦点
     */
    private void handleFocusChange(EditText editText, boolean hasFocus, boolean isAutoRegainFocus) {
        if (hasFocus) {
            return;
        }

        if (callback != null && !callback.isAutoRegainFocus()) {
            return;
        }

        if (callback == null && !isAutoRegainFocus) {
            return;
        }

        editText.postDelayed(() -> {
            if (callback == null || callback.isShowToUser()) {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
            }
        }, 1);
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

    public interface Callback {

        /**
         * 焦点变化事件
         */
        default void onFocusChange(View v, boolean hasFocus) {
        }

        /**
         * 扫码界面对于用户是否是可见的
         */
        default boolean isShowToUser() {
            return true;
        }

        /**
         * 扫码框是否可以自动再次重新获取焦点
         */
        default boolean isAutoRegainFocus() {
            return false;
        }

        /**
         * 是否可以接收扫码
         */
        default boolean isCanReceiveScanCode() {
            return true;
        }

        /**
         * 当扫码成功时回调
         *
         * @param isScan 是否属于扫码枪扫入
         * @param code   扫入或输入的码
         */
        void onScanCodeSuccess(boolean isScan, String code);
    }

}
