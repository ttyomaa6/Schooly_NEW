package com.egormoroz.schooly.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
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

import androidx.activity.OnBackPressedCallback;
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
import com.egormoroz.schooly.ui.chat.Chat;
import com.egormoroz.schooly.ui.chat.GroupChatFragment;
import com.egormoroz.schooly.ui.chat.MessageFragment;
import com.egormoroz.schooly.ui.main.MyClothes.CreateClothesFragment;
import com.egormoroz.schooly.ui.main.MyClothes.CriteriaFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.Comment;
import com.egormoroz.schooly.ui.news.CommentAdapter;
import com.egormoroz.schooly.ui.news.NewsFragment;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.news.ViewingClothesNews;
import com.egormoroz.schooly.ui.people.PeopleFragment;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.egormoroz.schooly.ui.profile.Wardrobe.AcceptNewLook;
import com.egormoroz.schooly.ui.profile.Wardrobe.ConstituentsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import javax.security.auth.callback.Callback;

public class ViewingLookFragment extends Fragment {

    FirebaseModel firebaseModel=new FirebaseModel();
    static FirebaseModel firebaseNewsModel=new FirebaseModel();
    ImageView back,like,comment,send,schoolyCoin,options,show;
    static TextView nickView,description,likesCount,lookPrice,lookPriceDollar,clothesCreator
            ,emptyList,comments,sendComment,noComment,save,complain,complainOtherUserText
            ,reasonText,noChats;
    //SceneView sceneView;
    LinearLayout linearElse,linearTelegram,linearInstagram;
    static EditText editText,messageEdit,addDescriptionEdit;
    RecyclerView clothesCreatorsRecycler,complainRecycler;
    RecyclerView recyclerView;
    static String likesCountString,lookPriceString,lookPriceDollarString,reasonTextString,descriptionText
            ,otherUserNickString,editGetText,nick;
    SendLookAdapter.ItemClickListener itemClickListener;
    ConstituentsAdapter.ItemClickListener itemClickListenerClothes;
    ComplainAdapter.ItemClickListener itemClickListenerComplain;
    RelativeLayout sendReason;
    CommentAdapter commentAdapter;
    UserInformation userInformation;
    Fragment fragment;
    ImageView lookImage;
    Bundle bundle;
    FirebaseModel DefaultDatabase = new FirebaseModel();
    ArrayList<Chat> searchDialogsArrayList;
    static FilamentModel filamentModel=new FilamentModel();
    String getEditText;

    public ViewingLookFragment(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment = fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
        firebaseNewsModel.initNewsDatabase();
        DefaultDatabase.initAll();
    }

    public static ViewingLookFragment newInstance(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new ViewingLookFragment(fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_viewinglook, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
//        AppBarLayout abl = getActivity().findViewById(R.id.AppBarLayout);
//        abl.setVisibility(abl.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        back=view.findViewById(R.id.back);
        like=view.findViewById(R.id.like);
        comment=view.findViewById(R.id.comment);
        description=view.findViewById(R.id.description);
        schoolyCoin=view.findViewById(R.id.schoolyCoin);
        clothesCreator=view.findViewById(R.id.clothesCreator);
        likesCount=view.findViewById(R.id.likesCount);
        lookImage=view.findViewById(R.id.lookImage);
        lookPrice=view.findViewById(R.id.lookPrice);
        show=view.findViewById(R.id.show);
        options=view.findViewById(R.id.options);
        lookPriceDollar=view.findViewById(R.id.lookPriceDollar);
        nickView=view.findViewById(R.id.nick);
        send=view.findViewById(R.id.send);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(fragment,getActivity());
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        LooksAdapter.lookInfo(new LooksAdapter.ItemClickListener() {
            @Override
            public void onItemClick(NewsItem newsItem) {
                nickView.setText(newsItem.getNick());
                nickView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userNameToProfile=nickView.getText().toString();
                        firebaseModel.getUsersReference().child(userNameToProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()){
                                    Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                }else {
                                    if(userNameToProfile.equals(nick)){
                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback",nick,ViewingLookFragment.newInstance(fragment, userInformation,bundle),userInformation,bundle),getActivity());
                                    }else {
                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile,ViewingLookFragment.newInstance(fragment, userInformation,bundle),userInformation,bundle
                                        ), getActivity());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
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
                Picasso.get().load(newsItem.getImageUrl()).into(lookImage);
                description.setText(newsItem.getItem_description());
                likesCountString=String.valueOf(newsItem.getLikes_count());
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
                comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheetDialogComments(newsItem);
                    }
                });
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheetDialog(newsItem);
                    }
                });
                show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogViewingLook(newsItem );
                    }
                });
                options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheetDialogLookOptions(newsItem);
                    }
                });
                clothesCreator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheetDialogClothesCreators(newsItem);
                    }
                });
                long likesCountLong=Long.valueOf(newsItem.getLikes_count());
                if(likesCountLong<1000){
                    likesCount.setText(String.valueOf(likesCountLong));
                }else if(likesCountLong>1000 && likesCountLong<10000){
                    likesCount.setText(likesCountString.substring(0, 1)+"."+likesCountString.substring(1, 2)+"K");
                }
                else if(likesCountLong>10000 && likesCountLong<100000){
                    likesCount.setText(likesCountString.substring(0, 2)+"."+likesCountString.substring(2,3)+"K");
                }
                else if(likesCountLong>10000 && likesCountLong<100000){
                    likesCount.setText(likesCountString.substring(0, 2)+"."+likesCountString.substring(2,3)+"K");
                }else if(likesCountLong>100000 && likesCountLong<1000000){
                    likesCount.setText(likesCountString.substring(0, 3)+"K");
                }
                else if(likesCountLong>1000000 && likesCountLong<10000000){
                    likesCount.setText(likesCountString.substring(0, 1)+"KK");
                }
                else if(likesCountLong>10000000 && likesCountLong<100000000) {
                    likesCount.setText(likesCountString.substring(0, 2) + "KK");
                }
                if (newsItem.getLookPrice()==0) {
                    lookPrice.setVisibility(View.GONE);
                    lookPriceDollarString=String.valueOf(newsItem.getLookPriceDollar());
                    if(newsItem.getLookPriceDollar()<1000){
                        lookPriceDollar.setText(lookPriceDollarString+"$");
                    }else if(newsItem.getLookPriceDollar()>1000 && newsItem.getLookPriceDollar()<10000){
                        lookPriceDollar.setText(lookPriceDollarString.substring(0, 1)+"."+lookPriceDollarString.substring(1, 2)+"K"+"$");
                    }
                    else if(newsItem.getLookPriceDollar()>10000 && newsItem.getLookPriceDollar()<100000){
                        lookPriceDollar.setText(lookPriceDollarString.substring(0, 2)+"."+lookPriceDollarString.substring(2,3)+"K"+"$");
                    }
                    else if(newsItem.getLookPriceDollar()>10000 && newsItem.getLookPriceDollar()<100000){
                        lookPriceDollar.setText(lookPriceDollarString.substring(0, 2)+"."+lookPriceDollarString.substring(2,3)+"K"+"$");
                    }else if(newsItem.getLookPriceDollar()>100000 && newsItem.getLookPriceDollar()<1000000){
                        lookPriceDollar.setText(lookPriceDollarString.substring(0, 3)+"K"+"$");
                    }
                    else if(newsItem.getLookPriceDollar()>1000000 && newsItem.getLookPriceDollar()<10000000){
                        lookPriceDollar.setText(lookPriceDollarString.substring(0, 1)+"KK"+"$");
                    }
                    else if(newsItem.getLookPriceDollar()>10000000 && newsItem.getLookPriceDollar()<100000000){
                        lookPriceDollar.setText(lookPriceDollarString.substring(0, 2)+"KK"+"$");
                    }
                }else{
                    lookPriceDollarString=String.valueOf(newsItem.getLookPriceDollar());
                    if(newsItem.getLookPriceDollar()<1000){
                        lookPriceDollar.setText(" + "+lookPriceDollarString+"$");
                    }else if(newsItem.getLookPriceDollar()>1000 && newsItem.getLookPriceDollar()<10000){
                        lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 1)+"."+lookPriceDollarString.substring(1, 2)+"K"+"$");
                    }
                    else if(newsItem.getLookPriceDollar()>10000 && newsItem.getLookPriceDollar()<100000){
                        lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 2)+"."+lookPriceDollarString.substring(2,3)+"K"+"$");
                    }
                    else if(newsItem.getLookPriceDollar()>10000 && newsItem.getLookPriceDollar()<100000){
                        lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 2)+"."+lookPriceDollarString.substring(2,3)+"K"+"$");
                    }else if(newsItem.getLookPriceDollar()>100000 && newsItem.getLookPriceDollar()<1000000){
                        lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 3)+"K"+"$");
                    }
                    else if(newsItem.getLookPriceDollar()>1000000 && newsItem.getLookPriceDollar()<10000000){
                        lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 1)+"KK"+"$");
                    }
                    else if(newsItem.getLookPriceDollar()>10000000 && newsItem.getLookPriceDollar()<100000000){
                        lookPriceDollar.setText(" + "+lookPriceDollarString.substring(0, 2)+"KK"+"$");
                    }
                }
                if (newsItem.getLookPriceDollar()==0){
                    lookPriceDollar.setVisibility(View.GONE);
                }
                if(newsItem.getLookPrice()==0){
                    schoolyCoin.setVisibility(View.GONE);
                    lookPrice.setVisibility(View.GONE);
                }else {
                    lookPriceString=String.valueOf(newsItem.getLookPrice());
                    if(newsItem.getLookPrice()<1000){
                        lookPrice.setText(String.valueOf(lookPriceString));
                    }else if(newsItem.getLookPrice()>1000 && newsItem.getLookPrice()<10000){
                        lookPrice.setText(lookPriceString.substring(0, 1)+"."+lookPriceString.substring(1, 2)+"K");
                    }
                    else if(newsItem.getLookPrice()>10000 && newsItem.getLookPrice()<100000){
                        lookPrice.setText(lookPriceString.substring(0, 2)+"."+lookPriceString.substring(2,3)+"K");
                    }
                    else if(newsItem.getLookPrice()>10000 && newsItem.getLookPrice()<100000){
                        lookPrice.setText(lookPriceString.substring(0, 2)+"."+lookPriceString.substring(2,3)+"K");
                    }else if(newsItem.getLookPrice()>100000 && newsItem.getLookPrice()<1000000){
                        lookPrice.setText(lookPriceString.substring(0, 3)+"K");
                    }
                    else if(newsItem.getLookPrice()>1000000 && newsItem.getLookPrice()<10000000){
                        lookPrice.setText(lookPriceString.substring(0, 1)+"KK");
                    }
                    else if(newsItem.getLookPrice()>10000000 && newsItem.getLookPrice()<100000000){
                        lookPrice.setText(lookPriceString.substring(0, 2)+"KK");
                    }
                }
                firebaseNewsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId()).child("likes_count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            final long[] value = {Integer.valueOf(task.getResult().getValue(String.class))};
                            likesCount.setText(String.valueOf(value[0]));
                            Query likeref = DefaultDatabase.getUsersReference().child(nick).child("likedNews").child(newsItem.getNewsId());
                            likeref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        like.setImageResource(R.drawable.ic_pressedheart40dp);
                                    }
                                    else {
                                        like.setImageResource(R.drawable.ic_heart40dp);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d("#####", "Firebase : " + firebaseNewsModel.getReference() + "   Likes before " + value[0]);
                                    Query likeref = DefaultDatabase.getUsersReference().child(nick).child("likedNews").child(newsItem.getNewsId());
                                    likeref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                value[0] -= 1;
                                                like.setImageResource(R.drawable.ic_heart40dp);
                                                DefaultDatabase.getUsersReference().child(nick).child("likedNews").child(newsItem.getNewsId()).removeValue();
                                            }
                                            else{
                                                value[0] += 1;
                                                like.setImageResource(R.drawable.ic_pressedheart40dp);
                                                DefaultDatabase.getUsersReference().child(nick).child("likedNews").child(newsItem.getNewsId()).setValue("liked");
                                            }
                                            Log.d("#####", "Firebase : " + firebaseNewsModel.getReference() + "   Likes " + value[0]);
                                            likesCount.setText(String.valueOf(value[0]));
                                            firebaseNewsModel.getReference().child(newsItem.getNick()).child(newsItem.getNewsId()).child("likes_count").setValue(String.valueOf(value[0]));
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
        });


    }

    private void showBottomSheetDialogComplain(NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_complain);

        complainOtherUserText=bottomSheetDialog.findViewById(R.id.complainOtherUserText);
        complainRecycler=bottomSheetDialog.findViewById(R.id.reasonsRecycler);

        complainOtherUserText.setText(newsItem.getNick());

        itemClickListenerComplain=new ComplainAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Reason reason) {
                showBottomSheetDialogComplainToBase(newsItem,reason);
                bottomSheetDialog.dismiss();
            }
        };
        ArrayList<Reason> arrayListReason=new ArrayList<>();
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.fraud)));
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.violenceordangerousorganizations)));
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.hostilesayingsorsymbols)));
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.saleofillegalgoods)));
        arrayListReason.add(new Reason(getContext().getResources().getString(R.string.violationofintellectualpropertyrights)));
        ComplainAdapter complainAdapter=new ComplainAdapter(arrayListReason,itemClickListenerComplain);
        complainRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        complainRecycler.setAdapter(complainAdapter);

        bottomSheetDialog.show();
    }

    private void showBottomSheetDialogLookOptions(NewsItem newsItem) {
        if(!newsItem.getNick().equals(nick)){
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
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
                                                        Toast.makeText(getContext(), v.getContext().getResources().getText(R.string.lookwasdeletedfromsaved), Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        save.setText(R.string.dontsave);
                                                        Log.d("####", "####11"+newsItem.getNewsId()+"  "+newsItem.getNewsId());
                                                        DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                                                .setValue(newsItem);
                                                        Toast.makeText(getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }

            complain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialogComplain(newsItem);
                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetDialog.show();
        }else{
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
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
                                                        Toast.makeText(getContext(), v.getContext().getResources().getText(R.string.lookwasdeletedfromsaved), Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        save.setText(R.string.dontsave);
                                                        DefaultDatabase.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                                                                .setValue(newsItem);
                                                        Toast.makeText(getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), v.getContext().getResources().getText(R.string.looksaved), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), R.string.lookwasdeleted, Toast.LENGTH_SHORT).show();
                }
            });
            bottomSheetDialog.show();
        }
    }

    private void showBottomSheetDialogComplainToBase(NewsItem newsItem,Reason reason) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_complaintobase);

        reasonText=bottomSheetDialog.findViewById(R.id.reasonText);
        sendReason=bottomSheetDialog.findViewById(R.id.sendReasons);
        addDescriptionEdit=bottomSheetDialog.findViewById(R.id.addDescriptionEdit);

        reasonTextString=reason.getReason();
        reasonText.setText(reasonTextString);
        sendReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.returnNewsItem(newsItem, new Callbacks.loadNewsTread() {
                    @Override
                    public void LoadNews(NewsItem newsItem) throws IOException, URISyntaxException {
                        descriptionText=addDescriptionEdit.getText().toString();
                        String uid=DefaultDatabase.getReference().child("AppData").child("complains").push().getKey();
                        DefaultDatabase.getReference().child("AppData").child("complains").child(uid)
                                .setValue(new Complain(newsItem.getNick(),nick, reasonTextString,descriptionText,uid,newsItem));
                        Toast.makeText(getContext(), R.string.complaintsent, Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

            }
        });


        bottomSheetDialog.show();
    }

    private void showBottomSheetDialogComments(NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_comment);
        FirebaseModel newsModel = new FirebaseModel();
        newsModel.initNewsDatabase();
        editText=bottomSheetDialog.findViewById(R.id.commentEdit);
        recyclerView=bottomSheetDialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyList=bottomSheetDialog.findViewById(R.id.emptyCommentsList);
        comments=bottomSheetDialog.findViewById(R.id.comments);
        comments.setText(R.string.comments);
        noComment=bottomSheetDialog.findViewById(R.id.noComment);
        sendComment=bottomSheetDialog.findViewById(R.id.send);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        loadComments(newsItem);
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

    public void loadComments(NewsItem newsItem){
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(commentAdapter);
    }

    private void showBottomSheetDialogClothesCreators(NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_clothescreators);
        clothesCreatorsRecycler=bottomSheetDialog.findViewById(R.id.recyclerView);
        itemClickListenerClothes=new ConstituentsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                bottomSheetDialog.dismiss();
                RecentMethods.setCurrentFragment(ViewingClothesNews.newInstance(ViewingLookFragment.newInstance(fragment, userInformation,bundle),userInformation,bundle), (Activity) getContext());
            }
        };
        if(newsItem.getClothesCreators()==null){
            RecentMethods.getLookClothes(newsItem.getNick(), newsItem.getNewsId(), firebaseNewsModel, new Callbacks.getLookClothes() {
                @Override
                public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                    ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(clothesArrayList,itemClickListenerClothes);
                    clothesCreatorsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    clothesCreatorsRecycler.setAdapter(constituentsAdapter);
                }
            });
        }else{
            if(newsItem.getClothesCreators().size()==0){
                RecentMethods.getLookClothes(newsItem.getNick(), newsItem.getNewsId(), firebaseNewsModel, new Callbacks.getLookClothes() {
                    @Override
                    public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                        ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(clothesArrayList,itemClickListenerClothes);
                        clothesCreatorsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        clothesCreatorsRecycler.setAdapter(constituentsAdapter);
                    }
                });
            } else {
                ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(newsItem.getClothesCreators(),itemClickListenerClothes);
                clothesCreatorsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                clothesCreatorsRecycler.setAdapter(constituentsAdapter);
            }
        }

        bottomSheetDialog.show();
    }

    private void showBottomSheetDialog(NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

        editText=bottomSheetDialog.findViewById(R.id.searchuser);
        recyclerView=bottomSheetDialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noChats=bottomSheetDialog.findViewById(R.id.noChats);
        linearElse=bottomSheetDialog.findViewById(R.id.linearElse);
        linearTelegram=bottomSheetDialog.findViewById(R.id.linearTelegram);
        linearInstagram=bottomSheetDialog.findViewById(R.id.linearInstagram);
        messageEdit=bottomSheetDialog.findViewById(R.id.message);

        linearElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(ViewingLookFragment.newInstance( fragment,userInformation, bundle), userInformation, bundle, null,"look",newsItem,null,"all"), getActivity());
                bottomSheetDialog.dismiss();
            }
        });

        linearTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(ViewingLookFragment.newInstance( fragment,userInformation, bundle), userInformation, bundle, null,"look",newsItem,null,"telegram"), getActivity());
                bottomSheetDialog.dismiss();
            }
        });
        linearInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(ViewingLookFragment.newInstance(fragment, userInformation, bundle), userInformation, bundle, null,"look",newsItem,null,"instagram"), getActivity());
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
                        RecentMethods.loadChatMembers(userInformation.getNick(), chat.getChatId(), firebaseModel, new Callbacks.GetChatMembers() {
                            @Override
                            public void getChatMembers(ArrayList<UserPeopleAdapter> chatMembers) {
                                chat.setMembers(chatMembers);
                                RecentMethods.setCurrentFragment(GroupChatFragment.newInstance(userInformation, bundle, ViewingLookFragment.newInstance(fragment, userInformation, bundle), chat),getActivity());
                                bottomSheetDialog.dismiss();
                            }
                        });
                    }else{
                        RecentMethods.setCurrentFragment(MessageFragment.newInstance(userInformation, bundle, ViewingLookFragment.newInstance(fragment, userInformation, bundle), chat),getActivity());
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
        addType(type);
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

    public void addType(String type) {
        final long[] value = new long[1];
        DatabaseReference ref = DefaultDatabase.getUsersReference().child(otherUserNickString).child("Chats").child(userInformation.getNick()).child(type);
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

    public void showDialogViewingLook(NewsItem newsItem){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_viewing_look);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        FirebaseModel newsModel = new FirebaseModel();

        CircularProgressIndicator progressIndicator;
        SurfaceView surfaceView;
        progressIndicator=dialog.findViewById(R.id.progressIndicator);
        surfaceView=dialog.findViewById(R.id.surfaceView);

        if(newsItem.getClothesCreators()==null){
            RecentMethods.getLookClothes(newsItem.getNick(), newsItem.getNewsId(), firebaseNewsModel, new Callbacks.getLookClothes() {
                @Override
                public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                    newsItem.setClothesCreators(clothesArrayList);
                    loadBuffers( newsItem, progressIndicator, surfaceView);
                }
            });
        }else{
            if(newsItem.getClothesCreators().size()==0){
                RecentMethods.getLookClothes(newsItem.getNick(), newsItem.getNewsId(), firebaseNewsModel, new Callbacks.getLookClothes() {
                    @Override
                    public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                        newsItem.setClothesCreators(clothesArrayList);
                        loadBuffers( newsItem, progressIndicator, surfaceView);
                    }
                });
            } else {
                loadBuffers( newsItem, progressIndicator, surfaceView);
            }
        }

        dialog.show();
    }

    public void loadBuffers(NewsItem newsItem,CircularProgressIndicator progressIndicator,SurfaceView surfaceView){
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
