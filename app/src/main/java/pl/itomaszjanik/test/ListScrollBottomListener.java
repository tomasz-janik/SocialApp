package pl.itomaszjanik.test;

import android.support.v7.widget.RecyclerView;

public class ListScrollBottomListener extends RecyclerView.OnScrollListener{

    private NavigationController mNavigationControllerBottom;

    public ListScrollBottomListener(NavigationController navigationController){
        mNavigationControllerBottom = navigationController;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(dy > 8){
            mNavigationControllerBottom.hideLayout();
        } else if(dy < -6){
            mNavigationControllerBottom.showLayout();
        }
    }

    @Override
    public void onScrollStateChanged (RecyclerView recyclerView, int newState){
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE)
            mNavigationControllerBottom.showLayout();
    }
}