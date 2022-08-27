package com.egormoroz.schooly;

import android.util.Log;

import androidx.annotation.NonNull;

import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.LoadNewsTread;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LoadNewsItemInScene {

    UserInformation userInformation;
    FirebaseModel newsModel;

    public LoadNewsItemInScene(UserInformation userInformation,  NewsItem newsItem,Callbacks.loadNewsTread loadNewsTread){
        newsModel=new FirebaseModel();
        newsModel.initNewsDatabase();
        this.userInformation=userInformation;
        loadLooksPerson(newsItem,loadNewsTread);
    }

    static byte[] buffer;
    static URI uri;
    static Buffer bufferToFilament,buffer1;

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
        if(newsItem.getClothesCreators()==null){
            FirebaseModel firebaseModel=new FirebaseModel();
            firebaseModel.initNewsDatabase();
            RecentMethods.loadClothesCreators(newsItem.getNick(), newsItem.getNewsId(), firebaseModel, new Callbacks.loadClothesArrayList() {
                @Override
                public void LoadClothes(ArrayList<Clothes> clothesArrayList) {
                    int[] stopLoad = {clothesArrayList.size()};
                    Log.d("AAA", "STOP LOAD VALUE32  "+stopLoad[0]+"  "+newsItem.getNewsId());
                    for(int i=0;i<clothesArrayList.size();i++){
                        Clothes clothes=clothesArrayList.get(i);
                        TaskRunner taskRunner=new TaskRunner();
                        int finalI = i;
                        taskRunner.executeAsync(new LongRunningTask(clothes), (data) -> {
                            clothesArrayListWithBuffers.add(data);
                            stopLoad[0]--;
                            Log.d("AAA", " MIDDLE LOAD32 "+ stopLoad[0]+"     "+ finalI);
                            if(stopLoad[0] ==0){
                                Log.d("AAA", " STOP LOAD32 "+ stopLoad[0]+"  "+finalI);
                                newsItem.setClothesCreators(clothesArrayListWithBuffers);
                                loadPersonBuffers(facePartArrayList,newsItem,loadNewsTread);
                            }
                        });
                    }
                }
            });
        }else{
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
    }

    public void loadPersonBuffers(  ArrayList<FacePart> facePartArrayList,NewsItem newsItem,Callbacks.loadNewsTread loadNewsTread){
        int loadValue=0;
        com.egormoroz.schooly.Color colorBody=new com.egormoroz.schooly.Color();
        com.egormoroz.schooly.Color colorHair=new com.egormoroz.schooly.Color();
        com.egormoroz.schooly.Color colorBrows=new com.egormoroz.schooly.Color();
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
                    com.egormoroz.schooly.Color[] color = {new com.egormoroz.schooly.Color()};
                    TaskRunnerCustom taskRunnerCustom1=new TaskRunnerCustom();
                    int finalI = i;
                    taskRunnerCustom1.executeAsync(new LongRunningTaskParts(facePart1), (data1) -> {
                        facePartArrayWithBuffers.add(data1);
                        stopLoad[0]--;
                        if(facePart1.getColorX()!=-1f && facePart1.getColorY()!=-1f && facePart1.getColorZ()!=-1f){
                            color[0] =new com.egormoroz.schooly.Color(facePart1.getColorX(),
                                    facePart1.getColorY(), facePart1.getColorZ()
                                    , 0, 0, 0);
                            switch (facePart1.getPartType()) {
                                case "body":
                                    colorBody.setColorX(facePart1.getColorX());
                                    colorBody.setColorY(facePart1.getColorY());
                                    colorBody.setColorZ(facePart1.getColorZ());
                                    break;
                                case "hair":
                                    colorHair.setColorX(facePart1.getColorX());
                                    colorHair.setColorY(facePart1.getColorY());
                                    colorHair.setColorZ(facePart1.getColorZ());
                                    break;
                                case "brows":
                                    colorBrows.setColorX(facePart1.getColorX());
                                    colorBrows.setColorY(facePart1.getColorY());
                                    colorBrows.setColorZ(facePart1.getColorZ());
                                    break;
                            }
                        }
                        Log.d("AAAA", " MIDDLE LOAD BODY  1 "+ stopLoad[0]+"   "+ finalI);
                        if(stopLoad[0]==0){
                            newsItem.setPerson(RecentMethods.setAllPerson(facePartArrayWithBuffers,"not",colorBody,colorHair,colorBrows));
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
