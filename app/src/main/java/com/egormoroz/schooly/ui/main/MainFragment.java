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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.Person;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;

import com.egormoroz.schooly.ui.chat.Chat;
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
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.Buffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class MainFragment extends Fragment{

    TextView todayMiningMain,circleNontifications,circleChat,getMore;
    private FirebaseModel firebaseModel = new FirebaseModel();
    ArrayList<Clothes> clothesArrayList=new ArrayList<Clothes>();
    ArrayList<Nontification > noViewedNonts=new ArrayList<>();
    ArrayList<Chat> noViewedChatNots=new ArrayList<>();
    ArrayList<Clothes> popularClothesArrayList=new ArrayList<Clothes>();
    RecyclerView clothesRecyclerMain,myClothesRecycler;
    String todayMiningFormatted,nickname;
    NewClothesAdapter.ItemClickListener itemClickListener;
    private static final int NOTIFY_ID = 101;
    RelativeLayout relativeShop,relativeMining,relativeMyClothes,relativeFirstLayout,createClothes,relativeTodayMining;
    MyClothesAdapterMain.ItemClickListener itemClickListenerMyClothes;
    LinearLayout coinsLinear;
    long totalProfitLong,totalPurchaseLong,totalProfitDollarLong;
    private static final String CHANNEL_ID = "Tyomaa channel";
    CircularProgressIndicator circularProgressIndicator;
    LinearLayoutManager linearLayoutManager;
    int newPosition,reverse;


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
        newPosition=bundle.getInt("NEWPOSITION");
        reverse=bundle.getInt("REVERSE");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(linearLayoutManager!=null)
        bundle.putInt("NEWPOSITION",linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        bundle.putInt("REVERSE", reverse);
    }
    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nickname=userInformation.getNick();
        myClothesRecycler=view.findViewById(R.id.mychlothesmain);
        relativeFirstLayout=view.findViewById(R.id.relativeFirstClothes);
        createClothes=view.findViewById(R.id.createClothes);
        coinsLinear=view.findViewById(R.id.linearCoins);
        getMore=view.findViewById(R.id.getMore);
        circularProgressIndicator=view.findViewById(R.id.progressIndicator);
        relativeTodayMining=view.findViewById(R.id.relativeTodayMining);
        relativeTodayMining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(MiningFragment.newInstance(userInformation, bundle), getActivity());
            }
        });
//        UserPeopleAdapter userPeopleAdapter=new UserPeopleAdapter();
//        userPeopleAdapter.setNick("Loppi");
//        userPeopleAdapter.setBio("gg");
//        userPeopleAdapter.setAvatar("dd");
//        ArrayList<UserPeopleAdapter> u=new ArrayList<>();
//        u.add(userPeopleAdapter);
//        firebaseModel.getUsersReference().child("tyomaa6").child("Dialogs")
//                .child("Loppi").child("members").child("Loppi").setValue(userPeopleAdapter);
//        firebaseModel.getReference().child("usersNicks").child("Vladcpp")
//                .setValue(new UserPeopleAdapter("Vladcpp", "a", "я украинец"));
//        firebaseModel.getReference().child("usersNicks").child("tyomaa6")
//                .setValue(new UserPeopleAdapter("tyomaa6", "a", "ceo"));
//        firebaseModel.getReference("usersNicks")
//                .child("mashina").setValue(new UserPeopleAdapter("mashina","6"," "));
//        String uid=firebaseModel.getUsersReference().child("mashina").push().getKey();
//        firebaseModel.getUsersReference().child("mashina").setValue(new UserInformation("mashina", "+375445632156", uid,
//                "6", "12345678lol", "Helicopter", 1000, new ArrayList<>(),new ArrayList<>(),1,100,0, new ArrayList<>()
//                , new ArrayList<>(), ""," ","open","open","open","open",
//                new ArrayList<>(),"regular", new ArrayList<>(),0,new ArrayList<>(),new ArrayList<>()
//                ,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()
//                ,new ArrayList<Clothes>(),new Person(new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart("", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fma.glb?alt=media&token=f7430695-13cb-4365-8910-c61b59a96acf", "",b ),
//                new FacePart(), new FacePart()),
//                new ArrayList<>(),new ArrayList<>()));
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
        FirebaseModel appDataModel=new FirebaseModel();
        appDataModel.initAppDataDatabase();
//                        firebaseModel.getUsersReference().child(nick).child("clothesRequest")
//                        .child("-MxuHf_f26Lr39Vx2Tx8").setValue(new ClothesRequest("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                , 100, "Jordan 4", 123, nick, "coin", "d", " https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Funtitled.glb?alt=media&token=657b45d7-a84b-4f2a-89f4-a699029401f7"
//                , "shoes", "okey", "no", "no", "-MxuHf_f26Lr39Vx2Tx8"));
//                firebaseModel.getReference().child("users")
//                        .child(nick).child("nontifications")
//                        .child("-MxuHf_f26Lr39Vx2Tx8").setValue(new Nontification(nick,"не отправлено","запросодежда"
//                        ,"","Jordan 4","https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500",
//                        "не просмотрено","-MxuHf_f26Lr39Vx2Tx8",0));
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
//        reasonsArrayList.add(new Reason("Мошенничество"));
//        reasonsArrayList.add(new Reason("Насилие или опасные организации"));
//        reasonsArrayList.add(new Reason("Враждебные высказывания или символы"));
//        reasonsArrayList.add(new Reason("Продажа незаконных товаров"));
//        reasonsArrayList.add(new Reason("Нарушение прав на интеллектуальную собственность"));
//        firebaseModel.getReference().child("AppData").child("complains").setValue(reasonsArrayList);
        circleChat=view.findViewById(R.id.circleChat);
        circleNontifications=view.findViewById(R.id.circleNontifications);
        //circularProgressIndicator=view.findViewById(R.id.miningIndicator);

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
//
//                firebaseModel.getUsersReference().child("tyomaa6").child("myClothes").child("-N78dSmApk_OlaBoh-LA").setValue(new Clothes("shoes", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Funtitled%20(7).png?alt=media&token=3b195cab-df73-4186-925f-88d382270c5b"
//                               ,270,"Plurixx",344,123,"tyomaa6","coin"," ","https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2F%D0%B4%D0%B5%D1%84%D0%BE%D0%BB%D1%82%D0%BD%D1%8B%D0%B5.glb?alt=media&token=da3c2362-cb07-43f2-8cc9-fe842af18b97",
//                        72,"foot","-N78dSmApk_OlaBoh-LA","exclusive",null,0f,0f,0f,0f));
////
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

        TextView schoolycoins=view.findViewById(R.id.schoolycoins);
        schoolycoins.setText(String.valueOf(userInformation.getmoney()));
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
                RecentMethods.setCurrentFragment(GenderFragment.newInstance(userInformation,bundle,MainFragment.newInstance(userInformation, bundle),"dd"), getActivity());
            }
        });
        if(nickname==null){
            RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
                @Override
                public void PassUserNick(String nick) {
                    nickname=nick;
                    userInformation.setNick(nickname);
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
                    loadClothesFromBase();
                    checkChatNots();
                    checkNots();
                    getMyClothes();
                }
            });
        }else {
            todayMiningMain=view.findViewById(R.id.todayminingmain);
            todayMiningFormatted = new DecimalFormat("#0.00").format(userInformation.getTodayMining());
            todayMiningMain.setText("+"+todayMiningFormatted);
            RecentMethods.GetTodayMiningValue(nickname, firebaseModel, new Callbacks.GetTodayMining() {
                @Override
                public void GetTodayMining(double todayMiningFromBase) {
                    todayMiningFormatted = new DecimalFormat("#0.00").format(todayMiningFromBase);
                    todayMiningMain.setText("+"+todayMiningFormatted);
                }
            });
            if (userInformation.getMiners()==null){
                RecentMethods.GetActiveMiner(nickname, firebaseModel, new Callbacks.GetActiveMiners() {
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
            loadClothesFromBase();
            checkChatNots();
            checkNots();
            getMyClothes();
        }
    }

    public void checkNots(){
        if(userInformation.getNontifications()!=null){
            for (int i=0;i<userInformation.getNontifications().size();i++){
                Nontification nontification=userInformation.getNontifications().get(i);
                if(nontification.getType().equals("не просмотрено")){
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
            RecentMethods.getNontificationsList(nickname, firebaseModel, new Callbacks.getNontificationsList() {
                @Override
                public void getNontificationsList(ArrayList<Nontification> nontifications) {
                    userInformation.setNotifications(nontifications);
                    for (int i=0;i<nontifications.size();i++){
                        Nontification nontification=nontifications.get(i);
                        if(nontification.getType().equals("не просмотрено")){
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
            circularProgressIndicator.setVisibility(View.GONE);
            popularClothesArrayList = (ArrayList<Clothes>) bundle.getSerializable("MAIN_REC_CLOTHES");
            if(reverse==0)Collections.reverse(popularClothesArrayList);
            reverse++;
            NewClothesAdapter newClothesAdapter=new NewClothesAdapter(popularClothesArrayList,itemClickListener,userInformation);
            linearLayoutManager=new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            clothesRecyclerMain.setLayoutManager(linearLayoutManager);
            clothesRecyclerMain.setAdapter(newClothesAdapter);
                clothesRecyclerMain.scrollToPosition(newPosition);
            relativeShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).setCurrentFragment((ShopFragment.newInstance(userInformation,bundle,MainFragment.newInstance(userInformation, bundle))));
                }
            });
        }else {
            RecentMethods.getClothes(firebaseModel, new Callbacks.GetClothes() {
                @Override
                public void getClothes(ArrayList<Clothes> allClothes) {
                    clothesArrayList.addAll(allClothes);
                    Collections.reverse(allClothes);
                    circularProgressIndicator.setVisibility(View.GONE);
                    for(int i=0;i<clothesArrayList.size();i++){
                        Clothes cl=clothesArrayList.get(i);
                        popularClothesArrayList.add(cl);
//                        firebaseModel.getUsersReference().child("stanislove")
//                                .child("clothes").setValue(popularClothesArrayList);
                    }
                    bundle.putSerializable("MAIN_REC_CLOTHES",popularClothesArrayList);
                    if(reverse==0)Collections.reverse(popularClothesArrayList);
                    reverse++;
                    NewClothesAdapter newClothesAdapter=new NewClothesAdapter(popularClothesArrayList,itemClickListener,userInformation);
                    linearLayoutManager=new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    clothesRecyclerMain.setLayoutManager(linearLayoutManager);
                    clothesRecyclerMain.setAdapter(newClothesAdapter);
                    if(newPosition==0)
                        clothesRecyclerMain.scrollToPosition(newPosition);
                    relativeShop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((MainActivity)getActivity()).setCurrentFragment((ShopFragment.newInstance(userInformation,bundle,MainFragment.newInstance(userInformation, bundle))));
                        }
                    });
                }
            });
        }

    }

    public void checkChatNots(){
        if(userInformation.getChats()!=null){
            for (int i=0;i<userInformation.getChats().size();i++){
                Chat chat=userInformation.getChats().get(i);
                if(chat.getUnreadMessages()>0){
                    noViewedChatNots.add(chat);
                }
            }
            if(noViewedChatNots.size()>0){
                circleChat.setVisibility(View.VISIBLE);
                if(noViewedChatNots.size()>9){
                    circleChat.setText("9+");
                }else {
                    circleChat.setText(String.valueOf(noViewedChatNots.size()));
                }
            }
        }else {
            RecentMethods.getDialogs(nickname, firebaseModel, new Callbacks.loadDialogs() {
                @Override
                public void LoadData(ArrayList<Chat> dialogs, ArrayList<Chat> talksArrayList) {
                    ArrayList<Chat> allChats=new ArrayList<>();
                    allChats.addAll(dialogs);
                    allChats.addAll(talksArrayList);
                    userInformation.setTalksArrayList(talksArrayList);
                    userInformation.setChats(dialogs);
                    for (int i=0;i<allChats.size();i++){
                        Chat chat=allChats.get(i);
                        if(chat.getUnreadMessages()>0){
                            noViewedChatNots.add(chat);
                        }
                    }
                    if(noViewedChatNots.size()>0){
                        circleChat.setVisibility(View.VISIBLE);
                        if(noViewedChatNots.size()>9){
                            circleChat.setText("9+");
                        }else {
                            circleChat.setText(String.valueOf(noViewedChatNots.size()));
                        }
                    }
                }
            });
        }
    }

    public void getMyClothes(){
        if(userInformation.getMyClothes() == null){
            Query query=firebaseModel.getUsersReference().child(nickname)
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

}