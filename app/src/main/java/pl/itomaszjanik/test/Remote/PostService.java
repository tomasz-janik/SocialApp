package pl.itomaszjanik.test.Remote;

import pl.itomaszjanik.test.Note;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PostService {

    @POST("insert.php")
    @FormUrlEncoded
    Call<Note> savePost(@Field("username")  String username,
                        @Field("date")      String date,
                        @Field("content")   String content,
                        @Field("hashesh")   String hashesh,
                        @Field("comment")   int comment
                        );
}
