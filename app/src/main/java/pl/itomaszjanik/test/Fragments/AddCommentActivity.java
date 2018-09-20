package pl.itomaszjanik.test.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.joda.time.DateTime;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.Comments.CommentPostCallback;
import pl.itomaszjanik.test.Comments.ReplayCommentCallback;
import pl.itomaszjanik.test.ExtendedComponents.ConfirmExitDialogFragment;

public class AddCommentActivity extends FragmentActivity implements ConfirmExitDialogFragment.NoticeDialogListener,
        OnLoginClick, SwitchLogged, CommentPostCallback, ReplayCommentCallback {

    private EditText input;
    private int length;
    private SharedPreferences sharedPreferences;
    private Comment comment;
    private Note note;
    private int userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_comment);

        input = findViewById(R.id.add_comment_edit_text);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            note = Parcels.unwrap(bundle.getParcelable("note"));
            comment = Parcels.unwrap(bundle.getParcelable("comment"));

            String comment = bundle.getString("input", "");
            length = bundle.getInt("name_length", 0);

            Spannable spannable = new SpannableString(comment);
            if (length > 0){
                ((TextView)findViewById(R.id.add_comment_info)).setText(R.string.replay_comment_string);
                spannable.setSpan(new BackgroundColorSpan(Color.parseColor("#22000000")),0, length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else{
                ((TextView)findViewById(R.id.add_comment_info)).setText(R.string.add_comment_string);
            }

            input.setText(spannable);
            input.setSelection(comment.length());
            input.setFocusableInTouchMode(true);
            input.requestFocus();
        }
        input.requestFocus();

        init();
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

    private void init(){
        initInput();
        initListeners();

        sharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", 0);
    }

    private void initInput(){
        input.addTextChangedListener(new TextWatcherBackspace() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                if (backSpace){
                    if (s.toString().length() < length){
                        input.setText("");
                        length = 0;
                    }
                }
            }
        });
        Utilities.showKeyboard(this);
    }

    @Override
    public void onBackPressed(){
        onExit();
    }

    @Override
    public void commentPostSucceeded(Comment comment){
        input.setText("");

        Intent returnIntent = new Intent();
        Bundle data = new Bundle();
        data.putParcelable("comment", Parcels.wrap(comment));
        returnIntent.putExtra("result", data);

        setResult(FragmentActivity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
    public void replayCommentSucceeded(Replay replay){
        input.setText("");

        Intent returnIntent = new Intent();
        Bundle data = new Bundle();
        data.putParcelable("replay", Parcels.wrap(replay));
        returnIntent.putExtra("result", data);

        setResult(FragmentActivity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void replayCommentFailed(){
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.comment_post_couldnt));
    }

    @Override
    public void replayCommentNoInternet(){
        Utilities.showSnackbarText(this, findViewById(R.id.main_layout),
                getString(R.string.no_internet));
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        setResult(FragmentActivity.RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onLoginClick(){
        //login = true;
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = ProfileUnsigned.newInstance();

        mTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        mTransaction.replace(R.id.main_content, fragment, fragment.getClass().getName());
        mTransaction.addToBackStack(null);

        mTransaction.commit();
    }

    @Override
    public void switchLogged(){
        getSupportFragmentManager().popBackStack();
    }

    private void onExit(){
        String inputString = input.getText().toString();
        if (inputString.equals("") || inputString.length() == length){
            setResult(FragmentActivity.RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else{
            DialogFragment exit_dialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag("confirm_exit");
            if (exit_dialog == null){
                DialogFragment newFragment = new ConfirmExitDialogFragment();
                newFragment.show(getSupportFragmentManager(), "confirm_exit");
            }
            else{
                if (exit_dialog.isVisible()) {
                    exit_dialog.dismiss();
                }
                else{
                    setResult(FragmentActivity.RESULT_CANCELED);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        }
    }

    private void initListeners(){
        findViewById(R.id.add_comment_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onExit();
            }
        });

        findViewById(R.id.add_comment_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });

        findViewById(R.id.add_comment_commit_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
    }


    private void addComment(){
        if (input != null){
            int checkComment = Utilities.checkComment(input.getText().toString());

            if (checkComment > 0){
                Utilities.hideKeyboard(AddCommentActivity.this);
                input.clearFocus();

                if (sharedPreferences.getBoolean("signed", false)) {
                    DateTime dateTime = new DateTime();
                    String time = dateTime.toString("yyyy-MM-dd HH:mm:ss");
                    userID = sharedPreferences.getInt("userID", 0);

                    if (note != null){
                        Utilities.commentPost(note.getId(), userID,
                                sharedPreferences.getString("username", "Anonim"), time, input.getText().toString(),
                                AddCommentActivity.this, AddCommentActivity.this);
                    }
                    else{
                        Utilities.replayComment(comment.getCommentID(), userID,
                                sharedPreferences.getString("username", "Anonim"), time, input.getText().toString(),
                                AddCommentActivity.this, AddCommentActivity.this);
                    }
                }
                else{
                    Utilities.showSnackbarLogin(AddCommentActivity.this,
                            findViewById(R.id.add_main_layout), getString(R.string.not_logged));
                }
            }
            else{
                Utilities.errorComment(checkComment, AddCommentActivity.this,
                        findViewById(R.id.add_main_layout));
            }

        }




    }

}
