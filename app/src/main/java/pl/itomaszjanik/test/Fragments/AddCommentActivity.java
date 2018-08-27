package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;

import java.util.ArrayList;
import java.util.List;

public class AddCommentActivity extends Activity {

    private EditText input;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_comment);
        input = (EditText) findViewById(R.id.add_comment_edit_text);

        Bundle bundle = getIntent().getExtras();
        String comment = "";
        if (bundle != null){
            comment = bundle.getString("input");
        }

        input.setText(comment);
        initListeners();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void initListeners(){
        findViewById(R.id.add_comment_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
