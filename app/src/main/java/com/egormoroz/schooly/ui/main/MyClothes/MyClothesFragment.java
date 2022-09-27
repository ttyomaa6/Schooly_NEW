package com.egormoroz.schooly.ui.main.MyClothes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.PopularClothesAdapter;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothesPopular;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.profile.Wardrobe.AcceptNewLook;
import com.egormoroz.schooly.ui.profile.Wardrobe.CreateLookFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MyClothesFragment extends Fragment {

    FirebaseModel firebaseModel=new FirebaseModel();
    RecyclerView recyclerMyClothes;
    RelativeLayout createClothesBig,relativeFirstClothes,createClothes;
    TextView totalProfitText,totalProfit,totalProfitDollar,clothes,totalPurchaseText
            ,totalPurchase,notFound;
    MyClothesAdapter.ItemClickListener itemClickListener;
    long totalProfitLong,totalPurchaseLong,totalProfitDollarLong;
    String totalProfitString,totalPurchaseString,totalProfitDollarString,editGetText,nick;
    ImageView schoolyCoin;
    EditText searchMyClothes;
    LinearLayout linearSearch;
    UserInformation userInformation;
    ArrayList<Clothes> clothesArrayList;
    Bundle bundle;

    public MyClothesFragment(ArrayList<Clothes> clothesArrayList,long totalProfitLong,long totalPurchaseLong,
                             long totalProfitDollarLong,UserInformation userInformation,Bundle bundle) {
        this.clothesArrayList = clothesArrayList;
        this.totalProfitLong=totalProfitLong;
        this.totalPurchaseLong=totalPurchaseLong;
        this.totalProfitDollarLong=totalProfitDollarLong;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static MyClothesFragment newInstance(ArrayList<Clothes> clothesArrayList,long totalProfitLong,long totalPurchaseLong,
                                                long totalProfitDollarLong,UserInformation userInformation,Bundle bundle) {
        return new MyClothesFragment(clothesArrayList,totalProfitLong,totalPurchaseLong,totalProfitDollarLong
        ,userInformation,bundle);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putString("EDIT_MYCLOTHES_TAG",searchMyClothes.getText().toString().trim());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_myclothes, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
//        AppBarLayout abl = getActivity().findViewById(R.id.AppBarLayout);
//        abl.setVisibility(abl.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        recyclerMyClothes=view.findViewById(R.id.recyclerMyClothes);
        createClothesBig=view.findViewById(R.id.createBigButtonRecycler);
        createClothes=view.findViewById(R.id.createClothesButton);
        totalProfitText=view.findViewById(R.id.totalProfitText);
        searchMyClothes=view.findViewById(R.id.searchMyClothes);
        totalProfit=view.findViewById(R.id.totalProfit);
        linearSearch=view.findViewById(R.id.linearsearch);
        notFound=view.findViewById(R.id.noClothes);
        totalPurchaseText=view.findViewById(R.id.totalPurchaseText);
        totalPurchase=view.findViewById(R.id.totalPurchase);
        totalProfitDollar=view.findViewById(R.id.totalProfitDollar);
        schoolyCoin=view.findViewById(R.id.schoolycoinGreen);
        clothes=view.findViewById(R.id.clothes);
        relativeFirstClothes=view.findViewById(R.id.relativeFirstClothes);
        itemClickListener=new MyClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                RecentMethods.setCurrentFragment(ViewingMyClothes.newInstance(MyClothesFragment.newInstance(clothesArrayList
                        ,totalProfitLong,totalPurchaseLong,totalProfitDollarLong,userInformation,bundle),userInformation,bundle), getActivity());
            }
        };
        ImageView backtomain=view.findViewById(R.id.back_tomain);
        backtomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
                    @Override
                    public void PassUserNick(String nick) {
                        RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation,bundle), getActivity());
                    }
                });
            }
        });
        searchMyClothes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editGetText=searchMyClothes.getText().toString().trim();
                editGetText=editGetText.toLowerCase();
                if (editGetText.length()>0) {
                    searchMyClothes(editGetText);
                }else if(editGetText.length()==0){
                    recyclerMyClothes.setVisibility(View.VISIBLE);
                    notFound.setVisibility(View.GONE);
                    MyClothesAdapter myClothesAdapter=new MyClothesAdapter(userInformation.getMyClothes(),itemClickListener,userInformation);
                    recyclerMyClothes.setAdapter(myClothesAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getMyClothes();
        setCounts();
    }

    public void getMyClothes(){
        if(clothesArrayList.size()==0){
            recyclerMyClothes.setVisibility(View.GONE);
            totalProfitText.setVisibility(View.GONE);
            totalProfit.setVisibility(View.GONE);
            totalPurchaseText.setVisibility(View.GONE);
            totalPurchase.setVisibility(View.GONE);
            linearSearch.setVisibility(View.GONE);
            schoolyCoin.setVisibility(View.GONE);
            clothes.setVisibility(View.GONE);
            searchMyClothes.setVisibility(View.GONE);
            createClothesBig.setVisibility(View.VISIBLE);
            relativeFirstClothes.setVisibility(View.VISIBLE);
            createClothes.setVisibility(View.GONE);
            createClothesBig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecentMethods.setCurrentFragment(CreateClothesFragment.newInstance(MyClothesFragment.newInstance(clothesArrayList
                            ,totalProfitLong,totalPurchaseLong,totalProfitDollarLong,userInformation,bundle),userInformation,bundle), getActivity());
                }
            });
        }else {
            recyclerMyClothes.setVisibility(View.VISIBLE);
            createClothesBig.setVisibility(View.GONE);
            relativeFirstClothes.setVisibility(View.GONE);
            createClothes.setVisibility(View.VISIBLE);
            if (bundle!=null){
                if(bundle.getString("EDIT_MYCLOTHES_TAG")!=null) {
                    String bundleEditText = bundle.getString("EDIT_MYCLOTHES_TAG").trim();
                    if (bundleEditText.length() != 0) {
                        searchMyClothes.setText(bundleEditText);
                        searchMyClothes(bundleEditText);
                    }else{
                        MyClothesAdapter myClothesAdapter=new MyClothesAdapter(clothesArrayList,itemClickListener,userInformation);
                        recyclerMyClothes.setAdapter(myClothesAdapter);
                    }
                }else{
                    MyClothesAdapter myClothesAdapter=new MyClothesAdapter(clothesArrayList,itemClickListener,userInformation);
                    recyclerMyClothes.setAdapter(myClothesAdapter);
                }
            }
            clothes.setText(getContext().getResources().getText(R.string.clothes)+" "+String.valueOf(userInformation.getMyClothes().size())+":");
            createClothes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecentMethods.setCurrentFragment(CreateClothesFragment.newInstance(MyClothesFragment.newInstance(clothesArrayList
                            ,totalProfitLong,totalPurchaseLong,totalProfitDollarLong,userInformation,bundle),userInformation,bundle), getActivity());
                }
            });
        }
    }

    public void searchMyClothes(String editTextText){
        ArrayList<Clothes> clothesFromBase = new ArrayList<>();
        for(int i=0;i<userInformation.getMyClothes().size();i++){
            Clothes clothes=userInformation.getMyClothes().get(i);
            String clothesTitle = clothes.getClothesTitle();
            String title = clothesTitle;
            int valueLetters = editTextText.length();
            title = title.toLowerCase();
            if (title.length() < valueLetters) {
                if (title.equals(editTextText))
                    clothesFromBase.add(clothes);
            } else {
                title = title.substring(0, valueLetters);
                if (title.equals(editTextText))
                    clothesFromBase.add(clothes);
            }

        }
        if (clothesFromBase.size() == 0) {
            recyclerMyClothes.setVisibility(View.GONE);
            notFound.setVisibility(View.VISIBLE);
        } else {
            recyclerMyClothes.setVisibility(View.VISIBLE);
            MyClothesAdapter myClothesAdapter=new MyClothesAdapter(clothesFromBase,itemClickListener,userInformation);
            recyclerMyClothes.setAdapter(myClothesAdapter);
            notFound.setVisibility(View.GONE);
        }
    }

    public  void setCounts(){
        if(totalPurchaseLong==0 || totalProfitDollarLong==0){
            totalProfitDollar.setVisibility(View.GONE);
        }
        if(totalProfitLong==0 && totalProfitDollarLong>0){
            totalProfit.setVisibility(View.GONE);
            schoolyCoin.setVisibility(View.GONE);
            totalProfitDollarString = String.valueOf(totalProfitDollarLong);
            if (totalProfitDollarLong < 1000) {
                totalProfitDollar.setText("+" + totalProfitDollarString+"$");
            } else if (totalProfitDollarLong > 1000 && totalProfitDollarLong < 10000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 1) + "." + totalProfitDollarString.substring(1, 2) + "K"+"$");
            } else if (totalProfitDollarLong > 10000 && totalProfitDollarLong < 100000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 2) + "." + totalProfitDollarString.substring(2, 3) + "K"+"$");
            } else if (totalProfitDollarLong > 10000 && totalProfitDollarLong < 100000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 2) + "." + totalProfitDollarString.substring(2, 3) + "K"+"$");
            } else if (totalProfitDollarLong > 100000 && totalProfitDollarLong < 1000000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 3) + "K"+"$");
            } else if (totalProfitDollarLong > 1000000 && totalProfitDollarLong < 10000000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 1) + "KK"+"$");
            } else if (totalProfitDollarLong > 10000000 && totalProfitDollarLong < 100000000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 2) + "KK"+"$");
            }
        }else{
            totalProfitDollarString = String.valueOf(totalProfitDollarLong);
            if (totalProfitDollarLong < 1000) {
                totalProfitDollar.setText("+" + totalProfitDollarString+"$");
            } else if (totalProfitDollarLong > 1000 && totalProfitDollarLong < 10000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 1) + "." + totalProfitDollarString.substring(1, 2) + "K"+"$");
            } else if (totalProfitDollarLong > 10000 && totalProfitDollarLong < 100000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 2) + "." + totalProfitDollarString.substring(2, 3) + "K"+"$");
            } else if (totalProfitDollarLong > 10000 && totalProfitDollarLong < 100000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 2) + "." + totalProfitDollarString.substring(2, 3) + "K"+"$");
            } else if (totalProfitDollarLong > 100000 && totalProfitDollarLong < 1000000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 3) + "K"+"$");
            } else if (totalProfitDollarLong > 1000000 && totalProfitDollarLong < 10000000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 1) + "KK"+"$");
            } else if (totalProfitDollarLong > 10000000 && totalProfitDollarLong < 100000000) {
                totalProfitDollar.setText("+" + totalProfitDollarString.substring(0, 2) + "KK"+"$");
            }
        }
        totalProfitString = String.valueOf(totalProfitLong);
        totalPurchaseString = String.valueOf(totalPurchaseLong);
        if (totalProfitLong < 1000) {
            totalProfit.setText("+" + totalProfitString);
        } else if (totalProfitLong > 1000 && totalProfitLong < 10000) {
            totalProfit.setText("+" + totalProfitString.substring(0, 1) + "." + totalProfitString.substring(1, 2) + "K");
        } else if (totalProfitLong > 10000 && totalProfitLong < 100000) {
            totalProfit.setText("+" + totalProfitString.substring(0, 2) + "." + totalProfitString.substring(2, 3) + "K");
        } else if (totalProfitLong > 10000 && totalProfitLong < 100000) {
            totalProfit.setText("+" + totalProfitString.substring(0, 2) + "." + totalProfitString.substring(2, 3) + "K");
        } else if (totalProfitLong > 100000 && totalProfitLong < 1000000) {
            totalProfit.setText("+" + totalProfitString.substring(0, 3) + "K");
        } else if (totalProfitLong > 1000000 && totalProfitLong < 10000000) {
            totalProfit.setText("+" + totalProfitString.substring(0, 1) + "KK");
        } else if (totalProfitLong > 10000000 && totalProfitLong < 100000000) {
            totalProfit.setText("+" + totalProfitString.substring(0, 2) + "KK");
        }
        //////////////////////////////////////
        if(totalPurchaseLong<1000){
            totalPurchase.setText(totalPurchaseString);
        }else if(totalPurchaseLong>1000 && totalPurchaseLong<10000){
            totalPurchase.setText(totalPurchaseString.substring(0, 1)+"."+totalPurchaseString.substring(1, 2)+"K");
        }
        else if(totalPurchaseLong>10000 && totalPurchaseLong<100000){
            totalPurchase.setText(totalPurchaseString.substring(0, 2)+"."+totalPurchaseString.substring(2,3)+"K");
        }
        else if(totalPurchaseLong>10000 && totalPurchaseLong<100000){
            totalPurchase.setText(totalPurchaseString.substring(0, 2)+"."+totalPurchaseString.substring(2,3)+"K");
        }else if(totalPurchaseLong>100000 && totalPurchaseLong<1000000){
            totalPurchase.setText(totalPurchaseString.substring(0, 3)+"K");
        }
        else if(totalPurchaseLong>1000000 && totalPurchaseLong<10000000){
            totalPurchase.setText(totalPurchaseString.substring(0, 1)+"KK");
        }
        else if(totalPurchaseLong>10000000 && totalPurchaseLong<100000000){
            totalPurchase.setText(totalPurchaseString.substring(0, 2)+"KK");
        }
    }
}
