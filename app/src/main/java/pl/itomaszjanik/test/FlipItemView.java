package pl.itomaszjanik.test;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;

public class FlipItemView extends OnlyIconItemView{

    public FlipItemView(Context context) {
        super(context);
    }

    public FlipItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    ObjectAnimator mAnimator;

    @Override
    public void onRepeat() {
        super.onRepeat();

        if (mAnimator == null){
            ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.icon),"Rotation",0f,-360f);
            animator.setDuration(1000);
            mAnimator = animator;
        }

        if (!mAnimator.isStarted()){
            mAnimator.start();
        }

    }
}
