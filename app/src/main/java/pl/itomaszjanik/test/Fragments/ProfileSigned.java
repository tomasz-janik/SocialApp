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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.parceler.Parcels;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.Posts.*;

import java.util.List;

public class ProfileSigned extends Fragment implements NoteClickListener, GetPostsCallback, ReactNoteCallback,
        UpdatePostCallback, NoteNoMoreClickListener, OnEndScrolled {

    private BottomPopup bottomPopup;
    private RecyclerView recyclerView;
    private NoteAdapter mNoteAdapter;

    private Note currentNote;
    private View currentView;

    private boolean started = false;
    private boolean loading = true;
    private int page = 0;

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
                if (loading){
                    mNoteAdapter.insert(list);
                    loading = false;
                }
                else{
                    mNoteAdapter.removeAll();
                    mNoteAdapter.insert(list);
                }
            }
            else{
                mNoteAdapter.insertNull();
            }
        }
    }

    @Override
    public void getPostFailed(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.couldnt_refresh), bottomPopup);
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
        Utilities.onLikeNoteClick(getContext(), ProfileSigned.this, view, note);
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

    public void loadPosts(){
        if (!started){
            Utilities.getPostsProfile(1, page, this, getContext());
            started = true;
        }
    }

    public boolean getStarted(){
        return started;
    }

    private void getPosts(){
        Utilities.getPostsProfile(1, page, this, getContext());
    }

}
