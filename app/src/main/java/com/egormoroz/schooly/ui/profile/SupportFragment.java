package com.egormoroz.schooly.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SupportFragment extends Fragment {

    FirebaseModel firebaseModel=new FirebaseModel();
    ImageView back;
    String type,nick,userNameToProfile;
    Fragment fragment;
    RelativeLayout submitcomplaint;
    UserInformation userInformation;
    Bundle bundle;
    EditText aboutProblem;

    public SupportFragment(String type, Fragment fragment, UserInformation userInformation, Bundle bundle) {
        this.type = type;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static SupportFragment newInstance(String type, Fragment fragment, UserInformation userInformation,Bundle bundle) {
        return new SupportFragment(type,fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_support, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        back=view.findViewById(R.id.back_tosettingssupport);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(SettingsFragment.newInstance(type, fragment, userInformation, bundle), getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(SettingsFragment.newInstance(type, fragment, userInformation, bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        aboutProblem=view.findViewById(R.id.aboutproblem);
        submitcomplaint=view.findViewById(R.id.submitcomplaint);
        submitcomplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aboutProblem.getText().toString().length()==0){
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.thereasonforthecomplaintisnotentered), Toast.LENGTH_SHORT).show();
                }else{
                    String uid=firebaseModel.getReference().child("AppData").child("support").push().getKey();
                    firebaseModel.getReference().child("AppData").child("support").child(uid)
                            .setValue(new Complain("",nick,aboutProblem.getText().toString(),"",uid,new NewsItem()));
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.complaintsent), Toast.LENGTH_SHORT).show();
                    RecentMethods.setCurrentFragment(SettingsFragment.newInstance(type, fragment, userInformation, bundle), getActivity());
                }
            }
        });

    }
}
