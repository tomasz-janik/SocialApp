package pl.itomaszjanik.test.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.ExtendedComponents.TextViewClickable;
import pl.itomaszjanik.test.Posts.*;
import pl.itomaszjanik.test.Remote.GenerateIDCallback;

import java.util.List;

public class TopFeed extends Fragment implements ReactNoteCallback, NoteClickListener, GetPostsCallback,
        UpdatePostCallback, OnEndScrolled, NoteNoMoreClickListener,
        GenerateIDCallback, SwipeRefreshLayout.OnRefreshListener {

    private static final String TYPE_DAILY = "get_posts_top_daily.php";
    private static final String TYPE_WEEKLY = "get_posts_top_weekly.php";
    private static final String TYPE_MONTHLY = "get_posts_top_monthly.php";
    private static final String TYPE_ALL = "get_posts_top_all.php";
    private static final String TYPE_COMMENTED = "get_posts_top_daily.php";

    private static final int GEN_LOAD = 0;
    private static final int GEN_START = 1;
    private static final int GEN_REACT = 2;

    private String TYPE_CURRENT = TYPE_DAILY;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Note currentNote;
    private View currentView;
    private boolean started = false;

    private TextViewClickable currentClicked;


    private RecyclerView recyclerView;
    private NoteAdapter mNoteAdapter;

    private CardView nonePosts;

    private boolean loading = false;
    private int page = 0;

    private RelativeLayout refreshLayout;
    private RelativeLayout mainLayout;

    private SharedPreferences sharedPreferences;
    private int userID;

    public TopFeed(){
    }

    public static TopFeed newInstance() {
        return new TopFeed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feed_top, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
        ((TextViewClickable) (view.findViewById(R.id.top_daily_text))).changeState();
        currentClicked = view.findViewById(R.id.top_daily_text);
        nonePosts = view.findViewById(R.id.posts_none);
        initListeners(view, R.id.top_daily);
        initListeners(view, R.id.top_weekly);
        initListeners(view, R.id.top_monthly);
        initListeners(view, R.id.top_alltime);
        initListeners(view, R.id.top_commented);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (currentNote != null){
            userID = sharedPreferences.getInt("userID", 0);
            Utilities.updatePost(userID,this, currentNote);
        }
    }

    @Override
    public void getPostSucceeded(List<Note> list){
        if (isAdded()){
            nonePosts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (list.size() != 0){
                refreshLayout.setVisibility(View.GONE);
                if (loading){
                    mNoteAdapter.insert(list);
                    loading = false;
                }
                else{
                    mNoteAdapter.removeAll();
                    mNoteAdapter.insert(list);
                }
            }
            else if (mNoteAdapter.getItemCount() != 0){
                if (page == 0){
                    recyclerView.setVisibility(View.GONE);
                    nonePosts.setVisibility(View.VISIBLE);
                }
                mNoteAdapter.insertNull();
            }
            else if (mNoteAdapter.getItemCount() == 0){
                nonePosts.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.INVISIBLE);
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void getPostFailed(){
        if (isAdded()){
            mSwipeRefreshLayout.setRefreshing(false);
            if (isAdded()){
                Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.couldnt_refresh));
            }
            if (recyclerView == null){
                refreshLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void getPostNoInternet(){
        if (isAdded()){
            mSwipeRefreshLayout.setRefreshing(false);
            Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.no_internet));
        }
    }

    @Override
    public void onRefresh() {
        loading = false;
        page = 0;
        getPosts(TYPE_CURRENT);
    }

    @Override
    public void reactNoteLikeSucceeded(Note note, View view){
        if (isAdded()){
            note.changeLiked();
            note.incrementLikes();
            ((TextView)view.findViewById(R.id.note_like_number)).setText(String.valueOf(note.getLikes()));
            ((TextView)view.findViewById(R.id.note_like_text)).setTextColor(Color.BLUE);
        }
    }

    @Override
    public void reactNoteUnlikeSucceeded(Note note, View view){
        if (isAdded()){
            note.changeLiked();
            note.decrementLikes();
            ((TextView)view.findViewById(R.id.note_like_number)).setText(String.valueOf(note.getLikes()));
            ((TextView)view.findViewById(R.id.note_like_text)).setTextColor(Color.parseColor("#747474"));
        }
    }

    @Override
    public void reactNoteLikeFailed(){
        if (isAdded()){
            Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.couldnt_like_post));
        }
    }

    @Override
    public void reactNoteUnlikeFailed(){
        if (isAdded()){
            Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.couldnt_unlike_post));
        }
    }

    @Override
    public void reactNoteNoInternet(){
        if (isAdded()){
            Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.no_internet));
        }
    }

    @Override
    public void onItemClick(View view, Note note){
        Bundle data = new Bundle();
        data.putParcelable("note", Parcels.wrap(note));

        currentNote = note;
        currentView = view;

        Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
        intent.putExtras(data);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onLikeClick(View view, Note note){
        currentView = view;
        currentNote = note;
        userID = sharedPreferences.getInt("userID", 0);

        if (userID == 0){
            Utilities.generateID(GEN_REACT, this, getContext());
        }
        else{
            Utilities.onLikeNoteClick(userID, getContext(), TopFeed.this, view, note);
        }
    }

    @Override
    public void updatePostSucceeded(Note note){
        if (isAdded() && currentView != null && note != null){
            currentNote.setLiked(note.getLiked());
            currentNote.setLikes(note.getLikes());
            currentNote.setComments(note.getComments());
            ((TextView)(currentView.findViewById(R.id.note_item_comments_number))).setText(String.valueOf(note.getComments()));
            TextView likes_number = currentView.findViewById(R.id.note_like_number);
            likes_number.setText(String.valueOf(note.getLikes()));
            if (note.getLiked()){
                ((TextView)(currentView.findViewById(R.id.note_like_text))).setTextColor(Color.BLUE);
            }
            else{
                ((TextView)(currentView.findViewById(R.id.note_like_text))).setTextColor(Color.parseColor("#747474"));
            }
        }
    }

    @Override
    public void updatePostFailed(){ }

    @Override
    public void onGenerateSuccess(String username, int userID, int task){
        this.userID = userID;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putInt("userID", userID);
        editor.apply();

        switch (task){
            case GEN_LOAD:
                getPosts(TYPE_CURRENT);
                break;
            case GEN_START:
                loadPosts();
                break;
            case GEN_REACT:
                onLikeClick(currentView, currentNote);
                break;
        }
    }

    @Override
    public void onGenerateFailed(int task){
        switch (task){
            case GEN_LOAD:
                getPostFailed();
                break;
            case GEN_START:
                getPostFailed();
                break;
            case GEN_REACT:
                reactNoteLikeFailed();
                break;
        }
    }

    @Override
    public void onGenerateNoInternet(int task){
        switch (task){
            case GEN_LOAD:
                getPostNoInternet();
                break;
            case GEN_START:
                getPostNoInternet();
                break;
            case GEN_REACT:
                reactNoteNoInternet();
                break;
        }
    }

    @Override
    public void onRefreshClick(){
        recyclerView.smoothScrollToPosition(0);
        loading = false;
        page = 0;
        getPosts(TYPE_CURRENT);
    }

    @Override
    public void onEnd(){
        if (!loading && started){
            page++;
            loading = true;
            getPosts(TYPE_CURRENT);
        }
    }

    private void initListeners(final View view, final int layout){
        view.findViewById(layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = getValue(layout);
                final TextViewClickable textView = getTextView(value, view);
                if (currentClicked != textView){
                    currentClicked.changeState();
                    textView.changeState();
                    currentClicked = textView;
                    currentNote = null;
                    currentView = null;
                    loading = false;
                    page = 0;
                    switch (value){
                        case Values.DAILY:
                            TYPE_CURRENT = TYPE_DAILY;
                            break;
                        case Values.WEEKLY:
                            TYPE_CURRENT = TYPE_WEEKLY;
                            break;
                        case Values.MONTHLY:
                            TYPE_CURRENT = TYPE_MONTHLY;
                            break;
                        case Values.ALLTIME:
                            TYPE_CURRENT = TYPE_ALL;
                            break;
                        case Values.COMMENTED:
                            TYPE_CURRENT = TYPE_COMMENTED;
                            break;
                    }
                    getPosts(TYPE_CURRENT);
                }
                else{
                    loading = false;
                    page = 0;
                    onRefresh();
                }
            }
        });
    }

    private int getValue(int layout){
        switch (layout){
            case R.id.top_daily:
                return Values.DAILY;
            case R.id.top_weekly:
                return Values.WEEKLY;
            case R.id.top_monthly:
                return Values.MONTHLY;
            case R.id.top_alltime:
                return Values.ALLTIME;
            case R.id.top_commented:
                return Values.COMMENTED;
            default:
                return Values.DAILY;
        }
    }

    private TextViewClickable getTextView(int i, View view){
        switch (i){
            case Values.DAILY:
                return view.findViewById(R.id.top_daily_text);
            case Values.WEEKLY:
                return view.findViewById(R.id.top_weekly_text);
            case Values.MONTHLY:
                return view.findViewById(R.id.top_monthly_text);
            case Values.ALLTIME:
                return view.findViewById(R.id.top_alltime_text);
            case Values.COMMENTED:
                return view.findViewById(R.id.top_commented_text);
            default:
                return view.findViewById(R.id.top_daily_text);
        }
    }

    private void init(View view){
        initRecyclerView(view);
        initRefreshLayout(view);
        initSwipeRefreshLayout(view);

        mainLayout = view.findViewById(R.id.main_layout);
        sharedPreferences = getContext().getSharedPreferences(Values.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", 0);
    }

    private void initSwipeRefreshLayout(View view){
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setProgressViewOffset(false,
                getResources().getDimensionPixelSize(R.dimen.refresher_offset_top),
                getResources().getDimensionPixelSize(R.dimen.refresher_offset_end_top));

    }

    private void initRefreshLayout(View view){
        refreshLayout = view.findViewById(R.id.feed_refresh_layout);
        refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeRefreshLayout.setRefreshing(true);
                recyclerView.smoothScrollToPosition(0);
                loading = false;
                page = 0;
                getPosts(TYPE_CURRENT);
            }
        });

    }

    private void initRecyclerView(View view){
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.item_separator, null);
        if (divider != null)
            dividerItemDecoration.setDrawable(divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnScrollListener(new ListScrollBottomListener((NavigationController) getActivity().findViewById(R.id.navigation_bottom)));

        mNoteAdapter = new NoteAdapter(R.layout.note_feed_top_top, getContext());
        mNoteAdapter.initListeners(this, this, this);
        recyclerView.setAdapter(mNoteAdapter);
    }

    public void loadPosts(){
        if (!started){
            userID = sharedPreferences.getInt("userID", 0);

            if (userID == 0){
                Utilities.generateID(GEN_START, this, getContext());
            }
            else{
                Utilities.getPostsTop(userID, page, TYPE_CURRENT, this, getContext());
            }
            mSwipeRefreshLayout.setRefreshing(true);
            started = true;
        }
    }

    public boolean getStarted(){
        return started;
    }

    private void getPosts(String type){
        userID = sharedPreferences.getInt("userID", 0);
        if (userID == 0){
            Utilities.generateID(GEN_LOAD, this, getContext());
        }
        else{
            Utilities.getPostsTop(userID, page, type, this, getContext());
        }
    }

}