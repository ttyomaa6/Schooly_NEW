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
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.ShopFragment;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ClothesFragmentProfileOther extends Fragment {

    RecyclerView recyclerOther;
    TextView noLooksOther;
    FirebaseModel firebaseModel=new FirebaseModel();
    String otherUserNick,nick;
    ClothesAdapterOther.ItemClickListener itemClickListener;
    UserInformation info;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;
    ArrayList<Clothes> clothesFromBase;

    public ClothesFragmentProfileOther(String otherUserNick,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.otherUserNick = otherUserNick;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static ClothesFragmentProfileOther newInstance(String otherUserNick,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new ClothesFragmentProfileOther(otherUserNick,fragment,userInformation,bundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpager_profileother, container, false);
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
        noLooksOther=view.findViewById(R.id.noLooks);
        recyclerOther=view.findViewById(R.id.Recycler);
        itemClickListener=new ClothesAdapterOther.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                RecentMethods.setCurrentFragment(ClothesViewingProfileOther.newInstance(ProfileFragment.newInstance("other", otherUserNick, fragment,userInformation,bundle),userInformation,bundle), getActivity());
            }
        };
        setClothesInAdapter();
    }

    public void setClothesInAdapter(){
        if(bundle!=null){
            if(bundle.getSerializable(otherUserNick+"CLOTHES_OTHER_BUNDLE")!=null){
                clothesFromBase= (ArrayList<Clothes>) bundle.getSerializable(otherUserNick+"CLOTHES_OTHER_BUNDLE");
                if (clothesFromBase.size()==0){
                    noLooksOther.setVisibility(View.VISIBLE);
                    noLooksOther.setText(otherUserNick+" "+getContext().getResources().getString(R.string.didnotdesign));
                    recyclerOther.setVisibility(View.GONE);
                }else {
                    recyclerOther.setVisibility(View.VISIBLE);
                    Collections.reverse(clothesFromBase);
                    ClothesAdapterOther clothesAdapter=new ClothesAdapterOther(clothesFromBase,itemClickListener);
                    GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(), 2);
                    recyclerOther.setLayoutManager(gridLayoutManager);
                    recyclerOther.setAdapter(clothesAdapter);
                }
            }else{
                firebaseModel.getUsersReference().child(otherUserNick)
                        .child("myClothes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            DataSnapshot snapshot= task.getResult();
                            clothesFromBase=new ArrayList<>();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                Clothes clothes = new Clothes();
                                if(snap.child("clothesPrice").exists()&&snap.child("purchaseNumber").exists()&&snap.child("purchaseToday").exists()&&
                                        snap.child("x").exists() &&snap.child("y").exists()&&
                                        snap.child("z").exists()&&snap.child("transformRatio").exists()
                                ) {
                                    clothes.setClothesImage(snap.child("clothesImage").getValue(String.class));
                                    clothes.setClothesPrice(snap.child("clothesPrice").getValue(Long.class));
                                    clothes.setPurchaseNumber(snap.child("purchaseNumber").getValue(Long.class));
                                    clothes.setClothesType(snap.child("clothesType").getValue(String.class));
                                    clothes.setClothesTitle(snap.child("clothesTitle").getValue(String.class));
                                    clothes.setCreator(snap.child("creator").getValue(String.class));
                                    clothes.setCurrencyType(snap.child("currencyType").getValue(String.class));
                                    clothes.setDescription(snap.child("description").getValue(String.class));
                                    clothes.setPurchaseToday(snap.child("purchaseToday").getValue(Long.class));
                                    clothes.setModel(snap.child("model").getValue(String.class));
                                    clothes.setBodyType(snap.child("bodyType").getValue(String.class));
                                    clothes.setUid(snap.child("uid").getValue(String.class));
                                    clothes.setExclusive(snap.child("exclusive").getValue(String.class));
                                    clothes.setX(snap.child("x").getValue(Float.class));
                                    clothes.setY(snap.child("y").getValue(Float.class));
                                    clothes.setZ(snap.child("z").getValue(Float.class));
                                    clothes.setTransformRatio(snap.child("transformRatio").getValue(Float.class));
                                    clothesFromBase.add(clothes);
                                }
                            }
                            bundle.putSerializable(otherUserNick+"CLOTHES_OTHER_BUNDLE",clothesFromBase);
                            if (clothesFromBase.size()==0){
                                noLooksOther.setVisibility(View.VISIBLE);
                                noLooksOther.setText(otherUserNick+" "+getContext().getResources().getString(R.string.didnotdesign));
                                recyclerOther.setVisibility(View.GONE);
                            }else {
                                recyclerOther.setVisibility(View.VISIBLE);
                                Collections.reverse(clothesFromBase);
                                ClothesAdapterOther clothesAdapter=new ClothesAdapterOther(clothesFromBase,itemClickListener);
                                GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(), 2);
                                recyclerOther.setLayoutManager(gridLayoutManager);
                                recyclerOther.setAdapter(clothesAdapter);
                            }
                        }
                    }
                });
            }
        }
    }

}
