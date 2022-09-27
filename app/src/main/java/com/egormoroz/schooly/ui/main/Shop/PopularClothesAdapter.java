package com.egormoroz.schooly.ui.main.Shop;

import android.graphics.Rect;
import android.util.TypedValue;
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
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PopularClothesAdapter extends RecyclerView.Adapter<PopularClothesAdapter.ViewHolder> {

    private ItemClickListener clickListener;
    ArrayList<Clothes> clothesArrayList;
    private FirebaseModel firebaseModel = new FirebaseModel();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference=storage.getReference();
    static Clothes clothes,trueClothes;
    static  int position;
    ItemClickListener onClothesClick;
    String clothesPriceString,purchaseNumberString;
    UserInformation userInformation;

    public PopularClothesAdapter(ArrayList<Clothes> clothesArrayList, ItemClickListener onClothesClick, UserInformation userInformation) {
        this.clothesArrayList= clothesArrayList;
        this.onClothesClick= onClothesClick;
        this.userInformation=userInformation;
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
        clothes=clothesArrayList.get(position);
        holder.clothesTitle.setText(clothes.getClothesTitle());
        holder.clothesImage.setVisibility(View.VISIBLE);
        holder.creator.setText(clothes.getCreator());
        clothesPriceString=String.valueOf(clothes.getClothesPrice());
        checkCounts(holder.clothesPrice, clothes.getClothesPrice(), clothesPriceString,"price");
        purchaseNumberString=String.valueOf(clothes.getPurchaseNumber());
        checkCounts(holder.purchaseNumber, clothes.getPurchaseNumber(), purchaseNumberString," ");

        if (clothes.getCurrencyType().equals("dollar")) {
            holder.coinsImage.setVisibility(View.GONE);
        }
        Picasso.get().load(clothes.getClothesImage()).into(holder.clothesImage);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onClothesClick.onItemClick(clothesArrayList.get(holder.getAdapterPosition()),holder.getAdapterPosition());
                trueClothes=clothesArrayList.get(holder.getAdapterPosition());
            }
        });
    }

    public void checkCounts(TextView textView,Long count,String stringCount,String type){
        if(clothes.getCurrencyType().equals("dollar")&&type.equals("price")){
            if (count < 1000) {
                textView.setText("$"+String.valueOf(count));
            } else if (count > 1000 && count < 10000) {
                textView.setText("$"+stringCount.substring(0, 1) + "." + stringCount.substring(1, 2) + "K");
            } else if (count > 10000 && count < 100000) {
                textView.setText("$"+stringCount.substring(0, 2) + "." + stringCount.substring(2, 3) + "K");
            } else if (count > 10000 && count < 100000) {
                textView.setText("$"+stringCount.substring(0, 2) + "." + stringCount.substring(2, 3) + "K");
            } else if (count > 100000 && count < 1000000) {
                textView.setText("$"+stringCount.substring(0, 3) + "K");
            } else if (count > 1000000 && count < 10000000) {
                textView.setText("$"+stringCount.substring(0, 1) + "." + stringCount.substring(1, 2) + "KK");
            } else if (count > 10000000 && count < 100000000) {
                textView.setText("$"+stringCount.substring(0, 2) + "." + stringCount.substring(2, 3) + "KK");
            }
        }else {
            if (count < 1000) {
                textView.setText(String.valueOf(count));
            } else if (count > 1000 && count < 10000) {
                textView.setText(stringCount.substring(0, 1) + "." + stringCount.substring(1, 2) + "K");
            } else if (count > 10000 && count < 100000) {
                textView.setText(stringCount.substring(0, 2) + "." + stringCount.substring(2, 3) + "K");
            } else if (count > 10000 && count < 100000) {
                textView.setText(stringCount.substring(0, 2) + "." + stringCount.substring(2, 3) + "K");
            } else if (count > 100000 && count < 1000000) {
                textView.setText(stringCount.substring(0, 3) + "K");
            } else if (count > 1000000 && count < 10000000) {
                textView.setText(stringCount.substring(0, 1) + "." + stringCount.substring(1, 2) + "KK");
            } else if (count > 10000000 && count < 100000000) {
                textView.setText(stringCount.substring(0, 2) + "." + stringCount.substring(2, 3) + "KK");
            }
        }
    }

    @Override
    public int getItemCount() {
        return clothesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView clothesTitle,clothesPrice,creator,purchaseNumber;
        ImageView clothesImage,ifBuy,dollarImage,coinsImage;
        ViewHolder(View itemView) {
            super(itemView);
            clothesImage=itemView.findViewById(R.id.clothesImage);
            clothesTitle=itemView.findViewById(R.id.clothesTitle);
            clothesPrice=itemView.findViewById(R.id.clothesPrice);
            creator=itemView.findViewById(R.id.creator);
            ifBuy=itemView.findViewById(R.id.ifBuy);
            dollarImage=itemView.findViewById(R.id.dollarImage);
            coinsImage=itemView.findViewById(R.id.coinsImage);
            purchaseNumber=itemView.findViewById(R.id.purchaseNumber);
        }


    }

    public static void singeClothesInfo(ItemClickListener itemClickListener){
        itemClickListener.onItemClick(trueClothes,position);
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