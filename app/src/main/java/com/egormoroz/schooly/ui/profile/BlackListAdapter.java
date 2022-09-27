package com.egormoroz.schooly.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.UserInformation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder>  {

    ArrayList<Subscriber> listAdapter;
    private BlackListAdapter.ItemClickListener clickListener;
    String nick;
    private FirebaseModel firebaseModel = new FirebaseModel();
    UserInformation userInformation;

    public  BlackListAdapter(ArrayList<Subscriber> listAdapter, UserInformation userInformation) {
        this.listAdapter = listAdapter;
        this.userInformation=userInformation;
    }


    @NotNull
    @Override
    public BlackListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_blacklist, viewGroup, false);
        BlackListAdapter.ViewHolder viewHolder=new BlackListAdapter.ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subscriber subscriber=listAdapter.get(position);
        nick=userInformation.getNick();
        holder.otherUserNick.setText(subscriber.getSub());
        holder.otherUserNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) clickListener.onItemClick(v, position);
            }
        });
        holder.putAwayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseModel.getUsersReference().child(nick)
                        .child("blackList").child(subscriber.getSub()).removeValue();
                holder.putAwayText.setText(R.string.putAwayDone);
                holder.putAwayText.setBackgroundResource(R.drawable.corners10grey);
            }
        });
    }



    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView otherUserNick,putAwayText;
        ViewHolder(View itemView) {
            super(itemView);
            otherUserNick = itemView.findViewById(R.id.otherUserNick);
            putAwayText=itemView.findViewById(R.id.putAwayText);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Subscriber getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(BlackListAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
