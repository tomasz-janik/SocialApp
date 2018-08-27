package pl.itomaszjanik.test;

import android.content.Context;
import android.widget.Toast;

public class Utilities {

    public static boolean checkComment(String string, Context context){
        if (string == null || string.equals("")){
            Toast.makeText(context, context.getResources().getString(R.string.comment_invalid), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (string.length() > Values.COMMENT_LIMIT){
            Toast.makeText(context, context.getResources().getString(R.string.comment_too_long), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
