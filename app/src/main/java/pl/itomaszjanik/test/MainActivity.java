package pl.itomaszjanik.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import pl.itomaszjanik.test.Fragments.AddPost;
import pl.itomaszjanik.test.Fragments.ItemFeed;
import pl.itomaszjanik.test.Fragments.Search;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static NavigationController mNavigationControllerBottom, mNavigationControllerTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUpperTab();
        initLowerTab();
    }

    private void initUpperTab(){
        PageNavigationView pageTopTabLayout = (PageNavigationView) findViewById(R.id.tab_top);

        mNavigationControllerTop = pageTopTabLayout.custom()
                .addItem(newItem(R.drawable.ic_nearby_gray_24dp,R.drawable.ic_nearby_red_24dp))
                //.addItem(newItem(R.drawable.ic_add_gray_24dp,R.drawable.ic_add_red_24dp))
                .addItem(newItem(R.drawable.ic_restore_gray_24dp,R.drawable.ic_restore_red_24dp))
                .build();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new TestViewPagerAdapter(getSupportFragmentManager()));
        mNavigationControllerTop.setupWithViewPager(viewPager);
    }

    private void initLowerTab(){
        PageNavigationView pageBottomTabLayout = (PageNavigationView) findViewById(R.id.tab_bottom);

        mNavigationControllerBottom = pageBottomTabLayout.custom()
                .addItem(newItem(R.drawable.ic_nearby_gray_24dp,R.drawable.ic_nearby_red_24dp))
                .addItem(newItem(R.drawable.ic_add_gray_24dp,R.drawable.ic_add_red_24dp))
                .addItem(newItem(R.drawable.ic_restore_gray_24dp,R.drawable.ic_restore_red_24dp))
                .build();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new TestViewPagerAdapter(getSupportFragmentManager()));
        mNavigationControllerBottom.setupWithViewPager(viewPager);
    }

    private BaseTabItem newItem(int drawable, int checkedDrawable){
        FlipItemView onlyIconItemView = new FlipItemView(this);
        onlyIconItemView.initialize(drawable,checkedDrawable);
        return onlyIconItemView;
    }

    public static class ListScrollListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(dy > 8){
                mNavigationControllerBottom.hideBottomLayout();
            } else if(dy < -8){
                mNavigationControllerBottom.showBottomLayout();
            }
        }
    }

    private class TestViewPagerAdapter extends FragmentPagerAdapter {

        TestViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return ItemFeed.newInstance();
                case 1:
                    return AddPost.newInstance();
                case 2:
                    return Search.newInstance();
                case 3:
                    return Search.newInstance();
                case 4:
                    return Search.newInstance();
                default:
                    return ItemFeed.newInstance();

            }
        }

        @Override
        public int getCount() {
            return mNavigationControllerBottom.getItemCount() + mNavigationControllerTop.getItemCount();
        }
    }
}