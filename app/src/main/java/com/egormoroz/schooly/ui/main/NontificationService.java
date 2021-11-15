package com.egormoroz.schooly.ui.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.Mining.Miner;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class NontificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    FirebaseModel firebaseModel=new FirebaseModel();
    private static final int NOTIFY_ID = 102;
    private static final String CHANNEL_ID = "Tyomaa channel";
    ArrayList<String> listOfNontifications = new ArrayList<String>();
    ArrayList<String> list = new ArrayList<String>();
    Subscriber otherUserNickNonts;
    String name;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        firebaseModel.initAll();
        getChangesInSubscribers();
        createNotificationChannel();
        return super.onStartCommand(intent, flags, startId);
    }




    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.chanel_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        createNotificationChannel();
        getChangesInSubscribers();
    }

    public void getChangesInSubscribers(){
//        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
//            @Override
//            public void PassUserNick(String nick) {
//                RecentMethods.checkSubscribers(nick, firebaseModel, new Callbacks.getSubscribersList() {
//                    @Override
//                    public void getSubscribersList(ArrayList<Subscriber> subscribers) {
//                        if(subscribers.size()!=0){
//                        int lastIndex=subscribers.size()-1;
//                        otherUserNickNonts=subscribers.get(lastIndex);
//                        name=otherUserNickNonts.getSub();
//                    }
//                    }
//                });
//            }
//        });
        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
            @Override
            public void PassUserNick(String nick) {
                Query query=firebaseModel.getUsersReference().child(nick).child("subscribers");
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        RecentMethods.getSubscribersList(nick, firebaseModel, new Callbacks.getSubscribersList() {
                            @Override
                            public void getSubscribersList(ArrayList<Subscriber> subscribers) {
                                int lastIndex=subscribers.size()-1;
                                otherUserNickNonts=subscribers.get(lastIndex);
                                Log.d("###", "dsaddf"+subscribers.size());
                                Log.d("###", "dsad"+lastIndex);
                                name=otherUserNickNonts.getSub();
                                Log.d("###", "d"+name);
                                nontification();
                            }
                        });

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
        });
    }
    public void nontification(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_schoolycoin)
                .setContentTitle(name)
                .setContentText("хочет добавить вас в друзья")
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFY_ID, builder.build());
        Log.d("######", "good");
    }

}