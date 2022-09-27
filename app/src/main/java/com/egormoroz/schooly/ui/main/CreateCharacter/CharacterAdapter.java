package com.egormoroz.schooly.ui.main.CreateCharacter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FilamentModel;
import com.egormoroz.schooly.Person;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.ui.main.Shop.BasketAdapter;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder>{

    ArrayList<FacePart> bodyPartArrayList;
    ItemClickListener onItemClick;
    ArrayList<FacePart> activeFaceParts;
    String type;

    public CharacterAdapter(ArrayList<FacePart> bodyPartArrayList, ItemClickListener onItemClick,ArrayList<FacePart> activeFaceParts,String type) {
        this.bodyPartArrayList= bodyPartArrayList;
        this.onItemClick= onItemClick;
        this.activeFaceParts=activeFaceParts;
        this.type=type;
    }


    @NonNull
    @Override
    public CharacterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitemcharacter, viewGroup, false);
        CharacterAdapter.ViewHolder viewHolder=new CharacterAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterAdapter.ViewHolder holder, int position) {
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
