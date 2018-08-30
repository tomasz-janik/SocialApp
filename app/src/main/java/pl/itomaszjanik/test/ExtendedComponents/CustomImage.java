package pl.itomaszjanik.test.ExtendedComponents;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.Toast;

public class CustomImage extends android.support.v7.widget.AppCompatImageView {
    //private Drawable drawableActive, drawableInactive;
    private int drawableActive, drawableInactive;
    private boolean active = false;

    public CustomImage(Context context) {
        super(context);
    }

    public CustomImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(int active, int inactive){
        this.drawableActive = active;
        this.drawableInactive = inactive;
    }

    private void changeActive(){
        if (active) active = false;
        else active = true;
    }

    private void changeDrawable(){
        if (active) setImageResource(drawableActive);
        else setImageResource(drawableInactive);
    }

    public void changeState(){
        changeActive();
        changeDrawable();
    }
}
