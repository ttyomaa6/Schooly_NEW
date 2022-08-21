package com.egormoroz.schooly.ui.profile.Wardrobe;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.egormoroz.schooly.BufferLoader;
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
import com.egormoroz.schooly.TaskRunnerCustom;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WardrobeFragment extends Fragment {
    String type;
    static String nick;
    Fragment fragment;
    static UserInformation userInformation;
    static Bundle bundle;

    public WardrobeFragment(String type, Fragment fragment, UserInformation userInformation, Bundle bundle) {
        this.type = type;
        this.fragment = fragment;
        WardrobeFragment.userInformation = userInformation;
        WardrobeFragment.bundle = bundle;
    }

    public static WardrobeFragment newInstance(String type, Fragment fragment, UserInformation userInformation, Bundle bundle) {
        return new WardrobeFragment(type, fragment, userInformation, bundle);

    }

    static FirebaseModel firebaseModel = new FirebaseModel();
    private ViewPager2 viewPager;
    FragmentAdapter fragmentAdapter;
    ArrayList<Clothes> clothesFromBase;
    RecyclerView searchRecycler;
    EditText searchText;
    TabLayout tabLayout;
    int tabLayoutPosition;
    static int loadValue;
    TextView notFound;
    SurfaceView surfaceView;
    WardrodeClothesAdapter.ItemClickListener itemClickListener;
    LockableNestedScrollView lockableNestedScrollView;
    static Buffer bufferToFilament,b;
    static byte[] buffer;
    static URI uri;
    static ArrayList<String > allLoadClothesUid=new ArrayList<>();
    static FilamentModel filamentModel;
    static ArrayList<Clothes> clothesList=new ArrayList<>();
    static ArrayList<String> clothesUid=new ArrayList<>();
    int a=0;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wardrobe, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        filamentModel=new FilamentModel();
        if(bundle.getSerializable("CLOTHESUID")!=null){
            clothesUid= (ArrayList<String>) bundle.getSerializable("CLOTHESUID");
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
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick = userInformation.getNick();
        surfaceView=view.findViewById(R.id.surfaceViewWardrobe);
        searchText = view.findViewById(R.id.searchClothesWardrobe);
        searchRecycler = view.findViewById(R.id.searchRecycler);
        lockableNestedScrollView=view.findViewById(R.id.lockableNestedScrollView);
        if(bundle.getSerializable("ALLLOADCLOTHESLIST")!=null){
            clothesList= (ArrayList<Clothes>) bundle.getSerializable("ALLLOADCLOTHESLIST");
        }
        if(bundle.getSerializable("ALLLOADCLOTHESUID")!=null){
            allLoadClothesUid= (ArrayList<String>) bundle.getSerializable("ALLLOADCLOTHESUID");
        }
        itemClickListener=new WardrodeClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes,String type,String fragmentString) {
                if(type.equals("view")){
                    if(loadValue==0) RecentMethods.setCurrentFragment(ViewingClothesWardrobe.newInstance(type,WardrobeFragment.newInstance(type, fragment, userInformation, bundle),userInformation,bundle), getActivity());
                }else{
                    if(loadValue==0) checkOnTryOn(clothes);
                }
            }
        };
        loadPerson(userInformation, lockableNestedScrollView,surfaceView);
        notFound = view.findViewById(R.id.notFound);
        tabLayout = view.findViewById(R.id.tabLayoutWardrobe);
        viewPager = view.findViewById(R.id.frcontwardrobe);

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
                if(loadValue==0) RecentMethods.setCurrentFragment(ProfileFragment.newInstance(type, nick, fragment, userInformation, bundle), getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if(loadValue==0) RecentMethods.setCurrentFragment(ProfileFragment.newInstance(type, nick, fragment, userInformation, bundle), getActivity());
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
                            WardrodeClothesAdapter wardrobeClothesAdapter = new WardrodeClothesAdapter(clothesFromBase, itemClickListener, userInformation,"wardrobe");
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
                        WardrodeClothesAdapter wardrobeClothesAdapter = new WardrodeClothesAdapter(clothesFromBase, itemClickListener, userInformation,"wardrobe");
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
                    return new WardrobeClothes(type, WardrobeFragment.newInstance(type, fragment, userInformation, bundle), userInformation, bundle,"wardrobe");
                case 2:
                    return new WardrobeHats(type, WardrobeFragment.newInstance(type, fragment, userInformation, bundle), userInformation, bundle,"wardrobe");
                case 3:
                    return new WardrobeAccessories(type, WardrobeFragment.newInstance(type, fragment, userInformation, bundle), userInformation, bundle,"wardrobe");
            }

            return new WardrobeShoes(type, WardrobeFragment.newInstance(type, fragment, userInformation, bundle), userInformation, bundle,"wardrobe");
        }

        @Override
        public int getItemCount() {
            return 4;
        }
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
                                    clothesUid.add(clothes.getUid());
                                    if(!allLoadClothesUid.contains(clothes.getUid())) {
                                        clothesList.add(clothes);
                                        allLoadClothesUid.add(clothes.getUid());
                                        Log.d("AAAA", "q   "+loadValue+"   "+clothes.getClothesTitle());
                                    }
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
                    taskRunner.executeAsync(new WardrobeFragment.LongRunningTaskClothes(clothes), (data) -> {
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

    public static void tryOnClothes(Clothes clothes,Clothes maskClothes){
        int a=0;
        if(clothesList.size()!=0){
            for(int i=0;i<clothesList.size();i++){
                Clothes clothes1=clothesList.get(i);
                if(clothes.getUid().equals(clothes1.getUid()) && clothes.getBuffer()!=null){
                    if(maskClothes!=null){
                        filamentModel.setMask(maskClothes);
                    }
                    filamentModel.populateScene(clothes.getBuffer(),clothes);
                    clothesUid.add(clothes.getUid());
                    loadValue=0;
                    Log.d("####", "q  "+loadValue);
                    break;
                }else if(a==0 && i==clothesList.size()-1){
                    TaskRunner taskRunner=new TaskRunner();
                    taskRunner.executeAsync(new LongRunningTaskClothes(clothes), (data) -> {
                        if(maskClothes!=null){
                            filamentModel.setMask(maskClothes);
                        }
                        filamentModel.populateScene(data.getBuffer(), data);
                        loadValue=0;
                        Log.d("####", "w  "+loadValue);
                    });
                    break;
                }
            }
        }else {
            TaskRunner taskRunner=new TaskRunner();
            taskRunner.executeAsync(new LongRunningTaskClothes(clothes), (data) -> {
                if(maskClothes!=null){
                    filamentModel.setMask(maskClothes);
                }
                filamentModel.populateScene(data.getBuffer(), data);
                loadValue=0;
                Log.d("####", "r  "+loadValue);
            });
            a++;
        }
    }

    public static void checkOnTryOn(Clothes clothes){
        Log.d("####", "a   "+loadValue);
        if(loadValue==0){
            loadValue++;
            Log.d("####", "b   "+loadValue);
            makeClothesInvisible(clothes);
        }
    }

    public static void sentToViewingFrag(String type, Fragment fragment, UserInformation userInformation, Bundle bundle, Activity activity){
        if(loadValue==0){
            RecentMethods.setCurrentFragment(ViewingClothesWardrobe.newInstance(type,fragment,userInformation,bundle), activity);
        }
    }

    public static void makeClothesInvisible(Clothes clothes){
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
                Log.d("#####", "dd");
                tryOnClothes(clothes,null);
                a++;
            }
        }  else{
            for(int i=0;i<userInformation.getLookClothes().size();i++){
                Clothes clothes1=userInformation.getLookClothes().get(i);
                if(clothes1.getUid().equals(clothes.getUid())){
                    loadValue=0;
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
                        Log.d("#####", "aaaa  "+clothesUid.size());
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
                        if(clothesToChange.getClothesTitle()!=null){
                            Log.d("#####", "dd111   "+clothesToChange.getClothesTitle());
                            tryOnClothes(clothes,clothesToChange);
                        }else{
                            tryOnClothes(clothes,null);

                        }
                        a++;
                    }
                }
            }
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
            RecentMethods.startLoadPerson(userInformation.getNick(), firebaseModel, new Callbacks.loadPerson() {
                @Override
                public void LoadPerson(Person person,ArrayList<FacePart> facePartArrayList) {
                    Log.d("AAA","ss  "+person.getBody());
                    LoadBodyParts.loadPersonBuffers(facePartArrayList, new Callbacks.loadFaceParts() {
                        @Override
                        public void LoadFaceParts(ArrayList<FacePart> facePartsArrayList) {
                            Log.d("AAAAA","ss11  "+facePartsArrayList.get(0).getBuffer()+"   "+facePartsArrayList.get(0).getUid());
                            for(int i=0;i<facePartsArrayList.size();i++){
                                FacePart facePart=facePartsArrayList.get(i);
                                Log.d("AAAAA","ss22  "+facePartsArrayList.get(i).getBuffer()+"   "+facePart.getUid()+"   "+i);
                                if(i==0){
                                    try {
                                        filamentModel.initFilament(surfaceView, facePart.getBuffer(), true, lockableNestedScrollView
                                                , "regularRender", true);
                                        loadLookClothes();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    filamentModel.populateSceneFacePart(facePartsArrayList.get(i).getBuffer());
                                }
                            }
                            userInformation.setPerson(RecentMethods.setAllPerson(facePartsArrayList,"not"));
                        }
                    });
                }
            });

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
            facePartArrayList.add(userInformation.getPerson().getPirsing());
            facePartArrayList.add(userInformation.getPerson().getSkinColor());
            for(int i=0;i<facePartArrayList.size();i++){
                FacePart facePart=facePartArrayList.get(i);
                if(facePart!=null){
                    if(i==0){
                        try {
                            filamentModel.initFilament(surfaceView, facePart.getBuffer(), true, lockableNestedScrollView
                                    , "regularRender", true);
                            loadLookClothes();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }else{
                        filamentModel.populateSceneFacePart(facePart.getBuffer());
                    }
                }
            }
        }
    }

    static class LongRunningTaskClothes implements Callable<Clothes> {
        private Clothes clothes;

        public LongRunningTaskClothes(Clothes clothes) {
            this.clothes = clothes;
        }

        @Override
        public Clothes call() {
            return addModelInScene(clothes);
        }
    }

}
