package com.egormoroz.schooly.ui.main.Shop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class PopularFragment extends Fragment {
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;

    public PopularFragment(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
    }

    public static PopularFragment newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        return new PopularFragment(userInformation,bundle,fragment);
    }

    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<Clothes> clothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> newClothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularClothesArrayList=new ArrayList<Clothes>();
    RecyclerView clothes,popularClothes;
    NewClothesAdapter.ItemClickListener itemClickListener;
    PopularClothesAdapter.ItemClickListener itemClickListenerPopular;
    TextView newText;
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
        positionNew=bundle.getInt("POPULARNEWPOSITION");
        positionPopular=bundle.getInt("POPULARPOPULARPOSITION");
        reverse=bundle.getInt("REVERSEPOPULAR");
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().requestLayout();
        positionNew=bundle.getInt("POPULARNEWPOSITION");
        positionPopular=bundle.getInt("POPULARPOPULARPOSITION");
    }

    @Override
    public void onPause() {
        super.onPause();
        bundle.putInt("POPULARNEWPOSITION",linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        bundle.putInt("POPULARPOPULARPOSITION", gridLayoutManager.findFirstVisibleItemPosition());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putInt("POPULARNEWPOSITION",linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        bundle.putInt("POPULARPOPULARPOSITION", gridLayoutManager.findFirstVisibleItemPosition());
        bundle.putInt("REVERSEPOPULAR", reverse);
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        newText=view.findViewById(R.id.newnew);

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
        if(bundle.getSerializable("CLOTHES_NEW")!=null){
            ArrayList<Clothes> newClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("CLOTHES_NEW");
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
                newClothesArrayList.add(cl);

            }
            bundle.putSerializable("CLOTHES_NEW",newClothesArrayList);
            if(reverse==0)Collections.reverse(newClothesArrayList);
            reverse++;
            NewClothesAdapter newClothesAdapter=new NewClothesAdapter(newClothesArrayList,itemClickListener,userInformation);
            linearLayoutManager=new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            clothes.setLayoutManager(linearLayoutManager);
            clothes.setAdapter(newClothesAdapter);
                clothes.scrollToPosition(positionNew);
        }
        if(bundle.getSerializable("CLOTHES_POPULAR")!=null){
            popularClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("CLOTHES_POPULAR");
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularClothesArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
                popularClothes.scrollToPosition(positionNew);
        }else{
            ArrayList<Clothes> allClothes= (ArrayList<Clothes>) bundle.getSerializable("ALL_CLOTHES");
            for(int i=0;i<allClothes.size();i++){
                Clothes cl=allClothes.get(i);
                popularClothesArrayList.add(cl);

            }

            popularClothesArrayList=sort_clothes_by_purchase_number(allClothes);
            bundle.putSerializable("CLOTHES_POPULAR",popularClothesArrayList);
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularClothesArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
                popularClothes.scrollToPosition(positionNew);
        }
    }
    public ArrayList<Clothes> sort_clothes_by_purchase_number(ArrayList<Clothes> cur){
        cur.sort((Clothes a, Clothes b) -> -(int)(a.getPurchaseNumber() - b.getPurchaseNumber()));
        return cur;
    }
    public ArrayList<Clothes> sort_clothes_by_purchase_today(ArrayList<Clothes> cur){
        cur.sort((Clothes a, Clothes b) -> -(int)(a.getPurchaseToday() - b.getPurchaseToday()));
        return cur;
    }
    public ArrayList<Clothes> sort_clothes_by_time(ArrayList<Clothes> cur){
        cur.sort((Clothes a, Clothes b) -> (int)(a.getTimesTamp() - b.getTimesTamp()));
        return cur;
    }
}