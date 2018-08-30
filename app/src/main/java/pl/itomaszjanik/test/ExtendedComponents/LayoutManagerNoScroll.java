package pl.itomaszjanik.test.ExtendedComponents;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by Bhavesh on 15-06-2017.
 */

public class LayoutManagerNoScroll extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public LayoutManagerNoScroll(Context context) {
        super(context);
    }

    public LayoutManagerNoScroll(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LayoutManagerNoScroll(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollHorizontally() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollHorizontally();
    }
}