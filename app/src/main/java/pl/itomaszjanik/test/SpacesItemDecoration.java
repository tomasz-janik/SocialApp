package pl.itomaszjanik.test;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) == state.getItemCount() - 1){
            outRect.bottom = space;
            outRect.top = 0;
        }
        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }
}
