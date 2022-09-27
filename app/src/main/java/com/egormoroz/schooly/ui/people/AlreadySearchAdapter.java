package com.egormoroz.schooly.ui.people;

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
import java.util.List;

public class AlreadySearchAdapter extends RecyclerView.Adapter<AlreadySearchAdapter.ViewHolder>{

    List<UserPeopleAdapter> listAdapterPeople;
    private AlreadySearchAdapter.ItemClickListener clickListener;
    String nick;
    private FirebaseModel firebaseModel = new FirebaseModel();
    UserInformation userInformation;

    public  AlreadySearchAdapter(ArrayList<UserPeopleAdapter> listAdapter, UserInformation userInformation) {
        this.listAdapterPeople = listAdapter;
        this.userInformation=userInformation;
    }

    @NotNull
    @Override
    public AlreadySearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_alreadysearch, viewGroup, false);
        AlreadySearchAdapter.ViewHolder viewHolder=new AlreadySearchAdapter.ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(AlreadySearchAdapter.ViewHolder holder, int position) {
        UserPeopleAdapter userInfo=listAdapterPeople.get(position);
        nick= userInformation.getNick();
        holder.usernickname.setText(userInfo.getNick());
        Picasso.get().load(userInformation.getAvatar()).into( holder.userAvatar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) clickListener.onItemClick(v, position,"profile");
            }
        });
        holder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) clickListener.onItemClick(v, position,"delete");
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAdapterPeople.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView usernickname;
        ImageView userAvatar,cross;
        ViewHolder(View itemView) {
            super(itemView);
            usernickname=itemView.findViewById(R.id.usernickname);
            userAvatar=itemView.findViewById(R.id.useravatar);
            cross=itemView.findViewById(R.id.cross);
        }
    }

    public UserPeopleAdapter getItem(int id) {
        return listAdapterPeople.get(id);
    }

    public void setClickListener(AlreadySearchAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position,String type);
    }
}
