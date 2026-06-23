package com.richard.library.basic.widget.banner;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class LoopViewPager extends ViewPager {

    private static final boolean DEFAULT_BOUNDARY_CASHING = false;

    private LoopPagerAdapterWrapper mAdapter;
    private boolean mBoundaryCaching = DEFAULT_BOUNDARY_CASHING;
    private List<ViewPager.OnPageChangeListener> mOnPageChangeListeners;
    private LoopOnPageChangeListener onPageChangeListener;

    /**
     * helper function which may be used when implementing FragmentPagerAdapter
     *
     * @param position
     * @param count
     * @return (position - 1)%count
     */
    public static int toRealPosition(int position, int count) {
        position = position - 1;
        if (position < 0) {
            position += count;
        } else {
            position = position % count;
        }
        return position;
    }

    /**
     * If set to true, the boundary views (i.e. first and last) will never be
     * destroyed This may help to prevent "blinking" of some views
     *
     * @param flag
     */
    public void setBoundaryCaching(boolean flag) {
        mBoundaryCaching = flag;
        if (mAdapter != null) {
            mAdapter.setBoundaryCaching(flag);
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mAdapter = new LoopPagerAdapterWrapper(adapter);
        mAdapter.setBoundaryCaching(mBoundaryCaching);
        super.setAdapter(mAdapter);
        setCurrentItem(0, false);
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter/* != null ? mAdapter.getRealAdapter() : null*/;
    }

    @Override
    public int getCurrentItem() {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
    }

    private int getSuperCurrentItem() {
        return super.getCurrentItem();
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        int realItem = mAdapter.toInnerPosition(item);
        super.setCurrentItem(realItem, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        if (getCurrentItem() != item) {
            setCurrentItem(item, true);
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        addOnPageChangeListener(listener);
    }

    @Override
    public void addOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = new ArrayList<>();
        }
        mOnPageChangeListeners.add(listener);
    }

    @Override
    public void removeOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.remove(listener);
        }
    }

    @Override
    public void clearOnPageChangeListeners() {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.clear();
        }
    }

    public LoopViewPager(Context context) {
        super(context);
        init(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        onPageChangeListener = new LoopOnPageChangeListener(this);
        super.removeOnPageChangeListener(onPageChangeListener);
        super.addOnPageChangeListener(onPageChangeListener);
    }

    private static class LoopOnPageChangeListener implements OnPageChangeListener {

        private LoopViewPager vp;

        public LoopOnPageChangeListener(LoopViewPager vp) {
            this.vp = vp;
        }

        private float mPreviousOffset = -1;
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {
            int realPosition = vp.mAdapter.toRealPosition(position);
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (vp.mOnPageChangeListeners != null) {
                    for (int i = 0; i < vp.mOnPageChangeListeners.size(); i++) {
                        OnPageChangeListener listener = vp.mOnPageChangeListeners.get(i);
                        if (listener != null) {
                            listener.onPageSelected(realPosition);
                        }
                    }
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int realPosition = position;
            if (vp.mAdapter != null) {
                realPosition = vp.mAdapter.toRealPosition(position);

                if (positionOffset == 0 && mPreviousOffset == 0 && (position == 0 || position == vp.mAdapter.getCount() - 1)) {
                    vp.setCurrentItem(realPosition, false);
                }
            }

            mPreviousOffset = positionOffset;

            if (vp.mOnPageChangeListeners != null) {
                for (int i = 0; i < vp.mOnPageChangeListeners.size(); i++) {
                    OnPageChangeListener listener = vp.mOnPageChangeListeners.get(i);
                    if (listener != null) {
                        if (realPosition != vp.mAdapter.getRealCount() - 1) {
                            listener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                        } else {
                            if (positionOffset > .5) {
                                listener.onPageScrolled(0, 0, 0);
                            } else {
                                listener.onPageScrolled(realPosition, 0, 0);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (vp.mAdapter != null) {
                int position = vp.getSuperCurrentItem();
                int realPosition = vp.mAdapter.toRealPosition(position);
                if (state == ViewPager.SCROLL_STATE_IDLE && (position == 0 || position == vp.mAdapter.getCount() - 1)) {
                    vp.setCurrentItem(realPosition, false);
                }
            }

            if (vp.mOnPageChangeListeners != null) {
                for (int i = 0; i < vp.mOnPageChangeListeners.size(); i++) {
                    OnPageChangeListener listener = vp.mOnPageChangeListeners.get(i);
                    if (listener != null) {
                        listener.onPageScrollStateChanged(state);
                    }
                }
            }
        }
    }
}
