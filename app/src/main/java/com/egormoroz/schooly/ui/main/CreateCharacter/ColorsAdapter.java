package com.egormoroz.schooly.ui.main.CreateCharacter;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Color;
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FilamentModel;
import com.egormoroz.schooly.R;

import java.util.ArrayList;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ViewHolder>{

    ArrayList<Color> colorArrayList;
    ItemClickListener onItemClick;
    FilamentModel filamentModel=new FilamentModel();
    ArrayList<FacePart> activeFaceParts;
    String type;

    public ColorsAdapter(ArrayList<Color> colorArrayList, ItemClickListener onItemClick,String type) {
        this.colorArrayList= colorArrayList;
        this.onItemClick= onItemClick;
        this.activeFaceParts=activeFaceParts;
        this.type=type;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_colors_adapter, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Color color=colorArrayList.get(position);
        Log.d("######", "CL   "+color.getViewR()+"   "+color.getViewG()+"  "+color.getViewB());
        holder.view.setBackgroundColor(android.graphics.Color.rgb(color.getViewR(),color.getViewG(),color.getViewB()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onItemClick(color);
            }
        });
    }

    public interface ItemClickListener {
        void onItemClick( Color color);
    }

    @Override
    public int getItemCount() {
        return colorArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ViewHolder(View itemView) {
            super(itemView);
            view=itemView.findViewById(R.id.colorView);
        }


    }
}
