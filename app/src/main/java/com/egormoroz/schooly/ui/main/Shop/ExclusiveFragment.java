package com.egormoroz.schooly.ui.main.Shop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import com.egormoroz.schooly.NestedScrollableHost;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class ExclusiveFragment extends Fragment {

    String version;
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;

    public ExclusiveFragment(String version,UserInformation userInformation,Bundle bundle,Fragment fragment) {
        this.version = version;
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
    }

    public static ExclusiveFragment newInstance(String version,UserInformation userInformation,Bundle bundle,Fragment fragment) {
        return new ExclusiveFragment(version,userInformation,bundle,fragment);

    }

    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<Clothes> clothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> exclusiveClothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> exclusivePopularArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularClothesArrayList=new ArrayList<Clothes>();
    RecyclerView clothes,popularClothes;
    NewClothesAdapter.ItemClickListener itemClickListener;
    PopularClothesAdapter.ItemClickListener itemClickListenerPopular;
    TextView newText,popularText,noPremium;
    NestedScrollableHost nestedScrollableHost;
    RelativeLayout premium,buyPremium;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    int positionNew,positionPopular,reverse;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layoutwiewpagershop, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        clothes=root.findViewById(R.id.newchlothesinshop);
        popularClothes=root.findViewById(R.id.popularchlothesinshop);
        positionNew=bundle.getInt("EXCLUSIVENEWPOSITION");
        positionPopular=bundle.getInt("EXCLUSIVEPOPULARPOSITION");
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().requestLayout();
        positionNew=bundle.getInt("EXCLUSIVENEWPOSITION");
        positionPopular=bundle.getInt("EXCLUSIVEPOPULARPOSITION");
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        newText=view.findViewById(R.id.newnew);
        popularText=view.findViewById(R.id.popular);
        nestedScrollableHost=view.findViewById(R.id.nestedHost);
        noPremium=view.findViewById(R.id.noPremium);
        premium=view.findViewById(R.id.premium);
        buyPremium=view.findViewById(R.id.buyPremium);

        if(version.equals("premium")){
            itemClickListener=new NewClothesAdapter.ItemClickListener() {
                @Override
                public void onItemClick(Clothes clothes) {
                    ((MainActivity)getActivity()).setCurrentFragment(ViewingClothes.newInstance(ShopFragment.newInstance(userInformation,bundle,fragment),userInformation,bundle));
                }
            };
            itemClickListenerPopular=new PopularClothesAdapter.ItemClickListener() {
                @Override
                public void onItemClick(Clothes clothes,int position) {
                    ((MainActivity)getActivity()).setCurrentFragment(ViewingClothesPopular.newInstance(userInformation,bundle,ShopFragment.newInstance(userInformation,bundle,fragment)));
                }
            };
            loadClothesFromBase();
        }else {
            premium.setVisibility(View.VISIBLE);
            noPremium.setVisibility(View.VISIBLE);
            buyPremium.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            newText.setVisibility(View.GONE);
            popularText.setVisibility(View.GONE);
            clothes.setVisibility(View.GONE);
            popularClothes.setVisibility(View.GONE);
            nestedScrollableHost.setVisibility(View.GONE);
        }
    }


    public void loadClothesFromBase(){
        if(bundle.getSerializable("EXCLUSIVE_NEW")!=null){
            ArrayList<Clothes> newClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("EXCLUSIVE_NEW");
            if(reverse==0)Collections.reverse(newClothesArrayList);
            reverse++;
            NewClothesAdapter newClothesAdapter=new NewClothesAdapter(newClothesArrayList,itemClickListener,userInformation);
            linearLayoutManager=new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            clothes.setLayoutManager(linearLayoutManager);
            clothes.setAdapter(newClothesAdapter);
                clothes.scrollToPosition(positionNew);
        }else{
            ArrayList<Clothes> allClothes= (ArrayList<Clothes>) bundle.getSerializable("ALL_CLOTHES");
            for(int i=0;i<allClothes.size();i++){
                Clothes cl=allClothes.get(i);
                if (cl.getExclusive().equals("exclusive")){
                    exclusiveClothesArrayList.add(cl);
                }

            }
            bundle.putSerializable("EXCLUSIVE_NEW",exclusiveClothesArrayList);
            if(reverse==0)Collections.reverse(exclusiveClothesArrayList);
            reverse++;
            NewClothesAdapter newClothesAdapter=new NewClothesAdapter(exclusiveClothesArrayList,itemClickListener,userInformation);
            linearLayoutManager=new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            clothes.setLayoutManager(linearLayoutManager);
            clothes.setAdapter(newClothesAdapter);
                clothes.scrollToPosition(positionNew);
        }
        if(bundle.getSerializable("EXCLUSIVE_POPULAR")!=null){
            popularClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("EXCLUSIVE_POPULAR");
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularClothesArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
            popularClothes.scrollToPosition(positionPopular);
        }else{
            ArrayList<Clothes> allClothes= (ArrayList<Clothes>) bundle.getSerializable("ALL_CLOTHES");
            for(int i=0;i<allClothes.size();i++){
                Clothes cl=allClothes.get(i);
                if (cl.getExclusive().equals("exclusive")){
                    exclusivePopularArrayList.add(cl);
                }

            }
            bundle.putSerializable("EXCLUSIVE_POPULAR",exclusivePopularArrayList);
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(exclusivePopularArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
            popularClothes.scrollToPosition(positionPopular);
        }
    }
}
