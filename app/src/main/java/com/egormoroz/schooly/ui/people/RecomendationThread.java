package com.egormoroz.schooly.ui.people;
import android.util.Log;
import android.util.LogPrinter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
public class RecomendationThread {
    String userNick;
    UserInformation userInformation;
    FirebaseModel model = new FirebaseModel();
    Set<String> was = new HashSet<String>();
    int checkValue;
    public RecomendationThread(String input, UserInformation info, Callbacks.getRecommendationsThread recommendationsInterface){
        model.initAll();
        this.userNick = input;
        this.userInformation = info;
        was.add(userNick);
        get_Subs(recommendationsInterface);
    }
    HashMap<UserPeopleAdapter, Integer> user_accurancy = new HashMap<>();
    public void get_Subs(Callbacks.getRecommendationsThread recommendationsInterface){
        Log.d("######", "START  "+userInformation.getSubscription());
        ArrayList<Subscriber> subscribers = userInformation.getSubscription();
        for(Subscriber user : subscribers){
            Log.d("######", "MIDDLE0" +
                    "  "+userInformation.getSubscription());
            was.add(user.getSub());
            model.getUsersReference().child(user.getSub()).child("subscription").get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                Log.d("######", "MIDDLE1" +
                                        "  "+userInformation.getSubscription());
                                DataSnapshot snapshot = task.getResult();
                                for(DataSnapshot snap : snapshot.getChildren()) {
                                    Subscriber current_user = ValidateSnap(snap);
                                    if(was.contains(current_user.getSub()))
                                        continue;
                                    was.add(current_user.getSub());
                                    Log.d("######", "MIDDLE" +
                                            "  "+userInformation.getSubscription());
                                    checkValue++;
                                    validate_Subscriber(ValidateSnap(snap), recommendationsInterface);
                                }
                            }
                        }
                    });
        }
    }
    public ArrayList<UserPeopleAdapter> get_Recommendations(){
        ArrayList<UserPeopleAdapter> result_list = new ArrayList<>();
        Set mapKeys = user_accurancy.keySet();
        Iterator keysIterator = mapKeys.iterator();
        while(keysIterator.hasNext())
            result_list.add((UserPeopleAdapter)keysIterator.next());
        return result_list;
    }
    public void validate_Subscriber(Subscriber sub, Callbacks.getRecommendationsThread recommendationsInterface){
        Log.d("#####", "FINISH   ");
        UserPeopleAdapter userPeopleAdapter=new UserPeopleAdapter();
        userPeopleAdapter.setNick(sub.getSub());
        model.getUsersReference().child(sub.getSub()).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            DataSnapshot snapshot = task.getResult();
                            userPeopleAdapter.setAvatar(snapshot.child("avatar").getValue(String.class));
                            userPeopleAdapter.setBio(snapshot.child("bio").getValue(String.class));
                            int acuur = 0;
                            if(user_accurancy.containsKey(userPeopleAdapter))
                                acuur = user_accurancy.get(userPeopleAdapter);
                            user_accurancy.remove(userPeopleAdapter);
                            user_accurancy.put(userPeopleAdapter, acuur + 1);
                            ArrayList<UserPeopleAdapter> users = get_Recommendations();
                            recommendationsInterface.getRecommendationsInterface(users);
                        }
                    }
                });
    }
    public Subscriber ValidateSnap(DataSnapshot snap){
        Subscriber subscriber = new Subscriber();
        subscriber.setSub(snap.getValue(String.class));
        Log.d("####","ser ");
        return subscriber;
    }
}
