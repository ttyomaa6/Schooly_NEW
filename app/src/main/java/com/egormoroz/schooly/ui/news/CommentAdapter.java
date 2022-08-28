package com.egormoroz.schooly.ui.news;

import android.os.Bundle;
import android.util.Log;
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
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.PeopleAdapter;
import com.egormoroz.schooly.ui.profile.ComplainAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Bundle bundle;
    String newsId, nick, likeWord;
    UserInformation userInformation;
    NewsItem newsItem;
    public CommentAdapter(List<Comment> commentAdapterList, String nick, Bundle bundle, String newsId, UserInformation userInformation, NewsItem newsItem) {
        this.commentAdapterList = commentAdapterList;
        this.nick=nick;
        this.bundle=bundle;
        this.userInformation = userInformation;
        this.newsId = newsId;
        this.newsItem = newsItem;
    }
    private List<Comment> commentAdapterList;
    FirebaseModel firebaseModel = new FirebaseModel();
    FirebaseModel firebaseNewsModel = new FirebaseModel();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.rvitem_comment, viewGroup, false);
        CommentAdapter.ViewHolder viewHolder=new CommentAdapter.ViewHolder(v);
        firebaseModel.initAll();
        firebaseNewsModel.initNewsDatabase();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment=commentAdapterList.get(position);
        holder.likesCount.setText(String.valueOf(comment.getLikes_count()));
        holder.nick.setText(comment.getNick());
        Picasso.get().load(comment.getImage()).into(holder.image);
        holder.comment.setText(comment.getText());
        holder.postTime.setText(comment.getPostTime());
        String resultPath = nick + "/" + newsId + "/comments/" + comment.getCommentId() + "/likes_count";
        if(comment.getType().equals("reply"))
            resultPath = nick + "/" + newsId + "/comments/" + comment.getParentId() +
                    "/reply/" + comment.getCommentId() + "/likes_count";
        String finalResultPath = resultPath;
        firebaseNewsModel.getReference(resultPath).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    long[] value = {task.getResult().getValue(Long.class)};
                    holder.likesCount.setText(String.valueOf(value[0]));
                    Query likeref = firebaseModel.getUsersReference().child(userInformation.getNick()).child("likedComm").child(comment.getCommentId());
                    likeref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                holder.like.setImageResource(R.drawable.ic_pressedheart40dp);
                            }
                            else {
                                holder.like.setImageResource(R.drawable.ic_heart40dp);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if(!comment.getType().equals("reply"))
                         holder.answer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                 NewsAdapter.CommentReply(comment.getCommentId(), comment.getNick(), newsItem);
                            }
                        });
                    holder.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Query likeref = firebaseModel.getUsersReference().child(userInformation.getNick()).child("likedComm").child(comment.getCommentId());
                            likeref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        value[0] -= 1;
                                        holder.like.setImageResource(R.drawable.ic_heart40dp);
                                        holder.likesCount.setText(String.valueOf(value[0]));
                                        firebaseModel.getUsersReference().child(userInformation.getNick()).child("likedComm").child(comment.getCommentId()).removeValue();
                                        firebaseNewsModel.getReference(finalResultPath).setValue(value[0]);
                                    }
                                    else {
                                        value[0] += 1;
                                        holder.likesCount.setText(String.valueOf(value[0]));
                                        holder.like.setImageResource(R.drawable.ic_pressedheart40dp);
                                        firebaseModel.getUsersReference().child(userInformation.getNick()).child("likedComm").child(comment.getCommentId()).setValue("liked");
                                        firebaseNewsModel.getReference(finalResultPath).setValue(value[0]);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });
                        }
                    });
                }
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView nick,comment,answer,likesCount,postTime;
        ImageView image,like;
        ViewHolder(View itemView) {
            super(itemView);
            nick=itemView.findViewById(R.id.nick);
            comment=itemView.findViewById(R.id.comment);
            postTime=itemView.findViewById(R.id.postTime);
            answer=itemView.findViewById(R.id.answer);
            likesCount=itemView.findViewById(R.id.likesCount);
            image=itemView.findViewById(R.id.image);
            like=itemView.findViewById(R.id.like);

        }

    }

    @Override
    public int getItemCount() {
        return commentAdapterList.size();
    }
}
