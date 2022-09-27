//package com.egormoroz.schooly.ui.main;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.Vibrator;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.egormoroz.schooly.Callbacks;
//import com.egormoroz.schooly.FirebaseModel;
//import com.egormoroz.schooly.MainActivity;
//import com.egormoroz.schooly.Nontification;
//import com.egormoroz.schooly.R;
//import com.egormoroz.schooly.RecentMethods;
//import com.egormoroz.schooly.Subscriber;
//import com.egormoroz.schooly.ui.main.Mining.Miner;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.Query;
//
//import java.util.ArrayList;
//
//public class NontificationService extends Service {
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//    FirebaseModel firebaseModel=new FirebaseModel();
//    private static final int NOTIFY_ID = 102;
//    private static final String CHANNEL_ID = "channel";
//    ArrayList<String> listOfNontifications = new ArrayList<String>();
//    ArrayList<String> list = new ArrayList<String>();
//    Nontification otherUserNickNonts;
//    String nickOther;
//
////    @Override
////    public void onCreate() {
////        startForeground(6, getNotification());
////    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        firebaseModel.initAll();
////        getChangesInSubscribers();
////        createNotificationChannel();
//        return Service.START_STICKY;
//    }
//
//
//
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.chanel_name);
//            String description = getString(R.string.app_name);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//            Log.d("####", "dd  "+nickOther);
//        }
//    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    public void getChangesInSubscribers(){
////        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
////            @Override
////            public void PassUserNick(String nick) {
////                RecentMethods.checkSubscribers(nick, firebaseModel, new Callbacks.getSubscribersList() {
////                    @Override
////                    public void getSubscribersList(ArrayList<Subscriber> subscribers) {
////                        if(subscribers.size()!=0){
////                        int lastIndex=subscribers.size()-1;
////                        otherUserNickNonts=subscribers.get(lastIndex);
////                        name=otherUserNickNonts.getSub();
////                    }
////                    }
////                });
////            }
////        });
//        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
//            @Override
//            public void PassUserNick(String nick) {
//                Query query=firebaseModel.getReference();
//                query.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        RecentMethods.getNontificationsList(nick, firebaseModel, new Callbacks.getNontificationsList() {
//                            @Override
//                            public void getNontificationsList(ArrayList<Nontification> nontifications) {
//                                Log.d("#####", "f "+nontifications);
//                                if(nontifications.size()!=0) {
//                                    for(int i=0;i<nontifications.size();i++){
//                                        otherUserNickNonts = nontifications.get(i);
//                                        nickOther = otherUserNickNonts.getNick();
//                                        Log.d("####", "shiiiiiiiitttttttttttt");
//                                        if(otherUserNickNonts.getTypeDispatch().equals("не отправлено")) {
//                                            nontification(nickOther);
//                                            firebaseModel.getUsersReference().child(nick).child("nontifications").child(otherUserNickNonts.getNick())
//                                                    .child("typeDispatch").setValue("отправлено");
//                                        }
//                                    }
//                                }
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//    }
//    public void nontification(String nickOther){
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this,
//                0, notificationIntent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_nontification_image)
//                .setContentTitle(nickOther)
//                .setContentText("хочет добавить вас в друзья")
//                .setContentIntent(contentIntent)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager =
//                NotificationManagerCompat.from(this);
//        notificationManager.notify(NOTIFY_ID, builder.build());
//        Log.d("######", "good");
//        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
//// Vibrate for 500 milliseconds
//        v.vibrate(400);
//    }
//
//}