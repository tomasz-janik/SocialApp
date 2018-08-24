package pl.itomaszjanik.test.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.itomaszjanik.test.*;

import java.util.ArrayList;
import java.util.List;

public class ItemFeed extends Fragment {

    public ItemFeed(){
    }

    public static ItemFeed newInstance() {
        return new ItemFeed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feed,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new NoteAdapter(createPosts(), new ItemClickListener() {
            @Override
            public void onItemClick(View v, Note note) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                NoteDetails noteDetails = NoteDetails.newInstance();

                Bundle data = new Bundle();
                data.putString("content", note.getContent());
                data.putString("hashes", note.getHashes());
                noteDetails.setArguments(data);

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.activity_material_design, noteDetails,"Note");
                fragmentTransaction.addToBackStack(null).commit();
            }
        }));


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.item_separator, null));
        //recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), R.drawable.item_separator));
        recyclerView.addItemDecoration(dividerItemDecoration);

        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));

        recyclerView.addOnScrollListener(new ListScrollBottomListener((NavigationController) getActivity().findViewById(R.id.navigation_bottom)));
    }

    private List<Note> createPosts(){
        List<Note> list = new ArrayList<Note>();
        list.add(new Note("Ja pierdole XD 18 osób przede mną w kolejce do biedry. Za co XD ","moj stary", "krakow"));
        list.add(new Note("Dlaczego w każdym sklepie w kraju ludzie robią normalne zakupy a w Biedrze jakby jutro ruscy mieli wbić z krymu? xD Come on 20 kilo cukru kurwa? XD", "ty","polska"));
        list.add(new Note("Stoisz i czekasz na tramwaj. Chcesz kupic bilet na przystanku i 10 osob kolejki. W tramwaju 5 xD Walić kupie ten miesięczny", "on", "mpk"));
        list.add(new Note("kokaina","oe", "no lol"));
        list.add(new Note("Dobra kurwa XD Miałem jechać autobusem 20 minut do domu i chcialem kupic bilet w autobusie. Czekałem na to jakies 16 minut i podbił do mnie kanar. Pomijając rozmowe powiedział mi, że i tak dostane mandat bo jebie go to ze nie zdazylem przez kolejke kupic biletów. Ja pierdole", "ja", "jebac to"));
        list.add(new Note("Jak mnie ten kraj wkurwia XD","oe", "policja"));
        list.add(new Note("Dlaczego zawsze na ulicy jakaś grupka patusów musi puszczać te upośledzone rapsy na głośnikach?","oe", "matki"));
        list.add(new Note("Niedługo podniosę deskę w kiblu i zobacze reklame MJUZIKLI","oe", "raperzy"));
        list.add(new Note("Mieszkanie na studiach ssie XD Mogłam się siąść xD “Będe sam gotowała” ehe ","oe", "alkohol"));
        list.add(new Note("Kiedy kupujesz kebsa za 17 zł z 20 jakie masz i po dwóch gryzach znajdujesz w nim robaka","oe", "warszawa"));
        list.add(new Note("Co wróce do domu to starzy robią 16 okrążenie po ranczu. Ileż kurwa można to puszcać. Na pamięć mam już kwestie solejuka hadziuka i innych pierdół","oe", "polityka"));
        list.add(new Note("Dlaczego moja loszka cały czas truje mi dupe? Like czasami zdaje mi sie, że odbija karte na zakładzie XD","oe", "nfz"));
        list.add(new Note("Pozdrowienia dla ludzi dających coś do relacji na fb :))) Żal","oe", "a szkoda ryja strzepic"));
        list.add(new Note("Grill rodzinny a pół rodziny to janusze. KURŁA XD bez jaj ostatnie 40 minut to rozmowa jak w oplu szwagra naprawić rozrząd czy jak to gówno sie tam nazywa XD","oe", "szkola"));
        list.add(new Note("Dobra odkąd wprowadzili 500+ w moim mieście patola wzrosła tak ze dwa razy XD Tyle karyn z wózkami co widze codziennie wracając ze szkoły to dawniej przez cały tydzień nie widziałem. Jak to wycofają to chyba patusy zaczną palić sklepy XD","oe", "morawiecki"));
        list.add(new Note("Ludzie są zjebani. Powiedziałem mojemu dalszemu znajomemu który ma kapele metalową, że moge im nakrecic teledysk (troszke sie bawie w nagrywanie i montowanie i lubie to) do ich pierwszej piosenki jeśli chcą za darmo. Wszyscy szczęśliwi. Spóźniłem sie godzinę z winy korków na nagrywanie i wszyscy mnie opierdolili z nich, że jestem nie słowny. Niby im to nagrałem ale teraz mam takie, że żałuje ze nie powiedzialem im zeby spierdalali","oe", "pomocy"));
        list.add(new Note("Gardze ludźmi z Woodstocku. Troche godności a nie babrania się w błocie. Oczywiście gdy powiesz typom METALOM włosy ala POCAHONTAS że to nie twoje klimaty to usłyszysz, że sztywniak itp. Przecież festiwale typu Impact czy Kraków Live tooo nieee bo ludzie tam są czyści i nie srają na trawe do okoła","oe", "gorlice"));
        list.add(new Note("Wkurwia mnie to, że auto rodziców stoi 6 dni w tygodniu w domu ale gdy chce je pożyczyc zeby przejechać sie 10km dalej to nie bo będzie potrzebne. #kurwamadź","oe", "karyna"));
        list.add(new Note("Drugi dzień po kupnie Iphona X i pomyślałem że kupie sobie twardą szybke bo bym umarł gdybym odrazu to rozwalił. Wbiłem do serwisu i w momencie przekazywania typowi telefonu wypadł mi z ręki i jebnął o ziemie. Podnosze i pajęczyna . Ja zrobilem sie blady a typo złapał sie za łeb. To mogło być bardziej pechowe?","oe", "potwory"));
        list.add(new Note("Mam dość mojego chłopaka","oe", "chichot"));
        list.add(new Note("Czwartą noc z rzędu tuż przed zasnięciem swędzi mnie najmniejszy palec u nogi i wybudza. Ty mały skurwysynie xD","oe", "hehe"));

        return list;
    }
}
