package com.egormoroz.schooly.ui.news;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
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
import com.egormoroz.schooly.FilamentModel;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.InstagramShareFragment;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.TaskRunner;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.profile.Complain;
import com.egormoroz.schooly.ui.profile.ComplainAdapter;
import com.egormoroz.schooly.ui.profile.Reason;
import com.egormoroz.schooly.ui.profile.SendLookAdapter;
import com.egormoroz.schooly.ui.profile.ViewingLookFragment;
import com.egormoroz.schooly.ui.profile.Wardrobe.ConstituentsAdapter;
import com.egormoroz.schooly.ui.profile.Wardrobe.WardrobeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
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
    static ArrayList<Subscriber> userFromBase;
    static TextView nickView,description,likesCount,lookPrice,lookPriceDollar,clothesCreator
            ,emptyList,comments,sendComment,noComment,save,complain,complainOtherUserText
            ,reasonText;
    static String likesCountString,lookPriceString,lookPriceDollarString,reasonTextString,descriptionText
            ,userName,otherUserNickString,editGetText,nick;
    static SendLookAdapter.ItemClickListener itemClickListener;
    ConstituentsAdapter.ItemClickListener itemClickListenerClothes;
    LinearLayout linearElse,linearTelegram,linearInstagram;
    ComplainAdapter.ItemClickListener itemClickListenerComplain;
    RelativeLayout sendReason;
    ArrayList<Clothes> lookClothesArrayList;
    static byte[] buffer;
    static URI uri;
    static Future<Buffer> future;
    static Buffer buffer1,bufferToFilament,b;
    static FilamentModel filamentModel=new FilamentModel();
    static ArrayList<Clothes> clothesList=new ArrayList<>();
    static ArrayList<String> clothesUid=new ArrayList<>();
    itemChanged itemChangeListener;
    Fragment fragment;
    Activity activity;


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
        nick=userInformation.getNick();
        filamentModel.postFrameCallback();
        holder.nick.setText(newsItem.getNick());
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

//        holder.send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showBottomSheetDialog(holder.itemView,holder.surfaceView,newsItem);
//            }
//        });
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
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView  like, comment,send,options;
        TextView description, like_count,clothesComponents,lookPrice,nick;
        ImageView surfaceView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            surfaceView=itemView.findViewById(R.id.surfaceView);
            send=itemView.findViewById(R.id.send);
            like = itemView.findViewById(R.id.like);
            description = itemView.findViewById(R.id.description);
            like_count = itemView.findViewById(R.id.likesCount);
            comment = itemView.findViewById(R.id.comment);
            clothesComponents=itemView.findViewById(R.id.clothesCreator);
            options=itemView.findViewById(R.id.options);
            lookPrice=itemView.findViewById(R.id.lookPrice);
            nick=itemView.findViewById(R.id.nick);
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
            }else {
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                .setValue(newsItem);
                        Toast.makeText(view.getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
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
            }else {
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                .setValue(newsItem);
                        Toast.makeText(view.getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
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
                DefaultDatabase.getReference().child("AppData").child("complains").child(uid)
                        .setValue(new Complain(newsItem.getNick(),nick, reasonTextString,descriptionText,uid,newsItem));
                Toast.makeText(view.getContext(), R.string.complaintsent, Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
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
                comment.add(convertSnapshotToComment(snapshot));
                commentAdapter.notifyItemInserted(comment.size() - 1);
               if(convertSnapshotToComment(snapshot).getType().equals("comment"))
                   for(DataSnapshot snap : snapshot.child("reply").getChildren()) {
                       comment.add(convertSnapshotToComment(snap));
                       commentAdapter.notifyItemInserted(comment.size() - 1);
                   }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("*****", "IM HERE");
                int it = 0;
                if(!convertSnapshotToComment(snapshot).getType().equals("reply")) {
                    for (DataSnapshot snap : snapshot.child("reply").getChildren()) {
                        if (it == snapshot.child("reply").getChildrenCount() - 1) {
                            Log.d("****", String.valueOf(snap.getRef()));
                            int insert_id = 0;
                            for (int j = 0; j < comment.size(); j++)
                                if (comment.get(j).getCommentId().equals(convertSnapshotToComment(snap).getParentId()))
                                    insert_id = j;
                            Log.d("*****", comment.size() + "   " + insert_id);
                            comment.add(insert_id + 1, convertSnapshotToComment(snap));
                            commentAdapter.notifyItemInserted(insert_id + 1);
                            Log.d("****", "Reply successfully completed");
                        }
                        ++it;
                    }
                }
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

    public void loadLookClothes(NewsItem newsItem){
        ArrayList<Clothes> clothesArrayListWithBuffers=newsItem.getClothesCreators();
        for(int i=0;i<clothesArrayListWithBuffers.size();i++){
            Clothes clothes=clothesArrayListWithBuffers.get(i);
            filamentModel.populateScene(clothes.getBuffer(), clothes);
        }
    }

    private void showBottomSheetDialogClothesCreators(NewsItem newsItem,View v) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_clothescreators);
        clothesCreatorsRecycler=bottomSheetDialog.findViewById(R.id.recyclerView);
        if(lookClothesArrayList.size()==0){
            RecentMethods.getLookClothes(newsItem.getNick(), newsItem.getNewsId(), firebaseNewsModel, new Callbacks.getLookClothes() {
                @Override
                public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                    ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(clothesArrayList,itemClickListenerClothes);
                    clothesCreatorsRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));
                    clothesCreatorsRecycler.setAdapter(constituentsAdapter);
                }
            });
        }else{
            ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(lookClothesArrayList,itemClickListenerClothes);
            clothesCreatorsRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));
            clothesCreatorsRecycler.setAdapter(constituentsAdapter);
        }
        itemClickListenerClothes=new ConstituentsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                bottomSheetDialog.dismiss();
                RecentMethods.setCurrentFragment(ViewingClothesNews.newInstance(ViewingLookFragment.newInstance(fragment, userInformation,bundle),userInformation,bundle), (Activity) v.getContext());
            }
        };

        bottomSheetDialog.show();
    }

    private void showBottomSheetDialog(View view,SurfaceView surfaceView,NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

        editText=bottomSheetDialog.findViewById(R.id.searchuser);
        recyclerView=bottomSheetDialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        emptyList=bottomSheetDialog.findViewById(R.id.emptySubscribersList);
        linearElse=bottomSheetDialog.findViewById(R.id.linearElse);
        linearTelegram=bottomSheetDialog.findViewById(R.id.linearTelegram);
        linearInstagram=bottomSheetDialog.findViewById(R.id.linearInstagram);
        messageEdit=bottomSheetDialog.findViewById(R.id.message);

        linearElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(NewsFragment.newInstance(userInformation, bundle), userInformation, bundle, null, "look", newsItem,null,"all"),activity);
                bottomSheetDialog.dismiss();
            }
        });
        linearTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(NewsFragment.newInstance(userInformation, bundle), userInformation, bundle, null, "look", newsItem,null,"telegram"),activity);
                bottomSheetDialog.dismiss();
            }
        });
        linearInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(NewsFragment.newInstance(userInformation, bundle), userInformation, bundle, null, "look", newsItem,null,"instagram"),activity);
                bottomSheetDialog.dismiss();
            }
        });
        if(userInformation.getSubscription()==null){
            RecentMethods.getSubscriptionList(userInformation.getNick(), firebaseNewsModel, new Callbacks.getFriendsList() {
                @Override
                public void getFriendsList(ArrayList<Subscriber> friends) {
                    if (friends.size()==0){
                        emptyList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
//                        SendLookAdapter sendLookAdapter = new SendLookAdapter(friends,itemClickListener);
//                        recyclerView.setAdapter(sendLookAdapter);
                    }
                }
            });
        }else {
            if (userInformation.getSubscription().size()==0){
                emptyList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
//                SendLookAdapter sendLookAdapter = new SendLookAdapter(userInformation.getSubscription(),itemClickListener);
//                recyclerView.setAdapter(sendLookAdapter);
            }
        }

        initUserEnter();

        bottomSheetDialog.show();
    }

    public interface Callback<Bitmap> {
        void onResult1(Bitmap bitmap);
    }

    public void initUserEnter() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userName = String.valueOf(editText.getText()).trim();
                userName = userName.toLowerCase();
                if(userInformation.getSubscription()==null){
                    Query query = firebaseNewsModel.getUsersReference().child(userInformation.getNick()).child("subscription");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userFromBase = new ArrayList<>();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                Subscriber subscriber = new Subscriber();
                                subscriber.setSub(snap.getValue(String.class));
                                String nick = subscriber.getSub();
                                int valueLetters = userName.length();
                                nick = nick.toLowerCase();
                                if (nick.length() < valueLetters) {
                                    if (nick.equals(userName))
                                        userFromBase.add(subscriber);
                                } else {
                                    nick = nick.substring(0, valueLetters);
                                    if (nick.equals(userName))
                                        userFromBase.add(subscriber);
                                }

                            }
                            if(userFromBase.size()==0){
                                emptyList.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }else {
                                emptyList.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
//                                SendLookAdapter sendLookAdapter = new SendLookAdapter(userFromBase,itemClickListener);
//                                recyclerView.setAdapter(sendLookAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    userFromBase=new ArrayList<>();
                    for (int s=0;s<userInformation.getSubscription().size();s++) {
                        Subscriber subscriber = userInformation.getSubscription().get(s);
                        String nick = subscriber.getSub();
                        int valueLetters = userName.length();
                        nick = nick.toLowerCase();
                        if (nick.length() < valueLetters) {
                            if (nick.equals(userName))
                                userFromBase.add(subscriber);
                        } else {
                            nick = nick.substring(0, valueLetters);
                            if (nick.equals(userName))
                                userFromBase.add(subscriber);
                        }

                    }
                    if(userFromBase.size()==0){
                        emptyList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        emptyList.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
//                        SendLookAdapter sendLookAdapter = new SendLookAdapter(userFromBase,itemClickListener);
//                        recyclerView.setAdapter(sendLookAdapter);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void addLastMessage(@NonNull String type, String Message){
        switch (type) {
            case "text":
                addType("text");
                firebaseNewsModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("LastMessage").setValue(Message);
                firebaseNewsModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("LastMessage").setValue(Message);
                break;
            case "voice":
                addType("voice");
                firebaseNewsModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("LastMessage").setValue("Голосовое сообщение");
                firebaseNewsModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("LastMessage").setValue("Голосовое сообщение");
                break;
            case "image":
                firebaseNewsModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("LastMessage").setValue("Фотография");
                firebaseNewsModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("LastMessage").setValue("Фотография");
                addType("image");
                break;
        }
        Calendar calendar = Calendar.getInstance();
        firebaseNewsModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("LastTime").setValue(RecentMethods.getCurrentTime());
        firebaseNewsModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("LastTime").setValue(RecentMethods.getCurrentTime());
        firebaseNewsModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
        firebaseNewsModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
    }

    public void addType(String type) {
        final long[] value = new long[1];
        DatabaseReference ref = firebaseNewsModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child(type);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    value[0] = (long) dataSnapshot.getValue();
                    value[0] = value[0] + 1;
                    dataSnapshot.getRef().setValue(value[0]);}
                else dataSnapshot.getRef().setValue(1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
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
                                    .child(newsItem.getNewsId()).child("comments").child(mainCommentId).child("reply").push().getKey();
                            firebaseNewsModel.getReference().child(newsItem.getNick())
                                    .child(newsItem.getNewsId()).child("comments").child(mainCommentId).child("reply").child(commentId)
                                    .setValue(new Comment(editText.getText().toString(), 0, commentId,RecentMethods.getCurrentTime(),nick,"image","reply", mainCommentId));
                            editText.getText().clear();
                            editText.setHint("");
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    public Comment convertSnapshotToComment(DataSnapshot snap){
        return new Comment(snap.child("text").getValue(String.class),
                           snap.child("likes_count").getValue(Long.class),
                           snap.child("commentId").getValue(String.class),
                           snap.child("postTime").getValue(String.class),
                           snap.child("nick").getValue(String.class),
                           snap.child("image").getValue(String.class),
                           snap.child("type").getValue(String.class),
                           snap.child("parentId").getValue(String.class));
    }

}


