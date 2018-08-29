package pl.itomaszjanik.test;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextWatcherBackspace implements TextWatcher {

    private int lastLength;

    public abstract void afterTextChanged(Editable s, boolean backSpace);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        lastLength = s.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        afterTextChanged(s, lastLength > s.length());
    }

}
