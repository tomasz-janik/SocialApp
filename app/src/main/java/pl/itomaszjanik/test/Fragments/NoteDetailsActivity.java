package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import org.joda.time.DateTime;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Comments.*;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopup;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopupListener;
import pl.itomaszjanik.test.Posts.NoteDetailsClickListener;
import pl.itomaszjanik.test.Posts.ReactNoteCallback;
import pl.itomaszjanik.test.Remote.*;

import java.util.ArrayList;
import java.util.List;

import static pl.itomaszjanik.test.Utilities.hideKeyboard;

public class NoteDetailsActivity extends Activity implements ReactNoteCallback, CommentPostCallback, GetCommentsCallback,
        ReactCommentsCallback, UpdateCommentCallback, NoteDetailsClickListener, OnEndScrolled,
        CommentClickListener, CommentsFooterClickListener, SwipeRefreshLayout.OnRefreshListener{

    private Note note;
    private EditText input;
    private BottomPopup bottomPopup;
    private EllipsisPopup ellipsisPopup;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayoutManager mLayoutManager;
    private int page;

    private Comment currentComment;
    private View currentView;

    private RecyclerView recyclerView;
    private CommentAdapter mCommentAdapter;
    private boolean loading = true;

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

        //prepareView();
        init();
        initListeners();
        initRecyclerView();
        mCommentAdapter.insertNull();
        mCommentAdapter.insertNull();
        mCommentAdapter.insertNull();
        getComments();

    }

    @Override
    public void onResume(){
        super.onResume();
        if (currentComment != null){
            Utilities.updateCommentCall(this, currentComment);
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
    public void reactNoteLikeSucceeded(Note note, View view){
        note.changeLiked();
        note.incrementLikes();
        ((TextView)view.findViewById(R.id.note_details_like_number)).setText(String.valueOf(note.getLikes()));
        ((TextView)view.findViewById(R.id.note_details_like_text)).setTextColor(Color.BLUE);
    }

    @Override
    public void reactNoteUnlikeSucceeded(Note note, View view){
        note.changeLiked();
        note.decrementLikes();
        ((TextView)view.findViewById(R.id.note_details_like_number)).setText(String.valueOf(note.getLikes()));
        ((TextView)view.findViewById(R.id.note_details_like_text)).setTextColor(Color.parseColor("#747474"));
    }

    @Override
    public void reactNoteLikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_like_post), bottomPopup);
    }

    @Override
    public void reactNoteUnlikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_unlike_post), bottomPopup);
    }

    @Override
    public void reactNoteNoInternet(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.no_internet), bottomPopup);
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

        Utilities.hideKeyboard(this);
        mCommentAdapter.removeLast();
        mCommentAdapter.insert(comment);
        mCommentAdapter.insertFooter();
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
            mCommentAdapter.removeLast();
            mCommentAdapter.insert(list);
            mCommentAdapter.insertFooter();
            loading = false;
        }
        else{
            mCommentAdapter.removeAll();
            mCommentAdapter.insert(list);
            mCommentAdapter.insertNull();
        }
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

    @Override
    public void onNoteClick(View view, Note note){}

    @Override
    public void onNoteLikeClick(View view, Note note){
        Utilities.onLikeNoteClick(this, this, view, note);
    }

    @Override
    public void onNoteCommentClick(View view, Note note){
        input.setFocusableInTouchMode(true);
        input.requestFocus();
        Utilities.showKeyboard(this);
    }

    @Override
    public void onNoteEllipsisClick(View view, Note note, RelativeLayout layout){
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

    @Override
    public void onNoteShareClick(View view, Note note){
        bottomPopup = Utilities.getBottomPopupLoading(this, R.layout.bottom_popup_loading,
                R.id.bottom_popup_text, getString(R.string.loading), bottomPopup);
        Bitmap screenshot = Utilities.getBitmapNote(this, note);
        Utilities.share(screenshot, NoteDetailsActivity.this);
    }

    @Override
    public void onRefreshClick(){
        recyclerView.smoothScrollToPosition(0);
        loading = false;
        page = 0;
        getComments();
    }

    @Override
    public void onCommentClick(View v, Comment comment){
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
    public void onCommentLikeClick(View v, Comment comment){
        Utilities.onLikeCommentClick(NoteDetailsActivity.this, NoteDetailsActivity.this, v, comment);
    }

    @Override
    public void onCommentReplayClick(View v, Comment comment){
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
    public void onCommentEllipsisClick(View v, Comment comment, RelativeLayout layout){
        if (ellipsisPopup == null){
            ellipsisPopup = new EllipsisPopup(v.getContext(), new EllipsisPopupListener() {
                @Override
                public void onClick(View v) {
                    bottomPopup = Utilities.getBottomPopupText(NoteDetailsActivity.this,
                            R.layout.bottom_popup_text, R.id.bottom_popup_text,
                            getString(R.string.report_commited), bottomPopup);
                }
            });
        }
        ellipsisPopup.showOnAnchor(findViewById(R.id.comment_ellipsis_icon),
                RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, true);

    }

    @Override
    public void onCommentShareClick(View v, Comment comment){
        bottomPopup = Utilities.getBottomPopupLoading(this, R.layout.bottom_popup_loading,
                R.id.bottom_popup_text, getString(R.string.loading), bottomPopup);
        Bitmap screenshot = Utilities.getBitmapComment(this, note, comment);
        Utilities.share(screenshot, this);
    }

    @Override
    public void onEnd(){
        if (mCommentAdapter.getItemCount() < note.getComments() + 3){
            if (!loading){
                page++;
                loading = true;
                getComments();
            }
        }
    }

    private void init(){
        initInput();
    }

    private void initInput(){

        input = findViewById(R.id.comment_insert_text);
        //updateCommentsNumber();
        //initRefreshLayout();
    }

    private void initRefreshLayout(){
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
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
        String noOfComments = Utilities.getCommentVariation(note.getComments(), this);

        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(0);
        if (holder != null){
            ((TextView)(holder.itemView.findViewById(R.id.note_details_comments_number))).setText(String.valueOf(note.getComments()));
        }
        holder = recyclerView.findViewHolderForLayoutPosition(1);
        if (holder != null){
            ((TextView)holder.itemView.findViewById(R.id.note_details_comments_number)).setText(noOfComments);
        }
        //((TextView)(findViewById(R.id.note_details_comments_number))).setText(noOfComments);
    }

    private void initCommentAdapter(){
        mCommentAdapter = new CommentAdapter(comments, new CommentClickListener() {
            @Override
            public void onCommentClick(View v, Comment comment) {
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
            public void onCommentLikeClick(View v, Comment comment){
                Utilities.onLikeCommentClick(NoteDetailsActivity.this, NoteDetailsActivity.this, v, comment);
                //bottomPopup = Utilities.getBottomPopupLogin(NoteDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
            }

            @Override
            public void onCommentReplayClick(View v, Comment comment){
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
            public void onCommentEllipsisClick(View v, Comment comment, RelativeLayout layout){
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
            public void onCommentShareClick(View v, Comment comment){
                bottomPopup = Utilities.getBottomPopupLoading(NoteDetailsActivity.this,
                        R.layout.bottom_popup_loading, R.id.bottom_popup_text, getString(R.string.loading), bottomPopup);
                Bitmap screenshot = Utilities.getBitmapComment(NoteDetailsActivity.this, note, comment);
                Utilities.share(screenshot, NoteDetailsActivity.this);
            }

        }, this, note);
        mCommentAdapter.initListeners(this, this, this, this);
        recyclerView.setAdapter(mCommentAdapter);
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.note_details_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.getItemAnimator().setChangeDuration(0);
        initCommentAdapter();
    }

    private void initListeners(){
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
    }

    private void getComments(){
        Utilities.getComments(1, note.getId(), page, NoteDetailsActivity.this,
                NoteDetailsActivity.this);
    }

}
