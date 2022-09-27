package com.egormoroz.schooly.ui.coins;

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
import com.egormoroz.schooly.ui.profile.SubscribersAdapter;

import java.util.ArrayList;

public class TransferMoneyAdapter extends RecyclerView.Adapter<TransferMoneyAdapter.ViewHolder>{

    ArrayList<Subscriber> listAdapter;
    private TransferMoneyAdapter.ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();

    public  TransferMoneyAdapter(ArrayList<Subscriber> listAdapter) {
        this.listAdapter = listAdapter;
    }

    @NonNull
    @Override
    public TransferMoneyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_transermoney, viewGroup, false);
        TransferMoneyAdapter.ViewHolder viewHolder=new TransferMoneyAdapter.ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransferMoneyAdapter.ViewHolder holder, int position) {
        Subscriber subscriber=listAdapter.get(position);
        holder.otherUserNick.setText(subscriber.getSub());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) clickListener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView otherUserNick,addFriend;
        ViewHolder(View itemView) {
            super(itemView);
            otherUserNick = itemView.findViewById(R.id.otherUserNick);
            addFriend=itemView.findViewById(R.id.addFriend);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Subscriber getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(TransferMoneyAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
