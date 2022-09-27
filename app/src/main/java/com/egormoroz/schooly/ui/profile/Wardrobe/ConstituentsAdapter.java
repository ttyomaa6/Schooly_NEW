package com.egormoroz.schooly.ui.profile.Wardrobe;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ConstituentsAdapter extends RecyclerView.Adapter<ConstituentsAdapter.ViewHolder> {

    ArrayList<Clothes> clothesArrayList;
    private FirebaseModel firebaseModel = new FirebaseModel();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference=storage.getReference();
    static Clothes trueClothes;
    ItemClickListener itemClickListener;

    public ConstituentsAdapter(ArrayList<Clothes> clothesArrayList, ItemClickListener itemClickListener) {
        this.clothesArrayList= clothesArrayList;
        this.itemClickListener=itemClickListener;
    }
    @NonNull
    @Override
    public ConstituentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_lookconsistuents, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConstituentsAdapter.ViewHolder holder, int position) {
        Clothes clothes=clothesArrayList.get(position);
        Picasso.get().load(clothes.getClothesImage()).into(holder.clothesImage);
        holder.clothesTitle.setText(clothes.getClothesTitle());
        if (clothes.getCurrencyType().equals("dollar")){
            holder.clothesPrice.setText(String.valueOf(clothes.getClothesPrice())+"$");
            holder.schoolyCoin.setVisibility(View.GONE);
        }else{
            holder.clothesPrice.setText(String.valueOf(clothes.getClothesPrice()));
        }
        holder.nick.setText(clothes.getCreator());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(clothesArrayList.get(holder.getAdapterPosition()));
                trueClothes=clothesArrayList.get(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return clothesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView clothesTitle,clothesPrice,nick;
        ImageView clothesImage,schoolyCoin;
        ViewHolder(View itemView) {
            super(itemView);
            clothesImage=itemView.findViewById(R.id.clothesImagecv);
            clothesTitle=itemView.findViewById(R.id.clothesTitlecv);
            clothesPrice=itemView.findViewById(R.id.clothesPricecv);
            schoolyCoin=itemView.findViewById(R.id.coinImagePrice);
            nick=itemView.findViewById(R.id.nick);
        }
    }

    public static void singeClothesInfo(ConstituentsAdapter.ItemClickListener itemClickListener){
        itemClickListener.onItemClick(trueClothes);
    }

    public interface ItemClickListener {
        void onItemClick( Clothes clothes);
    }
}
