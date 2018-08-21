package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import pl.itomaszjanik.test.EditTextKeyboard;
import pl.itomaszjanik.test.NavigationController;
import pl.itomaszjanik.test.R;

public class AddPost extends Fragment {

    NavigationController mNavigationControllerBottom;

    public AddPost() { }

    public static AddPost newInstance() {
        return new AddPost();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.add_post, container, false);

        mNavigationControllerBottom = (NavigationController) getActivity().findViewById(R.id.navigation_bottom);
        init(root);

        return root;
    }

    private void init(View view){
        initContent(view);
        initTags(view);
        initScrollView(view);
    }

    private void initContent(View view){
        EditTextKeyboard content = (EditTextKeyboard) view.findViewById(R.id.add_content);

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationControllerBottom.hideLayoutInstant();
            }
        });

        content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    mNavigationControllerBottom.hideLayoutInstant();
                }
            }
        });

        content.setKeyImeChangeListener(new EditTextKeyboard.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mNavigationControllerBottom.showLayoutInstant();
                }
            }
        });
    }

    private void initTags(View view){
        EditTextKeyboard tags = (EditTextKeyboard) view.findViewById(R.id.add_hashes);

        tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationControllerBottom.hideLayoutInstant();
            }
        });

        tags.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    mNavigationControllerBottom.hideLayoutInstant();
                }
            }
        });

        tags.setKeyImeChangeListener(new EditTextKeyboard.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mNavigationControllerBottom.showLayoutInstant();
                }
            }
        });
    }

    private void initScrollView(View view){
        RelativeLayout scrollView = (RelativeLayout) view.findViewById(R.id.layout);

        scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(getActivity());
                mNavigationControllerBottom.showLayoutInstant();
            }
        });

        scrollView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    hideSoftKeyboard(getActivity());
                    mNavigationControllerBottom.showLayoutInstant();
                }
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
