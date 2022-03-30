package com.egormoroz.schooly.ui.profile.Wardrobe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

public class ViewingClothesWardrobe extends Fragment {
  String type;
  Fragment fragment;

  public ViewingClothesWardrobe(String type,Fragment fragment) {
    this.type = type;
    this.fragment=fragment;
  }

  public static ViewingClothesWardrobe newInstance(String type,Fragment fragment) {
    return new ViewingClothesWardrobe(type,fragment);

  }

  WardrobeClothesAdapter.ItemClickListener itemClickListener;
  TextView clothesPriceCV,clothesTitleCV,schoolyCoinCV;
  ImageView clothesImageCV,backToShop;
  long schoolyCoins,clothesPrise;
  Clothes clothesViewing;
  private FirebaseModel firebaseModel = new FirebaseModel();
  DataSnapshot snap;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_viewingclotheswardrobe, container, false);
    BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
    bnv.setVisibility(bnv.GONE);
    firebaseModel.initAll();
    return root;

  }

  @Override
  public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
    super.onViewCreated(view, savedInstanceState);
    clothesImageCV=view.findViewById(R.id.clothesImagecv);
    clothesTitleCV=view.findViewById(R.id.clothesTitlecv);
    backToShop=view.findViewById(R.id.back_toshop);
    backToShop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        RecentMethods.setCurrentFragment(WardrobeFragment.newInstance(type,fragment), getActivity());
      }
    });

    WardrobeClothesAdapter.singeClothesInfo(new WardrobeClothesAdapter.ItemClickListener() {
      @Override
      public void onItemClick(Clothes clothes) {
        clothesViewing=clothes;
        clothesTitleCV.setText(clothes.getClothesTitle());
        clothesPrise=clothes.getClothesPrice();
        Picasso.get().load(clothes.getClothesImage()).into(clothesImageCV);
      }
    });
  }


}