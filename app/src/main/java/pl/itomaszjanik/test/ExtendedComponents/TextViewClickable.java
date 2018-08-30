package pl.itomaszjanik.test.ExtendedComponents;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import pl.itomaszjanik.test.R;

public class TextViewClickable extends AppCompatTextView {

    private boolean clicked = false;

    public TextViewClickable(Context context) {
        super(context);
    }

    public TextViewClickable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewClickable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void changeState(){
        if (clicked){
            this.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextInactive, null));
            clicked = false;
        }
        else{
            this.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorTextActive, null));
            clicked = true;
        }
    }
}
