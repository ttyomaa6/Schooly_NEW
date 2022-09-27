package com.egormoroz.schooly.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.CreateCharacter.CreateCharacterFragment;
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GenderFragment extends Fragment {
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;
    String type;

    public GenderFragment(UserInformation userInformation,Bundle bundle,Fragment fragment,String type) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
        this.type=type;
    }

    public static GenderFragment newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment,String type) {
        return new GenderFragment(userInformation,bundle,fragment,type);

    }

    RadioGroup radioGroup;
    RadioButton radioButton1,radioButton2;
    ImageView imageViewContinue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gender, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ImageView leftarrowtoreg =view.findViewById(R.id.leftarrowtoreg);
        leftarrowtoreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        imageViewContinue=view.findViewById(R.id.continueImage);
        radioGroup=view.findViewById(R.id.radioGroup);
        radioButton1=view.findViewById(R.id.radio_button_1);
        radioButton2=view.findViewById(R.id.radio_button_2);
        if (bundle!=null){
            if(bundle.getString("GENDER")!=null){
                if(bundle.getString("GENDER").equals("man")){
                    radioGroup.check( R.id.radio_button_1);
                }else {
                    radioGroup.check(R.id.radio_button_2);
                }
            }
        }

        imageViewContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((radioGroup.getCheckedRadioButtonId()!=-1)){
                    int idExclusive=radioGroup.getCheckedRadioButtonId();
                    switch(idExclusive){
                        case R.id.radio_button_1:
                            bundle.putString("GENDER", "man");
                            break;
                        case R.id.radio_button_2:
                            bundle.putString("GENDER", "woman");
                            break;
                    }
                    RecentMethods.setCurrentFragment(CreateCharacterFragment.newInstance(userInformation,bundle,GenderFragment.newInstance(userInformation, bundle, fragment, type),type), getActivity());
                }else {
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.mustselectagender), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
