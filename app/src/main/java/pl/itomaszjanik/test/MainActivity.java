package pl.itomaszjanik.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import net.danlew.android.joda.JodaTimeAndroid;
import pl.itomaszjanik.test.ExtendedComponents.CustomImage;
import pl.itomaszjanik.test.Fragments.*;

public class MainActivity extends AppCompatActivity {

    private NavigationController mNavigationControllerBottom, mNavigationControllerTop;
    private TestViewPagerAdapter testViewPagerAdapter;
    private FloatingSearchView searchView;
    private ViewPager viewPager;
    private ButtonLogic buttonLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JodaTimeAndroid.init(this);

        testViewPagerAdapter = new TestViewPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(testViewPagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        mNavigationControllerTop = findViewById(R.id.navigation_top);
        mNavigationControllerBottom = findViewById(R.id.navigation_bottom);
        searchView = findViewById(R.id.floating_search_view);

        buttonLogic = new ButtonLogic();
        initImages();
    }

    private void initImages(){
        ((CustomImage) (findViewById(R.id.icon_feed))).init(R.drawable.icon_wall_active_24dp, R.drawable.icon_wall_inactive_24dp);
        ((CustomImage) (findViewById(R.id.icon_feed))).changeState();

        ((CustomImage) (findViewById(R.id.icon_add))).init(R.drawable.icon_add_24dp, R.drawable.icon_add_24dp);
        ((CustomImage) (findViewById(R.id.icon_search))).init(R.drawable.icon_search_active_24dp, R.drawable.icon_search_inactive_24dp);
        ((CustomImage) (findViewById(R.id.icon_top))).init(R.drawable.icon_top_active_24dp, R.drawable.icon_top_active_24dp);
        ((CustomImage) (findViewById(R.id.icon_profile))).init(R.drawable.icon_profile_inactive_24dp, R.drawable.icon_profile_inactive_24dp);
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


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == Values.INDEX_TOP){
            Fragment noteFragment = getSupportFragmentManager().findFragmentByTag("Note");
            if (noteFragment != null && noteFragment.isVisible()){
                super.onBackPressed();
            }
            else{
                clickFeed(null);
            }
        }
        else if (viewPager.getCurrentItem() == Values.INDEX_SEARCH){
            searchView.setVisibility(View.GONE);
            clickFeed(null);
        }
        else if (viewPager.getCurrentItem() != Values.INDEX_FEED){
            clickFeed(null);
        }
        else{
            super.onBackPressed();
        }
    }

    private class TestViewPagerAdapter extends FragmentPagerAdapter {

        private Fragment mCurrentFragment;

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
                    return TopFeed.newInstance();
                case Values.INDEX_PROFILE:
                    return Profile.newInstance();
            }
            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return Values.NO_OF_TABS;
        }

        Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

    }

    private class ButtonLogic{
        private int currentPage = 0;
        ButtonLogic(){}

        void clickFeed(){
            showNavigation();
            updatePage();
            if (currentPage != Values.INDEX_FEED){
                updateColors(currentPage, Values.INDEX_FEED);
                viewPager.setCurrentItem(Values.INDEX_FEED, true);
                searchView.setVisibility(View.GONE);
            }
            mNavigationControllerBottom.showLayout();
        }

        void clickAdd(){
            updatePage();
            showNavigation();
            if (currentPage != Values.INDEX_ADD){
                updateColors(currentPage, Values.INDEX_ADD);
                viewPager.setCurrentItem(Values.INDEX_ADD, true);
                searchView.setVisibility(View.GONE);
                //mNavigationControllerBottom.hideLayout();
            }
        }

        void clickSearch(){
            updatePage();
            showNavigation();
            if (currentPage != Values.INDEX_SEARCH){
                updateColors(currentPage, Values.INDEX_SEARCH);
                viewPager.setCurrentItem(Values.INDEX_SEARCH, true);
                searchView.setVisibility(View.VISIBLE);
            }
        }

        void clickTop(){
            updatePage();
            showNavigation();
            if (currentPage != Values.INDEX_TOP){
                Utilities.hideKeyboard(MainActivity.this);
                updateColors(currentPage, Values.INDEX_TOP);
                viewPager.setCurrentItem(Values.INDEX_TOP, true);
                searchView.setVisibility(View.GONE);
                TopFeed fragment = (TopFeed)testViewPagerAdapter.getCurrentFragment();
                if (!fragment.getStarted()){
                    fragment.loadPosts();
                }
            }
        }

        void clickProfile(){
            updatePage();
            showNavigation();
            if (currentPage != Values.INDEX_PROFILE){
                Utilities.hideKeyboard(MainActivity.this);
                updateColors(currentPage, Values.INDEX_PROFILE);
                viewPager.setCurrentItem(Values.INDEX_PROFILE, true);
                searchView.setVisibility(View.GONE);
                Profile fragment = (Profile)testViewPagerAdapter.getCurrentFragment();
                if (!fragment.getStarted()){
                    fragment.loadPosts();
                }
            }
        }

        private void showNavigation(){
            mNavigationControllerBottom.showLayout();
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