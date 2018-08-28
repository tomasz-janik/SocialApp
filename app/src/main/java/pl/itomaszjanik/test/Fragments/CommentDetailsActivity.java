package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;

import java.util.ArrayList;
import java.util.List;

public class CommentDetailsActivity extends Activity {

    private Comment comment;
    private EditText input;
    private TextView content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_details);

        ((CustomImage) (findViewById(R.id.comment_details_icon_back))).init(R.drawable.ic_arrow_black_24dp, R.drawable.ic_arrow_black_24dp);

        input = (EditText) findViewById(R.id.comment_insert_text);
        input.setHint(getResources().getString(R.string.comment_details_hint));
        content = (TextView) findViewById(R.id.comment_details_content);
        initListeners();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            comment = Parcels.unwrap(bundle.getParcelable("comment"));
            if (comment == null){
                comment = new Comment("TEST", "TEST", "26/08/2018 22:41:00", 0, 0);
            }
            content.setText(comment.getContent());
            if (bundle.getBoolean("replay", false)){
                input.setFocusableInTouchMode(true);
                input.requestFocus();
            }
        }

        int noOfComments = comment.getNoOfReplays();
        String noOfCommentsString = noOfComments + " ";
        if (noOfComments == 1){
            noOfCommentsString += getResources().getString(R.string.replay_one);
        }
        else{
            noOfCommentsString += getResources().getString(R.string.replay_two);
        }

        ((TextView)(findViewById(R.id.comment_details_comments_number))).setText(noOfCommentsString);


        initRecyclerView();
    }


    private void initRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comment_details_comments_recycler_view);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        CommentAdapter adapter = new CommentAdapter(getComments(), new CommentClickListener() {
            @Override
            public void onItemClick(View v, Comment comment) {}

            @Override
            public void onLikeClick(View v, RelativeLayout layout){}

            @Override
            public void onReplayClick(View v, Comment comment){}


        }, this);

        Temp lm = new Temp(this, LinearLayoutManager.VERTICAL,false);
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
                    if (Utilities.checkComment(input.getText().toString(), getApplicationContext())){

                    }
                }

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
