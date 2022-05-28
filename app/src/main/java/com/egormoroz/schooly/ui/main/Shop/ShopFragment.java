package com.egormoroz.schooly.ui.main.Shop;

import android.content.SharedPreferences;
import android.media.TimedText;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.RecentMethods;

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.ui.coins.CoinsFragmentSecond;
import com.egormoroz.schooly.ui.coins.CoinsMainFragment;
import com.egormoroz.schooly.ui.main.ChatsFragment;
import com.egormoroz.schooly.ui.main.DialogsActivity;
import com.egormoroz.schooly.ui.main.GroupsFragment;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopFragment extends Fragment {
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;

    public ShopFragment(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
    }

    public static ShopFragment newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        return new ShopFragment(userInformation,bundle,fragment);
    }

    FirebaseModel firebaseModel=new FirebaseModel();
    TextView coinsshop,notFound;
    private ViewPager2 viewPager;
    FragmentAdapter fragmentAdapter;
    ImageView basket;
    String version;
    EditText searchClothes;
    static String editGetText;
    RecyclerView searchRecycler;
    ArrayList<Clothes> clothesFromBase,allClothes;
    TabLayout tabLayout;
    PopularClothesAdapter.ItemClickListener itemClickListenerPopular;
    LinearLayout coinsLinear;
    int tabLayoutPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shop, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        coinsshop=root.findViewById(R.id.schoolycoinshopfrag);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putString("EDIT_SHOP_TAG",searchClothes.getText().toString().trim());
        bundle.putInt("TAB_INT_SHOP", tabLayoutPosition);
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        version= userInformation.getVersion();
        searchRecycler=view.findViewById(R.id.searchRecycler);
        notFound=view.findViewById(R.id.notFound);
        searchClothes=view.findViewById(R.id.searchClothes);
        tabLayout = view.findViewById(R.id.tabLayoutShop);
        viewPager=view.findViewById(R.id.frcontshop);
        itemClickListenerPopular=new PopularClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                ((MainActivity)getActivity()).setCurrentFragment(ViewingClothesPopular.newInstance(userInformation,bundle,ShopFragment.newInstance(userInformation,bundle,fragment)));
            }
        };
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                RecentMethods.setCurrentFragment(fragment,getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        ImageView backtoprofileshop=view.findViewById(R.id.back_toprofile);
        backtoprofileshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(fragment,getActivity());
            }
        });
        coinsLinear=view.findViewById(R.id.linearCoins);
        coinsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(CoinsFragmentSecond.newInstance(ShopFragment.newInstance(userInformation,bundle,fragment),userInformation,bundle),getActivity());
            }
        });

        if (bundle!=null){
            tabLayoutPosition=bundle.getInt("TAB_INT_SHOP");
            if(bundle.getString("EDIT_SHOP_TAG")!=null){
                String bundleEditText=bundle.getString("EDIT_SHOP_TAG").trim();
                if(bundleEditText.length()!=0){
                    searchClothes.setText(bundleEditText);
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    loadSearchClothes(bundleEditText);
                }
            }
        }
        searchClothes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editGetText=searchClothes.getText().toString().trim();
                editGetText=editGetText.toLowerCase();
                if (editGetText.length()>0) {
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    loadSearchClothes(editGetText);
                }else if(editGetText.length()==0){
                    viewPager.setVisibility(View.VISIBLE);
                    searchRecycler.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    notFound.setVisibility(View.GONE);
                    FragmentManager fm = getChildFragmentManager();
                    fragmentAdapter = new FragmentAdapter(fm, getLifecycle());
                    viewPager.setAdapter(fragmentAdapter);
                    viewPager.setCurrentItem(tabLayoutPosition, false);

                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            tabLayoutPosition=tab.getPosition();
                            viewPager.setCurrentItem(tabLayoutPosition);
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

                    tabLayout.selectTab(tabLayout.getTabAt(tabLayoutPosition));
                    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            tabLayoutPosition=position;
                            tabLayout.selectTab(tabLayout.getTabAt(tabLayoutPosition));
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        coinsshop.setText(String.valueOf(userInformation.getmoney()));
        FragmentManager fm = getChildFragmentManager();
        fragmentAdapter = new FragmentAdapter(fm, getLifecycle());
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(tabLayoutPosition, false);

        tabLayout.addTab(tabLayout.newTab().setText("Главная"));
        tabLayout.addTab(tabLayout.newTab().setText("Эксклюзивная"));
        tabLayout.addTab(tabLayout.newTab().setText("Обувь"));
        tabLayout.addTab(tabLayout.newTab().setText("Одежда"));
        tabLayout.addTab(tabLayout.newTab().setText("Головные уборы"));
        tabLayout.addTab(tabLayout.newTab().setText("Акскссуары"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayoutPosition=tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.selectTab(tabLayout.getTabAt(tabLayoutPosition));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayoutPosition=position;
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


        basket=view.findViewById(R.id.basket);
        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(BasketFragment.newInstance(userInformation, bundle,fragment),getActivity());
            }
        });

    }

    public void loadSearchClothes(String editTextText){
        if(bundle!=null){
            if(bundle.getSerializable("ALL_CLOTHES")==null){
                firebaseModel.getReference("AppData/Clothes/AllClothes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot=task.getResult();
                            clothesFromBase=new ArrayList<>();
                            allClothes=new ArrayList<>();
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
                                clothes.setModel(snap.child("model").getValue(String.class));
                                clothes.setBodyType(snap.child("bodyType").getValue(String.class));
                                clothes.setUid(snap.child("uid").getValue(String.class));
                                clothes.setExclusive(snap.child("exclusive").getValue(String.class));
                                allClothes.add(clothes);
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
                            bundle.putSerializable("ALL_CLOTHES", allClothes);
                            if (clothesFromBase.size()==0){
                                searchRecycler.setVisibility(View.GONE);
                                notFound.setVisibility(View.VISIBLE);
                            }else {
                                searchRecycler.setVisibility(View.VISIBLE);
                                PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(clothesFromBase,itemClickListenerPopular,userInformation);
                                searchRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                searchRecycler.setAdapter(popularClothesAdapter);
                                notFound.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }else{
                allClothes= (ArrayList<Clothes>) bundle.getSerializable("ALL_CLOTHES");
                clothesFromBase=new ArrayList<>();
                for (int i=0;i<allClothes.size();i++) {
                    Clothes clothes = allClothes.get(i);
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
                    searchRecycler.setVisibility(View.GONE);
                    notFound.setVisibility(View.VISIBLE);
                }else {
                    searchRecycler.setVisibility(View.VISIBLE);
                    PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(clothesFromBase,itemClickListenerPopular,userInformation);
                    searchRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    searchRecycler.setAdapter(popularClothesAdapter);
                    notFound.setVisibility(View.GONE);
                }
            }
        }
    }

//    public void loadModelInBase(){
//        Clothes clothes=new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                , 120,"Jordan 1");
//        Clothes clothes2=new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                , 100,"Jordan 4");
//        Clothes clothes3=new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                , 10,"Jordan 6");
//        firebaseModel.getReference().child("AppData").child("Clothes")
//                .child("AllClothes").child(clothes.getClothesTitle()).setValue(clothes);
//        firebaseModel.getReference().child("AppData").child("Clothes")
//                .child("Popular").child(clothes.getClothesTitle()).setValue(clothes);
//        firebaseModel.getReference().child("AppData").child("Clothes")
//                .child("AllNew").child(clothes.getClothesTitle()).setValue(clothes);
////        firebaseModel.getReference().child("AppData").child("Clothes")
////                .child("AllClothes").child(clothes2.getClothesTitle()).setValue(clothes2);
//        firebaseModel.getReference().child("AppData").child("Clothes")
//                .child("Popular").child(clothes2.getClothesTitle()).setValue(clothes2);
//        firebaseModel.getReference().child("AppData").child("Clothes")
//                .child("AllNew").child(clothes2.getClothesTitle()).setValue(clothes2);
//        firebaseModel.getReference().child("AppData").child("Clothes")
//                .child("AllNew").child(clothes3.getClothesTitle()).setValue(clothes3);
//    }


    public class FragmentAdapter extends FragmentStateAdapter {

        public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment ( int position){
            switch (position){
                case 0:
                    return new PopularFragment(userInformation, bundle,fragment);
                case 1:
                    return new ExclusiveFragment(version,userInformation,bundle,fragment);
                case 2:
                    return new ShoesFargment(userInformation,bundle,fragment);
                case 3:
                    return new ClothesFragment(userInformation,bundle,fragment);
                case 4:
                    return new HatsFragment(userInformation,bundle,fragment);
                case 5:
                    return new AccessoriesFragment(userInformation,bundle,fragment);
            }
            return null;
        }


        @Override
        public int getItemCount() {
            return 6;
        }

    }


}