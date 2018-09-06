package pl.itomaszjanik.test.AddPostTags;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.flexbox.FlexboxLayout;
import pl.itomaszjanik.test.R;
import pl.itomaszjanik.test.Utilities;
import pl.itomaszjanik.test.Values;

import java.util.ArrayList;
import java.util.List;

public class AddedTagView extends FlexboxLayout {

    private List<SingleAddedTag> addedTags;
    private int noOfTags;

    public AddedTagView(Context context) {
        super(context);
        if (addedTags == null){
            addedTags = new ArrayList<>();
            noOfTags = 0;
        }
    }

    public AddedTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (addedTags == null){
            addedTags = new ArrayList<>();
            noOfTags = 0;
        }
    }

    public int addTag(String text){
        if (noOfTags < Values.MAX_TAGS_NUMBER){
            if (text == null || text.equals("")){
                Toast.makeText(getContext(), getResources().getString(R.string.tags_invalid), Toast.LENGTH_SHORT).show();
                return 1;
            }
            if (checkDuplicate(text)){
                Toast.makeText(getContext(), getResources().getString(R.string.tags_duplicate), Toast.LENGTH_SHORT).show();
                return 2;
            }
            SingleAddedTag tag = new SingleAddedTag(getContext());
            int format = tag.setText(text);
            setClickListener(tag);
            this.addView(tag);
            tag.fadeIn();
            addedTags.add(tag);
            noOfTags++;

            return format;
        }
        else{
            Toast.makeText(getContext(), getResources().getString(R.string.tags_too_many), Toast.LENGTH_LONG).show();
            return 3;
        }
    }

    /**
     *
     * @param text text used as #
     * @return true if it isn't duplicate, false if it is
     */
    private boolean checkDuplicate(String text){
        text = "#" + text;
        for (int i = 0; i < noOfTags; i++){
            if (text.equals(addedTags.get(i).getText())){
                return true;
            }
        }
        return false;
    }

    private void removeTag(SingleAddedTag tag){
        this.removeView(tag);
        addedTags.remove(tag);
        noOfTags--;
    }

    private void setClickListener(final SingleAddedTag tag){
        if (tag != null){
            ImageButton removeButton = tag.getRemoveButton();
            if (removeButton != null && !removeButton.hasOnClickListeners())
            tag.getRemoveButton().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeTag(tag);
                }
            });
        }
    }

    /**
     *
     * @return true if there can't be anymore tags, false when there can be
     */
    public boolean isViewFull(){
        return noOfTags == Values.MAX_TAGS_NUMBER;
    }


}
