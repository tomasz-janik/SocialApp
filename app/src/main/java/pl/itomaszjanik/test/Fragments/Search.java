package pl.itomaszjanik.test.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.mancj.materialsearchbar.MaterialSearchBar;
import pl.itomaszjanik.test.DataSuggestion;
import pl.itomaszjanik.test.NavigationController;
import pl.itomaszjanik.test.R;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment implements MaterialSearchBar.OnSearchActionListener, FloatingSearchView.OnClearSearchActionListener {

    private NavigationController mNavigationControllerBottom;
    private FloatingSearchView mSearchView;
    private SharedPreferences preferences;
    private static List<DataSuggestion> suggestions = new ArrayList<>();

    public Search() {
    }

    public static Search newInstance() {
        return new Search();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int size = preferences.getInt("suggestion_size",0);

        if (size == 0){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("suggestion_01", "#krakow");
            editor.putString("suggestion_02", "#warszawa");
            editor.putString("suggestion_03", "#mpk");
            editor.putInt("suggestion_size", 3);
            editor.apply();
            if (suggestions.size() == 0){
                suggestions.add(new DataSuggestion(preferences.getString("suggestion_03", "#mpk")));
                suggestions.add(new DataSuggestion(preferences.getString("suggestion_02", "#warszawa")));
                suggestions.add(new DataSuggestion(preferences.getString("suggestion_01", "#krakow")));
            }
        }
        else if (suggestions.size() == 0){
            suggestions.add(new DataSuggestion(preferences.getString("suggestion_01", "#mpk")));
            suggestions.add(new DataSuggestion(preferences.getString("suggestion_02", "#warszawa")));
            suggestions.add(new DataSuggestion(preferences.getString("suggestion_03", "#krakow")));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSearchView = (FloatingSearchView) getActivity().findViewById(R.id.floating_search_view);
        mNavigationControllerBottom = (NavigationController) getActivity().findViewById(R.id.navigation_bottom);
        initSearch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Toast.makeText(getActivity().getApplicationContext(), preferences.getInt("suggestion_size",0)+"", Toast.LENGTH_LONG).show();
        return inflater.inflate(R.layout.search, container, false);
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
                //todo query on server

                query = "#" + query;

                int size = preferences.getInt("suggestion_size", 0);
                if (size == 3){
                    SharedPreferences.Editor editor = preferences.edit();
                    String suggestion_03 = preferences.getString("suggestion_03", "");
                    String suggestion_02 = preferences.getString("suggestion_02", "");

                    editor.putString("suggestion_03", query);
                    suggestions.get(2).setDataName(query);
                    editor.putString("suggestion_02", suggestion_03);
                    suggestions.get(1).setDataName(suggestion_03);
                    editor.putString("suggestion_01", suggestion_02);
                    suggestions.get(0).setDataName(suggestion_02);
                    editor.apply();
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
                //DataSuggestion colorSuggestion = (DataSuggestion) item;

                String textColor = "#000000";
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



}
