package com.egormoroz.schooly.ui.main.Mining;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.coins.CoinsFragmentSecond;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeakMinersAdapter extends RecyclerView.Adapter<WeakMinersAdapter.ViewHolder> {


    ArrayList<Miner> listAdapterMiner;
    private ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();
    ItemClickListener itemClickListener;
    String nick;
    UserInformation userInformation;
    int checkValue;
    String userNick;

    public WeakMinersAdapter(ArrayList<Miner> listAdapter, ItemClickListener itemClickListener,UserInformation userInformation) {
        this.listAdapterMiner = listAdapter;
        this.itemClickListener= itemClickListener;
        this.userInformation=userInformation;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.allminersrecyclerview_item, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        firebaseModel.initAll();
        nick=userInformation.getNick();
        Miner miner=listAdapterMiner.get(position);
        holder.minerPrice.setText(String.valueOf(miner.getMinerPrice()));
        holder.inHour.setText("+"+String.valueOf(miner.getInHour()+"S в час"));
        String minerPriceText= (String) holder.minerPrice.getText();
        holder.minerImage.setVisibility(View.VISIBLE);
        Picasso.get().load(miner.getMinerImage()).into(holder.minerImage);
        for(int i=0;i<userInformation.getMyMiners().size();i++){
            Miner miner1=userInformation.getMyMiners().get(i);
            if(String.valueOf(miner1.getMinerPrice()).equals(String.valueOf(miner.getMinerPrice()))){
                holder.buy.setText("Куплено");
                holder.buy.setBackgroundResource(R.drawable.corners14grey);
            }
        }
        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query=firebaseModel.getUsersReference().child(nick).child("miners")
                        .child(String.valueOf(holder.getAdapterPosition())+"weak");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Toast.makeText(v.getContext(), "Майнер куплен", Toast.LENGTH_SHORT).show();
                        }else{
                            int pos=holder.getAdapterPosition();
                            itemClickListener.onItemClick(pos,miner,"weak", userInformation.getmoney());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAdapterMiner.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView minerPrice,buy,inHour;
        ImageView minerImage;
        ViewHolder(View itemView) {
            super(itemView);
            minerPrice=itemView.findViewById(R.id.minerprice);
            buy=itemView.findViewById(R.id.buy);
            minerImage=itemView.findViewById(R.id.minerImage);
            inHour=itemView.findViewById(R.id.inHour);
        }
    }

    Miner getItem(int id) {
        return listAdapterMiner.get(id);
    }


    public interface ItemClickListener {
        void onItemClick(int position,Miner miner,String type,long money);
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

    public void showDialog(View v,String textInDialog){

        final Dialog dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.Text);
        text.setText(textInDialog);
        RelativeLayout relative=dialog.findViewById(R.id.Relative);


        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}