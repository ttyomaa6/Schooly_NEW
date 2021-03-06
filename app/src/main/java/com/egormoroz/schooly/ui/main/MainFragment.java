package com.egormoroz.schooly.ui.main;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;

import com.egormoroz.schooly.ui.coins.CoinsFragmentSecond;
import com.egormoroz.schooly.ui.chat.DialogsFragment;
import com.egormoroz.schooly.ui.main.Mining.Miner;
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.egormoroz.schooly.ui.main.MyClothes.CreateClothesFragment;
import com.egormoroz.schooly.ui.main.MyClothes.MyClothesAdapterMain;
import com.egormoroz.schooly.ui.main.MyClothes.MyClothesFragment;
import com.egormoroz.schooly.ui.main.MyClothes.ViewingMyClothesMain;
import com.egormoroz.schooly.ui.main.Nontifications.NontificationFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.egormoroz.schooly.ui.main.Shop.ShopFragment;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class MainFragment extends Fragment{

    TextView todayMiningMain,circleNontifications,circleChat,getMore;
    private FirebaseModel firebaseModel = new FirebaseModel();
    ArrayList<Clothes> clothesArrayList=new ArrayList<Clothes>();
    ArrayList<Nontification > noViewedNonts=new ArrayList<>();
    ArrayList<Clothes> popularClothesArrayList=new ArrayList<Clothes>();
    RecyclerView clothesRecyclerMain,myClothesRecycler;
    String todayMiningFormatted,nick;
    NewClothesAdapter.ItemClickListener itemClickListener;
    private static final int NOTIFY_ID = 101;
    RelativeLayout relativeShop,relativeMining,relativeMyClothes,relativeFirstLayout,createClothes;
    CircularProgressIndicator circularProgressIndicator;
    MyClothesAdapterMain.ItemClickListener itemClickListenerMyClothes;
    LinearLayout coinsLinear;
    long totalProfitLong,totalPurchaseLong,totalProfitDollarLong;
    private static final String CHANNEL_ID = "Tyomaa channel";


    UserInformation userInformation;
    Bundle bundle;

    public MainFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static MainFragment newInstance(UserInformation userInformation,Bundle bundle) {
        return new MainFragment(userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(View.VISIBLE);
        firebaseModel.initAll();
        return root;
    }
    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        myClothesRecycler=view.findViewById(R.id.mychlothesmain);
        relativeFirstLayout=view.findViewById(R.id.relativeFirstClothes);
        createClothes=view.findViewById(R.id.createClothes);
        coinsLinear=view.findViewById(R.id.linearCoins);
        getMore=view.findViewById(R.id.getMore);
//        UserPeopleAdapter userPeopleAdapter=new UserPeopleAdapter();
//        userPeopleAdapter.setNick("Loppi");
//        userPeopleAdapter.setBio("gg");
//        userPeopleAdapter.setAvatar("dd");
//        ArrayList<UserPeopleAdapter> u=new ArrayList<>();
//        u.add(userPeopleAdapter);
//        firebaseModel.getUsersReference().child("tyomaa6").child("Dialogs")
//                .child("Loppi").child("members").child("Loppi").setValue(userPeopleAdapter);
//        firebaseModel.getReference().child("usersNicks").child("Vladcpp")
//                .setValue(new UserPeopleAdapter("Vladcpp", "a", "?? ????????????????"));
//        firebaseModel.getReference().child("usersNicks").child("tyomaa6")
//                .setValue(new UserPeopleAdapter("tyomaa6", "a", "ceo"));

        getMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(MoreMoneyFragment.newInstance(MainFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
            }
        });
        coinsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(CoinsFragmentSecond.newInstance(MainFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
            }
        });
        relativeMyClothes=view.findViewById(R.id.relativeClothes);
        relativeMining=view.findViewById(R.id.relativeMining);
        relativeShop=view.findViewById(R.id.relativeshop);
//                        firebaseModel.getUsersReference().child(nick).child("clothesRequest")
//                        .child("-MxuHf_f26Lr39Vx2Tx8").setValue(new ClothesRequest("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                , 100, "Jordan 4", 123, nick, "coin", "d", " https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Funtitled.glb?alt=media&token=657b45d7-a84b-4f2a-89f4-a699029401f7"
//                , "shoes", "okey", "no", "no", "-MxuHf_f26Lr39Vx2Tx8"));
//                firebaseModel.getReference().child("users")
//                        .child(nick).child("nontifications")
//                        .child("-MxuHf_f26Lr39Vx2Tx8").setValue(new Nontification(nick,"???? ????????????????????","????????????????????????"
//                        ,"","Jordan 4","https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500",
//                        "???? ??????????????????????","-MxuHf_f26Lr39Vx2Tx8",0));
        ImageView chat=view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(DialogsFragment.newInstance(userInformation, bundle, MainFragment.newInstance(userInformation, bundle)), getActivity());
                //               ((Activity) getActivity()).overridePendingTransition(0, 0);
//                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
//                Uri intentUri =
//                        Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
//                                .appendQueryParameter("file", "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf")
//                                .appendQueryParameter("mode", "3d_only")
//                                .build();
//                sceneViewerIntent.setData(intentUri);
//                sceneViewerIntent.setPackage("com.google.ar.core");
//                startActivity(sceneViewerIntent);
//                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                startActivity(intent);
            }
        });
//        firebaseModel.getUsersReference().child("tyomaa6").child("subscription").child("Spaccacrani")
//                .setValue("Spaccacrani");
//        firebaseModel.getUsersReference().child("Spaccacrani").child("subscribers").child("tyomaa6")
//                .setValue("tyomaa6");


//        ArrayList<Reason> reasonsArrayList=new ArrayList<>();
//        reasonsArrayList.add(new Reason("??????????????????????????"));
//        reasonsArrayList.add(new Reason("?????????????? ?????? ?????????????? ??????????????????????"));
//        reasonsArrayList.add(new Reason("???????????????????? ???????????????????????? ?????? ??????????????"));
//        reasonsArrayList.add(new Reason("?????????????? ???????????????????? ??????????????"));
//        reasonsArrayList.add(new Reason("?????????????????? ???????? ???? ???????????????????????????????? ??????????????????????????"));
//        firebaseModel.getReference().child("AppData").child("complains").setValue(reasonsArrayList);
        circleChat=view.findViewById(R.id.circleChat);
        circleNontifications=view.findViewById(R.id.circleNontifications);
        circularProgressIndicator=view.findViewById(R.id.miningIndicator);

//        TextView getMore=view.findViewById(R.id.getMore);
//        getMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Uri stickerAssetUri =  Uri.parse("https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/miners%2Ffimw.png?alt=media&token=9798e9ea-15a0-4ef2-869b-63ce4dc95b78");
////                String sourceApplication = "com.egormoroz.schooly";
////
////                Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
////                intent.putExtra("source_application", sourceApplication);
////
////                intent.setType("image/*");
////                intent.putExtra("interactive_asset_uri", stickerAssetUri);
////                intent.putExtra("top_background_color", "#33FF33");
////                intent.putExtra("bottom_background_color", "#FF00FF");
////
////// Instantiate activity and verify it will resolve implicit intent
////                Activity activity = getActivity();
////                activity.grantUriPermission(
////                        "com.instagram.android", stickerAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
////                    activity.startActivityForResult(intent, 0);
//                //               }
//
//// Instantiate implicit intent with ADD_TO_STORY action,
//// sticker asset, and background colors
////                Intent intent = new Intent(Intent.ACTION_SEND);
////                intent.putExtra("source_application", sourceApplication);
////
////                intent.setType("image/jpg");
////                intent.putExtra("interactive_asset_uri", stickerAssetUri);
////
////// Instantiate activity and verify it will resolve implicit intent
////                startActivity(intent);
//            }
//        });
        ImageView nontifications=view.findViewById(R.id.nontification);
        nontifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setCurrentFragment(NontificationFragment.newInstance(userInformation,bundle));
//
            }
        });
        ImageView coin = view.findViewById(R.id.schoolycoin);
        coin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecentMethods.setCurrentFragment(CoinsFragmentSecond.newInstance(MainFragment.newInstance(userInformation, bundle), userInformation, bundle), getActivity());
            }
        });
        //              firebaseModel.getReference().child("usersNicks").child("Spaccacrani").setValue(new UserPeopleAdapter("Spaccacrani", "5", "hello"));
        //               String uid=firebaseModel.getUsersReference().child("tyomaa6").child("myClothes").push().getKey();
//                firebaseModel.getUsersReference().child("tyomaa6").child("myClothes").child(uid).setValue(new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
        //                       ,220,"Prada",344,123,"tyomaa6","coin"," ","",72,"foot",uid,"exclusive"));
//                firebaseModel.getUsersReference().child("Vladcpp").child("myClothes").child(uid).setValue(new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,220,"Blazer",344,123,"Vladcpp","coin"," ","",72,"foot",uid,"exclusive"));
//                String uid=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//                String uid1=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//                String uid2=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//                String uid3=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//                String uid4=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//                String uid5=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//                String uid6=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//                String uid7=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//                String uid8=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//                firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid).setValue(new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,120,"Jordan 1",0,123,"Schooly","dollar"," ","",0,"foot",uid,"no"));
//                firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid1).setValue(new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,120,"Jordan 1",0,123,"tyomaaa6","dollar"," ","",0,"foot",uid1,"exclusive"));
//                firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid2).setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,120,"Yeazzy",0,123,"Schooly","coin","great model","",0,"watches",uid2,"no"));
//                firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid6).setValue(new Clothes("hats", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,120,"Y-3",0,123,"Schooly","coin"," ","",0,"hat",uid6,"no"));
//                firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid3).setValue(new Clothes("clothes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,120,"Prada",0,123,"Schooly","coin"," ","",0,"t-shirt",uid3,"no"));
//              firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid4).setValue(new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,120,"Raf Simons",0,123,"Schooly","coin"," ","",0,"foot",uid4,"exclusive"));
//                firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid5).setValue(new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,120,"Martins",0,123,"Schooly","coin"," ","",0,"foot",uid5,"no"));
//                firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid7).setValue(new Clothes("clothes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,120,"Blazer",0,123,"Schooly","coin"," ","",0,"foot",uid7,"no"));
//                firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid8).setValue(new Clothes("clothes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                        ,120,"Christian Dior",0,123,"Schooly","coin"," ","",0,"foot",uid8,"no"));


//        firebaseModel.getUsersReference()
//                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful()){
//                    DataSnapshot snapshot=task.getResult();
//                    for(DataSnapshot snapshot1:snapshot.getChildren()){
//                        UserInformation userInformation=new UserInformation();
//                        userInformation.setNick(snapshot1.child("nick").getValue(String.class));
//                        if(userInformation.getNick().contains("fake")) {
//                            firebaseModel.getUsersReference()
//                                    .child(userInformation.getNick()).removeValue();
//                        }
//                    }                }
//            }
//        });

        relativeShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setCurrentFragment((ShopFragment.newInstance(userInformation,bundle,MainFragment.newInstance(userInformation, bundle))));
            }
        });
        if (userInformation.getMiners()==null){
            RecentMethods.GetActiveMiner(nick, firebaseModel, new Callbacks.GetActiveMiners() {
                @Override
                public void GetActiveMiners(ArrayList<Miner> activeMinersFromBase) {
                    userInformation.setMiners(activeMinersFromBase);
                    relativeMining.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecentMethods.setCurrentFragment(MiningFragment.newInstance(userInformation,bundle), getActivity());
                        }
                    });
                }
            });
        }else{
            relativeMining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecentMethods.setCurrentFragment(MiningFragment.newInstance(userInformation,bundle), getActivity());

                }
            });
        }

        TextView schoolycoins=view.findViewById(R.id.schoolycoins);
        schoolycoins.setText(String.valueOf(userInformation.getmoney()));
//        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
//            @Override
//            public void PassUserNick(String nick) {
//                RecentMethods.GetMoneyFromBase(nick, firebaseModel, new Callbacks.MoneyFromBase() {
//                    @Override
//                    public void GetMoneyFromBase(long money) {
//                        schoolycoins.setText(String.valueOf(money));
//                    }
//                });
//            }
//        });
        clothesRecyclerMain=view.findViewById(R.id.newchlothesinshop);
        itemClickListener=new NewClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                ((MainActivity)getActivity()).setCurrentFragment(ViewingClothes.newInstance(MainFragment.newInstance(userInformation,bundle),userInformation,bundle));
            }
        };
        TextView appName=view.findViewById(R.id.appname);
        createNotificationChannel();
        appName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(GenderFragment.newInstance(userInformation,bundle,MainFragment.newInstance(userInformation, bundle)), getActivity());
            }
        });
        todayMiningMain=view.findViewById(R.id.todayminingmain);
        todayMiningFormatted = new DecimalFormat("#0.00").format(userInformation.getTodayMining());
        todayMiningMain.setText("+"+todayMiningFormatted);
        RecentMethods.GetTodayMiningValue(nick, firebaseModel, new Callbacks.GetTodayMining() {
            @Override
            public void GetTodayMining(double todayMiningFromBase) {
                todayMiningFormatted = new DecimalFormat("#0.00").format(todayMiningFromBase);
                todayMiningMain.setText("+"+todayMiningFormatted);
            }
        });
        loadClothesFromBase();
        checkNots();
        getMyClothes();
    }

    public void checkNots(){
        if(userInformation.getNontifications()!=null){
            for (int i=0;i<userInformation.getNontifications().size();i++){
                Nontification nontification=userInformation.getNontifications().get(i);
                if(nontification.getType().equals("???? ??????????????????????")){
                    noViewedNonts.add(nontification);
                }
            }
            if(noViewedNonts.size()>0){
                circleNontifications.setVisibility(View.VISIBLE);
                if(noViewedNonts.size()>9){
                    circleNontifications.setText("9+");
                }else {
                    circleNontifications.setText(String.valueOf(noViewedNonts.size()));
                }
            }
        }else {
            RecentMethods.getNontificationsList(nick, firebaseModel, new Callbacks.getNontificationsList() {
                @Override
                public void getNontificationsList(ArrayList<Nontification> nontifications) {
                    userInformation.setNotifications(nontifications);
                    for (int i=0;i<nontifications.size();i++){
                        Nontification nontification=nontifications.get(i);
                        if(nontification.getType().equals("???? ??????????????????????")){
                            noViewedNonts.add(nontification);
                        }
                    }
                    if(noViewedNonts.size()>0){
                        circleNontifications.setVisibility(View.VISIBLE);
                        if(noViewedNonts.size()>9){
                            circleNontifications.setText("9+");
                        }else {
                            circleNontifications.setText(String.valueOf(noViewedNonts.size()));
                        }
                    }
                }
            });
        }
    }

    public void loadClothesFromBase(){
        if(bundle.getSerializable("MAIN_REC_CLOTHES")!=null){
            popularClothesArrayList = (ArrayList<Clothes>) bundle.getSerializable("MAIN_REC_CLOTHES");
            NewClothesAdapter newClothesAdapter=new NewClothesAdapter(popularClothesArrayList,itemClickListener,userInformation);
            clothesRecyclerMain.setAdapter(newClothesAdapter);
        }else {
            RecentMethods.getClothes(firebaseModel, new Callbacks.GetClothes() {
                @Override
                public void getClothes(ArrayList<Clothes> allClothes) {
                    clothesArrayList.addAll(allClothes);
                    for(int i=0;i<clothesArrayList.size();i++){
                        Clothes cl=clothesArrayList.get(i);
                        popularClothesArrayList.add(cl);
                    }
                    bundle.putSerializable("MAIN_REC_CLOTHES",popularClothesArrayList);
                    NewClothesAdapter newClothesAdapter=new NewClothesAdapter(popularClothesArrayList,itemClickListener,userInformation);
                    clothesRecyclerMain.setAdapter(newClothesAdapter);
                }
            });
        }

    }

    public void getMyClothes(){
        if(userInformation.getMyClothes() == null){
            Query query=firebaseModel.getUsersReference().child(nick)
                    .child("myClothes").orderByKey();
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<Clothes> clothesFromBase=new ArrayList<>();
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
                        clothes.setUid(snap.child("uid").getValue(String.class));
                        clothesFromBase.add(clothes);
                        if (clothes.getCurrencyType().equals("dollar")){
                            totalProfitDollarLong+=clothes.getPurchaseToday()*clothes.getClothesPrice();
                        }else {
                            totalProfitLong+=clothes.getPurchaseToday()*clothes.getClothesPrice();
                        }
                        totalPurchaseLong+=clothes.getPurchaseToday();
                    }
                    userInformation.setMyClothes(clothesFromBase);
                    relativeMyClothes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecentMethods.setCurrentFragment(MyClothesFragment.newInstance(clothesFromBase
                                    ,totalProfitLong,totalPurchaseLong,totalProfitDollarLong,userInformation,bundle), getActivity());
                        }
                    });
                    if(clothesFromBase.size()==0){
                        relativeFirstLayout.setVisibility(View.VISIBLE);
                        myClothesRecycler.setVisibility(View.GONE);
                        createClothes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecentMethods.setCurrentFragment(CreateClothesFragment.newInstance(MainFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
                            }
                        });
                        relativeMyClothes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecentMethods.setCurrentFragment(MyClothesFragment.newInstance(clothesFromBase
                                        ,totalProfitLong,totalPurchaseLong,totalProfitDollarLong,userInformation,bundle), getActivity());
                            }
                        });
                    }else {
                        itemClickListenerMyClothes=new MyClothesAdapterMain.ItemClickListener() {
                            @Override
                            public void onItemClick(Clothes clothes) {
                                RecentMethods.setCurrentFragment(ViewingMyClothesMain.newInstance(MainFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
                            }
                        };
                        relativeFirstLayout.setVisibility(View.GONE);
                        Collections.reverse(clothesFromBase);
                        MyClothesAdapterMain myClothesAdapterMain=new MyClothesAdapterMain(clothesFromBase, itemClickListenerMyClothes,userInformation);
                        myClothesRecycler.setAdapter(myClothesAdapterMain);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            if(userInformation.getMyClothes().size()==0){
                relativeFirstLayout.setVisibility(View.VISIBLE);
                myClothesRecycler.setVisibility(View.GONE);
                createClothes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.setCurrentFragment(CreateClothesFragment.newInstance(MainFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
                    }
                });
                relativeMyClothes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.setCurrentFragment(MyClothesFragment.newInstance(userInformation.getMyClothes()
                                ,totalProfitLong,totalPurchaseLong,totalProfitDollarLong,userInformation,bundle), getActivity());
                    }
                });
            }else {
                itemClickListenerMyClothes=new MyClothesAdapterMain.ItemClickListener() {
                    @Override
                    public void onItemClick(Clothes clothes) {
                        RecentMethods.setCurrentFragment(ViewingMyClothesMain.newInstance(MainFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
                    }
                };
                relativeFirstLayout.setVisibility(View.GONE);
                MyClothesAdapterMain myClothesAdapterMain=new MyClothesAdapterMain(userInformation.getMyClothes(), itemClickListenerMyClothes,userInformation);
                myClothesRecycler.setAdapter(myClothesAdapterMain);
                for( int i=0;i<userInformation.getMyClothes().size();i++){
                    Clothes clothes=userInformation.getMyClothes().get(i);
                    if (clothes.getCurrencyType().equals("dollar")){
                        totalProfitDollarLong+=clothes.getPurchaseToday()*clothes.getClothesPrice();
                    }else {
                        totalProfitLong+=clothes.getPurchaseToday()*clothes.getClothesPrice();
                    }
                    totalPurchaseLong+=clothes.getPurchaseToday();
                }
                relativeMyClothes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.setCurrentFragment(MyClothesFragment.newInstance(userInformation.getMyClothes()
                                ,totalProfitLong,totalPurchaseLong,totalProfitDollarLong,userInformation,bundle), getActivity());
                    }
                });
            }
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.chanel_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(getActivity(),NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void shareToInstagram() {
        Intent intent = getContext().getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");
            shareIntent.putExtra(Intent.EXTRA_STREAM, "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/miners%2Ffims.png?alt=media&token=adafb44e-3ac1-43a3-bde6-6f7c4315ee0c");
            shareIntent.setType("image/jpeg");
            startActivity(shareIntent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
            startActivity(intent);
        }
    }
}