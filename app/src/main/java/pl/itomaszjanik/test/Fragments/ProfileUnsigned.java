package pl.itomaszjanik.test.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import pl.itomaszjanik.test.*;

public class ProfileUnsigned extends Fragment {

    private static final int VIEW_LOGIN = 0;
    private static final int VIEW_REGISTER = 1;

    private int currentPosition = VIEW_LOGIN;
    private RelativeLayout login, register;
    private TextView loginText, registerText;

    public ProfileUnsigned() {
    }

    public static ProfileUnsigned newInstance() {
        return new ProfileUnsigned();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_unsigned, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void init(View view){
        initNavigation(view);
        switchFragments();
    }

    private void initNavigation(View view){
        login = view.findViewById(R.id.login);
        register = view.findViewById(R.id.register);
        loginText = view.findViewById(R.id.login_text);
        registerText = view.findViewById(R.id.register_text);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPosition != VIEW_LOGIN){
                    currentPosition = VIEW_LOGIN;
                    loginText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextActive));
                    registerText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextInactive));
                    switchFragments();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPosition != VIEW_REGISTER){
                    currentPosition = VIEW_REGISTER;
                    loginText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextInactive));
                    registerText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextActive));
                    switchFragments();
                }
            }
        });

    }

    private void switchFragments(){
        FragmentTransaction mTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment;
        switch (currentPosition){
            case VIEW_LOGIN:
                fragment = new ProfileUnsignedLogin();
                break;
            case VIEW_REGISTER:
                fragment = new ProfileUnsignedRegister();
                break;
            default:
                fragment = new ProfileUnsignedLogin();
                break;
        }
        mTransaction.replace(R.id.main_fragment, fragment, fragment.getClass().getName());
        mTransaction.commit();
    }


}

