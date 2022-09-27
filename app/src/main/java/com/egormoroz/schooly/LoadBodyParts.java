package com.egormoroz.schooly;

import android.util.Log;

import com.egormoroz.schooly.ui.news.NewsItem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LoadBodyParts {

    static byte[] buffer;
    static URI uri;
    static Buffer buffer1;

    public static void loadPersonBuffers(ArrayList<FacePart> facePartArrayList, Callbacks.loadFaceParts loadFaceParts){
        int loadValue=0;
        ArrayList<FacePart> facePartArrayWithBuffers=new ArrayList<>();
        ArrayList<FacePart> filteredFaceParts=new ArrayList<>();
        for(int s=0;s<facePartArrayList.size();s++){
            FacePart facePart=facePartArrayList.get(s);
            if(facePart!=null){
                filteredFaceParts.add(facePart);
                Log.d("AAAA", " COME TO METHOD   " + facePart.getColorZ()+"   "+facePart.getColorY()+"   "+facePart.getPartType());
                loadValue++;
            }
            if(s==facePartArrayList.size()-1){
                int[] stopLoad={loadValue};
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
                            Log.d("AAAA", " STOP LOAD BODY  1 "+ stopLoad[0]+"   "+finalI);
                            Log.d("AAAA", " STOP BUFFER "+ facePartArrayWithBuffers.get(0).getColorY());
                            loadFaceParts.LoadFaceParts(facePartArrayWithBuffers);
                        }
                    });
                }
            }
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
