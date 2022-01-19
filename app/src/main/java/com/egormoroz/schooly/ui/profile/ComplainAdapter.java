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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ViewHolder>  {

    ArrayList<Reason> listAdapter;
    private ComplainAdapter.ItemClickListener clickListener;
    private FirebaseModel firebaseModel = new FirebaseModel();
    int clickValue;
    static ArrayList<Reason> reasonsToBase=new ArrayList<>();

    public  ComplainAdapter(ArrayList<Reason> listAdapter) {
        this.listAdapter = listAdapter;
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
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()) {
                    reasonsToBase.add(new Reason(holder.reasonText.getText().toString()));
                    Log.d("####", "list "+reasonsToBase);
                    Log.d("####", "lyj "+holder.reasonText.getText().toString());
                }
                else {
                    int indexToRemove=reasonsToBase.indexOf(new Reason(reason.getReason()));
                    Log.d("####", "i "+indexToRemove);
                    Log.d("####", "listfhtjyj "+holder.reasonText.getText().toString());
                    reasonsToBase.remove(reasonsToBase.indexOf(new Reason(reason.getReason())));
                    Log.d("####", "listbj "+reasonsToBase);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return listAdapter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView reasonText;
        final CheckBox checkBox;
        ViewHolder(View itemView) {
            super(itemView);
            reasonText=itemView.findViewById(R.id.reasonText);
            checkBox=itemView.findViewById(R.id.checkBox);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public interface reasonsList{
        void sendList(ArrayList<Reason> reasons);
    }
    public static void getReasonsList(reasonsList reasonsList){
        reasonsList.sendList(reasonsToBase);
    }

    Reason getItem(int id) {
        return listAdapter.get(id);
    }

    void setClickListener(ComplainAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
