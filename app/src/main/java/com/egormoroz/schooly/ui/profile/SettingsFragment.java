package com.egormoroz.schooly.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsFragment extends Fragment {


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    FirebaseModel firebaseModel=new FirebaseModel();
    TextView  userNick,userNumber,userPassword;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
         View root =inflater.inflate(R.layout.fragment_settings,container,false);
         BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
         bnv.setVisibility(bnv.GONE);
         firebaseModel.initAll();
         return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);


        ImageView imageView = view.findViewById(R.id.backtomainfromsettings);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setCurrentFragment(ProfileFragment.newInstance("user",
                        new UserInformation()));
            }
        });
        userNick=view.findViewById(R.id.userNick);
        userNumber=view.findViewById(R.id.userNumber);
        userPassword=view.findViewById(R.id.userPassword);
        viewNick();

    }

    public void viewNick(){
        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
            @Override
            public void PassUserNick(String nick) {
                userNick.setText(nick);
            }
        });
    }



}
