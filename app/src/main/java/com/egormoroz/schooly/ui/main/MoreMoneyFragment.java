package com.egormoroz.schooly.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.chat.User;
import com.egormoroz.schooly.ui.coins.CoinsFragmentSecond;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.FittingFragment;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class MoreMoneyFragment extends Fragment {

    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;

    public MoreMoneyFragment(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment = fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static MoreMoneyFragment newInstance(Fragment fragment, UserInformation userInformation,Bundle bundle) {
        return new MoreMoneyFragment(fragment,userInformation,bundle);

    }


    ImageView back;
    private FirebaseModel firebaseModel = new FirebaseModel();
    RelativeLayout buyOne,buyTwo,buyThree;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_moremoney, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;

    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        buyOne=view.findViewById(R.id.buy);
        buyTwo=view.findViewById(R.id.buy2);
        buyThree=view.findViewById(R.id.buy3);
        back=view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
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
    }
}
