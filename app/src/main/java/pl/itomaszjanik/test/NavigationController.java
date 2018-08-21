package pl.itomaszjanik.test;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

public class NavigationController extends LinearLayout {

    private ObjectAnimator animator;
    private boolean hide = false;

    public NavigationController(Context context){
        super(context);
    }

    public NavigationController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationController(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void hideLayoutInstant() {
        if(!hide){
            hide = true;
            getAnimatorInstant().setDuration(0).start();
        }
        else{
            hide = true;
            getAnimatorInstant().cancel();
            getAnimatorInstant().setDuration(0).start();
        }
    }

    public void showLayoutInstant() {
        if(hide){
            hide = false;
            getAnimatorInstant().setDuration(1000).reverse();
        }
    }

    public void hideLayout() {
        if(!hide){
            hide = true;
            getAnimator().setDuration(300).start();
        }
    }

    public void showLayout() {
        if(hide){
            hide = false;
            getAnimator().setDuration(300).reverse();
        }
    }

    private ObjectAnimator getAnimator(){

        if (animator == null){
            animator = ObjectAnimator.ofFloat(this,"translationY",0,this.getHeight());

            animator.setDuration(300);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());

        }
        return animator;
    }

    private ObjectAnimator getAnimatorInstant(){

        if (animator == null){
            animator = ObjectAnimator.ofFloat(this,"translationY",0,this.getHeight());

            animator.setDuration(0);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());

        }
        return animator;
    }
}
