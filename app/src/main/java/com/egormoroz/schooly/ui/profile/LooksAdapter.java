package com.egormoroz.schooly.ui.profile;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FilamentModel;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class LooksAdapter extends RecyclerView.Adapter<LooksAdapter.ViewHolder> {

    ArrayList<NewsItem> listAdapter;
    private LooksAdapter.ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();
    static  NewsItem newsItemToViewing;
    Fragment fragment;
    View view;
    NewsItem  newsItem;
    String viewLookCount;
    FilamentModel filamentModel=new FilamentModel();

    public LooksAdapter(ArrayList<NewsItem> listAdapter,Fragment fragment,RecyclerView recyclerView) {
        this.listAdapter = listAdapter;
        this.fragment=fragment;
    }


    @NotNull
    @Override
    public LooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_look, viewGroup, false);
        LooksAdapter.ViewHolder viewHolder=new LooksAdapter.ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LooksAdapter.ViewHolder holder, int position) {
        newsItem=listAdapter.get(position);
        newsItemToViewing=newsItem;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) clickListener.onItemClick(listAdapter.get(holder.getAdapterPosition()));
                newsItemToViewing=listAdapter.get(holder.getAdapterPosition());
            }
        });
        Picasso.get().load(newsItem.getImageUrl()).into(holder.surfaceView);
        viewLookCount=String.valueOf(newsItem.getViewCount());
//        try {
//            filamentModel.executeTask(newsItem.getImageUrl(), holder.surfaceView,false,null);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
        checkCounts(holder.viewPurchase, newsItem.getViewCount(), viewLookCount);

    }

    public void checkCounts(TextView textView,Long count,String stringCount){
        if(count<1000){
            textView.setText(String.valueOf(count));
        }else if(count>1000 && count<10000){
            textView.setText(stringCount.substring(0, 1)+"."+stringCount.substring(1, 2)+"K");
        }
        else if(count>10000 && count<100000){
            textView.setText(stringCount.substring(0, 2)+"."+stringCount.substring(2,3)+"K");
        }else if(count>100000 && count<1000000){
            textView.setText(stringCount.substring(0, 3)+"K");
        }
        else if(count>1000000 && count<10000000){
            textView.setText(stringCount.substring(0, 1)+"KK");
        }
        else if(count>10000000 && count<100000000){
            textView.setText(stringCount.substring(0, 2)+"KK");
        }
    }

    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView surfaceView;
        TextView viewPurchase;
        ViewHolder(View itemView) {
            super(itemView);
           surfaceView=itemView.findViewById(R.id.lookScene);
            viewPurchase=itemView.findViewById(R.id.viewPurchase);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(newsItemToViewing);

        }
    }

    NewsItem getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(LooksAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public static void lookInfo(LooksAdapter.ItemClickListener itemClickListener){
        itemClickListener.onItemClick(newsItemToViewing);
    }

    public interface ItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void loadModels(Uri url, SceneView sceneView, Context context, float scale) {
//        ModelRenderable.builder()
//                .setSource(
//                        context, new RenderableSource.Builder().setSource(
//                                context,
//                                url,
//                                RenderableSource.SourceType.GLB
//                        ).setScale(scale)
//                                .setRecenterMode(RenderableSource.RecenterMode.CENTER)
//                                .build()
//                )
//                .setRegistryId(url)
//                .build()
//                .thenAccept(new Consumer<ModelRenderable>() {
//                    @Override
//                    public void accept(ModelRenderable modelRenderable) {
//                        addNode(modelRenderable, sceneView);
//                    }
//                });
//    }
//
//    public void addNode(ModelRenderable modelRenderable, SceneView sceneView) {
//        Node modelNode1 = new Node();
//        modelNode1.setRenderable(modelRenderable);
//        modelNode1.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
////        modelNode1.setLocalRotation(Quaternion.multiply(
////                Quaternion.axisAngle(new Vector3(1f, 0f, 0f), 45),
////                Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 75)));
//        modelNode1.setLocalPosition(new Vector3(0f, 0f, -0.9f));
//        sceneView.getScene().addChild(modelNode1);
//        try {
//            sceneView.resume();
//        } catch (CameraNotAvailableException e) {
//            e.printStackTrace();
//        }
//    }
}
