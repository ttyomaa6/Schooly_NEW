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

import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class FaceShapeFragment extends Fragment {

    RecyclerView recyclerView;
    CharacterAdapter.ItemClickListener itemClickListener;
    ArrayList<FacePart> bodyPartsArrayList=new ArrayList<>();
    ArrayList<FacePart> activeFaceParts=new ArrayList<>();
    public static FaceShapeFragment newInstance() {
        return new FaceShapeFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpagerskincolour, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
//        AppBarLayout abl = getActivity().findViewById(R.id.AppBarLayout);
//        abl.setVisibility(abl.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        recyclerView=view.findViewById(R.id.recyclerSkinColour);
        CharacterAdapter characterAdapter=new CharacterAdapter(bodyPartsArrayList,itemClickListener,activeFaceParts,"face");
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(characterAdapter);

    }
}
