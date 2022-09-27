package com.egormoroz.schooly.ui.profile;

import android.os.Bundle;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class LooksFragmentProfileOther extends Fragment {

  RecyclerView looksRecyclerOther;
  TextView noLooksOther;
  FirebaseModel firebaseModel=new FirebaseModel();
  String otherUserNick;
  ArrayList<NewsItem> look;
  Fragment fragment;
  UserInformation userInformation;
  Bundle bundle;

  public LooksFragmentProfileOther(String otherUserNick, Fragment fragment, UserInformation userInformation,Bundle bundle) {
    this.otherUserNick = otherUserNick;
    this.fragment=fragment;
    this.userInformation=userInformation;
    this.bundle=bundle;
  }

  public static LooksFragmentProfileOther newInstance(String otherUserNick,Fragment fragment,UserInformation userInformation,Bundle bundle) {
    return new LooksFragmentProfileOther(otherUserNick,fragment,userInformation,bundle);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.viewpager_profileother, container, false);
    BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
    bnv.setVisibility(bnv.VISIBLE);
    firebaseModel.initAll();
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
    noLooksOther=view.findViewById(R.id.noLooks);
    looksRecyclerOther=view.findViewById(R.id.Recycler);
    checkLooksOther();
  }

  public void checkLooksOther(){
    if(bundle!=null){
      if(bundle.getSerializable(otherUserNick+"LOOKS_OTHER_BUNDLE")!=null){
        look= (ArrayList<NewsItem>) bundle.getSerializable(otherUserNick+"LOOKS_OTHER_BUNDLE");
        if (look.size()==0){
          noLooksOther.setVisibility(View.VISIBLE);
          noLooksOther.setText(getContext().getResources().getString(R.string.nolooks1)+" "+otherUserNick+" "+getContext().getResources().getString(R.string.nolooks2));
          looksRecyclerOther.setVisibility(View.GONE);
        }else{
          LooksAdapter looksAdapter=new LooksAdapter(look,LooksFragmentProfileOther.newInstance(otherUserNick,fragment,userInformation,bundle),looksRecyclerOther);
          GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(), 3);
          looksRecyclerOther.setLayoutManager(gridLayoutManager);
          looksRecyclerOther.setAdapter(looksAdapter);
          LooksAdapter.ItemClickListener itemClickListener=new LooksAdapter.ItemClickListener() {
            @Override
            public void onItemClick(NewsItem newsItem) {
              ProfileFragment.checkLoadValueOther(newsItem,otherUserNick,getActivity());
            }
          };
          looksAdapter.setClickListener(itemClickListener);
        }
      }else {
        RecentMethods.getLooksList(otherUserNick, firebaseModel, new Callbacks.getLooksList() {
          @Override
          public void getLooksList(ArrayList<NewsItem> look) {
            bundle.putSerializable(otherUserNick+"LOOKS_OTHER_BUNDLE",look);
            if (look.size()==0){
              noLooksOther.setVisibility(View.VISIBLE);
              noLooksOther.setText(getContext().getResources().getString(R.string.nolooks1)+" "+otherUserNick+" "+getContext().getResources().getString(R.string.nolooks2));
              looksRecyclerOther.setVisibility(View.GONE);
            }else{
              Collections.reverse(look);
              LooksAdapter looksAdapter=new LooksAdapter(look,LooksFragmentProfileOther.newInstance(otherUserNick,fragment,userInformation,bundle),looksRecyclerOther);
              GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(), 3);
              looksRecyclerOther.setLayoutManager(gridLayoutManager);
              looksRecyclerOther.setAdapter(looksAdapter);
              LooksAdapter.ItemClickListener itemClickListener=new LooksAdapter.ItemClickListener() {
                @Override
                public void onItemClick(NewsItem newsItem) {
                  ProfileFragment.checkLoadValueOther(newsItem,otherUserNick,getActivity());
                }
              };
              looksAdapter.setClickListener(itemClickListener);
            }
          }
        });
      }
    }
  }
}