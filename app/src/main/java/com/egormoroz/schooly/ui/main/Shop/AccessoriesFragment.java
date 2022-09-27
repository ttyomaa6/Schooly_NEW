package com.egormoroz.schooly.ui.main.Shop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class AccessoriesFragment extends Fragment {
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;

    public AccessoriesFragment(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
    }

    public static AccessoriesFragment newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        return new AccessoriesFragment(userInformation,bundle,fragment);
    }

    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<Clothes> clothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> accessoriesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularClothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularSortAccessoriesArrayList=new ArrayList<Clothes>();
    RecyclerView clothes,popularClothes;
    NewClothesAdapter.ItemClickListener itemClickListener;
    PopularClothesAdapter.ItemClickListener itemClickListenerPopular;
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
        positionNew=bundle.getInt("ACCESSORIESNEWPOSITION");
        positionPopular=bundle.getInt("ACCESSORIESPOPULARPOSITION");
        reverse=bundle.getInt("REVERSEACCESSORIES");
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().requestLayout();
        positionNew=bundle.getInt("ACCESSORIESNEWPOSITION");
        positionPopular=bundle.getInt("ACCESSORIESPOPULARPOSITION");
    }

    @Override
    public void onPause() {
        super.onPause();
        bundle.putInt("ACCESSORIESNEWPOSITION",linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        bundle.putInt("ACCESSORIESPOPULARPOSITION", gridLayoutManager.findFirstVisibleItemPosition());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putInt("ACCESSORIESNEWPOSITION",linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        bundle.putInt("ACCESSORIESPOPULARPOSITION", gridLayoutManager.findFirstVisibleItemPosition());
        bundle.putInt("REVERSEACCESSORIES", reverse);
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

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
    }


    public void loadClothesFromBase(){
        if(bundle.getSerializable("ACCESSORIES_NEW")!=null){
            ArrayList<Clothes> newClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("ACCESSORIES_NEW");
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
                if (cl.getClothesType().equals("accessories")){
                    accessoriesArrayList.add(cl);
                }

            }
            bundle.putSerializable("ACCESSORIES_NEW",accessoriesArrayList);
            if(reverse==0)Collections.reverse(accessoriesArrayList);
            reverse++;
            NewClothesAdapter newClothesAdapter=new NewClothesAdapter(accessoriesArrayList,itemClickListener,userInformation);
            linearLayoutManager=new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            clothes.setLayoutManager(linearLayoutManager);
            clothes.setAdapter(newClothesAdapter);
                clothes.scrollToPosition(positionNew);
        }
        if(bundle.getSerializable("ACCESSORIES_POPULAR")!=null){
            popularClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("ACCESSORIES_POPULAR");
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularClothesArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
            popularClothes.scrollToPosition(positionPopular);
        }else{
            ArrayList<Clothes> allClothes= (ArrayList<Clothes>) bundle.getSerializable("ALL_CLOTHES");
            for(int i=0;i<allClothes.size();i++){
                Clothes cl=allClothes.get(i);
                if (cl.getClothesType().equals("accessories")){
                    popularSortAccessoriesArrayList.add(cl);
                }

            }
            bundle.putSerializable("ACCESSORIES_POPULAR",popularSortAccessoriesArrayList);
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularSortAccessoriesArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
            popularClothes.scrollToPosition(positionPopular);
        }
    }
}