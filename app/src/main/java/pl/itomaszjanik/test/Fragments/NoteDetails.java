package pl.itomaszjanik.test.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import pl.itomaszjanik.test.CustomImage;
import pl.itomaszjanik.test.R;

public class NoteDetails extends Fragment {

    private TextView content, hashes, date, rate;

    public NoteDetails() {
    }

    public static NoteDetails newInstance() {
        return new NoteDetails();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        content = (TextView) view.findViewById(R.id.note_details_content);
        hashes = (TextView) view.findViewById(R.id.note_details_hashes);

        ((CustomImage) (view.findViewById(R.id.note_details_icon_back))).init(R.drawable.ic_arrow_black_24dp, R.drawable.ic_arrow_black_24dp);

        Bundle bundle = getArguments();
        if (bundle != null){
            content.setText(bundle.getString("content"));
            hashes.setText(bundle.getString("hashes"));
        }

    }

    public void clickBack(View view){
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.note_details, container, false);
    }
}
