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
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
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
import pl.itomaszjanik.test.Remote.CommentPostCallback;
import pl.itomaszjanik.test.Remote.FailedCallback;
import pl.itomaszjanik.test.Remote.PostService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static pl.itomaszjanik.test.Utilities.hideKeyboard;
import static pl.itomaszjanik.test.Utilities.prepareHashesText;

public class NoteDetailsActivity extends Activity implements FailedCallback, CommentPostCallback {

    private Note note;
    private TextView content, hashes, date, rate;
    private EditText input;
    private BottomPopup bottomPopup;
    private EllipsisPopup ellipsisPopup;

    private RecyclerView recyclerView;
    private List<Comment> comments = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            note = Parcels.unwrap(bundle.getParcelable("note"));
            if (note == null){
                ArrayList<String> list = new ArrayList<>();
                list.add("TEST");
                list.add("#TEST");
                note = new Note(0,"TEST", "26/08/2018 22:41:00", "TEST", "#TEST", 0, 0, 0, false);
            }
        }

        prepareView();
        initListeners();
        initRecyclerView();
        getComments(String.valueOf(note.getId()));
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
    public void commentPostSucceeded(){
        input.setText("");
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.comment_post_added), bottomPopup);
        getComments(String.valueOf(note.getId()));
        note.incrementComments();
        updateCommentsNumber();
    }

    @Override
    public void commentPostFailed(){
        bottomPopup = Utilities.getBottomPopupText(this,
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.comment_post_couldnt), bottomPopup);
    }

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
    }

    private void updateCommentsNumber(){
        String noOfComments = Utilities.getCommentVariation(note.getComments(), NoteDetailsActivity.this);
        ((TextView)(findViewById(R.id.note_details_comments_number))).setText(noOfComments);
    }

    private void updateRecyclerAdapter(){
        CommentAdapter adapter = new CommentAdapter(comments, new CommentClickListener() {
            @Override
            public void onItemClick(View v, Comment comment) {
                Bundle data = new Bundle();
                data.putParcelable("comment", Parcels.wrap(comment));
                data.putParcelable("note", Parcels.wrap(note));

                Intent intent = new Intent(getApplication(), CommentDetailsActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            @Override
            public void onLikeClick(View v, RelativeLayout layout){
                bottomPopup = Utilities.getBottomPopupLogin(NoteDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
            }

            @Override
            public void onReplayClick(View v, Comment comment){
                Bundle data = new Bundle();
                data.putParcelable("comment", Parcels.wrap(comment));
                data.putParcelable("note", Parcels.wrap(note));
                data.putBoolean("replay", true);

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
        recyclerView.setAdapter(adapter);
    }


    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.note_details_comments_recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        LayoutManagerNoScroll lm = new LayoutManagerNoScroll(this, LinearLayoutManager.VERTICAL,false);
        lm.setScrollEnabled(false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setNestedScrollingEnabled(false);

        //recyclerView.setAdapter(adapter);

        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.comments_divider, null);
        if (divider != null){
            CommentsDivider dividerItemDecoration = new CommentsDivider(divider);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
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
                        bottomPopup = Utilities.commentPost(note.getId(), 1, "admin", time,  input.getText().toString(),
                                NoteDetailsActivity.this, NoteDetailsActivity.this, bottomPopup);
                    }
                    else{
                        bottomPopup = Utilities.errorComment(checkComment, NoteDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
                    }
                }

            }
        });
    }

    private void getComments(String postID){
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        service.getCommentsPost(postID).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()){
                    try{
                        comments = response.body();
                        updateRecyclerAdapter();
                        //Toast.makeText(NoteDetailsActivity.this, "" + response.body().get(0).getCONTENT(), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){}
                    //callback.commentPostSucceeded();
                }
                else{
                   // callback.commentPostFailed();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(NoteDetailsActivity.this, "" + t, Toast.LENGTH_SHORT).show();

                //callback.commentPostFailed();
            }
        });
    }

}
