package pl.itomaszjanik.test.AddPostTags;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import pl.itomaszjanik.test.R;
import pl.itomaszjanik.test.Values;

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
        textView = (TextView) this.findViewById(R.id.added_hash_text);
        imageButton = (ImageButton) this.findViewById(R.id.added_hash_remove);
        //this.setAlpha(0);
    }

    public void setText(String text){
        if (textView != null){
            if (!text.startsWith("#")){
                text = "#" + text;
            }
            textView.setText(text);
        }
    }

    @Nullable
    public ImageButton getRemoveButton(){
        if (imageButton != null){
            return imageButton;
        }
        return null;
    }

    public void fadeIn(){
        if (!visible){
            visible = true;
            getAnimator().start();
        }
    }

    public void fadeOut(){
        if (visible){
            visible = false;
            getAnimator().reverse();
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
