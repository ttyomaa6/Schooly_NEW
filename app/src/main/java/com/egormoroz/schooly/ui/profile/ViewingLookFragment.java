package com.egormoroz.schooly.ui.profile;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.MyClothes.CreateClothesFragment;
import com.egormoroz.schooly.ui.main.MyClothes.CriteriaFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.Comment;
import com.egormoroz.schooly.ui.news.CommentAdapter;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.news.ViewingClothesNews;
import com.egormoroz.schooly.ui.people.PeopleFragment;
import com.egormoroz.schooly.ui.profile.Wardrobe.AcceptNewLook;
import com.egormoroz.schooly.ui.profile.Wardrobe.ConstituentsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class ViewingLookFragment extends Fragment {

    FirebaseModel firebaseModel=new FirebaseModel();
    ImageView back,like,comment,send,schoolyCoin,cross,options;
    TextView nickView,description,likesCount,lookPrice,lookPriceDollar,clothesCreator
            ,emptyList,comments,sendComment,noComment,save,complain,complainOtherUserText
            ,reasonText;
    SceneView sceneView;
    LinearLayout linearElse,linearTelegram,linearInstagram;
    EditText editText,messageEdit,addDescriptionEdit;
    RecyclerView commentsRecycler,clothesCreatorsRecycler,complainRecycler;
    RecyclerView recyclerView;
    String likesCountString,lookPriceString,lookPriceDollarString,reasonTextString,descriptionText
    ,userName,otherUserNickString,editGetText,nick;
    SendLookAdapter.ItemClickListener itemClickListener;
    ConstituentsAdapter.ItemClickListener itemClickListenerClothes;
    ComplainAdapter.ItemClickListener itemClickListenerComplain;
    RelativeLayout sendReason;
    UserInformation userInformation;
    Fragment fragment;

    public ViewingLookFragment(Fragment fragment,UserInformation userInformation) {
        this.fragment = fragment;
        this.userInformation=userInformation;
    }

    public static ViewingLookFragment newInstance(Fragment fragment,UserInformation userInformation) {
        return new ViewingLookFragment(fragment,userInformation);

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
        sceneView=view.findViewById(R.id.sceneView);
        lookPrice=view.findViewById(R.id.lookPrice);
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

        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        LooksAdapter.lookInfo(new LooksAdapter.ItemClickListener() {
            @Override
            public void onItemClick(NewsItem newsItem) {
                loadModels(Uri.parse(newsItem.getImageUrl()), sceneView, ViewingLookFragment.this, 0.25f);
                nickView.setText(newsItem.getNick());
                likesCountString=String.valueOf(newsItem.getLikes_count());
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
                else if(likesCountLong>10000000 && likesCountLong<100000000){
                    likesCount.setText(likesCountString.substring(0, 2)+"KK");
                }
                description.setText(newsItem.getItem_description());
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
            }
        });

    }

    private void showBottomSheetDialogComments(NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_comment);

        editText=bottomSheetDialog.findViewById(R.id.commentEdit);
        recyclerView=bottomSheetDialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyList=bottomSheetDialog.findViewById(R.id.emptyCommentsList);
        comments=bottomSheetDialog.findViewById(R.id.comments);
        comments.setText("Комментарии:");
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
                            String commentId=firebaseModel.getUsersReference().child(newsItem.getNick()).child("looks")
                                    .child(newsItem.getNewsId()).child("comments").push().getKey();
                            firebaseModel.getUsersReference().child(newsItem.getNick()).child("looks")
                                    .child(newsItem.getNewsId()).child("comments").child(commentId)
                                    .setValue(new Comment(editText.getText().toString(), 0, commentId,"0",nick,"image","comment"));
                            editText.getText().clear();
                            loadComments(newsItem);
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
        RecentMethods.getComplainReasonList(firebaseModel, new Callbacks.getComplainReasonsList() {
            @Override
            public void getComplainReasonsList(ArrayList<Reason> reason) {
                ComplainAdapter complainAdapter=new ComplainAdapter(reason,itemClickListenerComplain);
                complainRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                complainRecycler.setAdapter(complainAdapter);
            }
        });

        bottomSheetDialog.show();
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
                descriptionText=addDescriptionEdit.getText().toString();
                String uid=firebaseModel.getReference().child("complains").push().getKey();
                firebaseModel.getReference().child("complains").child(uid)
                        .setValue(new Complain(nick,newsItem.getNick(), reasonTextString,descriptionText));
                Toast.makeText(getContext(), "Жалоба отправлена", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.show();
    }

    public void loadComments(NewsItem newsItem){
        RecentMethods.getCommentsList(newsItem.getNick(), newsItem.getNewsId(), firebaseModel, new Callbacks.getCommentsList() {
            @Override
            public void getCommentsList(ArrayList<Comment> comment) {
                if(comment.size()==0){
                    noComment.setVisibility(View.VISIBLE);
                }else {
                    noComment.setVisibility(View.GONE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    CommentAdapter commentAdapter = new CommentAdapter(comment);
                    recyclerView.setAdapter(commentAdapter);
                }
            }
        });
    }

    private void showBottomSheetDialogClothesCreators(NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_clothescreators);
        itemClickListenerClothes=new ConstituentsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                bottomSheetDialog.dismiss();
                RecentMethods.setCurrentFragment(ViewingClothesNews.newInstance(ViewingLookFragment.newInstance(fragment, userInformation),userInformation), getActivity());
            }
        };
        clothesCreatorsRecycler=bottomSheetDialog.findViewById(R.id.recyclerView);
        RecentMethods.getLookClothes(newsItem.getNick(), newsItem.getNewsId(), firebaseModel, new Callbacks.getLookClothes() {
            @Override
            public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
                ConstituentsAdapter constituentsAdapter=new ConstituentsAdapter(clothesArrayList,itemClickListenerClothes);
                clothesCreatorsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                clothesCreatorsRecycler.setAdapter(constituentsAdapter);
            }
        });

        bottomSheetDialog.show();
    }

    private void showBottomSheetDialogLookOptions(NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_lookoptions);

        save=bottomSheetDialog.findViewById(R.id.save);
        complain=bottomSheetDialog.findViewById(R.id.complain);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseModel.getUsersReference().child(nick).child("saved").child(newsItem.getNewsId())
                        .setValue(newsItem);
                Toast.makeText(getContext(), "Образ сохранен", Toast.LENGTH_SHORT).show();
            }
        });

        complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogComplain(newsItem);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void showBottomSheetDialog(NewsItem newsItem) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

        editText=bottomSheetDialog.findViewById(R.id.searchuser);
        recyclerView=bottomSheetDialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyList=bottomSheetDialog.findViewById(R.id.emptySubscribersList);
        linearElse=bottomSheetDialog.findViewById(R.id.linearElse);
        linearTelegram=bottomSheetDialog.findViewById(R.id.linearTelegram);
        linearInstagram=bottomSheetDialog.findViewById(R.id.linearInstagram);
        messageEdit=bottomSheetDialog.findViewById(R.id.message);

        linearElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        linearTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        linearInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        itemClickListener=new SendLookAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String otherUserNick, String type) {
                if(type.equals("send")){
                    String messageText = messageEdit.getText().toString();

                    String messageSenderRef = otherUserNick + "/Chats/" + nick + "/Messages";
                    String messageReceiverRef = nick + "/Chats/" + otherUserNick+ "/Messages";
                    otherUserNickString=otherUserNick;

                    DatabaseReference userMessageKeyRef = firebaseModel.getUsersReference().child(nick).child("Chats").child(otherUserNick).child("Messages").push();
                    String messagePushID = userMessageKeyRef.getKey();

                    Map<String, String> messageTextBody = new HashMap<>();
                    messageTextBody.put("message", messageText);
                    messageTextBody.put("type", "text");
                    messageTextBody.put("from", nick);
                    messageTextBody.put("to", otherUserNick);
                    messageTextBody.put("time", RecentMethods.getCurrentTime());
                    messageTextBody.put("messageID", messagePushID);
                    addLastMessage("text", messageText);

                    Map<String, Object> messageBodyDetails = new HashMap<String, Object>();
                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                    messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
                }else {
                    Log.d("####", type);
                }
            }
        };

        RecentMethods.getSubscriptionList(nick, firebaseModel, new Callbacks.getFriendsList() {
            @Override
            public void getFriendsList(ArrayList<Subscriber> friends) {
                if (friends.size()==0){
                    emptyList.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    SendLookAdapter sendLookAdapter = new SendLookAdapter(friends,itemClickListener);
                    recyclerView.setAdapter(sendLookAdapter);
                }
            }
        });

        initUserEnter();

        bottomSheetDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadModels(Uri url, SceneView sceneView, Fragment fragment, float scale) {
        ModelRenderable.builder()
                .setSource(
                        fragment.getContext(), new RenderableSource.Builder().setSource(
                                fragment.getContext(),
                                url,
                                RenderableSource.SourceType.GLB
                        ).setScale(scale)
                                .setRecenterMode(RenderableSource.RecenterMode.CENTER)
                                .build()
                )
                .setRegistryId(url)
                .build()
                .thenAccept(new Consumer<ModelRenderable>() {
                    @Override
                    public void accept(ModelRenderable modelRenderable) {
                        addNode(modelRenderable, sceneView);
                    }
                });
    }

    public void addNode(ModelRenderable modelRenderable, SceneView sceneView) {
        Node modelNode1 = new Node();
        modelNode1.setRenderable(modelRenderable);
        modelNode1.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
//        modelNode1.setLocalRotation(Quaternion.multiply(
//                Quaternion.axisAngle(new Vector3(1f, 0f, 0f), 45),
//                Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 75)));
        modelNode1.setLocalPosition(new Vector3(0f, 0f, -0.9f));
        sceneView.getScene().addChild(modelNode1);
        try {
            sceneView.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }
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
                firebaseModel.getUsersReference().child(nick).child("subscription")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot= task.getResult();
                            ArrayList<Subscriber> userFromBase = new ArrayList<>();
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
                            SendLookAdapter sendLookAdapter = new SendLookAdapter(userFromBase,itemClickListener);
                            recyclerView.setAdapter(sendLookAdapter);
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void addLastMessage(String type, String Message){
        switch (type) {
            case "text":
                addType("text");
                firebaseModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("LastMessage").setValue(Message);
                firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("LastMessage").setValue(Message);
                break;
            case "voice":
                addType("voice");
                firebaseModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("LastMessage").setValue("Голосовое сообщение");
                firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("LastMessage").setValue("Голосовое сообщение");
                break;
            case "image":
                firebaseModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("LastMessage").setValue("Фотография");
                firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("LastMessage").setValue("Фотография");
                addType("image");
                break;
        }
        Calendar calendar = Calendar.getInstance();
        firebaseModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("LastTime").setValue(RecentMethods.getCurrentTime());
        firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("LastTime").setValue(RecentMethods.getCurrentTime());
        firebaseModel.getUsersReference().child(nick).child("Chats").child(otherUserNickString).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
        firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
    }

    public void addType(String type) {
        final long[] value = new long[1];
        DatabaseReference ref = firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(nick).child(type);
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

}
