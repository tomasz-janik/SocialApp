package pl.itomaszjanik.test.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import pl.itomaszjanik.test.BottomPopup.BottomPopup;
import pl.itomaszjanik.test.R;
import pl.itomaszjanik.test.Remote.RegisterCallback;
import pl.itomaszjanik.test.Utilities;

public class ProfileUnsignedRegister extends Fragment implements RegisterCallback {

    private BottomPopup bottomPopup;
    private EditText username, password;

    public ProfileUnsignedRegister() {
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
    public void onRegisterSucceeded(){
    }

    @Override
    public void onRegisterFailed(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.register_failed), bottomPopup);
        }
    }

    @Override
    public void onRegisterNotUnique(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.register_not_unique), bottomPopup);
        }
    }

    @Override
    public void onRegisterNoInternet(){
        if (isAdded()){
            bottomPopup = Utilities.getBottomPopupText(getContext(),
                    R.layout.bottom_popup_text, R.id.bottom_popup_text,
                    getString(R.string.no_internet), bottomPopup);
        }
    }

    private void init(View view){
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);

        view.findViewById(R.id.register_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.register(username.getText().toString(), password.getText().toString(),
                        ProfileUnsignedRegister.this, getContext());
            }
        });

    }

}

