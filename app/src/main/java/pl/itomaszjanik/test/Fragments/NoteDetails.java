package pl.itomaszjanik.test.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;

import java.util.ArrayList;
import java.util.List;

public class NoteDetails extends Fragment {

    private Note note;
    private TextView content, hashes, date, rate;
    private List<Comment> comments;

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

        initListeners(view);


        Bundle bundle = getArguments();
        if (bundle != null){
            note = Parcels.unwrap(bundle.getParcelable("note"));
            if (note == null){
                ArrayList<String> list = new ArrayList<>();
                list.add("TEST");
                list.add("#TEST");
                note = new Note("TEST", "TEST", list, 0);
            }
            content.setText(note.getContent());
            hashes.setText(prepareHashesText(note.getHashes()));
            //note = bundle.getSerializable("content");
            //content.setText(bundle.getString("content"));
            //hashes.setText(bundle.getString("hashes"));
        }

        int noOfComments = note.getNoOfComments();
        String noOfCommentsString = noOfComments + " ";
        if (noOfComments == 0){
            noOfCommentsString += getResources().getString(R.string.comment_five);
        }
        else if (noOfComments == 1){
            noOfCommentsString += getResources().getString(R.string.comment_one);
        }
        else if (noOfComments < 5){
            noOfCommentsString += getResources().getString(R.string.comment_two);
        }
        else{
            noOfCommentsString += getResources().getString(R.string.comment_five);
        }

        ((TextView)(view.findViewById(R.id.note_details_comments_number))).setText(noOfCommentsString);
        initRecyclerView(view);
    }

    private void initRecyclerView(View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.note_details_comments_recycler_view);
      //  RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        CommentAdapter adapter = new CommentAdapter(note.getComments(), new CommentClickListener() {
            @Override
            public void onItemClick(View v, Comment comment) {

            }
        }, getContext());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.comments_divider, null);
        if (divider != null)
            dividerItemDecoration.setDrawable(divider);

        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private String prepareHashesText(List<String> list){
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

    private void initListeners(View view){
        view.findViewById(R.id.note_details_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.note_details, container, false);
    }
}
