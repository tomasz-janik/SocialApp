package pl.itomaszjanik.test.Remote;

import okhttp3.ResponseBody;
import pl.itomaszjanik.test.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface PostService {

    @POST("get_posts.php")
    @FormUrlEncoded
    Call<List<Note>> getPosts(@Field("user_id")  int userID,
                              @Field("page")     int page);

    @POST("{type}")
    @FormUrlEncoded
    Call<List<Note>> getPostsTop(@Path("type")     String type,
                                 @Field("user_id") int userID,
                                 @Field("page")    int page
    );

    @POST("get_posts_search.php")
    @FormUrlEncoded
    Call<List<Note>> getPostsSearch(@Field("user_id") int userID,
                                    @Field("page")    int postID,
                                    @Field("search")  String search
    );

    @POST("get_posts_profile.php")
    @FormUrlEncoded
    Call<List<Note>> getPostsProfile(@Field("user_id")  int userID,
                                     @Field("username") String username,
                                     @Field("page")     int postID
    );

    @POST("get_comments_post.php")
    @FormUrlEncoded
    Call<List<Comment>> getCommentsPost(@Field("user_id") int userID,
                                        @Field("post_id") int postID,
                                        @Field("page")    int page
    );

    @POST("get_replays_comment.php")
    @FormUrlEncoded
    Call<List<Replay>> getReplaysComment(@Field("userID")    int userID,
                                         @Field("commentID") int commentID,
                                         @Field("page")      int page
    );

    @POST("insert.php")
    @FormUrlEncoded
    Call<Note> savePost(@Field("username")  String username,
                        @Field("date")      String date,
                        @Field("content")   String content,
                        @Field("tags")      String tags
                        );

    @POST("update_post.php")
    @FormUrlEncoded
    Call<Note> updatePost(@Field("post_id")  int postID,
                          @Field("user_id")  int userID
    );

    @POST("update_comment.php")
    @FormUrlEncoded
    Call<Comment> updateComment(@Field("comment_id")  int commentID,
                                @Field("user_id")     int userID
    );

    @POST("like_post.php")
    @FormUrlEncoded
    Call<ResponseBody> likePost(@Field("post_id")  int postID,
                                @Field("user_id")  int userID
    );

    @POST("unlike_post.php")
    @FormUrlEncoded
    Call<ResponseBody> unlikePost(@Field("post_id")  int postID,
                                  @Field("user_id")  int userID
    );

    @POST("comment_post.php")
    @FormUrlEncoded
    Call<Comment> commentPost(@Field("post_id")  int postID,
                              @Field("user_id")  int userID,
                              @Field("username") String username,
                              @Field("date")     String date,
                              @Field("content")  String content
    );

    @POST("like_comment.php")
    @FormUrlEncoded
    Call<ResponseBody> likeComment(@Field("comment_id")  int commentID,
                                   @Field("user_id")     int userID
    );

    @POST("unlike_comment.php")
    @FormUrlEncoded
    Call<ResponseBody> unlikeComment(@Field("comment_id")  int commentID,
                                     @Field("user_id")     int userID
    );

    @POST("replay_comment.php")
    @FormUrlEncoded
    Call<Replay> replayComment(@Field("comment_id")  int commentID,
                               @Field("user_id")     int userID,
                               @Field("username")    String username,
                               @Field("date")        String date,
                               @Field("content")     String content
    );

    @POST("like_replay.php")
    @FormUrlEncoded
    Call<ResponseBody> likeReplay(@Field("replay_id")   int replayID,
                                  @Field("user_id")     int userID
    );

    @POST("unlike_replay.php")
    @FormUrlEncoded
    Call<ResponseBody> unlikeReplay(@Field("replay_id")   int replayID,
                                    @Field("user_id")     int userID
    );

    @POST("register.php")
    @FormUrlEncoded
    Call<ResponseBody> register(@Field("username")   String username,
                                @Field("password")   String password
    );

    @POST("login.php")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("username")   String username,
                             @Field("password")   String password
    );

    @POST("generateID.php")
    Call<ResponseBody> generateID();

    @POST("setup.php")
    Call<ResponseBody> init();
}
