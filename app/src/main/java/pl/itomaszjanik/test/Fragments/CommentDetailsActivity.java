package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.*;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import org.joda.time.DateTime;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Comments.*;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopup;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopupListener;
import pl.itomaszjanik.test.Remote.GenerateIDCallback;
import pl.itomaszjanik.test.Replays.*;
import pl.itomaszjanik.test.Replays.ReplaysFooterClickListener;

import java.util.List;

public class CommentDetailsActivity extends Activity implements ReactCommentsCallback, ReplayCommentCallback,
        GetReplaysCallback, ReactReplayCallback, CommentClickListener, ReplaysFooterClickListener,
        GenerateIDCallback, OnEndScrolled {

    private static final int GEN_LOAD = 0;
    private static final int GEN_REACT_COMMENT = 1;
    private static final int GEN_REACT_REPLAY = 2;

    private Note note;
    private Comment comment;
    private EditText input;
    private int length;
    private boolean change;
    private BottomPopup bottomPopup;
    private EllipsisPopup ellipsisPopup;

    private ReplayAdapter mCommentAdapter;
    private RecyclerView recyclerView;

    private View currentReplayView, currentCommentView;
    private Replay currentReplay;

    private boolean loading = true;
    private int page = 0;

    private SharedPreferences sharedPreferences;
    private int userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_details);

        Bundle bundle = getIntent().getExtras();
        boolean replay;
        if (bundle != null){
            comment = Parcels.unwrap(bundle.getParcelable("comment"));
            note = Parcels.unwrap(bundle.getParcelable("note"));
            if (comment == null){
                comment = new Comment(1, 1, 1, "admin", "26.08.2018 22:41:00", "TEST", 0, 0, false);
            }
            if (note == null){
                note = new Note(0,"TEST", "26.08.2018 22:41:00", "TEST", "#TEST", 0, 0, 0, false);
            }
            replay = bundle.getBoolean("replay", false);
        }
        else{
            note = new Note(0,"TEST", "26.08.2018 22:41:00", "TEST", "#TEST", 0, 0, 0, false);
            comment = new Comment(1, 1, 1, "admin", "26.08.2018 22:41:00", "TEST", 0, 0, false);
            replay = false;
        }

        init(replay);

        initRecyclerView();
        mCommentAdapter.insertNull();
        mCommentAdapter.insertNull();
        mCommentAdapter.insertNull();

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

        Utilities.hideKeyboard(this);
        mCommentAdapter.removeLast();
        mCommentAdapter.insert(replay);
        mCommentAdapter.insertFooter();
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
            mCommentAdapter.insertFooter();
            //mCommentAdapter.removeLast();
            //mCommentAdapter.insertFooter();
            loading = false;
        }
        else{
            mCommentAdapter.removeAll();
            mCommentAdapter.insert(list);
            mCommentAdapter.insertNull();
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

    @Override
    public void onCommentClick(View view, Comment comment){

    }

    @Override
    public void onCommentLikeClick(View view, Comment comment){
        currentCommentView = view;
        userID = sharedPreferences.getInt("userID", 0);
        if (userID == 0){
            Utilities.generateID(GEN_REACT_COMMENT, this, this);
        }
        else{
            Utilities.onLikeCommentClick(userID, this, this, view, comment);
        }
    }

    @Override
    public void onCommentReplayClick(View view, Comment comment){
        input.setText("");
        length = 0;
        input.setFocusableInTouchMode(true);
        input.requestFocus();
        Utilities.showKeyboard(CommentDetailsActivity.this);
    }

    @Override
    public void onCommentEllipsisClick(View view, Comment comment, RelativeLayout layout){
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

    @Override
    public void onCommentShareClick(View view, Comment comment){
        bottomPopup = Utilities.getBottomPopupLoading(this, R.layout.bottom_popup_loading,
                R.id.bottom_popup_text, getString(R.string.loading), bottomPopup);
        Bitmap screenshot = Utilities.getBitmapComment(this, note, comment);
        Utilities.share(screenshot, this);
    }

    @Override
    public void onGenerateSuccess(String username, int userID, int task){
        this.userID = userID;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putInt("userID", userID);
        editor.apply();

        switch (task){
            case GEN_LOAD:
                getReplays();
                break;
            case GEN_REACT_COMMENT:
                onCommentLikeClick(currentCommentView, comment);
                break;
            case GEN_REACT_REPLAY:
                Utilities.onLikeReplayClick(userID,this, this, currentReplayView, currentReplay);
                break;
        }
    }

    @Override
    public void onGenerateFailed(int task){
        switch (task){
            case GEN_LOAD:
                getReplayFailed();
                break;
            case GEN_REACT_COMMENT:
                reactCommentLikeFailed();
                break;
            case GEN_REACT_REPLAY:
                reactReplayLikeFailed();
                break;
        }
    }

    @Override
    public void onGenerateNoInternet(int task){
        switch (task){
            case GEN_LOAD:
                getReplayNoInternet();
                break;
            case GEN_REACT_COMMENT:
                reactCommentNoInternet();
                break;
            case GEN_REACT_REPLAY:
                reactReplayNoInternet();
                break;
        }
    }

    @Override
    public void onRefreshClick(){
        recyclerView.smoothScrollToPosition(0);
        loading = false;
        page = 0;
        getReplays();
    }

    @Override
    public void onEnd(){
        if (mCommentAdapter.getItemCount() < comment.getReplays() + 3){
            if (!loading){
                page++;
                loading = true;
                getReplays();
            }
        }
    }

    private void initInput(boolean replay){
        input = findViewById(R.id.comment_insert_text);

        input.setHint(getResources().getString(R.string.comment_details_hint));
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
        if (replay){
            input.setFocusableInTouchMode(true);
            input.requestFocus();
        }
    }

    private void init(boolean replay){
        initInput(replay);
        initListeners();

        sharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", 0);
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.comment_details_comments_recycler_view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(CommentDetailsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.getItemAnimator().setChangeDuration(0);
        initCommentAdapter();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initCommentAdapter(){
        mCommentAdapter = new ReplayAdapter(new ReplayClickListener() {
            @Override
            public void onItemClick(View v, Replay replay){}

            @Override
            public void onLikeClick(View v, Replay replay){
                currentReplayView = v;
                currentReplay = replay;
                if (userID == 0){
                    Utilities.generateID(GEN_REACT_REPLAY, CommentDetailsActivity.this,
                            CommentDetailsActivity.this);
                }
                else{
                    Utilities.onLikeReplayClick(userID,CommentDetailsActivity.this,
                            CommentDetailsActivity.this, v, replay);
                }
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
            public void onEllipsisClick(View v, Replay replay, RelativeLayout layout){
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

        }, comment, this, this);
        mCommentAdapter.initListeners(this, this);
        recyclerView.setAdapter(mCommentAdapter);
    }



    private void initListeners(){
        final SharedPreferences sharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES, Context.MODE_PRIVATE);

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
                    int checkComment = Utilities.checkComment(input.getText().toString());

                    if (checkComment > 0){
                        input.clearFocus();
                        Utilities.hideKeyboard(CommentDetailsActivity.this);

                        if (sharedPreferences.getBoolean("signed", false)){
                            DateTime dateTime = new DateTime();
                            String time = dateTime.toString("yyyy-MM-dd HH:mm:ss");
                            userID = sharedPreferences.getInt("userID", 0);

                            Utilities.replayComment(comment.getCommentID(), userID,
                                    sharedPreferences.getString("username", "Anonim"), time, input.getText().toString(),
                                    CommentDetailsActivity.this, CommentDetailsActivity.this);
                        }
                        else{
                            bottomPopup = Utilities.getBottomPopupLogin(CommentDetailsActivity.this,
                                    R.layout.bottom_popup_text, bottomPopup);
                        }
                    }
                    else{
                        bottomPopup = Utilities.errorComment(checkComment, CommentDetailsActivity.this, bottomPopup);
                    }
                }

            }
        });
    }

    private void updateReplaysNumber(){
        String noOfComments = Utilities.getReplaysVariation(comment.getReplays(), CommentDetailsActivity.this);

        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(0);
        if (holder != null){
            ((TextView)(holder.itemView.findViewById(R.id.comment_item_replays))).setText(String.valueOf(comment.getReplays()));
        }
        holder = recyclerView.findViewHolderForLayoutPosition(1);
        if (holder != null){
            ((TextView)holder.itemView.findViewById(R.id.comment_details_comments_number)).setText(noOfComments);
        }
    }

    private void getReplays(){
        if (userID == 0){
            Utilities.generateID(GEN_LOAD, this, this);
        }
        else{
            Utilities.getReplays(userID, comment.getCommentID(), page, this, this);
        }
    }

}
