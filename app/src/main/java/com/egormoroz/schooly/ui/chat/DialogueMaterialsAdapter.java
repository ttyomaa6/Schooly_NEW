package com.egormoroz.schooly.ui.chat;

import android.content.Intent;
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
import com.egormoroz.schooly.ui.chat.holders.ImageViewerActivity;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.profile.BlackListAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DialogueMaterialsAdapter extends RecyclerView.Adapter<DialogueMaterialsAdapter.ViewHolder> {

    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<String> listAdapter;
    UserInformation userInformation;
    DialogueMaterialsAdapter.ItemClickListener clickListener;

    public  DialogueMaterialsAdapter(ArrayList<String> listAdapter, UserInformation userInformation) {
        this.listAdapter = listAdapter;
        this.userInformation=userInformation;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext()).
                inflate(R.layout.rvitem_dialogue_materials, parent, false);
        ViewHolder viewHolder=new ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String image=listAdapter.get(position);
        Picasso.get().load(image).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                intent.putExtra("url", listAdapter.get(holder.getAdapterPosition()));
                holder.itemView.getContext().startActivity(intent);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick("image");
        }
    }

    void setClickListener(DialogueMaterialsAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(String image);
    }
}
