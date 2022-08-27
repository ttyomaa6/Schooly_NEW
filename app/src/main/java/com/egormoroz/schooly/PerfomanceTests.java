package com.egormoroz.schooly;
import android.util.Log;
import androidx.annotation.NonNull;

import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import java.util.Random;
public class PerfomanceTests {
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
                ,new ArrayList<Clothes>(),new Person(new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(),new FacePart(),
                new FacePart(), new FacePart()), new ArrayList<>(),new ArrayList<>(),"","",new ArrayList<>()
        );
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
        firebaseModel.getReference().child("usersNicks").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot = task.getResult();
                    for(DataSnapshot snap : snapshot.getChildren()) {
                        String nickname = snap.child("nick").getValue(String.class);
                        if(nickname.substring(0, 4).equals("fake")) {
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
    public static void addUsersWithBehaviour(int amount, FirebaseModel firebaseModel){
        generate_users(amount, firebaseModel);
        random_subscription_generation(firebaseModel);
    }
    public static void FriendsRecommendationsTest(){
        FirebaseModel firebaseModel = new FirebaseModel();
        firebaseModel.initAll();
        for(int i = 1; i <= 5; i++){
            deleteFakeUsers(firebaseModel);
            addUsersWithBehaviour(i * 100, firebaseModel);
        }
    }
}
