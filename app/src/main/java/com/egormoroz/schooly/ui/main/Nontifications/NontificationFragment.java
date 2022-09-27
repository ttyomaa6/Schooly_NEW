package com.egormoroz.schooly.ui.main.Nontifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.PerfomanceTests;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.PeopleFragment;
import com.egormoroz.schooly.ui.people.RecomendationThread;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NontificationFragment extends Fragment {
    UserInformation userInformation;
    Bundle bundle;

    public NontificationFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static NontificationFragment newInstance(UserInformation userInformation,Bundle bundle) {
        return new NontificationFragment(userInformation,bundle);
    }

    FirebaseModel firebaseModel=new FirebaseModel();
    RecyclerView nontsRecyclerView;
    TextView emptyNonts;
    ArrayList<Subscriber> arrayListNonts=new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nontifications, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        // PerfomanceTests.deleteFakeUsers(firebaseModel);
        // PerfomanceTests.addUsersWithBehaviour(400, firebaseModel);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView backToMain = view.findViewById(R.id.backtomainfromnonts);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setCurrentFragment(MainFragment.newInstance(userInformation,bundle));
            }
        });
        nontsRecyclerView=view.findViewById(R.id.nontificationsrecyclerview);
        emptyNonts=view.findViewById(R.id.emptyNonts);
        getNontificationList();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    public  void  getNontificationList(){
        if(userInformation.getNontifications()==null){
            RecentMethods.getNontificationsList(userInformation.getNick(), firebaseModel, new Callbacks.getNontificationsList() {
                @Override
                public void getNontificationsList(ArrayList<Nontification> nontifications) {
                    if (nontifications.size()==0){
                        emptyNonts.setVisibility(View.VISIBLE);
                        emptyNonts.setText(getContext().getResources().getText(R.string.nonotificationyet));
                    }else {
                        emptyNonts.setVisibility(View.GONE);
                    }
                    Collections.reverse(nontifications);
                    NontificationAdapter nontificationAdapter=new NontificationAdapter(nontifications,userInformation);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    nontsRecyclerView.setLayoutManager(layoutManager);
                    nontsRecyclerView.setAdapter(nontificationAdapter);
                    NontificationAdapter.ItemClickListener itemClickListener=new NontificationAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(Nontification nontification,String type) {
                            if(type.equals("clothesRequest")){
                                RecentMethods.setCurrentFragment(ClothesRequestFragment.newInstance(NontificationFragment.newInstance(userInformation,bundle),nontification.getUid(),userInformation,bundle), getActivity());
                            }else if(type.equals("sub")){
                                firebaseModel.getUsersReference().child(nontification.getNick()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.exists()){
                                            Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                        }else {
                                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", nontification.getNick(), NontificationFragment.newInstance(userInformation,bundle),userInformation,bundle),getActivity());
                                        }                            }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    };
                    nontificationAdapter.setClickListener(itemClickListener);
                }
            });
        }else{
            if (userInformation.getNontifications().size()==0){
                emptyNonts.setVisibility(View.VISIBLE);
                emptyNonts.setText(getContext().getResources().getText(R.string.nonotificationyet));
            }else {
                emptyNonts.setVisibility(View.GONE);
            }
            NontificationAdapter nontificationAdapter=new NontificationAdapter(userInformation.getNontifications(),userInformation);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            nontsRecyclerView.setLayoutManager(layoutManager);
            nontsRecyclerView.setAdapter(nontificationAdapter);
            NontificationAdapter.ItemClickListener itemClickListener=new NontificationAdapter.ItemClickListener() {
                @Override
                public void onItemClick(Nontification nontification,String type) {
                    if(type.equals("clothesRequest")){
                        RecentMethods.setCurrentFragment(ClothesRequestFragment.newInstance(NontificationFragment.newInstance(userInformation,bundle),nontification.getUid(),userInformation,bundle), getActivity());
                    }else if(type.equals("sub")){
                        firebaseModel.getUsersReference().child(nontification.getNick()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()){
                                    Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                }else {
                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", nontification.getNick(), NontificationFragment.newInstance(userInformation,bundle),userInformation,bundle),getActivity());
                                }                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            };
            nontificationAdapter.setClickListener(itemClickListener);
        }
    }


}
