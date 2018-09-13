package pl.itomaszjanik.test.Fragments;

import android.content.Intent;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Posts.GetPostsCallback;
import pl.itomaszjanik.test.Posts.NoteAdapter;
import pl.itomaszjanik.test.Posts.NoteClickListener;
import pl.itomaszjanik.test.Posts.ReactNoteCallback;
import pl.itomaszjanik.test.Remote.PostService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ItemFeed extends Fragment implements ReactNoteCallback, NoteClickListener, GetPostsCallback,
        OnEndScrolled, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;


    private Note currentNote;
    private View currentView;

    private RecyclerView recyclerView;
    private NoteAdapter mNoteAdapter;
    private boolean loading = true;
    private int page = 0;

    private RelativeLayout refreshLayout;
    private BottomPopup bottomPopup;

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
        View rootView = inflater.inflate(R.layout.feed, container,false);

        refreshLayout = rootView.findViewById(R.id.feed_refresh_layout);
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

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
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
        return rootView;
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
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            Call<Note> call = service.updatePost(currentNote.getId(), 1);
            call.enqueue(new Callback<Note>() {
                @Override
                public void onResponse(Call<Note> call, Response<Note> response) {
                    if (currentView != null){
                        Note note = response.body();
                        if (note != null){
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
                }

                @Override
                public void onFailure(Call<Note> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void getPostSucceeded(List<Note> list){
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

    @Override
    public void getPostFailed(){
        mSwipeRefreshLayout.setRefreshing(false);
        bottomPopup = Utilities.getBottomPopupText(getContext(),
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_refresh), bottomPopup);
        if (recyclerView == null){
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getPostNoInternet(){
        bottomPopup = Utilities.getBottomPopupText(getContext(),
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.no_internet), bottomPopup);
    }

    @Override
    public void onRefresh() {
        getPosts();
    }

    @Override
    public void reactNoteLikeSucceeded(Note note, View view){
        note.changeLiked();
        note.incrementLikes();
        ((TextView)view.findViewById(R.id.note_like_number)).setText(String.valueOf(note.getLikes()));
        ((TextView)view.findViewById(R.id.note_like_text)).setTextColor(Color.BLUE);
    }

    @Override
    public void reactNoteUnlikeSucceeded(Note note, View view){
        note.changeLiked();
        note.decrementLikes();
        ((TextView)view.findViewById(R.id.note_like_number)).setText(String.valueOf(note.getLikes()));
        ((TextView)view.findViewById(R.id.note_like_text)).setTextColor(Color.parseColor("#747474"));
    }

    @Override
    public void reactNoteLikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(getContext(),
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_like_post), bottomPopup);
    }

    @Override
    public void reactNoteUnlikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(getContext(),
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_unlike_post), bottomPopup);
    }

    @Override
    public void reactNoteNoInternet(){
        bottomPopup = Utilities.getBottomPopupText(getContext(),
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.no_internet), bottomPopup);
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
        Utilities.onLikeNoteClick(getContext(), ItemFeed.this, view, note);
    }

    @Override
    public void onEnd(){
        if (mNoteAdapter.getItemCount() < 10){
            if (!loading){
                page++;
                loading = true;
                getPosts();
            }
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

        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));

        recyclerView.addOnScrollListener(new ListScrollBottomListener((NavigationController) getActivity().findViewById(R.id.navigation_bottom)));
        recyclerView.setAdapter(new NoteAdapter(ItemFeed.this, getContext()));

        mNoteAdapter = new NoteAdapter(this, getContext());
        mNoteAdapter.initListeners(this, this);
        recyclerView.setAdapter(mNoteAdapter);
    }

    private void getPosts(){
        Utilities.getPosts(1, page, this, getContext());
    }



}
