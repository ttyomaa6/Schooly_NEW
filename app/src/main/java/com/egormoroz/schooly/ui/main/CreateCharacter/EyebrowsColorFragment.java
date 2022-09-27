package com.egormoroz.schooly.ui.main.CreateCharacter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Color;
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class EyebrowsColorFragment extends Fragment {

    RecyclerView recyclerView;
    ColorsAdapter.ItemClickListener itemClickListener;
    CircularProgressIndicator progressIndicator;

    public static EyebrowsColorFragment newInstance() {
        return new EyebrowsColorFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpagerskincolour, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        itemClickListener=new ColorsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Color color) {
                CreateCharacterFragment.changeColor("brows", color);
            }
        };
        progressIndicator=view.findViewById(R.id.progressIndicator);
        recyclerView=view.findViewById(R.id.recyclerSkinColour);
        ArrayList< Color> colorsArrayList= RecentMethods.returnColors();
        progressIndicator.setVisibility(View.GONE);
        ColorsAdapter colorsAdapter=new ColorsAdapter(colorsArrayList, itemClickListener, "brows");
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(colorsAdapter);
    }
}
