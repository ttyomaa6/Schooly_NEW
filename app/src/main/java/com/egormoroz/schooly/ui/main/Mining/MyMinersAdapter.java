package com.egormoroz.schooly.ui.main.Mining;

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
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyMinersAdapter extends RecyclerView.Adapter<MyMinersAdapter.ViewHolder> {

    List<Miner> listAdapter;
    private ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();
    UserInformation userInformation;
    String nick;

    public  MyMinersAdapter(ArrayList<Miner> listAdapter,UserInformation userInformation) {
        this.listAdapter = listAdapter;
        this.userInformation=userInformation;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.myminers_item, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        firebaseModel.initAll();
        nick=userInformation.getNick();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Miner miner=listAdapter.get(position);
        holder.inHour.setText("+"+String.valueOf(miner.getInHour())+"S");
        holder.minerImage.setVisibility(View.VISIBLE);
        switch ((int) miner.getInHour()){
            case 10:
                holder.minerImage.setBackgroundResource(R.drawable.weak0);
                break;
            case 11:
                holder.minerImage.setBackgroundResource(R.drawable.weak1);
                break;
            case 12:
                holder.minerImage.setBackgroundResource(R.drawable.weak2);
                break;
            case 13:
                holder.minerImage.setBackgroundResource(R.drawable.weak3);
                break;
            case 14:
                holder.minerImage.setBackgroundResource(R.drawable.weak4);
                break;
            case 20:
                holder.minerImage.setBackgroundResource(R.drawable.medium0);
                break;
            case 22:
                holder.minerImage.setBackgroundResource(R.drawable.medium1);
                break;
            case 24:
                holder.minerImage.setBackgroundResource(R.drawable.medium2);
                break;
            case 26:
                holder.minerImage.setBackgroundResource(R.drawable.madium3);
                break;
            case 28:
                holder.minerImage.setBackgroundResource(R.drawable.medium4);
                break;
            case 30:
                holder.minerImage.setBackgroundResource(R.drawable.strong0);
                break;
            case 33:
                holder.minerImage.setBackgroundResource(R.drawable.strong1);
                break;
            case 36:
                holder.minerImage.setBackgroundResource(R.drawable.strong2);
                break;
            case 39:
                holder.minerImage.setBackgroundResource(R.drawable.strong3);
                break;
            case 42:
                holder.minerImage.setBackgroundResource(R.drawable.strong4);
                break;
        }
        if(userInformation.getMiners().size()==5){
            holder.use.setBackgroundResource(R.drawable.corners14grey);
        }
        for (int i=0;i<userInformation.getMiners().size();i++){
            Miner miner1=userInformation.getMiners().get(i);
            if(String.valueOf(miner1.getMinerPrice()).equals(String.valueOf(miner.getMinerPrice()))){
                holder.use.setText(holder.use.getContext().getResources().getText(R.string.used));
                holder.use.setBackgroundResource(R.drawable.corners14dpappcolor2dpstroke);
                holder.use.setTextColor(Color.parseColor("#F3A2E5"));
            }
        }
        holder.use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.use.getText().toString().equals(holder.use.getContext().getResources().getText(R.string.used))){

                }else{
                    if(userInformation.getMiners().size()==3){
                        holder.use.setBackgroundResource(R.drawable.corners14grey);
                        Toast.makeText(v.getContext(), v.getContext().getResources().getText(R.string.fiveminersarealreadyactive),Toast.LENGTH_SHORT).show();
                    }else {
                        int pos=holder.getAdapterPosition();
                        holder.use.setText(holder.use.getContext().getResources().getText(R.string.used));
                        holder.use.setBackgroundResource(R.drawable.corners14dpappcolor2dpstroke);
                        holder.use.setTextColor(Color.parseColor("#F3A2E5"));
                        firebaseModel.getUsersReference().child(nick)
                                .child("activeMiners")
                                .child(String.valueOf(miner.getMinerPrice())).setValue(listAdapter.get(pos));

                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView use;
        final ImageView minerImage;
        final TextView inHour;
        ViewHolder(View itemView) {
            super(itemView);
            use = itemView.findViewById(R.id.use);
            inHour=itemView.findViewById(R.id.inhour);
            minerImage=itemView.findViewById(R.id.viewrecycler);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Miner getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}