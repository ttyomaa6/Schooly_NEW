package com.egormoroz.schooly.ui.profile.Wardrobe;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FilamentModel;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.LoadBodyParts;
import com.egormoroz.schooly.LoadClothesArrayListBuffers;
import com.egormoroz.schooly.LockableNestedScrollView;
import com.egormoroz.schooly.Person;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.TaskRunner;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.NewsAdapter;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.news.ViewingClothesNews;
import com.egormoroz.schooly.ui.profile.LooksAdapter;
import com.egormoroz.schooly.ui.profile.LooksFragmentProfileOther;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AcceptNewLook extends Fragment {

    FirebaseModel firebaseModel=new FirebaseModel();
    RelativeLayout publish;
    TextView lookPrice,lookPriceDollar,constituentsText,publishText;
    CircularProgressIndicator circularProgressIndicator;
    EditText descriptionLook;
    ImageView schoolyCoin;
    RecyclerView recyclerView;
    long lookPriceLong,lookPriceDollarLong;
    String lookPriceString,lookPriceDollarString,nick,type;
    ConstituentsAdapter.ItemClickListener itemClickListener;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;
    String lookType;
    SurfaceView surfaceView;
    static byte[] buffer;
    static URI uri;
    static Buffer bufferToFilament;
    static FilamentModel filamentModel=new FilamentModel();
    static ArrayList<Clothes> clothesList=new ArrayList<>();
    static ArrayList<String> clothesUid=new ArrayList<>();
    LockableNestedScrollView lockableNestedScrollView;
    static int loadValue;
    int a=0;
    static ArrayList<String > allLoadClothesUid=new ArrayList<>();
    Person personMain=new Person();

    public AcceptNewLook( String type, Fragment fragment, UserInformation userInformation, Bundle bundle, String lookType, ArrayList<String> clothesUid,
                         ArrayList<Clothes> clothesList) {
        this.type = type;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.lookType=lookType;
        AcceptNewLook.clothesUid =clothesUid;
        AcceptNewLook.clothesList=clothesList;
    }

    public static AcceptNewLook newInstance(String type,Fragment fragment,UserInformation userInformation,Bundle bundle,String lookType
            ,ArrayList<String> clothesUid, ArrayList<Clothes> clothesList) {
        return new AcceptNewLook(type,fragment,userInformation,bundle,lookType,clothesUid,clothesList);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_acceptnewlook, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(descriptionLook.getText().toString().length()>0){
            bundle.putString("EDIT_DESCRIPTION_LOOK",descriptionLook.getText().toString().trim());
        }
        bundle.putSerializable("CLOTHESUID", clothesUid);
        bundle.putSerializable("ALLLOADCLOTHESLIST", clothesList);
        bundle.putSerializable("ALLLOADCLOTHESUID", allLoadClothesUid);
    }


    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        publish=view.findViewById(R.id.publish);
        lookPrice=view.findViewById(R.id.lookPrice);
        lookPriceDollar=view.findViewById(R.id.lookPriceDollar);
        descriptionLook=view.findViewById(R.id.addDescriptionEdit);
        surfaceView=view.findViewById(R.id.surfaceView);
        recyclerView=view.findViewById(R.id.constituentsRecycler);
        schoolyCoin=view.findViewById(R.id.schoolyCoin);
        constituentsText=view.findViewById(R.id.lookConstituentsText);
        circularProgressIndicator=view.findViewById(R.id.progressIndicator);
        publishText=view.findViewById(R.id.publishText);
        personMain=userInformation.getPerson();
        itemClickListener=new ConstituentsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                RecentMethods.setCurrentFragment(ViewingClothesNews.newInstance(AcceptNewLook.newInstance(type,fragment, userInformation,bundle,lookType,clothesUid,clothesList),userInformation,bundle), getActivity());
            }
        };

        ImageView backfromwardrobe=view.findViewById(R.id.back_toprofile);
        backfromwardrobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(CreateLookFragment.newInstance(type,fragment,userInformation,bundle,lookType), getActivity());
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                RecentMethods.setCurrentFragment(CreateLookFragment.newInstance(type,fragment,userInformation,bundle,lookType), getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        lockableNestedScrollView=view.findViewById(R.id.lockableNestedScrollView);
        loadPerson(userInformation, lockableNestedScrollView,surfaceView);
        if(bundle!=null){
            if(bundle.getString("EDIT_DESCRIPTION_LOOK")!=null){
                String editDescriptionText=bundle.getString("EDIT_DESCRIPTION_LOOK");
                descriptionLook.setText(editDescriptionText);
            }
        }
        if(bundle.getSerializable("ALLLOADCLOTHESUID")!=null){
            allLoadClothesUid= (ArrayList<String>) bundle.getSerializable("ALLLOADCLOTHESUID");
        }
        getLookClothes();
    }

    public void getLookClothes(){
        if(userInformation.getLookClothes().size()!=0){
            for (int i=0;i<userInformation.getLookClothes().size();i++){
                Clothes clothes=userInformation.getLookClothes().get(i);
                if(clothes.getCurrencyType().equals("dollar")){
                    lookPriceDollarLong+=clothes.getClothesPrice();
                }else {
                    lookPriceLong+=clothes.getClothesPrice();
                }
            }
            if(lookPriceLong==0){
                lookPrice.setText("0");
            }
            if(lookPriceDollarLong>0 || lookPriceLong>0){
                setTextInLookPrice();
            }
            FirebaseModel newsModel = new FirebaseModel();
            newsModel.initNewsDatabase();
            ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(userInformation.getLookClothes(), itemClickListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(constituentsAdapter);
            publish.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    publishText.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    String lookId=newsModel.getReference().child(nick).push().getKey();
                    descriptionLook.getText().clear();
                    getBitmapFormSurfaceView(surfaceView, getActivity(), new Callback<Bitmap>() {
                        @Override
                        public void onResult1(Bitmap bitmap) {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images")
                                    .child(lookId + ".png");
                            UploadTask uploadTask = storageReference.putBytes(getImageUri(getContext(), bitmap));
                            uploadTask.continueWithTask(new Continuation() {
                                @Override
                                public Object then(@NonNull Task task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return storageReference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri downloadUrl = task.getResult();
                                    ArrayList<FacePart> facePartArrayList=new ArrayList<>();
                                    facePartArrayList.add(userInformation.getPerson().getBody());
                                    facePartArrayList.add(userInformation.getPerson().getBrows());
                                    facePartArrayList.add(userInformation.getPerson().getEars());
                                    facePartArrayList.add(userInformation.getPerson().getEyes());
                                    facePartArrayList.add(userInformation.getPerson().getHair());
                                    facePartArrayList.add(userInformation.getPerson().getHead());
                                    facePartArrayList.add(userInformation.getPerson().getLips());
                                    facePartArrayList.add(userInformation.getPerson().getNose());
                                    facePartArrayList.add(userInformation.getPerson().getMustache());
                                    facePartArrayList.add(userInformation.getPerson().getSkinColor());
                                    Person person=new Person();
                                    for(int i=0;i<facePartArrayList.size();i++){
                                        FacePart facePart=facePartArrayList.get(i);
                                        if(facePart!=null){
                                                facePart.setBuffer(null);
                                            switch (facePart.getPartType()){
                                                case "body":
                                                    person.setBody(facePart);
                                                    break;
                                                case "hair":
                                                    person.setHair(facePart);
                                                    break;
                                                case "mouth":
                                                    person.setLips(facePart);
                                                    break;
                                                case "nose":
                                                    person.setNose(facePart);
                                                    break;
                                                case "brows":
                                                    person.setBrows(facePart);
                                                    break;
                                                case "eyes":
                                                    person.setEyes(facePart);
                                                    break;
                                            }
                                        }
                                    }
                                    if(lookType.equals("mainlook")){
                                        firebaseModel.getUsersReference().child(nick).child("mainLook")
                                                .setValue(userInformation.getLookClothes());
                                        userInformation.setMainLook(null);
                                    }else {
                                        newsModel.getReference().child(nick).child(lookId)
                                                .setValue(new NewsItem(downloadUrl.toString(), descriptionLook.getText().toString(), "0", lookId,
                                                        "", userInformation.getLookClothes(), lookPriceLong, 0,
                                                        "", nick, lookPriceDollarLong, person, 0));
                                        newsModel.getReference().child(nick).child(lookId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                    }
                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance(type, nick, fragment,userInformation,bundle), getActivity());

                                }
                            });
                        }
                    });
                }
            });
        }else{
            lookPrice.setText("0");
            constituentsText.setVisibility(View.GONE);
            publish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.nolookcomponents), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        filamentModel.postFrameCallback();
    }

    @Override
    public void onPause() {
        super.onPause();

        filamentModel.removeFrameCallback();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        filamentModel.removeFrameCallback();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getBitmapFormSurfaceView(View view, Activity activity, Callback<Bitmap> callback) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        PixelCopy.request((SurfaceView) view, bitmap, copyResult -> {
            if (copyResult == PixelCopy.SUCCESS) {
                callback.onResult1(bitmap);
            }
        }, new Handler(Looper.getMainLooper()));
    }

    public byte[] getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        byte[] bytesArray=bytes.toByteArray();
        return bytesArray;
    }

    public interface Callback<Bitmap> {
        void onResult1(Bitmap bitmap);
    }

    public void loadLookClothes(){
        loadValue=1;
        Log.d("AAAA", "LOAD SIZE  1   "+clothesList.size()+"   "+clothesUid.size());
        if(clothesUid.size()==0) {
            RecentMethods.getMyLookClothesOnce(nick, firebaseModel, new Callbacks.getLookClothes() {
                @Override
                public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                    if(clothesArrayList.size()>0){
                        LoadClothesArrayListBuffers.loadClothesBuffer(clothesArrayList, new Callbacks.loadClothesArrayList() {
                            @Override
                            public void LoadClothes(ArrayList<Clothes> clothesArrayList) {
                                for(int i=0;i<clothesArrayList.size();i++){
                                    Clothes clothes=clothesArrayList.get(i);
                                    filamentModel.populateScene(clothes.getBuffer(), clothes);
                                    if(!allLoadClothesUid.contains(clothes.getUid())) {
                                        clothesList.add(clothes);
                                        allLoadClothesUid.add(clothes.getUid());
                                        Log.d("AAAA", "q   "+loadValue+"   "+clothes.getClothesTitle());
                                    }
                                    Log.d("AAAA", "q   "+loadValue+"   "+clothes.getClothesTitle());
                                    clothesUid.add(clothes.getUid());
                                }
                                loadValue=0;
                            }
                        });
                    }else{
                        loadValue=0;
                    }
                }
            });
        } else{
            Log.d("AAAA", "LOAD SIZE   "+clothesList.size());
            loadValue=clothesUid.size();
            for(int i=0;i<clothesList.size();i++ ){
                Clothes clothes=clothesList.get(i);
                Log.d("AAAAA",clothes.getClothesTitle());
                if(clothesUid.contains(clothes.getUid())&&clothes.getBuffer()!=null){
                    filamentModel.populateScene(clothes.getBuffer(), clothes);
                    loadValue--;
                    Log.d("AAAA", "LOAD 1    "+loadValue);
                    if(loadValue==0){
                        Log.d("AAAA", "ALREADY LOAD1   "+loadValue);
                        loadValue=0;
                    }
                    a++;
                } else if(clothesUid.contains(clothes.getUid())&&clothes.getBuffer()==null){
                    TaskRunner taskRunner=new TaskRunner();
                    taskRunner.executeAsync(new LongRunningTask(clothes), (data) -> {
                        filamentModel.populateScene(data.getBuffer(), data);
                        loadValue--;
                        Log.d("AAAAA", "LOAD  "+loadValue);
                        if(loadValue==0){
                            loadValue=0;
                            Log.d("AAAAA", "ALREADY LOAD    "+loadValue);
                        }
                    });
                    a++;
                }
            }
        }
    }

    public void setTextInLookPrice(){
        if (lookPriceDollarLong==0 && lookPriceLong>0){
            lookPriceDollar.setVisibility(View.GONE);
        }else{
            lookPriceDollarString=String.valueOf(lookPriceDollarLong);
            if(lookPriceDollarLong<1000){
                lookPriceDollar.setText(" + "+lookPriceDollarString+"$");
            }else if(lookPriceDollarLong>1000 && lookPriceDollarLong<10000){
                lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 1)+"."+lookPriceDollarString.substring(1, 2)+"K"+"$");
            }
            else if(lookPriceDollarLong>10000 && lookPriceDollarLong<100000){
                lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 2)+"."+lookPriceDollarString.substring(2,3)+"K"+"$");
            }
            else if(lookPriceDollarLong>10000 && lookPriceDollarLong<100000){
                lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 2)+"."+lookPriceDollarString.substring(2,3)+"K"+"$");
            }else if(lookPriceDollarLong>100000 && lookPriceDollarLong<1000000){
                lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 3)+"K"+"$");
            }
            else if(lookPriceDollarLong>1000000 && lookPriceDollarLong<10000000){
                lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 1)+"KK"+"$");
            }
            else if(lookPriceDollarLong>10000000 && lookPriceDollarLong<100000000){
                lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 2)+"KK"+"$");
            }
        }
        if(lookPriceLong==0){
            schoolyCoin.setVisibility(View.GONE);
            lookPrice.setVisibility(View.GONE);
            lookPriceDollarString=String.valueOf(lookPriceDollarLong);
            checkCounts(lookPriceDollar, lookPriceDollarLong, lookPriceDollarString);
        }else {
            lookPriceString=String.valueOf(lookPriceLong);
            checkCounts(lookPrice, lookPriceLong, lookPriceString);
        }
    }

    public void checkCounts(TextView textView,Long count,String stringCount){
        if(count<1000){
            textView.setText(String.valueOf(count));
        }else if(count>1000 && count<10000){
            textView.setText(stringCount.substring(0, 1)+"."+stringCount.substring(1, 2)+"K");
        }
        else if(count>10000 && count<100000){
            textView.setText(stringCount.substring(0, 2)+"."+stringCount.substring(2,3)+"K");
        }else if(count>100000 && count<1000000){
            textView.setText(stringCount.substring(0, 3)+"K");
        }
        else if(count>1000000 && count<10000000){
            textView.setText(stringCount.substring(0, 1)+"KK");
        }
        else if(count>10000000 && count<100000000){
            textView.setText(stringCount.substring(0, 2)+"KK");
        }
    }

    public static Clothes addModelInScene(Clothes clothes)  {
        try {
            uri = new URI(clothes.getModel());
            buffer = RecentMethods.getBytes(uri.toURL());
            bufferToFilament= ByteBuffer.wrap(buffer);
            clothes.setBuffer(bufferToFilament);
            if(!allLoadClothesUid.contains(clothes.getUid())) {
                clothesList.add(clothes);
                allLoadClothesUid.add(clothes.getUid());
                Log.d("AAAA", "q   "+loadValue+"   "+clothes.getClothesTitle());
            }
            clothesUid.add(clothes.getUid());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return clothes;
    }

    public void loadPerson(UserInformation userInformation,LockableNestedScrollView lockableNestedScrollView,SurfaceView surfaceView){
        loadValue=1;
        if(userInformation.getPerson()==null){
            Log.d("AAAAA", "aaaasssh  "+userInformation.getNick());
            loadPersonBuffer(userInformation,lockableNestedScrollView,surfaceView);

        }else{
            if (userInformation.getPerson().getBody().getBuffer()==null){
                loadPersonBuffer(userInformation,lockableNestedScrollView,surfaceView);
            }else{
                Log.d("####", "aa    "+userInformation.getPerson());
                ArrayList<FacePart> facePartArrayList=new ArrayList<>();
                facePartArrayList.add(userInformation.getPerson().getBody());
                facePartArrayList.add(userInformation.getPerson().getBrows());
                facePartArrayList.add(userInformation.getPerson().getEars());
                facePartArrayList.add(userInformation.getPerson().getEyes());
                facePartArrayList.add(userInformation.getPerson().getHair());
                facePartArrayList.add(userInformation.getPerson().getHead());
                facePartArrayList.add(userInformation.getPerson().getLips());
                facePartArrayList.add(userInformation.getPerson().getNose());
                facePartArrayList.add(userInformation.getPerson().getMustache());
                facePartArrayList.add(userInformation.getPerson().getSkinColor());
                for(int i=0;i<facePartArrayList.size();i++){
                    FacePart facePart=facePartArrayList.get(i);
                    com.egormoroz.schooly.Color[] color = {new com.egormoroz.schooly.Color()};
                    if(facePart!=null){
                        if(facePart.getColorX()!=null && facePart.getColorY()!=null && facePart.getColorZ()!=null){
                            if(facePart.getColorX()!=-1f && facePart.getColorY()!=-1f && facePart.getColorZ()!=-1f) {
                                color[0] = new com.egormoroz.schooly.Color(facePart.getColorX(),
                                        facePart.getColorY(), facePart.getColorZ()
                                        , 0, 0, 0);
                            }
                        }
                        if(i==0){
                            try {
                                filamentModel.initShareFilament(surfaceView, facePart.getBuffer(), true, lockableNestedScrollView
                                        , "regularRender", true);
                                if(color[0].getColorX() !=null)
                                    filamentModel.changeColor(facePart.getPartType(),color[0] );
                                loadLookClothes();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }else{
                            filamentModel.populateSceneFacePart(facePart.getBuffer());
                            if(color[0].getColorX() !=null)
                                filamentModel.changeColor(facePart.getPartType(),color[0] );
                        }
                    }
                }
            }
        }
    }

    public void loadPersonBuffer(UserInformation userInformation,LockableNestedScrollView lockableNestedScrollView,SurfaceView surfaceView){
        com.egormoroz.schooly.Color colorBody=new com.egormoroz.schooly.Color();
        com.egormoroz.schooly.Color colorHair=new com.egormoroz.schooly.Color();
        com.egormoroz.schooly.Color colorBrows=new com.egormoroz.schooly.Color();
        RecentMethods.startLoadPerson(userInformation.getNick(), firebaseModel, new Callbacks.loadPerson() {
            @Override
            public void LoadPerson(Person person,ArrayList<FacePart> facePartArrayList) {
                LoadBodyParts.loadPersonBuffers(facePartArrayList, new Callbacks.loadFaceParts() {
                    @Override
                    public void LoadFaceParts(ArrayList<FacePart> facePartsArrayList) {
                        for(int i=0;i<facePartsArrayList.size();i++){
                            FacePart facePart=facePartsArrayList.get(i);
                            com.egormoroz.schooly.Color[] color = {new com.egormoroz.schooly.Color()};
                            if(facePart.getColorX()!=-1f && facePart.getColorY()!=-1f && facePart.getColorZ()!=-1f){
                                color[0] =new com.egormoroz.schooly.Color(facePartsArrayList.get(i).getColorX(),
                                        facePartsArrayList.get(i).getColorY(), facePartsArrayList.get(i).getColorZ()
                                        , 0, 0, 0);
                                switch (facePart.getPartType()) {
                                    case "body":
                                        colorBody.setColorX(facePart.getColorX());
                                        colorBody.setColorY(facePart.getColorY());
                                        colorBody.setColorZ(facePart.getColorZ());
                                        break;
                                    case "hair":
                                        colorHair.setColorX(facePart.getColorX());
                                        colorHair.setColorY(facePart.getColorY());
                                        colorHair.setColorZ(facePart.getColorZ());
                                        break;
                                    case "brows":
                                        colorBrows.setColorX(facePart.getColorX());
                                        colorBrows.setColorY(facePart.getColorY());
                                        colorBrows.setColorZ(facePart.getColorZ());
                                        break;
                                }
                            }
                            if(i==0){
                                try {
                                    filamentModel.initShareFilament(surfaceView, facePart.getBuffer(), true, lockableNestedScrollView
                                            , "regularRender", true);
                                    if(color[0].getColorX() !=null)
                                        filamentModel.changeColor(facePart.getPartType(), color[0]);
                                    loadLookClothes();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                filamentModel.populateSceneFacePart(facePart.getBuffer());
                                if(color[0].getColorX() !=null)
                                    filamentModel.changeColor(facePart.getPartType(), color[0]);
                            }
                        }
                        userInformation.setPerson(RecentMethods.setAllPerson(facePartsArrayList,"not",colorBody,colorHair,colorBrows));
                    }
                });
            }
        });
    }

    static class LongRunningTask implements Callable<Clothes> {
        private Clothes clothes;

        public LongRunningTask(Clothes clothes) {
            this.clothes = clothes;
        }

        @Override
        public Clothes call() {
            return addModelInScene(clothes);
        }
    }
}