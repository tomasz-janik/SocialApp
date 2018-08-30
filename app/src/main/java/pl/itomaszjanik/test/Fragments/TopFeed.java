package pl.itomaszjanik.test.Fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.parceler.Parcels;
import pl.itomaszjanik.test.*;
import pl.itomaszjanik.test.ExtendedComponents.TextViewClickable;
import pl.itomaszjanik.test.Note;
import pl.itomaszjanik.test.Posts.NoteAdapter;
import pl.itomaszjanik.test.Posts.NoteClickListener;

import java.util.ArrayList;
import java.util.List;

public class TopFeed extends Fragment {

    private TextViewClickable currentClicked;

    public TopFeed(){
    }

    public static TopFeed newInstance() {
        return new TopFeed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feed_top,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_top);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new NoteAdapter(createPosts(), new NoteClickListener() {
            @Override
            public void onItemClick(View v, Note note) {
                Bundle data = new Bundle();
                data.putParcelable("note", Parcels.wrap(note));

                Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, getContext()));


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        Drawable divider = ResourcesCompat.getDrawable(getResources(), R.drawable.item_separator, null);
        if (divider != null)
            dividerItemDecoration.setDrawable(divider);

        //recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), R.drawable.item_separator));
        recyclerView.addItemDecoration(dividerItemDecoration);

        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));

        recyclerView.addOnScrollListener(new ListScrollTopListener((NavigationController) getActivity().findViewById(R.id.navigation_feed_top)));
        recyclerView.addOnScrollListener(new ListScrollBottomListener((NavigationController) getActivity().findViewById(R.id.navigation_bottom)));

        ((TextViewClickable) (view.findViewById(R.id.top_daily_text))).changeState();
        currentClicked = (TextViewClickable) view.findViewById(R.id.top_daily_text);
        initListeners(view, R.id.top_daily);
        initListeners(view, R.id.top_weekly);
        initListeners(view, R.id.top_monthly);
        initListeners(view, R.id.top_alltime);
        initListeners(view, R.id.top_commented);
    }


    private void initListeners(final View view, final int layout){
        view.findViewById(layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextViewClickable textView = getTextView(getValue(layout), view);
                if (currentClicked != textView){
                    currentClicked.changeState();
                    textView.changeState();
                    currentClicked = textView;
                }
            }
        });
    }

    private int getValue(int layout){
        switch (layout){
            case R.id.top_daily:
                return Values.DAILY;
            case R.id.top_weekly:
                return Values.WEEKLY;
            case R.id.top_monthly:
                return Values.MONTHLY;
            case R.id.top_alltime:
                return Values.ALLTIME;
            case R.id.top_commented:
                return Values.COMMENTED;
            default:
                return Values.DAILY;
        }
    }

    private TextViewClickable getTextView(int i, View view){
        switch (i){
            case Values.DAILY:
                return view.findViewById(R.id.top_daily_text);
            case Values.WEEKLY:
                return view.findViewById(R.id.top_weekly_text);
            case Values.MONTHLY:
                return view.findViewById(R.id.top_monthly_text);
            case Values.ALLTIME:
                return view.findViewById(R.id.top_alltime_text);
            case Values.COMMENTED:
                return view.findViewById(R.id.top_commented_text);
            default:
                return view.findViewById(R.id.top_daily_text);
        }
    }

    private List<Note> createPosts(){
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
        tags03.add("UJ");

        List<String> tags04 = new ArrayList<>();
        tags04.add("polska");
        tags04.add("kościół");

        list.add(new Note("Ja pierdole XD 18 osób przede mną w kolejce do biedry. Za co XD ","moj stary", tags01,10));
        list.add(new Note("Dlaczego w każdym sklepie w kraju ludzie robią normalne zakupy a w Biedrze jakby jutro ruscy mieli wbić z krymu? xD Come on 20 kilo cukru kurwa? XD", "ty",tags04,89));
        list.add(new Note("Stoisz i czekasz na tramwaj. Chcesz kupic bilet na przystanku i 10 osob kolejki. W tramwaju 5 xD Walić kupie ten miesięczny", "on", tags02,666));
        list.add(new Note("kokaina","oe", tags03,123));
        list.add(new Note("Dobra kurwa XD Miałem jechać autobusem 20 minut do domu i chcialem kupic bilet w autobusie. Czekałem na to jakies 16 minut i podbił do mnie kanar. Pomijając rozmowe powiedział mi, że i tak dostane mandat bo jebie go to ze nie zdazylem przez kolejke kupic biletów. Ja pierdole", "ja", tags04,111));
        list.add(new Note("Jak mnie ten kraj wkurwia XD","oe", tags03, 120));
        list.add(new Note("Dlaczego zawsze na ulicy jakaś grupka patusów musi puszczać te upośledzone rapsy na głośnikach?","oe", tags02, 1111));
        list.add(new Note("Niedługo podniosę deskę w kiblu i zobacze reklame MJUZIKLI","oe", tags01, 555));
        list.add(new Note("Mieszkanie na studiach ssie XD Mogłam się siąść xD “Będe sam gotowała” ehe ","oe", tags04, 0));
        list.add(new Note("Kiedy kupujesz kebsa za 17 zł z 20 jakie masz i po dwóch gryzach znajdujesz w nim robaka","oe", tags02, 12));
        list.add(new Note("Co wróce do domu to starzy robią 16 okrążenie po ranczu. Ileż kurwa można to puszcać. Na pamięć mam już kwestie solejuka hadziuka i innych pierdół","oe", tags01, 94));
        list.add(new Note("Dlaczego moja loszka cały czas truje mi dupe? Like czasami zdaje mi sie, że odbija karte na zakładzie XD","oe", tags03, 55));
        list.add(new Note("Pozdrowienia dla ludzi dających coś do relacji na fb :))) Żal","oe", tags04, 8));
        list.add(new Note("Grill rodzinny a pół rodziny to janusze. KURŁA XD bez jaj ostatnie 40 minut to rozmowa jak w oplu szwagra naprawić rozrząd czy jak to gówno sie tam nazywa XD","oe", tags01, 2));
        list.add(new Note("Dobra odkąd wprowadzili 500+ w moim mieście patola wzrosła tak ze dwa razy XD Tyle karyn z wózkami co widze codziennie wracając ze szkoły to dawniej przez cały tydzień nie widziałem. Jak to wycofają to chyba patusy zaczną palić sklepy XD","oe", tags04, 33));
        list.add(new Note("Ludzie są zjebani. Powiedziałem mojemu dalszemu znajomemu który ma kapele metalową, że moge im nakrecic teledysk (troszke sie bawie w nagrywanie i montowanie i lubie to) do ich pierwszej piosenki jeśli chcą za darmo. Wszyscy szczęśliwi. Spóźniłem sie godzinę z winy korków na nagrywanie i wszyscy mnie opierdolili z nich, że jestem nie słowny. Niby im to nagrałem ale teraz mam takie, że żałuje ze nie powiedzialem im zeby spierdalali","oe", tags03, 55));
        list.add(new Note("Gardze ludźmi z Woodstocku. Troche godności a nie babrania się w błocie. Oczywiście gdy powiesz typom METALOM włosy ala POCAHONTAS że to nie twoje klimaty to usłyszysz, że sztywniak itp. Przecież festiwale typu Impact czy Kraków Live tooo nieee bo ludzie tam są czyści i nie srają na trawe do okoła","oe", tags02, 12));
        list.add(new Note("Wkurwia mnie to, że auto rodziców stoi 6 dni w tygodniu w domu ale gdy chce je pożyczyc zeby przejechać sie 10km dalej to nie bo będzie potrzebne. #kurwamadź","oe", tags01, 18));
        list.add(new Note("Drugi dzień po kupnie Iphona X i pomyślałem że kupie sobie twardą szybke bo bym umarł gdybym odrazu to rozwalił. Wbiłem do serwisu i w momencie przekazywania typowi telefonu wypadł mi z ręki i jebnął o ziemie. Podnosze i pajęczyna . Ja zrobilem sie blady a typo złapał sie za łeb. To mogło być bardziej pechowe?","oe", tags03, 3));
        list.add(new Note("Mam dość mojego chłopaka","oe", tags01, 99));
        list.add(new Note("Czwartą noc z rzędu tuż przed zasnięciem swędzi mnie najmniejszy palec u nogi i wybudza. Ty mały skurwysynie xD","oe", tags04, 100));

        return list;
    }
}
