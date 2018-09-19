package pl.itomaszjanik.test.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import pl.itomaszjanik.test.R;
import pl.itomaszjanik.test.Remote.RegisterCallback;
import pl.itomaszjanik.test.SwitchLogged;
import pl.itomaszjanik.test.Utilities;
import pl.itomaszjanik.test.Values;

public class ProfileUnsignedRegister extends Fragment implements RegisterCallback {

    private CardView usernameCard, passwordCard;
    private RelativeLayout mainLayout;
    private EditText username, password;
    private CheckBox checkbox;
    private SharedPreferences sharedPreferences;

    public ProfileUnsignedRegister() {}

    public static ProfileUnsignedRegister newInstance(){
        return new ProfileUnsignedRegister();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_unsigned_register, container,false);
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

    @Override
    public void onRegisterSucceeded(String username, int userID){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("signed", true);
        editor.putString("username", username);
        editor.putInt("userID", userID);
        editor.apply();

        SwitchLogged switchLogged = (SwitchLogged)getParentFragment();
        switchLogged.switchLogged();
    }

    @Override
    public void onRegisterFailed(){
        if (isAdded()){
            Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.register_failed));
        }
    }

    @Override
    public void onRegisterNotUnique(){
        if (isAdded()){
            Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            usernameCard.startAnimation(animShake);
            Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.register_not_unique));
        }
    }

    @Override
    public void onRegisterNoInternet(){
        if (isAdded()){
            Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.no_internet));
        }
    }

    private void init(View view){
        sharedPreferences = getContext().getSharedPreferences(Values.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        mainLayout = view.findViewById(R.id.main_layout);
        usernameCard = view.findViewById(R.id.username_card);
        passwordCard = view.findViewById(R.id.password_card);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        checkbox = view.findViewById(R.id.checkBox);

        view.findViewById(R.id.register_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();
                if (usernameText.isEmpty() || usernameText.length() < 4){
                    Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    usernameCard.startAnimation(animShake);

                    Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.username_too_short));
                    return;
                }

                if (passwordText.isEmpty() || passwordText.length() < 6){
                    Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    passwordCard.startAnimation(animShake);

                    Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.password_too_short));
                    return;
                }

                if (!checkbox.isChecked()){
                    Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    checkbox.startAnimation(animShake);

                    Utilities.showSnackbarText(getContext(), mainLayout, getString(R.string.checkbox_not_checked));
                }
                else{
                    Utilities.register(usernameText, passwordText,
                            ProfileUnsignedRegister.this, getContext());
                    Utilities.hideKeyboard(getActivity());
                }
            }
        });

    }

}

