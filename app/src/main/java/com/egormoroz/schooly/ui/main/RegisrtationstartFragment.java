package com.egormoroz.schooly.ui.main;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class RegisrtationstartFragment extends Fragment {
    UserInformation userInformation;
    Bundle bundle;

    public RegisrtationstartFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static RegisrtationstartFragment newInstance(UserInformation userInformation,Bundle bundle) {
        return new RegisrtationstartFragment(userInformation,bundle);
    }
    Button RegistrationButton;
    Button EnterButton;
    FirebaseAuth AuthBase;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reg, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        AuthBase = FirebaseAuth.getInstance();
        RegistrationButton = root.findViewById(R.id.registr);
        EnterButton = root.findViewById(R.id.enter);
        ////////////Is user Logged in
        //isUserLoggedIn();
        RegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecentMethods.setCurrentFragment(RegFragment.newInstance(userInformation,bundle), getActivity());
            }
        });
        EnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInformation userInformation=new UserInformation();
                RecentMethods.setCurrentFragment(EnterFragment.newInstance(userInformation,bundle), getActivity());
            }
        });
        return root;
    }
    void isUserLoggedIn(){
        // if(AuthBase.getCurrentUser() != null)
        //RecentMethods.setCurrentFragment(MainFragment.newInstance(), getActivity());
    }
}
