package pl.itomaszjanik.test;

import android.graphics.drawable.Drawable;
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
import android.widget.*;

import pl.itomaszjanik.test.Fragments.AddPost;
import pl.itomaszjanik.test.Fragments.ItemFeed;
import pl.itomaszjanik.test.Fragments.Search;

public class MainActivity extends AppCompatActivity {

    private static NavigationController mNavigationControllerBottom, mNavigationControllerTop;
    private TestViewPagerAdapter testViewPagerAdapter;
    private ViewPager viewPager;
    private ButtonLogic buttonLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testViewPagerAdapter = new TestViewPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(testViewPagerAdapter);

        mNavigationControllerTop = new NavigationController( (LinearLayout) findViewById(R.id.navigation_top));
        mNavigationControllerBottom = new NavigationController( (LinearLayout) findViewById(R.id.navigation_bottom));

        buttonLogic = new ButtonLogic();
        initImages();
    }

    private void initImages(){
        ((CustomImage) (findViewById(R.id.icon_feed))).init(R.drawable.ic_restore_red_24dp, R.drawable.ic_restore_gray_24dp);
        ((CustomImage) (findViewById(R.id.icon_feed))).changeState();
        ((CustomImage) (findViewById(R.id.icon_add))).init(R.drawable.ic_add_red_24dp, R.drawable.ic_add_gray_24dp);
        ((CustomImage) (findViewById(R.id.icon_search))).init(R.drawable.ic_nearby_red_24dp, R.drawable.ic_nearby_gray_24dp);
        ((CustomImage) (findViewById(R.id.icon_top))).init(R.drawable.ic_restore_red_24dp, R.drawable.ic_restore_gray_24dp);
        ((CustomImage) (findViewById(R.id.icon_profile))).init(R.drawable.ic_restore_red_24dp, R.drawable.ic_restore_gray_24dp);
    }

    public void clickFeed(View view){
        buttonLogic.clickFeed();
    }
    public void clickAdd(View view){
        buttonLogic.clickAdd();
    }
    public void clickSearch(View view){
        buttonLogic.clickSearch();
    }
    public void clickTop(View view){
        buttonLogic.clickTop();
    }
    public void clickProfile(View view){
        buttonLogic.clickProfile();
    }


    public static class ListScrollListener extends RecyclerView.OnScrollListener{

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

    private class TestViewPagerAdapter extends FragmentPagerAdapter {

        TestViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case Values.INDEX_FEED:
                    return ItemFeed.newInstance();
                case Values.INDEX_ADD:
                    return AddPost.newInstance();
                case Values.INDEX_SEARCH:
                    return Search.newInstance();
                case Values.INDEX_TOP:
                    return Search.newInstance();
                case Values.INDEX_PROFILE:
                    return Search.newInstance();
                default:
                    return ItemFeed.newInstance();

            }
        }

        @Override
        public int getCount() {
            return Values.NO_OF_TABS;
        }
    }

    private class ButtonLogic{
        private int currentPage = 0;
        ButtonLogic(){}

        public void clickFeed(){
            updatePage();
            if (currentPage == Values.INDEX_FEED){
                viewPager.scrollTo(viewPager.getScrollX(), 0);
            }
            else{
                updateColors(currentPage, Values.INDEX_FEED);
                viewPager.setCurrentItem(Values.INDEX_FEED, true);
            }
        }

        public void clickAdd(){
            updatePage();
            if (currentPage != Values.INDEX_ADD){
                updateColors(currentPage, Values.INDEX_ADD);
                viewPager.setCurrentItem(Values.INDEX_ADD, true);
            }
        }

        public void clickSearch(){
            updatePage();
            if (currentPage != Values.INDEX_SEARCH){
                updateColors(currentPage, Values.INDEX_SEARCH);
                viewPager.setCurrentItem(Values.INDEX_SEARCH, true);
            }
        }

        public void clickTop(){
            updatePage();
            if (currentPage != Values.INDEX_TOP){
                updateColors(currentPage, Values.INDEX_TOP);
                viewPager.setCurrentItem(Values.INDEX_TOP, true);
            }
        }

        public void clickProfile(){
            updatePage();
            if (currentPage != Values.INDEX_PROFILE){
                updateColors(currentPage, Values.INDEX_PROFILE);
                viewPager.setCurrentItem(Values.INDEX_PROFILE, true);
            }
        }

        private void updatePage(){
            currentPage = viewPager.getCurrentItem();
        }

        private void updateColors(int current, int next){
            getImage(current).changeState();
            getImage(next).changeState();
        }

        private CustomImage getImage(int index){
            switch (index){
                case Values.INDEX_FEED:
                    return findViewById(R.id.icon_feed);
                case Values.INDEX_ADD:
                    return findViewById(R.id.icon_add);
                case Values.INDEX_SEARCH:
                    return findViewById(R.id.icon_search);
                case Values.INDEX_TOP:
                    return findViewById(R.id.icon_top);
                case Values.INDEX_PROFILE:
                    return findViewById(R.id.icon_profile);
                default:
                    return findViewById(R.id.icon_profile);
            }
        }
    }
}