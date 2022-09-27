package com.egormoroz.schooly.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.profile.Wardrobe.CreateLookFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class LooksFragmentProfile extends Fragment {

    RecyclerView looksRecycler;
    TextView createNewLookText,createNewLook;
    FirebaseModel firebaseModel=new FirebaseModel();
    int looksListSize;
    String type,nick;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;

    public LooksFragmentProfile(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.type = type;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static LooksFragmentProfile newInstance(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new LooksFragmentProfile(type,fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpager_profile, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.VISIBLE);
        firebaseModel.initAll();
//        AppBarLayout abl = getActivity().findViewById(R.id.AppBarLayout);
//        abl.setVisibility(abl.GONE);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().requestLayout();

    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        createNewLook=view.findViewById(R.id.CreateYourLook);
        createNewLookText=view.findViewById(R.id.textCreateYourLook);
        looksRecycler=view.findViewById(R.id.Recycler);
        putLooks();
    }

    public void putLooks(){
        if(userInformation.getLooks()==null){
            RecentMethods.getLooksList(nick, firebaseModel, new Callbacks.getLooksList() {
                @Override
                public void getLooksList(ArrayList<NewsItem> look) {
                    looksListSize=look.size();
                    if (looksListSize==0){
                        createNewLookText.setVisibility(View.VISIBLE);
                        createNewLookText.setText(R.string.createyourfirstlook);
                        createNewLook.setVisibility(View.VISIBLE);
                        looksRecycler.setVisibility(View.GONE);
                        createNewLook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecentMethods.setCurrentFragment(CreateLookFragment.newInstance(type,fragment,userInformation,bundle,"newlook"), getActivity());
                            }
                        });
                    }else {
                        Collections.reverse(look);
                        LooksAdapter looksAdapter=new LooksAdapter(look,LooksFragmentProfile.newInstance(type,fragment,userInformation,bundle),looksRecycler);
                        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(), 3);
                        looksAdapter.setHasStableIds(true);
                        looksRecycler.setHasFixedSize(true);
                        looksRecycler.setItemViewCacheSize(20);
                        looksRecycler.setLayoutManager(gridLayoutManager);
                        looksRecycler.setAdapter(looksAdapter);
                        LooksAdapter.ItemClickListener itemClickListener=new LooksAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(NewsItem newsItem) {
                                ProfileFragment.checkLoadValue(newsItem,userInformation,getActivity());

                            }
                        };
                        looksAdapter.setClickListener(itemClickListener);
                    }
                }
            });
        }else {
            if (userInformation.getLooks().size()==0){
                createNewLookText.setVisibility(View.VISIBLE);
                createNewLookText.setText(R.string.createyourfirstlook);
                createNewLook.setVisibility(View.VISIBLE);
                looksRecycler.setVisibility(View.GONE);
                createNewLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.setCurrentFragment(CreateLookFragment.newInstance(type,fragment,userInformation,bundle,"newlook"), getActivity());
                    }
                });
            }else {
                LooksAdapter looksAdapter = new LooksAdapter(userInformation.getLooks(), LooksFragmentProfile.newInstance(type, fragment, userInformation,bundle), looksRecycler);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
                looksAdapter.setHasStableIds(true);
                looksRecycler.setHasFixedSize(true);
                looksRecycler.setItemViewCacheSize(20);
                looksRecycler.setLayoutManager(gridLayoutManager);
                looksRecycler.setAdapter(looksAdapter);
                LooksAdapter.ItemClickListener itemClickListener = new LooksAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(NewsItem newsItem) {
                        ProfileFragment.checkLoadValue(newsItem,userInformation,getActivity());
                    }
                };
                looksAdapter.setClickListener(itemClickListener);
            }
        }
    }
}