package com.egormoroz.schooly.ui.chat;

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
import com.egormoroz.schooly.ui.coins.TransferMoneyAdapter;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {

    ArrayList<Chat> chatArrayList;
    ItemClickListener itemClickListener;
    FirebaseModel firebaseModel=new FirebaseModel();


    public DialogAdapter(ArrayList<Chat> chatArrayList) {
        this.chatArrayList = chatArrayList;
    }


    @NotNull
    @Override
    public DialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.dialog_item_layout, viewGroup, false);
        DialogAdapter.ViewHolder viewHolder = new DialogAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DialogAdapter.ViewHolder holder, int position) {
        firebaseModel.initAll();
        Chat chat=chatArrayList.get(position);
        holder.userName.setText(chat.getName());
        holder.lastTime.setText(chat.getLastTime());
        if (chat.getLastMessage().length()<=16){
            holder.lastMessage.setText(chat.getLastMessage());
        }else {
            String lastMessage=chat.getLastMessage().substring(0, 16);
            holder.lastMessage.setText(lastMessage+"...");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) itemClickListener.onItemClick(chat.getName());
            }
        });

    }


    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userName,lastMessage,lastTime,newMessages;
        ViewHolder(View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.otherUserNick);
            lastMessage=itemView.findViewById(R.id.lastMessage);
            lastTime=itemView.findViewById(R.id.time);
            newMessages=itemView.findViewById(R.id.newMessages);
        }


        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemClick("");
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public interface ItemClickListener {
        void onItemClick(String userName);
    }
}