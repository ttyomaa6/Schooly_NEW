package com.egormoroz.schooly.ui.main.CreateCharacter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BrowsAdapter extends RecyclerView.Adapter<BrowsAdapter.ViewHolder>{

    ArrayList<FacePart> bodyPartArrayList;
    ItemClickListener onItemClick;
    ArrayList<FacePart> activeFaceParts;
    String type;

    public BrowsAdapter(ArrayList<FacePart> bodyPartArrayList, ItemClickListener onItemClick,ArrayList<FacePart> activeFaceParts,String type) {
        this.bodyPartArrayList= bodyPartArrayList;
        this.onItemClick= onItemClick;
        this.activeFaceParts=activeFaceParts;
        this.type=type;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_brows, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FacePart facePart=bodyPartArrayList.get(position);
        Picasso.get().load(facePart.getFacePartImage()).into(holder.imageViewPerson);
        holder.imageViewPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onItemClick(facePart);
            }
        });
    }

    public interface ItemClickListener {
        void onItemClick( FacePart facePart);
    }

    @Override
    public int getItemCount() {
        return bodyPartArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPerson;
        ViewHolder(View itemView) {
            super(itemView);
            imageViewPerson=itemView.findViewById(R.id.personImage);
        }


    }
}
