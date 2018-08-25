package pl.itomaszjanik.test;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

public class NavigationController extends LinearLayout {

    private AnimatorSet animator2, animatorReverse;
    private ObjectAnimator animator;
    private boolean hide = false;
    private boolean hiding = false;
    private boolean showing = false;

    public NavigationController(Context context){
        super(context);
    }

    public NavigationController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationController(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void hideLayoutIns22tant() {
        if(!hide){
            hide = true;
            getAnimator(0).start();
            getAnimatorInstant().setDuration(0).start();
        }
        else{
            getAnimatorInstant().cancel();
            getAnimatorInstant().setDuration(0).start();
        }
    }

    public void hideLayoutInstant(){
        if (!hide && showing){
            hide = true;
            getAnimatorReverse(0).cancel();
            getAnimator(0).start();
        }
        if (!hide){
            hide = true;
            getAnimator(0).start();
        }
    }

    public void showLayoutInstant(){
        if (hide && hiding){
            hide = false;
            getAnimator(0).cancel();
            getAnimatorReverse(1000).start();
        }
        else if (hide){
            hide = false;
            getAnimatorReverse(1000).start();
        }
    }

    public void showLayo2utInstant() {
        if(hide){
            hide = false;
            getAnimatorInstant().setDuration(1000).reverse();
        }
    }

    public void hideLayout() {
        if(!hide){
            hide = true;
            getAnimator(300).start();
        }
    }

    public void showLayout() {
        if(hide){
            hide = false;
            getAnimatorReverse(300).start();
        }
    }

    public void hideLayoutTop() {
        if(!hide){
            hide = true;
            getAnimatorTop().setDuration(300).start();
        }
    }

    public void showLayoutTop() {
        if(hide){
            hide = false;
            getAnimatorTop().setDuration(300).reverse();
        }
    }

    private ObjectAnimator getAnima2tor(){

        if (animator == null){
            animator = ObjectAnimator.ofFloat(this,"translationY",0,this.getHeight());

            animator.setDuration(300);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());

        }
        return animator;
    }

    private AnimatorSet getAnimator(int time){
        if (animator2 == null){
            animator2 = new AnimatorSet();
            ObjectAnimator translate = ObjectAnimator.ofFloat(this,"translationY",0,this.getHeight());
            ObjectAnimator fade = ObjectAnimator.ofFloat(this, "alpha",1, 0);

            animator2.setDuration(time);
            animator2.setInterpolator(new AccelerateDecelerateInterpolator());
            animator2.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    hiding = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    hiding = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    hiding = false;
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator2.playTogether(translate, fade);
        }
        return animator2;
    }

    private AnimatorSet getAnimatorReverse(int time){
        if (animatorReverse == null){
            animatorReverse = new AnimatorSet();
            ObjectAnimator translate = ObjectAnimator.ofFloat(this,"translationY",this.getHeight(), 0);
            ObjectAnimator fade = ObjectAnimator.ofFloat(this, View.ALPHA,0, 1);

            animatorReverse.setDuration(time);
            animatorReverse.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorReverse.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    showing = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    showing = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    showing = false;
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animatorReverse.playTogether(translate, fade);
        }
        return animatorReverse;
    }

    private ObjectAnimator getAnimatorTop(){

        if (animator == null){
            animator = ObjectAnimator.ofFloat(this,"translationY",0, -this.getHeight());

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
