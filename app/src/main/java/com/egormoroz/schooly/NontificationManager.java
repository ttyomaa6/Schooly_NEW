package com.egormoroz.schooly;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NontificationManager extends Worker {

    FirebaseModel firebaseModel=new FirebaseModel();
    int NOTIFY_ID;
    private static final String CHANNEL_ID = "channel";
    private static final String CHANNEL_ID1 = "channel1";
    Nontification nonts;
    String nickOther,nick;

    public NontificationManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        firebaseModel.initAll();
        nick= getInputData().getString("NICK_WORK");
        if(nick!=null){
            Log.d("###", nick);
            getChangesInSubscribers();
        }
        Log.d("######", "great");
        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.chanel_name);
            String description = getApplicationContext().getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("AAA", "dd  ");
        }
    }

    public void getChangesInSubscribers(){
        Log.d("###", "shiiiiiiiitttttttttttt");
        Query query=firebaseModel.getUsersReference().child(nick).child("nontifications");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                nonts=dataSnapshot.getValue(Nontification.class);
                Log.d("###", "s");
                if(nonts.getTypeDispatch().equals("не отправлено")) {
                    nickOther = nonts.getNick();
                    if(nonts.getTypeView().equals("обычный")) {
                        nontification("Еще один!",nickOther+" подписался на тебя",NOTIFY_ID);
                        firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
                                .child("typeDispatch").setValue("отправлено");
                        createNotificationChannel();
                    }else if(nonts.getTypeView().equals("запросодежда")){
                        nontification("Пришел ответ на заявку","Рассмотрена заявка на добавление одежды "+nonts.getClothesName() ,NOTIFY_ID);
                        firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
                                .child("typeDispatch").setValue("отправлено");
                        createNotificationChannel();
                    }else if(nonts.getTypeView().equals("запрос")){
                        nontification("Запрос на подписку",nickOther+" хочет подписаться на тебя!",NOTIFY_ID);
                        firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
                                .child("typeDispatch").setValue("отправлено");
                        createNotificationChannel();
                    }else if(nonts.getTypeView().equals("одежда")){
                        nontification("Еще больше прибыли!",nickOther+" купил у тебя "+nonts.getClothesName()+"!",NOTIFY_ID);
                        firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
                                .child("typeDispatch").setValue("отправлено");
                        createNotificationChannel();
                    }else if(nonts.getTypeView().equals("перевод")){
                        nontification("Перевод коинов",nickOther+" перевел тебе коины!",NOTIFY_ID);
                        firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
                                .child("typeDispatch").setValue("отправлено");
                        createNotificationChannel();
                    }else if(nonts.getTypeView().equals("подарок")){
                        nontification("Ух ты! Новый подарок!",nickOther+" подарил тебе подарок!",NOTIFY_ID);
                        firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
                                .child("typeDispatch").setValue("отправлено");
                        createNotificationChannel();
                    }
                    else if(nonts.getTypeView().equals("майнинг")){
                        nontification("Майнинг","+"+nonts.getClothesProfit()+"S за день",NOTIFY_ID);
                        firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
                                .child("typeDispatch").setValue("отправлено");
                        createNotificationChannel();
                    }
                    else if(nonts.getTypeView().equals("одеждаприбыль")){
                        nontification("Прибыль с продаж одежды","+"+nonts.getClothesProfit()+"S за день",NOTIFY_ID);
                        firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
                                .child("typeDispatch").setValue("отправлено");
                        createNotificationChannel();
                    }
                }
//                        RecentMethods.getNontificationsList(nick, firebaseModel, new Callbacks.getNontificationsList() {
//                            @Override
//                            public void getNontificationsList(ArrayList<Nontification> nontifications) {
//                                if(nontifications.size()!=0) {
//                                    for(int i=0;i<nontifications.size();i++){
//                                        nonts = nontifications.get(i);
//                                        Log.d("AAA", "shiiiiiiiitttttttttttt");
//                                        if(nonts.getTypeDispatch().equals("не отправлено")) {
//                                            nickOther = nonts.getNick();
//                                            if(nonts.getTypeView().equals("обычный")) {
//                                                nontification("Еще один!",nickOther+" подписался на тебя",NOTIFY_ID);
//                                                firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
//                                                        .child("typeDispatch").setValue("отправлено");
//                                                createNotificationChannel();
//                                            }else if(nonts.getTypeView().equals("запросодежда")){
//                                                nontification("Пришел ответ на заявку","Рассмотрена заявка на добавление одежды"+nonts.getClothesName() ,NOTIFY_ID);
//                                                firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
//                                                        .child("typeDispatch").setValue("отправлено");
//                                                createNotificationChannel();
//                                            }else if(nonts.getTypeView().equals("запрос")){
//                                                nontification("Запрос на подписку",nickOther+" хочет подписаться на тебя!",NOTIFY_ID);
//                                                firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
//                                                        .child("typeDispatch").setValue("отправлено");
//                                                createNotificationChannel();
//                                            }else if(nonts.getTypeView().equals("одежда")){
//                                                nontification("Еще больше прибыли!",nickOther+" купил у тебя "+nonts.getClothesName()+"!",NOTIFY_ID);
//                                                firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
//                                                        .child("typeDispatch").setValue("отправлено");
//                                                createNotificationChannel();
//                                            }else if(nonts.getTypeView().equals("перевод")){
//                                                nontification("Перевод коинов",nickOther+" перевел тебе коины!",NOTIFY_ID);
//                                                firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
//                                                        .child("typeDispatch").setValue("отправлено");
//                                                createNotificationChannel();
//                                            }else if(nonts.getTypeView().equals("подарок")){
//                                                nontification("Ух ты! Новый подарок!",nickOther+" подарил тебе подарок",NOTIFY_ID);
//                                                firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
//                                                        .child("typeDispatch").setValue("отправлено");
//                                                createNotificationChannel();
//                                            }
//                                            else if(nonts.getTypeView().equals("майнинг")){
//                                                nontification("Майнинг","+"+nonts.getClothesProfit()+"S за день",NOTIFY_ID);
//                                                firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
//                                                        .child("typeDispatch").setValue("отправлено");
//                                                createNotificationChannel();
//                                            }
//                                            else if(nonts.getTypeView().equals("одеждаприбыль")){
//                                                nontification("Прибыль с продаж одежды","+"+nonts.getClothesProfit()+"S за день",NOTIFY_ID);
//                                                firebaseModel.getUsersReference().child(nick).child("nontifications").child(nonts.getUid())
//                                                        .child("typeDispatch").setValue("отправлено");
//                                                createNotificationChannel();
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        });

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

    public void nontification(String nickOther,String text, int notificationId){
        Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nontification_image)
                .setContentTitle(nickOther)
                .setContentText(text)
                .setContentIntent(notifyPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(notificationId, builder.build());
        NOTIFY_ID+=1;
        Log.d("AAAA", "zz  "+NOTIFY_ID);
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }

}
