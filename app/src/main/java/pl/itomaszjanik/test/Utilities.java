package pl.itomaszjanik.test;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.ResponseBody;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Remote.FailedCallback;
import pl.itomaszjanik.test.Remote.PostService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Utilities {

    public static BottomPopup onLikePostClick(Context context, FailedCallback callback, Note note, View view,
                                              BottomPopup bottomPopup, int textID, int numberID){
        if (isNetworkAvailable(context)){
            if (note.getLiked()){
                Utilities.unlikePost(callback, note, view, textID, numberID);
            }
            else{
                Utilities.likePost(callback, note, view, textID, numberID);
            }
        }
        else{
            bottomPopup = getBottomPopupText(context,
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    context.getString(R.string.no_internet), bottomPopup);
        }
        return bottomPopup;
    }

    public static void likePost(FailedCallback failedCallback, Note note, View view, int textID, int numberID){
        reactPostCall(true, failedCallback, note, view, textID, numberID);
    }

    public static void unlikePost(FailedCallback failedCallback, Note note, View view, int textID, int numberID){
        reactPostCall(false, failedCallback, note, view, textID, numberID);
    }

    private static void reactPostCall(final boolean like, final FailedCallback failedCallback, final Note note,
                                      final View view, final int textID, final int numberID){
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        Call<ResponseBody> call;
        if (like){
            call = service.likePost(note.getId(), 1);
        }
        else{
            call = service.unlikePost(note.getId(), 1);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (like){
                    likeResponse(note, view, response.body(), textID, numberID);
                }
                else{
                    unlikeResponse(note, view, response.body(), textID, numberID);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (like){
                    failedCallback.likeFailed();
                }
                else{
                    failedCallback.unlikeFailed();
                }
            }
        });
    }

    private static void likeResponse(Note note, View view, ResponseBody response, int textID, int numberID){
        note.setLiked(true);
        try{
            if (response != null){
                String output = response.string();
                if (output.equals("yes")){
                    TextView likes_number = view.findViewById(numberID);
                    int likesNo = Integer.valueOf(likes_number.getText().toString()) + 1;
                    likes_number.setText(String.valueOf(likesNo));
                    ((TextView)(view.findViewById(textID))).setTextColor(Color.BLUE);
                    note.setLikes(likesNo);
                    note.setLiked(true);
                }
            }
        } catch (Exception e){
            Log.e(":(",":(");
        }
    }

    private static void unlikeResponse(Note note, View view, ResponseBody response, int textID, int numberID){
        note.setLiked(false);
        try{
            if (response != null){
                String output = response.string();
                if (output.equals("yes")){
                    TextView likes_number = view.findViewById(numberID);
                    int likesNo = Integer.valueOf(likes_number.getText().toString()) - 1;
                    likes_number.setText(String.valueOf(likesNo));
                    ((TextView)(view.findViewById(textID))).setTextColor(Color.parseColor("#747474"));
                    note.setLikes(likesNo);
                    note.setLiked(false);
                }
            }
        } catch (Exception e){
            Log.e(":(",":(");
        }
    }

    public static int checkComment(String string, Context context){
        if (string == null || string.equals("")){
            return Values.COMMENT_EMPTY;
        }
        if (string.length() > Values.COMMENT_LIMIT){
            return Values.COMMENT_TOO_LONG;
        }
        return 1;
    }

    public static BottomPopup errorComment(int error, Context context, int layout, BottomPopup popup){
        switch (error){
            case Values.COMMENT_EMPTY:
                popup = getBottomPopupText(context, R.layout.bottom_popup_text, R.id.bottom_popup_text, context.getString(R.string.comment_invalid), popup);
                break;
            case Values.COMMENT_TOO_LONG:
                popup = getBottomPopupText(context, R.layout.bottom_popup_text, R.id.bottom_popup_text, context.getString(R.string.comment_too_long), popup);
                break;
        }
        return popup;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
    }

    public static BottomPopup getBottomPopupLogin(Context context, int layout, BottomPopup popup){
        if (popup == null || popup.getText() != null){
            popup = new BottomPopup.Builder(context)
                    .setContentView(layout)
                    .bindClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), "Click Button", Toast.LENGTH_SHORT).show();
                        }
                    }, R.id.bottom_popup_login)
                    .setGravity(Gravity.BOTTOM)
                    .setAutoDismiss(true)
                    .build();
        }

        popup.show();
        return popup;
    }

    public static BottomPopup getBottomPopupText(Context context, int layout, int textView, String text, BottomPopup popup){
        if (popup == null || popup.getText() == null || popup.getText().equals(context.getString(R.string.loading))){
            popup = new BottomPopup.Builder(context)
                    .setContentView(layout)
                    .setString(text, textView)
                    .setAutoDismiss(true)
                    .setGravity(Gravity.BOTTOM)
                    .build();
        }
        else if (!popup.getText().equals(text)){
            popup.setTextView(text);
        }
        popup.show();

        return popup;
    }

    public static BottomPopup getBottomPopupText(Context context, int layout, int textView, String text, BottomPopup popup, boolean dismiss){
        if (popup == null || popup.getText() == null || popup.getText().equals(context.getString(R.string.loading))){
            popup = new BottomPopup.Builder(context)
                    .setContentView(layout)
                    .setString(text, textView)
                    .setAutoDismiss(dismiss)
                    .setGravity(Gravity.BOTTOM)
                    .build();
        }
        else if (!popup.getText().equals(text)){
            popup.setTextView(text);
        }
        popup.show();

        return popup;
    }

    public static BottomPopup getBottomPopupLoading(Context context, int layout, int textView, String text, BottomPopup popup){
        if (popup == null || popup.getText() == null || !popup.getText().equals(text)){
            popup = new BottomPopup.Builder(context)
                    .setContentView(layout)
                    .setString(text, textView)
                    .setAutoDismiss(true)
                    .setGravity(Gravity.BOTTOM)
                    .build();
        }
        popup.show();

        return popup;
    }

    public static BottomPopup getBottomPopupLoading(boolean dismiss, Context context, int layout, int textView, String text, BottomPopup popup){
        if (popup == null || popup.getText() == null || !popup.getText().equals(text)){
            popup = new BottomPopup.Builder(context)
                    .setContentView(layout)
                    .setString(text, textView)
                    .setAutoDismiss(dismiss)
                    .setGravity(Gravity.BOTTOM)
                    .build();
        }
        popup.show();

        return popup;
    }

    public static String decodeDate(String date, Context context){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        DateTime creationTime = dateTimeFormatter.parseDateTime(date);

        Interval interval = new Interval(creationTime, new Instant());
        Duration duration = interval.toDuration();
        String output = "";
        if (duration.getStandardDays() >= 356){
            long years = duration.getStandardDays()/356;
            if (years == 1){
                output += context.getResources().getString(R.string.year_one);
            }
            else if (years < 5){
                output += years + " ";
                output += context.getResources().getString(R.string.year_two);
            }
            else{
                output += years + " ";
                output += context.getResources().getString(R.string.year_five);
            }
        }
        else if (duration.getStandardDays() >= 31){
            long months = duration.getStandardDays() / 31;
            if (months == 1){
                output += context.getResources().getString(R.string.month_one);
            }
            else if (months < 5){
                output += months + " ";
                output += context.getResources().getString(R.string.month_two);
            }
            else{
                output += months + " ";
                output += context.getResources().getString(R.string.month_five);
            }
        }
        else if (duration.getStandardDays() >= 7){
            long weeks = duration.getStandardDays() / 7;
            if (weeks == 1){
                output += context.getResources().getString(R.string.week_one);
            }
            else if (weeks < 5){
                output += weeks + " ";
                output += context.getResources().getString(R.string.week_two);
            }
            else{
                output += weeks + " ";
                output += context.getResources().getString(R.string.week_five);
            }
        }
        else if (duration.getStandardDays() >= 1){
            long days = duration.getStandardDays();
            if (days == 1){
                output += context.getResources().getString(R.string.day_one);
            }
            else{
                output += days + " ";
                output += context.getResources().getString(R.string.day_two);
            }
        }
        else if (duration.getStandardHours() >= 1){
            long hours = duration.getStandardHours();
            if (hours == 1){
                output += context.getResources().getString(R.string.hour_one);
            }
            else if (hours < 5){
                output += hours + " ";
                output += context.getResources().getString(R.string.hour_two);
            }
            else{
                output += hours + " ";
                output += context.getResources().getString(R.string.hour_five);
            }
        }
        else if (duration.getStandardMinutes() >= 1){
            long minutes = duration.getStandardMinutes();
            if (minutes == 1){
                output += context.getResources().getString(R.string.minute_one);
            }
            else if (minutes < 5){
                output += minutes + " ";
                output += context.getResources().getString(R.string.minute_two);
            }
            else{
                output += minutes + " ";
                output += context.getResources().getString(R.string.minute_five);
            }
        }
        else{
            output += context.getResources().getString(R.string.second);
        }
        return (output + " temu");
    }

    public static void share(Bitmap bitmap, Activity activity) {
        String fileName = "share.png";
        File dir = new File(activity.getCacheDir(), "images");
        dir.mkdirs();
        File file = new File(dir, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName()+".provider", file);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        intent.setDataAndType(uri, activity.getContentResolver().getType(uri));
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        //intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            activity.startActivity(Intent.createChooser(intent, "Share Image"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap getBitmapNote(Activity activity, Note note){
        final View view = LayoutInflater.from(activity).inflate(R.layout.screenshoot_note, (ViewGroup) activity.findViewById(R.id.ide), false);
        setNoteDetails(view, note);

        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        view.layout(0, 0, rootView.getWidth(), rootView.getHeight());

        int width = view.getWidth();
        int height = view.getHeight();

        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED);

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight);
        //view.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(width, view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c);

        return b;
    }

    public static Bitmap getBitmapComment(Activity activity, Note note, Comment comment){
        final View view = LayoutInflater.from(activity).inflate(R.layout.screenshoot_comment, (ViewGroup) activity.findViewById(R.id.ide), false);

        setNoteDetails(view, note);
        ((TextView)(view.findViewById(R.id.note_details_comments_number))).setText(String.valueOf(note.getComments()));

        RelativeLayout commentMain = ((RelativeLayout)view.findViewById(R.id.comment_main));
        setCommentDetails(commentMain, comment, activity);

        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        view.layout(0, 0, rootView.getWidth(), rootView.getHeight());

        int width = view.getWidth();
        int height = view.getHeight();

        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED);

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight);
        //view.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(width, view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c);

        return b;
    }

    public static Bitmap getBitmapReplay(Activity activity, Note note, Comment comment, Comment replay){
        final View view = LayoutInflater.from(activity).inflate(R.layout.screenshoot_replay, (ViewGroup) activity.findViewById(R.id.ide), false);

        setNoteDetails(view, note);
        ((TextView)(view.findViewById(R.id.note_details_comments_number))).setText(String.valueOf(note.getComments()));

        RelativeLayout commentMain = ((RelativeLayout)view.findViewById(R.id.comment_main));
        setCommentDetails(commentMain, comment, activity);

        RelativeLayout commentReplay = ((RelativeLayout)view.findViewById(R.id.comment_replay));
        setReplayDetails(commentReplay, replay, activity);



        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        view.layout(0, 0, rootView.getWidth(), rootView.getHeight());

        int width = view.getWidth();
        int height = view.getHeight();

        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED);

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight);
        //view.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(width, view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c);

        return b;
    }

    private static void setNoteDetails(View view, Note note){
        ((TextView)(view.findViewById(R.id.note_details_content))).setText(note.getContent());
        ((TextView)(view.findViewById(R.id.note_details_hashes))).setText(prepareHasheshText(note.getHashesh()));

        ((TextView)view.findViewById(R.id.note_details_user)).setText(note.getUsername());
        ((TextView)view.findViewById(R.id.note_details_date)).setText(Utilities.decodeDate(note.getDate(), view.getContext()));
        ((TextView)view.findViewById(R.id.note_details_like_number)).setText(String.valueOf(note.getLikes()));
    }

    private static void setCommentDetails(RelativeLayout layout, Comment comment, Activity activity){
        ((TextView)(layout.findViewById(R.id.comment_username))).setText(comment.getUsername());
        ((TextView)(layout.findViewById(R.id.comment_date))).setText(decodeDate(comment.getDate(), activity));
        ((TextView)(layout.findViewById(R.id.comment_content))).setText(comment.getContent());

        ((TextView)(layout.findViewById(R.id.comment_like_number))).setText(String.valueOf(comment.getLikes()));
        ((TextView)(layout.findViewById(R.id.comment_item_replays))).setText(String.valueOf(comment.getNoOfReplays()));
    }

    private static void setReplayDetails(RelativeLayout layout, Comment comment, Activity activity){
        ((TextView)(layout.findViewById(R.id.comment_username))).setText(comment.getUsername());
        ((TextView)(layout.findViewById(R.id.comment_date))).setText(decodeDate(comment.getDate(), activity));
        ((TextView)(layout.findViewById(R.id.comment_content))).setText(comment.getContent());

        ((TextView)(layout.findViewById(R.id.comment_like_number))).setText(String.valueOf(comment.getLikes()));
    }

    public static String prepareHasheshText(String string){
        return string;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static String prepareHashesText(List<String> list){
        StringBuilder buffer = new StringBuilder();
        for (String temp: list) {
            if (temp.startsWith("#")){
                temp = temp + " ";
            }
            else{
                temp = "#" + temp + " ";
            }
            buffer.append(temp);
        }
        return buffer.toString();
    }

    public static String getCommentVariation(int noOfComments, Context context){
        String noOfCommentsString = noOfComments + " ";
        if (noOfComments == 0){
            noOfCommentsString += context.getString(R.string.comment_five);
        }
        else if (noOfComments == 1){
            noOfCommentsString += context.getString(R.string.comment_one);
        }
        else if (noOfComments < 5){
            noOfCommentsString += context.getString(R.string.comment_two);
        }
        else{
            noOfCommentsString += context.getString(R.string.comment_five);
        }
        return noOfCommentsString;
    }

}
