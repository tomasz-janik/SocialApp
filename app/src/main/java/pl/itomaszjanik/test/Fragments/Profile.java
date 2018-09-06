package pl.itomaszjanik.test.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.w3c.dom.Text;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.Remote.*;
import pl.itomaszjanik.test.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Profile extends Fragment {

    //NoteService mService;
    TextView textView;
    Button button;

    public Profile() {
    }

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container,false);

        //mService = Utilities.getNoteService();
        ///mService = RetrofitClient.getClient(Values.URL).create(NoteService.class);
        textView = view.findViewById(R.id.text);
        button = view.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteService service = RetrofitClient.getClient(Values.URL).create(NoteService.class);
                Call<List<Note>> call = service.getNote();
                call.enqueue(new Callback<List<Note>>() {
                    @Override
                    public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                        //generateDataList(response.body());

                            textView.setText("" +response.body().get(1).getContent());
                    }

                    @Override
                    public void onFailure(Call<List<Note>> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong...Please try later!\n" + t, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
