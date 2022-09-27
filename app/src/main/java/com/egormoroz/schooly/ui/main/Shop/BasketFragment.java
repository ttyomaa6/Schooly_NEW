package com.egormoroz.schooly.ui.main.Shop;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.coins.CoinsFragmentSecond;
import com.egormoroz.schooly.ui.coins.CoinsMainFragment;
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BasketFragment extends Fragment {
  UserInformation userInformation;
  Bundle bundle;
  Fragment fragment;

  public BasketFragment(UserInformation userInformation,Bundle bundle,Fragment fragment) {
    this.userInformation=userInformation;
    this.bundle=bundle;
    this.fragment=fragment;
  }

  public static BasketFragment newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment) {
    return new BasketFragment(userInformation,bundle,fragment);
  }

  FirebaseModel firebaseModel=new FirebaseModel();
  TextView numberOfClothes,schoolyCoin,notFound;
  ImageView backtoshop;
  RecyclerView basketRecycler;
  ArrayList<Clothes> clothesArrayList=new ArrayList<Clothes>();
  ArrayList<Clothes> clothesFromBase;
  static BasketAdapter.ItemClickListener onItemClick;
  static String editGetText;
  LinearLayout coinsLinear;
  EditText editText;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_basket, container, false);
    BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
    bnv.setVisibility(bnv.GONE);
    firebaseModel.initAll();
    return root;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    bundle.putString("EDIT_BASKET_TAG",editText.getText().toString().trim());
  }


  @Override
  public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
    super.onViewCreated(view, savedInstanceState);
    backtoshop=view.findViewById(R.id.back_toshopfrombasket);
    editText=view.findViewById(R.id.searchClothes);
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        RecentMethods.setCurrentFragment(ShopFragment.newInstance(userInformation,bundle,fragment), getActivity());
      }
    };

    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

    backtoshop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        RecentMethods.setCurrentFragment(ShopFragment.newInstance(userInformation,bundle,fragment), getActivity());
      }
    });
    onItemClick=new BasketAdapter.ItemClickListener() {
      @Override
      public void onItemClick(Clothes clothes) {
        RecentMethods.setCurrentFragment(ViewingClothesBasket.newInstance(userInformation,bundle,BasketFragment.newInstance(userInformation, bundle, fragment)),getActivity());
      }
    };
    coinsLinear=view.findViewById(R.id.linearCoinsBasket);
    schoolyCoin=view.findViewById(R.id.schoolycoinbasketfrag);
    schoolyCoin.setText(String.valueOf(userInformation.getmoney()));
    notFound=view.findViewById(R.id.notFound);
    coinsLinear.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        RecentMethods.setCurrentFragment(CoinsFragmentSecond.newInstance(BasketFragment.newInstance(userInformation,bundle,fragment),userInformation,bundle), getActivity());
      }
    });
    numberOfClothes=view.findViewById(R.id.numberofclothes);
    numberOfClothes.setText(getContext().getResources().getText(R.string.itemsincart)+String.valueOf(userInformation.getClothesBasket().size()));
    basketRecycler=view.findViewById(R.id.basketrecycler);
    if (bundle!=null){
      if(bundle.getString("EDIT_BASKET_TAG")!=null) {
        String bundleEditText = bundle.getString("EDIT_BASKET_TAG");
        if (bundleEditText.length() != 0) {
          editText.setText(bundleEditText);
          basketRecycler.setVisibility(View.GONE);
          loadSearchClothes(bundleEditText);
        }else {
          loadClothesFromBasket();
        }
      }else {
        loadClothesFromBasket();
      }
    }
    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        editGetText=editText.getText().toString();
        editGetText=editGetText.toLowerCase();
        if (editGetText.length()>0) {
          loadSearchClothes(editGetText);
        }else {
          notFound.setVisibility(View.GONE);
          basketRecycler.setVisibility(View.VISIBLE);
          loadClothesFromBasket();
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

  }

  public void loadClothesFromBasket(){
    if(userInformation.getClothesBasket()==null){
      RecentMethods.getClothesInBasket(userInformation.getNick(), firebaseModel, new Callbacks.GetClothes() {
        @Override
        public void getClothes(ArrayList<Clothes> allClothes) {
          clothesArrayList.addAll(allClothes);
          numberOfClothes.setText(getContext().getResources().getText(R.string.itemsincart)+String.valueOf(clothesArrayList.size()));
          userInformation.setClothesBasket(allClothes);
          if(clothesArrayList.size()==0){
            notFound.setVisibility(View.VISIBLE);
            basketRecycler.setVisibility(View.GONE);
          }else {
            notFound.setVisibility(View.GONE);
            basketRecycler.setVisibility(View.VISIBLE);
            BasketAdapter basketAdapter=new BasketAdapter(clothesArrayList,onItemClick);
            basketRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            basketRecycler.setAdapter(basketAdapter);
          }
        }
      });
    }else {
      numberOfClothes.setText(getContext().getResources().getText(R.string.itemsincart)+String.valueOf(userInformation.getClothesBasket().size()));
      userInformation.setClothesBasket(userInformation.getClothesBasket());
      if(userInformation.getClothesBasket().size()==0){
        notFound.setVisibility(View.VISIBLE);
        basketRecycler.setVisibility(View.GONE);
      }else {
        notFound.setVisibility(View.GONE);
        basketRecycler.setVisibility(View.VISIBLE);
        BasketAdapter basketAdapter=new BasketAdapter(userInformation.getClothesBasket(),onItemClick);
        basketRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        basketRecycler.setAdapter(basketAdapter);
      }
    }
  }

  public void loadSearchClothes(String editTextText){
    if(userInformation.getClothesBasket()!=null){
      clothesFromBase=new ArrayList<>();
      for (int i=0;i<userInformation.getClothesBasket().size();i++) {
        Clothes clothes = userInformation.getClothesBasket().get(i);
        String clothesTitle=clothes.getClothesTitle();
        String title=clothesTitle;
        int valueLetters=editTextText.length();
        title=title.toLowerCase();
        if(title.length()<valueLetters){
          if(title.equals(editTextText))
            clothesFromBase.add(clothes);
        }else{
          title=title.substring(0, valueLetters);
          if(title.equals(editTextText))
            clothesFromBase.add(clothes);
        }
      }
      if (clothesFromBase.size()==0){
        basketRecycler.setVisibility(View.GONE);
        notFound.setVisibility(View.VISIBLE);
      }else {
        basketRecycler.setVisibility(View.VISIBLE);
        BasketAdapter basketAdapter=new BasketAdapter(clothesFromBase,onItemClick);
        basketRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        basketRecycler.setAdapter(basketAdapter);
        notFound.setVisibility(View.GONE);
      }
    }else{
      firebaseModel.getUsersReference().child(userInformation.getNick()).child("basket").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DataSnapshot> task) {
          if(task.isSuccessful()){
            DataSnapshot snapshot=task.getResult();
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
                String clothesTitle=clothes.getClothesTitle();
                String title=clothesTitle;
                int valueLetters=editTextText.length();
                title=title.toLowerCase();
                if(title.length()<valueLetters){
                  if(title.equals(editTextText))
                    clothesFromBase.add(clothes);
                }else{
                  title=title.substring(0, valueLetters);
                  if(title.equals(editTextText))
                    clothesFromBase.add(clothes);
                }
              }

            }
            if (clothesFromBase.size()==0){
              basketRecycler.setVisibility(View.GONE);
              notFound.setVisibility(View.VISIBLE);
            }else {
              basketRecycler.setVisibility(View.VISIBLE);
              BasketAdapter basketAdapter=new BasketAdapter(clothesFromBase,onItemClick);
              basketRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
              basketRecycler.setAdapter(basketAdapter);
              notFound.setVisibility(View.GONE);
            }
          }
        }
      });

    }
  }

}