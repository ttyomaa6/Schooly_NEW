package com.egormoroz.schooly.ui.coins;

import android.graphics.Color;
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

import java.util.ArrayList;

public class TransferHistoryAdapter extends RecyclerView.Adapter<TransferHistoryAdapter.ViewHolder>{

    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<Transfer> transfers;

    public  TransferHistoryAdapter(ArrayList<Transfer> listAdapter) {
        this.transfers = listAdapter;
    }

    @NonNull
    @Override
    public TransferHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_transferhistory, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        firebaseModel.initAll();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransferHistoryAdapter.ViewHolder holder, int position) {
        Transfer transfer=transfers.get(position);
        holder.fromWho.setText(transfer.getWho());
        if (transfer.getType().equals("from")){
            holder.type.setText(holder.type.getContext().getResources().getText(R.string.receipt));
            holder.sum.setTextColor(Color.parseColor("#53B35C"));
            holder.sum.setText("+"+String.valueOf(transfer.getSum()));
            holder.image.setImageResource(R.drawable.ic_greenschoolycoin);
        }else {
            holder.type.setText(holder.type.getContext().getResources().getText(R.string.translation));
            holder.sum.setTextColor(Color.parseColor("#D0D0D0"));
            holder.sum.setText("-"+String.valueOf(transfer.getSum()));
            holder.image.setImageResource(R.drawable.ic_schoolycoin24dpgrey);
        }
    }

    @Override
    public int getItemCount() {
        return transfers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView type,fromWho,sum;
        ImageView image;
        ViewHolder(View itemView) {
            super(itemView);
            type=itemView.findViewById(R.id.type);
            fromWho=itemView.findViewById(R.id.fromWho);
            sum=itemView.findViewById(R.id.sum);
            image=itemView.findViewById(R.id.image);
        }

    }
}
