package pl.itomaszjanik.test.ExtendedComponents;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class EditTextNoFocusOnBack extends AppCompatEditText {

    private int length = 0;

    public EditTextNoFocusOnBack(Context context) {
        super(context);
    }

    public EditTextNoFocusOnBack(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextNoFocusOnBack(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLength(int length){
        this.length = length;
    }

    @Override
    public boolean onKeyPreIme(int key_code, KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
            this.clearFocus();
        }
        return super.onKeyPreIme(key_code, event);
    }
}
