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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.profile.Wardrobe.CreateLookFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class SavedFragment extends Fragment {

    FirebaseModel firebaseModel=new FirebaseModel();
    RecyclerView recyclerView;
    ImageView back;
    TextView emptyList;
    UserInformation userInformation;
    String type,nick;
    Fragment fragment;
    Bundle bundle;

    public SavedFragment(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.type = type;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static SavedFragment newInstance(String type, Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new SavedFragment(type,fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saved, container, false);
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
        emptyList=view.findViewById(R.id.noSaved);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(SettingsFragment.newInstance(type,fragment,userInformation,bundle),getActivity());
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(SettingsFragment.newInstance(type, fragment,userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        setSavedLooksInAdapter();

    }

    public void setSavedLooksInAdapter(){
        if(userInformation.getSavedLooks()==null){
            RecentMethods.getSavedLooks(nick, firebaseModel, new Callbacks.getSavedLook() {
                @Override
                public void getSavedLook(ArrayList<NewsItem> newsItems) {
                    userInformation.setSavedLooks(newsItems);
                    if(newsItems.size()==0){
                        emptyList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        Collections.reverse(newsItems);
                        LooksAdapter looksAdapter=new LooksAdapter(newsItems, SavedFragment.newInstance(type,fragment,userInformation,bundle),recyclerView);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        recyclerView.setAdapter(looksAdapter);
                        LooksAdapter.ItemClickListener itemClickListener=new LooksAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(NewsItem newsItem) {
                                firebaseModel.getUsersReference().child(newsItem.getNick()).child("looks")
                                        .child(newsItem.getNewsId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DataSnapshot snapshot=task.getResult();
                                            if(snapshot.exists()){
                                                RecentMethods.setCurrentFragment(ViewingLookFragment.newInstance(SavedFragment.newInstance(type,fragment,userInformation,bundle),userInformation,bundle), getActivity());
                                            }else{
                                                Toast.makeText(getContext(), R.string.thepublicationwasnotfound, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }
                        };
                        looksAdapter.setClickListener(itemClickListener);
                    }
                }
            });
        }else {
            if(userInformation.getSavedLooks().size()==0){
                emptyList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
                LooksAdapter looksAdapter=new LooksAdapter(userInformation.getSavedLooks(), SavedFragment.newInstance(type,fragment,userInformation,bundle),recyclerView);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView.setAdapter(looksAdapter);
                LooksAdapter.ItemClickListener itemClickListener=new LooksAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(NewsItem newsItem) {
                        firebaseModel.getUsersReference().child(newsItem.getNick()).child("looks")
                                .child(newsItem.getNewsId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    DataSnapshot snapshot=task.getResult();
                                    if(snapshot.exists()){
                                        RecentMethods.setCurrentFragment(ViewingLookFragment.newInstance(SavedFragment.newInstance(type,fragment,userInformation,bundle),userInformation,bundle), getActivity());
                                    }else{
                                        Toast.makeText(getContext(), R.string.thepublicationwasnotfound, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                };
                looksAdapter.setClickListener(itemClickListener);
            }
        }
    }
}
