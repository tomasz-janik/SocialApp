package pl.itomaszjanik.test;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
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
import pl.itomaszjanik.test.Comments.*;
import pl.itomaszjanik.test.Posts.GetPostsCallback;
import pl.itomaszjanik.test.Posts.ReactNoteCallback;
import pl.itomaszjanik.test.Posts.UpdatePostCallback;
import pl.itomaszjanik.test.Remote.*;
import pl.itomaszjanik.test.Replays.GetReplaysCallback;
import pl.itomaszjanik.test.Replays.ReactReplayCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Utilities {

    public static void onLikeNoteClick(int userID, Context context, ReactNoteCallback callback, View view, Note note){
        if (isNetworkAvailable(context)){
            Utilities.reactNoteCall(userID, callback, view, note);
        }
        else{
            callback.reactNoteNoInternet();
        }
    }

    private static void reactNoteCall(int userID, final ReactNoteCallback callback, final View view, final Note note){
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        Call<ResponseBody> call;
        if (note.getLiked()){
            call = service.unlikePost(note.getId(), userID);
        }
        else{
            call = service.likePost(note.getId(), userID);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@Nullable Call<ResponseBody> call, @Nullable Response<ResponseBody> response) {
                if (note.getLiked()){
                    callback.reactNoteUnlikeSucceeded(note, view);
                }
                else{
                    callback.reactNoteLikeSucceeded(note, view);
                }
            }

            @Override
            public void onFailure(@Nullable Call<ResponseBody> call, @Nullable Throwable t) {
                if (note.getLiked()){
                    callback.reactNoteUnlikeFailed();
                }
                else{
                    callback.reactNoteLikeFailed();
                }
            }
        });
    }


    public static void onLikeCommentClick(Context context, ReactCommentsCallback callback, View view, Comment comment){
        if (isNetworkAvailable(context)){
            Utilities.reactCommentCall(callback, view, comment);
        }
        else{
            callback.reactCommentNoInternet();
        }
    }

    private static void reactCommentCall(final ReactCommentsCallback callback, final View view, final Comment comment){
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        Call<ResponseBody> call;
        if (comment.getLiked()){
            call = service.unlikeComment(comment.getCommentID(), 1);
        }
        else{
            call = service.likeComment(comment.getCommentID(), 1);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@Nullable Call<ResponseBody> call, @Nullable Response<ResponseBody> response) {
                if (comment.getLiked()){
                    callback.reactCommentUnlikeSucceeded(comment, view);
                }
                else{
                    callback.reactCommentLikeSucceeded(comment, view);
                }
            }

            @Override
            public void onFailure(@Nullable Call<ResponseBody> call, @Nullable Throwable t) {
                if (comment.getLiked()){
                    callback.reactCommentUnlikeFailed();
                }
                else{
                    callback.reactCommentLikeFailed();
                }
            }
        });
    }

    public static void commentPost(int postID, int userID, String username, String date, String content,
                                   final CommentPostCallback callback, Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.commentPost(postID, userID, username, date, content).enqueue(new Callback<Comment>() {
                @Override
                public void onResponse(@Nullable Call<Comment> call, @Nullable Response<Comment> response) {
                    if (response != null){
                        if (response.isSuccessful()){
                            callback.commentPostSucceeded(response.body());
                        }
                        else{
                            callback.commentPostFailed();
                        }
                    }
                    else{
                        callback.commentPostFailed();
                    }
                }

                @Override
                public void onFailure(@Nullable Call<Comment> call, @Nullable Throwable t) {
                    callback.commentPostFailed();
                }
            });
        }
        else{
            callback.commentPostNoInternet();
        }
    }

    public static void getPosts(int userID, int page, final GetPostsCallback callback, Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.getPosts(userID, page).enqueue(new Callback<List<Note>>() {
                @Override
                public void onResponse(@Nullable Call<List<Note>> call, @Nullable Response<List<Note>> response) {
                    if (response != null && response.isSuccessful()){
                        if (response.body() != null){
                            callback.getPostSucceeded(response.body());
                            return;
                        }
                    }
                    callback.getPostFailed();
                }

                @Override
                public void onFailure(@Nullable Call<List<Note>> call, @Nullable Throwable t) {
                    callback.getPostFailed();
                }
            });
        }
        else{
            callback.getPostNoInternet();
        }
    }

    public static void getPostsTop(int userID, int page, String type, final GetPostsCallback callback, Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.getPostsTop(type, userID, page).enqueue(new Callback<List<Note>>() {
                @Override
                public void onResponse(@Nullable Call<List<Note>> call, @Nullable Response<List<Note>> response) {
                    if (response != null && response.isSuccessful()){
                        if (response.body() != null){
                            callback.getPostSucceeded(response.body());
                            return;
                        }
                    }
                    callback.getPostFailed();
                }

                @Override
                public void onFailure(@Nullable Call<List<Note>> call, @Nullable Throwable t) {
                    callback.getPostFailed();
                }
            });
        }
        else{
            callback.getPostNoInternet();
        }
    }

    public static void getPostsSearch(int userID, int page, String search, final GetPostsCallback callback, Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.getPostsSearch(userID, page, search).enqueue(new Callback<List<Note>>() {
                @Override
                public void onResponse(@Nullable Call<List<Note>> call, @Nullable Response<List<Note>> response) {
                    if (response != null && response.isSuccessful()){
                        if (response.body() != null){
                            callback.getPostSucceeded(response.body());
                            return;
                        }
                    }
                    callback.getPostFailed();
                }

                @Override
                public void onFailure(@Nullable Call<List<Note>> call, @Nullable Throwable t) {
                    callback.getPostFailed();
                }
            });
        }
        else{
            callback.getPostNoInternet();
        }
    }

    public static void getPostsProfile(int userID, String username, int page, final GetPostsCallback callback, Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.getPostsProfile(userID, username, page).enqueue(new Callback<List<Note>>() {
                @Override
                public void onResponse(@Nullable Call<List<Note>> call, @Nullable Response<List<Note>> response) {
                    if (response != null && response.isSuccessful()){
                        if (response.body() != null){
                            callback.getPostSucceeded(response.body());
                            return;
                        }
                    }
                    callback.getPostFailed();
                }

                @Override
                public void onFailure(@Nullable Call<List<Note>> call, @Nullable Throwable t) {
                    callback.getPostFailed();
                }
            });
        }
        else{
            callback.getPostNoInternet();
        }
    }

    public static void updatePost(int userID, final UpdatePostCallback callback, Note note){
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        Call<Note> call = service.updatePost(note.getId(), userID);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@Nullable Call<Note> call, @Nullable Response<Note> response) {
                if (response != null){
                    callback.updatePostSucceeded(response.body());
                }
                else{
                    callback.updatePostFailed();
                }

            }

            @Override
            public void onFailure(@Nullable Call<Note> call, @Nullable Throwable t) {
                callback.updatePostFailed();
            }
        });
    }

    public static void getComments(int userID, int postID, int page, final GetCommentsCallback callback, Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.getCommentsPost(userID, postID, page).enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(@Nullable Call<List<Comment>> call, @Nullable Response<List<Comment>> response) {
                    if (response != null && response.isSuccessful()){
                        if (response.body() != null){
                            callback.getCommentSucceeded(response.body());
                        }
                        else{
                            callback.getCommentFailed();
                        }
                    }
                    else{
                        callback.getCommentFailed();
                    }
                }

                @Override
                public void onFailure(@Nullable Call<List<Comment>> call, @Nullable Throwable t) {
                    callback.getCommentFailed();
                }
            });
        }
        else{
            callback.getCommentNoInternet();
        }
    }

    public static void updateCommentCall(final UpdateCommentCallback callback, Comment comment){
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        Call<Comment> call = service.updateComment(comment.getCommentID(), 1);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(@Nullable Call<Comment> call, @Nullable Response<Comment> response) {
                if (response != null){
                    callback.updateCommentSucceeded(response.body());
                }
                else{
                    callback.updateCommentFailed();
                }
            }

            @Override
            public void onFailure(@Nullable Call<Comment> call, @Nullable Throwable t) {
                callback.updateCommentFailed();
            }
        });
    }

    public static void getReplays(int userID, int commentID, int page, final GetReplaysCallback callback, Context context) {
        if (isNetworkAvailable(context)) {
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.getReplaysComment(userID, commentID, page).enqueue(new Callback<List<Replay>>() {
                @Override
                public void onResponse(@Nullable Call<List<Replay>> call, @Nullable Response<List<Replay>> response) {
                    if (response != null && response.isSuccessful()) {
                        if (response.body() != null) {
                            callback.getReplaySucceeded(response.body());
                        } else {
                            callback.getReplayFailed();
                        }
                    } else {
                        callback.getReplayFailed();
                    }
                }

                @Override
                public void onFailure(@Nullable Call<List<Replay>> call, @Nullable Throwable t) {
                    callback.getReplayFailed();
                }
            });
        } else {
            callback.getReplayNoInternet();
        }
    }

    public static void replayComment(int commentID, int userID, String username, String date, String content,
                                     final ReplayCommentCallback callback, Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.replayComment(commentID, userID, username, date, content).enqueue(new Callback<Replay>() {
                @Override
                public void onResponse(@Nullable Call<Replay> call, @Nullable Response<Replay> response) {
                    if (response != null && response.isSuccessful()){
                        callback.replayCommentSucceeded(response.body());
                    }
                    else{
                        callback.replayCommentFailed();
                    }
                }

                @Override
                public void onFailure(@Nullable Call<Replay> call, @Nullable Throwable t) {
                    callback.replayCommentFailed();
                }
            });
        }
        else{
            callback.replayCommentNoInternet();
        }
    }

    public static void onLikeReplayClick(Context context, ReactReplayCallback callback, View view, Replay replay){
        if (isNetworkAvailable(context)){
            Utilities.reactReplayCall(callback, view, replay);
        }
        else{
            callback.reactReplayNoInternet();
        }
    }

    private static void reactReplayCall(final ReactReplayCallback callback, final View view, final Replay replay){
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        Call<ResponseBody> call;
        if (replay.getLiked()){
            call = service.unlikeReplay(replay.getReplayID(), 1);
        }
        else{
            call = service.likeReplay(replay.getReplayID(), 1);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@Nullable Call<ResponseBody> call, @Nullable Response<ResponseBody> response) {
                if (replay.getLiked()){
                    callback.reactReplayUnlikeSucceeded(replay, view);
                }
                else{
                    callback.reactReplayLikeSucceeded(replay, view);
                }
            }

            @Override
            public void onFailure(@Nullable Call<ResponseBody> call, @Nullable Throwable t) {
                if (replay.getLiked()){
                    callback.reactReplayUnlikeFailed();
                }
                else{
                    callback.reactReplayLikeFailed();
                }
            }
        });
    }

    public static void register(final String username, String password, final RegisterCallback registerCallback, final Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.register(username, password).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @Nullable Response<ResponseBody> response) {
                    if (response != null && response.body() != null && response.isSuccessful()){
                        String output;
                        try{
                            output = response.body().string();
                        }
                        catch (Exception e){
                            output = "";
                        }

                        if (output.startsWith("success")){
                            int userID = Integer.valueOf(output.substring(7));
                            registerCallback.onRegisterSucceeded(username, userID);
                        }
                        else if (output.equals("not unique")){
                            registerCallback.onRegisterNotUnique();
                        }
                        else{
                            registerCallback.onRegisterFailed();
                        }
                    }
                    else{
                        registerCallback.onRegisterFailed();
                    }
                }

                @Override
                public void onFailure(@Nullable Call<ResponseBody> call, @Nullable Throwable t) {
                    registerCallback.onRegisterFailed();
                }
            });
        }
        else{
            registerCallback.onRegisterNoInternet();
        }
    }

    public static void login(final String username, String password, final LoginCallback loginCallback, final Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.login(username, password).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @Nullable Response<ResponseBody> response) {
                    if (response != null && response.body() != null && response.isSuccessful()){
                        String output;
                        try{
                            output = response.body().string();
                        }
                        catch (Exception e){
                            output = "";
                        }

                        if (output.startsWith("success")){
                            int userID = Integer.valueOf(output.substring(7));
                            loginCallback.onLoginSucceeded(username, userID);
                        }
                        else if (output.equals("error")){
                            loginCallback.onLoginWrongPassword();
                        }
                        else{
                            loginCallback.onLoginFailed();
                        }
                    }
                    else{
                        loginCallback.onLoginFailed();
                    }
                }

                @Override
                public void onFailure(@Nullable Call<ResponseBody> call, @Nullable Throwable t) {
                    loginCallback.onLoginFailed();
                }
            });
        }
        else{
            loginCallback.onLoginNoInternet();
        }
    }

    public static void generateID(final int task, final GenerateIDCallback generateIDCallback, final Context context){
        if (isNetworkAvailable(context)){
            PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
            service.generateID().enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@Nullable Call<ResponseBody> call, @Nullable Response<ResponseBody> response) {
                    if (response != null && response.body() != null && response.isSuccessful()){
                        String output;
                        try{
                            output = response.body().string();
                        }
                        catch (Exception e){
                            output = "";
                        }

                        if (output.startsWith("success")){
                            int userID = Integer.valueOf(output.substring(7));
                            generateIDCallback.onGenerateSuccess(output.substring(0, 6), userID, task);
                        }
                        else if (output.equals("error")){
                            generateIDCallback.onGenerateFailed(task);
                        }
                        else{
                            generateIDCallback.onGenerateFailed(task);
                        }
                    }
                    else{
                        generateIDCallback.onGenerateFailed(task);
                    }
                }

                @Override
                public void onFailure(@Nullable Call<ResponseBody> call, @Nullable Throwable t) {
                    generateIDCallback.onGenerateFailed(task);
                }
            });
        }
        else{
            generateIDCallback.onGenerateNoInternet(task);
        }
    }

    public static int checkComment(String string){
        if (string == null || string.equals("")){
            return Values.COMMENT_EMPTY;
        }
        if (string.length() > Values.COMMENT_LIMIT){
            return Values.COMMENT_TOO_LONG;
        }
        return 1;
    }

    public static BottomPopup errorComment(int error, Context context, BottomPopup popup){
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
        View view = activity.getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
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

    public static String decodeDate(String date, Context context){
        String output = "";

        try{
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            DateTime creationTime = dateTimeFormatter.parseDateTime(date);

            Interval interval = new Interval(creationTime, new Instant());
            Duration duration = interval.toDuration();
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
        }
        catch (Exception e){
            output += context.getString(R.string.second);
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

        RelativeLayout commentMain = view.findViewById(R.id.comment_main);
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

    public static Bitmap getBitmapReplay(Activity activity, Note note, Comment comment, Replay replay){
        final View view = LayoutInflater.from(activity).inflate(R.layout.screenshoot_replay, (ViewGroup) activity.findViewById(R.id.ide), false);

        setNoteDetails(view, note);
        ((TextView)(view.findViewById(R.id.note_details_comments_number))).setText(String.valueOf(note.getComments()));

        RelativeLayout commentMain = view.findViewById(R.id.comment_main);
        setCommentDetails(commentMain, comment, activity);

        RelativeLayout commentReplay = view.findViewById(R.id.comment_replay);
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
        ((TextView)(layout.findViewById(R.id.comment_item_replays))).setText(String.valueOf(comment.getReplays()));
    }

    private static void setReplayDetails(RelativeLayout layout, Replay comment, Activity activity){
        ((TextView)(layout.findViewById(R.id.comment_username))).setText(comment.getUsername());
        ((TextView)(layout.findViewById(R.id.comment_date))).setText(decodeDate(comment.getDate(), activity));
        ((TextView)(layout.findViewById(R.id.comment_content))).setText(comment.getContent());

        ((TextView)(layout.findViewById(R.id.comment_like_number))).setText(String.valueOf(comment.getLikes()));
    }

    public static String prepareHasheshText(String string){
        return string;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
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

    public static String getReplaysVariation(int noOfReplays, Context context){
        String noOfCommentsString = noOfReplays + " ";
        if (noOfReplays == 1){
            noOfCommentsString += context.getResources().getString(R.string.replay_one);
        }
        else{
            noOfCommentsString += context.getResources().getString(R.string.replay_two);
        }
        return noOfCommentsString;
    }

}
