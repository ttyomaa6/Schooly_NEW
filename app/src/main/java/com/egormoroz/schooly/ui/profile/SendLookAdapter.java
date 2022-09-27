package com.egormoroz.schooly.ui.profile;

import android.graphics.Color;
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
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.chat.Chat;
import com.egormoroz.schooly.ui.coins.TransferMoneyAdapter;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;

import java.util.ArrayList;

public class SendLookAdapter extends RecyclerView.Adapter<SendLookAdapter.ViewHolder> {
    ArrayList<Chat> listAdapter;
    private FirebaseModel firebaseModel = new FirebaseModel();
    SendLookAdapter.ItemClickListener itemClickListener;

    public  SendLookAdapter(ArrayList<Chat> listAdapter, ItemClickListener itemClickListener) {
        this.listAdapter = listAdapter;
        this.itemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    public SendLookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rv_item_bottom_sheet_look, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SendLookAdapter.ViewHolder holder, int position) {
        Chat chat=listAdapter.get(position);
        holder.otherUserNick.setText(chat.getName());
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.send.getText().equals(holder.send.getContext().getResources().getText(R.string.send))){
                    holder.send.setText(holder.send.getContext().getResources().getText(R.string.openchat));
                    holder.send.setTextColor(Color.parseColor("#F3A2E5"));
                    holder.send.setBackgroundResource(R.drawable.corners10appcolor2dpstroke);
                    itemClickListener.onItemClick(chat, "send");
                }else {
                   itemClickListener.onItemClick(chat,"chat");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView otherUserNick,send;
        ImageView userImage;
        ViewHolder(View itemView) {
            super(itemView);
            otherUserNick = itemView.findViewById(R.id.otherUserNick);
            send=itemView.findViewById(R.id.send);
            userImage=itemView.findViewById(R.id.userImage);
        }
    }

    Chat getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(SendLookAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(Chat chat,String type);
    }
}
