package com.egormoroz.schooly.ui.profile;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ViewHolder>  {

    ArrayList<Reason> listAdapter;
    private FirebaseModel firebaseModel = new FirebaseModel();
    int clickValue;
    static ArrayList<Reason> reasonsToBase=new ArrayList<>();
    ComplainAdapter.ItemClickListener itemClickListener;
    static Reason reason1;

    public  ComplainAdapter(ArrayList<Reason> listAdapter,ItemClickListener itemClickListener) {
        this.listAdapter = listAdapter;
        this.itemClickListener=itemClickListener;
    }

    public static void complain(ItemClickListener itemClickListener){
        itemClickListener.onItemClick(reason1);
    }


    @NotNull
    @Override
    public ComplainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_reason, viewGroup, false);
        ComplainAdapter.ViewHolder viewHolder=new ComplainAdapter.ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reason reason=listAdapter.get(position);
        holder.reasonText.setText(reason.getReason());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(new Reason(holder.reasonText.getText().toString()));
                reason1=new Reason(holder.reasonText.getText().toString());
            }
        });
    }



    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        final TextView reasonText;
        ViewHolder(View itemView) {
            super(itemView);
            reasonText=itemView.findViewById(R.id.reasonText);
        }
    }

    Reason getItem(int id) {
        return listAdapter.get(id);
    }


    public interface ItemClickListener {
        void onItemClick(Reason reason);
    }

}
