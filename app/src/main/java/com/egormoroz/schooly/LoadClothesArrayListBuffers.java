package com.egormoroz.schooly;

import android.util.Log;

import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.news.LoadNewsTread;
import com.egormoroz.schooly.ui.news.NewsItem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LoadClothesArrayListBuffers {

    static byte[] buffer;
    static URI uri;
    static Buffer bufferToFilament;

    public static void loadClothesBuffer(ArrayList<Clothes> clothesArrayList,Callbacks.loadClothesArrayList loadClothesArrayList){
        ArrayList<Clothes> clothesArrayListWithBuffers=new ArrayList<>();
        int[] stopLoad = {clothesArrayList.size()};
        Log.d("AAA", "STOP LOAD VALUE  "+stopLoad[0]+"  ");
        for(int i=0;i<clothesArrayList.size();i++){
            Clothes clothes=clothesArrayList.get(i);
            TaskRunner taskRunner=new TaskRunner();
            int finalI = i;
            taskRunner.executeAsync(new LongRunningTask(clothes), (data) -> {
                clothesArrayListWithBuffers.add(data);
                stopLoad[0]--;
                Log.d("AAA", " MIDDLE LOAD "+ stopLoad[0]+"     "+ finalI);
                if(stopLoad[0] ==0){
                    Log.d("AAA", " STOP LOAD "+ stopLoad[0]+"  "+finalI);
                    loadClothesArrayList.LoadClothes(clothesArrayListWithBuffers);
                }
            });
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

}
