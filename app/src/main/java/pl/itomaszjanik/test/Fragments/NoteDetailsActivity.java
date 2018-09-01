package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static pl.itomaszjanik.test.Utilities.prepareHashesText;

public class NoteDetailsActivity extends Activity {

    private Note note;
    private TextView content, hashes, date, rate;
    private EditText input;
    private BottomPopup bottomPopup;
    private EllipsisPopup ellipsisPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_details);
        content = (TextView) findViewById(R.id.note_details_content);
        hashes = (TextView) findViewById(R.id.note_details_hashes);
        input = (EditText) findViewById(R.id.comment_insert_text);

        ((CustomImage) (findViewById(R.id.note_details_icon_back))).init(R.drawable.ic_arrow_black_24dp, R.drawable.ic_arrow_black_24dp);

        initListeners();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            note = Parcels.unwrap(bundle.getParcelable("note"));
            if (note == null){
                ArrayList<String> list = new ArrayList<>();
                list.add("TEST");
                list.add("#TEST");
                note = new Note("TEST", "TEST", list, 0);
            }
            content.setText(note.getContent());
            hashes.setText(prepareHashesText(note.getHashes()));
        }

        int noOfComments = note.getNoOfComments();
        String noOfCommentsString = noOfComments + " ";
        if (noOfComments == 0){
            noOfCommentsString += getResources().getString(R.string.comment_five);
        }
        else if (noOfComments == 1){
            noOfCommentsString += getResources().getString(R.string.comment_one);
        }
        else if (noOfComments < 5){
            noOfCommentsString += getResources().getString(R.string.comment_two);
        }
        else{
            noOfCommentsString += getResources().getString(R.string.comment_five);
        }

        ((TextView)(findViewById(R.id.note_details_comments_number))).setText(noOfCommentsString);
        initRecyclerView();
    }


    private void initRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.note_details_comments_recycler_view);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        CommentAdapter adapter = new CommentAdapter(note.getComments(), new CommentClickListener() {
            @Override
            public void onItemClick(View v, Comment comment) {
                Bundle data = new Bundle();
                data.putParcelable("comment", Parcels.wrap(comment));

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
                Bitmap screenshot = Utilities.getBitmapComment(NoteDetailsActivity.this, note, comment);
                Utilities.share(screenshot, NoteDetailsActivity.this);
            }

        }, this);

        LayoutManagerNoScroll lm = new LayoutManagerNoScroll(this, LinearLayoutManager.VERTICAL,false);
        lm.setScrollEnabled(false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(adapter);

        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.comments_divider, null);
        if (divider != null){
            CommentsDivider dividerItemDecoration = new CommentsDivider(divider);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
                        bottomPopup = Utilities.getBottomPopupLogin(NoteDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
                    }
                    else{
                        bottomPopup = Utilities.errorComment(checkComment, NoteDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
                    }
                }

            }
        });
    }
}
