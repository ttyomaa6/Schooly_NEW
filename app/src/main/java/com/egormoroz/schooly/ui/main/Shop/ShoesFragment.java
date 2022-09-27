package com.egormoroz.schooly.ui.main.Shop;

import static java.lang.Double.max;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class ShoesFragment extends Fragment {
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;

    public ShoesFragment(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
    }

    public static ShoesFragment newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        return new ShoesFragment(userInformation,bundle,fragment);
    }
    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<Clothes> shoesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularClothesArrayList=new ArrayList<Clothes>();
    ArrayList<Clothes> popularSortShoesArrayList=new ArrayList<Clothes>();
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
        positionNew=bundle.getInt("SHOESNEWPOSITION");
        positionPopular=bundle.getInt("SHOESPOPULARPOSITION");
        reverse=bundle.getInt("REVERSESHOES");
        Log.d("#####", "P   "+positionPopular);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().requestLayout();
        positionNew=bundle.getInt("SHOESNEWPOSITION");
        positionPopular=bundle.getInt("SHOESPOPULARPOSITION");
    }

    @Override
    public void onPause() {
        super.onPause();
        bundle.putInt("SHOESNEWPOSITION",linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        bundle.putInt("SHOESPOPULARPOSITION", positionPopular);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putInt("SHOESNEWPOSITION",linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        bundle.putInt("SHOESPOPULARPOSITION", positionPopular);
        bundle.putInt("REVERSESHOES", reverse);
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
                positionPopular=position;
                Log.d("AAAA", "pos0   "+positionPopular);
                ((MainActivity)getActivity()).setCurrentFragment(ViewingClothesPopular.newInstance(userInformation,bundle,ShopFragment.newInstance(userInformation,bundle,fragment)));
            }
        };
        loadClothesFromBase();
    }


    public void loadClothesFromBase(){
        Log.d("AAAA", "pos   "+positionPopular);
        if(bundle.getSerializable("SHOES_NEW")!=null){
            ArrayList<Clothes> newClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("SHOES_NEW");
            if(reverse==0) Collections.reverse(newClothesArrayList);
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
                if (cl.getClothesType().equals("shoes")){
                    shoesArrayList.add(cl);
                }

            }
            bundle.putSerializable("SHOES_NEW",shoesArrayList);
            if(reverse==0)Collections.reverse(shoesArrayList);
            reverse++;
            NewClothesAdapter newClothesAdapter=new NewClothesAdapter(shoesArrayList,itemClickListener,userInformation);
            linearLayoutManager=new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            clothes.setLayoutManager(linearLayoutManager);
            clothes.setAdapter(newClothesAdapter);
            clothes.scrollToPosition(positionNew);
        }
        if(bundle.getSerializable("SHOES_POPULAR")!=null){
            popularClothesArrayList= (ArrayList<Clothes>) bundle.getSerializable("SHOES_POPULAR");
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularClothesArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
            popularClothes.scrollToPosition(positionPopular);
            gridLayoutManager.scrollToPosition(positionPopular);
        }else{
            ArrayList<Clothes> allClothes= (ArrayList<Clothes>) bundle.getSerializable("ALL_CLOTHES");
            for(int i=0;i<allClothes.size();i++){
                Clothes cl=allClothes.get(i);
                if (cl.getClothesType().equals("shoes")){
                    popularSortShoesArrayList.add(cl);
                }

            }
            bundle.putSerializable("SHOES_POPULAR",popularSortShoesArrayList);
            PopularClothesAdapter popularClothesAdapter=new PopularClothesAdapter(popularSortShoesArrayList,itemClickListenerPopular,userInformation);
            gridLayoutManager=new GridLayoutManager(getContext(), 2);
            popularClothes.setLayoutManager(gridLayoutManager);
            popularClothes.setAdapter(popularClothesAdapter);
            popularClothes.scrollToPosition(positionPopular);
            gridLayoutManager.scrollToPosition(positionPopular);
        }
    }

    public static ArrayList<Integer> get_max(int amount, ArrayList<Integer> nums){
        Collections.sort(nums);
        ArrayList<Integer> res = new ArrayList<Integer>();
        for(int i = nums.size() - 1; i >= max(0, nums.size() - amount); i--)
            res.add(nums.get(i));
        return res;
    }
}