package com.egormoroz.schooly.ui.main.Mining;

import android.os.Bundle;
import android.util.Log;
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

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MyMinersFragment extends Fragment {

    ArrayList<Miner> listAdapter=new ArrayList<Miner>();
    UserInformation userInformation;
    Bundle bundle;

    public MyMinersFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static MyMinersFragment newInstance(UserInformation userInformation,Bundle bundle) {
        return new MyMinersFragment(userInformation,bundle);
    }
    private FirebaseModel firebaseModel = new FirebaseModel();
    RecyclerView recyclerviewMining;
    TextView useMiner,emptyMyMiners,buyMiner;
    String nick;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_myminers, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(MiningFragment.newInstance(userInformation,bundle), getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        ImageView backtomoning=view.findViewById(R.id.back_tomining);
        backtomoning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setCurrentFragment(MiningFragment.newInstance(userInformation,bundle));
            }
        });
        recyclerviewMining=view.findViewById(R.id.recyclerviewmyminers);
        useMiner=view.findViewById(R.id.use);
        emptyMyMiners=view.findViewById(R.id.emptyMyMiners);
        buyMiner=view.findViewById(R.id.buyMiner);
        GetMyMinersFromBase();
    }

    public void GetMyMinersFromBase(){
        if(userInformation.getMyMiners()==null){
            RecentMethods.MyMinersFromBase(nick,firebaseModel, new Callbacks.GetMyMinerFromBase(){
                @Override
                public void GetMyMinerFromBase(ArrayList<Miner> myMinersFromBase) {
                    listAdapter.addAll(myMinersFromBase);
                    userInformation.setMyMiners(myMinersFromBase);
                    if (myMinersFromBase.size()==0){
                        emptyMyMiners.setVisibility(View.VISIBLE);
                        recyclerviewMining.setVisibility(View.GONE);
                    }else {
                        recyclerviewMining.setVisibility(View.VISIBLE);
                        emptyMyMiners.setVisibility(View.GONE);
                        buyMiner.setVisibility(View.GONE);
                    }
                    MyMinersAdapter myminersAdapter=new MyMinersAdapter(listAdapter,userInformation);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setReverseLayout(true);layoutManager.setStackFromEnd(true);
                    recyclerviewMining.setLayoutManager(layoutManager);
                    recyclerviewMining.setAdapter(myminersAdapter);
                }
            });
        } else {
            if (userInformation.getMyMiners().size()==0){
                emptyMyMiners.setVisibility(View.VISIBLE);
                recyclerviewMining.setVisibility(View.GONE);
            }else {
                recyclerviewMining.setVisibility(View.VISIBLE);
                emptyMyMiners.setVisibility(View.GONE);
                buyMiner.setVisibility(View.GONE);
            }
            MyMinersAdapter myminersAdapter=new MyMinersAdapter(userInformation.getMyMiners(),userInformation);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            recyclerviewMining.setLayoutManager(layoutManager);
            recyclerviewMining.setAdapter(myminersAdapter);
        }
    }
}
