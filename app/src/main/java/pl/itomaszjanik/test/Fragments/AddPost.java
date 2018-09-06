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
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.AddPostTags.AddedTagView;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.ExtendedComponents.EditTextKeyboard;
import pl.itomaszjanik.test.Remote.NoteService;
import pl.itomaszjanik.test.Remote.PostService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPost extends Fragment {

    private NavigationController mNavigationControllerBottom;
    private ScrollView scrollView;
    private EditTextKeyboard mContent, tags;
    private AddedTagView addedTagView;
    private BottomPopup bottomPopup;
    private PostService postService;
    private String hashesh = "";

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
        postService = RetrofitClient.getClient(Values.URL).create(PostService.class);
    }

    private void initContent(final View view){
        mContent = (EditTextKeyboard) view.findViewById(R.id.add_content);

        mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationControllerBottom.hideLayoutInstant();
            }
        });

        mContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    mNavigationControllerBottom.hideLayoutInstant();
                }
            }
        });

        mContent.setKeyImeChangeListener(new EditTextKeyboard.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mNavigationControllerBottom.showLayoutInstant();
                    mContent.clearFocus();
                    view.findViewById(R.id.add_main_layout).requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0,mContent.getBottom());
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
                    int respond = addedTagView.addTag(tags.getText().toString());
                    if (respond == 0) {
                        hashesh += " " + tags.getText().toString();
                    }
                    else if (respond == -1){
                        hashesh += " #" + tags.getText().toString();
                    }
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
                if (mContent.getText().toString().equals("")){
                    bottomPopup = Utilities.getBottomPopupText(getContext(),
                            R.layout.bottom_popup_text, R.id.bottom_popup_text,
                            getString(R.string.add_empty_content), bottomPopup);
                }
                else if (hashesh.equals("")){
                    bottomPopup = Utilities.getBottomPopupText(getContext(),
                            R.layout.bottom_popup_text, R.id.bottom_popup_text,
                            getString(R.string.add_empty_tags), bottomPopup);
                }
                else{
                    Instant now = new Instant();
                    DateTime dateTime = new DateTime();
                    String time = dateTime.toString("yyyy/MM/dd HH:mm:ss");
                    //String hashesh = addedTagView.getTags();

                    sendPost("admin", time, mContent.getText().toString(), hashesh, 0);
                }
            }
        });
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void sendPost(String username, String date, String content, String hashesh, int comment) {
        postService.savePost(username, date, content, hashesh, comment).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                //Toast.makeText(getContext(), ":)", Toast.LENGTH_SHORT).show();
                bottomPopup = Utilities.getBottomPopupText(getContext(),
                        R.layout.bottom_popup_text, R.id.bottom_popup_text,
                        getString(R.string.added_post), bottomPopup);
                mContent.setText("");

            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                //Toast.makeText(getContext(), ":(\n"+t, Toast.LENGTH_SHORT).show();
                bottomPopup = Utilities.getBottomPopupText(getContext(),
                        R.layout.bottom_popup_text, R.id.bottom_popup_text,
                        getString(R.string.couldnt_add_post), bottomPopup);
            }
        });
    }

}
