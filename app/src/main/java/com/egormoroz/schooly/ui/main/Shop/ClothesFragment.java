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

public class ClothesFragment extends Fragment {
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;

    public ClothesFragment(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
    }

    public static ClothesFragment newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        return new ClothesFragment(userInformation,bundle,fragment);
    }
    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<Clothes> newClothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularClothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularSortClothesArrayList=new ArrayList<Clothes>();
    RecyclerView clothes,popularClothes;
    NewClothesAdapter.ItemClickListener itemClickListener;
    PopularClothesAdapter.ItemClickListener itemClickListenerPopular;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    int positionNew,positionPopular;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layoutwiewpagershop, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        clothes=root.findViewById(R.id.newchlothesinshop);
        popularClothes=root.findViewById(R.id.popularchlothesinshop);
        positionNew=bundle.getInt("CLOTHESNEWPOSITION");
        positionPopular=bundle.getInt("CLOTHESPOPULARPOSITION");
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().requestLayout();
        positionNew=bundle.getInt("CLOTHESNEWPOSITION");
        positionPopular=bundle.getInt("CLOTHESPOPULARPOSITION");
    }

    @Override
    public void onPause() {
        super.onPause();
        bundle.putInt("CLOTHESNEWPOSITION",linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        bundle.putInt("CLOTHESPOPULARPOSITION", gridLayoutManager.findFirstVisibleItemPosition());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putInt("CLOTHESNEWPOSITION",linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        bundle.putInt("CLOTHESPOPULARPOSITION", gridLayoutManager.findFirstVisibleItemPosition());
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
            public void onItemClick(Clothes clothes) {
                ((MainActivity)getActivity()).setCurrentFragment(ViewingClothesPopular.newInstance(userInformation,bundle,ShopFragment.newInstance(userInformation,bundle,fragment)));
            }
        };
        loadClothesFromBase();
    }


    public void loadClothesFromBase(){
        if(bundle.getSerializable("WEAR_NEW")!=null){
            ArrayList<Clothes> newClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("WEAR_NEW");
            Log.d("####", "n  "+newClothesArrayList);
            NewClothesAdapter newClothesAdapter=new NewClothesAdapter(newClothesArrayList,itemClickListener,userInformation);
            linearLayoutManager=new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            clothes.setLayoutManager(linearLayoutManager);
            clothes.setAdapter(newClothesAdapter);
                clothes.scrollToPosition(positionNew);
        }else{
            ArrayList<Clothes> allClothes= (ArrayList<Clothes>) bundle.getSerializable("ALL_CLOTHES");
            Log.d("####", "n1 "+allClothes);
            for(int i=0;i<allClothes.size();i++){
                Clothes cl=allClothes.get(i);
                if (cl.getClothesType().equals("clothes")){
                    newClothesArrayList.add(cl);
                }

            }
            NewClothesAdapter newClothesAdapter=new NewClothesAdapter(newClothesArrayList,itemClickListener,userInformation);
            linearLayoutManager=new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            clothes.setLayoutManager(linearLayoutManager);
            clothes.setAdapter(newClothesAdapter);
                clothes.scrollToPosition(positionNew);
        }
        if(bundle.getSerializable("WEAR_POPULAR")!=null){
            popularClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("WEAR_POPULAR");
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularClothesArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
                popularClothes.scrollToPosition(positionNew);
        }else{
            ArrayList<Clothes> allClothes= (ArrayList<Clothes>) bundle.getSerializable("ALL_CLOTHES");
            for(int i=0;i<allClothes.size();i++){
                Clothes cl=allClothes.get(i);
                if (cl.getClothesType().equals("clothes")){
                    popularSortClothesArrayList.add(cl);
                }
            }
            bundle.putSerializable("WEAR_POPULAR",popularSortClothesArrayList);
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularSortClothesArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
                popularClothes.scrollToPosition(positionNew);
        }
    }
}