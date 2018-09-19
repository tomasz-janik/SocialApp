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
import pl.itomaszjanik.test.Posts.*;
import pl.itomaszjanik.test.Remote.GenerateIDCallback;

import java.util.List;

public class ProfileSigned extends Fragment implements NoteClickListener, GetPostsCallback, ReactNoteCallback,
        UpdatePostCallback, NoteNoMoreClickListener, GenerateIDCallback, OnEndScrolled {

    private static final int GEN_LOAD = 0;
    private static final int GEN_START = 1;
    private static final int GEN_REACT = 2;

    private RelativeLayout mainLayout;
    private RecyclerView recyclerView;
    private NoteAdapter mNoteAdapter;
    private CardView nonePosts;

    private Note currentNote;
    private View currentView;

    private boolean started = false;
    private boolean loading = true;
    private int page = 0;

    private SharedPreferences sharedPreferences;
    private int userID;

    public ProfileSigned() {
    }

    public static ProfileSigned newInstance() {
        return new ProfileSigned();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_signed, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        Bundle bundle = getArguments();
        if (bundle != null){
            if (bundle.getBoolean("refresh", false)){
                getPosts();
            }
        }
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
                if (loading){
                    mNoteAdapter.insert(list);
                    loading = false;
                }
                else{
                    mNoteAdapter.removeAll();
                    mNoteAdapter.insert(list);
                }
            }
            else if (mNoteAdapter.getItemCount() == 0){
                nonePosts.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void getPostFailed(){
        if (isAdded()){
            Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.couldnt_refresh));
        }
    }

    @Override
    public void getPostNoInternet(){
        if (isAdded()){
            Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.no_internet));
        }
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
            Utilities.onLikeNoteClick(userID, getContext(), ProfileSigned.this, view, note);
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
        getPosts();
    }

    @Override
    public void onEnd(){
        if (!loading && started){
            page++;
            loading = true;
            getPosts();
        }
    }


    private void init(View view){
        nonePosts = view.findViewById(R.id.posts_none);
        mainLayout = view.findViewById(R.id.main_layout);

        sharedPreferences = getContext().getSharedPreferences(Values.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", 0);

        initRecyclerView(view);
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

    void loadPosts(){
        if (!started && isAdded()){
            userID = sharedPreferences.getInt("userID", 0);

            if (userID == 0){
                Utilities.generateID(GEN_START, this, getContext());
            }
            else{
                Utilities.getPostsProfile(userID, sharedPreferences.getString("username", "a"),
                        page, this, getContext());
                started = true;
            }
        }
    }

    boolean getStarted(){
        return started;
    }

    private void getPosts(){
        userID = sharedPreferences.getInt("userID", 0);
        if (userID == 0){
            Utilities.generateID(GEN_LOAD, this, getContext());
        }
        else{
            Utilities.getPostsProfile(userID, sharedPreferences.getString("username", "a"),
                    page, this, getContext());
        }
    }

}
