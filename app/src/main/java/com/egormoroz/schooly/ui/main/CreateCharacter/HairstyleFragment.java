package com.egormoroz.schooly.ui.main.CreateCharacter;

import android.os.Bundle;
import android.transition.CircularPropagation;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class HairstyleFragment extends Fragment {

    RecyclerView recyclerView;
    CharacterAdapter.ItemClickListener itemClickListener;
    ArrayList<FacePart> activeFaceParts=new ArrayList<>();
    ArrayList<FacePart> facePartsWithBuffers=new ArrayList<>();
    FirebaseModel firebaseModel=new FirebaseModel();
    static byte[] buffer;
    static URI uri;
    static Buffer bufferToFilament,b;
    CircularProgressIndicator circularProgressIndicator;
    int loadValue;
    public static HairstyleFragment newInstance() {
        return new HairstyleFragment();
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


        itemClickListener=new CharacterAdapter.ItemClickListener() {
            @Override
            public void onItemClick(FacePart facePart) {
                CreateCharacterFragment.loadNewFacePart(facePart);
            }
        };
         RecentMethods.getCurrentFaceParts("hair", firebaseModel, new Callbacks.loadFacePartsCustom() {
            @Override
            public void LoadNews(ArrayList<FacePart> facePartsArrayList) {
                Log.d("######", "a  "+facePartsArrayList);
                loadBuffers(facePartsArrayList);
            }
        });

        circularProgressIndicator=view.findViewById(R.id.progressIndicator);
        recyclerView=view.findViewById(R.id.recyclerSkinColour);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

//        ArrayList<FacePart> facePartArrayList=new ArrayList<>();
//        FacePart facePart=new FacePart("hair", "hair", )
    }

    public void loadBuffers(ArrayList<FacePart>  facePartArrayList){
        loadValue=facePartArrayList.size()-1;
        for(int i=0;i<facePartArrayList.size();i++){
            FacePart facePart=facePartArrayList.get(i);
            Log.d("######", "f1  "+facePart.getModel()+"    "+facePart.getPartTitle());
            if(!facePart.getPartTitle().equals("bald")){
                TaskRunnerCustom taskRunnerCustom=new TaskRunnerCustom();
                taskRunnerCustom.executeAsync(new LongRunningTask(facePart), (data) -> {
                    if(data.getModel()!=null){
                        facePartsWithBuffers.add(data);
                    }
                    loadValue--;
                    if(loadValue==0){
                        activeFaceParts=CreateCharacterFragment.sentFaceParts();
                        circularProgressIndicator.setVisibility(View.GONE);
                        Log.d("######", "f  "+facePartsWithBuffers);
                        CharacterAdapter characterAdapter=new CharacterAdapter(facePartsWithBuffers,itemClickListener,activeFaceParts,"hair");
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        recyclerView.setAdapter(characterAdapter);
                    }
                });
            }else {
                facePartsWithBuffers.add(facePart);
                loadValue--;
                if(loadValue==0){
                    activeFaceParts=CreateCharacterFragment.sentFaceParts();
                    circularProgressIndicator.setVisibility(View.GONE);
                    Log.d("######", "f  "+facePartsWithBuffers);
                    CharacterAdapter characterAdapter=new CharacterAdapter(facePartsWithBuffers,itemClickListener,activeFaceParts,"hair");
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    recyclerView.setAdapter(characterAdapter);
                }
            }
        }
    }

    static class LongRunningTask implements Callable<FacePart> {
        private FacePart facePart;

        public LongRunningTask(FacePart facePart) {
            this.facePart = facePart;
        }

        @Override
        public FacePart call() {
            return loadFacePart(facePart);
        }
    }

    public static FacePart loadFacePart(FacePart facePart){
        if(facePart.getModel()!=null) {
            try {
                uri = new URI(facePart.getModel());
                buffer = RecentMethods.getBytes(uri.toURL());
                bufferToFilament = ByteBuffer.wrap(buffer);
                facePart.setBuffer(bufferToFilament);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return facePart;
    }


}
