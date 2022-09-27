package com.egormoroz.schooly.ui.people;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.R;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    List<UserPeopleAdapter> listAdapterPeople;
    private ItemClickListener clickListener;
    UserInformation userInformation;

    public  PeopleAdapter(ArrayList<UserPeopleAdapter> listAdapter,UserInformation userInformation) {
        this.listAdapterPeople = listAdapter;
        this.userInformation=userInformation;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.peoplerecyclerview_item, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserPeopleAdapter userInformation=listAdapterPeople.get(position);
        holder.usernickname.setText(userInformation.getNick());
        Picasso.get().load(userInformation.getAvatar()).into( holder.userAvatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) clickListener.onItemClick(v, position, userInformation.getAvatar()
                , userInformation.getBio());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAdapterPeople.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView usernickname;
        ImageView userAvatar;
        ViewHolder(View itemView) {
            super(itemView);
            usernickname=itemView.findViewById(R.id.usernickname);
            userAvatar=itemView.findViewById(R.id.useravatar);
        }
    }

    public UserPeopleAdapter getItem(int id) {
        return listAdapterPeople.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position,String avatar,String bio);
    }
}