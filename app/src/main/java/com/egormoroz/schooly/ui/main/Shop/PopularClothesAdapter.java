package com.egormoroz.schooly.ui.main.Shop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.ChatsFragment;
import com.egormoroz.schooly.ui.main.RegisrtationstartFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class PopularClothesAdapter extends RecyclerView.Adapter<PopularClothesAdapter.ViewHolder> {

    private ItemClickListener clickListener;
    ArrayList<Clothes> clothesArrayList;
    private FirebaseModel firebaseModel = new FirebaseModel();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference=storage.getReference();
    static Clothes clothes;
    ItemClickListener onClothesClick;
    static int pos;

    public PopularClothesAdapter(ArrayList<Clothes> clothesArrayList,ItemClickListener onClothesClick) {
        this.clothesArrayList= clothesArrayList;
        this.onClothesClick= onClothesClick;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitempopular, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        firebaseModel.initAll();
        pos=position;
        clothes=clothesArrayList.get(position);
        holder.clothesTitle.setText(clothes.getClothesTitle());
        Log.d("#####", "ggxadwd  "+holder.clothesImage);
        File file=new File(clothes.getClothesImage());
        storageReference.child("clothes").getFile(file);
        holder.clothesImage.setVisibility(View.VISIBLE);
        Picasso.get().load(clothes.getClothesImage()).into(holder.clothesImage);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onClothesClick.onItemClick(clothes,position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return clothesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView clothesTitle;
        ImageView clothesImage;
        ViewHolder(View itemView) {
            super(itemView);
            clothesImage=itemView.findViewById(R.id.clothesImage);
            clothesTitle=itemView.findViewById(R.id.clothesTitle);
        }


    }

    public static void singeClothesInfo(ItemClickListener itemClickListener){
        itemClickListener.onItemClick(clothes, pos);
    }


    public interface ItemClickListener {
        void onItemClick( Clothes clothes,int position);
    }

    static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int margin = 16;
            int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, view.getResources().getDisplayMetrics());
            if(parent.getChildAdapterPosition(view) == 0){
                outRect.left = space;
                outRect.bottom = 0;
            }
            if(parent.getChildAdapterPosition(view) == 4){
                outRect.right = space;
                outRect.bottom = 0;
            }
        }

    }
}
