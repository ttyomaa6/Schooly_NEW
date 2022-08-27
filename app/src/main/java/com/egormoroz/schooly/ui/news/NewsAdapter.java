package com.egormoroz.schooly.ui.news;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AnyRes;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FilamentModel;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.InstagramShareFragment;
import com.egormoroz.schooly.LoadNewsItemInScene;
import com.egormoroz.schooly.Person;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.TaskRunner;
import com.egormoroz.schooly.ui.chat.Chat;
import com.egormoroz.schooly.ui.chat.GroupChatFragment;
import com.egormoroz.schooly.ui.chat.MessageFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.egormoroz.schooly.ui.profile.Complain;
import com.egormoroz.schooly.ui.profile.ComplainAdapter;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.egormoroz.schooly.ui.profile.Reason;
import com.egormoroz.schooly.ui.profile.SendLookAdapter;
import com.egormoroz.schooly.ui.profile.ViewingLookFragment;
import com.egormoroz.schooly.ui.profile.Wardrobe.ConstituentsAdapter;
import com.egormoroz.schooly.ui.profile.Wardrobe.ViewingClothesWardrobe;
import com.egormoroz.schooly.ui.profile.Wardrobe.WardrobeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ImageViewHolder> {
    public interface itemChanged{
        public void onItemChanged(int position, String type);
    }
    private List<NewsItem> newsList;
    static FirebaseModel firebaseNewsModel = new FirebaseModel();
    FirebaseModel DefaultDatabase = new FirebaseModel();
    static UserInformation userInformation;
    Bundle bundle;
    CommentAdapter commentAdapter;
    static EditText editText,messageEdit,addDescriptionEdit;
    RecyclerView clothesCreatorsRecycler,complainRecycler;
    static RecyclerView recyclerView;
    static TextView emptyList,comments,sendComment,noComment,save,complain,complainOtherUserText
            ,reasonText,noChats;
    static String reasonTextString,descriptionText
            ,otherUserNickString,editGetText,nick;
    static SendLookAdapter.ItemClickListener itemClickListener;
    ConstituentsAdapter.ItemClickListener itemClickListenerClothes;
    LinearLayout linearElse,linearTelegram,linearInstagram;
    ComplainAdapter.ItemClickListener itemClickListenerComplain;
    RelativeLayout sendReason;
    static FilamentModel filamentModel=new FilamentModel();
    itemChanged itemChangeListener;
    Fragment fragment;
    String getEditText;
    Activity activity;
    ArrayList<Chat> searchDialogsArrayList;


    public NewsAdapter(List<NewsItem> newsList,UserInformation userInformation,Bundle bundle,Fragment fragment,Activity activity,
                       itemChanged itemChangeListener) {
        this.newsList = newsList;
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
        this.activity=activity;
        this.itemChangeListener = itemChangeListener;
        firebaseNewsModel.initNewsDatabase();
        DefaultDatabase.initAll();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_image,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Log.d("ON BIND", "BIND POSITION: " + position);
        NewsItem newsItem = newsList.get(position);
        if(newsItem!=null){
            holder.relativeAll.setVisibility( View.VISIBLE);
            nick=userInformation.getNick();
            holder.nick.setText(newsItem.getNick());
            holder.noLooks.setVisibility(View.GONE);

            holder.nick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userNameToProfile=holder.nick.getText().toString();
                    DefaultDatabase.getUsersReference().child(userNameToProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                Toast.makeText(holder.itemView.getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                            }else {
                                if(userNameToProfile.equals(nick)){
                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback",nick,NewsFragment.newInstance( userInformation,bundle),userInformation,bundle),activity);
                                }else {
                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile,NewsFragment.newInstance( userInformation,bundle),userInformation,bundle
                                    ), activity);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            if(userInformation.getViewedNews()!=null){
                if(!userInformation.getViewedNews().contains(newsItem.getNewsId()))
                    DefaultDatabase.getUsersReference().child(userInformation.getNick())
                            .child("viewedNews").child(newsItem.getNewsId()).setValue(newsItem.getNewsId());
            }else {
                RecentMethods.loadViewedNews(userInformation.getNick(), DefaultDatabase, new Callbacks.LoadViewedNews() {
                    @Override
                    public void getViewedNews(ArrayList<String> viewedNews) {
                        if(!viewedNews.contains(newsItem.getNewsId()))
                            DefaultDatabase.getUsersReference().child(userInformation.getNick())
                                    .child("viewedNews").child(newsItem.getNewsId()).setValue(newsItem.getNewsId());
                    }
                });
            }
            firebaseNewsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId())
                            .child("viewCount").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                long viewCount=snapshot.getValue(Long.class);
                                firebaseNewsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId())
                                        .child("viewCount").setValue(viewCount+1);
                            }
                        }
                    });
            holder.like_count.setText(newsItem.getLikes_count());
            holder.description.setText(newsItem.getItem_description());
            firebaseNewsModel.initNewsDatabase();
            Picasso.get().load(newsItem.getImageUrl()).into(holder.surfaceView);
            Log.d("#####", "Database url" + firebaseNewsModel.getReference());
            firebaseNewsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId()).child("likes_count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        final long[] value = {Integer.valueOf(task.getResult().getValue(String.class))};
                        holder.like_count.setText(String.valueOf(value[0]));
                        Query likeref = DefaultDatabase.getUsersReference().child(nick).child("likedNews").child(newsItem.getNewsId());
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
                        holder.like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("#####", "Firebase : " + firebaseNewsModel.getReference() + "   Likes before " + value[0]);
                                Query likeref = DefaultDatabase.getUsersReference().child(nick).child("likedNews").child(newsItem.getNewsId());
                                likeref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            value[0] -= 1;
                                            holder.like.setImageResource(R.drawable.ic_heart40dp);
                                            DefaultDatabase.getUsersReference().child(nick).child("likedNews").child(newsItem.getNewsId()).removeValue();
                                        }
                                        else{
                                            value[0] += 1;
                                            holder.like.setImageResource(R.drawable.ic_pressedheart40dp);
                                            DefaultDatabase.getUsersReference().child(nick).child("likedNews").child(newsItem.getNewsId()).setValue("liked");
                                        }
                                        Log.d("#####", "Firebase : " + firebaseNewsModel.getReference() + "   Likes " + value[0]);
                                        holder.like_count.setText(String.valueOf(value[0]));
                                        firebaseNewsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId()).child("likes_count").setValue(String.valueOf(value[0]));
                                        itemChangeListener.onItemChanged(position, "like");
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

            holder.lookPrice.setText(String.valueOf(newsItem.getLookPrice()));

            holder.show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogViewingLook(newsItem, holder.itemView);
                }
            });

            holder.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialog(holder.itemView,newsItem);
                }
            });
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBottomSheetDialogComments(newsItem, holder.comment);
                }
            });
            holder.clothesComponents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialogClothesCreators(newsItem, holder.itemView);
                }
            });
            holder.options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialogLookOptions(newsItem,holder.itemView);
                }
            });
        }else {
            holder.noLooks.setVisibility( View.VISIBLE);
            holder.relativeAll.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView  like, comment,send,options,show;
        TextView description, like_count,clothesComponents,lookPrice,nick,noLooks;
        ImageView surfaceView;
        RelativeLayout relativeAll;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            surfaceView=itemView.findViewById(R.id.surfaceView);
            send=itemView.findViewById(R.id.send);
            noLooks=itemView.findViewById(R.id.noLooks);
            like = itemView.findViewById(R.id.like);
            description = itemView.findViewById(R.id.description);
            like_count = itemView.findViewById(R.id.likesCount);
            comment = itemView.findViewById(R.id.comment);
            clothesComponents=itemView.findViewById(R.id.clothesCreator);
            options=itemView.findViewById(R.id.options);
            lookPrice=itemView.findViewById(R.id.lookPrice);
            nick=itemView.findViewById(R.id.nick);
            show=itemView.findViewById(R.id.show);
            relativeAll=itemView.findViewById(R.id.relativeAll);
        }
    }


    private void showBottomSheetDialogComplain(NewsItem newsItem,View view) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_complain);

        complainOtherUserText=bottomSheetDialog.findViewById(R.id.complainOtherUserText);
        complainRecycler=bottomSheetDialog.findViewById(R.id.reasonsRecycler);

        complainOtherUserText.setText(newsItem.getNick());

        itemClickListenerComplain=new ComplainAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Reason reason) {
                showBottomSheetDialogComplainToBase(newsItem,reason,view);
                bottomSheetDialog.dismiss();
            }
        };
        ArrayList<Reason> arrayListReason=new ArrayList<>();
        arrayListReason.add(new Reason(view.getContext().getResources().getString(R.string.fraud)));
        arrayListReason.add(new Reason(view.getContext().getResources().getString(R.string.violenceordangerousorganizations)));
        arrayListReason.add(new Reason(view.getContext().getResources().getString(R.string.hostilesayingsorsymbols)));
        arrayListReason.add(new Reason(view.getContext().getResources().getString(R.string.saleofillegalgoods)));
        arrayListReason.add(new Reason(view.getContext().getResources().getString(R.string.violationofintellectualpropertyrights)));
        ComplainAdapter complainAdapter=new ComplainAdapter(arrayListReason,itemClickListenerComplain);
        complainRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        complainRecycler.setAdapter(complainAdapter);

        bottomSheetDialog.show();
    }

    private void showBottomSheetDialogLookOptions(NewsItem newsItem,View view) {
        if(!newsItem.getNick().equals(nick)){
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_lookoptions);

            save=bottomSheetDialog.findViewById(R.id.save);
            complain=bottomSheetDialog.findViewById(R.id.complain);

            if(userInformation.getSavedLooks().size()>0){
                for(int i=0;i<userInformation.getSavedLooks().size();i++){
                    NewsItem newsItem1=userInformation.getSavedLooks().get(i);
                    if(newsItem1.getNewsId().equals(newsItem.getNewsId())) {
                        save.setText(R.string.dontsave);
                    }
                }
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.returnNewsItem(newsItem, new Callbacks.loadNewsTread() {
                            @Override
                            public void LoadNews(NewsItem newsItem) throws IOException, URISyntaxException {
                                DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DataSnapshot snapshot=task.getResult();
                                                    if(snapshot.exists()){
                                                        save.setText(R.string.save);
                                                        DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                                                .removeValue();
                                                        Toast.makeText(view.getContext(), v.getContext().getResources().getText(R.string.lookwasdeletedfromsaved), Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        save.setText(R.string.dontsave);
                                                        Log.d("####", "####11"+newsItem.getNewsId()+"  "+newsItem.getNewsId());
                                                        DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                                                .setValue(newsItem);
                                                        Toast.makeText(view.getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                            }
                        });

                    }
                });
            }else {
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.returnNewsItem(newsItem, new Callbacks.loadNewsTread() {
                            @Override
                            public void LoadNews(NewsItem newsItem) throws IOException, URISyntaxException {
                                DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                        .setValue(newsItem);
                                Toast.makeText(view.getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }

            complain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialogComplain(newsItem,view);
                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetDialog.show();
        }else{
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_lookoptions);

            save=bottomSheetDialog.findViewById(R.id.save);
            complain=bottomSheetDialog.findViewById(R.id.complain);
            complain.setText(R.string.deletelook);

            if(userInformation.getSavedLooks().size()>0){
                for(int i=0;i<userInformation.getSavedLooks().size();i++){
                    NewsItem newsItem1=userInformation.getSavedLooks().get(i);
                    if(newsItem1.getNewsId().equals(newsItem.getNewsId())) {
                        save.setText(R.string.dontsave);
                    }
                }
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.returnNewsItem(newsItem, new Callbacks.loadNewsTread() {
                            @Override
                            public void LoadNews(NewsItem newsItem) throws IOException, URISyntaxException {
                                DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DataSnapshot snapshot=task.getResult();
                                                    if(snapshot.exists()){
                                                        save.setText(R.string.save);
                                                        DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                                                .removeValue();
                                                        Toast.makeText(view.getContext(), v.getContext().getResources().getText(R.string.lookwasdeletedfromsaved), Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        save.setText(R.string.dontsave);
                                                        DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                                                .setValue(newsItem);
                                                        Toast.makeText(view.getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                            }
                        });

                    }
                });
            }else {
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.returnNewsItem(newsItem, new Callbacks.loadNewsTread() {
                            @Override
                            public void LoadNews(NewsItem newsItem) throws IOException, URISyntaxException {
                                DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                        .setValue(newsItem);
                                Toast.makeText(view.getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }

            complain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DefaultDatabase.getUsersReference().child(nick).child("looks")
                            .child(newsItem.getNewsId()).removeValue();
                    Toast.makeText(view.getContext(), R.string.lookwasdeleted, Toast.LENGTH_SHORT).show();
                }
            });
            bottomSheetDialog.show();
        }
    }

    private void showBottomSheetDialogComplainToBase(NewsItem newsItem,Reason reason,View view) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_complaintobase);

        reasonText=bottomSheetDialog.findViewById(R.id.reasonText);
        sendReason=bottomSheetDialog.findViewById(R.id.sendReasons);
        addDescriptionEdit=bottomSheetDialog.findViewById(R.id.addDescriptionEdit);

        reasonTextString=reason.getReason();
        reasonText.setText(reasonTextString);
        sendReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionText=addDescriptionEdit.getText().toString();
                String uid=DefaultDatabase.getReference().child("AppData").child("complains").push().getKey();
                RecentMethods.returnNewsItem(newsItem, new Callbacks.loadNewsTread() {
                    @Override
                    public void LoadNews(NewsItem newsItem) throws IOException, URISyntaxException {
                        DefaultDatabase.getReference().child("AppData").child("complains").child(uid)
                                .setValue(new Complain(newsItem.getNick(),nick, reasonTextString,descriptionText,uid,newsItem));
                        Toast.makeText(view.getContext(), R.string.complaintsent, Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

            }
        });


        bottomSheetDialog.show();
    }

    private void showBottomSheetDialogComments(NewsItem newsItem,View v) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_comment);
        FirebaseModel newsModel = new FirebaseModel();
        newsModel.initNewsDatabase();
        editText=bottomSheetDialog.findViewById(R.id.commentEdit);
        recyclerView=bottomSheetDialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        emptyList=bottomSheetDialog.findViewById(R.id.emptyCommentsList);
        comments=bottomSheetDialog.findViewById(R.id.comments);
        comments.setText(R.string.comments);
        noComment=bottomSheetDialog.findViewById(R.id.noComment);
        sendComment=bottomSheetDialog.findViewById(R.id.send);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        loadComments(newsItem,v);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editGetText=editText.getText().toString();
                if (editGetText.length()==0){
                    sendComment.setVisibility(View.GONE);
                }else {
                    sendComment.setVisibility(View.VISIBLE);
                    sendComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String commentId=newsModel.getReference().child(newsItem.getNick())
                                    .child(newsItem.getNewsId()).child("comments").push().getKey();
                            newsModel.getReference().child(newsItem.getNick())
                                    .child(newsItem.getNewsId()).child("comments").child(commentId)
                                    .setValue(new Comment(editText.getText().toString(), 0, commentId,RecentMethods.getCurrentTime(),nick,"image","comment", ""));
                            editText.getText().clear();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bottomSheetDialog.show();
    }

    public void loadComments(NewsItem newsItem,View v){
        ArrayList<Comment> comment = new ArrayList<>();
        commentAdapter = new CommentAdapter(comment, newsItem.getNick(), bundle, newsItem.getNewsId(), userInformation, newsItem);
        firebaseNewsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId()).child("comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                comment.add(snapshot.getValue(Comment.class));
                commentAdapter.notifyItemInserted(comment.size() - 1);
               //if(snapshot.getValue(Comment.class).getType().equals("comment"))
               //    firebaseNewsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId())
               //            .child("comments").child(snapshot.getValue(Comment.class).getCommentId()).child("reply").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
               //        @Override
               //        public void onComplete(@NonNull Task<DataSnapshot> task) {
               //            if(task.isSuccessful())
               //                for(DataSnapshot snap : task.getResult().getChildren()) {
               //                    comment.add(snap.getValue(Comment.class));
               //                    commentAdapter.notifyItemInserted(comment.size() - 1);
               //                }
               //        }
               //    });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Comment comment1 = snapshot.getValue(Comment.class);
                firebaseNewsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId()).child("comments")
                        .child(comment1.getCommentId()).child("reply").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            DataSnapshot request = task.getResult();
                            int it = 0;
                            for(DataSnapshot snap : request.getChildren()) {
                                if (it == request.getChildrenCount() - 1) {
                                    Log.d("****", "HERE");
                                    int insert_id = 0;
                                    for(int j = 0; j < comment.size(); j++)
                                        if(comment.get(j).getCommentId() == snap.getValue(Comment.class).getParentId())
                                            insert_id = j;
                                    comment.add(insert_id + 1, snap.getValue(Comment.class));
                                    commentAdapter.notifyItemInserted(insert_id + 1);
                                }
                                ++it;
                            }
                        }
                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        noComment.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setAdapter(commentAdapter);
    }

    private void showBottomSheetDialogClothesCreators(NewsItem newsItem,View v) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_clothescreators);
        clothesCreatorsRecycler=bottomSheetDialog.findViewById(R.id.recyclerView);
        itemClickListenerClothes=new ConstituentsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                bottomSheetDialog.dismiss();
                RecentMethods.setCurrentFragment(ViewingClothesNews.newInstance(NewsFragment.newInstance( userInformation,bundle),userInformation,bundle), (Activity) v.getContext());
            }
        };
        if(newsItem.getClothesCreators()==null){
            RecentMethods.getLookClothes(newsItem.getNick(), newsItem.getNewsId(), firebaseNewsModel, new Callbacks.getLookClothes() {
                @Override
                public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                    ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(clothesArrayList,itemClickListenerClothes);
                    clothesCreatorsRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));
                    clothesCreatorsRecycler.setAdapter(constituentsAdapter);
                }
            });
        }else{
            if(newsItem.getClothesCreators().size()==0){
                RecentMethods.getLookClothes(newsItem.getNick(), newsItem.getNewsId(), firebaseNewsModel, new Callbacks.getLookClothes() {
                    @Override
                    public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                        ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(clothesArrayList,itemClickListenerClothes);
                        clothesCreatorsRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));
                        clothesCreatorsRecycler.setAdapter(constituentsAdapter);
                    }
                });
            } else {
                ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(newsItem.getClothesCreators(),itemClickListenerClothes);
                clothesCreatorsRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));
                clothesCreatorsRecycler.setAdapter(constituentsAdapter);
            }
        }

        bottomSheetDialog.show();
    }

    private void showBottomSheetDialog(View view,NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

        editText=bottomSheetDialog.findViewById(R.id.searchuser);
        recyclerView=bottomSheetDialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        noChats=bottomSheetDialog.findViewById(R.id.noChats);
        linearElse=bottomSheetDialog.findViewById(R.id.linearElse);
        linearTelegram=bottomSheetDialog.findViewById(R.id.linearTelegram);
        linearInstagram=bottomSheetDialog.findViewById(R.id.linearInstagram);
        messageEdit=bottomSheetDialog.findViewById(R.id.message);

        linearElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(NewsFragment.newInstance( userInformation, bundle), userInformation, bundle, null,"look",newsItem,null,"all"), activity);
                bottomSheetDialog.dismiss();
            }
        });

        linearTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(NewsFragment.newInstance( userInformation, bundle), userInformation, bundle, null,"look",newsItem,null,"telegram"), activity);
                bottomSheetDialog.dismiss();
            }
        });
        linearInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(NewsFragment.newInstance( userInformation, bundle), userInformation, bundle, null,"look",newsItem,null,"instagram"), activity);
                bottomSheetDialog.dismiss();
            }
        });
        itemClickListener=new SendLookAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Chat chat, String type) {
                if(type.equals("send")){
                    String messageText = messageEdit.getText().toString();
                    if(chat.getType().equals("talk")){
                        String messageSenderRef = chat.getChatId() + "/Messages";

                        RecentMethods.loadChatMembers(userInformation.getNick(), chat.getChatId(), DefaultDatabase, new Callbacks.GetChatMembers() {
                            @Override
                            public void getChatMembers(ArrayList<UserPeopleAdapter> chatMembers) {
                                for(int i=0;i<chatMembers.size();i++){
                                    String nick=chatMembers.get(i).getNick();
                                    addLastMessageGroup("look", messageText,nick,chat);
                                    addUnreadGroup(nick,chat);
                                }
                            }
                        });

                        NewsItem newsItem1;
                        newsItem1=newsItem;
                        RecentMethods.returnNewsItem(newsItem1, new Callbacks.loadNewsTread() {
                            @Override
                            public void LoadNews(NewsItem newsItem) throws IOException, URISyntaxException {
                                Map<String, Object> messageTextBody = new HashMap<>();
                                DatabaseReference userMessageKeyRef = DefaultDatabase.getReference().child("groups").child(chat.getChatId()).child("Messages").push();
                                String messagePushID = userMessageKeyRef.getKey();
                                messageTextBody.put("message", messageText);
                                messageTextBody.put("type", "look");
                                messageTextBody.put("from", userInformation.getNick() );
                                messageTextBody.put("to", chat.getName());
                                messageTextBody.put("time", RecentMethods.getCurrentTime());
                                messageTextBody.put("messageID", messagePushID);
                                messageTextBody.put("look", newsItem);

                                Map<String, Object> messageBodyDetails = new HashMap<String, Object>();
                                messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                DefaultDatabase.getReference().child("groups").updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        messageEdit.setText("");
                                    }
                                });
                            }
                        });
                    }else{

                        String messageSenderRef = chat.getName() + "/Chats/" + userInformation.getNick() + "/Messages";
                        String messageReceiverRef = userInformation.getNick()  + "/Chats/" + chat.getName()+ "/Messages";
                        otherUserNickString=chat.getName();

                        DatabaseReference userMessageKeyRef = DefaultDatabase.getUsersReference().child(userInformation.getNick() ).child("Chats").child(chat.getName()).child("Messages").push();
                        String messagePushID = userMessageKeyRef.getKey();

                        addLastMessage("look", messageText);
                        addUnread();

                        NewsItem newsItem1;
                        newsItem1=newsItem;
                        RecentMethods.returnNewsItem(newsItem1, new Callbacks.loadNewsTread() {
                            @Override
                            public void LoadNews(NewsItem newsItem) throws IOException, URISyntaxException {
                                Map<String, Object> messageTextBody = new HashMap<>();
                                messageTextBody.put("message", messageText);
                                messageTextBody.put("type", "look");
                                messageTextBody.put("from", userInformation.getNick() );
                                messageTextBody.put("to", chat.getName());
                                messageTextBody.put("time", RecentMethods.getCurrentTime());
                                messageTextBody.put("messageID", messagePushID);
                                messageTextBody.put("look", newsItem);

                                Map<String, Object> messageBodyDetails = new HashMap<String, Object>();
                                messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
                                DefaultDatabase.getUsersReference().updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        messageEdit.setText("");
                                    }
                                });
                            }
                        });
                    }
                }else {
                    if(chat.getType().equals("talk")){
                        RecentMethods.loadChatMembers(userInformation.getNick(), chat.getChatId(), DefaultDatabase, new Callbacks.GetChatMembers() {
                            @Override
                            public void getChatMembers(ArrayList<UserPeopleAdapter> chatMembers) {
                                chat.setMembers(chatMembers);
                                RecentMethods.setCurrentFragment(GroupChatFragment.newInstance(userInformation, bundle, NewsFragment.newInstance( userInformation, bundle), chat),activity);
                                bottomSheetDialog.dismiss();
                            }
                        });
                    }else{
                        RecentMethods.setCurrentFragment(MessageFragment.newInstance(userInformation, bundle, NewsFragment.newInstance( userInformation, bundle), chat),activity);
                        bottomSheetDialog.dismiss();
                    }
                }
            }
        };
        if(userInformation.getChats()==null || userInformation.getTalksArrayList()==null){
            RecentMethods.getDialogs(userInformation.getNick(), DefaultDatabase, new Callbacks.loadDialogs() {
                @Override
                public void LoadData(ArrayList<Chat> dialogs, ArrayList<Chat> talksArrayList) {
                    ArrayList<Chat> allChats=new ArrayList<>();
                    allChats.addAll(dialogs);
                    allChats.addAll(talksArrayList);
                    allChats=RecentMethods.sort_chats_by_time(allChats);
                    if (allChats.size()==0){
                        emptyList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        SendLookAdapter sendLookAdapter = new SendLookAdapter(allChats,itemClickListener);
                        recyclerView.setAdapter(sendLookAdapter);
                    }
                    initUserEnter(allChats);
                }
            });
        }else {
            ArrayList<Chat> allChats=new ArrayList<>();
            allChats.addAll(userInformation.getChats());
            allChats.addAll(userInformation.getTalksArrayList());
            allChats=RecentMethods.sort_chats_by_time(allChats);
            if (allChats.size()==0){
                emptyList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
                SendLookAdapter sendLookAdapter = new SendLookAdapter(allChats,itemClickListener);
                recyclerView.setAdapter(sendLookAdapter);
            }
            initUserEnter(allChats);
        }

        bottomSheetDialog.show();
    }

    private void addLastMessageGroup(String type, String Message,String name,Chat chat) {

        Log.d("####",type);
        DefaultDatabase.getUsersReference().child(name).child("Dialogs").child(chat.getChatId()).child("lastMessage").setValue("Образ");

        Calendar calendar = Calendar.getInstance();
        DefaultDatabase.getUsersReference().child(name).child("Dialogs").child(chat.getChatId()).child("lastTime").setValue(RecentMethods.getCurrentTime());
        Map<String,String> map=new HashMap<>();
        map= ServerValue.TIMESTAMP;
        DefaultDatabase.getUsersReference().child(name).child("Dialogs").child(chat.getChatId()).child("timeMill").setValue(map);
    }

    public void addUnreadGroup(String name,Chat chat) {
        final long[] value = new long[1];
        DatabaseReference ref = DefaultDatabase.getUsersReference().child(name).child("Dialogs").child(chat.getChatId()).child("unreadMessages");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value[0] = (long) dataSnapshot.getValue();
                    value[0] = value[0] + 1;
                    dataSnapshot.getRef().setValue(value[0]);
                    DefaultDatabase.getUsersReference().child(name).child("Dialogs")
                            .child(chat.getChatId()).child("unreadMessages").setValue(0);
                } else dataSnapshot.getRef().setValue(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    public void initUserEnter(ArrayList<Chat> allChats) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getEditText=editText.getText().toString().toLowerCase();
                if (getEditText.length()>0){
                    recyclerView.setVisibility(View.GONE);
                    searchChats(getEditText.toLowerCase(),allChats);

                }else if(getEditText.length()==0){

                    recyclerView.setVisibility(View.VISIBLE);
                    noChats.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void searchChats(String textEdit,ArrayList<Chat> allChats){
        if(allChats==null){
            DefaultDatabase.getUsersReference().child(userInformation.getNick()).child("Chats").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        searchDialogsArrayList=new ArrayList<>();
                        DataSnapshot snapshot=task.getResult();
                        for (DataSnapshot snap:snapshot.getChildren()){
                            Chat chat=new Chat();
                            chat.setName(snap.child("name").getValue(String.class));
                            chat.setLastMessage(snap.child("lastMessage").getValue(String.class));
                            chat.setLastTime(snap.child("lastTime").getValue(String.class));
                            chat.setUnreadMessages(snap.child("unreadMessages").getValue(Long.class));
                            chat.setType(snap.child("type").getValue(String.class));
                            String chatName=chat.getName();
                            String title=chatName;
                            int valueLetters=textEdit.length();
                            title=title.toLowerCase();
                            if(title.length()<valueLetters){
                                if(title.equals(textEdit))
                                    searchDialogsArrayList.add(chat);
                            }else{
                                title=title.substring(0, valueLetters);
                                if(title.equals(textEdit))
                                    searchDialogsArrayList.add(chat);
                            }
                        }
                        if (searchDialogsArrayList.size()==0){
                            recyclerView.setVisibility(View.GONE);
                            noChats.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            SendLookAdapter sendLookAdapter = new SendLookAdapter(searchDialogsArrayList,itemClickListener);
                            recyclerView.setAdapter(sendLookAdapter);
                            noChats.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }else {
            searchDialogsArrayList=new ArrayList<>();
            for (int i=0;i<allChats.size();i++) {
                Chat chat = allChats.get(i);
                String chatName=chat.getName();
                String title=chatName;
                int valueLetters=textEdit.length();
                title=title.toLowerCase();
                if(title.length()<valueLetters){
                    if(title.equals(textEdit))
                        searchDialogsArrayList.add(chat);
                }else{
                    title=title.substring(0, valueLetters);
                    if(title.equals(textEdit))
                        searchDialogsArrayList.add(chat);
                }
            }
            if (searchDialogsArrayList.size()==0){
                recyclerView.setVisibility(View.GONE);
                noChats.setVisibility(View.VISIBLE);
            }else {
                recyclerView.setVisibility(View.VISIBLE);
                SendLookAdapter sendLookAdapter = new SendLookAdapter(searchDialogsArrayList,itemClickListener);
                recyclerView.setAdapter(sendLookAdapter);
                noChats.setVisibility(View.GONE);
            }
        }
    }

    private void addLastMessage(String type, String Message){
        DefaultDatabase.getUsersReference().child(userInformation.getNick()).child("Dialogs").child(otherUserNickString).child("lastMessage").setValue("Образ");
        DefaultDatabase.getUsersReference().child(otherUserNickString).child("Dialogs").child(userInformation.getNick()).child("lastMessage").setValue("Образ");
        Calendar calendar = Calendar.getInstance();
        DefaultDatabase.getUsersReference().child(userInformation.getNick()).child("Dialogs").child(otherUserNickString).child("lastTime").setValue(RecentMethods.getCurrentTime());
        DefaultDatabase.getUsersReference().child(otherUserNickString).child("Dialogs").child(userInformation.getNick()).child("lastTime").setValue(RecentMethods.getCurrentTime());
        Map<String,String> map=new HashMap<>();
        map= ServerValue.TIMESTAMP;
        DefaultDatabase.getUsersReference().child(userInformation.getNick()).child("Dialogs").child(otherUserNickString).child("timeMill").setValue(map);
        DefaultDatabase.getUsersReference().child(otherUserNickString).child("Dialogs").child(userInformation.getNick()).child("timeMill").setValue(map);
    }

    public void addUnread() {
        final long[] value = new long[1];
        DatabaseReference ref = DefaultDatabase.getUsersReference().child(otherUserNickString).child("Dialogs").child(userInformation.getNick()).child("unreadMessages");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value[0] = (long) dataSnapshot.getValue();
                    value[0] = value[0] + 1;
                    dataSnapshot.getRef().setValue(value[0]);
                    DefaultDatabase.getUsersReference().child(userInformation.getNick()).child("Dialogs")
                            .child(otherUserNickString).child("unreadMessages").setValue(0);
                } else dataSnapshot.getRef().setValue(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    public interface Callback<Bitmap> {
        void onResult1(Bitmap bitmap);
    }
    public static void CommentReply(String mainCommentId, String name, NewsItem newsItem){
        editText.setHint("You replying to " + name + "\n");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editGetText=editText.getText().toString();
                if (editGetText.length()==0)
                    sendComment.setVisibility(View.GONE);
                else {
                    sendComment.setVisibility(View.VISIBLE);
                    sendComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String commentId=firebaseNewsModel.getReference().child(newsItem.getNick())
                                    .child(newsItem.getNewsId()).child("comments").push().getKey();
                            firebaseNewsModel.getReference().child(newsItem.getNick())
                                    .child(newsItem.getNewsId()).child("comments").child(mainCommentId).child("reply").child(commentId)
                                    .setValue(new Comment(editText.getText().toString(), 0, commentId,RecentMethods.getCurrentTime(),nick,"image","reply", mainCommentId));
                            editText.getText().clear();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void showDialogViewingLook(NewsItem newsItem,View view){

        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog_viewing_look);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        FirebaseModel newsModel = new FirebaseModel();


        CircularProgressIndicator progressIndicator;
        SurfaceView surfaceView;
        progressIndicator=dialog.findViewById(R.id.progressIndicator);
        surfaceView=dialog.findViewById(R.id.surfaceView);



        LoadNewsItemInScene loadNewsItemInScene=new LoadNewsItemInScene(userInformation, newsItem, new Callbacks.loadNewsTread() {
            @Override
            public void LoadNews(NewsItem newsItem)  {
                progressIndicator.setVisibility(View.GONE);
                filamentModel.postFrameCallback();
                try {
                    filamentModel.initNewsFilament(surfaceView,newsItem.getPerson().getBody().getBuffer(),true,null,"regularRender",true);
                    if(newsItem.getPerson().getBody().getColorX()!=null){
                        com.egormoroz.schooly.Color color=new com.egormoroz.schooly.Color();
                        color.setColorX(newsItem.getPerson().getBody().getColorX());
                        color.setColorY(newsItem.getPerson().getBody().getColorY());
                        color.setColorZ(newsItem.getPerson().getBody().getColorZ());
                        filamentModel.changeColor(newsItem.getPerson().getBody().getPartType(),color );
                    }
                    loadClothesInScene(newsItem.getClothesCreators());
                    if(newsItem.getPerson().getBrows()!=null){
                        filamentModel.populateSceneFacePart(newsItem.getPerson().getBrows().getBuffer());
                        if(newsItem.getPerson().getBrows().getColorX()!=null){
                            com.egormoroz.schooly.Color color=new com.egormoroz.schooly.Color();
                            color.setColorX(newsItem.getPerson().getBrows().getColorX());
                            color.setColorY(newsItem.getPerson().getBrows().getColorY());
                            color.setColorZ(newsItem.getPerson().getBrows().getColorZ());
                            filamentModel.changeColor(newsItem.getPerson().getBrows().getPartType(),color );
                        }
                    }
                    if(newsItem.getPerson().getHair()!=null){
                        filamentModel.populateSceneFacePart(newsItem.getPerson().getHair().getBuffer());
                        if(newsItem.getPerson().getHair().getColorX()!=null){
                            com.egormoroz.schooly.Color color=new com.egormoroz.schooly.Color();
                            color.setColorX(newsItem.getPerson().getHair().getColorX());
                            color.setColorY(newsItem.getPerson().getHair().getColorY());
                            color.setColorZ(newsItem.getPerson().getHair().getColorZ());
                            filamentModel.changeColor(newsItem.getPerson().getHair().getPartType(),color );
                        }

                    }
                    if(newsItem.getPerson().getLips().getBuffer()!=null){
                        filamentModel.populateSceneFacePart(newsItem.getPerson().getLips().getBuffer());
                        if(newsItem.getPerson().getLips().getColorX()!=null){
                            com.egormoroz.schooly.Color color=new com.egormoroz.schooly.Color();
                            color.setColorX(newsItem.getPerson().getLips().getColorX());
                            color.setColorY(newsItem.getPerson().getLips().getColorY());
                            color.setColorZ(newsItem.getPerson().getLips().getColorZ());
                            filamentModel.changeColor(newsItem.getPerson().getLips().getPartType(),color );
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
    }

    public void loadClothesInScene(ArrayList<Clothes> clothesArrayList){
        for(int i=0;i<clothesArrayList.size();i++){
          Clothes clothes=clothesArrayList.get(i);
          if(clothes.getBuffer()!=null){
              filamentModel.populateScene(clothes.getBuffer(), clothes);
          }
        }
    }


}


