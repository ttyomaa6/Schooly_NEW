package com.egormoroz.schooly.ui.news;

import android.media.FaceDetector;
import android.util.Log;

import androidx.annotation.NonNull;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FilamentModel;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Person;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.TaskRunner;
import com.egormoroz.schooly.TaskRunnerCustom;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadNewsTread {
    UserInformation userInformation;
    FirebaseModel newsModel;

    public LoadNewsTread(UserInformation userInformation, Callbacks.loadNewsTread loadNewsTread){
        newsModel=new FirebaseModel();
        newsModel.initNewsDatabase();
        this.userInformation=userInformation;
        getSubsForNews(loadNewsTread);
    }

    static byte[] buffer;
    static URI uri;
    static Buffer bufferToFilament,buffer1;
    ArrayList<String> stringSubscriptionArrayList=new ArrayList<>();
    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<String> allLoadNewsArrayList=new ArrayList<>();

    public void getSubsForNews(Callbacks.loadNewsTread loadNewsTread){
        firebaseModel.initAll();
        if(userInformation.getSubscription()==null){
            Log.d("AAAAA", "ffff33333     "+userInformation.getSubscription().size());
            firebaseModel.getUsersReference().child(userInformation.getNick())
                            .child("subscription").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                RecentMethods.getSubscriptionList(userInformation.getNick(), firebaseModel, new Callbacks.getFriendsList() {
                                    @Override
                                    public void getFriendsList(ArrayList<Subscriber> friends) {
                                        Log.d("AAAAA", "fffff11     "+friends.size());
                                        if(friends.size()==0){
                                            loadRandomRecommendationNews(loadNewsTread);
                                        }else {
                                            for(int i=0;i<friends.size();i++){
                                                stringSubscriptionArrayList.add(friends.get(i).getSub());
                                                if(i==userInformation.getSubscription().size()-1){
                                                    getNews(stringSubscriptionArrayList, loadNewsTread);
                                                }
                                            }
                                        }
                                    }
                                });
                            }else{
                                loadRandomRecommendationNews(loadNewsTread);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else{
            Log.d("AAAAA", "ffff2222     "+userInformation.getSubscription().size());
            if(userInformation.getSubscription()==null || userInformation.getSubscription().size()==0){
                loadRandomRecommendationNews(loadNewsTread);
            }else{
                for(int i=0;i<userInformation.getSubscription().size();i++){
                    stringSubscriptionArrayList.add(userInformation.getSubscription().get(i).getSub());
                    getNews(stringSubscriptionArrayList, loadNewsTread);
                }
            }
        }
    }

    public void getNews(ArrayList<String> stringSubscriptionArrayList,Callbacks.loadNewsTread loadNewsTread){
        if(userInformation.getViewedNews()!=null){
            loadNews(stringSubscriptionArrayList, loadNewsTread,"first");
        }else {
            RecentMethods.loadViewedNews(userInformation.getNick(), firebaseModel, new Callbacks.LoadViewedNews() {
                @Override
                public void getViewedNews(ArrayList<String> viewedNews) {
                    loadNews(stringSubscriptionArrayList, loadNewsTread,"first");
                }
            });
        }
    }

    public void loadNews(ArrayList<String> stringSubscriptionArrayList,Callbacks.loadNewsTread loadNewsTread,String type){
        ArrayList<NewsItem> newsItemArrayList=new ArrayList<>();
        for(int i=0;i<stringSubscriptionArrayList.size();i++){
            Log.d("AAAAA", "fffff    "+type);
            String searchNick=stringSubscriptionArrayList.get(i);
            int finalI = i;
            newsModel.getReference().child(searchNick).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d("AAAAA", "fffff55");
                        DataSnapshot snapshot=task.getResult();
                        for(DataSnapshot snap:snapshot.getChildren()){
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
                            if(!userInformation.getViewedNews().contains(newsItem.getNewsId())&&!allLoadNewsArrayList.contains(newsItem.getNewsId())&&
                            !newsItem.getNick().equals(userInformation.getNick())){
                                Log.d("MMM", "mynews");
                                allLoadNewsArrayList.add(newsItem.getNewsId());
                                newsItemArrayList.add(newsItem);
                            }

                        }
                        if(finalI ==stringSubscriptionArrayList.size()-1){
                            if(!type.equals("third")){
                                if(newsItemArrayList.size()==0){
                                    loadRandomRecommendationNews(loadNewsTread);
                                }else {
                                    loadLooksClothes(newsItemArrayList, loadNewsTread, type);
                                }
                            }
                            else {
                                if(newsItemArrayList.size()==0){
                                    try {
                                        loadNewsTread.LoadNews(null);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    loadLooksClothes(newsItemArrayList, loadNewsTread, type);
                                }
                                Log.d("AAAAAA", "QQQQQ");
                            }
                        }
                    }
                }
            });
        }
    }

    public void loadLooksClothes(ArrayList<NewsItem> newsItemArrayList,Callbacks.loadNewsTread loadNewsTread,String type){
        Log.d("AAAAA", "CONTINUE AFTER1230 ");
        for(int i=0;i<newsItemArrayList.size();i++){
            NewsItem newsItem=newsItemArrayList.get(i);
            int finalI = i;
            newsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId()).child("clothesCreators").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                        newsItem.setClothesCreators(clothesArrayList);
                        if(finalI ==newsItemArrayList.size()-1){
                            sortNewsList(newsItemArrayList, loadNewsTread,type);
                        }
                    }
                }
            });
        }
    }

    public void sortNewsList(ArrayList<NewsItem> newsItemArrayList,Callbacks.loadNewsTread loadNewsTread,String type)  {
        ArrayList<NewsItem> sortNewsArrayList=sort_news_by_time(newsItemArrayList);
        Log.d("AAAAA", "CONTINUE AFTER "+sortNewsArrayList.size());
        Collections.reverse(sortNewsArrayList);
        for(int i=0;i<sortNewsArrayList.size();i++){
            NewsItem newsItem=sortNewsArrayList.get(i);
            try {
                loadNewsTread.LoadNews(newsItem);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(i==sortNewsArrayList.size()-1){
                if(type.equals("first")) {
                    getSecondNewsTread(loadNewsTread);
                }else {
                    loadRandomRecommendationNews(loadNewsTread);
                }
            }
        }
    }

    public void getSecondNewsTread(Callbacks.loadNewsTread loadNewsTread){
        ArrayList<String> stringSubsList=new ArrayList<>();
        for(int i=0;i<userInformation.getSubscription().size();i++){
            String nick=userInformation.getSubscription().get(i).getSub();
            int finalI = i;
            RecentMethods.getSubscriptionList(nick, firebaseModel, new Callbacks.getFriendsList() {
                @Override
                public void getFriendsList(ArrayList<Subscriber> friends) {
                    for(int i=0;i<friends.size();i++){
                        stringSubsList.add(friends.get(i).getSub());
                    }
                    if(finalI ==userInformation.getSubscription().size()-1){
                        loadNews(stringSubsList,loadNewsTread,"second");
                    }
                }
            });
        }
    }

    public void loadRandomRecommendationNews(Callbacks.loadNewsTread loadNewsTread){
        Log.d("AAAAA", "RANDOM");
        ArrayList<String> usersNicks=new ArrayList<>();
        firebaseModel.getReference().child("usersNicks").limitToFirst(300).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot=task.getResult();
                    for(DataSnapshot snap:snapshot.getChildren()){
                        usersNicks.add(snap.child("nick").getValue(String.class));
                    }
                    loadNews(usersNicks, loadNewsTread, "third");
                }
            }
        });
    }

    public ArrayList<NewsItem> sort_news_by_time(ArrayList<NewsItem> cur){
        cur.sort((NewsItem a, NewsItem b) -> (int)(a.getTimestamp() - b.getTimestamp()));
        return cur;
    }

    public void loadLooksPerson(NewsItem newsItem,Callbacks.loadNewsTread loadNewsTread){
        newsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId()).child("person").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot=task.getResult();
                            ArrayList<FacePart> facePartArrayList=new ArrayList<>();
                            for(DataSnapshot snap:snapshot.getChildren()){
                                FacePart facePart=snap.getValue(FacePart.class);
                                facePartArrayList.add(facePart);
                            }
                            loadClothesBuffer(loadNewsTread, newsItem,facePartArrayList);
                        }
                    }
                });
    }

    public void loadClothesBuffer(Callbacks.loadNewsTread loadNewsTread,NewsItem newsItem, ArrayList<FacePart> facePartArrayList){
        ArrayList<Clothes> clothesArrayListWithBuffers=new ArrayList<>();
        int[] stopLoad = {newsItem.getClothesCreators().size()};
        Log.d("AAA", "STOP LOAD VALUE  "+stopLoad[0]+"  "+newsItem.getNewsId());
        for(int i=0;i<newsItem.getClothesCreators().size();i++){
            Clothes clothes=newsItem.getClothesCreators().get(i);
            TaskRunner taskRunner=new TaskRunner();
            int finalI = i;
            taskRunner.executeAsync(new LongRunningTask(clothes), (data) -> {
                clothesArrayListWithBuffers.add(data);
                stopLoad[0]--;
                Log.d("AAA", " MIDDLE LOAD "+ stopLoad[0]+"     "+ finalI);
                if(stopLoad[0] ==0){
                    Log.d("AAA", " STOP LOAD "+ stopLoad[0]+"  "+finalI);
                    newsItem.setClothesCreators(clothesArrayListWithBuffers);
                    loadPersonBuffers(facePartArrayList,newsItem,loadNewsTread);
                }
            });
        }
    }

    public void loadPersonBuffers(  ArrayList<FacePart> facePartArrayList,NewsItem newsItem,Callbacks.loadNewsTread loadNewsTread){
        int loadValue=0;
        Log.d("AAAA", " COME TO METHOD");
        ArrayList<FacePart> filteredFaceParts=new ArrayList<>();
        for(int s=0;s<facePartArrayList.size();s++){
            FacePart facePart=facePartArrayList.get(s);
            if(facePart!=null){
                filteredFaceParts.add(facePart);
                loadValue++;
            }
            if(s==facePartArrayList.size()-1){
                int[] stopLoad={loadValue};
                ArrayList<FacePart> facePartArrayWithBuffers=new ArrayList<>();
                for(int i=0;i<filteredFaceParts.size();i++){
                    Log.d("AAA", "q  "+filteredFaceParts.size());
                    FacePart facePart1=filteredFaceParts.get(i);
                    TaskRunnerCustom taskRunnerCustom1=new TaskRunnerCustom();
                    int finalI = i;
                    taskRunnerCustom1.executeAsync(new LongRunningTaskParts(facePart1), (data1) -> {
                        facePartArrayWithBuffers.add(data1);
                        stopLoad[0]--;
                        Log.d("AAAA", " MIDDLE LOAD BODY  1 "+ stopLoad[0]+"   "+ finalI);
                        if(stopLoad[0]==0){
                            newsItem.setPerson(RecentMethods.setAllPerson(facePartArrayWithBuffers,"not",null,null,null));
                            loadNewsTread.LoadNews(newsItem);
                            Log.d("AAAA", " STOP LOAD BODY  1 "+ stopLoad[0]+"   "+finalI);
                        }
                    });
                }
            }
        }
    }

    public static Clothes addModelInScene(Clothes clothes)  {
        try {
            uri = new URI(clothes.getModel());
            buffer = RecentMethods.getBytes(uri.toURL());
            bufferToFilament= ByteBuffer.wrap(buffer);
            clothes.setBuffer(bufferToFilament);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return clothes;
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

    public static FacePart loadBodyPart(FacePart facePart){
        try {
            uri = new URI(facePart.getModel());
            buffer = RecentMethods.getBytes(uri.toURL());
            buffer1= ByteBuffer.wrap(buffer);
            facePart.setBuffer(buffer1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return facePart;
    }

    static class LongRunningTaskParts implements Callable<FacePart> {
        private FacePart facePart;

        public LongRunningTaskParts(FacePart facePart) {
            this.facePart = facePart;
        }

        @Override
        public FacePart call() {
            return loadBodyPart(facePart);
        }
    }
}
