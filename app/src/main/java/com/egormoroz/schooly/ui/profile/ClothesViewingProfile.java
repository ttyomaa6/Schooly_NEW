package com.egormoroz.schooly.ui.profile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.InstagramShareFragment;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.chat.Chat;
import com.egormoroz.schooly.ui.chat.GroupChatFragment;
import com.egormoroz.schooly.ui.chat.MessageFragment;
import com.egormoroz.schooly.ui.main.MyClothes.PresentClothesFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.NewsFragment;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.egormoroz.schooly.ui.profile.Wardrobe.ViewingClothesWardrobe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ClothesViewingProfile extends Fragment {

    Fragment fragment;
    String type,nick;
    UserInformation userInformation;
    Bundle bundle;


    public ClothesViewingProfile(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment = fragment;
        this.type = type;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static ClothesViewingProfile newInstance(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new ClothesViewingProfile(type,fragment,userInformation,bundle);

    }


    TextView clothesTitleCV, description, noDescription,purchaseToday,purchaseAll,profitToday,profitAll
            ,perSentToday,perSentAll,clothesPrice;
    ImageView clothesImageCV, backToShop, coinsImage,coinsImageAll,coinsImagePurple,send;
    RelativeLayout presentClothes,resaleClothes;
    RecyclerView recyclerView;
    LinearLayout linearElse,linearTelegram,linearInstagram;
    TextView emptyList;
    EditText editText,messageEdit;
    String otherUserNickString;
    Clothes clothesViewing;
    private FirebaseModel firebaseModel = new FirebaseModel();
    double perCent;
    String clothesPriceString,purchaseTodayString,profitTodayString,profitAllString;
    ArrayList<Chat> searchDialogsArrayList;
    SendLookAdapter.ItemClickListener itemClickListener;
    String getEditText;
    TextView noChats;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_viewingmyclothes, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;

    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        clothesImageCV = view.findViewById(R.id.clothesImagecv);
        coinsImage = view.findViewById(R.id.coinsImage);
        coinsImageAll = view.findViewById(R.id.coinsImageAll);
        noDescription = view.findViewById(R.id.noDescription);
        clothesTitleCV = view.findViewById(R.id.clothesTitlecv);
        description = view.findViewById(R.id.description);
        backToShop = view.findViewById(R.id.back_toshop);
        purchaseToday=view.findViewById(R.id.purchasesToday);
        coinsImagePurple=view.findViewById(R.id.coinImagePrice);
        clothesPrice=view.findViewById(R.id.clothesPricecv);
        resaleClothes=view.findViewById(R.id.resaleClothes);
        resaleClothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        send=view.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
        purchaseAll=view.findViewById(R.id.purchasesAll);
        profitToday=view.findViewById(R.id.profit);
        profitAll=view.findViewById(R.id.profitAll);
        presentClothes=view.findViewById(R.id.presentClothes);
        perSentToday=view.findViewById(R.id.perSentPurchase);
        perSentAll=view.findViewById(R.id.perSentPurchaseAll);
        backToShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        ClothesAdapter.singeClothesInfo(new ClothesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                clothesViewing = clothes;
                presentClothes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.setCurrentFragment(PresentClothesFragment.newInstance(clothesViewing,ClothesViewingProfile.newInstance(type,fragment,userInformation,bundle),userInformation,bundle), getActivity());
                    }
                });
                clothesTitleCV.setText(clothes.getClothesTitle());
                purchaseTodayString=String.valueOf(clothesViewing.getPurchaseToday());
                if(clothes.getPurchaseToday()<1000){
                    purchaseToday.setText(String.valueOf(clothes.getPurchaseToday()));
                }else if(clothes.getPurchaseToday()>1000 && clothes.getPurchaseToday()<10000){
                    purchaseToday.setText(purchaseTodayString.substring(0, 1)+"."+purchaseTodayString.substring(1, 2)+"K");
                }
                else if(clothes.getPurchaseToday()>10000 && clothes.getPurchaseToday()<100000){
                    purchaseToday.setText(purchaseTodayString.substring(0, 2)+"."+purchaseTodayString.substring(2,3)+"K");
                }
                else if(clothes.getPurchaseToday()>10000 && clothes.getPurchaseToday()<100000){
                    purchaseToday.setText(purchaseTodayString.substring(0, 2)+"."+purchaseTodayString.substring(2,3)+"K");
                }else if(clothes.getPurchaseToday()>100000 && clothes.getPurchaseToday()<1000000){
                    purchaseToday.setText(purchaseTodayString.substring(0, 3)+"K");
                }
                else if(clothes.getPurchaseToday()>1000000 && clothes.getPurchaseToday()<10000000){
                    purchaseToday.setText(purchaseTodayString.substring(0, 1)+"KK");
                }
                else if(clothes.getPurchaseToday()>10000000 && clothes.getPurchaseToday()<100000000){
                    purchaseToday.setText(purchaseTodayString.substring(0, 2)+"KK");
                }
                clothesPriceString=String.valueOf(clothes.getPurchaseNumber());
                if(clothes.getPurchaseNumber()<1000){
                    purchaseAll.setText(String.valueOf(clothes.getPurchaseNumber()));
                }else if(clothes.getPurchaseNumber()>1000 && clothes.getPurchaseNumber()<10000){
                    purchaseAll.setText(clothesPriceString.substring(0, 1)+"."+clothesPriceString.substring(1, 2)+"K");
                }
                else if(clothes.getPurchaseNumber()>10000 && clothes.getPurchaseNumber()<100000){
                    purchaseAll.setText(clothesPriceString.substring(0, 2)+"."+clothesPriceString.substring(2,3)+"K");
                }
                else if(clothes.getPurchaseNumber()>10000 && clothes.getPurchaseNumber()<100000){
                    purchaseAll.setText(clothesPriceString.substring(0, 2)+"."+clothesPriceString.substring(2,3)+"K");
                }else if(clothes.getPurchaseNumber()>100000 && clothes.getPurchaseNumber()<1000000){
                    purchaseAll.setText(clothesPriceString.substring(0, 3)+"K");
                }
                else if(clothes.getPurchaseNumber()>1000000 && clothes.getPurchaseNumber()<10000000){
                    purchaseAll.setText(clothesPriceString.substring(0, 1)+"KK");
                }
                else if(clothes.getPurchaseNumber()>10000000 && clothes.getPurchaseNumber()<100000000){
                    purchaseAll.setText(clothesPriceString.substring(0, 2)+"KK");
                }
                profitTodayString=String.valueOf(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday());
                profitAllString=String.valueOf(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice());
                if (clothesViewing.getCurrencyType().equals("dollar")){
                    clothesPrice.setText("$"+String.valueOf(clothes.getClothesPrice()));
                    coinsImagePurple.setVisibility(View.GONE);
                    coinsImage.setVisibility(View.GONE);
                    coinsImageAll.setVisibility(View.GONE);
                    ////
                    if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<1000){
                        profitToday.setText("+"+profitTodayString+"$");
                    }else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>1000
                            && clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<10000){
                        profitToday.setText("+"+profitTodayString.substring(0, 1)+"."+profitTodayString.substring(1, 2)+"K"+"$");
                    }
                    else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>10000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<100000){
                        profitToday.setText("+"+profitTodayString.substring(0, 2)+"."+profitTodayString.substring(2,3)+"K"+"$");
                    }
                    else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>10000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<100000){
                        profitToday.setText("+"+profitTodayString.substring(0, 2)+"."+profitTodayString.substring(2,3)+"K"+"$");
                    }else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>100000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<1000000){
                        profitToday.setText("+"+profitTodayString.substring(0, 3)+"K"+"$");
                    }
                    else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>1000000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<10000000){
                        profitToday.setText("+"+profitTodayString.substring(0, 1)+"KK"+"$");
                    }
                    else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>10000000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<100000000){
                        profitToday.setText("+"+profitTodayString.substring(0, 2)+"KK"+"$");
                    }
                    ////////
                    if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<1000){
                        profitAll.setText("+"+profitAllString+"$");
                    }else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>1000
                            && clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<10000){
                        profitAll.setText("+"+profitAllString.substring(0, 1)+"."+profitAllString.substring(1, 2)+"K"+"$");
                    }
                    else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>10000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<100000){
                        profitAll.setText("+"+profitAllString.substring(0, 2)+"."+profitAllString.substring(2,3)+"K"+"$");
                    }
                    else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>10000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<100000){
                        profitAll.setText("+"+profitAllString.substring(0, 2)+"."+profitAllString.substring(2,3)+"K"+"$");
                    }else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>100000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<1000000){
                        profitAll.setText("+"+profitAllString.substring(0, 3)+"K"+"$");
                    }
                    else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>1000000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<10000000){
                        profitAll.setText("+"+profitAllString.substring(0, 1)+"KK"+"$");
                    }
                    else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>10000000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<100000000){
                        profitAll.setText("+"+profitAllString.substring(0, 2)+"KK"+"$");
                    }
                }else {
                    clothesPrice.setText(String.valueOf(clothes.getClothesPrice()));
                    if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<1000){
                        profitToday.setText("+"+profitTodayString);
                    }else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>1000
                            && clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<10000){
                        profitToday.setText("+"+profitTodayString.substring(0, 1)+"."+profitTodayString.substring(1, 2)+"K");
                    }
                    else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>10000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<100000){
                        profitToday.setText("+"+profitTodayString.substring(0, 2)+"."+profitTodayString.substring(2,3)+"K");
                    }
                    else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>10000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<100000){
                        profitToday.setText("+"+profitTodayString.substring(0, 2)+"."+profitTodayString.substring(2,3)+"K");
                    }else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>100000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<1000000){
                        profitToday.setText("+"+profitTodayString.substring(0, 3)+"K");
                    }
                    else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>1000000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<10000000){
                        profitToday.setText("+"+profitTodayString.substring(0, 1)+"KK");
                    }
                    else if(clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()>10000000 &&
                            clothesViewing.getClothesPrice()*clothesViewing.getPurchaseToday()<100000000){
                        profitToday.setText("+"+profitTodayString.substring(0, 2)+"KK");
                    }
                    ////////
                    if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<1000){
                        profitAll.setText("+"+profitAllString);
                    }else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>1000
                            && clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<10000){
                        profitAll.setText("+"+profitAllString.substring(0, 1)+"."+profitAllString.substring(1, 2)+"K");
                    }
                    else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>10000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<100000){
                        profitAll.setText("+"+profitAllString.substring(0, 2)+"."+profitAllString.substring(2,3)+"K");
                    }
                    else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>10000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<100000){
                        profitAll.setText("+"+profitAllString.substring(0, 2)+"."+profitAllString.substring(2,3)+"K");
                    }else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>100000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<1000000){
                        profitAll.setText("+"+profitAllString.substring(0, 3)+"K");
                    }
                    else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>1000000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<10000000){
                        profitAll.setText("+"+profitAllString.substring(0, 1)+"KK");
                    }
                    else if(clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()>10000000 &&
                            clothesViewing.getPurchaseNumber()*clothesViewing.getClothesPrice()<100000000){
                        profitAll.setText("+"+profitAllString.substring(0, 2)+"KK");
                    }
                }
                if (clothesViewing.getPurchaseNumber()==0){
                    perCent=0;
                }else {
                    perCent=clothesViewing.getPurchaseToday()*100/clothesViewing.getPurchaseNumber();
                }
                perSentToday.setText("("+String.valueOf(perCent)+"%)");
                Picasso.get().load(clothesViewing.getClothesImage()).into(clothesImageCV);
                if (clothesViewing.getDescription().trim().length() == 0) {
                    noDescription.setVisibility(View.VISIBLE);
                    description.setVisibility(View.GONE);
                } else {
                    description.setText(clothesViewing.getDescription());
                }
            }
        });
    }

    private void showBottomSheetDialog() {

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
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(ClothesViewingProfile.newInstance(type,fragment, userInformation, bundle), userInformation, bundle, clothesViewing,"clothes",null,null,"all"), getActivity());
                bottomSheetDialog.dismiss();
            }
        });

        linearTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(ClothesViewingProfile.newInstance(type,fragment, userInformation, bundle), userInformation, bundle, clothesViewing,"clothes",null,null,"telegram"), getActivity());
                bottomSheetDialog.dismiss();
            }
        });
        linearInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(ClothesViewingProfile.newInstance(type,fragment, userInformation, bundle), userInformation, bundle, clothesViewing,"clothes",null,null,"instagram"), getActivity());
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



                        RecentMethods.loadChatMembers(userInformation.getNick(), chat.getChatId(), firebaseModel, new Callbacks.GetChatMembers() {
                            @Override
                            public void getChatMembers(ArrayList<UserPeopleAdapter> chatMembers) {
                                for(int i=0;i<chatMembers.size();i++){
                                    String nick=chatMembers.get(i).getNick();
                                    addLastMessageGroup("clothes", messageText,nick,chat);
                                    addUnreadGroup(nick,chat);
                                }
                            }
                        });

                        Clothes clothes=clothesViewing;
                        clothes.setBuffer(null);
                        DatabaseReference userMessageKeyRef = firebaseModel.getReference().child("groups").child(chat.getChatId()).child("Messages").push();
                        String messagePushID = userMessageKeyRef.getKey();
                        Map<String, Object> messageTextBody = new HashMap<>();
                        messageTextBody.put("message", messageText);
                        messageTextBody.put("type", "clothes");
                        messageTextBody.put("from", userInformation.getNick() );
                        messageTextBody.put("to", chat.getName());
                        messageTextBody.put("time", RecentMethods.getCurrentTime());
                        messageTextBody.put("messageID", messagePushID);
                        messageTextBody.put("clothes", clothes);
                        Map<String, Object> messageBodyDetails = new HashMap<>();
                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);

                        firebaseModel.getReference().child("groups").updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                messageEdit.setText("");
                            }
                        });
                    }else{

                        String messageSenderRef = chat.getName() + "/Chats/" + userInformation.getNick() + "/Messages";
                        String messageReceiverRef = userInformation.getNick()  + "/Chats/" + chat.getName()+ "/Messages";
                        otherUserNickString=chat.getName();

                        DatabaseReference userMessageKeyRef = firebaseModel.getUsersReference().child(userInformation.getNick() ).child("Chats").child(chat.getName()).child("Messages").push();
                        String messagePushID = userMessageKeyRef.getKey();

                        addLastMessage("clothes", messageText);
                        addUnread();

                        Clothes clothes=clothesViewing;
                        clothes.setBuffer(null);
                        Map<String, Object> messageTextBody = new HashMap<>();
                        messageTextBody.put("message", messageText);
                        messageTextBody.put("type", "clothes");
                        messageTextBody.put("from", userInformation.getNick() );
                        messageTextBody.put("to", chat.getName());
                        messageTextBody.put("time", RecentMethods.getCurrentTime());
                        messageTextBody.put("messageID", messagePushID);
                        messageTextBody.put("clothes", clothes);

                        Map<String, Object> messageBodyDetails = new HashMap<String, Object>();
                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                        messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
                        firebaseModel.getUsersReference().updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                messageEdit.setText("");
                            }
                        });
                    }
                }else {
                    if(chat.getType().equals("talk")){
                        RecentMethods.loadChatMembers(userInformation.getNick(), chat.getChatId(), firebaseModel, new Callbacks.GetChatMembers() {
                            @Override
                            public void getChatMembers(ArrayList<UserPeopleAdapter> chatMembers) {
                                chat.setMembers(chatMembers);
                                RecentMethods.setCurrentFragment(GroupChatFragment.newInstance(userInformation, bundle, ClothesViewingProfile.newInstance(type,fragment, userInformation, bundle), chat),getActivity());
                                bottomSheetDialog.dismiss();
                            }
                        });
                    }else{
                        RecentMethods.setCurrentFragment(MessageFragment.newInstance(userInformation, bundle, ClothesViewingProfile.newInstance(type,fragment, userInformation, bundle), chat),getActivity());
                        bottomSheetDialog.dismiss();
                    }
                }
            }
        };
        if(userInformation.getChats()==null || userInformation.getTalksArrayList()==null){
            RecentMethods.getDialogs(userInformation.getNick(), firebaseModel, new Callbacks.loadDialogs() {
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
        firebaseModel.getUsersReference().child(name).child("Dialogs").child(chat.getChatId()).child("lastMessage").setValue("Одежда");

        Calendar calendar = Calendar.getInstance();
        firebaseModel.getUsersReference().child(name).child("Dialogs").child(chat.getChatId()).child("lastTime").setValue(RecentMethods.getCurrentTime());
        Map<String,String> map=new HashMap<>();
        map= ServerValue.TIMESTAMP;
        firebaseModel.getUsersReference().child(name).child("Dialogs").child(chat.getChatId()).child("timeMill").setValue(map);
    }

    public void addUnreadGroup(String name,Chat chat) {
        final long[] value = new long[1];
        DatabaseReference ref = firebaseModel.getUsersReference().child(name).child("Dialogs").child(chat.getChatId()).child("unreadMessages");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value[0] = (long) dataSnapshot.getValue();
                    value[0] = value[0] + 1;
                    dataSnapshot.getRef().setValue(value[0]);
                    firebaseModel.getUsersReference().child(name).child("Dialogs")
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
            firebaseModel.getUsersReference().child(userInformation.getNick()).child("Chats").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("Dialogs").child(otherUserNickString).child("lastMessage").setValue("Одежда");
        firebaseModel.getUsersReference().child(otherUserNickString).child("Dialogs").child(userInformation.getNick()).child("lastMessage").setValue("Одежда");
        Calendar calendar = Calendar.getInstance();
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("Dialogs").child(otherUserNickString).child("lastTime").setValue(RecentMethods.getCurrentTime());
        firebaseModel.getUsersReference().child(otherUserNickString).child("Dialogs").child(userInformation.getNick()).child("lastTime").setValue(RecentMethods.getCurrentTime());
        Map<String,String> map=new HashMap<>();
        map= ServerValue.TIMESTAMP;
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("Dialogs").child(otherUserNickString).child("timeMill").setValue(map);
        firebaseModel.getUsersReference().child(otherUserNickString).child("Dialogs").child(userInformation.getNick()).child("timeMill").setValue(map);
    }

    public void addUnread() {
        final long[] value = new long[1];
        DatabaseReference ref = firebaseModel.getUsersReference().child(otherUserNickString).child("Dialogs").child(userInformation.getNick()).child("unreadMessages");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value[0] = (long) dataSnapshot.getValue();
                    value[0] = value[0] + 1;
                    dataSnapshot.getRef().setValue(value[0]);
                    firebaseModel.getUsersReference().child(userInformation.getNick()).child("Dialogs")
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
        DatabaseReference ref = firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(userInformation.getNick()).child(type);
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

    public void showDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_layout_sell_clothes);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout sellRelative=dialog.findViewById(R.id.sellRelative);


        sellRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}