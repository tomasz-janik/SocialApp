package pl.itomaszjanik.test.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.ResponseBody;
import org.parceler.Parcels;
import org.w3c.dom.Text;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.Posts.NoteAdapter;
import pl.itomaszjanik.test.Posts.NoteClickListener;
import pl.itomaszjanik.test.Remote.FailedCallback;
import pl.itomaszjanik.test.Remote.NoteService;
import pl.itomaszjanik.test.Remote.PostService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ItemFeed extends Fragment implements SwipeRefreshLayout.OnRefreshListener, FailedCallback {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Note> posts = new ArrayList<>();

    private Note currentNote;
    private View currentView;

    private RecyclerView recyclerView;
    private RelativeLayout refreshLayout;
    private BottomPopup bottomPopup;
    private SharedPreferences preferences;

    public ItemFeed(){
    }

    public static ItemFeed newInstance() {
        return new ItemFeed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.feed, container,false);

        refreshLayout = (RelativeLayout) rootView.findViewById(R.id.feed_refresh_layout);
        refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeRefreshLayout.setRefreshing(true);
                createPosts();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setProgressViewOffset(false,
                getResources().getDimensionPixelSize(R.dimen.refresher_offset),
                getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);
                createPosts();
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.item_separator, null);
        if (divider != null)
            dividerItemDecoration.setDrawable(divider);

        //recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), R.drawable.item_separator));
        recyclerView.addItemDecoration(dividerItemDecoration);

        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));

        recyclerView.addOnScrollListener(new ListScrollBottomListener((NavigationController) getActivity().findViewById(R.id.navigation_bottom)));

        createPosts();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (currentNote != null && currentView != null){
            boolean current = currentNote.getLiked();
            if (preferences.getBoolean("currentNote", true) != current){
                int newLikes;
                if (current){
                    TextView likes_number = currentView.findViewById(R.id.note_like_number);
                    newLikes = Integer.valueOf(likes_number.getText().toString()) - 1;
                    likes_number.setText(String.valueOf(newLikes));
                    ((TextView)(currentView.findViewById(R.id.note_like_text))).setTextColor(Color.parseColor("#747474"));
                }
                else{
                    TextView likes_number = currentView.findViewById(R.id.note_like_number);
                    newLikes = Integer.valueOf(likes_number.getText().toString()) + 1;
                    likes_number.setText(String.valueOf(newLikes));
                    ((TextView)(currentView.findViewById(R.id.note_like_text))).setTextColor(Color.BLUE);
                }
                currentNote.setLiked(!current);
                currentNote.setLikes(newLikes);
            }
        }
    }

    @Override
    public void onRefresh() {
        createPosts();
    }

    @Override
    public void likeFailed(){
        bottomPopup = Utilities.getBottomPopupText(getContext(),
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_like_post), bottomPopup);
    }

    @Override
    public void unlikeFailed(){
        bottomPopup = Utilities.getBottomPopupText(getContext(),
                R.layout.bottom_popup_text, R.id.bottom_popup_text,
                getString(R.string.couldnt_unlike_post), bottomPopup);
    }

    private void createPosts(){
        if (!Utilities.isNetworkAvailable(getContext())){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.no_internet), bottomPopup);
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        PostService service = RetrofitClient.getClient(Values.URL).create(PostService.class);
        Call<List<Note>> call = service.getPosts(1);
        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                posts = response.body();
                if (bottomPopup != null)
                    bottomPopup.dismiss();
                refreshLayout.setVisibility(View.GONE);
                recyclerAdapter();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                //Toast.makeText(getContext(), ":(", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                bottomPopup = Utilities.getBottomPopupText(getContext(),
                        R.layout.bottom_popup_text, R.id.bottom_popup_text,
                        getString(R.string.couldnt_refresh), bottomPopup);
                if (recyclerView == null){
                    refreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void recyclerAdapter(){
        recyclerView.setAdapter(new NoteAdapter(posts, new NoteClickListener() {
            @Override
            public void onItemClick(View v, Note note) {
                Bundle data = new Bundle();
                data.putParcelable("note", Parcels.wrap(note));
                //NoteAdapter.CustomViewHolder lol =
                currentNote = note;
                currentView = v;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("currentNote", note.getLiked());
                editor.apply();

                Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onLikeClick(View view, Note note){
                Utilities.onLikePostClick(getContext(), ItemFeed.this, note, view,
                        bottomPopup, R.id.note_like_text, R.id.note_like_number);
            }

            @Override
            public void onCommentClick(View v, Note note){
                Bundle data = new Bundle();
                data.putParcelable("note", Parcels.wrap(note));

                Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, getContext()));
    }

    /*private List<Note> createPos2ts(){
        List<Note> list = new ArrayList<>();
        List<String> tags01 = new ArrayList<>();
        tags01.add("kraków");
        tags01.add("mpk");
        tags01.add("chore");
        tags01.add("policja");

        List<String> tags02 = new ArrayList<>();
        tags02.add("warszawa");

        List<String> tags03 = new ArrayList<>();
        tags03.add("#warszawa");
        tags03.add("#facebook");
        tags03.add("#polimorfizm");
        tags03.add("studia");
        tags03.add("UJ_LOL_11233");

        List<String> tags04 = new ArrayList<>();
        tags04.add("polska");
        tags04.add("kościół");

        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment("chuja sie pan zna, prosze pana", "staszek22", "26/08/2018 22:41:00", 2, 5));
        comments.add(new Comment("panie odpierdol sie pan", "misio69", "26/08/2018 22:12:00"));
        comments.add(new Comment("nie rozumiem w czym problem", "heniek33", "25/08/2018 22:41:00"));
        comments.add(new Comment("eskeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeetit", "lilPump", "25/08/2018 22:41:00"));
        comments.add(new Comment("bez nas nie było by bułek w sklepie proszę to docenić", "bug12", "25/08/2018 22:41:00"));
        comments.add(new Comment("proszę pana (specjalnie z małej litery), skończ pan pierdolić", "pomidor", "25/08/2018 22:41:00"));
        comments.add(new Comment("hahahaah super bardzo śmieszne, leci plusik", "adam_guz", "25/08/2018 22:41:00"));
        comments.add(new Comment("było już, na razie leci tylko warn, ale jeszcze raz i ban", "wpierdalator", "25/08/2018 22:41:00"));


        list.add(new Note("Ja pierdole XD 18 osób przede mną w kolejce do biedry. Za co XD","moj stary", "26/08/2018 22:41:00",tags01,10));
        list.add(new Note("Dlaczego w każdym sklepie w kraju ludzie robią normalne zakupy a w Biedrze jakby jutro ruscy mieli wbić z krymu? xD Come on 20 kilo cukru kurwa? XD", "ty", "26/08/2018 22:41:00", tags04, comments, 89));
        list.add(new Note("Stoisz i czekasz na tramwaj. Chcesz kupic bilet na przystanku i 10 osob kolejki. W tramwaju 5 xD Walić kupie ten miesięczny", "on", "26/08/2018 22:41:00", tags02,666));
        list.add(new Note("kokaina","randomek33", "26/08/2018 22:41:00", tags03,123));
        list.add(new Note("Dobra kurwa XD Miałem jechać autobusem 20 minut do domu i chcialem kupic bilet w autobusie. Czekałem na to jakies 16 minut i podbił do mnie kanar. Pomijając rozmowe powiedział mi, że i tak dostane mandat bo jebie go to ze nie zdazylem przez kolejke kupic biletów. Ja pierdole", "ja", "26/08/2018 22:41:00", tags04,111));
        list.add(new Note("Jak mnie ten kraj wkurwia XD","randomek33", "26/08/2018 22:41:00", tags03, 120));
        list.add(new Note("Dlaczego zawsze na ulicy jakaś grupka patusów musi puszczać te upośledzone rapsy na głośnikach?","randomek33", "26/08/2018 22:41:00", tags02, 1111));
        list.add(new Note("Niedługo podniosę deskę w kiblu i zobacze reklame MJUZIKLI","randomek33", "26/08/2018 22:41:00", tags01, 555));
        list.add(new Note("Mieszkanie na studiach ssie XD Mogłam się siąść xD “Będe sam gotowała” ehe ","randomek33", "26/08/2018 22:41:00", tags04, 0));
        list.add(new Note("Kiedy kupujesz kebsa za 17 zł z 20 jakie masz i po dwóch gryzach znajdujesz w nim robaka","randomek33", "26/08/2018 22:41:00", tags02, 12));
        list.add(new Note("Co wróce do domu to starzy robią 16 okrążenie po ranczu. Ileż kurwa można to puszcać. Na pamięć mam już kwestie solejuka hadziuka i innych pierdół","randomek33", "26/08/2018 22:41:00", tags01, 94));
        list.add(new Note("Dlaczego moja loszka cały czas truje mi dupe? Like czasami zdaje mi sie, że odbija karte na zakładzie XD","randomek33", "26/08/2018 22:41:00", tags03, 55));
        list.add(new Note("Pozdrowienia dla ludzi dających coś do relacji na fb :))) Żal","randomek33", "26/08/2018 22:41:00", tags04, 8));
        list.add(new Note("Grill rodzinny a pół rodziny to janusze. KURŁA XD bez jaj ostatnie 40 minut to rozmowa jak w oplu szwagra naprawić rozrząd czy jak to gówno sie tam nazywa XD","randomek33", "26/08/2018 22:41:00", tags01, 2));
        list.add(new Note("Dobra odkąd wprowadzili 500+ w moim mieście patola wzrosła tak ze dwa razy XD Tyle karyn z wózkami co widze codziennie wracając ze szkoły to dawniej przez cały tydzień nie widziałem. Jak to wycofają to chyba patusy zaczną palić sklepy XD","randomek33", "26/08/2018 22:41:00", tags04, 33));
        list.add(new Note("Ludzie są zjebani. Powiedziałem mojemu dalszemu znajomemu który ma kapele metalową, że moge im nakrecic teledysk (troszke sie bawie w nagrywanie i montowanie i lubie to) do ich pierwszej piosenki jeśli chcą za darmo. Wszyscy szczęśliwi. Spóźniłem sie godzinę z winy korków na nagrywanie i wszyscy mnie opierdolili z nich, że jestem nie słowny. Niby im to nagrałem ale teraz mam takie, że żałuje ze nie powiedzialem im zeby spierdalali","randomek33", "26/08/2018 22:41:00", tags03, 55));
        list.add(new Note("Gardze ludźmi z Woodstocku. Troche godności a nie babrania się w błocie. Oczywiście gdy powiesz typom METALOM włosy ala POCAHONTAS że to nie twoje klimaty to usłyszysz, że sztywniak itp. Przecież festiwale typu Impact czy Kraków Live tooo nieee bo ludzie tam są czyści i nie srają na trawe do okoła","randomek33", "26/08/2018 22:41:00", tags02, 12));
        list.add(new Note("Wkurwia mnie to, że auto rodziców stoi 6 dni w tygodniu w domu ale gdy chce je pożyczyc zeby przejechać sie 10km dalej to nie bo będzie potrzebne. #kurwamadź","randomek33", "26/08/2018 22:41:00", tags01, 18));
        list.add(new Note("Drugi dzień po kupnie Iphona X i pomyślałem że kupie sobie twardą szybke bo bym umarł gdybym odrazu to rozwalił. Wbiłem do serwisu i w momencie przekazywania typowi telefonu wypadł mi z ręki i jebnął o ziemie. Podnosze i pajęczyna . Ja zrobilem sie blady a typo złapał sie za łeb. To mogło być bardziej pechowe?","randomek33", "26/08/2018 22:41:00", tags03, 3));
        list.add(new Note("Mam dość mojego chłopaka","randomek33", "26/08/2018 22:41:00", tags01, 99));
        list.add(new Note("Czwartą noc z rzędu tuż przed zasnięciem swędzi mnie najmniejszy palec u nogi i wybudza. Ty mały skurwysynie xD","randomek33", "26/08/2018 22:41:00", tags04, 100));

        return list;
    }*/
}
