package pl.itomaszjanik.test.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import org.joda.time.DateTime;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.AddPostTags.AddedTagView;
import pl.itomaszjanik.test.ExtendedComponents.EditTextKeyboard;
import pl.itomaszjanik.test.Remote.PostService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPost extends Fragment {

    private NavigationController mNavigationControllerBottom;
    private ScrollView scrollView;
    private EditTextKeyboard mContent, tags;
    private AddedTagView addedTagView;
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

        mNavigationControllerBottom = getActivity().findViewById(R.id.navigation_bottom);
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
        mContent = view.findViewById(R.id.add_content);

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
        tags = view.findViewById(R.id.add_hashes);

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
                    if (respond > 0){
                        switch (respond){
                            case AddedTagView.INVALID:
                                Utilities.showSnackbarText(getContext(), scrollView, getString(R.string.tags_invalid));
                                break;
                            case AddedTagView.DUPLICATE:
                                Utilities.showSnackbarText(getContext(), scrollView, getString(R.string.tags_duplicate));
                                break;
                            case AddedTagView.LIMIT:
                                Utilities.showSnackbarText(getContext(), scrollView, getString(R.string.tags_too_many));
                                break;
                        }
                    }
                    else{
                        if (respond == 0) {
                            hashesh += " " + tags.getText().toString();
                        }
                        else if (respond == -1){
                            hashesh += " #" + tags.getText().toString();
                        }
                    }
                    tags.setText("");
                }
                return false;
            }
        });
    }

    private void initAddedTags(View view){
        addedTagView = view.findViewById(R.id.add_added_tag_view);
    }

    private void initMainLayout(View view){
        RelativeLayout mainLayout = view.findViewById(R.id.add_main_layout);
        mainLayout.requestFocusFromTouch();

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeyboard(getActivity());
                mNavigationControllerBottom.showLayoutInstant();
            }
        });

        mainLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    Utilities.hideKeyboard(getActivity());
                    mNavigationControllerBottom.showLayoutInstant();
                }
            }
        });
    }

    private void initAdd(final View view) {
        final RelativeLayout addPost = view.findViewById(R.id.add_commit);

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContent.getText().toString().equals("")){
                    Utilities.showSnackbarText(getContext(), scrollView, getString(R.string.add_empty_content));
                }
                else if (hashesh.equals("")){
                    Utilities.showSnackbarText(getContext(), scrollView, getString(R.string.add_empty_tags));
                }
                else{
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(Values.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    DateTime dateTime = new DateTime();
                    String time = dateTime.toString("yyyy-MM-dd HH:mm:ss");
                    if (sharedPreferences.getBoolean("signed", false)){
                        sendPost(sharedPreferences.getString("username", "Anonim"), time, mContent.getText().toString(), hashesh);
                    }
                    else{
                        sendPost("Anonim", time, mContent.getText().toString(), hashesh);
                    }
                }
            }
        });
    }


    private void sendPost(String username, String date, String content, String hashesh) {
        if (!Utilities.isNetworkAvailable(getContext())){
            Utilities.showSnackbarText(getContext(), scrollView, getString(R.string.no_internet));
            return;
        }

        postService.savePost(username, date, content, hashesh).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@Nullable Call<Note> call, @Nullable Response<Note> response) {
                if (response != null && response.isSuccessful()){
                    Utilities.showSnackbarText(getContext(), scrollView, getString(R.string.added_post));
                    mContent.setText("");
                    addedTagView.clearTags();
                }
            }

            @Override
            public void onFailure(@Nullable Call<Note> call, @Nullable Throwable t) {
                Utilities.showSnackbarText(getContext(), scrollView, getString(R.string.couldnt_add_post));
            }
        });
    }

}
