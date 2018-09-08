package pl.itomaszjanik.test.Remote;

import okhttp3.ResponseBody;
import pl.itomaszjanik.test.Comment;
import pl.itomaszjanik.test.Comment02;
import pl.itomaszjanik.test.Note;
import pl.itomaszjanik.test.Note02;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface PostService {

    @POST("select.php")
    @FormUrlEncoded
    Call<List<Note>> getPosts(@Field("user_id")  int userID);

    @POST("insert.php")
    @FormUrlEncoded
    Call<Note> savePost(@Field("username")  String username,
                        @Field("date")      String date,
                        @Field("content")   String content,
                        @Field("hashesh")   String hashesh,
                        @Field("comment")   int comment
                        );

    @POST("update_post.php")
    @FormUrlEncoded
    Call<Note> updatePost(@Field("post_id")  int postID,
                                  @Field("user_id")  int userID
    );

    @POST("like.php")
    @FormUrlEncoded
    Call<ResponseBody> likePost(@Field("post_id")  int postID,
                                @Field("user_id")  int userID
    );

    @POST("unlike.php")
    @FormUrlEncoded
    Call<ResponseBody> unlikePost(@Field("post_id")  int postID,
                            @Field("user_id")  int userID
    );

    @POST("comment_post.php")
    @FormUrlEncoded
    Call<ResponseBody> commentPost(@Field("post_id")  int postID,
                                   @Field("user_id")  int userID,
                                   @Field("username") String username,
                                   @Field("date")     String date,
                                   @Field("content")  String content
    );

    @GET("POSTS/{postID}/COMMENTS/0.json")
    Call<List<Comment>> getCommentsPost(@Path("postID") String postID);

}
