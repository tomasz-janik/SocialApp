package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import pl.itomaszjanik.test.*;

public class AddCommentActivity extends FragmentActivity implements ConfirmExitDialogFragment.NoticeDialogListener {

    private EditText input;
    private int length;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_comment);
        input = (EditText) findViewById(R.id.add_comment_edit_text);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
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
            input.setFocusableInTouchMode(true);
            input.requestFocus();
            input.setSelection(comment.length());
        }

        //input.setText(comment);
        initInput();
        initListeners();
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
    }

    @Override
    public void onBackPressed(){
        onExit();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void onExit(){
        String inputString = input.getText().toString();
        if (inputString.equals("") || inputString.length() == length){
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
                if (Utilities.checkComment(input.getText().toString(), getApplicationContext())){

                }
            }
        });

        findViewById(R.id.add_comment_commit_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.checkComment(input.getText().toString(), getApplicationContext())){

                }
            }
        });
    }


}
