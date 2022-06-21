package com.egormoroz.schooly.ui.profile;

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
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.ViewHolder>  {

    ArrayList<Subscriber> listAdapter;
    private SubscribersAdapter.ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();
    UserInformation userInformation;
    ArrayList<Subscriber> subscriptions,blackList;
    String nick;
    int a=0;

    public  SubscribersAdapter(ArrayList<Subscriber> listAdapter,UserInformation userInformation) {
        this.listAdapter = listAdapter;
        this.userInformation=userInformation;
    }


    @NotNull
    @Override
    public SubscribersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitemsubscribers, viewGroup, false);
        SubscribersAdapter.ViewHolder viewHolder=new SubscribersAdapter.ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subscriber subscriber=listAdapter.get(position);
        nick=userInformation.getNick();
        holder.otherUserNick.setText(subscriber.getSub());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) clickListener.onItemClick(view, position);
            }
        });
        if(nick.equals(subscriber.getSub())){
            holder.addFriend.setVisibility(View.GONE);
        }
        subscriptions= userInformation.getSubscription();
        for(int i=0;i<userInformation.getSubscription().size();i++){
            Subscriber sub=userInformation.getSubscription().get(i);
            if(sub.getSub().equals(subscriber.getSub())){
                holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.unsubscride));
                holder.addFriend.setTextColor(Color.parseColor("#F3A2E5"));
                holder.addFriend.setBackgroundResource(R.drawable.corners10appcolor2dpstroke);
            }
        }
        blackList=userInformation.getBlackList();
        for (int i=0;i<userInformation.getBlackList().size();i++){
            Subscriber sub=userInformation.getBlackList().get(i);
            if(sub.getSub().equals(subscriber.getSub())){
                holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.unlock));
                holder.addFriend.setTextColor(Color.parseColor("#F3A2E5"));
                holder.addFriend.setBackgroundResource(R.drawable.corners10appcolor2dpstroke);
            }
        }
        Query queryRequest2=firebaseModel.getUsersReference().child(subscriber.getSub())
                .child("requests").child(nick);
        queryRequest2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.requested));
                    holder.addFriend.setTextColor(Color.parseColor("#F3A2E5"));
                    holder.addFriend.setBackgroundResource(R.drawable.corners10appcolor2dpstroke);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseModel.getUsersReference().child(nick)
                        .child("subscription").child(subscriber.getSub()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot=task.getResult();
                            if (snapshot.exists()) {
                                a=1;
                            } else {
                                a=2;
                            }
                            firebaseModel.getUsersReference().child(subscriber.getSub())
                                    .child("requests").child(nick).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DataSnapshot snapshot=task.getResult();
                                        if (snapshot.exists()) {
                                            a=3;
                                        }
                                        firebaseModel.getUsersReference().child(subscriber.getSub())
                                                .child("blackList").child(nick).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DataSnapshot snapshot=task.getResult();
                                                    if (snapshot.exists()) {
                                                        a=4;
                                                    }
                                                    firebaseModel.getUsersReference().child(nick)
                                                            .child("blackList").child(subscriber.getSub()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                DataSnapshot snapshot=task.getResult();
                                                                if (snapshot.exists()) {
                                                                    a=5;
                                                                    holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.unlock));
                                                                    holder.addFriend.setTextColor(Color.parseColor("#F3A2E5"));
                                                                    holder.addFriend.setBackgroundResource(R.drawable.corners10appcolor2dpstroke);
                                                                }
                                                                if(a!=0) {
                                                                    if (a == 2) {
                                                                        Log.d("#####", "ab  " + a);
                                                                        Query query1=firebaseModel.getUsersReference().child(subscriber.getSub())
                                                                                .child("accountType");
                                                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if(snapshot.getValue(String.class).equals("open")){
                                                                                    firebaseModel.getReference().child("users").child(nick).child("subscription")
                                                                                            .child(subscriber.getSub()).setValue(subscriber.getSub());
                                                                                    firebaseModel.getReference().child("users").child(subscriber.getSub()).child("subscribers")
                                                                                            .child(nick).setValue(nick);
                                                                                    String numToBase=firebaseModel.getReference().child("users")
                                                                                            .child(subscriber.getSub()).child("nontifications")
                                                                                            .push().getKey();
                                                                                    Date date = new Date();
                                                                                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd hh:mm a");
                                                                                    String dateAndTime = formatter.format(date);
                                                                                    firebaseModel.getReference().child("users")
                                                                                            .child(subscriber.getSub()).child("nontifications")
                                                                                            .child(numToBase).setValue(new Nontification(nick,"не отправлено","обычный"
                                                                                            ,""," "," ","не просмотрено",numToBase,0));
                                                                                    holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.unsubscride));
                                                                                    holder.addFriend.setTextColor(Color.parseColor("#F3A2E5"));
                                                                                    holder.addFriend.setBackgroundResource(R.drawable.corners10appcolor2dpstroke);
                                                                                    a=0;
                                                                                }else {
                                                                                    firebaseModel.getReference().child("users").child(subscriber.getSub()).child("requests")
                                                                                            .child(nick).setValue(nick);
                                                                                    String numToBase=firebaseModel.getReference().child("users")
                                                                                            .child(subscriber.getSub()).child("nontifications")
                                                                                            .push().getKey();
                                                                                    Date date = new Date();
                                                                                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd hh:mm a");
                                                                                    String dateAndTime = formatter.format(date);
                                                                                    firebaseModel.getReference().child("users")
                                                                                            .child(subscriber.getSub()).child("nontifications")
                                                                                            .child(numToBase).setValue(new Nontification(nick,"не отправлено","запрос"
                                                                                            ,""," "," ","не просмотрено",numToBase,0));
                                                                                    holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.requested));
                                                                                    holder.addFriend.setTextColor(Color.parseColor("#F3A2E5"));
                                                                                    holder.addFriend.setBackgroundResource(R.drawable.corners10appcolor2dpstroke);
                                                                                    a=0;
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                    }
                                                                    if (a == 1) {
                                                                        firebaseModel.getReference().child("users").child(nick).child("subscription")
                                                                                .child(subscriber.getSub()).removeValue();
                                                                        firebaseModel.getReference().child("users").child(subscriber.getSub()).child("subscribers")
                                                                                .child(nick).removeValue();
                                                                        holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.subscride));
                                                                        holder.addFriend.setTextColor(Color.parseColor("#FFFEFE"));
                                                                        holder.addFriend.setBackgroundResource(R.drawable.corners10dpappcolor);
                                                                        a=0;

                                                                    }
                                                                    if (a == 3) {
                                                                        firebaseModel.getReference().child("users").child(subscriber.getSub()).child("requests")
                                                                                .child(nick).removeValue();
                                                                        holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.subscride));
                                                                        holder.addFriend.setTextColor(Color.parseColor("#FFFEFE"));
                                                                        holder.addFriend.setBackgroundResource(R.drawable.corners10dpappcolor);
                                                                        a=0;

                                                                    }if (a == 4) {
                                                                        Toast.makeText(v.getContext(), v.getContext().getResources().getText(R.string.theuserhasblockedyou), Toast.LENGTH_SHORT).show();
                                                                        a=0;
                                                                    }
                                                                    if (a == 5) {
                                                                        firebaseModel.getUsersReference().child(nick).child("blackList")
                                                                                .child(subscriber.getSub()).removeValue();
                                                                        holder.addFriend.setText(holder.addFriend.getContext().getResources().getText(R.string.subscride));
                                                                        holder.addFriend.setTextColor(Color.parseColor("#FFFEFE"));
                                                                        holder.addFriend.setBackgroundResource(R.drawable.corners10dpappcolor);
                                                                        a=0;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
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
        final TextView otherUserNick,addFriend;
        ViewHolder(View itemView) {
            super(itemView);
            otherUserNick = itemView.findViewById(R.id.otherUserNick);
            addFriend=itemView.findViewById(R.id.addFriend);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Subscriber getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(SubscribersAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}