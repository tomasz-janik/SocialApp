package pl.itomaszjanik.test;

import android.os.Parcel;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class DataSuggestion implements SearchSuggestion {

    private String mColorName;
    private boolean mIsHistory = false;

    public DataSuggestion(String suggestion) {
        this.mColorName = suggestion.toLowerCase();
    }

    public DataSuggestion(Parcel source) {
        this.mColorName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mColorName;
    }

    public static final Creator<DataSuggestion> CREATOR = new Creator<DataSuggestion>() {
        @Override
        public DataSuggestion createFromParcel(Parcel in) {
            return new DataSuggestion(in);
        }

        @Override
        public DataSuggestion[] newArray(int size) {
            return new DataSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mColorName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
