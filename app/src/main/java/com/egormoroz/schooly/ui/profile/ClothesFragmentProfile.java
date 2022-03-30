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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClothesFragmentProfile extends Fragment {

    RecyclerView looksRecycler;
    TextView createNewLookText,createNewLook;
    ClothesAdapter.ItemClickListener itemClickListener;
    FirebaseModel firebaseModel=new FirebaseModel();

    Fragment fragment;
    String type;


    public ClothesFragmentProfile(String type,Fragment fragment) {
        this.fragment = fragment;
        this.type = type;
    }

    public static ClothesFragmentProfile newInstance(String type,Fragment fragment) {
        return new ClothesFragmentProfile(type,fragment);

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
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);


        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
            @Override
            public void PassUserNick(String nick) {
                itemClickListener=new ClothesAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(Clothes clothes) {
                        RecentMethods.setCurrentFragment(ClothesViewingProfile.newInstance(type,ProfileFragment.newInstance(type,nick,ClothesFragmentProfile.newInstance(type,fragment))), getActivity());
                    }
                };
            }
        });
        createNewLook=view.findViewById(R.id.CreateYourLook);
        createNewLookText=view.findViewById(R.id.textCreateYourLook);
        looksRecycler=view.findViewById(R.id.Recycler);
        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
            @Override
            public void PassUserNick(String nick) {
                Query query=firebaseModel.getUsersReference().child(nick)
                        .child("myClothes");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Clothes> clothesFromBase=new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Clothes clothes = new Clothes();
                            clothes.setClothesImage(snap.child("clothesImage").getValue(String.class));
                            clothes.setClothesPrice(snap.child("clothesPrice").getValue(Long.class));
                            clothes.setPurchaseNumber(snap.child("purchaseNumber").getValue(Long.class));
                            clothes.setClothesType(snap.child("clothesType").getValue(String.class));
                            clothes.setClothesTitle(snap.child("clothesTitle").getValue(String.class));
                            clothes.setCreator(snap.child("creator").getValue(String.class));
                            clothes.setCurrencyType(snap.child("currencyType").getValue(String.class));
                            clothes.setDescription(snap.child("description").getValue(String.class));
                            clothes.setPurchaseToday(snap.child("purchaseToday").getValue(Long.class));
                            clothes.setBodyType(snap.child("bodyType").getValue(String.class));
                            clothes.setModel(snap.child("model").getValue(String.class));
                            clothes.setUid(snap.child("uid").getValue(String.class));
                            clothesFromBase.add(clothes);
                        }
                        if (clothesFromBase.size()==0){
                            createNewLookText.setVisibility(View.VISIBLE);
                            createNewLookText.setText("Создай свою одежду!");
                            createNewLook.setVisibility(View.VISIBLE);
                            RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
                                @Override
                                public void PassUserNick(String nick) {
                                    createNewLook.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            RecentMethods.setCurrentFragment(CreateClothesFragment.newInstance(ProfileFragment.newInstance("user", nick, ClothesFragmentProfile.newInstance(type,fragment))), getActivity());
                                        }
                                    });
                                }
                            });
                            looksRecycler.setVisibility(View.GONE);
                        }else {
                            looksRecycler.setVisibility(View.VISIBLE);
                            ClothesAdapter clothesAdapter=new ClothesAdapter(clothesFromBase,itemClickListener);
                            looksRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
                            looksRecycler.setAdapter(clothesAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}
