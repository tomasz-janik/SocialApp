package pl.itomaszjanik.test.AddPostTags;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import com.google.android.flexbox.FlexboxLayout;
import pl.itomaszjanik.test.Values;

import java.util.ArrayList;
import java.util.List;

public class AddedTagView extends FlexboxLayout {

    public static final int INVALID = 1;
    public static final int DUPLICATE = 2;
    public static final int LIMIT = 3;

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
                return INVALID;
            }
            if (checkDuplicate(text)){
                return DUPLICATE;
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
            return LIMIT;
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

    public void clearTags(){
        for (SingleAddedTag tag : addedTags){
            this.removeView(tag);
        }
        addedTags.clear();
        noOfTags = 0;
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

    public String getTags(){
        StringBuilder builder = new StringBuilder();
        for (SingleAddedTag tag : addedTags){
            builder.append(tag.getText());
            builder.append(" ");
        }

        return builder.toString();
    }

}
