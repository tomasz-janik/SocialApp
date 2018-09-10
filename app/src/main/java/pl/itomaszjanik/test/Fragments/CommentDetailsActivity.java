package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import okhttp3.internal.Util;
import org.joda.time.DateTime;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Comments.*;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopup;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopupListener;
import pl.itomaszjanik.test.ExtendedComponents.CustomImage;
import pl.itomaszjanik.test.ExtendedComponents.LayoutManagerNoScroll;
import pl.itomaszjanik.test.Remote.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class CommentDetailsActivity extends Activity implements ReactCommentsCallback, ReplayCommentCallback,
        GetReplaysCallback, ReactReplayCallback {

    private Note note;
    private Comment comment;
    private EditText input;
    private TextView username, date, content;
    private int length;
    private boolean change;
    private BottomPopup bottomPopup;
    private EllipsisPopup ellipsisPopup;

    private CommentNoReplayAdapter mCommentAdapter;
    private RecyclerView recyclerView;
    private NestedScrollView scrollView;

    private boolean loading;
    private int page = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            comment = Parcels.unwrap(bundle.getParcelable("comment"));
            note = Parcels.unwrap(bundle.getParcelable("note"));
            if (comment == null){
                comment = new Comment(1, 1, 1, "admin", "26.08.2018 22:41:00", "TEST", 0, 0, false);
            }
            if (note == null){
                note = new Note(0,"TEST", "26.08.2018 22:41:00", "TEST", "#TEST", 0, 0, 0, false);
            }
            initMainContent(bundle.getBoolean("replay", false));
        }
        else{
            note = new Note(0,"TEST", "26.08.2018 22:41:00", "TEST", "#TEST", 0, 0, 0, false);
            comment = new Comment(1, 1, 1, "admin", "26.08.2018 22:41:00", "TEST", 0, 0, false);
            initMainContent(false);
        }

        initInput();
        initCommentsNumber();
        initRecyclerView();
        getReplays();
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
    public void replayCommentSucceeded(Replay replay){
        input.setText("");
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.comment_post_added), bottomPopup);

        mCommentAdapter.insert(replay);

        comment.incrementReplays();
        updateReplaysNumber();
    }

    @Override
    public void replayCommentFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_unlike_comment), bottomPopup);
    }

    @Override
    public void replayCommentNoInternet(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.no_internet), bottomPopup);
    }

    @Override
    public void getReplaySucceeded(List<Replay> list){
        if (loading){
            mCommentAdapter.removeLast();
            mCommentAdapter.insert(list);
        }
        else{
            mCommentAdapter.removeAll();
            mCommentAdapter.insert(list);
        }
    }

    @Override
    public void getReplayFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.comment_load_couldnt), bottomPopup);
    }

    @Override
    public void getReplayNoInternet(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.no_internet), bottomPopup);
    }

    @Override
    public void reactReplayLikeSucceeded(Replay replay, View view){
        replay.changeLiked();
        replay.incrementLikes();
        ((TextView)view.findViewById(R.id.comment_like_number)).setText(String.valueOf(replay.getLikes()));
        ((TextView)view.findViewById(R.id.comment_like_text)).setTextColor(Color.BLUE);
    }

    @Override
    public void reactReplayLikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_like_comment), bottomPopup);
    }

    @Override
    public void reactReplayUnlikeSucceeded(Replay replay, View view){
        replay.changeLiked();
        replay.decrementLikes();
        ((TextView)view.findViewById(R.id.comment_like_number)).setText(String.valueOf(replay.getLikes()));
        ((TextView)view.findViewById(R.id.comment_like_text)).setTextColor(Color.parseColor("#747474"));

    }

    @Override
    public void reactReplayUnlikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_unlike_comment), bottomPopup);
    }

    @Override
    public void reactReplayNoInternet(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.no_internet), bottomPopup);
    }

    private void findViews(){
        ((CustomImage) (findViewById(R.id.comment_details_icon_back))).init(R.drawable.ic_arrow_black_24dp, R.drawable.ic_arrow_black_24dp);

        input = (EditText) findViewById(R.id.comment_insert_text);
        input.setHint(getResources().getString(R.string.comment_details_hint));

        username = (TextView) findViewById(R.id.comment_username);
        date = (TextView) findViewById(R.id.comment_date);
        content = (TextView) findViewById(R.id.comment_content);

    }

    private void initInput(){
        input.addTextChangedListener(new TextWatcherBackspace() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                if (backSpace){
                    if (s.toString().length() < length){
                        if (!change){
                            input.setText("");
                            length = 0;
                        }
                    }
                }
            }
        });
    }

    private void initCommentsNumber(){
        int noOfComments = comment.getReplays();
        String noOfCommentsString = noOfComments + " ";
        if (noOfComments == 1){
            noOfCommentsString += getResources().getString(R.string.replay_one);
        }
        else{
            noOfCommentsString += getResources().getString(R.string.replay_two);
        }

        ((TextView)(findViewById(R.id.comment_details_comments_number))).setText(noOfCommentsString);
        ((TextView)(findViewById(R.id.comment_item_replays))).setText(String.valueOf(comment.getReplays()));
    }

    private void initMainContent(boolean replay){
        findViews();
        initListeners();

        username.setText(comment.getUsername());
        date.setText(Utilities.decodeDate(comment.getDate(), getApplicationContext()));
        content.setText(comment.getContent());

        if (comment.getLiked()){
            ((TextView)findViewById(R.id.comment_like_text)).setTextColor(Color.BLUE);
        }
        else{
            ((TextView)findViewById(R.id.comment_like_text)).setTextColor(Color.parseColor("#747474"));
        }
        ((TextView)findViewById(R.id.comment_like_number)).setText(String.valueOf(comment.getLikes()));

        if (replay){
            input.setFocusableInTouchMode(true);
            input.requestFocus();
        }
    }


    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.comment_details_comments_recycler_view);

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

        scrollView = findViewById(R.id.scroll_view_comment_details);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                if (diff == 0) {
                    if (mCommentAdapter.getItemCount() < comment.getReplays()){
                        loading = true;
                        mCommentAdapter.insertNull();
                        page++;
                        getReplays();
                    }
                }
            }
        });
        initCommentAdapter();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initCommentAdapter(){
        mCommentAdapter = new CommentNoReplayAdapter(new ReplayClickListener() {
            @Override
            public void onItemClick(View v, Replay replay){}

            @Override
            public void onLikeClick(View v, Replay comment){
                Utilities.onLikeReplayClick(CommentDetailsActivity.this, CommentDetailsActivity.this,
                        v, comment);
            }

            @Override
            public void onReplayClick(View v, Replay comment){
                String output = "@" + comment.getUsername() + " ";
                Spannable spannable = new SpannableString(output);
                spannable.setSpan(new BackgroundColorSpan(Color.parseColor("#22000000")),0, output.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                change = true;
                length = output.length();
                input.setText("");
                input.setText(spannable);
                change = false;
                input.setFocusableInTouchMode(true);
                input.requestFocus();
                input.setSelection(length);

                Utilities.showKeyboard(CommentDetailsActivity.this);
            }

            @Override
            public void onEllipsisClick(View v, RelativeLayout layout){
                if (ellipsisPopup == null){
                    ellipsisPopup = new EllipsisPopup(v.getContext(), new EllipsisPopupListener() {
                        @Override
                        public void onClick(View v) {
                            bottomPopup = Utilities.getBottomPopupText(CommentDetailsActivity.this,
                                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                                    getString(R.string.report_commited), bottomPopup);
                        }
                    });
                }
                ellipsisPopup.showOnAnchor(layout.findViewById(R.id.comment_ellipsis_icon),
                        RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, true);
            }

            @Override
            public void onShareClick(View v, Replay replay){
                bottomPopup = Utilities.getBottomPopupLoading(CommentDetailsActivity.this,
                        R.layout.bottom_popup_loading, R.id.bottom_popup_text, getString(R.string.loading), bottomPopup);
                Bitmap screenshot = Utilities.getBitmapReplay(CommentDetailsActivity.this, note, comment, replay);
                Utilities.share(screenshot, CommentDetailsActivity.this);
            }

        }, this);
        recyclerView.setAdapter(mCommentAdapter);
    }



    private void initListeners(){
        findViewById(R.id.comment_details_button_back).setOnClickListener(new View.OnClickListener() {
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
                        data.putInt("name_length", length);
                    }
                    input.setText("");
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
                    input.clearFocus();
                    Utilities.hideKeyboard(CommentDetailsActivity.this);
                    int checkComment = Utilities.checkComment(input.getText().toString(), CommentDetailsActivity.this);
                    if (checkComment > 0){
                        DateTime dateTime = new DateTime();
                        String time = dateTime.toString("yyyy/MM/dd HH:mm:ss");
                        Utilities.replayComment(comment.getCommentID(), 1, "admin",
                                time, input.getText().toString(), CommentDetailsActivity.this, CommentDetailsActivity.this);
                    }
                    else{
                        bottomPopup = Utilities.errorComment(checkComment, CommentDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
                    }
                }

            }
        });

        findViewById(R.id.comment_like_it_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.onLikeCommentClick(CommentDetailsActivity.this, CommentDetailsActivity.this, view, comment);
            }
        });

        findViewById(R.id.comment_replay_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.setText("");
                length = 0;
                input.setFocusableInTouchMode(true);
                input.requestFocus();
                Utilities.showKeyboard(CommentDetailsActivity.this);
            }
        });

        findViewById(R.id.comment_ellipsis_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ellipsisPopup == null){
                    ellipsisPopup = new EllipsisPopup(view.getContext(), new EllipsisPopupListener() {
                        @Override
                        public void onClick(View v) {
                            bottomPopup = Utilities.getBottomPopupText(CommentDetailsActivity.this,
                                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                                    getString(R.string.report_commited), bottomPopup);
                        }
                    });
                }
                ellipsisPopup.showOnAnchor(findViewById(R.id.comment_ellipsis_icon),
                        RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, true);
            }
        });

        findViewById(R.id.comment_share_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomPopup = Utilities.getBottomPopupLoading(CommentDetailsActivity.this,
                        R.layout.bottom_popup_loading, R.id.bottom_popup_text, getString(R.string.loading), bottomPopup);
                Bitmap screenshot = Utilities.getBitmapComment(CommentDetailsActivity.this, note, comment);
                Utilities.share(screenshot, CommentDetailsActivity.this);
            }
        });

        findViewById(R.id.comment_details_comments_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = false;
                scrollView.fullScroll(View.FOCUS_UP);
                scrollView.smoothScrollTo(0,0);
                page = 0;
                getReplays();
            }
        });

    }

    private void updateReplaysNumber(){
        String noOfComments = Utilities.getReplaysVariation(comment.getReplays(), CommentDetailsActivity.this);
        ((TextView)(findViewById(R.id.comment_details_comments_number))).setText(noOfComments);
        ((TextView)(findViewById(R.id.comment_item_replays))).setText(String.valueOf(comment.getReplays()));
    }

    private void getReplays(){
        Utilities.getReplays(1, comment.getCommentID(), page, CommentDetailsActivity.this,
                CommentDetailsActivity.this);
    }

}
