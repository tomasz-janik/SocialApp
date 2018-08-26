package pl.itomaszjanik.test.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import pl.itomaszjanik.test.AddPostTags.AddedTagView;
import pl.itomaszjanik.test.EditTextKeyboard;
import pl.itomaszjanik.test.NavigationController;
import pl.itomaszjanik.test.R;

public class AddPost extends Fragment {

    private NavigationController mNavigationControllerBottom;
    private ScrollView scrollView;
    private EditTextKeyboard content, tags;
    private AddedTagView addedTagView;

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
        scrollView = root.findViewById(R.id.scroll_view);
        init(root);

        return root;
    }

    private void init(View view){
        initContent(view);
        initTags(view);
        initAddedTags(view);
        initMainLayout(view);
        initAdd(view);
    }

    private void initContent(final View view){
        content = (EditTextKeyboard) view.findViewById(R.id.add_content);

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
                    content.clearFocus();
                    view.findViewById(R.id.add_main_layout).requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0,content.getBottom());
                        }
                    });
                }
            }
        });
    }

    private void initTags(final View view){
        tags = (EditTextKeyboard) view.findViewById(R.id.add_hashes);
        //tags.setImeActionLabel("OK", KeyEvent.KEYCODE_ENTER);


        tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationControllerBottom.hideLayoutInstant();
                if (tags.getText().toString().equals("")){
                    //tags.setText("#");
                }
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
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    mNavigationControllerBottom.showLayoutInstant();
                    view.findViewById(R.id.add_main_layout).requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0,tags.getBottom());
                        }
                    });
                }
            }
        });

        tags.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tags.clearFocus();
                    addedTagView.addTag(tags.getText().toString());
                    tags.setText("");
                }
                return false;
            }
        });
    }

    private void initAddedTags(View view){
        addedTagView = (AddedTagView) view.findViewById(R.id.add_added_tag_view);
    }

    private void initMainLayout(View view){
        RelativeLayout mainLayout = (RelativeLayout) view.findViewById(R.id.add_main_layout);
        mainLayout.requestFocusFromTouch();

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(getActivity());
                mNavigationControllerBottom.showLayoutInstant();
            }
        });

        mainLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    hideSoftKeyboard(getActivity());
                    mNavigationControllerBottom.showLayoutInstant();
                }
            }
        });
    }

    private void initAdd(final View view) {
        final RelativeLayout addPost = (RelativeLayout) view.findViewById(R.id.add_commit);

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (content.getText().toString().equals("")){
                    Toast.makeText(getContext(), getResources().getText(R.string.add_empty_content), Toast.LENGTH_LONG).show();
                }
                else if (tags.getText().toString().equals("")){
                    Toast.makeText(getContext(), getResources().getText(R.string.add_empty_tags), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
