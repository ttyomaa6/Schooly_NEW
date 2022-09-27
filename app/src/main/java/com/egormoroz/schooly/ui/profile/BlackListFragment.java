package com.egormoroz.schooly.ui.profile;

import android.os.Bundle;
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
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.PeopleFragment;
import com.egormoroz.schooly.ui.profile.Wardrobe.CreateLookFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public  class BlackListFragment extends Fragment {
    FirebaseModel firebaseModel=new FirebaseModel();
    RecyclerView recyclerView;
    ImageView back;
    TextView emptyList;
    BlackListAdapter.ItemClickListener clickListener;
    String type,nick,userNameToProfile;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;

    public BlackListFragment(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.type = type;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static BlackListFragment newInstance(String type, Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new BlackListFragment(type,fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blacklist, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        recyclerView=view.findViewById(R.id.blackListRecycler);
        back=view.findViewById(R.id.back_tosettings);
        emptyList=view.findViewById(R.id.emptyBlackList);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(SettingsFragment.newInstance(type,fragment,userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(SettingsFragment.newInstance(type,fragment,userInformation,bundle),getActivity());
            }
        });
        setBlackListInAdapter();
    }

    public void setBlackListInAdapter(){
        if(userInformation.getBlackList()==null){
            RecentMethods.getBlackList(nick, firebaseModel, new Callbacks.getSubscribersList() {
                @Override
                public void getSubscribersList(ArrayList<Subscriber> subscribers) {
                    userInformation.setBlackList(subscribers);
                    if(subscribers.size()==0){
                        emptyList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        BlackListAdapter blackListAdapter=new BlackListAdapter(subscribers,userInformation);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(blackListAdapter);
                        clickListener=new BlackListAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Subscriber user = blackListAdapter.getItem(position);
                                userNameToProfile=user.getSub();
                                firebaseModel.getUsersReference().child(userNameToProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.exists()){
                                            Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                        }else {
                                            if(userNameToProfile.equals(nick)){
                                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback",nick,BlackListFragment.newInstance(type,fragment,userInformation,bundle),userInformation,bundle),getActivity());
                                            }else {
                                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile,BlackListFragment.newInstance(type,fragment,userInformation,bundle),userInformation,bundle),
                                                        getActivity());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        };
                        blackListAdapter.setClickListener(clickListener);
                    }
                }
            });
        }else {
            if(userInformation.getBlackList().size()==0){
                emptyList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
                BlackListAdapter blackListAdapter=new BlackListAdapter(userInformation.getBlackList(),userInformation);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(blackListAdapter);
                clickListener=new BlackListAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Subscriber user = blackListAdapter.getItem(position);
                        userNameToProfile=user.getSub();
                        if(userNameToProfile.equals(nick)){
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback",nick,fragment,userInformation,bundle),getActivity());
                        }else {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile,BlackListFragment.newInstance(type,fragment,userInformation,bundle),userInformation,bundle),
                                    getActivity());
                        }
                    }
                };
                blackListAdapter.setClickListener(clickListener);
            }
        }
    }
}
