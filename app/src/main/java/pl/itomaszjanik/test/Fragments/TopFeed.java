package pl.itomaszjanik.test.Fragments;

import android.content.Intent;
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
import android.widget.Toast;
import okhttp3.ResponseBody;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.ExtendedComponents.TextViewClickable;
import pl.itomaszjanik.test.Note;
import pl.itomaszjanik.test.Posts.NoteAdapter;
import pl.itomaszjanik.test.Posts.NoteClickListener;
import pl.itomaszjanik.test.Remote.NoteService;
import pl.itomaszjanik.test.Remote.PostService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class TopFeed extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private TextViewClickable currentClicked;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    List<Note> posts = new ArrayList<>();

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
        View rootView =  inflater.inflate(R.layout.feed_top,container,false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setProgressViewOffset(false,
                getResources().getDimensionPixelSize(R.dimen.refresher_offset),
                getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);
                createPosts();
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_top);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.item_separator, null);
        if (divider != null)
            dividerItemDecoration.setDrawable(divider);

        //recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), R.drawable.item_separator));
        recyclerView.addItemDecoration(dividerItemDecoration);

        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));

        recyclerView.addOnScrollListener(new ListScrollTopListener((NavigationController) getActivity().findViewById(R.id.navigation_feed_top)));
        recyclerView.addOnScrollListener(new ListScrollBottomListener((NavigationController) getActivity().findViewById(R.id.navigation_bottom)));
        createPosts();

        ((TextViewClickable) (view.findViewById(R.id.top_daily_text))).changeState();
        currentClicked = (TextViewClickable) view.findViewById(R.id.top_daily_text);
        initListeners(view, R.id.top_daily);
        initListeners(view, R.id.top_weekly);
        initListeners(view, R.id.top_monthly);
        initListeners(view, R.id.top_alltime);
        initListeners(view, R.id.top_commented);
    }

    private void recyclerAdapter(){
        recyclerView.setAdapter(new NoteAdapter(new NoteClickListener() {
            @Override
            public void onItemClick(View v, Note note) {
                Bundle data = new Bundle();
                data.putParcelable("note", Parcels.wrap(note));

                Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onLikeClick(View v, Note note){
                likePost(note);
            }
        }, getContext()));

    }


    private void initListeners(final View view, final int layout){
        view.findViewById(layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextViewClickable textView = getTextView(getValue(layout), view);
                if (currentClicked != textView){
                    currentClicked.changeState();
                    textView.changeState();
                    currentClicked = textView;
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

    @Override
    public void onRefresh() {
        createPosts();
    }



    private void createPosts(){
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        Call<List<Note>> call = service.getPosts(1, 0);
        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                posts = response.body();
                recyclerAdapter();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                Toast.makeText(getContext(), ":(", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void likePost(Note note){
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        Call<ResponseBody> call = service.likePost(note.getId(), 1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
