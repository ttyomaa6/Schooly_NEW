package com.egormoroz.schooly.ui.main.Nontifications;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class NontificationAdapter extends RecyclerView.Adapter<NontificationAdapter.ViewHolder>  {

    ArrayList<Nontification> listAdapter;
    private NontificationAdapter.ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();
    String nick;
    UserInformation userInformation;
    static Nontification sendNont;
    static String type;

    public  NontificationAdapter(ArrayList<Nontification> listAdapter, UserInformation userInformation) {
        this.listAdapter = listAdapter;
        this.userInformation=userInformation;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_nontification, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Nontification nontification=listAdapter.get(position);
        nick = userInformation.getNick();
        if (!nontification.getType().equals("запрос")){
            holder.addFriend.setVisibility(View.GONE);
        }
        if(nontification.getType().equals("не просмотрено")){
            firebaseModel.getUsersReference().child(nick).child("nontifications")
                    .child(nontification.getUid()).child("type")
                    .setValue("просмотрено");
        }
        if(nontification.getTypeView().equals("запрос")) {
            holder.otherUserNick.setVisibility(View.VISIBLE);
            holder.userImage.setVisibility(View.VISIBLE);
            holder.userImageCard.setVisibility(View.VISIBLE);
            holder.otherUserNick.setText(nontification.getNick()+holder.otherUserNick.getContext().getResources().getString(R.string.wantstofollowyou));
            holder.addFriend.setVisibility(View.VISIBLE);
            holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.added));
            holder.otherUserNick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) clickListener.onItemClick(listAdapter.get(holder.getAdapterPosition()),"sub");
                    sendNont=listAdapter.get(holder.getAdapterPosition());
                }
            });
            firebaseModel.getUsersReference().child(nontification.getNick()).child("personImage")
                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                Picasso.get().load(snapshot.getValue(String.class)).into(holder.userImage);
                            }
                        }
                    });
            holder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseModel.getReference().child("users")
                            .child(nick).child("subscribers")
                            .child(nontification.getNick()).setValue(nontification.getNick());
                    firebaseModel.getReference().child("users")
                            .child(nontification.getNick()).child("subscription")
                            .child(nick).setValue(nick);
                    firebaseModel.getUsersReference().child(nick).child("nontifications")
                            .child(nontification.getUid()).removeValue();
                    firebaseModel.getReference().child("users").child(nick).child("requests")
                            .child(nontification.getNick()).removeValue();
                    holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.added));
                }
            });
        }else if(nontification.getTypeView().equals("обычный")) {
            holder.otherUserNick.setVisibility(View.VISIBLE);
            holder.userImage.setVisibility(View.VISIBLE);
            holder.userImageCard.setVisibility(View.VISIBLE);
            firebaseModel.getUsersReference().child(nontification.getNick()).child("personImage")
                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                Picasso.get().load(snapshot.getValue(String.class)).into(holder.userImage);
                            }
                        }
                    });
            holder.otherUserNick.setText(nontification.getNick()+" "+holder.otherUserNick.getContext().getResources().getString(R.string.subscribeyou));
            holder.otherUserNick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) clickListener.onItemClick(listAdapter.get(holder.getAdapterPosition()),"sub");
                    sendNont=listAdapter.get(holder.getAdapterPosition());
                }
            });
        }else if(nontification.getTypeView().equals("одежда")) {
            holder.otherUserNick.setVisibility(View.VISIBLE);
            holder.userImage.setVisibility(View.VISIBLE);
            holder.userImageCard.setVisibility(View.VISIBLE);
            firebaseModel.getUsersReference().child(nontification.getNick()).child("personImage")
                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                Picasso.get().load(snapshot.getValue(String.class)).into(holder.userImage);
                            }
                        }
                    });
            holder.otherUserNick.setText(nontification.getNick()+" "+holder.otherUserNick.getContext().getResources().getString(R.string.boughtfromyou)+" "+nontification.getClothesName());
            holder.otherUserNick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) clickListener.onItemClick(listAdapter.get(holder.getAdapterPosition()),"sub");
                    sendNont=listAdapter.get(holder.getAdapterPosition());
                }
            });
        }else if (nontification.getTypeView().equals("перевод")){
            holder.otherUserNick.setVisibility(View.VISIBLE);
            holder.userImage.setVisibility(View.VISIBLE);
            holder.userImageCard.setVisibility(View.VISIBLE);
            firebaseModel.getUsersReference().child(nontification.getNick()).child("personImage")
                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                Picasso.get().load(snapshot.getValue(String.class)).into(holder.userImage);
                            }
                        }
                    });
            holder.otherUserNick.setText(nontification.getNick()+" "+holder.otherUserNick.getContext().getResources().getString(R.string.translatedtoyou)+" "+nontification.getClothesName()+"S"+" "+
                    holder.otherUserNick.getContext().getResources().getString(R.string.coins1));
            holder.otherUserNick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) clickListener.onItemClick(listAdapter.get(holder.getAdapterPosition()),"sub");
                    sendNont=listAdapter.get(holder.getAdapterPosition());
                }
            });
        }else if (nontification.getTypeView().equals("запросодежда")){
            holder.otherUserNick.setVisibility(View.VISIBLE);
            holder.userImage.setVisibility(View.VISIBLE);
            holder.userImageCard.setVisibility(View.VISIBLE);
            firebaseModel.getUsersReference().child(nontification.getNick()).child("personImage")
                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                Picasso.get().load(snapshot.getValue(String.class)).into(holder.userImage);
                            }
                        }
                    });
            Picasso.get().load(nontification.getClothesImage()).into(holder.userImage);
            holder.otherUserNick.setText(holder.otherUserNick.getContext().getResources().getText(R.string.applicationreceivedru)+" "+nontification.getClothesName()+" "+holder.otherUserNick.getContext().getResources().getText(R.string.applicationreceiveden));
            holder.addFriend.setVisibility(View.VISIBLE);
            holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.moveto));
            holder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) clickListener.onItemClick(listAdapter.get(holder.getAdapterPosition()),"clothesRequest");
                    sendNont=listAdapter.get(holder.getAdapterPosition());
                }
            });
        }
        else if (nontification.getTypeView().equals("подарок")){
            holder.otherUserNick.setVisibility(View.VISIBLE);
            holder.userImage.setVisibility(View.VISIBLE);
            holder.userImageCard.setVisibility(View.VISIBLE);
            firebaseModel.getUsersReference().child(nontification.getNick()).child("personImage")
                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                Picasso.get().load(snapshot.getValue(String.class)).into(holder.userImage);
                            }
                        }
                    });
            holder.otherUserNick.setText(nontification.getNick()+" "+holder.otherUserNick.getContext().getResources().getString(R.string.gaveyou)+" "+nontification.getClothesName()+" !!!");
            holder.addFriend.setVisibility(View.GONE);
            holder.otherUserNick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) clickListener.onItemClick(listAdapter.get(holder.getAdapterPosition()),"sub");
                    sendNont=listAdapter.get(holder.getAdapterPosition());
                }
            });
        }
        else if (nontification.getTypeView().equals("майнинг")){
            holder.imageCoins.setVisibility(View.VISIBLE);
            holder.fromWho.setVisibility(View.VISIBLE);
            holder.sum.setVisibility(View.VISIBLE);
            holder.type.setVisibility(View.VISIBLE);
            holder.remittanceTime.setVisibility(View.VISIBLE);
            holder.type.setText(holder.type.getContext().getResources().getText(R.string.receipt));
            holder.fromWho.setText(holder.fromWho.getContext().getResources().getText(R.string.mining));
            holder.sum.setText("+"+String.valueOf(nontification.getClothesProfit()));
            holder.otherUserNick.setVisibility(View.GONE);
            holder.addFriend.setVisibility(View.GONE);
            holder.userImage.setVisibility(View.GONE);
            holder.userImageCard.setVisibility(View.GONE);
        }
        else if (nontification.getTypeView().equals("одеждаприбыль")){
            holder.imageCoins.setVisibility(View.VISIBLE);
            holder.fromWho.setVisibility(View.VISIBLE);
            holder.sum.setVisibility(View.VISIBLE);
            holder.type.setVisibility(View.VISIBLE);
            holder.remittanceTime.setVisibility(View.VISIBLE);
            holder.type.setText(holder.type.getContext().getResources().getText(R.string.receipt));
            holder.fromWho.setText(holder.fromWho.getContext().getResources().getText(R.string.clothes));
            holder.sum.setText("+"+String.valueOf(nontification.getClothesProfit()));
            holder.otherUserNick.setVisibility(View.GONE);
            holder.addFriend.setVisibility(View.GONE);
            holder.userImage.setVisibility(View.GONE);
            holder.userImageCard.setVisibility(View.GONE);
        }
    }



    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView otherUserNick,addFriend,type,fromWho,sum,remittanceTime;
        ImageView imageCoins,userImage;
        CardView userImageCard;
        ViewHolder(View itemView) {
            super(itemView);
            otherUserNick = itemView.findViewById(R.id.otherUserNick);
            addFriend=itemView.findViewById(R.id.addFriend);
            type=itemView.findViewById(R.id.type);
            fromWho=itemView.findViewById(R.id.fromWho);
            sum=itemView.findViewById(R.id.sum);
            imageCoins=itemView.findViewById(R.id.image);
            userImage=itemView.findViewById(R.id.userImage);
            userImageCard=itemView.findViewById(R.id.userImageCard);
            remittanceTime=itemView.findViewById(R.id.remittanceTime);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(sendNont,"ok");
        }
    }

    Nontification getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(NontificationAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public static void singeClothesInfo(NontificationAdapter.ItemClickListener itemClickListener){
        itemClickListener.onItemClick(sendNont,type);
    }

    public interface ItemClickListener {
        void onItemClick( Nontification nontification,String type);
    }
}