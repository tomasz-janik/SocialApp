package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import okhttp3.ResponseBody;
import org.joda.time.DateTime;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Comments.CommentAdapter;
import pl.itomaszjanik.test.Comments.CommentClickListener;
import pl.itomaszjanik.test.Comments.CommentsDivider;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopup;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopupListener;
import pl.itomaszjanik.test.ExtendedComponents.CustomImage;
import pl.itomaszjanik.test.ExtendedComponents.LayoutManagerNoScroll;
import pl.itomaszjanik.test.Remote.*;
import pl.itomaszjanik.test.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static pl.itomaszjanik.test.Utilities.hideKeyboard;

public class NoteDetailsActivity extends Activity implements FailedCallback, CommentPostCallback, GetCommentsCallback,
        ReactCommentsCallback, UpdateCommentCallback, SwipeRefreshLayout.OnRefreshListener{

    private Note note;
    private TextView content, hashes;
    private EditText input;
    private BottomPopup bottomPopup;
    private EllipsisPopup ellipsisPopup;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView scrollView;

    private int page;

    private Comment currentComment;
    private View currentView;

    private RecyclerView recyclerView;
    private CommentAdapter mCommentAdapter;
    private boolean loading;

    private List<Comment> comments = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            note = Parcels.unwrap(bundle.getParcelable("note"));
            if (note == null){
                note = new Note(0,"TEST", "26/08/2018 22:41:00", "TEST", "#TEST", 0, 0, 0, false);
            }
        }

        prepareView();
        initListeners();
        initRecyclerView();
        getComments();

    }

    @Override
    public void onResume(){
        super.onResume();
        if (currentComment != null){
            Utilities.updateCommentCall(NoteDetailsActivity.this, currentComment);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onRefresh() {
        getComments();
    }


    @Override
    public void likeFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_like_post), bottomPopup);
        note.incrementComments();
    }

    @Override
    public void unlikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_unlike_post), bottomPopup);
    }

    @Override
    public void reactCommentLikeSucceeded(Comment comment, View view){
        comment.changeLiked();
        comment.incrementLikes();
        ((TextView)view.findViewById(R.id.comment_like_number)).setText(String.valueOf(comment.getLikes()));
        ((TextView)view.findViewById(R.id.comment_like_text)).setTextColor(Color.BLUE);
    }

    @Override
    public void reactCommentLikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_like_comment), bottomPopup);
    }

    @Override
    public void reactCommentUnlikeSucceeded(Comment comment, View view){
        comment.changeLiked();
        comment.decrementLikes();
        ((TextView)view.findViewById(R.id.comment_like_number)).setText(String.valueOf(comment.getLikes()));
        ((TextView)view.findViewById(R.id.comment_like_text)).setTextColor(Color.parseColor("#747474"));

    }

    @Override
    public void reactCommentUnlikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_unlike_comment), bottomPopup);
    }

    @Override
    public void reactCommentNoInternet(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.no_internet), bottomPopup);
    }

    @Override
    public void commentPostSucceeded(Comment comment){
        input.setText("");
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.comment_post_added), bottomPopup);
        //getComments(0);
        comments.add(comment);
        mCommentAdapter.notifyItemInserted(comments.size() - 1);
        //scrollView.fullScroll(View.FOCUS_DOWN);
        //scrollView.smoothScrollTo(0,scrollView.getHeight());

        note.incrementComments();
        updateCommentsNumber();
    }

    @Override
    public void commentPostFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.comment_post_couldnt), bottomPopup);
    }

    @Override
    public void commentPostNoInternet(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.no_internet), bottomPopup);
    }

    @Override
    public void getCommentSucceeded(List<Comment> list){
        if (loading){
            comments.remove(comments.size() - 1);
            mCommentAdapter.notifyItemRemoved(comments.size());
            comments.addAll(list);
        }
        else{
            comments = list;
        }
        updateRecyclerAdapter();
    }

    @Override
    public void getCommentFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.comment_load_couldnt), bottomPopup);
    }

    @Override
    public void getCommentNoInternet(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.no_internet), bottomPopup);
    }

    @Override
    public void updateCommentSucceeded(Comment comment){
        if (currentView != null){
            currentComment.setLiked(comment.getLiked());
            currentComment.setLikes(comment.getLikes());
            currentComment.setReplays(comment.getReplays());
            ((TextView)currentView.findViewById(R.id.comment_item_replays)).setText(String.valueOf(comment.getReplays()));
            ((TextView)currentView.findViewById(R.id.comment_like_number)).setText(String.valueOf(comment.getLikes()));
            if (comment.getLiked()){
                ((TextView)(currentView.findViewById(R.id.comment_like_text))).setTextColor(Color.BLUE);
            }
            else{
                ((TextView)(currentView.findViewById(R.id.comment_like_text))).setTextColor(Color.parseColor("#747474"));
            }
        }
    }

    @Override
    public void updateCommentFailed(){}

    private void prepareView(){
        content = (TextView) findViewById(R.id.note_details_content);
        hashes = (TextView) findViewById(R.id.note_details_hashes);
        input = (EditText) findViewById(R.id.comment_insert_text);
        ((CustomImage) (findViewById(R.id.note_details_icon_back))).init(R.drawable.ic_arrow_black_24dp, R.drawable.ic_arrow_black_24dp);

        content.setText(note.getContent());
        hashes.setText(Utilities.prepareHasheshText(note.getHashesh()));

        ((TextView)findViewById(R.id.note_details_user)).setText(note.getUsername());
        ((TextView)findViewById(R.id.note_details_date)).setText(Utilities.decodeDate(note.getDate(), NoteDetailsActivity.this));
        ((TextView)findViewById(R.id.note_details_item_replays)).setText(String.valueOf(note.getComments()));
        ((TextView)findViewById(R.id.note_details_like_number)).setText(String.valueOf(note.getLikes()));

        if (note.getLiked()){
            ((TextView)findViewById(R.id.note_details_like_text)).setTextColor(Color.BLUE);
        }
        updateCommentsNumber();
        //initRefreshLayout();
    }

    private void initRefreshLayout(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
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
                page = 0;
                getComments();
            }
        });
    }

    private void updateCommentsNumber(){
        String noOfComments = Utilities.getCommentVariation(note.getComments(), NoteDetailsActivity.this);
        ((TextView)(findViewById(R.id.note_details_comments_number))).setText(noOfComments);
    }

    private void updateRecyclerAdapter(){
        mCommentAdapter = new CommentAdapter(comments, new CommentClickListener() {
            @Override
            public void onItemClick(View v, Comment comment) {
                Bundle data = new Bundle();
                data.putParcelable("comment", Parcels.wrap(comment));
                data.putParcelable("note", Parcels.wrap(note));

                currentView = v;
                currentComment = comment;

                Intent intent = new Intent(getApplication(), CommentDetailsActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            @Override
            public void onLikeClick(View v, Comment comment){
                Utilities.onLikeCommentClick(NoteDetailsActivity.this, NoteDetailsActivity.this, v, comment);
                //bottomPopup = Utilities.getBottomPopupLogin(NoteDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
            }

            @Override
            public void onReplayClick(View v, Comment comment){
                Bundle data = new Bundle();
                data.putParcelable("comment", Parcels.wrap(comment));
                data.putParcelable("note", Parcels.wrap(note));
                data.putBoolean("replay", true);

                currentView = v;
                currentComment = comment;

                Intent intent = new Intent(getApplication(), CommentDetailsActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onEllipsisClick(View v, RelativeLayout layout){
                if (ellipsisPopup == null){
                    ellipsisPopup = new EllipsisPopup(v.getContext(), new EllipsisPopupListener(){
                        @Override
                        public void onClick(View view){
                            bottomPopup = Utilities.getBottomPopupText(NoteDetailsActivity.this,
                                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                                    getString(R.string.report_commited), bottomPopup);
                        }
                    });
                }
                ellipsisPopup.showOnAnchor(layout.findViewById(R.id.comment_ellipsis_icon),
                        RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, true);

            }

            @Override
            public void onShareClick(View v, Comment comment){
                bottomPopup = Utilities.getBottomPopupLoading(NoteDetailsActivity.this,
                        R.layout.bottom_popup_loading, R.id.bottom_popup_text, getString(R.string.loading), bottomPopup);
                Bitmap screenshot = Utilities.getBitmapComment(NoteDetailsActivity.this, note, comment);
                Utilities.share(screenshot, NoteDetailsActivity.this);
            }

        }, this);
        recyclerView.setAdapter(mCommentAdapter);
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.note_details_comments_recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LayoutManagerNoScroll lm = new LayoutManagerNoScroll(this, LinearLayoutManager.VERTICAL,false);
        lm.setScrollEnabled(false);

        recyclerView.setLayoutManager(lm);
        recyclerView.setNestedScrollingEnabled(false);

        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.comments_divider, null);
        if (divider != null){
            CommentsDivider dividerItemDecoration = new CommentsDivider(divider);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }

        scrollView = findViewById(R.id.scroll_view_note);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                if (diff == 0) {
                    if (comments.size() < note.getComments()){
                        loading = true;
                        comments.add(null);
                        mCommentAdapter.notifyItemInserted(comments.size() - 1);
                        //Toast.makeText(NoteDetailsActivity.this, "gggg", Toast.LENGTH_SHORT).show();
                        page++;
                        getComments();
                    }
                }
            }
        });
    }

    private void initListeners(){
        findViewById(R.id.note_details_like_it_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomPopup = Utilities.onLikePostClick(NoteDetailsActivity.this,
                        NoteDetailsActivity.this, note, view, bottomPopup,
                        R.id.note_details_like_text, R.id.note_details_like_number);
            }
        });

        findViewById(R.id.note_details_replay_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.setFocusableInTouchMode(true);
                input.requestFocus();
                Utilities.showKeyboard(NoteDetailsActivity.this);
            }
        });

        findViewById(R.id.note_details_ellipsis_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ellipsisPopup == null){
                    ellipsisPopup = new EllipsisPopup(view.getContext(), new EllipsisPopupListener(){
                        @Override
                        public void onClick(View view){
                            bottomPopup = Utilities.getBottomPopupText(NoteDetailsActivity.this,
                                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                                    getString(R.string.report_commited), bottomPopup);
                        }
                    });
                }
                ellipsisPopup.showOnAnchor(findViewById(R.id.note_details_ellipsis_icon),
                        RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, true);
            }
        });

        findViewById(R.id.note_details_share_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomPopup = Utilities.getBottomPopupLoading(NoteDetailsActivity.this,
                        R.layout.bottom_popup_loading, R.id.bottom_popup_text, getString(R.string.loading), bottomPopup);
                Bitmap screenshot = Utilities.getBitmapNote(NoteDetailsActivity.this, note);
                Utilities.share(screenshot, NoteDetailsActivity.this);
            }
        });

        findViewById(R.id.note_details_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        findViewById(R.id.comment_insert_fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                if (input != null){
                    String inputText = input.getText().toString();
                    if (!inputText.equals("")){
                        data.putString("input", inputText);
                    }
                    input.setText("");
                    input.clearFocus();
                }
                Intent intent = new Intent(getApplicationContext(), AddCommentActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        findViewById(R.id.comment_insert_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input != null){
                    int checkComment = Utilities.checkComment(input.getText().toString(), NoteDetailsActivity.this);
                    if (checkComment > 0){
                        hideKeyboard(NoteDetailsActivity.this);
                        input.clearFocus();
                        DateTime dateTime = new DateTime();
                        String time = dateTime.toString("yyyy/MM/dd HH:mm:ss");
                        Utilities.commentPost(note.getId(), 1, "admin", time,  input.getText().toString(),
                                NoteDetailsActivity.this, NoteDetailsActivity.this);
                    }
                    else{
                        bottomPopup = Utilities.errorComment(checkComment, NoteDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
                    }
                }

            }
        });

        findViewById(R.id.note_details_comments_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = false;
                scrollView.fullScroll(View.FOCUS_UP);
                scrollView.smoothScrollTo(0,0);
                page = 0;
                getComments();
            }
        });
    }

    private void getComments(){
        Utilities.getComments(1, note.getId(), page, NoteDetailsActivity.this,
                NoteDetailsActivity.this);
    }

}
