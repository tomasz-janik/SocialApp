package pl.itomaszjanik.test.BottomPopup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.AnyThread;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * PopupWindow with blurred below view.
 * Created by kyle on 2017/3/14.
 */

@SuppressWarnings("ALL")
public class BlurPopupWindow extends FrameLayout {
    private static final String TAG = "BlurPopupWindow";

    private static final float DEFAULT_BLUR_RADIUS = 10;
    private static final float DEFAULT_SCALE_RATIO = 0.4f;
    private static final long DEFAULT_ANIMATION_DURATION = 300;

    public interface OnDismissListener {
        void onDismiss(BlurPopupWindow popupWindow);
    }

    private Activity mActivity;
    protected ImageView mBlurView;
    protected FrameLayout mContentLayout;
    private boolean mAnimating;
    private boolean visible;
    private boolean autoDismiss;
    private String text;

    private WindowManager mWindowManager;

    private View mContentView;
    private int mTintColor;
    private View mAnchorView;
    private float mBlurRadius;
    private float mScaleRatio;
    private long mAnimationDuration;
    private boolean mDismissOnTouchBackground;
    private boolean mDismissOnClickBack;
    private OnDismissListener mOnDismissListener;
    private TextView textView;


    public BlurPopupWindow(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!(getContext() instanceof Activity)) {
            throw new IllegalArgumentException("Context must be Activity");
        }
        mActivity = (Activity) getContext();
        mWindowManager = mActivity.getWindowManager();

        mBlurRadius = DEFAULT_BLUR_RADIUS;
        mScaleRatio = DEFAULT_SCALE_RATIO;
        mAnimationDuration = DEFAULT_ANIMATION_DURATION;

        setFocusable(true);
        setFocusableInTouchMode(true);

        mContentLayout = new FrameLayout(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mContentLayout, lp);

        mBlurView = new ImageView(mActivity);
        mBlurView.setScaleType(ImageView.ScaleType.FIT_XY);
        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        mBlurView.setLayoutParams(lp);
        mContentLayout.addView(mBlurView);

        mContentView = createContentView(mContentLayout);
        if (mContentView != null) {
            mContentLayout.addView(mContentView);
        }
    }

    /**
     * Override this to create custom content.
     *
     * @param parent the parent where content view would be.
     * @return
     */
    protected View createContentView(ViewGroup parent) {
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAnimating || !mDismissOnTouchBackground) {
            return super.onTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            dismiss();
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mAnimating || !mDismissOnClickBack) {
            return super.onKeyUp(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void setContentView(View contentView) {
        if (contentView == null) {
            throw new IllegalArgumentException("contentView can not be null");
        }
        if (mContentView != null) {
            if (mContentView.getParent() != null) {
                ((ViewGroup) mContentView.getParent()).removeView(mContentView);
            }
            mContentView = null;
        }
        mContentView = contentView;
        mContentLayout.addView(mContentView);
    }

    public View getContentView() {
        return mContentView;
    }

    public void show() {
        if (mAnimating || visible) {
            return;
        }
        visible = true;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.RGBA_8888;

        int statusBarHeight = 0;
        int navigationBarHeight = BlurPopupWindow.getNaviHeight(mActivity);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        int trimTopHeight = statusBarHeight;
        int trimBottomHeight = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // No need to trim status bar height in SDK > 21.
            trimTopHeight = 0;

            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            if ((lp.flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                trimBottomHeight = navigationBarHeight;
            }

            // This line will cause decor view fill all the screen, even if FLAG_TRANSLUCENT_NAVIGATION
            // was not set.
            params.flags = lp.flags;

            if (trimBottomHeight > 0) {

                // If trimBottomHeight > 0, it means that we cut navigation bar off and we need shrink
                // popup windows' content height by increase bottom padding.
                setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom() + navigationBarHeight);
            } else {

                // If navigation is showing on the screen, whether translucent or not, we should move contentView
                // on top of it.
                boolean moveContent = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    moveContent = true;
                } else if (navigationBarHeight > 0 && (lp.flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) != 0) {
                    // Navigation feature diffs from v19 to v21.
                    moveContent = true;
                }
                if (navigationBarHeight > 0 && moveContent) {
                    if (mContentView != null) {
                        MarginLayoutParams layoutParams = (MarginLayoutParams) mContentView.getLayoutParams();
                        layoutParams.bottomMargin += navigationBarHeight;
                    }
                }
            }
        }

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.gravity = Gravity.BOTTOM;

        mWindowManager.addView(this, params);

        ObjectAnimator showAnimator = createShowAnimator();
        if (showAnimator != null) {
            mAnimating = true;
            showAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    mAnimating = false;
                    requestFocus();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimating = false;
                    requestFocus();
                }
            });
            showAnimator.start();
        }
        onShow();
        if (autoDismiss)
            autoDismiss();
    }

    private void autoDismiss(){
        Runnable mRunnable;
        Handler mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                try{
                    BlurPopupWindow.this.dismiss();
                }catch (Exception e){

                }
            }
        };

        mHandler.postDelayed(mRunnable,2500);
    }

    public void dismiss() {
        if (mAnimating) {
            return;
        }
        onDismiss();
        ObjectAnimator animator = createDismissAnimator();
        if (animator == null) {
            mWindowManager.removeView(this);
        } else {
            mAnimating = true;
            ObjectAnimator.ofFloat(mBlurView, "alpha", mBlurView.getAlpha(), 0).setDuration(getAnimationDuration()).start();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    removeSelf();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    removeSelf();
                }

                private void removeSelf() {
                    try {
                        mWindowManager.removeView(BlurPopupWindow.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        visible = false;
                        mAnimating = false;
                    }
                }
            });
            animator.start();
        }
    }

    protected void onBlurredImageGot(Bitmap bitmap) {
        mBlurView.setImageBitmap(bitmap);
        if (!mAnimating) {
            ObjectAnimator.ofFloat(mBlurView, "alpha", 0, 1f).setDuration(getAnimationDuration()).start();
        }
    }

    /**
     * When executing show method in this method, should override {@link BlurPopupWindow#createShowAnimator()}
     * and return null as well.
     */
    protected void onShow() {
    }

    /**
     * Do not start any animation in this method. use {@link BlurPopupWindow#createDismissAnimator()} instead.
     */
    @CallSuper
    protected void onDismiss() {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }
    }

    protected ObjectAnimator createShowAnimator() {
        return ObjectAnimator.ofFloat(mContentLayout, "alpha", 0, 1.f).setDuration(getAnimationDuration());
    }

    protected ObjectAnimator createDismissAnimator() {
        return ObjectAnimator.ofFloat(mContentLayout, "alpha", mContentLayout.getAlpha(), 0).setDuration(getAnimationDuration());
    }

    public String getText(){
        return text;
    }

    public void setText(String string, TextView textView){
        this.text = string;
        this.textView = textView;
    }

    public void setTextView(String string){
        if (textView != null){
            textView.setText(string);
        }
    }

    public void setAutoDismiss(boolean autoDismiss){
        this.autoDismiss = autoDismiss;
    }

    public int getTintColor() {
        return mTintColor;
    }

    public void setTintColor(int tintColor) {
        mTintColor = tintColor;
    }

    public View getAnchorView() {
        return mAnchorView;
    }

    public void setAnchorView(View anchorView) {
        mAnchorView = anchorView;
    }

    @AnyThread
    public float getBlurRadius() {
        return mBlurRadius;
    }

    public void setBlurRadius(float blurRadius) {
        mBlurRadius = blurRadius;
    }

    @AnyThread
    public float getScaleRatio() {
        return mScaleRatio;
    }

    public void setScaleRatio(float scaleRatio) {
        mScaleRatio = scaleRatio;
    }

    public long getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public boolean isDismissOnTouchBackground() {
        return mDismissOnTouchBackground;
    }

    public void setDismissOnTouchBackground(boolean dismissOnTouchBackground) {
        mDismissOnTouchBackground = dismissOnTouchBackground;
    }

    public boolean isDismissOnClickBack() {
        return mDismissOnClickBack;
    }

    public void setDismissOnClickBack(boolean dismissOnClickBack) {
        mDismissOnClickBack = dismissOnClickBack;
    }

    public OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public static class Builder<T extends BlurPopupWindow> {
        private static final String TAG = "BlurPopupWindow.Builder";
        protected Context mContext;
        private View mContentView;
        private int mTintColor;
        private float mBlurRadius;
        private float mScaleRatio;
        private long mAnimationDuration;
        private boolean mDismissOnTouchBackground = true;
        private boolean mDismissOnClickBack = true;
        private int mGravity = -1;
        private OnDismissListener mOnDismissListener;
        private String mText;
        private TextView mTextView;
        private boolean autoDismiss;

        public Builder(Context context) {
            mContext = context;

            mBlurRadius = BlurPopupWindow.DEFAULT_BLUR_RADIUS;
            mScaleRatio = BlurPopupWindow.DEFAULT_SCALE_RATIO;
            mAnimationDuration = BlurPopupWindow.DEFAULT_ANIMATION_DURATION;
        }

        public Builder<T> setContentView(View contentView) {
            mContentView = contentView;
            return this;
        }

        public Builder<T> setContentView(int resId) {
            View view = LayoutInflater.from(mContext).inflate(resId, new FrameLayout(mContext), false);
            mContentView = view;
            return this;
        }

        public Builder<T> setString(String string, int textView){
            if (mContentView != null){
                mTextView = mContentView.findViewById(textView);
                mText = string;
                mTextView.setText(string);
            }
            return this;
        }

        public Builder<T> setAutoDismiss(boolean autoDismiss){
            this.autoDismiss = autoDismiss;
            return this;
        }

        public Builder<T> bindContentViewClickListener(View.OnClickListener listener) {
            if (mContentView != null) {
                mContentView.setClickable(true);
                mContentView.setOnClickListener(listener);
            }
            return this;
        }

        public Builder<T> bindClickListener(View.OnClickListener listener, int... views) {
            if (mContentView != null) {
                for (int viewId : views) {
                    View view = mContentView.findViewById(viewId);
                    if (view != null) {
                        view.setOnClickListener(listener);
                    }
                }
            }
            return this;
        }

        public Builder<T> setGravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        public Builder<T> setTintColor(int tintColor) {
            mTintColor = tintColor;
            return this;
        }

        public Builder<T> setScaleRatio(float scaleRatio) {
            if (scaleRatio <= 0 || scaleRatio > 1) {
                Log.w(TAG, "scaleRatio invalid: " + scaleRatio + ". It can only be (0, 1]");
                return this;
            }
            mScaleRatio = scaleRatio;
            return this;
        }

        public Builder<T> setBlurRadius(float blurRadius) {
            if (blurRadius < 0 || blurRadius > 25) {
                Log.w(TAG, "blurRadius invalid: " + blurRadius + ". It can only be [0, 25]");
                return this;
            }
            mBlurRadius = blurRadius;
            return this;
        }

        public Builder<T> setAnimationDuration(long animatingDuration) {
            if (animatingDuration < 0) {
                Log.w(TAG, "animatingDuration invalid: " + animatingDuration + ". It can only be (0, ..)");
                return this;
            }
            mAnimationDuration = animatingDuration;
            return this;
        }

        public Builder<T> setDismissOnTouchBackground(boolean dismissOnTouchBackground) {
            mDismissOnTouchBackground = dismissOnTouchBackground;
            return this;
        }

        public Builder<T> setDismissOnClickBack(boolean dismissOnClickBack) {
            mDismissOnClickBack = dismissOnClickBack;
            return this;
        }

        public Builder<T> setOnDismissListener(OnDismissListener onDismissListener) {
            mOnDismissListener = onDismissListener;
            return this;
        }

        protected T createPopupWindow() {
            //noinspection unchecked
            return (T) new BlurPopupWindow(mContext);
        }

        public T build() {
            T popupWindow = createPopupWindow();
            if (mContentView != null) {
                ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
                if (layoutParams == null || !(layoutParams instanceof FrameLayout.LayoutParams)) {
                    layoutParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                }
                if (mGravity != -1) {
                    ((LayoutParams) layoutParams).gravity = mGravity;
                }
                mContentView.setLayoutParams(layoutParams);
                popupWindow.setContentView(mContentView);
            }
            popupWindow.setTintColor(mTintColor);
            popupWindow.setAnimationDuration(mAnimationDuration);
            popupWindow.setBlurRadius(mBlurRadius);
            popupWindow.setScaleRatio(mScaleRatio);
            popupWindow.setDismissOnTouchBackground(mDismissOnTouchBackground);
            popupWindow.setDismissOnClickBack(mDismissOnClickBack);
            popupWindow.setOnDismissListener(mOnDismissListener);
            popupWindow.setText(mText, mTextView);
            popupWindow.setAutoDismiss(autoDismiss);

            return popupWindow;
        }
    }

    private static int getNaviHeight(Activity activity) {
        if (activity == null) {
            return 0;
        }
        Display display = activity.getWindowManager().getDefaultDisplay();
        int contentHeight = activity.getResources().getDisplayMetrics().heightPixels;
        int realHeight = 0;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final DisplayMetrics metrics = new DisplayMetrics();
            display.getRealMetrics(metrics);
            realHeight = metrics.heightPixels;
        } else {
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return realHeight - contentHeight;
    }

}