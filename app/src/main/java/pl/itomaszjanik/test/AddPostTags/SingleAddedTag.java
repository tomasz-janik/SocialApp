package pl.itomaszjanik.test.AddPostTags;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import pl.itomaszjanik.test.R;

public class SingleAddedTag extends LinearLayout {

    private TextView textView;
    private ImageButton imageButton;
    private boolean visible = false;
    private ObjectAnimator animator;


    public SingleAddedTag(Context context) {
        super(context);
        View.inflate(context, R.layout.add_hashesh_bar, this);
        init();
    }

    public SingleAddedTag(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SingleAddedTag(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        textView = this.findViewById(R.id.added_hash_text);
        imageButton = this.findViewById(R.id.added_hash_remove);
    }

    public int setText(String text){
        int format = -2;
        if (textView != null){
            if (!text.startsWith("#")){
                text = "#" + text;
                format = -1;
            }
            else{
                format = 0;
            }
            textView.setText(text);
        }
        return format;
    }

    @Nullable
    ImageButton getRemoveButton(){
        if (imageButton != null){
            return imageButton;
        }
        return null;
    }

    void fadeIn(){
        if (!visible){
            visible = true;
            getAnimator().start();
        }
    }

    private ObjectAnimator getAnimator(){
        if (animator == null){
            animator = ObjectAnimator.ofFloat(this,"alpha",0, 1);

            animator.setDuration(300);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        return animator;
    }


    @Nullable
    public String getText(){
        if (textView != null){
            return textView.getText().toString();
        }
        return null;
    }

}
