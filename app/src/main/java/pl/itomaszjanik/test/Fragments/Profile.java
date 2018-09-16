package pl.itomaszjanik.test.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.itomaszjanik.test.R;
import pl.itomaszjanik.test.SwitchLogged;
import pl.itomaszjanik.test.Values;

public class Profile extends Fragment implements SwitchLogged {

    private ProfileSigned profileSigned;
    private ProfileUnsigned profileUnsigned;
    private boolean signed;

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
        return inflater.inflate(R.layout.profile, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void switchLogged(){
        signed = true;

        if (profileSigned == null){
            profileSigned = ProfileSigned.newInstance();
        }

        Bundle bundle = new Bundle();
        bundle.putBoolean("refresh", true);
        profileSigned.setArguments(bundle);

        FragmentTransaction mTransaction = getChildFragmentManager().beginTransaction();
        mTransaction.replace(R.id.main_view, profileSigned, profileSigned.getClass().getName());
        mTransaction.commit();


    }

    private void init(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Values.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        signed = sharedPreferences.getBoolean("signed", false);

        switchFragments();
    }


    private void switchFragments(){
        FragmentTransaction mTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment;

        if (signed){
            if (profileSigned == null){
                profileSigned = ProfileSigned.newInstance();
            }
            fragment = profileSigned;
        }
        else{
            if (profileUnsigned == null){
                profileUnsigned = ProfileUnsigned.newInstance();
            }
            fragment = profileUnsigned;
        }
        mTransaction.replace(R.id.main_view, fragment, fragment.getClass().getName());
        mTransaction.commit();
    }

    public boolean getStarted(){
        if (profileSigned != null){
            return profileSigned.getStarted();
        }
        return false;
    }

    public void loadPosts(){
        if (profileSigned != null){
            profileSigned.loadPosts();
        }
    }


}

