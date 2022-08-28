package com.egormoroz.schooly.ui.main.Shop;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FilamentModel;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.LoadBodyParts;
import com.egormoroz.schooly.LockableNestedScrollView;
import com.egormoroz.schooly.Person;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.TaskRunner;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.profile.Wardrobe.CreateLookFragment;
import com.egormoroz.schooly.ui.profile.Wardrobe.ViewingClothesWardrobe;
import com.egormoroz.schooly.ui.profile.Wardrobe.WardrobeAccessories;
import com.egormoroz.schooly.ui.profile.Wardrobe.WardrobeClothes;
import com.egormoroz.schooly.ui.profile.Wardrobe.WardrobeFragment;
import com.egormoroz.schooly.ui.profile.Wardrobe.WardrodeClothesAdapter;
import com.egormoroz.schooly.ui.profile.Wardrobe.WardrobeHats;
import com.egormoroz.schooly.ui.profile.Wardrobe.WardrobeShoes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FittingFragment extends Fragment {
    String type;
    static String nick;
    Fragment fragment;
    static UserInformation userInformation;
    static Bundle bundle;
    static Clothes clothesFitting;

    public FittingFragment( Fragment fragment, UserInformation userInformation, Bundle bundle,Clothes clothesFitting) {
        this.fragment = fragment;
        FittingFragment.userInformation = userInformation;
        FittingFragment.bundle = bundle;
        FittingFragment.clothesFitting=clothesFitting;
    }

    public static FittingFragment newInstance( Fragment fragment, UserInformation userInformation, Bundle bundle,Clothes clothesFitting) {
        return new FittingFragment( fragment, userInformation, bundle,clothesFitting);

    }

    static FirebaseModel firebaseModel = new FirebaseModel();
    private ViewPager2 viewPager;
    FragmentAdapter fragmentAdapter;
    ArrayList<Clothes> clothesFromBase;
    RecyclerView searchRecycler;
    EditText searchText;
    TabLayout tabLayout;
    int tabLayoutPosition;
    TextView notFound;
    SurfaceView surfaceView;
    static ArrayList<Buffer> buffers;
    WardrodeClothesAdapter.ItemClickListener itemClickListener;
    LockableNestedScrollView lockableNestedScrollView;
    static byte[] buffer;
    static URI uri;
    static Buffer bufferToFilament,b;
    static FilamentModel filamentModel;
    static ArrayList<Clothes> clothesList=new ArrayList<>();
    static ArrayList<String> clothesUid=new ArrayList<>();
    static int loadValue;
    static ArrayList<String > allLoadClothesUid=new ArrayList<>();
    int a=0;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fitting, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        filamentModel=new FilamentModel();
        if(bundle.getSerializable("CLOTHESUID")!=null){
            clothesUid= (ArrayList<String>) bundle.getSerializable("CLOTHESUID");
            Log.d("####", "SS   "+clothesUid.size());
        }
        if(bundle.getSerializable("ALLLOADCLOTHESLIST")!=null){
            clothesList= (ArrayList<Clothes>) bundle.getSerializable("ALLLOADCLOTHESLIST");
            Log.d("####", "y   "+clothesList.size());
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putString("EDIT_WARDROBE_TAG", searchText.getText().toString().trim());
        bundle.putInt("TAB_INT_WARDROBE", tabLayoutPosition);
        bundle.putSerializable("CLOTHESUID", clothesUid);
        bundle.putSerializable("ALLLOADCLOTHESLIST", clothesList);
        bundle.putSerializable("ALLLOADCLOTHESUID", allLoadClothesUid);
        Log.d("####", "FINISH SIZE   "+clothesUid.size());
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick = userInformation.getNick();
        surfaceView=view.findViewById(R.id.surfaceViewWardrobe);
        searchText = view.findViewById(R.id.searchClothesWardrobe);
        searchRecycler = view.findViewById(R.id.searchRecycler);
        itemClickListener=new WardrodeClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes,String type,String fragmentString) {
                if(type.equals("view")){
                    if(loadValue==0)RecentMethods.setCurrentFragment(ViewingClothesWardrobe.newInstance(type,FittingFragment.newInstance(fragment, userInformation, bundle,clothes),userInformation,bundle), getActivity());
                }else{
                    if(loadValue==0)checkOnTryOn(clothes);
                }
            }
        };
        notFound = view.findViewById(R.id.notFound);
        tabLayout = view.findViewById(R.id.tabLayoutWardrobe);
        viewPager = view.findViewById(R.id.frcontwardrobe);
        lockableNestedScrollView=view.findViewById(R.id.lockableNestedScrollView);
        if(bundle.getSerializable("ALLLOADCLOTHESUID")!=null){
            allLoadClothesUid= (ArrayList<String>) bundle.getSerializable("ALLLOADCLOTHESUID");
        }
        loadPerson(userInformation, lockableNestedScrollView,surfaceView);

        if (bundle != null) {
            tabLayoutPosition = bundle.getInt("TAB_INT_WARDROBE");
            if (bundle.getString("EDIT_WARDROBE_TAG") != null) {
                String bundleEditText = bundle.getString("EDIT_WARDROBE_TAG").trim();
                if (bundleEditText.length() != 0) {
                    searchText.setText(bundleEditText);
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    loadSearchClothes(bundleEditText);
                }
            }
        }

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchText.getText().toString().length() > 0) {
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    loadSearchClothes(searchText.getText().toString());
                } else {
                    viewPager.setVisibility(View.VISIBLE);
                    searchRecycler.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    notFound.setVisibility(View.GONE);
                    FragmentManager fm = getChildFragmentManager();
                    fragmentAdapter = new FragmentAdapter(fm, getLifecycle());
                    viewPager.setAdapter(fragmentAdapter);
                    viewPager.setCurrentItem(tabLayoutPosition, false);

                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            tabLayoutPosition = tab.getPosition();
                            viewPager.setCurrentItem(tabLayoutPosition);
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

                    tabLayout.selectTab(tabLayout.getTabAt(tabLayoutPosition));
                    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            tabLayoutPosition = position;
                            tabLayout.selectTab(tabLayout.getTabAt(tabLayoutPosition));
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageView backfromwardrobe = view.findViewById(R.id.back_toprofile);
        backfromwardrobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadValue==0)RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if(loadValue==0)RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        FragmentManager fm = getChildFragmentManager();
        fragmentAdapter = new FragmentAdapter(fm, getLifecycle());
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(tabLayoutPosition, false);

        tabLayout.addTab(tabLayout.newTab().setText(getContext().getResources().getText(R.string.shoes)));
        tabLayout.addTab(tabLayout.newTab().setText(getContext().getResources().getText(R.string.clothes)));
        tabLayout.addTab(tabLayout.newTab().setText(getContext().getResources().getText(R.string.hats)));
        tabLayout.addTab(tabLayout.newTab().setText(getContext().getResources().getText(R.string.accessories)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayoutPosition = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.selectTab(tabLayout.getTabAt(tabLayoutPosition));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayoutPosition = position;
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    public void loadSearchClothes(String editTextText) {
        firebaseModel.getUsersReference().child(nick).child("clothes")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (userInformation.getClothes() == null) {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        clothesFromBase = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Clothes clothes = new Clothes();
                            clothes.setClothesImage(snap.child("clothesImage").getValue(String.class));
                            clothes.setClothesPrice(snap.child("clothesPrice").getValue(Long.class));
                            clothes.setPurchaseNumber(snap.child("purchaseNumber").getValue(Long.class));
                            clothes.setClothesType(snap.child("clothesType").getValue(String.class));
                            clothes.setClothesTitle(snap.child("clothesTitle").getValue(String.class));
                            clothes.setCreator(snap.child("creator").getValue(String.class));
                            clothes.setCurrencyType(snap.child("currencyType").getValue(String.class));
                            clothes.setDescription(snap.child("description").getValue(String.class));
                            clothes.setPurchaseToday(snap.child("purchaseToday").getValue(Long.class));
                            clothes.setModel(snap.child("model").getValue(String.class));
                            clothes.setBodyType(snap.child("bodyType").getValue(String.class));
                            clothes.setUid(snap.child("uid").getValue(String.class));
                            clothes.setX(snap.child("x").getValue(Float.class));
                            clothes.setY(snap.child("y").getValue(Float.class));
                            clothes.setZ(snap.child("z").getValue(Float.class));
                            clothes.setTransformRatio(snap.child("transformRatio").getValue(Float.class));
                            String clothesTitle = clothes.getClothesTitle();
                            String title = clothesTitle;
                            int valueLetters = editTextText.length();
                            title = title.toLowerCase();
                            if (title.length() < valueLetters) {
                                if (title.equals(editTextText))
                                    clothesFromBase.add(clothes);
                            } else {
                                title = title.substring(0, valueLetters);
                                if (title.equals(editTextText))
                                    clothesFromBase.add(clothes);
                            }
                        }
                        if (clothesFromBase.size() == 0) {
                            searchRecycler.setVisibility(View.GONE);
                            notFound.setVisibility(View.VISIBLE);
                        } else {
                            notFound.setVisibility(View.GONE);
                            searchRecycler.setVisibility(View.VISIBLE);
                            WardrodeClothesAdapter wardrobeClothesAdapter = new WardrodeClothesAdapter(clothesFromBase, itemClickListener, userInformation,"tryOn");
                            searchRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                            searchRecycler.setAdapter(wardrobeClothesAdapter);
                        }
                    }
                } else {
                    clothesFromBase = new ArrayList<>();
                    for (int i = 0; i < userInformation.getClothes().size(); i++) {
                        Clothes clothes = userInformation.getClothes().get(i);
                        String clothesTitle = clothes.getClothesTitle();
                        String title = clothesTitle;
                        int valueLetters = editTextText.length();
                        title = title.toLowerCase();
                        if (title.length() < valueLetters) {
                            if (title.equals(editTextText))
                                clothesFromBase.add(clothes);
                        } else {
                            title = title.substring(0, valueLetters);
                            if (title.equals(editTextText))
                                clothesFromBase.add(clothes);
                        }
                    }
                    if (clothesFromBase.size() == 0) {
                        searchRecycler.setVisibility(View.GONE);
                        notFound.setVisibility(View.VISIBLE);
                    } else {
                        notFound.setVisibility(View.GONE);
                        searchRecycler.setVisibility(View.VISIBLE);
                        WardrodeClothesAdapter wardrobeClothesAdapter = new WardrodeClothesAdapter(clothesFromBase, itemClickListener, userInformation,"tryOn");
                        searchRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        searchRecycler.setAdapter(wardrobeClothesAdapter);
                    }
                }
            }
        });
    }


    public class FragmentAdapter extends FragmentStateAdapter {
        public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position) {
                case 1:
                    return new WardrobeClothes(type, FittingFragment.newInstance(fragment, userInformation, bundle,clothesFitting), userInformation, bundle,"tryOn");
                case 2:
                    return new WardrobeHats(type, FittingFragment.newInstance(fragment, userInformation, bundle,clothesFitting), userInformation, bundle,"tryOn");
                case 3:
                    return new WardrobeAccessories(type, FittingFragment.newInstance( fragment, userInformation, bundle,clothesFitting), userInformation, bundle,"tryOn");
            }

            return new WardrobeShoes(type, FittingFragment.newInstance(fragment, userInformation, bundle,clothesFitting), userInformation, bundle,"tryOn");
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    public void loadLookClothes(){
        loadValue=1;
        Log.d("######", "k");
        if(clothesUid.size()==0 || clothesList.size()==0) {
            RecentMethods.getMyLookClothesOnce(nick, firebaseModel, new Callbacks.getLookClothes() {
                @Override
                public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                    if(clothesArrayList.size()>0){
                        loadValue=clothesArrayList.size();
                        for(int i=0;i<clothesArrayList.size();i++){
                            Clothes clothes=clothesArrayList.get(i);
                            if(!clothes.getUid().equals(clothesFitting.getUid())){
                                if(!clothes.getBodyType().equals(clothesFitting.getBodyType())){
                                    Log.d("#####", "11");
                                    TaskRunner taskRunner=new TaskRunner();
                                    taskRunner.executeAsync(new LongRunningTask(clothes), (data) -> {
                                        filamentModel.populateScene(data.getBuffer(), data);
                                        loadValue--;
                                        if(loadValue==0){
                                            loadValue=0;
                                        }
                                    });
                                }else{
                                    TaskRunner taskRunner=new TaskRunner();
                                    taskRunner.executeAsync(new LongRunningTask(clothes), (data) -> {
                                        filamentModel.setMask(clothes);
                                        loadValue--;
                                        if(loadValue==0){
                                            loadValue=0;
                                        }
                                    });
                                }
                            }else{
                                loadValue--;
                                if(loadValue==0) {
                                    loadValue = 0;
                                }
                                if(!allLoadClothesUid.contains(clothesFitting.getUid())) {
                                    clothesList.add(clothesFitting);
                                    allLoadClothesUid.add(clothesFitting.getUid());
                                    Log.d("AAAA", "q111   "+loadValue+"   "+clothesFitting.getClothesTitle());
                                }
                                Log.d("AAAA", "AAAA  "+loadValue+"   "+clothesFitting.getClothesTitle());
                                clothesUid.add(clothesFitting.getUid());
                            }
                        }
                    }else{
                        loadValue=0;
                    }
                }
            });
        } else{
            Log.d("####", "START LOAD SIZES   "+clothesList.size()+"     "+clothesUid.size());
            loadValue=clothesUid.size();
            for(int i=0;i<clothesList.size();i++ ){
                Clothes clothes=clothesList.get(i);
                Log.d("####", "TITLE   "+clothes.getClothesTitle());
                if(!clothes.getUid().equals(clothesFitting.getUid())){
                    if(clothesUid.contains(clothes.getUid())){
                        if(!clothes.getBodyType().equals(clothesFitting.getBodyType())){
                            if(clothes.getBuffer()!=null){
                                filamentModel.populateScene(clothes.getBuffer(), clothes);
                                loadValue--;
                                Log.d("####", "LOAD VAALUE1   "+loadValue);
                                if(loadValue==0){
                                    loadValue=0;
                                }
                                a++;
                            }    else{
                                TaskRunner taskRunner=new TaskRunner();
                                taskRunner.executeAsync(new LongRunningTask(clothes), (data) -> {
                                    filamentModel.populateScene(data.getBuffer(), data);
                                    loadValue--;
                                    Log.d("####", "LOAD VALUE2   "+loadValue);
                                    if(loadValue==0){
                                        loadValue=0;
                                    }
                                });
                                a++;
                            }
                        }else{
                            filamentModel.setMask(clothes);
                            loadValue--;
                            Log.d("####", "LOAD VALUE3   "+loadValue);
                            if(loadValue==0) {
                                loadValue = 0;
                            }
                            if (clothesUid.contains(clothes.getUid())){
                                a++;
                            }
                        }
                    }else{
                        Log.d("####", "LOAD VALUE4   "+loadValue);
                        if(i==clothesUid.size()-1) {
                            loadValue = 0;
                        }
                    }
                }else{
                    Log.d("####", "LOAD VALUE5   "+loadValue);
                    loadValue--;
                    if(loadValue==0) {
                        loadValue = 0;
                    }
                    if (clothesUid.contains(clothes.getUid())){
                        a++;
                    }
                }
            }
        }
    }

    public static void tryOnClothes(Clothes clothes,Clothes maskClothes){
        int a=0;
        loadValue++;
        if(clothesList.size()!=0){
            for(int i=0;i<clothesList.size();i++){
                Clothes clothes1=clothesList.get(i);
                if(clothes.getUid().equals(clothes1.getUid()) && clothes.getBuffer()!=null){
                    if(maskClothes!=null){
                        filamentModel.setMask(maskClothes);
                    }
                    filamentModel.populateScene(clothes.getBuffer(),clothes);
                    clothesUid.add(clothes.getUid());
                    Log.d("#####", "SIZEEEE2   "+clothesUid.size());
                    loadValue=0;
                    break;
                }else if(a==0 && i==clothesList.size()-1){
                    TaskRunner taskRunner=new TaskRunner();
                    taskRunner.executeAsync(new LongRunningTask(clothes), (data) -> {
                        if(maskClothes!=null){
                            filamentModel.setMask(maskClothes);
                        }
                        filamentModel.populateScene(data.getBuffer(), data);
                        loadValue=0;
                        Log.d("#####", "SIZEEEE1   "+clothesUid.size());
                    });
                    break;
                }
            }
        }else {
            TaskRunner taskRunner=new TaskRunner();
            taskRunner.executeAsync(new LongRunningTask(clothes), (data) -> {
                if(maskClothes!=null){
                    filamentModel.setMask(maskClothes);
                }
                filamentModel.populateScene(data.getBuffer(), data);
                loadValue=0;
                Log.d("#####", "SIZEEEE   "+clothesUid.size());
            });
            a++;
        }
    }

    public static void checkOnTryOn(Clothes clothes){
        Log.d("####", "a   "+loadValue);
        if(loadValue==0){
            Log.d("####", "b   "+loadValue);
            makeClothesInvisible(clothes);
        }
    }

    public static void sentToViewingFrag(String type, Fragment fragment, UserInformation userInformation, Bundle bundle, Activity activity,Clothes clothes){
        if(loadValue==0){
            RecentMethods.setCurrentFragment(ViewingClothesWardrobe.newInstance(type,fragment,userInformation,bundle), activity);
        }
    }

    public static void makeClothesInvisible(Clothes clothes){
        if(!clothes.getBodyType().equals(clothesFitting.getBodyType())){
            Log.d("####", "dd   2");
            String type=clothes.getBodyType();
            int a=0;
            int c=0;
            b=clothes.getBuffer();
            Clothes clothesToChange=new Clothes();
            if(userInformation.getLookClothes().size()==0){
                clothes.setBuffer(null);
                firebaseModel.getUsersReference().child(nick).child("lookClothes")
                        .child(clothes.getUid()).setValue(clothes);
                if(a==0){
                    clothes.setBuffer(b);
                    tryOnClothes(clothes,null);
                    a++;
                }
            }  else{
                for(int i=0;i<userInformation.getLookClothes().size();i++){
                    Clothes clothes1=userInformation.getLookClothes().get(i);
                    if(clothes1.getUid().equals(clothes.getUid())){
                        break;
                    }if(clothes1.getBodyType().equals(type)){
                        clothesToChange=clothes1;
                        c++;
                    }
                    if(i==userInformation.getLookClothes().size()-1){
                        if(c==1){
                            firebaseModel.getUsersReference().child(nick).child("lookClothes")
                                    .child(clothesToChange.getUid()).removeValue();
                            clothesUid.remove(clothesToChange.getUid());
                            clothes.setBuffer(null);
                            firebaseModel.getUsersReference().child(nick).child("lookClothes")
                                    .child(clothes.getUid()).setValue(clothes);
                        }     else{
                            clothes.setBuffer(null);
                            firebaseModel.getUsersReference().child(nick).child("lookClothes")
                                    .child(clothes.getUid()).setValue(clothes);
                        }
                        if(a==0){
                            clothes.setBuffer(b);
                            tryOnClothes(clothes,clothesToChange);
                            a++;
                        }
                    }
                }
            }
        }else{
            loadValue=0;
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

    public static Clothes addModelInScene(Clothes clothes)  {
        try {
            uri = new URI(clothes.getModel());
            buffer = RecentMethods.getBytes(uri.toURL());
            bufferToFilament= ByteBuffer.wrap(buffer);
            clothes.setBuffer(bufferToFilament);
            if(!clothes.getUid().equals(clothesFitting.getUid())){
                clothesUid.add(clothes.getUid());
                if(!allLoadClothesUid.contains(clothes.getUid())) {
                    clothesList.add(clothes);
                    allLoadClothesUid.add(clothes.getUid());
                    Log.d("AAAA", "q   "+loadValue+"   "+clothes.getClothesTitle());
                }
            }
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
                        if(facePart.getColorX()!=-1f && facePart.getColorY()!=-1f && facePart.getColorZ()!=-1f) {
                            color[0] = new com.egormoroz.schooly.Color(facePart.getColorX(),
                                    facePart.getColorY(), facePart.getColorZ()
                                    , 0, 0, 0);
                        }
                        if(i==0){
                            try {
                                filamentModel.initFilament(surfaceView, facePart.getBuffer(), true, lockableNestedScrollView
                                        , "regularRender", true);
                                if(color[0].getColorX() !=null)
                                    filamentModel.changeColor(facePart.getPartType(),color[0] );
                                loadTryOnClothes();
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
                                    filamentModel.initFilament(surfaceView, facePart.getBuffer(), true, lockableNestedScrollView
                                            , "regularRender", true);
                                    if(color[0].getColorX() !=null)
                                        filamentModel.changeColor(facePart.getPartType(), color[0]);
                                    loadTryOnClothes();
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

    public void loadTryOnClothes(){
        if(clothesUid.contains(clothesFitting.getUid())){
            loadValue=1;
            if(clothesFitting.getBuffer()!=null){
                filamentModel.populateScene(clothesFitting.getBuffer(), clothesFitting);
                loadValue=0;
                loadLookClothes();
            }    else{
                TaskRunner taskRunner=new TaskRunner();
                taskRunner.executeAsync(new LongRunningTask(clothesFitting), (data) -> {
                    clothesFitting=data;
                    filamentModel.populateScene(data.getBuffer(), data);
                    loadLookClothes();
                });
            }
        }else{
            loadValue=1;
            TaskRunner taskRunner=new TaskRunner();
            taskRunner.executeAsync(new LongRunningTask(clothesFitting), (data) -> {
                clothesFitting=data;
                filamentModel.populateScene(data.getBuffer(), data);
                loadLookClothes();
            });
        }
    }

}
