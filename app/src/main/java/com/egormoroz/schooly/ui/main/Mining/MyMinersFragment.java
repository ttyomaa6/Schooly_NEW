package com.egormoroz.schooly.ui.main.Mining;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MyMinersFragment extends Fragment {

    ArrayList<Miner> listAdapter=new ArrayList<Miner>();
    public static MyMinersFragment newInstanse(){return new MyMinersFragment();}
    private FirebaseModel firebaseModel = new FirebaseModel();
    RecyclerView recyclerviewMining;
    TextView useMiner,emptyMyMiners,buyMiner;


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
        ImageView backtomoning=view.findViewById(R.id.back_tomining);
        backtomoning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setCurrentFragment(MiningFragment.newInstanse());
            }
        });
        GetMyMinersFromBase();
        recyclerviewMining=view.findViewById(R.id.recyclerviewmyminers);
        useMiner=view.findViewById(R.id.use);
        emptyMyMiners=view.findViewById(R.id.emptyMyMiners);
        buyMiner=view.findViewById(R.id.buyMiner);
    }

    public void GetMyMinersFromBase(){
        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
            @Override
            public void PassUserNick(String nick) {
                RecentMethods.MyMinersFromBase(nick,firebaseModel, new Callbacks.GetMyMinerFromBase(){
                            @Override
                            public void GetMyMinerFromBase(ArrayList<Miner> myMinersFromBase) {
                                listAdapter.addAll(myMinersFromBase);
                                if (myMinersFromBase.size()==0){
                                    emptyMyMiners.setVisibility(View.VISIBLE);
                                }else {
                                    emptyMyMiners.setVisibility(View.GONE);
                                    buyMiner.setVisibility(View.GONE);
                                }
                                MyMinersAdapter myminersAdapter=new MyMinersAdapter(listAdapter);
                                recyclerviewMining.setAdapter(myminersAdapter);
                            }
                        });
            }
        });
    }
}
