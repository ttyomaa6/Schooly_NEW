package com.egormoroz.schooly.ui.main.Mining;

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

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ActiveMinersAdapter extends RecyclerView.Adapter<ActiveMinersAdapter.ViewHolder> {

    ArrayList<Miner> listAdapterActivaMiner;
    private ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();
    UserInformation userInformation;
    String nick;

    public ActiveMinersAdapter(ArrayList<Miner> listAdapter,UserInformation userInformation) {
        this.listAdapterActivaMiner = listAdapter;
        this.userInformation=userInformation;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.horizontalrecyclerview_item, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        nick=userInformation.getNick();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        firebaseModel.initAll();
        Miner miner=listAdapterActivaMiner.get(holder.getAdapterPosition());
        holder.inHour.setText("+"+String.valueOf(miner.getInHour())+"S");
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
            case 20:
                holder.minerImage.setBackgroundResource(R.drawable.medium0);
                break;
            case 22:
                holder.minerImage.setBackgroundResource(R.drawable.medium1);
                break;
            case 24:
                holder.minerImage.setBackgroundResource(R.drawable.medium2);
                break;
            case 26:
                holder.minerImage.setBackgroundResource(R.drawable.madium3);
                break;
            case 28:
                holder.minerImage.setBackgroundResource(R.drawable.medium4);
                break;
            case 30:
                holder.minerImage.setBackgroundResource(R.drawable.strong0);
                break;
            case 33:
                holder.minerImage.setBackgroundResource(R.drawable.strong1);
                break;
            case 36:
                holder.minerImage.setBackgroundResource(R.drawable.strong2);
                break;
            case 39:
                holder.minerImage.setBackgroundResource(R.drawable.strong3);
                break;
            case 42:
                holder.minerImage.setBackgroundResource(R.drawable.strong4);
                break;
        }
        holder.putAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseModel.getUsersReference().child(nick)
                        .child("activeMiners").child(String.valueOf(miner.getMinerPrice())).removeValue();
                holder.putAway.setText(holder.putAway.getContext().getResources().getText(R.string.notactive));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAdapterActivaMiner.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView inHour,putAway;
        ImageView minerImage;
        ViewHolder(View itemView) {
            super(itemView);
            inHour=itemView.findViewById(R.id.minerinhour);
            putAway=itemView.findViewById(R.id.putaway);
            minerImage=itemView.findViewById(R.id.activeminerimage);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Miner getItem(int id) {
        return listAdapterActivaMiner.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
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
        }
    }
}