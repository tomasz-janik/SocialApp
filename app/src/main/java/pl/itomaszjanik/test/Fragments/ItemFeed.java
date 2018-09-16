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
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Posts.*;
import pl.itomaszjanik.test.Remote.GenerateIDCallback;

import java.util.List;

public class ItemFeed extends Fragment implements ReactNoteCallback, NoteClickListener, GetPostsCallback,
        UpdatePostCallback, OnEndScrolled, NoteNoMoreClickListener, GenerateIDCallback, SwipeRefreshLayout.OnRefreshListener {

    private static final int GEN_LOAD = 0;
    private static final int GEN_REACT = 1;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Note currentNote;
    private View currentView;

    private RecyclerView recyclerView;
    private NoteAdapter mNoteAdapter;
    private boolean loading = true;
    private int page = 0;

    private int userID;

    private RelativeLayout refreshLayout;
    private BottomPopup bottomPopup;
    private SharedPreferences sharedPreferences;

    public ItemFeed(){
    }

    public static ItemFeed newInstance() {
        return new ItemFeed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feed, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
        //createPosts();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (currentNote != null){
            Utilities.updatePost(this, currentNote);
        }
    }

    @Override
    public void getPostSucceeded(List<Note> list){
        if (isAdded()){
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
                mSwipeRefreshLayout.setRefreshing(false);
            }
            else{
                mSwipeRefreshLayout.setRefreshing(false);
                mNoteAdapter.insertNull();
            }
        }
    }

    @Override
    public void getPostFailed(){
        if (isAdded()){
            mSwipeRefreshLayout.setRefreshing(false);
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.couldnt_refresh), bottomPopup);
            if (recyclerView == null){
                refreshLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void getPostNoInternet(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.no_internet), bottomPopup);
        }
    }

    @Override
    public void onRefresh() {
        getPosts();
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
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.couldnt_like_post), bottomPopup);
        }
    }

    @Override
    public void reactNoteUnlikeFailed(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.couldnt_unlike_post), bottomPopup);
        }
    }

    @Override
    public void reactNoteNoInternet(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.no_internet), bottomPopup);
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

        int userID = sharedPreferences.getInt("userID",0);
        if (userID == 0){
            Utilities.generateID(GEN_REACT, this, getContext());
        }
        else{
            Utilities.onLikeNoteClick(userID, getContext(), ItemFeed.this, view, note);
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
                getPosts();
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
        getPosts();
    }

    @Override
    public void onEnd(){
        if (!loading){
            page++;
            loading = true;
            getPosts();
        }
    }

    private void init(View view){
        initRecyclerView(view);
        initRefreshLayout(view);
        initSwipeRefreshLayout(view);

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
                getResources().getDimensionPixelSize(R.dimen.refresher_offset),
                getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loading = true;
                page = 0;
                getPosts();
            }
        });
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
                getPosts();
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

        mNoteAdapter = new NoteAdapter(R.layout.note_feed_top, getContext());
        mNoteAdapter.initListeners(this, this, this);
        recyclerView.setAdapter(mNoteAdapter);
    }

    private void getPosts(){
        //this means that user is not signed and also has no ID
        if (userID == 0){
            Utilities.generateID(GEN_LOAD,this, getContext());
        }
        else{
            Utilities.getPosts(userID, page, this, getContext());
        }
    }

}
