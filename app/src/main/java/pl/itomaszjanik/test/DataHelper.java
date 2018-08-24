package pl.itomaszjanik.test;

import android.content.Context;
import android.widget.Filter;

import java.util.*;

public class DataHelper {

    private static List<DataSuggestion> sDataSuggestions =
            new ArrayList<>(Arrays.asList(
                    new DataSuggestion("green"),
                    new DataSuggestion("blue"),
                    new DataSuggestion("pink"),
                    new DataSuggestion("purple"),
                    new DataSuggestion("brown"),
                    new DataSuggestion("gray"),
                    new DataSuggestion("Granny Smith Apple"),
                    new DataSuggestion("Indigo"),
                    new DataSuggestion("Periwinkle"),
                    new DataSuggestion("Mahogany"),
                    new DataSuggestion("Maize"),
                    new DataSuggestion("Mahogany"),
                    new DataSuggestion("Outer Space"),
                    new DataSuggestion("Melon"),
                    new DataSuggestion("Yellow"),
                    new DataSuggestion("Orange"),
                    new DataSuggestion("Red"),
                    new DataSuggestion("Orchid")));


    public static List<DataSuggestion> getHistory(Context context, int count) {

        List<DataSuggestion> suggestionList = new ArrayList<>();
        DataSuggestion dataSuggestion;
        for (int i = 0; i < sDataSuggestions.size(); i++) {
            dataSuggestion = sDataSuggestions.get(i);
            dataSuggestion.setIsHistory(true);
            suggestionList.add(dataSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (DataSuggestion dataSuggestion : sDataSuggestions) {
            dataSuggestion.setIsHistory(false);
        }
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<DataSuggestion> results);
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                List<DataSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (DataSuggestion suggestion : sDataSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<DataSuggestion>() {
                    @Override
                    public int compare(DataSuggestion lhs, DataSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<DataSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

}
