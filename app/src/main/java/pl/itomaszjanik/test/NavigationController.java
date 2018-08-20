package pl.itomaszjanik.test;

import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

public class NavigationController {

    private ObjectAnimator animator;
    private LinearLayout layout;
    private boolean hide = false;

    public NavigationController(LinearLayout layout){
        this.layout = layout;
    }

    public void hideLayout() {
        if(!hide){
            hide = true;
            getAnimator().start();
        }
    }

    public void showLayout() {
        if(hide){
            hide = false;
            getAnimator().reverse();
        }
    }

    private ObjectAnimator getAnimator(){

        if (animator == null){
            if (layout != null){
                animator = ObjectAnimator.ofFloat(layout,"translationY",0,layout.getHeight());

                animator.setDuration(300);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
            }

        }
        return animator;
    }
}
