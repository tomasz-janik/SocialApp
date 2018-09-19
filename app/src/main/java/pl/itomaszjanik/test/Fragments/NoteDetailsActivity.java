package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import org.joda.time.DateTime;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.Comments.*;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopup;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopupListener;
import pl.itomaszjanik.test.Posts.NoteDetailsClickListener;
import pl.itomaszjanik.test.Posts.ReactNoteCallback;
import pl.itomaszjanik.test.Remote.GenerateIDCallback;

import java.util.ArrayList;
import java.util.List;

public class NoteDetailsActivity extends Activity implements ReactNoteCallback, CommentPostCallback, GetCommentsCallback,
        ReactCommentsCallback, UpdateCommentCallback, NoteDetailsClickListener, OnEndScrolled, GenerateIDCallback,
        CommentClickListener, CommentsFooterClickListener, SwipeRefreshLayout.OnRefreshListener{

    private static final int GEN_LOAD = 0;
    private static final int GEN_REACT_POST = 1;
    private static final int GEN_REACT_COMMENT = 2;

    private Note note;
    private EditText input;
    private EllipsisPopup ellipsisPopup;

    private int page;

    private Comment currentComment;
    private View currentCommentView, currentNoteView;

    private RecyclerView recyclerView;
    private CommentAdapter mCommentAdapter;
    private boolean loading = true;

    private SharedPreferences sharedPreferences;
    private int userID;

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

        int uiOptions;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        else{
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        if (currentComment != null){
            userID = sharedPreferences.getInt("userID", 0);
            Utilities.updateCommentCall(userID,this, currentComment);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            int uiOptions;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            else{
                uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            }
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
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
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.couldnt_like_post));
    }

    @Override
    public void reactNoteUnlikeFailed(){
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.couldnt_unlike_post));
    }

    @Override
    public void reactNoteNoInternet(){
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.no_internet));

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
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.couldnt_like_comment));

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
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.couldnt_unlike_comment));
    }

    @Override
    public void reactCommentNoInternet(){
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.no_internet));
    }

    @Override
    public void commentPostSucceeded(Comment comment){
        input.setText("");

        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.comment_post_added));

        Utilities.hideKeyboard(this);
        mCommentAdapter.removeLast();
        mCommentAdapter.insert(comment);
        mCommentAdapter.insertFooter();
        note.incrementComments();
        updateCommentsNumber();
    }

    @Override
    public void commentPostFailed(){
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.comment_post_couldnt));
    }

    @Override
    public void commentPostNoInternet(){
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.no_internet));
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
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.comment_load_couldnt));
    }

    @Override
    public void getCommentNoInternet(){
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.no_internet));
    }

    @Override
    public void updateCommentSucceeded(Comment comment){
        if (currentCommentView != null){
            currentComment.setLiked(comment.getLiked());
            currentComment.setLikes(comment.getLikes());
            currentComment.setReplays(comment.getReplays());
            ((TextView)currentCommentView.findViewById(R.id.comment_item_replays)).setText(String.valueOf(comment.getReplays()));
            ((TextView)currentCommentView.findViewById(R.id.comment_like_number)).setText(String.valueOf(comment.getLikes()));
            if (comment.getLiked()){
                ((TextView)(currentCommentView.findViewById(R.id.comment_like_text))).setTextColor(Color.BLUE);
            }
            else{
                ((TextView)(currentCommentView.findViewById(R.id.comment_like_text))).setTextColor(Color.parseColor("#747474"));
            }
        }
    }

    @Override
    public void updateCommentFailed(){}

    @Override
    public void onNoteClick(View view, Note note){}

    @Override
    public void onNoteLikeClick(View view, Note note){
        currentNoteView = view;
        userID = sharedPreferences.getInt("userID", 0);

        if (userID == 0){
            Utilities.generateID(GEN_REACT_POST, this, this);
        }
        else{
            Utilities.onLikeNoteClick(userID, this, this, view, note);
        }
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
                    Utilities.showSnackbarText(NoteDetailsActivity.this, findViewById(R.id.main_layout),
                            getString(R.string.report_commited));
                }
            });
        }
        ellipsisPopup.showOnAnchor(findViewById(R.id.note_details_ellipsis_icon),
                RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, true);

    }

    @Override
    public void onNoteShareClick(View view, Note note){
        Utilities.showSnackbarLoad(this, findViewById(R.id.main_layout), getString(R.string.loading));

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

        currentCommentView = v;
        currentComment = comment;

        Intent intent = new Intent(getApplication(), CommentDetailsActivity.class);
        intent.putExtras(data);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onCommentLikeClick(View v, Comment comment){
        currentCommentView = v;
        currentComment = comment;
        userID = sharedPreferences.getInt("userID", 0);
        if (userID == 0){
            Utilities.generateID(GEN_REACT_COMMENT, this, this);
        }
        else{
            Utilities.onLikeCommentClick(userID,this, this, v, comment);
        }
    }

    @Override
    public void onCommentReplayClick(View v, Comment comment){
        Bundle data = new Bundle();
        data.putParcelable("comment", Parcels.wrap(comment));
        data.putParcelable("note", Parcels.wrap(note));

        currentCommentView = v;
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
                    Utilities.showSnackbarText(NoteDetailsActivity.this, findViewById(R.id.main_layout),
                            getString(R.string.report_commited));
                }
            });
        }
        ellipsisPopup.showOnAnchor(findViewById(R.id.comment_ellipsis_icon),
                RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT, true);

    }

    @Override
    public void onCommentShareClick(View v, Comment comment){
        Utilities.showSnackbarLoad(this, findViewById(R.id.main_layout), getString(R.string.loading));

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
                getComments();
                break;
            case GEN_REACT_POST:
                onNoteLikeClick(currentNoteView, note);
                break;
            case GEN_REACT_COMMENT:
                onCommentLikeClick(currentCommentView, currentComment);
                break;
        }
    }

    @Override
    public void onGenerateFailed(int task){
        switch (task){
            case GEN_LOAD:
                getCommentFailed();
                break;
            case GEN_REACT_POST:
                reactNoteLikeFailed();
                break;
            case GEN_REACT_COMMENT:
                reactCommentLikeFailed();
                break;
        }
    }

    @Override
    public void onGenerateNoInternet(int task){
        switch (task){
            case GEN_LOAD:
                getCommentNoInternet();
                break;
            case GEN_REACT_POST:
                reactNoteNoInternet();
                break;
            case GEN_REACT_COMMENT:
                reactCommentNoInternet();
                break;
        }
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

        sharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", 0);
    }

    private void initInput(){
        input = findViewById(R.id.comment_insert_text);
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
    }

    private void initCommentAdapter(){
        mCommentAdapter = new CommentAdapter(comments, this, note);
        mCommentAdapter.initListeners(this, this, this, this);
        recyclerView.setAdapter(mCommentAdapter);
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.note_details_recycler_view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
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
                    int checkComment = Utilities.checkComment(input.getText().toString());

                    if (checkComment > 0){
                        input.clearFocus();
                        Utilities.hideKeyboard(NoteDetailsActivity.this);

                        if (sharedPreferences.getBoolean("signed", false)){
                            DateTime dateTime = new DateTime();
                            String time = dateTime.toString("yyyy-MM-dd HH:mm:ss");
                            userID = sharedPreferences.getInt("userID", 0);

                            Utilities.commentPost(note.getId(), userID, sharedPreferences.getString("username", "Anonim"),
                                    time, input.getText().toString(), NoteDetailsActivity.this,
                                    NoteDetailsActivity.this);
                        }
                        else{
                            Utilities.showSnackbarLogin(NoteDetailsActivity.this,
                                    findViewById(R.id.main_layout), getString(R.string.not_logged));
                        }
                    }
                    else{
                        Utilities.errorComment(checkComment, NoteDetailsActivity.this,
                                findViewById(R.id.main_layout));
                    }
                }

            }
        });
    }

    private void getComments(){
        if (userID == 0){
            Utilities.generateID(GEN_LOAD, this, this);
        }
        else{
            Utilities.getComments(1, note.getId(), page, this, this);
        }
    }

}
