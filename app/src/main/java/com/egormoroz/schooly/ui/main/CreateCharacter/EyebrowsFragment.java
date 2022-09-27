package com.egormoroz.schooly.ui.main.CreateCharacter;

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
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.TaskRunnerCustom;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class EyebrowsFragment extends Fragment {

    RecyclerView recyclerView;
    BrowsAdapter.ItemClickListener itemClickListener;
    ArrayList<FacePart> bodyPartsArrayList=new ArrayList<>();
    ArrayList<FacePart> activeFaceParts=new ArrayList<>();
    FirebaseModel firebaseModel=new FirebaseModel();
    CircularProgressIndicator circularProgressIndicator;
    ArrayList<FacePart> facePartsWithBuffers=new ArrayList<>();
    int loadValue;
    public static EyebrowsFragment newInstance() {
        return new EyebrowsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpagerskincolour, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAppDataDatabase();
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        itemClickListener=new BrowsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(FacePart facePart) {
                CreateCharacterFragment.loadNewFacePart(facePart);
            }
        };

        circularProgressIndicator=view.findViewById(R.id.progressIndicator);
        recyclerView=view.findViewById(R.id.recyclerSkinColour);
        RecentMethods.getCurrentFaceParts("brows", firebaseModel, new Callbacks.loadFacePartsCustom() {
            @Override
            public void LoadNews(ArrayList<FacePart> facePartsArrayList) {
                Log.d("######", "a  "+facePartsArrayList);
                loadBuffers(facePartsArrayList);
            }
        });

    }

    public void loadBuffers(ArrayList<FacePart>  facePartArrayList){
        loadValue=facePartArrayList.size()-1;
        for(int i=0;i<facePartArrayList.size();i++){
            FacePart facePart=facePartArrayList.get(i);
            Log.d("######", "b  "+facePart.getPartTitle());
            if(!facePart.getPartTitle().equals("bald")){
                TaskRunnerCustom taskRunnerCustom=new TaskRunnerCustom();
                taskRunnerCustom.executeAsync(new HairstyleFragment.LongRunningTask(facePart), (data) -> {
                    if(data.getModel()!=null){
                        facePartsWithBuffers.add(data);
                    }
                    loadValue--;
                    if(loadValue==0){
                        activeFaceParts=CreateCharacterFragment.sentFaceParts();
                        circularProgressIndicator.setVisibility(View.GONE);
                        Log.d("######", "f  "+facePartsWithBuffers);
                        BrowsAdapter browsAdapter=new BrowsAdapter(facePartsWithBuffers,itemClickListener,activeFaceParts,"brows");
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        recyclerView.setAdapter(browsAdapter);
                    }
                });
            }else {
                facePartsWithBuffers.add(facePart);
                loadValue--;
                if(loadValue==0){
                    activeFaceParts=CreateCharacterFragment.sentFaceParts();
                    circularProgressIndicator.setVisibility(View.GONE);
                    Log.d("######", "f  "+facePartsWithBuffers);
                    BrowsAdapter browsAdapter=new BrowsAdapter(facePartsWithBuffers,itemClickListener,activeFaceParts,"brows");
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    recyclerView.setAdapter(browsAdapter);
                }
            }
        }
    }
}
