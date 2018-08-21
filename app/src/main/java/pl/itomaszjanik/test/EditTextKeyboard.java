package pl.itomaszjanik.test;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class EditTextKeyboard extends AppCompatEditText {

    private static final String TAG = "Edit Text with Keyboard";
    private KeyImeChange keyImeChangeListener;


    public EditTextKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextKeyboard(Context context) {
        super(context);
    }

    public void setKeyImeChangeListener(KeyImeChange listener){
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange {
        public void onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event){
        if(keyImeChangeListener != null){
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }
}