package com.egormoroz.schooly;
import android.app.Activity;
import android.media.FaceDetector;
import android.os.Bundle;
import android.renderscript.Matrix4f;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.egormoroz.schooly.ui.chat.Chat;
import com.egormoroz.schooly.ui.coins.Transfer;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.Mining.Miner;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.Comment;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.egormoroz.schooly.ui.profile.Reason;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class RecentMethods {
    static Buffer b;
    public static void isNickCorrect(String nickname, DatabaseReference reference, TextView errorTextnickname) {
        Log.d("########", "Method: isNickCorrect");
        Log.d("########", "Reference: " + String.valueOf(reference));
        if (nickname.length() < 4) {
            showErrorMessage(ErrorList.ERROR_NICK_IS_TO_SHORT, errorTextnickname);
            return;
        }
        isNickUniqueFun(nickname, reference, new Callbacks.UniqueNick() {
            @Override
            public void uniqueNicknameCallback(boolean isUnique) {
                Log.d("########", "Is nick unique: " + isUnique);
                if (!isUnique)
                    showErrorMessage(ErrorList.NICK_IS_USED, errorTextnickname);
                else
                    showErrorMessage(ErrorList.NOTHING, errorTextnickname);
            }
        });
    }
    public static void isNickUniqueFun(String nickname, DatabaseReference ref, final Callbacks.UniqueNick callback) {
        Query query = ref.orderByChild("nick").equalTo(nickname);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.uniqueNicknameCallback(!snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public static void showErrorMessage(ErrorList tag, TextView errorText) {
        switch (tag) {
            case NOT_ENOUGH_SYMBOL_ERROR:
                errorText.setText("The minimum number of symbols is 8");
                break;
            case ONLY_ENGLISH_SYMBOLS_ERROR:
                errorText.setText("Оnly English symbols in the nickname");
                break;
            case INVALID_SYMBOLS_ERROR:
                errorText.setText("Invalid symbols");
                break;
            case NOT_ONLY_NUMBERS_ERROR:
                errorText.setText("The password must contain more than just numbers");
                break;
            case NOT_ONLY_LETTERS:
                errorText.setText("The password must contain more than just letters");
                break;
            case NOTHING:
                errorText.setText(" ");
                break;
            case NICK_IS_USED:
                errorText.setText("Nickname is already being used");
                break;
            case PHONE_ERROR:
                errorText.setText("Phone is not correct");
                break;
            case ERROR_EMPTY_PHONE:
                errorText.setText("You didn't enter your phone");
                break;
        }
    }
    public static void setCurrentFragment(Fragment fragment, Activity activity) {
        FragmentTransaction ft = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }
    public static String makeEmail(String phone) {
        String email = "schooly";
        for (int i = 1; i < phone.length(); i++)
            email += phone.toCharArray()[i];
        email += "@gmail.com";
        return email;
    }
    public static String getPhone(String email) {
        String res = email;
        res = res.replace("schooly", "");
        res = res.replace("@gmail.com", "");
        return "+" + res;
    }
    public static boolean saveData(DatabaseReference ref, FirebaseUser user, String nick, Bundle bundle, Activity activity
    ,String personImage,Person person,ArrayList<Clothes> defaultClothes,ArrayList<Clothes> mainLookClothes){

        UserInformation res = new UserInformation(nick, "unknown", user.getUid(),
                "6", "unknown", "Helicopter", 1000
                , new ArrayList<>(),new ArrayList<>(), 1,1000,0, new ArrayList<>(),new ArrayList<>(),
                "","","open","open","open",
                "open",new ArrayList<>(),"regular", new ArrayList<>(),0,mainLookClothes,defaultClothes,new ArrayList<>()
                ,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),mainLookClothes,person
        ,new ArrayList<>(),new ArrayList<>(),"",personImage,new ArrayList<>());
        ref.child(nick).setValue(res).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseModel firebaseModel=new FirebaseModel();
                firebaseModel.initAll();
                firebaseModel.getReference("usersNicks")
                        .child(nick).setValue(new UserPeopleAdapter(nick,personImage," "));
                RecentMethods.setCurrentFragment(MainFragment.newInstance(res, bundle),activity );
                ((MainActivity)activity).IsEntered();
                ((MainActivity)activity).checkMining();
            }
        });
        return nick.isEmpty();
    }
    public static void hasThisUserFun(FirebaseAuth AuthenticationBase, FirebaseUser user,
                                      Callbacks.hasGoogleUser callback) {
        if(user == null){
            callback.hasGoogleUserCallback(false);
            return;
        }
        String email = String.valueOf(user.getEmail());
        AuthenticationBase.fetchSignInMethodsForEmail(email).
                addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean has = !task.getResult().getSignInMethods().isEmpty();
                        callback.hasGoogleUserCallback(has);
                    }
                });
    }
    public static void hasThisUser(FirebaseAuth AuthenticationBase, FirebaseUser user,
                                   Callbacks.hasGoogleUser callback) {
        hasThisUserFun(AuthenticationBase, user, new Callbacks.hasGoogleUser() {
            @Override
            public void hasGoogleUserCallback(boolean hasThisUser) {
                callback.hasGoogleUserCallback(hasThisUser);
            }
        });
    }
    public static ArrayList<UserInformation> findUsers(String username) {
        ArrayList<UserInformation> result = new ArrayList<UserInformation>();

        return result;
    }
    public static void LoadUserDataByNickFun(FirebaseModel model,
                                             Callbacks.LoadUserDataInterface callback,
                                             String nick) {
        model.initAll();
        Query query = model.getReference("users").
                orderByChild("nick").equalTo(nick);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotParent) {
                ArrayList<UserInformation> data = new ArrayList<>();
                for (DataSnapshot snapshot : snapshotParent.getChildren()) {
                    UserInformation userData = new UserInformation();
                    userData.setAge(snapshot.child("age").getValue(Long.class));
                    userData.setAvatar(snapshot.child("avatar").getValue(String.class));
                    userData.setGender(snapshot.child("gender").getValue(String.class));
                    userData.setNick(snapshot.child("nick").getValue(String.class));
                    userData.setPassword(snapshot.child("password").getValue(String.class));
                    userData.setPhone(snapshot.child("phone").getValue(String.class));
                    userData.setUid(snapshot.child("uid").getValue(String.class));
                    userData.setQueue(snapshot.child("queue").getValue(String.class));
                    userData.setmoney(snapshot.child("money").getValue(Long.class));
                    userData.setTodayMining(snapshot.child("todayMining").getValue(Double.class));
                    userData.setPerson(snapshot.child("person").getValue(Person.class));
                    userData.setPersonImage(snapshot.child("personImage").getValue(String.class));
                    data.add(userData);
                }
                callback.LoadData(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void LoadUserDataByNick(FirebaseModel model,
                                          String nick,
                                          Callbacks.PassLoadUserDataInterface passData) {
        LoadUserDataByNickFun(model, new Callbacks.LoadUserDataInterface() {
            @Override
            public void LoadData(ArrayList<UserInformation> data) {
                passData.PassData(data);
            }
        }, nick);
    }

    public static void UserNickByUid(String uid, FirebaseModel model, Callbacks.GetUserNickByUid callback) {
        model.initAll();
        Query query = model.getUsersReference().orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren())
                    callback.PassUserNick(snap.child("nick").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void hasUid(String uid, FirebaseModel model, Callbacks.HasUid callback){
        model.initAll();
        Query query = model.getReference("users")
                .orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.HasUidCallback(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getBio(String nick, FirebaseModel model, Callbacks.GetBio callback){
        model.initAll();
        Query query =model.getUsersReference().child(nick).child("bio");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.GetBiography(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getTransferHistory(String nick,FirebaseModel firebaseModel,Callbacks.getTransferHistory callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getUsersReference().child(nick).child("transferHistory").orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Transfer> transferHistoryBase=new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Transfer transfer=new Transfer();
                    transfer.setSum(snap.child("sum").getValue(Long.class));
                    transfer.setType(snap.child("type").getValue(String.class));
                    transfer.setWho(snap.child("who").getValue(String.class));
                    transferHistoryBase.add(transfer);
                }
                callback.getTransferHistory(transferHistoryBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getAlreadySearched(String nick,FirebaseModel firebaseModel,Callbacks.GetAlreadySearched callback){
        firebaseModel.initAll();
        firebaseModel.getUsersReference().child(nick).child("alreadySearched").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserPeopleAdapter> searchedUserFromBase=new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    UserPeopleAdapter upaSearch=new UserPeopleAdapter();
                    upaSearch.setNick(snap.child("nick").getValue(String.class));
                    upaSearch.setBio(snap.child("bio").getValue(String.class));
                    upaSearch.setAvatar(snap.child("avatar").getValue(String.class));
                    searchedUserFromBase.add(upaSearch);
                }
                callback.getAlreadySearched(searchedUserFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ///////////////////////MINING///////////////////////////////

    public static void AllminersFromBase(FirebaseModel model, Callbacks.GetMinerFromBase callback) {
        model.initAll();
        Query query = model.getReference("AppData/AllMiners");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Miner> minersFromBase=new ArrayList<>();
                for (DataSnapshot snap : snapshot.child("Weak").getChildren()) {
                    Miner miner = new Miner();
                    miner.setInHour(snap.child("inHour").getValue(Long.class));
                    miner.setMinerPrice(snap.child("minerPrice").getValue(Long.class));
                    miner.setMinerImage(snap.child("minerImage").getValue(String.class));
                    minersFromBase.add(miner);
                }
                callback.GetMinerFromBase(minersFromBase);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void AverageMinersFromBase(FirebaseModel model, Callbacks.GetMinerFromBase callback) {
        model.initAll();
        Query query = model.getReference("AppData/AllMiners");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Miner> minersFromBase=new ArrayList<>();
                for (DataSnapshot snap : snapshot.child("Average").getChildren()) {
                    Miner miner = new Miner();
                    miner.setInHour(snap.child("inHour").getValue(Long.class));
                    miner.setMinerPrice(snap.child("minerPrice").getValue(Long.class));
                    miner.setMinerImage(snap.child("minerImage").getValue(String.class));
                    minersFromBase.add(miner);
                }
                callback.GetMinerFromBase(minersFromBase);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void StrongMinersFromBase(FirebaseModel model, Callbacks.GetMinerFromBase callback) {
        model.initAll();
        Query query = model.getReference("AppData/AllMiners");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Miner> minersFromBase=new ArrayList<>();
                for (DataSnapshot snap : snapshot.child("Strong").getChildren()) {
                    Miner miner = new Miner();
                    miner.setInHour(snap.child("inHour").getValue(Long.class));
                    miner.setMinerPrice(snap.child("minerPrice").getValue(Long.class));
                    miner.setMinerImage(snap.child("minerImage").getValue(String.class));
                    Log.d("####", "one more");
                    minersFromBase.add(miner);
                }
                callback.GetMinerFromBase(minersFromBase);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void buyWeakMiner(String currentMiner,FirebaseModel model,Callbacks.buyMiner callback){
        model.initAll();
        Query query=model.getReference("AppData/AllMiners/Weak")
                .orderByChild(currentMiner);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Miner miner=snapshot.child(currentMiner).getValue(Miner.class);
                callback.buyMiner(miner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void buyAverageMiner(String currentMiner,FirebaseModel model,Callbacks.buyMiner callback){
        model.initAll();
        Query query=model.getReference("AppData/AllMiners/Average")
                .orderByChild(currentMiner);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Miner miner=snapshot.child(currentMiner).getValue(Miner.class);
                callback.buyMiner(miner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void buyStrongMiner(String currentMiner,FirebaseModel model,Callbacks.buyMiner callback){
        model.initAll();
        Query query=model.getReference("AppData/AllMiners/Strong")
                .orderByChild(currentMiner);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Miner miner=snapshot.child(currentMiner).getValue(Miner.class);
                callback.buyMiner(miner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void MyMinersFromBase(String nick,FirebaseModel model, Callbacks.GetMyMinerFromBase callback) {
        model.initAll();
        Query query = model.getUsersReference().child(nick)
                .child("miners");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Miner> myMinersFromBase=new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Miner miner = new Miner();
                    miner.setInHour(snap.child("inHour").getValue(Long.class));
                    miner.setMinerPrice(snap.child("minerPrice").getValue(Long.class));
                    miner.setMinerImage(snap.child("minerImage").getValue(String.class));
                    myMinersFromBase.add(miner);
                }
                callback.GetMyMinerFromBase(myMinersFromBase);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void GetActiveMiner(String nick,FirebaseModel model, Callbacks.GetActiveMiners callback) {
        model.initAll();
        if(nick!=null){
            if(model.getUsersReference().child(nick).child("activeMiners")!=null){
                model.getUsersReference().child(nick).child("activeMiners")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<Miner> activeMinersFromBase=new ArrayList<>();
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Miner miner = new Miner();
                                    miner.setInHour(snap.child("inHour").getValue(Long.class));
                                    miner.setMinerPrice(snap.child("minerPrice").getValue(Long.class));
                                    miner.setMinerImage(snap.child("minerImage").getValue(String.class));
                                    activeMinersFromBase.add(miner);
                                }
                                callback.GetActiveMiners(activeMinersFromBase);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        }
    }

    public static void GetMoneyFromBase(String nick,FirebaseModel firebaseModel,Callbacks.MoneyFromBase callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getUsersReference().child(nick)
                .child("money");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    callback.GetMoneyFromBase(snapshot.getValue(Long.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void GetTodayMining(String nick,FirebaseModel model,Callbacks.GetTodayMining callback){
        model.initAll();
        model.getUsersReference().child(nick)
                .child("todayMining").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot=task.getResult();
                    if (snapshot.getValue(Double.class) != null)
                        callback.GetTodayMining(snapshot.getValue(Double.class));
                }
            }
        });
    }
    public static void GetTodayMiningValue(String nick,FirebaseModel model,Callbacks.GetTodayMining callback){
        model.initAll();
        model.getUsersReference().child(nick)
                .child("todayMining").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Double.class) != null)
                    callback.GetTodayMining(snapshot.getValue(Double.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void GetTimesTamp(String nick, FirebaseModel firebaseModel,Callbacks.GetTimesTamp callback){
        firebaseModel.initAll();
        Query query =firebaseModel.getUsersReference().child(nick).child("timesTamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.GetTimesTamp(snapshot.getValue(Long.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void GetTimeStampNow(String nick, FirebaseModel firebaseModel,Callbacks.GetTimesTamp callback){
        firebaseModel.initAll();
        Query query =firebaseModel.getUsersReference().child(nick).child("serverTimeNow");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.GetTimesTamp(snapshot.getValue(Long.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //////////////////////////////////////////////////////////////
    ////////////////////SHOP//////////////////////////////////////

    public static void getClothes(FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        firebaseModel.getReference("AppData/Clothes/AllClothes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot=task.getResult();
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
                        clothes.setBodyType(snap.child("bodyType").getValue(String.class));
                        clothes.setUid(snap.child("uid").getValue(String.class));
                        clothes.setExclusive(snap.child("exclusive").getValue(String.class));
                        clothes.setX(snap.child("x").getValue(Float.class));
                        clothes.setY(snap.child("y").getValue(Float.class));
                        clothes.setZ(snap.child("z").getValue(Float.class));
                        clothes.setTransformRatio(snap.child("transformRatio").getValue(Float.class));
                        clothesFromBase.add(clothes);
                    }
                    callback.getClothes(clothesFromBase);
                }
            }
        });
    }

    public static void getPopular(FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getReference("AppData/Clothes/AllClothes");
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
                    clothes.setBodyType(snap.child("bodyType").getValue(String.class));
                    clothes.setUid(snap.child("uid").getValue(String.class));
                    clothes.setExclusive(snap.child("exclusive").getValue(String.class));
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void getNewClothes(FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getReference("AppData/Clothes/AllNew");
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
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void getShoes(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getReference("AppData/Clothes/Shoes");
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
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void getClothesFromBase(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getReference("AppData/Clothes/Clothes");
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
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getHats(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getReference("AppData/Clothes/Hats");
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
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getAsseccories(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getReference("AppData/Clothes/Asseccories");
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
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getClothesInBasket(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getUsersReference().child(nick).child("basket");
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
                    clothes.setExclusive(snap.child("exclusive").getValue(String.class));
                    clothes.setX(snap.child("x").getValue(Float.class));
                    clothes.setY(snap.child("y").getValue(Float.class));
                    clothes.setZ(snap.child("z").getValue(Float.class));
                    clothes.setTransformRatio(snap.child("transformRatio").getValue(Float.class));
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getDefaultLookClothes(String path,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAppDataDatabase();
        Query query=firebaseModel.getReference().child(path);
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
                    clothes.setExclusive(snap.child("exclusive").getValue(String.class));
                    clothes.setX(snap.child("x").getValue(Float.class));
                    clothes.setY(snap.child("y").getValue(Float.class));
                    clothes.setZ(snap.child("z").getValue(Float.class));
                    clothes.setTransformRatio(snap.child("transformRatio").getValue(Float.class));
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /////////////////////////Wardrobe////////////////////////
    public static void getClothesInWardrobe(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getUsersReference().child(nick)
                .child("clothes").orderByKey();
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
                    clothes.setCurrencyType(snap.child("currencyType").getValue(String.class));
                    clothes.setCreator(snap.child("creator").getValue(String.class));
                    clothes.setDescription(snap.child("description").getValue(String.class));
                    clothes.setPurchaseToday(snap.child("purchaseToday").getValue(Long.class));
                    clothes.setModel(snap.child("model").getValue(String.class));
                    clothes.setBodyType(snap.child("bodyType").getValue(String.class));
                    clothes.setUid(snap.child("uid").getValue(String.class));
                    clothes.setExclusive(snap.child("exclusive").getValue(String.class));
                    clothes.setX(snap.child("x").getValue(Float.class));
                    clothes.setY(snap.child("y").getValue(Float.class));
                    clothes.setZ(snap.child("z").getValue(Float.class));
                    clothes.setTransformRatio(snap.child("transformRatio").getValue(Float.class));
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getShoesInWardrobe(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getUsersReference().child(nick)
                .child("clothes").child("shoes");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Clothes> clothesFromBase=new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Clothes clothes = new Clothes();
                    clothes.setClothesImage(snap.child("clothesImage").getValue(String.class));
                    clothes.setClothesPrice(snap.child("clothesPrice").getValue(Long.class));
                    clothes.setClothesType(snap.child("clothesType").getValue(String.class));
                    clothes.setPurchaseNumber(snap.child("purchaseNumber").getValue(Long.class));
                    clothes.setClothesTitle(snap.child("clothesTitle").getValue(String.class));
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getHatsInWardrobe(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getUsersReference().child(nick)
                .child("clothes").child("hats");
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
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getAccessoriesInWardrobe(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getUsersReference().child(nick)
                .child("clothes").child("accessories");
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
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ////////////////////////// Friends System /////////////////////
//    public static void getFriendsList(String nickName, FirebaseModel model, Callbacks.getFriendsList callback){
//        model.initAll();
//        Query query = model.getUsersReference().orderByChild("nick").equalTo(nickName);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<String> friendsList = new ArrayList<>();
//                for(DataSnapshot snap : snapshot.getChildren()){
//                    String[] friends = (snap.child("friends").getValue(String.class)).split("#");
//                    for(String friend : friends)
//                        friendsList.add(friend);
//                }
//                callback.getFriendsList(friendsList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
    public static void FriendQueryAccapted(String userName1, String userName2, FirebaseModel model){
        model.initAll();
        Query query = model.getUsersReference().child("nick").equalTo(userName1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    String friends = snap.child("friends").getValue(String.class) + "#" + userName2;
                    String subscribers = snap.child("subscribers").getValue(String.class);
                    String queue = snap.child("queue").getValue(String.class);
                    queue = queue.replace("#" + userName2, "");
                    subscribers = subscribers.replace("#" + userName2, "");
                    model.getUsersReference().child(userName1).child("friends").setValue(friends);
                    model.getUsersReference().child(userName1).child("subscribers").setValue(subscribers);
                    model.getUsersReference().child(userName1).child("queue").setValue(queue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        query = model.getUsersReference().child("nick").equalTo(userName2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    String friends = snap.child("friends").getValue(String.class) + "#" + userName1;
                    model.getUsersReference().child(userName2).child("friends").setValue(friends);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getAmountOfFriends(String uid, FirebaseModel model, Callbacks.getAmountOfFriends callback){
        model.initAll();
        Query query = model.getUsersReference().child("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    int cnt = 0;
                    String friendsList = snap.child("friends").getValue(String.class);
                    if(friendsList.isEmpty()) {
                        callback.getAmountOfFriends(0);
                        break;
                    }
                    for(char c : friendsList.toCharArray())
                        if(c == '#')
                            cnt++;
                    callback.getAmountOfFriends(cnt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    ////////////////////////// Subscribers System /////////////////
//    public static void getSubscribersList(String nickName, FirebaseModel model, Callbacks.getSubscribersList callback){
//        model.initAll();
//        Query query = model.getUsersReference().child("nick").equalTo(nickName);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<String> subscribersList = new ArrayList<>();
//                for(DataSnapshot snap : snapshot.getChildren()){
//                    String[] subscribers = (snap.child("subscribers").getValue(String.class)).split("#");
//                    for(String subscriber : subscribers)
//                        subscribersList.add(subscriber);
//                }
//                Log.d("######", "int"+subscribersList);
//                callback.getSubscribersList(subscribersList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
    public static void friendQuery(String userName1, String userName2, FirebaseModel model){
        model.initAll();
        Query query = model.getUsersReference().child("nick").equalTo(userName1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    String subscribers = snap.child("subscribers").getValue(String.class) + "#" + userName2;
                    String queue = snap.child("queue").getValue(String.class) + "#" + userName2;
                    model.getUsersReference().child(userName1).child("subscribers").setValue(subscribers);
                    model.getUsersReference().child(userName1).child("queue").setValue(queue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void getAmountOfSubscribers(String uid, FirebaseModel model, Callbacks.getAmountOfSubscribers callback){
        model.initAll();
        Query query = model.getUsersReference().child("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    int cnt = 0;
                    String subscribersList = snap.child("subscribers").getValue(String.class);
                    if(subscribersList.isEmpty()) {
                        callback.getAmountOfSubscribers(0);
                        break;
                    }
                    for(char c : subscribersList.toCharArray())
                        if(c == '#')
                            cnt++;
                    callback.getAmountOfSubscribers(cnt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void checkSubscribers(String nick, FirebaseModel model,  Callbacks.getSubscribersList callback){
        Query query=model.getUsersReference().child(nick).child("subscribers");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ArrayList<Subscriber> subscribersList = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    Subscriber subscriber=new Subscriber();
                    subscriber.setSub(snap.child("sub").getValue(String.class));
                    subscribersList.add(subscriber);
                }
                Log.d("###", "name1"+subscribersList);
                callback.getSubscribersList(subscribersList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getSubscribersList(String nickName, FirebaseModel model, Callbacks.getSubscribersList callback){
        model.initAll();
        Query query=model.getUsersReference().child(nickName).child("subscribers");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Subscriber> subscribersList = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Subscriber subscriber=new Subscriber();
                    subscriber.setSub(snap.getValue(String.class));
                    subscribersList.add(subscriber);
                }
                callback.getSubscribersList(subscribersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public static void getBlackList(String nickName, FirebaseModel model, Callbacks.getSubscribersList callback){
        model.initAll();
        Query query=model.getUsersReference().child(nickName).child("blackList");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Subscriber> subscribersList = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Subscriber subscriber=new Subscriber();
                    subscriber.setSub(snap.getValue(String.class));
                    subscribersList.add(subscriber);
                }
                callback.getSubscribersList(subscribersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getSavedLooks(String nickName, FirebaseModel model, Callbacks.getSavedLook callback){
        model.initAll();
        Query query=model.getUsersReference().child(nickName).child("saved").orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<NewsItem> savedArrayList = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()){
                    NewsItem newsItem=new NewsItem();
                    newsItem=snap.getValue(NewsItem.class);
                    savedArrayList.add(newsItem);
                }
                callback.getSavedLook(savedArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void getLooksList(String nickName, FirebaseModel model, Callbacks.getLooksList callback){
        Log.d("#####", "Firebase model: " + model.getReference());
        model.initNewsDatabase();
        model.getReference().child(nickName).orderByKey()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<NewsItem> lookList = new ArrayList<>();
                        for (DataSnapshot snap:snapshot.getChildren()){
                            NewsItem newsItem=new NewsItem();
                            newsItem.setImageUrl(snap.child("imageUrl").getValue(String.class));
                            newsItem.setLookPrice(snap.child("lookPrice").getValue(Long.class));
                            newsItem.setItem_description(snap.child("item_description").getValue(String.class));
                            newsItem.setNewsId(snap.child("newsId").getValue(String.class));
                            newsItem.setLikesCount(snap.child("likes_count").getValue(String.class));
                            newsItem.setViewCount(snap.child("viewCount").getValue(Long.class));
                            newsItem.setPostTime(snap.child("postTime").getValue(String.class));
                            newsItem.setNick(snap.child("nick").getValue(String.class));
                            newsItem.setLookPriceDollar(snap.child("lookPriceDollar").getValue(Long.class));
                            lookList.add(newsItem);
                        }
                        callback.getLooksList(lookList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static void getNontificationsList(String nickName, FirebaseModel model, Callbacks.getNontificationsList callback){
        model.initAll();
        model.getUsersReference().child(nickName).child("nontifications").orderByKey()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Nontification> nontificationArrayList = new ArrayList<>();
                        for (DataSnapshot snap:snapshot.getChildren()){
                            Nontification nontification=new Nontification();
                            nontification.setNick(snap.child("nick").getValue(String.class));
                            nontification.setTypeDispatch(snap.child("typeDispatch").getValue(String.class));
                            nontification.setTypeView(snap.child("typeView").getValue(String.class));
                            nontification.setTimestamp(snap.child("timestamp").getValue(String.class));
                            nontification.setClothesName(snap.child("clothesName").getValue(String.class));
                            nontification.setClothesImage(snap.child("clothesImage").getValue(String.class));
                            nontification.setType(snap.child("type").getValue(String.class));
                            nontification.setUid(snap.child("uid").getValue(String.class));
                            nontification.setClothesProfit(snap.child("clothesProfit").getValue(Long.class));
                            nontificationArrayList.add(nontification);
                        }
                        callback.getNontificationsList(nontificationArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void getComplainReasonList( FirebaseModel model, Callbacks.getComplainReasonsList callback){
        model.initAll();
        Query query=model.getReference().child("AppData").child("complains");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Reason> reasonsArrayList = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Reason reason=new Reason();
                    reason.setReason(snap.child("reason").getValue(String.class));
                    reasonsArrayList.add(reason);
                }
                callback.getComplainReasonsList(reasonsArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getSubscriptionList(String nickName, FirebaseModel model, Callbacks.getFriendsList callback){
        model.initAll();
        Query query=model.getUsersReference().child(nickName).child("subscription");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Subscriber> subscribersList = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Subscriber subscriber=new Subscriber();
                    subscriber.setSub(snap.getValue(String.class));
                    subscribersList.add(subscriber);
                }
                callback.getFriendsList(subscribersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public static void getCommentsList(String nickName,String newsId, FirebaseModel model, Callbacks.getCommentsList callback){
        model.initNewsDatabase();
        Query query=model.getReference().child(nickName)
                .child(newsId).child("comments");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("#####", "WHATAFACKA");
                ArrayList<Comment> commentsList = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Comment comment=snap.getValue(Comment.class);
                    commentsList.add(comment);
                }
                callback.getCommentsList(commentsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void addCommentToTheList(String postOwnerNick, String newsId, FirebaseModel model, Comment comment, String commentId){
        model.initNewsDatabase();
        model.getReference().child(postOwnerNick)
                .child(newsId).child("comments").child(commentId)
                .setValue(comment);
    }
    public static void getMyLookClothes(String nick, FirebaseModel firebaseModel, Callbacks.getLookClothes callback){
        firebaseModel.initAll();
        Query query=firebaseModel.getUsersReference().child(nick).child("lookClothes");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Clothes> clothesArrayList=new ArrayList<>();
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    Clothes clothes=new Clothes();
                    clothes=snap.getValue(Clothes.class);
                    clothesArrayList.add(clothes);
                }
                callback.getLookClothes(clothesArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getLookClothes(String nick,String uid,FirebaseModel firebaseModel,Callbacks.getLookClothes callback){
        firebaseModel.initNewsDatabase();
        Query query=firebaseModel.getReference().child(nick).child(uid)
                .child("clothesCreators");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Clothes> clothesArrayList=new ArrayList<>();
                for(DataSnapshot snap:snapshot.getChildren()){
                    Clothes clothes=new Clothes();
                    clothes=snap.getValue(Clothes.class);
                    clothesArrayList.add(clothes);
                }
                callback.getLookClothes(clothesArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ///////////////////MY CLOTHES//////////////////
    public static void getMyClothes(String nick,FirebaseModel firebaseModel,Callbacks.GetClothes callback){
        firebaseModel.initAll();
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
                    clothes.setBodyType(snap.child("bodyType").getValue(String.class));
                    clothes.setModel(snap.child("model").getValue(String.class));
                    clothes.setUid(snap.child("uid").getValue(String.class));
                    clothes.setX(snap.child("x").getValue(Float.class));
                    clothes.setY(snap.child("y").getValue(Float.class));
                    clothes.setZ(snap.child("z").getValue(Float.class));
                    clothes.setTransformRatio(snap.child("transformRatio").getValue(Float.class));
                    clothesFromBase.add(clothes);
                }
                callback.getClothes(clothesFromBase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //////////////////////////////////////////

    public static void setState (String state, String nick, FirebaseModel firebaseModel){
        firebaseModel.initAll();
        final long[] time = new long[1];

        if (state == "Online"){
            firebaseModel.getUsersReference().child(nick).child("Status").setValue(state);
            firebaseModel.getUsersReference().child(nick).child("Last Seen").setValue(state);
        }
        else {
            firebaseModel.getUsersReference().child(nick).child("Status").setValue(state);
            GetTimeStampNow(nick, firebaseModel, new Callbacks.GetTimesTamp() {
                @Override
                public void GetTimesTamp(long timesTamp) {
                    time[0] = timesTamp;
                }
            });
            firebaseModel.getUsersReference().child(nick).child("Status").setValue(time[0]);
        }
    }


    public static String getCurrentTime() {
        String time;
        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        String timeH, timeM;
        timeH = String.valueOf(hours);
        timeM = String.valueOf(minutes);
        if (minutes < 10)
            timeM = "0" + minutes;
        if (hours < 10)
            timeH = "0" + hours;
        time = timeH + ":" + timeM;
        return time;
    }
    public static UserInformation ValidateSnapToUserInformation(DataSnapshot snapshot){
        UserInformation userInformation = new UserInformation();
        userInformation.setAge(snapshot.child("age").getValue(Long.class));
        userInformation.setAvatar(snapshot.child("avatar").getValue(String.class));
        userInformation.setGender(snapshot.child("gender").getValue(String.class));
        userInformation.setNick(snapshot.child("nick").getValue(String.class));
        userInformation.setPassword(snapshot.child("password").getValue(String.class));
        userInformation.setPhone(snapshot.child("phone").getValue(String.class));
        userInformation.setUid(snapshot.child("uid").getValue(String.class));
        userInformation.setBio(snapshot.child("bio").getValue(String.class));
        userInformation.setVersion(snapshot.child("version").getValue(String.class));
        userInformation.setQueue(snapshot.child("queue").getValue(String.class));
        userInformation.setChatsNontsType(snapshot.child("chatsNontsType").getValue(String.class));
        userInformation.setGroupChatsNontsType(snapshot.child("groupChatsNontsType").getValue(String.class));
        userInformation.setProfileNontsType(snapshot.child("profileNontsType").getValue(String.class));
        userInformation.setAccountType(snapshot.child("accountType").getValue(String.class));
        userInformation.setmoney(snapshot.child("money").getValue(Long.class));
        userInformation.setTodayMining(snapshot.child("todayMining").getValue(Double.class));
        userInformation.setPersonImage(snapshot.child("personImage").getValue(String.class));
        return userInformation;
    }
    public static UserPeopleAdapter validateUserInformationToUserPeopleAdapter(UserInformation user){
        UserPeopleAdapter userPeopleAdapter = new UserPeopleAdapter();
        userPeopleAdapter.setBio(user.getBio());
        userPeopleAdapter.setAvatar(user.getAvatar());
        userPeopleAdapter.setNick(user.getNick());
        return userPeopleAdapter;
    }
    public static void generate_users(int amount, FirebaseModel firebaseModel){
        for(int i = 0; i < amount; i++){
            UserInformation user = getRandomUser();
            firebaseModel.getUsersReference().child(user.getNick()).setValue(user);
            firebaseModel.getReference().child("usersNicks").child(user.getNick())
                    .setValue(RecentMethods.validateUserInformationToUserPeopleAdapter(user));
        }
    }
    public static UserInformation getRandomUser(){
        UserInformation user = new UserInformation("nick", "fidjfif", "gk",
                "6", "password", "Helicopter", 1000, new ArrayList<>(),new ArrayList<>(),1,100,0, new ArrayList<>()
                , new ArrayList<>(), ""," ","open","open","open","open",
                new ArrayList<>(),"regular", new ArrayList<>(),0,new ArrayList<>(),new ArrayList<>()
                ,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()
                ,new ArrayList<Clothes>(),new Person(new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart("", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fma.glb?alt=media&token=f7430695-13cb-4365-8910-c61b59a96acf", "",b ,"",0f,0f,0f),
                new FacePart(), new FacePart()),new ArrayList<>(),new ArrayList<>(),"",""
        ,new ArrayList<>());
        user.setNick("fake");
        Random random = new Random();
        for(int i = 0; i < 10; i++)
            user.setNick(user.getNick() + (char)(random.nextInt(26) + 65));
        return user;
    }
    public static void deleteFakeUsers(FirebaseModel firebaseModel){
        firebaseModel.getUsersReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot = task.getResult();
                    for(DataSnapshot snap : snapshot.getChildren()) {
                        String nickname = snap.child("nick").getValue(String.class);
                        if(nickname.substring(0, 4).equals("fake")) {
                            firebaseModel.getUsersReference().child(nickname).removeValue();
                            firebaseModel.getReference().child("usersNicks").child(nickname).removeValue();
                        }
                    }
                }
                else
                    Log.d("#######", "delete users error");
            }
        });
    }
    public static void random_subscription_generation(FirebaseModel firebaseModel){
        Random random = new Random();
        ArrayList<UserInformation> users = new ArrayList<>();
        firebaseModel.getUsersReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot = task.getResult();
                    for(DataSnapshot snap : snapshot.getChildren())
                        users.add(RecentMethods.ValidateSnapToUserInformation(snap));
                    for(int i = 0; i < 3000; i++){
                        int index_a = random.nextInt(users.size());
                        int index_b = random.nextInt(users.size());
                        while (index_a == index_b)
                            index_b = random.nextInt(users.size());
                        firebaseModel.getUsersReference().child(users.get(index_b).getNick())
                                .child("subscribers").child(users.get(index_a).getNick()).setValue(users.get(index_a).getNick());
                        firebaseModel.getUsersReference().child(users.get(index_a).getNick())
                                .child("subscription").child(users.get(index_b).getNick()).setValue(users.get(index_b).getNick());
                        if(i % 500 == 0 & i > 0)
                            Log.d("#########", i + "   Done ");
                    }
                }
                else
                    Log.d("#######", "get all users error");
            }
        });
    }
    public static void usersBehaviourTest(int amount, FirebaseModel firebaseModel){
        generate_users(amount, firebaseModel);
        random_subscription_generation(firebaseModel);
    }

    public static void getDialogs(String nick,FirebaseModel firebaseModel,Callbacks.loadDialogs loadDialogs){
        firebaseModel.initAll();
        firebaseModel.getUsersReference().child(nick).child("Dialogs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("##", "ss");
                ArrayList<Chat> chatArrayList=new ArrayList<>();
                ArrayList<Chat> talksArrayList=new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    Chat chat=new Chat();
                    chat.setName(snap.child("name").getValue(String.class));
                    chat.setLastMessage(snap.child("lastMessage").getValue(String.class));
                    chat.setLastTime(snap.child("lastTime").getValue(String.class));
                    chat.setUnreadMessages(snap.child("unreadMessages").getValue(Long.class));
                    chat.setType(snap.child("type").getValue(String.class));
                    chat.setTimeMill(snap.child("timeMill").getValue(Long.class));
                    chat.setChatId(snap.child("chatId").getValue(String.class));
                    if(chat.getType().equals("personal")){
                        chatArrayList.add(chat);
                    }else{
                        talksArrayList.add(chat);
                    }
                }
                loadDialogs.LoadData(chatArrayList,talksArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void loadChatMembers(String nick,String uid,FirebaseModel firebaseModel,Callbacks.GetChatMembers callback){
        firebaseModel.initAll();
        firebaseModel.getUsersReference().child(nick).child("Dialogs").child(uid).child("members").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot=task.getResult();
                            ArrayList<UserPeopleAdapter> members=new ArrayList<>();
                            for(DataSnapshot snap:snapshot.getChildren()){
                                UserPeopleAdapter userPeopleAdapter=snap.getValue(UserPeopleAdapter.class);
                                members.add(userPeopleAdapter);
                            }
                            callback.getChatMembers(members);
                        }
                    }
                });
    }

    public static void getMyLookClothesOnce(String nick, FirebaseModel firebaseModel, Callbacks.getLookClothes callback){
        firebaseModel.initAll();
        firebaseModel.getUsersReference().child(nick).child("lookClothes")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot dataSnapshot=task.getResult();
                            ArrayList<Clothes> clothesArrayList=new ArrayList<>();
                            for(DataSnapshot snap:dataSnapshot.getChildren()){
                                Clothes clothes=new Clothes();
                                clothes=snap.getValue(Clothes.class);
                                clothesArrayList.add(clothes);
                            }
                            callback.getLookClothes(clothesArrayList);
                        }
                    }
                });
    }

    ////////////////////FACEPART///////////////
    public static void getCurrentFaceParts(String path,FirebaseModel firebaseModel,Callbacks.loadFacePartsCustom callback){
        firebaseModel.initAppDataDatabase();
        firebaseModel.getReference().child("parts").child(path).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    ArrayList<FacePart> facePartArrayList = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        FacePart facePart = snap.getValue(FacePart.class);
                        Log.d("#####", facePart.getPartTitle());
                        facePartArrayList.add(facePart);
                    }
                    callback.LoadNews(facePartArrayList);
                }
            }
        });
    }

    public static byte[] getBytes( URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = new BufferedInputStream(url.openStream());
            byte[] byteChunk = new byte[4096];
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
        }
        catch (IOException e) {
            Log.d("####", "Failed while reading bytes from %s: %s"+ url.toExternalForm()+ e.getMessage());
            e.printStackTrace ();
        }
        finally {
            if (is != null) { is.close(); }
        }
        return  baos.toByteArray();
    }

    public static void loadClothesCreators(String nick,String newsId,FirebaseModel firebaseModel,Callbacks.loadClothesArrayList callback){
        firebaseModel.initNewsDatabase();
        firebaseModel.getReference().child(nick).child(newsId).child("clothesCreators").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("AAAAA", "CONTINUE AFTER0 ");
                    DataSnapshot snapshot= task.getResult();
                    ArrayList<Clothes> clothesArrayList=new ArrayList<>();
                    for(DataSnapshot snap:snapshot.getChildren()){
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
                        clothes.setExclusive(snap.child("exclusive").getValue(String.class));
                        clothes.setX(snap.child("x").getValue(Float.class));
                        clothes.setY(snap.child("y").getValue(Float.class));
                        clothes.setZ(snap.child("z").getValue(Float.class));
                        clothes.setTransformRatio(snap.child("transformRatio").getValue(Float.class));
                        clothesArrayList.add(clothes);
                    }
                    callback.LoadClothes(clothesArrayList);
                }
            }
        });
    }

    public static void startLoadPerson(String nick,FirebaseModel firebaseModel,Callbacks.loadPerson callback){
        firebaseModel.initAll();
         Color colorBody=new Color();
          Color colorHair=new Color();
           Color colorBrows=new Color();
        firebaseModel.getUsersReference().child(nick).child("person").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot=task.getResult();
                    ArrayList<FacePart> facePartArrayList=new ArrayList<>();
                    for(DataSnapshot snap:snapshot.getChildren()){
                        FacePart facePart=snap.getValue(FacePart.class);
                        switch (facePart.getPartType()) {
                            case "body":
                                colorBody.setColorX(facePart.getColorX());
                                colorBody.setColorY(facePart.getColorY());
                                colorBody.setColorZ(facePart.getColorZ());
                                Log.d("AAAAA", "body  " + colorBody.getColorY()+"   "+facePart.getPartType());
                                break;
                            case "hair":
                                colorHair.setColorX(facePart.getColorX());
                                colorHair.setColorY(facePart.getColorY());
                                colorHair.setColorZ(facePart.getColorZ());
                                Log.d("AAAAA", "hair  " + colorHair.getColorY()+"   "+facePart.getPartType());
                                break;
                            case "brows":
                                colorBrows.setColorX(facePart.getColorX());
                                colorBrows.setColorY(facePart.getColorY());
                                colorBrows.setColorZ(facePart.getColorZ());
                                break;
                        }
                        facePartArrayList.add(facePart);
                    }
                    callback.LoadPerson(setAllPerson(facePartArrayList,"not",colorBody,colorHair ,colorBrows),facePartArrayList);
                }
            }
        });
    }

    public static Person setAllPerson(ArrayList<FacePart> personParts,String type,Color colorBody,Color colorHair,
                                      Color colorBrows){
        Person person=new Person();
        ArrayList<FacePart> parts=new ArrayList<>();
        parts.addAll(personParts);
        for(int i=0;i<parts.size();i++){
            FacePart facePart=parts.get(i);
            if(facePart!=null){
                if(type.equals("base")){
                    facePart.setBuffer(null);
                }
                switch (facePart.getPartType()){
                    case "body":
                        if(colorBody!=null){
                            facePart.setColorX(colorBody.getColorX());
                            facePart.setColorY(colorBody.getColorY());
                            facePart.setColorZ(colorBody.getColorZ());
                        }
                        person.setBody(facePart);
                        break;
                    case "hair":
                        if(colorHair!=null){
                            facePart.setColorX(colorHair.getColorX());
                            facePart.setColorY(colorHair.getColorY());
                            facePart.setColorZ(colorHair.getColorZ());
                        }
                        person.setHair(facePart);
                        break;
                    case "mouth":
                        person.setLips(facePart);
                        break;
                    case "nose":
                        person.setNose(facePart);
                        break;
                    case "brows":
                        if(colorBrows!=null){
                            facePart.setColorX(colorBrows.getColorX());
                            facePart.setColorY(colorBrows.getColorY());
                            facePart.setColorZ(colorBrows.getColorZ());
                        }
                        person.setBrows(facePart);
                        break;
                    case "eyes":
                        person.setEyes(facePart);
                        break;
                }
            }
        }
        return person;
    }

    public static ArrayList<Chat> sort_chats_by_time(ArrayList<Chat> cur){
        cur.sort((Chat a, Chat b) -> (int)(a.getTimeMill() - b.getTimeMill()));
        return cur;
    }

    public static void returnNewsItem(NewsItem newsItem1 ,Callbacks.loadNewsTread loadNewsTread) {
        ArrayList<FacePart> facePartArrayList=new ArrayList<>();
        if(newsItem1.getPerson()!=null){
            facePartArrayList.add(newsItem1.getPerson().getBody());
            facePartArrayList.add(newsItem1.getPerson().getBrows());
            facePartArrayList.add(newsItem1.getPerson().getEars());
            facePartArrayList.add(newsItem1.getPerson().getEyes());
            facePartArrayList.add(newsItem1.getPerson().getHair());
            facePartArrayList.add(newsItem1.getPerson().getHead());
            facePartArrayList.add(newsItem1.getPerson().getLips());
            facePartArrayList.add(newsItem1.getPerson().getNose());
            facePartArrayList.add(newsItem1.getPerson().getMustache());
            facePartArrayList.add(newsItem1.getPerson().getSkinColor());
            Person person=RecentMethods.setAllPerson(facePartArrayList,"base",null,null,null);
            newsItem1.setPerson(person);
        }
        ArrayList<Clothes> clothesWithoutBuffers=new ArrayList<>();
        if( newsItem1.getClothesCreators()!=null){
            for(int i=0;i<newsItem1.getClothesCreators().size();i++){
                Clothes clothes=newsItem1.getClothesCreators().get(i);
                clothes.setBuffer(null);
                clothesWithoutBuffers.add(clothes);
                if(i==newsItem1.getClothesCreators().size()-1){
                    newsItem1.setClothesCreators(clothesWithoutBuffers);
                    try {
                        loadNewsTread.LoadNews(newsItem1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else  {
            try {
                loadNewsTread.LoadNews(newsItem1);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }

    public static void loadViewedNews(String nick,FirebaseModel firebaseModel,Callbacks.LoadViewedNews callback){
        firebaseModel.getUsersReference().child(nick).child("viewedNews").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot=task.getResult();
                    ArrayList<String> viewedNewsList=new ArrayList<>();
                    for(DataSnapshot snap:snapshot.getChildren()){
                        String viewedNews=snap.getValue(String.class);
                        viewedNewsList.add(viewedNews);
                    }
                    callback.getViewedNews(viewedNewsList);

                }
            }
        });
    }

    public static ArrayList<Color> returnColors(){
        ArrayList<Color> colorsArrayList=new ArrayList<>();
        Color color=new Color(1f, 1f, 1f,255, 255, 255);
        colorsArrayList.add(color);
        Color color1=new Color(0f, 0f, 0f,0, 0, 0);
        colorsArrayList.add(color1);
        Color color2=new Color(1f, 0.909f, 0.470f,255, 232, 120);
        colorsArrayList.add(color2);
        Color color3=new Color(1f, 0.894f, 0.368f,255, 228, 94);
        colorsArrayList.add(color3);
        Color color4=new Color(0.988f, 0.537f, 0.188f,252, 137, 48);
        colorsArrayList.add(color4);
        Color colorMilky=new Color(0.901f,0.901f, 0.901f,230, 230, 230);
        colorsArrayList.add(colorMilky);
        Color color5=new Color(0.721f, 0.721f, 0.721f,184, 184, 184);
        colorsArrayList.add(color5);
        Color color6=new Color(0.541f, 0.541f, 0.541f,138, 138, 138);
        colorsArrayList.add(color6);
        Color colorBrown=new Color(0.188f, 0.094f, 0.019f,48, 24, 5);
        colorsArrayList.add(colorBrown);
        Color colorBrownBlack=new Color(0.141f, 0.105f, 0.078f,36, 27, 20);
        colorsArrayList.add(colorBrownBlack);
        Color colorRed=new Color(0.760f, 0.090f,0.054f,194, 23, 14);
        colorsArrayList.add(colorRed);
        Color colorLightRed=new Color(0.941f, 0.317f, 0.380f,240, 81, 97);
        colorsArrayList.add(colorLightRed);
        Color colorYellow=new Color(0.760f, 0.713f, 0.054f,194, 182, 14);
        colorsArrayList.add(colorYellow);
        Color colorGreen=new Color(0.337f, 0.760f, 0.054f,86, 194, 14);
        colorsArrayList.add(colorGreen);
        Color colorGreen2=new Color(0.054f, 0.760f, 0.396f,14, 194, 101);
        colorsArrayList.add(colorGreen2);
        Color colorBlue=new Color(0.054f, 0.760f,0.666f,14, 194, 170);
        colorsArrayList.add(colorBlue);
        Color colorBlueMore=new Color(0.054f, 0.396f, 0.760f,14, 101, 194);
        colorsArrayList.add(colorBlueMore);
        Color colorPurple=new Color(0.290f, 0.054f, 0.760f,74, 14, 194);
        colorsArrayList.add(colorPurple);
        Color colorPurpleLight=new Color(0.803f, 0.317f, 0.941f,205, 81, 240);
        colorsArrayList.add(colorPurpleLight);
        Color colorPinkLight=new Color(0.941f, 0.317f, 0.858f,240, 81, 219);
        colorsArrayList.add(colorPinkLight);
        return colorsArrayList;
    }

    public static ArrayList<Color> returnBodyColors(){
        ArrayList<Color> colorsArrayList=new ArrayList<>();
        Color color=new Color(1f, 0.847f, 0.619f,255, 216, 158);
        colorsArrayList.add(color);
        Color colorBrownLighter=new Color( 0.98f, 0.776f, 0.470f,250, 198, 120);
        colorsArrayList.add(colorBrownLighter);
        Color color1=new Color(1f, 0.8f, 0.501f,255, 204, 128);
        colorsArrayList.add(color1);
        Color colorBrown=new Color(0.89f, 0.686f, 0.384f,227, 175, 98);
        colorsArrayList.add(colorBrown);
        Color color2=new Color(1f, 0.725f, 0.309f,255, 185, 79);
        colorsArrayList.add(color2);
        Color color3=new Color(0.858f, 0.572f, 0.137f,219, 146, 35);
        colorsArrayList.add(color3);
        Color color4=new Color(0.988f, 0.537f, 0.188f,252, 137, 48);
        colorsArrayList.add(color4);
        Color colorMilky=new Color(0.329f,0.219f, 0.05f,84, 56, 13);
        colorsArrayList.add(colorMilky);
        Color color5=new Color(0.239f, 0.152f, 0.023f,61, 39, 6);
        colorsArrayList.add(color5);
        Color color6=new Color(0.211f, 0.129f, 0.007f,54, 33, 2);
        colorsArrayList.add(color6);
        return colorsArrayList;
    }


}