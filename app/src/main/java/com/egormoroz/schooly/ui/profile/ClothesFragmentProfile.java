package com.egormoroz.schooly.ui.profile;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.GenderFragment;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.MyClothes.CreateClothesFragment;
import com.egormoroz.schooly.ui.main.MyClothes.MyClothesAdapter;
import com.egormoroz.schooly.ui.main.MyClothes.MyClothesFragment;
import com.egormoroz.schooly.ui.main.MyClothes.ViewingMyClothes;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ClothesFragmentProfile extends Fragment {

    RecyclerView looksRecycler;
    TextView createNewLookText,createNewLook;
    ClothesAdapter.ItemClickListener itemClickListener;
    FirebaseModel firebaseModel=new FirebaseModel();
    Fragment fragment;
    String type,nick;
    UserInformation userInformation;
    Bundle bundle;


    public ClothesFragmentProfile(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment = fragment;
        this.type = type;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static ClothesFragmentProfile newInstance(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new ClothesFragmentProfile(type,fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpager_profile, container, false);
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
        nick=userInformation.getNick();
        itemClickListener=new ClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                RecentMethods.setCurrentFragment(ClothesViewingProfile.newInstance(type,ProfileFragment.newInstance(type,nick,fragment,userInformation,bundle),userInformation,bundle), getActivity());
            }
        };
        createNewLook=view.findViewById(R.id.CreateYourLook);
        createNewLookText=view.findViewById(R.id.textCreateYourLook);
        looksRecycler=view.findViewById(R.id.Recycler);
        putClothesInAdapter();

    }
    public void putClothesInAdapter(){
        if(userInformation.getMyClothes()==null){
            RecentMethods.getMyClothes(nick, firebaseModel, new Callbacks.GetClothes() {
                @Override
                public void getClothes(ArrayList<Clothes> allClothes) {
                    if (allClothes.size()==0){
                        createNewLookText.setVisibility(View.VISIBLE);
                        createNewLookText.setText(R.string.designyourclothes);
                        createNewLook.setVisibility(View.VISIBLE);
                        createNewLook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecentMethods.setCurrentFragment(CreateClothesFragment.newInstance(ProfileFragment.newInstance(type, nick, fragment,userInformation,bundle),userInformation,bundle), getActivity());
                            }
                        });
                        looksRecycler.setVisibility(View.GONE);
                    }else {
                        looksRecycler.setVisibility(View.VISIBLE);
                        Collections.reverse(allClothes);
                        ClothesAdapter clothesAdapter=new ClothesAdapter(allClothes,itemClickListener);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        layoutManager.setReverseLayout(true);
                        layoutManager.setStackFromEnd(true);
                        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(), 2);
                        looksRecycler.setLayoutManager(gridLayoutManager);
                        looksRecycler.setAdapter(clothesAdapter);
                    }
                }
            });
        }else {
            if (userInformation.getMyClothes().size()==0){
                createNewLookText.setVisibility(View.VISIBLE);
                createNewLookText.setText(R.string.designyourclothes);
                createNewLook.setVisibility(View.VISIBLE);
                createNewLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.setCurrentFragment(CreateClothesFragment.newInstance(ProfileFragment.newInstance(type, nick, fragment,userInformation,bundle),userInformation,bundle), getActivity());
                    }
                });
                looksRecycler.setVisibility(View.GONE);
            }else {
                looksRecycler.setVisibility(View.VISIBLE);
                Collections.reverse(userInformation.getMyClothes());
                ClothesAdapter clothesAdapter=new ClothesAdapter(userInformation.getMyClothes(),itemClickListener);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(), 2);
                looksRecycler.setLayoutManager(gridLayoutManager);
                looksRecycler.setAdapter(clothesAdapter);
            }
        }
    }
}
