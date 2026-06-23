package com.richard.library.basic.widget.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.richard.library.basic.R;

/**
 * <pre>
 * Description : 标签
 * Author : admin-richard
 * Date : 2017/9/22 14:29
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/9/22 14:29     admin-richard         new file.
 * </pre>
 */
public class TagView extends FrameLayout implements Checkable {

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private CompoundButton compoundButton;
    private boolean isChecked;

    //该tagView在列表当中实际的下标位置
    private int position;
    //点击TagView事件回调
    private OnClickListener onClickTagViewListener;
    //改变选择时的回调
    private OnChangeCheckCallback onChangeCheckCallback;

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        compoundButton = findViewById(R.id.tag_view_compoundButton);
        if (compoundButton == null) {
            throw new IllegalArgumentException("必须包含一个id为tag_view_compoundButton的CompoundButton控件");
        }
        super.onFinishInflate();
    }

    /**
     * 设置小图标
     */
    public void setIcon(Integer leftIconRes, Integer topIconRes, Integer rightIconRes, Integer bottomIconRes) {
        compoundButton.setCompoundDrawablesWithIntrinsicBounds(
                leftIconRes == null ? 0 : leftIconRes
                , topIconRes == null ? 0 : topIconRes
                , rightIconRes == null ? 0 : rightIconRes
                , bottomIconRes == null ? 0 : bottomIconRes
        );
    }

    /**
     * 设置显示文本
     */
    public void setText(String text) {
        compoundButton.setText(text);
    }

    /**
     * 获取当前view位置标识
     */
    public int getPosition() {
        return position;
    }

    /**
     * 设置当前view位置标识
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 获取CompoundButton
     */
    public CompoundButton getCompoundButton() {
        return compoundButton;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(states, CHECKED_STATE_SET);
        }
        return states;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked == checked) {
            return;
        }
        this.isChecked = checked;
        super.refreshDrawableState();
        this.updateChildCheckableViewState(this);
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        this.setChecked(!isChecked);
    }

    /**
     * 更新当前TagView的childView的check状态
     */
    private void updateChildCheckableViewState(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = ((ViewGroup) view);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                this.updateChildCheckableViewState(viewGroup.getChildAt(i));
            }
        }
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(isChecked);
        }
    }

    @Override
    public boolean performClick() {
        this.touchEvent(MotionEvent.ACTION_UP);
        return super.performClick();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return this.touchEvent(ev.getAction());
    }

    /**
     * 处理触摸事件
     */
    private boolean touchEvent(int action) {
        if (!isEnabled()) {
            return true;
        }
        if (action == MotionEvent.ACTION_UP && onClickTagViewListener != null) {
            onClickTagViewListener.onClick(compoundButton);
        }
        return true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        this.onClickTagViewListener = onClickListener;
    }

    public void performChangeCheckCallback() {
        if (onChangeCheckCallback != null) {
            onChangeCheckCallback.callback(compoundButton, compoundButton.isChecked());
        }
    }

    public void setOnChangeCheckCallback(OnChangeCheckCallback onChangeCheckCallback) {
        this.onChangeCheckCallback = onChangeCheckCallback;
    }

    public interface OnChangeCheckCallback {
        void callback(CompoundButton compoundButton, boolean isChecked);
    }
}
