package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
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
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Comments.CommentClickListener;
import pl.itomaszjanik.test.Comments.CommentNoReplayAdapter;
import pl.itomaszjanik.test.Comments.CommentsDivider;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopup;
import pl.itomaszjanik.test.EllipsisPopup.EllipsisPopupListener;
import pl.itomaszjanik.test.ExtendedComponents.CustomImage;
import pl.itomaszjanik.test.ExtendedComponents.LayoutManagerNoScroll;

import java.util.ArrayList;
import java.util.List;

public class CommentDetailsActivity extends Activity {

    private Note note;
    private Comment comment;
    private EditText input;
    private TextView username, date, content;
    private int length;
    private boolean change;
    private BottomPopup bottomPopup;
    private EllipsisPopup ellipsisPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_details);

        findViews();
        initListeners();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            comment = Parcels.unwrap(bundle.getParcelable("comment"));
            note = Parcels.unwrap(bundle.getParcelable("note"));
            if (comment == null){
                comment = new Comment("TEST", "TEST", "26/08/2018 22:41:00", 0, 0);
            }
            if (note == null){
                ArrayList<String> list = new ArrayList<>();
                list.add("TEST");
                list.add("#TEST");
                note = new Note("TEST", "TEST", "26/08/2018 22:41:00", list, 0);
            }

            initMainContent(bundle.getBoolean("replay", false));
        }

        initInput();
        initCommentsNumber();
        initRecyclerView();
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
        int noOfComments = comment.getNoOfReplays();
        String noOfCommentsString = noOfComments + " ";
        if (noOfComments == 1){
            noOfCommentsString += getResources().getString(R.string.replay_one);
        }
        else{
            noOfCommentsString += getResources().getString(R.string.replay_two);
        }

        ((TextView)(findViewById(R.id.comment_details_comments_number))).setText(noOfCommentsString);
        ((TextView)(findViewById(R.id.comment_item_replays))).setText(String.valueOf(comment.getNoOfReplays()));
    }

    private void initMainContent(boolean replay){
        username.setText(comment.getUsername());
        date.setText(Utilities.decodeDate(comment.getDate(), getApplicationContext()));
        content.setText(comment.getContent());

        ((TextView)findViewById(R.id.comment_like_number)).setText(String.valueOf(comment.getLikes()));
        if (replay){
            input.setFocusableInTouchMode(true);
            input.requestFocus();
        }
    }


    private void initRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comment_details_comments_recycler_view);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        CommentNoReplayAdapter adapter = new CommentNoReplayAdapter(getComments(), new CommentClickListener() {
            @Override
            public void onItemClick(View v, Comment comment){}

            @Override
            public void onLikeClick(View v, RelativeLayout layout){
                bottomPopup = Utilities.getBottomPopupLogin(CommentDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
            }

            @Override
            public void onReplayClick(View v, Comment comment){
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


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
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
            public void onShareClick(View v, Comment replay){
                Bitmap screenshot = Utilities.getBitmapReplay(CommentDetailsActivity.this, note, comment, replay);
                Utilities.share(screenshot, CommentDetailsActivity.this);
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
                    //input.clearFocus();
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
                        bottomPopup = Utilities.getBottomPopupLogin(CommentDetailsActivity.this, R.layout.bottom_popup_login, bottomPopup);
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
                bottomPopup = Utilities.getBottomPopupLogin(CommentDetailsActivity.this, R.layout.bottom_popup_text, bottomPopup);
            }
        });

        findViewById(R.id.comment_replay_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.setText("");
                length = 0;
                input.setFocusableInTouchMode(true);
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
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
    }

    private List<Comment> getComments(){
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment("niech mnie pan zostawi", "misio69", "26/08/2018 22:41:00"));
        comments.add(new Comment("a chuj z toba", "stachuBachu", "26/08/2018 22:12:00"));
        return comments;
    }


}
