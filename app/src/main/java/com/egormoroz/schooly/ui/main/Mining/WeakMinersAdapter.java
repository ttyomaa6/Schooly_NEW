package com.egormoroz.schooly.ui.main.Mining;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
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

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
        holder.inHour.setText("+"+String.valueOf(miner.getInHour()+"S"+holder.inHour.getContext().getResources().getString(R.string.inhour)));
        String minerPriceText= (String) holder.minerPrice.getText();
        holder.minerImage.setVisibility(View.VISIBLE);
        switch ((int) miner.getInHour()){
            case 10:
                holder.minerImage.setBackgroundResource(R.drawable.weak0);
                break;
            case 11:
                holder.minerImage.setBackgroundResource(R.drawable.weak1);
                break;
            case 12:
                holder.minerImage.setBackgroundResource(R.drawable.weak2);
                break;
            case 13:
                holder.minerImage.setBackgroundResource(R.drawable.weak3);
                break;
            case 14:
                holder.minerImage.setBackgroundResource(R.drawable.weak4);
                break;
        }
        for(int i=0;i<userInformation.getMyMiners().size();i++){
            Miner miner1=userInformation.getMyMiners().get(i);
            if(String.valueOf(miner1.getMinerPrice()).equals(String.valueOf(miner.getMinerPrice()))){
                holder.buy.setText(holder.buy.getContext().getResources().getText(R.string.purchased));
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
                            Toast.makeText(v.getContext(), v.getContext().getResources().getText(R.string.minerpurchased), Toast.LENGTH_SHORT).show();
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
        RelativeLayout relative=dialog.findViewById(R.id.Delete_relative_layout);


        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}