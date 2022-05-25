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
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class NewClothesAdapter extends RecyclerView.Adapter<NewClothesAdapter.ViewHolder> {

  public interface ItemClickListener {
    void onItemClick( Clothes clothes);
  }

  ArrayList<Clothes> clothesArrayList;
  private FirebaseModel firebaseModel = new FirebaseModel();
  FirebaseStorage storage = FirebaseStorage.getInstance();
  StorageReference storageReference=storage.getReference();
  static Clothes clothes,trueClothes;
  UserInformation userInformation;
  String clothesPriceString,purchaseNumberString;
  ItemClickListener itemClickListener;
  String nick;


  public NewClothesAdapter(ArrayList<Clothes> clothesArrayList, ItemClickListener itemClickListener, UserInformation userInformation) {
    this.clothesArrayList= clothesArrayList;
    this.itemClickListener= itemClickListener;
    this.userInformation=userInformation;
  }

  public static void singeClothesInfo(ItemClickListener itemClickListener){
    itemClickListener.onItemClick(trueClothes);
  }


  @NotNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
            inflate(R.layout.new_clothes_rvitem, viewGroup, false);
    ViewHolder viewHolder=new ViewHolder(v);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    firebaseModel.initAll();
    nick=userInformation.getNick();
    clothes=clothesArrayList.get(position);
    holder.clothesTitle.setText(clothes.getClothesTitle());
    File file=new File(clothes.getClothesImage());
    storageReference.child("clothes").getFile(file);
    holder.clothesImage.setVisibility(View.VISIBLE);
    holder.creator.setText(clothes.getCreator());
    clothesPriceString=String.valueOf(clothes.getClothesPrice());
    checkCounts(holder.clothesPrise, clothes.getClothesPrice(), clothesPriceString);
    purchaseNumberString=String.valueOf(clothes.getPurchaseNumber());
    checkCounts(holder.purchaseNumber, clothes.getPurchaseNumber(), purchaseNumberString);
    if (clothes.getCurrencyType().equals("dollar")){
      holder.dollarImage.setVisibility(View.VISIBLE);
      holder.coinsImage.setVisibility(View.GONE);
    }
    if(userInformation.getClothes()!=null){
      for(int i=0;i<userInformation.getClothes().size();i++){
        Clothes clothes1=userInformation.getClothes().get(i);
        if(clothes1.getUid().equals(clothes.getUid())){
          holder.ifBuy.setVisibility(View.VISIBLE);
        }
      }
    }
    Picasso.get().load(clothes.getClothesImage()).into(holder.clothesImage);
    holder.itemView.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v)
      {
        itemClickListener.onItemClick(clothesArrayList.get(holder.getAdapterPosition()));
        trueClothes=clothesArrayList.get(holder.getAdapterPosition());
      }
    });
  }

  public void checkCounts(TextView textView,Long count,String stringCount){
    if(count<1000){
      textView.setText(String.valueOf(count));
    }else if(count>1000 && count<10000){
      textView.setText(stringCount.substring(0, 1)+"."+stringCount.substring(1, 2)+"K");
    }
    else if(count>10000 && count<100000){
      textView.setText(stringCount.substring(0, 2)+"."+stringCount.substring(2,3)+"K");
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
    return clothesArrayList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView clothesPrise,clothesTitle,creator,purchaseNumber;
    ImageView clothesImage,ifBuy,dollarImage,coinsImage;
    ViewHolder(View itemView) {
      super(itemView);
      clothesPrise=itemView.findViewById(R.id.clothesPrice);
      clothesImage=itemView.findViewById(R.id.clothesImage);
      clothesTitle=itemView.findViewById(R.id.clothesTitle);
      creator=itemView.findViewById(R.id.creator);
      ifBuy=itemView.findViewById(R.id.ifBuy);
      coinsImage=itemView.findViewById(R.id.coinsImage);
      dollarImage=itemView.findViewById(R.id.dollarImage);
      purchaseNumber=itemView.findViewById(R.id.purchaseNumber);
    }


  }
}