package com.richard.library.basic.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.Snackbar.SnackbarLayout;
import com.richard.library.context.util.DensityUtilKt;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/10/16
 *     desc  : utils about snackbar
 * </pre>
 */
public final class SnackBarUtil {

    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_SHORT = -1;
    public static final int LENGTH_LONG = 0;

    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    private static final int COLOR_DEFAULT = 0xFEFFFFFF;
    private static final int COLOR_SUCCESS = 0xFF2BB600;
    private static final int COLOR_WARNING = 0xFFFFC100;
    private static final int COLOR_ERROR = 0xFFFF0000;
    private static final int COLOR_MESSAGE = 0xFFFFFFFF;

    private static WeakReference<Snackbar> sWeakSnackbar;

    private View view;
    private CharSequence message;
    private int messageTextColor;
    private int messageTextSize;
    private int bgColor;
    private int bgResource;
    private Drawable bgDrawable;
    private int duration;
    private CharSequence actionText;
    private int actionTextColor;
    private View.OnClickListener actionListener;
    private int bottomMargin;
    private boolean isMessageWidthWrapContent;//消息是否随内容宽度自适应

    private SnackBarUtil(final View parent) {
        setDefault();
        this.view = parent;
    }

    private void setDefault() {
        message = "";
        messageTextColor = COLOR_DEFAULT;
        messageTextSize = 0;
        bgColor = COLOR_DEFAULT;
        bgResource = -1;
        bgDrawable = null;
        duration = LENGTH_SHORT;
        actionText = "";
        actionTextColor = COLOR_DEFAULT;
        bottomMargin = 0;
        isMessageWidthWrapContent = false;
    }

    /**
     * Set the view to find a parent from.
     *
     * @param view The view to find a parent from.
     * @return the single {@link SnackBarUtil} instance
     */
    public static SnackBarUtil with(@NonNull final View view) {
        return new SnackBarUtil(view);
    }

    /**
     * Set the message.
     *
     * @param msg The message.
     * @return the single {@link SnackBarUtil} instance
     */
    public SnackBarUtil setMessage(@NonNull final CharSequence msg) {
        this.message = msg;
        return this;
    }

    /**
     * Set the color of message.
     *
     * @param color The color of message.
     * @return the single {@link SnackBarUtil} instance
     */
    public SnackBarUtil setMessageTextColor(@ColorInt final int color) {
        this.messageTextColor = color;
        return this;
    }

    /**
     * 设置消息文本字体大小 sp
     */
    public SnackBarUtil setMessageTextSize(int messageTextSize) {
        this.messageTextSize = messageTextSize;
        return this;
    }

    /**
     * 设置消息是否随内容宽度自适应
     */
    public SnackBarUtil setMessageWidthWrapContent(boolean messageWidthWrapContent) {
        this.isMessageWidthWrapContent = messageWidthWrapContent;
        return this;
    }

    /**
     * Set the color of background.
     *
     * @param color The color of background.
     * @return the single {@link SnackBarUtil} instance
     */
    public SnackBarUtil setBgColor(@ColorInt final int color) {
        this.bgColor = color;
        return this;
    }

    /**
     * Set the resource of background.
     *
     * @param bgResource The resource of background.
     * @return the single {@link SnackBarUtil} instance
     */
    public SnackBarUtil setBgResource(@DrawableRes final int bgResource) {
        this.bgResource = bgResource;
        return this;
    }

    public SnackBarUtil setBgDrawable(Drawable bgDrawable) {
        this.bgDrawable = bgDrawable;
        return this;
    }

    /**
     * Set the duration.
     *
     * @param duration The duration.
     *                 <ul>
     *                 <li>{@link Duration#LENGTH_INDEFINITE}</li>
     *                 <li>{@link Duration#LENGTH_SHORT     }</li>
     *                 <li>{@link Duration#LENGTH_LONG      }</li>
     *                 </ul>
     * @return the single {@link SnackBarUtil} instance
     */
    public SnackBarUtil setDuration(@Duration final int duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Set the action.
     *
     * @param text     The text.
     * @param listener The click listener.
     * @return the single {@link SnackBarUtil} instance
     */
    public SnackBarUtil setAction(@NonNull final CharSequence text,
                                  @NonNull final View.OnClickListener listener) {
        return setAction(text, COLOR_DEFAULT, listener);
    }

    /**
     * Set the action.
     *
     * @param text     The text.
     * @param color    The color of text.
     * @param listener The click listener.
     * @return the single {@link SnackBarUtil} instance
     */

    public SnackBarUtil setAction(@NonNull final CharSequence text,
                                  @ColorInt final int color,
                                  @NonNull final View.OnClickListener listener) {
        this.actionText = text;
        this.actionTextColor = color;
        this.actionListener = listener;
        return this;
    }

    /**
     * Set the bottom margin.
     *
     * @param bottomMargin The size of bottom margin, in pixel.
     */
    public SnackBarUtil setBottomMargin(@IntRange(from = 1) final int bottomMargin) {
        this.bottomMargin = bottomMargin;
        return this;
    }

    /**
     * Show the snackbar.
     */
    public Snackbar show() {
        return show(false);
    }

    /**
     * Show the snackbar.
     *
     * @param isShowTop True to show the snack bar on the top, false otherwise.
     */
    @SuppressLint("RestrictedApi")
    public Snackbar show(boolean isShowTop) {
        View view = this.view;
        if (view == null) return null;
        if (isShowTop) {
            ViewGroup suitableParent = findSuitableParentCopyFromSnackbar(view);
            View topSnackBarContainer = suitableParent.findViewWithTag("topSnackBarCoordinatorLayout");
            if (topSnackBarContainer == null) {
                CoordinatorLayout topSnackBarCoordinatorLayout = new CoordinatorLayout(view.getContext());
                topSnackBarCoordinatorLayout.setTag("topSnackBarCoordinatorLayout");
                topSnackBarCoordinatorLayout.setRotation(180);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // bring to front
                    topSnackBarCoordinatorLayout.setElevation(100);
                }
                suitableParent.addView(topSnackBarCoordinatorLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                topSnackBarContainer = topSnackBarCoordinatorLayout;
            }
            view = topSnackBarContainer;
        }

        if (messageTextColor != COLOR_DEFAULT || messageTextSize > 0) {
            SpannableString spannableString = new SpannableString(message);

            if (messageTextColor != COLOR_DEFAULT) {
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(messageTextColor);
                spannableString.setSpan(
                        colorSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }

            if (messageTextSize > 0) {
                spannableString.setSpan(
                        new AbsoluteSizeSpan(DensityUtilKt.sp2px(messageTextSize), false), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
            sWeakSnackbar = new WeakReference<>(Snackbar.make(view, spannableString, duration));
        } else {
            sWeakSnackbar = new WeakReference<>(Snackbar.make(view, message, duration));
        }

        final Snackbar snackbar = sWeakSnackbar.get();
        final SnackbarLayout snackBarView = (SnackbarLayout) snackbar.getView();
        if (isShowTop) {
            for (int i = 0; i < snackBarView.getChildCount(); i++) {
                View child = snackBarView.getChildAt(i);
                child.setRotation(180);
            }
        }

        if (isMessageWidthWrapContent) {
            snackBarView.setMinimumWidth(0);
            ViewGroup.LayoutParams params = snackBarView.getLayoutParams();
            if (isMessageWidthWrapContent) {
                params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
                params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            }

            if (params instanceof CoordinatorLayout.LayoutParams clp) {
                clp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            } else if (params instanceof FrameLayout.LayoutParams flp) {
                flp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            }

            snackBarView.setLayoutParams(params);
        }

        if (bgDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                snackBarView.setBackground(bgDrawable);
            } else {
                snackBarView.setBackgroundDrawable(bgDrawable);
            }
        } else if (bgResource != -1) {
            snackBarView.setBackgroundResource(bgResource);
        } else if (bgColor != COLOR_DEFAULT) {
            snackBarView.setBackgroundColor(bgColor);
        }
        if (bottomMargin != 0) {
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) snackBarView.getLayoutParams();
            params.bottomMargin = bottomMargin;
        }
        if (actionText.length() > 0 && actionListener != null) {
            if (actionTextColor != COLOR_DEFAULT) {
                snackbar.setActionTextColor(actionTextColor);
            }
            snackbar.setAction(actionText, actionListener);
        }
        snackbar.show();
        return snackbar;
    }

    /**
     * Show the snackbar with success style.
     */
    public void showSuccess() {
        showSuccess(false);
    }

    /**
     * Show the snackbar with success style.
     *
     * @param isShowTop True to show the snack bar on the top, false otherwise.
     */
    public void showSuccess(boolean isShowTop) {
        bgColor = COLOR_SUCCESS;
        messageTextColor = COLOR_MESSAGE;
        actionTextColor = COLOR_MESSAGE;
        show(isShowTop);
    }

    /**
     * Show the snackbar with warning style.
     */
    public void showWarning() {
        showWarning(false);
    }

    /**
     * Show the snackbar with warning style.
     *
     * @param isShowTop True to show the snackbar on the top, false otherwise.
     */
    public void showWarning(boolean isShowTop) {
        bgColor = COLOR_WARNING;
        messageTextColor = COLOR_MESSAGE;
        actionTextColor = COLOR_MESSAGE;
        show(isShowTop);
    }

    /**
     * Show the snackbar with error style.
     */
    public void showError() {
        showError(false);
    }

    /**
     * Show the snackbar with error style.
     *
     * @param isShowTop True to show the snackbar on the top, false otherwise.
     */
    public void showError(boolean isShowTop) {
        bgColor = COLOR_ERROR;
        messageTextColor = COLOR_MESSAGE;
        actionTextColor = COLOR_MESSAGE;
        show(isShowTop);
    }

    /**
     * Dismiss the snackbar.
     */
    public static void dismiss() {
        if (sWeakSnackbar != null && sWeakSnackbar.get() != null) {
            sWeakSnackbar.get().dismiss();
            sWeakSnackbar = null;
        }
    }

    /**
     * Return the view of snackbar.
     *
     * @return the view of snackbar
     */
    public static View getView() {
        Snackbar snackbar = sWeakSnackbar.get();
        if (snackbar == null) return null;
        return snackbar.getView();
    }

    /**
     * Add view to the snackbar.
     * <p>Call it after {@link #show()}</p>
     *
     * @param layoutId The id of layout.
     * @param params   The params.
     */
    public static void addView(@LayoutRes final int layoutId,
                               @NonNull final ViewGroup.LayoutParams params) {
        final View view = getView();
        if (view != null) {
            view.setPadding(0, 0, 0, 0);
            @SuppressLint("RestrictedApi") SnackbarLayout layout = (SnackbarLayout) view;
            View child = LayoutInflater.from(view.getContext()).inflate(layoutId, null);
            layout.addView(child, -1, params);
        }
    }

    /**
     * Add view to the snackbar.
     * <p>Call it after {@link #show()}</p>
     *
     * @param child  The child view.
     * @param params The params.
     */
    public static void addView(@NonNull final View child,
                               @NonNull final ViewGroup.LayoutParams params) {
        final View view = getView();
        if (view != null) {
            view.setPadding(0, 0, 0, 0);
            @SuppressLint("RestrictedApi") SnackbarLayout layout = (SnackbarLayout) view;
            layout.addView(child, params);
        }
    }

    private static ViewGroup findSuitableParentCopyFromSnackbar(View view) {
        ViewGroup fallback = null;

        do {
            if (view instanceof CoordinatorLayout c) {
                return (ViewGroup) view;
            }

            if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    return (ViewGroup) view;
                }

                fallback = (ViewGroup) view;
            }

            if (view != null) {
                ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        return fallback;
    }
}
