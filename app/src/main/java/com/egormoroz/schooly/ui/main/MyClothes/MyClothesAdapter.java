package com.egormoroz.schooly.ui.main.MyClothes;

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
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MyClothesAdapter extends RecyclerView.Adapter<MyClothesAdapter.ViewHolder> {

    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<Clothes> clothesArrayList;
    static Clothes clothes,trueClothes;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference=storage.getReference();
    MyClothesAdapter.ItemClickListener itemClickListener;
    String clothesPriceString,profitTodayString,purchaseTodayString;
    UserInformation userInformation;
    double perCent;

    public MyClothesAdapter(ArrayList<Clothes> clothesArrayList, MyClothesAdapter.ItemClickListener itemClickListener, UserInformation userInformation) {
        this.clothesArrayList= clothesArrayList;
        this.itemClickListener= itemClickListener;
        this.userInformation=userInformation;
    }

    public static void singeClothesInfo(MyClothesAdapter.ItemClickListener itemClickListener){
        itemClickListener.onItemClick(trueClothes);
    }

    public interface ItemClickListener {
        void onItemClick( Clothes clothes);
    }

    @NonNull
    @Override
    public MyClothesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_myclothesfrag, viewGroup, false);
        MyClothesAdapter.ViewHolder viewHolder=new MyClothesAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyClothesAdapter.ViewHolder holder, int position) {
        firebaseModel.initAll();
        clothes=clothesArrayList.get(position);
        holder.clothesTitle.setText(clothes.getClothesTitle());
        File file=new File(clothes.getClothesImage());
        storageReference.child("clothes").getFile(file);
        holder.clothesImage.setVisibility(View.VISIBLE);
        clothesPriceString=String.valueOf(clothes.getPurchaseNumber());
        checkCounts(holder.purchaseNumber, clothes.getPurchaseNumber(), clothesPriceString);
        profitTodayString=String.valueOf(clothes.getClothesPrice()*clothes.getPurchaseToday());
        if (clothes.getCurrencyType().equals("dollar")){
            holder.coinsImage.setVisibility(View.GONE);
            holder.profit.setText("+"+String.valueOf(clothes.getClothesPrice()*clothes.getPurchaseToday())+"$");
            if(clothes.getClothesPrice()*clothes.getPurchaseToday()<1000){
                holder.profit.setText("+"+profitTodayString+"$");
            }else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>1000
                    && clothes.getClothesPrice()*clothes.getPurchaseToday()<10000){
                holder.profit.setText("+"+profitTodayString.substring(0, 1)+"."+profitTodayString.substring(1, 2)+"K"+"$");
            }
            else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>10000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<100000){
                holder.profit.setText("+"+profitTodayString.substring(0, 2)+"."+profitTodayString.substring(2,3)+"K"+"$");
            }
            else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>10000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<100000){
                holder.profit.setText("+"+profitTodayString.substring(0, 2)+"."+profitTodayString.substring(2,3)+"K"+"$");
            }else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>100000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<1000000){
                holder.profit.setText("+"+profitTodayString.substring(0, 3)+"K"+"$");
            }
            else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>1000000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<10000000){
                holder.profit.setText("+"+profitTodayString.substring(0, 1)+"KK"+"$");
            }
            else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>10000000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<100000000){
                holder.profit.setText("+"+profitTodayString.substring(0, 2)+"KK"+"$");
            }
        }else {
            holder.profit.setText("+"+String.valueOf(clothes.getClothesPrice()*clothes.getPurchaseToday()));
            if(clothes.getClothesPrice()*clothes.getPurchaseToday()<1000){
                holder.profit.setText("+"+profitTodayString);
            }else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>1000
                    && clothes.getClothesPrice()*clothes.getPurchaseToday()<10000){
                holder.profit.setText("+"+profitTodayString.substring(0, 1)+"."+profitTodayString.substring(1, 2)+"K");
            }
            else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>10000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<100000){
                holder.profit.setText("+"+profitTodayString.substring(0, 2)+"."+profitTodayString.substring(2,3)+"K");
            }
            else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>10000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<100000){
                holder.profit.setText("+"+profitTodayString.substring(0, 2)+"."+profitTodayString.substring(2,3)+"K");
            }else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>100000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<1000000){
                holder.profit.setText("+"+profitTodayString.substring(0, 3)+"K");
            }
            else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>1000000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<10000000){
                holder.profit.setText("+"+profitTodayString.substring(0, 1)+"KK");
            }
            else if(clothes.getClothesPrice()*clothes.getPurchaseToday()>10000000 &&
                    clothes.getClothesPrice()*clothes.getPurchaseToday()<100000000){
                holder.profit.setText("+"+profitTodayString.substring(0, 2)+"KK");
            }
        }
        purchaseTodayString=String.valueOf(clothes.getPurchaseToday());
        checkCounts(holder.purchasesToday, clothes.getPurchaseToday(), purchaseTodayString);
        if (clothes.getPurchaseNumber()==0){
            perCent=0;
        }else {
            perCent=clothes.getPurchaseToday()*100/clothes.getPurchaseNumber();
        }
        holder.perSentPurchase.setText("("+String.valueOf(perCent)+"%)");
//        if (clothes.getCurrencyType().equals("dollar")){
//            holder.dollarImage.setVisibility(View.VISIBLE);
//            holder.coinsImage.setVisibility(View.GONE);
//        }
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
        TextView purchasesToday,clothesTitle,purchaseNumber,perSentPurchase,profit;
        ImageView clothesImage,coinsImage;
        ViewHolder(View itemView) {
            super(itemView);
            profit=itemView.findViewById(R.id.profit);
            perSentPurchase=itemView.findViewById(R.id.perSentPurchase);
            purchasesToday=itemView.findViewById(R.id.purchasesToday);
            clothesImage=itemView.findViewById(R.id.clothesImage);
            clothesTitle=itemView.findViewById(R.id.clothesTitle);
            coinsImage=itemView.findViewById(R.id.coinsImage);
            purchaseNumber=itemView.findViewById(R.id.purchaseNumber);
        }


    }
}
