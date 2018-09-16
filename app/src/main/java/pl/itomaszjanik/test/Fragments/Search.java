package pl.itomaszjanik.test.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.mancj.materialsearchbar.MaterialSearchBar;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Posts.*;
import pl.itomaszjanik.test.Remote.GenerateIDCallback;
import pl.itomaszjanik.test.Search.DataSuggestion;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment implements MaterialSearchBar.OnSearchActionListener, GetPostsCallback,
        NoteClickListener, OnEndScrolled, ReactNoteCallback, UpdatePostCallback, NoteNoMoreClickListener,
        GenerateIDCallback, FloatingSearchView.OnClearSearchActionListener {

    private static final int GEN_LOAD = 0;
    private static final int GEN_REACT = 1;

    private NavigationController mNavigationControllerBottom;
    private FloatingSearchView mSearchView;
    private static List<DataSuggestion> suggestions = new ArrayList<>();

    private BottomPopup bottomPopup;
    private CardView nonePosts;

    private RecyclerView recyclerView;
    private NoteAdapter mNoteAdapter;

    private Note currentNote;
    private View currentView;

    private SharedPreferences sharedPreferences;
    private int userID;

    private int page = 0;
    private boolean loading = false;
    private String search;

    public Search() {
    }

    public static Search newInstance() {
        return new Search();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int size = sharedPreferences.getInt("suggestion_size",0);

        if (size == 0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("suggestion_01", "#krakow");
            editor.putString("suggestion_02", "#warszawa");
            editor.putString("suggestion_03", "#mpk");
            editor.putInt("suggestion_size", 3);
            editor.apply();
            if (suggestions.size() == 0){
                suggestions.add(new DataSuggestion(sharedPreferences.getString("suggestion_03", "#mpk")));
                suggestions.add(new DataSuggestion(sharedPreferences.getString("suggestion_02", "#warszawa")));
                suggestions.add(new DataSuggestion(sharedPreferences.getString("suggestion_01", "#krakow")));
            }
        }
        else if (suggestions.size() == 0){
            suggestions.add(new DataSuggestion(sharedPreferences.getString("suggestion_01", "#mpk")));
            suggestions.add(new DataSuggestion(sharedPreferences.getString("suggestion_02", "#warszawa")));
            suggestions.add(new DataSuggestion(sharedPreferences.getString("suggestion_03", "#krakow")));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSearchView = getActivity().findViewById(R.id.floating_search_view);
        mNavigationControllerBottom = getActivity().findViewById(R.id.navigation_bottom);
        nonePosts = view.findViewById(R.id.posts_none);
        init(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (currentNote != null){
            userID = sharedPreferences.getInt("userID", 0);
            Utilities.updatePost(userID,this, currentNote);
        }
    }

    @Override
    public void getPostSucceeded(List<Note> list){
        nonePosts.setVisibility(View.GONE);
        if (list.size() != 0){
            if (loading){
                mNoteAdapter.insert(list);
                loading = false;
            }
            else{
                mNoteAdapter.removeAll();
                mNoteAdapter.insert(list);
            }
        }
        else if (mNoteAdapter.getItemCount() != 0){
            mNoteAdapter.insertNull();
        }
        else if (mNoteAdapter.getItemCount() == 0){
            nonePosts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getPostFailed(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.couldnt_refresh), bottomPopup);
        }
    }

    @Override
    public void getPostNoInternet(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.no_internet), bottomPopup);
        }
    }

    @Override
    public void onItemClick(View view, Note note){
        Bundle data = new Bundle();
        data.putParcelable("note", Parcels.wrap(note));

        currentNote = note;
        currentView = view;

        Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
        intent.putExtras(data);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onLikeClick(View view, Note note){
        currentView = view;
        currentNote = note;
        userID = sharedPreferences.getInt("userID", 0);

        if (userID == 0){
            Utilities.generateID(GEN_REACT, this, getContext());
        }
        else{
            Utilities.onLikeNoteClick(1, getContext(), Search.this, view, note);
        }
    }

    @Override
    public void reactNoteLikeSucceeded(Note note, View view){
        if (isAdded()){
            note.changeLiked();
            note.incrementLikes();
            ((TextView)view.findViewById(R.id.note_like_number)).setText(String.valueOf(note.getLikes()));
            ((TextView)view.findViewById(R.id.note_like_text)).setTextColor(Color.BLUE);
        }
    }

    @Override
    public void reactNoteUnlikeSucceeded(Note note, View view){
        if (isAdded()){
            note.changeLiked();
            note.decrementLikes();
            ((TextView)view.findViewById(R.id.note_like_number)).setText(String.valueOf(note.getLikes()));
            ((TextView)view.findViewById(R.id.note_like_text)).setTextColor(Color.parseColor("#747474"));
        }
    }

    @Override
    public void reactNoteLikeFailed(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.couldnt_like_post), bottomPopup);
        }
    }

    @Override
    public void reactNoteUnlikeFailed(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.couldnt_unlike_post), bottomPopup);
        }
    }

    @Override
    public void reactNoteNoInternet(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.no_internet), bottomPopup);
        }
    }

    @Override
    public void updatePostSucceeded(Note note){
        if (isAdded() && currentView != null && note != null){
            currentNote.setLiked(note.getLiked());
            currentNote.setLikes(note.getLikes());
            currentNote.setComments(note.getComments());
            ((TextView)(currentView.findViewById(R.id.note_item_comments_number))).setText(String.valueOf(note.getComments()));
            TextView likes_number = currentView.findViewById(R.id.note_like_number);
            likes_number.setText(String.valueOf(note.getLikes()));
            if (note.getLiked()){
                ((TextView)(currentView.findViewById(R.id.note_like_text))).setTextColor(Color.BLUE);
            }
            else{
                ((TextView)(currentView.findViewById(R.id.note_like_text))).setTextColor(Color.parseColor("#747474"));
            }
        }
    }

    @Override
    public void updatePostFailed(){ }

    @Override
    public void onGenerateSuccess(String username, int userID, int task){
        this.userID = userID;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putInt("userID", userID);
        editor.apply();

        switch (task){
            case GEN_LOAD:
                getPosts(search);
                break;
            case GEN_REACT:
                onLikeClick(currentView, currentNote);
                break;
        }
    }

    @Override
    public void onGenerateFailed(int task){
        switch (task){
            case GEN_LOAD:
                getPostFailed();
                break;
            case GEN_REACT:
                reactNoteLikeFailed();
                break;
        }
    }

    @Override
    public void onGenerateNoInternet(int task){
        switch (task){
            case GEN_LOAD:
                getPostNoInternet();
                break;
            case GEN_REACT:
                reactNoteNoInternet();
                break;
        }
    }

    @Override
    public void onRefreshClick(){
        recyclerView.smoothScrollToPosition(0);
        loading = false;
        page = 0;
        getPosts(search);
    }

    @Override
    public void onEnd(){
        if (!loading){
            page++;
            loading = true;
            getPosts(search);
        }
    }

    private void init(View view){
        initSearch();
        initRecyclerView(view);

        userID = sharedPreferences.getInt("userID", 0);
    }

    private void initRecyclerView(View view){
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.item_separator, null);
        if (divider != null)
            dividerItemDecoration.setDrawable(divider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnScrollListener(new ListScrollBottomListener((NavigationController) getActivity().findViewById(R.id.navigation_bottom)));

        mNoteAdapter = new NoteAdapter(R.layout.note_search_top, getContext());
        mNoteAdapter.initListeners(this, this, this);
        recyclerView.setAdapter(mNoteAdapter);
    }


    private void initSearch() {
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                mSearchView.setText(searchSuggestion.getBody().substring(1));
                mSearchView.clearSuggestions();
            }

            @Override
            public void onSearchAction(String query) {
                page = 0;
                loading = false;
                search = query;
                getPosts(search);

                if (query == null || query.equals("")){
                    return;
                }
                query = "#" + query;

                int size = sharedPreferences.getInt("suggestion_size", 0);
                if (size == 3){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String suggestion_03 = sharedPreferences.getString("suggestion_03", "");
                    String suggestion_02 = sharedPreferences.getString("suggestion_02", "");
                    String suggestion_01 = sharedPreferences.getString("suggestion_01", "");
                    if (query.equals(suggestion_02)){
                        editor.putString("suggestion_03", query);
                        suggestions.get(2).setDataName(query);
                        editor.putString("suggestion_02", suggestion_03);
                        suggestions.get(1).setDataName(suggestion_03);
                        editor.putString("suggestion_01", suggestion_01);
                        suggestions.get(0).setDataName(suggestion_01);
                        editor.apply();
                    }
                    else if (query.equals(suggestion_03)){
                        editor.putString("suggestion_03", suggestion_03);
                        suggestions.get(2).setDataName(suggestion_03);
                        editor.putString("suggestion_02", suggestion_02);
                        suggestions.get(1).setDataName(suggestion_02);
                        editor.putString("suggestion_01", suggestion_01);
                        suggestions.get(0).setDataName(suggestion_01);
                        editor.apply();
                    }
                    else{
                        editor.putString("suggestion_03", query);
                        suggestions.get(2).setDataName(query);
                        editor.putString("suggestion_02", suggestion_03);
                        suggestions.get(1).setDataName(suggestion_03);
                        editor.putString("suggestion_01", suggestion_02);
                        suggestions.get(0).setDataName(suggestion_02);
                        editor.apply();
                    }
                }
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                mNavigationControllerBottom.hideLayoutInstant();


                if (mSearchView.getText() == null || mSearchView.getText().equals("")){
                    mSearchView.clearSuggestions();
                    mSearchView.swapSuggestions(suggestions);
                }

                //Toast.makeText(getActivity().getApplicationContext(), "" + suggestions.size(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFocusCleared() {
                mNavigationControllerBottom.showLayoutInstant();
                mSearchView.clearSuggestions();
            }
        });

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (newQuery.equals("")){
                    mSearchView.clearSuggestions();
                    mSearchView.swapSuggestions(suggestions);
                }
                else{
                    mSearchView.clearSuggestions();
                }
            }
        });


        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                String textLight = "#787878";

                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_history_black_24dp, null));
                leftIcon.setAlpha(.36f);

                textView.setTextColor(Color.parseColor(textLight));

            }

        });
    }

    @Override
    public void onClearSearchClicked(){
        mSearchView.clearSuggestions();
        mSearchView.swapSuggestions(suggestions);
    }


    @Override
    public void onSearchStateChanged(boolean enabled) {
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode){
            case MaterialSearchBar.BUTTON_NAVIGATION:
                //drawer.openDrawer(Gravity.LEFT);
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                mNavigationControllerBottom.showLayoutInstant();
                //mSearchView.disableSearch();
                break;
        }
    }

    private void getPosts(String search){
        userID = sharedPreferences.getInt("userID", 0);
        if (userID == 0){
            Utilities.generateID(GEN_LOAD, this, getContext());
        }
        else{
            Utilities.getPostsSearch(userID, page, search, this, getContext());
        }
    }



}
