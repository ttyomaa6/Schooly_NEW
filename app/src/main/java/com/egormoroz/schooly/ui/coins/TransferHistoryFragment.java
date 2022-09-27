package com.egormoroz.schooly.ui.coins;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class TransferHistoryFragment extends Fragment {

    ArrayList<UserInformation> listAdapterPeople=new ArrayList<UserInformation>();
    RecyclerView historyRecyclerView;
    FirebaseModel firebaseModel=new FirebaseModel();
    ImageView backToCoins;
    TextView noTransfer;
    Fragment fragment;
    UserInformation userInformation;
    String nick;
    Bundle bundle;

    public TransferHistoryFragment(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment = fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static TransferHistoryFragment newInstance(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new TransferHistoryFragment(fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transferhistory, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        backToCoins=view.findViewById(R.id.backtocoins);
        historyRecyclerView=view.findViewById(R.id.hisroryRecycler);
        noTransfer=view.findViewById(R.id.noTransfer);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(TransferMoneyFragment.newInstance(fragment,userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        backToCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(TransferMoneyFragment.newInstance(fragment,userInformation,bundle), getActivity());
            }
        });
        firebaseModel.initAll();
        setHistoryInAdapter();
    }

    public void setHistoryInAdapter(){
        if(userInformation.getTransfers()==null){
            RecentMethods.getTransferHistory(nick, firebaseModel, new Callbacks.getTransferHistory() {
                @Override
                public void getTransferHistory(ArrayList<Transfer> transfers) {
                    userInformation.setTransfers(transfers);
                    if (transfers.size()==0){
                        noTransfer.setVisibility(View.VISIBLE);
                    }else {
                        Collections.reverse(transfers);
                        TransferHistoryAdapter transferHistoryAdapter=new TransferHistoryAdapter(transfers);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        historyRecyclerView.setLayoutManager(layoutManager);
                        historyRecyclerView.setAdapter(transferHistoryAdapter);
                        noTransfer.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            if (userInformation.getTransfers().size()==0){
                noTransfer.setVisibility(View.VISIBLE);
            }else {
                TransferHistoryAdapter transferHistoryAdapter=new TransferHistoryAdapter(userInformation.getTransfers());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                historyRecyclerView.setLayoutManager(layoutManager);
                historyRecyclerView.setAdapter(transferHistoryAdapter);
                noTransfer.setVisibility(View.GONE);
            }
        }
    }
}
