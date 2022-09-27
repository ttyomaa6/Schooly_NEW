package com.egormoroz.schooly.ui.profile.Wardrobe;

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
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.FittingFragment;
import com.egormoroz.schooly.ui.main.Shop.ShopFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class WardrobeAccessories extends Fragment {
    String type,nick,fragmentString;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;

    public WardrobeAccessories(String type,Fragment fragment,UserInformation userInformation,Bundle bundle,String fragmentString) {
        this.type = type;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragmentString=fragmentString;
    }

    public static WardrobeAccessories newInstance(String type,Fragment fragment,UserInformation userInformation,Bundle bundle,String fragmentString) {
        return new WardrobeAccessories(type,fragment,userInformation,bundle,fragmentString);
    }
    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<Clothes> clothesArrayListWardrobe=new ArrayList<Clothes>();
    ArrayList<Clothes> sortAccessoriesArrayListWardrobe=new ArrayList<Clothes>();
    RecyclerView wardrobeRecyclerView;
    WardrodeClothesAdapter.ItemClickListener itemClickListener;
    TextView buyToShop,noClothesText;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpagerwardrobe, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
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
        nick=userInformation.getNick();
        wardrobeRecyclerView=view.findViewById(R.id.recyclerwardrobe);
        itemClickListener=new WardrodeClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes,String type,String fragmentString) {
                if(type.equals("view")){
                    if (fragmentString.equals("wardrobe")){
                        WardrobeFragment.sentToViewingFrag(type,fragment,userInformation,bundle, getActivity());
                    }else if(fragmentString.equals("createClothes")){
                        CreateLookFragment.sentToViewingFrag(type,fragment,userInformation,bundle, getActivity());
                    }else if(fragmentString.equals("tryOn")){
                        FittingFragment.sentToViewingFrag(type,fragment,userInformation,bundle, getActivity(),clothes);
                    }
                }else{
                    if (fragmentString.equals("wardrobe")){
                        WardrobeFragment.checkOnTryOn(clothes,getActivity());
                    }else if(fragmentString.equals("createClothes")){
                        CreateLookFragment.checkOnTryOn(clothes,getActivity());
                    }else if(fragmentString.equals("tryOn")){
                        FittingFragment.checkOnTryOn(clothes,getActivity());
                    }
                }
            }
        };
        buyToShop=view.findViewById(R.id.buyToShop);
        noClothesText=view.findViewById(R.id.noClothesText);
        loadClothesInWardrobe();
    }


    public void loadClothesInWardrobe(){
        if(userInformation.getClothes()==null){
            RecentMethods.getClothesInWardrobe(nick, firebaseModel, new Callbacks.GetClothes() {
                @Override
                public void getClothes(ArrayList<Clothes> allClothes) {
                    clothesArrayListWardrobe.addAll(allClothes);
                    for(int i=0;i<clothesArrayListWardrobe.size();i++){
                        Clothes cl=clothesArrayListWardrobe.get(i);
                        String clType=cl.getClothesType();
                        if (clType.equals("accessories")){
                            sortAccessoriesArrayListWardrobe.add(cl);
                        }
                    }
                    if (sortAccessoriesArrayListWardrobe.size()==0){
                        buyToShop.setVisibility(View.VISIBLE);
                        noClothesText.setVisibility(View.VISIBLE);
                        wardrobeRecyclerView.setVisibility(View.GONE);
                        buyToShop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecentMethods.setCurrentFragment(ShopFragment.newInstance(userInformation,bundle,fragment), getActivity());
                            }
                        });
                    }else {
                        Collections.reverse(sortAccessoriesArrayListWardrobe);
                        WardrodeClothesAdapter newClothesAdapter = new WardrodeClothesAdapter(sortAccessoriesArrayListWardrobe, itemClickListener,userInformation,fragmentString);
                        wardrobeRecyclerView.setAdapter(newClothesAdapter);
                    }
                }
            });
        }else {
            for(int i=0;i<userInformation.getClothes().size();i++){
                Clothes cl=userInformation.getClothes().get(i);
                String clType=cl.getClothesType();
                if (clType.equals("accessories")){
                    sortAccessoriesArrayListWardrobe.add(cl);
                }
            }
            if (sortAccessoriesArrayListWardrobe.size()==0){
                buyToShop.setVisibility(View.VISIBLE);
                noClothesText.setVisibility(View.VISIBLE);
                wardrobeRecyclerView.setVisibility(View.GONE);
                buyToShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.setCurrentFragment(ShopFragment.newInstance(userInformation,bundle,fragment), getActivity());
                    }
                });
            }else {
                Collections.reverse(sortAccessoriesArrayListWardrobe);
                WardrodeClothesAdapter newClothesAdapter = new WardrodeClothesAdapter(sortAccessoriesArrayListWardrobe, itemClickListener,userInformation,fragmentString);
                wardrobeRecyclerView.setAdapter(newClothesAdapter);
            }
        }
    }
}