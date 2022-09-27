package com.egormoroz.schooly.ui.main.MyClothes;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.coins.TransferMoneyAdapter;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PresentClothesAdapter  extends RecyclerView.Adapter<PresentClothesAdapter.ViewHolder>{

    ArrayList<Subscriber> listAdapter;
    private PresentClothesAdapter.ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();
    int alreadyHave=0;
    String nick;
    Clothes clothes;
    UserInformation userInformation;

    public  PresentClothesAdapter(ArrayList<Subscriber> listAdapter,Clothes clothes,UserInformation userInformation) {
        this.listAdapter = listAdapter;
        this.clothes=clothes;
        this.userInformation=userInformation;
    }

    @NonNull
    @Override
    public PresentClothesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_presentclothes, viewGroup, false);
        PresentClothesAdapter.ViewHolder viewHolder=new PresentClothesAdapter.ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PresentClothesAdapter.ViewHolder holder, int position) {
        Subscriber subscriber=listAdapter.get(position);
        nick=userInformation.getNick();
        holder.otherUserNick.setText(subscriber.getSub());
        holder.presentClothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseModel.getUsersReference().child(subscriber.getSub())
                        .child("clothes").child(clothes.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot=task.getResult();
                            if (snapshot.exists()) {
                                alreadyHave=1;
                            } else {
                                alreadyHave=2;
                            }
                            if(alreadyHave>0){
                                if (clickListener != null) clickListener.onItemClick(alreadyHave, position);
                            }
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView otherUserNick,presentClothes;
        ViewHolder(View itemView) {
            super(itemView);
            otherUserNick = itemView.findViewById(R.id.otherUserNick);
            presentClothes=itemView.findViewById(R.id.presentClothes);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(alreadyHave, getAdapterPosition());
        }
    }

    Subscriber getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(PresentClothesAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int alreadyHave, int position);
    }
}
