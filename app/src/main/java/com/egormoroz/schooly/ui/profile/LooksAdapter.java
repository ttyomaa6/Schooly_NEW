package com.egormoroz.schooly.ui.profile;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
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
import com.egormoroz.schooly.Subscriber;
import com.google.ar.sceneform.SceneView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LooksAdapter extends RecyclerView.Adapter<LooksAdapter.ViewHolder> {

    ArrayList<Look> listAdapter;
    private LooksAdapter.ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();

    public LooksAdapter(ArrayList<Look> listAdapter) {
        this.listAdapter = listAdapter;
    }


    @NotNull
    @Override
    public LooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_look, viewGroup, false);
        LooksAdapter.ViewHolder viewHolder=new LooksAdapter.ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LooksAdapter.ViewHolder holder, int position) {
        Look look=listAdapter.get(position);
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
        final SceneView lookScene;
        TextView viewPurchase;
        ViewHolder(View itemView) {
            super(itemView);
            lookScene=itemView.findViewById(R.id.lookScene);
            viewPurchase=itemView.findViewById(R.id.viewPurchase);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Look getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(LooksAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
