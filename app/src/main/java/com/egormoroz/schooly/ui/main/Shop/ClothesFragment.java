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

    public ClothesFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static ClothesFragment newInstance(UserInformation userInformation,Bundle bundle) {
        return new ClothesFragment(userInformation,bundle);
    }
    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<Clothes> clothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> newClothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularClothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularSortClothesArrayList=new ArrayList<Clothes>();
    RecyclerView clothes,popularClothes;
    NewClothesAdapter.ItemClickListener itemClickListener;
    PopularClothesAdapter.ItemClickListener itemClickListenerPopular;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layoutwiewpagershop, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        clothes=root.findViewById(R.id.newchlothesinshop);
        popularClothes=root.findViewById(R.id.popularchlothesinshop);
        loadClothesFromBase();
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

        itemClickListener=new NewClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                ((MainActivity)getActivity()).setCurrentFragment(ViewingClothes.newInstance(ShopFragment.newInstance(userInformation,bundle),userInformation,bundle));
            }
        };
        itemClickListenerPopular=new PopularClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                ((MainActivity)getActivity()).setCurrentFragment(ViewingClothesPopular.newInstance(userInformation,bundle));
            }
        };
    }


    public void loadClothesFromBase(){
        RecentMethods.getClothes(firebaseModel, new Callbacks.GetClothes() {
            @Override
            public void getClothes(ArrayList<Clothes> allClothes) {
                clothesArrayList.addAll(allClothes);
                for(int i=0;i<clothesArrayList.size();i++){
                    Clothes cl=clothesArrayList.get(i);
                    if (cl.getClothesType().equals("clothes")){
                        newClothesArrayList.add(cl);
                    }
//                           if (cl.getPurchaseNumber()==1){
//                               firebaseModel.getReference("AppData/Clothes/Popular").setValue()
//                            }
                }
                NewClothesAdapter newClothesAdapter=new NewClothesAdapter(newClothesArrayList,itemClickListener);
                clothes.setAdapter(newClothesAdapter);
            }
        });

        RecentMethods.getPopular( firebaseModel, new Callbacks.GetClothes() {
            @Override
            public void getClothes(ArrayList<Clothes> allClothes) {
                popularClothesArrayList.addAll(allClothes);
                for(int i=0;i<popularClothesArrayList.size();i++){
                    Clothes cl=popularClothesArrayList.get(i);
                    if (cl.getClothesType().equals("clothes")){
                        popularSortClothesArrayList.add(cl);
                    }
                }
                PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularSortClothesArrayList,itemClickListenerPopular);
                popularClothes.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                popularClothes.setNestedScrollingEnabled(false);
                popularClothes.setAdapter(popularClothesAdapter);
            }
        });
    }
}