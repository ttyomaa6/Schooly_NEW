package com.egormoroz.schooly.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ComplainFragment extends Fragment {
    String otherUserNick,nick;
    ComplainAdapter.ItemClickListener itemClickListener;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;

    public ComplainFragment(String otherUserNick,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.otherUserNick = otherUserNick;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static ComplainFragment newInstance(String otherUserNick,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new ComplainFragment(otherUserNick,fragment,userInformation,bundle);
    }

    FirebaseModel firebaseModel=new FirebaseModel();
    RecyclerView recyclerView;
    TextView otherUserNickText;
    ImageView back;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View root =inflater.inflate(R.layout.fragment_complain,container,false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();

        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        otherUserNickText=view.findViewById(R.id.complainOtherUserText);
        otherUserNickText.setText(otherUserNick);
        recyclerView=view.findViewById(R.id.reasonsRecycler);
        back=view.findViewById(R.id.backtoOtherUser);
        itemClickListener=new ComplainAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Reason reason) {
                RecentMethods.setCurrentFragment(ComplainFragmentToBase.newInstance(otherUserNick,fragment,userInformation,bundle), getActivity());
            }
        };
        ArrayList<Reason> arrayListReason=new ArrayList<>();
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.fraud)));
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.violenceordangerousorganizations)));
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.hostilesayingsorsymbols)));
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.saleofillegalgoods)));
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.violationofintellectualpropertyrights)));
        ComplainAdapter complainAdapter=new ComplainAdapter(arrayListReason,itemClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(complainAdapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", otherUserNick, fragment,userInformation,bundle),
                        getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", otherUserNick, fragment,userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
}