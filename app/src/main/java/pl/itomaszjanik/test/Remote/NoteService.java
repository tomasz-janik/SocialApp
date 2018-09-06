package pl.itomaszjanik.test.Remote;

import pl.itomaszjanik.test.Note;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface NoteService {

    @GET("select.php")
    Call<List<Note>> getNote();
}
