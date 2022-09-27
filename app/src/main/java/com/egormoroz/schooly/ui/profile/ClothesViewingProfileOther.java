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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.InstagramShareFragment;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.chat.Chat;
import com.egormoroz.schooly.ui.chat.GroupChatFragment;
import com.egormoroz.schooly.ui.chat.MessageFragment;
import com.egormoroz.schooly.ui.coins.CoinsFragmentSecond;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.FittingFragment;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothesPopular;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClothesViewingProfileOther extends Fragment {

    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;

    public ClothesViewingProfileOther(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment = fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static ClothesViewingProfileOther newInstance(Fragment fragment, UserInformation userInformation,Bundle bundle) {
        return new ClothesViewingProfileOther(fragment,userInformation,bundle);

    }

    TextView clothesPriceCV,clothesTitleCV,schoolyCoinCV,buyClothesBottom
            ,purchaseNumber,creator,description,noDescription,fittingClothes;
    ImageView clothesImageCV,backToShop,coinsImage,dollarImage,inBasket,notInBasket,send;
    long schoolyCoins,clothesPrise;
    RelativeLayout checkBasket;
    LinearLayout linearElse,linearTelegram,linearInstagram;
    int a=0;
    RecyclerView recyclerView;
    TextView emptyList;
    EditText editText,messageEdit;
    String otherUserNickString,nick;
    Clothes clothesViewing;
    private FirebaseModel firebaseModel = new FirebaseModel();
    LinearLayout coinsLinear;
    String clothesPriceString;
    ArrayList<Chat> allChats=new ArrayList<>();
    ArrayList<Chat> searchDialogsArrayList;
    SendLookAdapter.ItemClickListener itemClickListener;
    String getEditText;
    TextView noChats;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_viewingclothes, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;

    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        schoolyCoinCV=view.findViewById(R.id.schoolycoincvfrag);
        clothesImageCV=view.findViewById(R.id.clothesImagecv);
        inBasket=view.findViewById(R.id.inBasketClothes);
        notInBasket=view.findViewById(R.id.notInBasketClothes);
        coinsImage=view.findViewById(R.id.coinsImage);
        noDescription=view.findViewById(R.id.noDescription);
        dollarImage=view.findViewById(R.id.dollarImage);
        clothesTitleCV=view.findViewById(R.id.clothesTitlecv);
        description=view.findViewById(R.id.description);
        creator=view.findViewById(R.id.creator);
        checkBasket=view.findViewById(R.id.checkBasket);
        clothesPriceCV=view.findViewById(R.id.clothesPricecv);
        backToShop=view.findViewById(R.id.back_toshop);
        buyClothesBottom=view.findViewById(R.id.buyClothesBottom);
        purchaseNumber=view.findViewById(R.id.purchaseNumberViewing);
        coinsLinear=view.findViewById(R.id.linearCoins);
        fittingClothes=view.findViewById(R.id.fittingClothes);
        send=view.findViewById(R.id.send);
        schoolyCoinCV.setText(String.valueOf(userInformation.getmoney()));
        schoolyCoins=userInformation.getmoney();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
        fittingClothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(FittingFragment.newInstance(ClothesViewingProfileOther.newInstance(fragment,userInformation,bundle),userInformation,bundle,clothesViewing), getActivity());
            }
        });
        coinsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(CoinsFragmentSecond.newInstance(ClothesViewingProfileOther.newInstance(fragment,userInformation,bundle),userInformation,bundle), getActivity());
            }
        });
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

        ClothesAdapterOther.singeClothesInfo(new ClothesAdapterOther.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                clothesViewing=clothes;
                clothesPriceCV.setText(String.valueOf(clothes.getClothesPrice()));
                clothesTitleCV.setText(clothes.getClothesTitle());
                clothesPrise=clothes.getClothesPrice();
                creator.setText(clothesViewing.getCreator());
                creator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseModel.getUsersReference().child(clothesViewing.getCreator()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()){
                                    Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                }else {
                                    if (clothesViewing.getCreator().equals(nick)) {
                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("user", nick, ClothesViewingProfileOther.newInstance(fragment,userInformation,bundle),userInformation,bundle), getActivity());
                                    }else {
                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", clothesViewing.getCreator(), ClothesViewingProfileOther.newInstance(fragment,userInformation,bundle),userInformation,bundle), getActivity());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                if (clothesViewing.getDescription().trim().length()==0){
                    noDescription.setVisibility(View.VISIBLE);
                    description.setVisibility(View.GONE);
                }else {
                    description.setText(clothesViewing.getDescription());
                }
                clothesPriceString=String.valueOf(clothes.getPurchaseNumber());
                if(clothes.getPurchaseNumber()<1000){
                    purchaseNumber.setText(String.valueOf(clothes.getPurchaseNumber()));
                }else if(clothes.getPurchaseNumber()>1000 && clothes.getPurchaseNumber()<10000){
                    purchaseNumber.setText(clothesPriceString.substring(0, 1)+"."+clothesPriceString.substring(1, 2)+"K");
                }
                else if(clothes.getPurchaseNumber()>10000 && clothes.getPurchaseNumber()<100000){
                    purchaseNumber.setText(clothesPriceString.substring(0, 2)+"."+clothesPriceString.substring(2,3)+"K");
                }
                else if(clothes.getPurchaseNumber()>10000 && clothes.getPurchaseNumber()<100000){
                    purchaseNumber.setText(clothesPriceString.substring(0, 2)+"."+clothesPriceString.substring(2,3)+"K");
                }else if(clothes.getPurchaseNumber()>100000 && clothes.getPurchaseNumber()<1000000){
                    purchaseNumber.setText(clothesPriceString.substring(0, 3)+"K");
                }
                else if(clothes.getPurchaseNumber()>1000000 && clothes.getPurchaseNumber()<10000000){
                    purchaseNumber.setText(clothesPriceString.substring(0, 1)+"KK");
                }
                else if(clothes.getPurchaseNumber()>10000000 && clothes.getPurchaseNumber()<100000000){
                    purchaseNumber.setText(clothesPriceString.substring(0, 2)+"KK");
                }
                Picasso.get().load(clothes.getClothesImage()).into(clothesImageCV);
                if (clothesViewing.getCurrencyType().equals("dollar")){
                    dollarImage.setVisibility(View.VISIBLE);
                    clothesPriceCV.setText("$"+String.valueOf(clothes.getClothesPrice()));
                    coinsImage.setVisibility(View.GONE);
                }
            }
        });
        checkClothes();
        if (a==2 || a==0){
            checkIfBuy();
        }
        buyClothes();
        putInBasket();
        if (a!=3 && a!=0){
            checkClothes();
        }
        checkClothesOnBuy();
    }

    public void checkIfBuy(){
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("clothes")
                .child(clothesViewing.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    a=3;
                }else {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void buyClothes(){
        buyClothesBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a==3){
                    showDialogAlreadyBuy(getContext().getResources().getText(R.string.itempurchased).toString());
                }else {
                    showDialog();
                }
            }
        });
    }

    public void putInBasket(){
        checkBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseModel.getUsersReference().child(nick).child("clothes")
                        .child(clothesViewing.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot= task.getResult();
                            if(snapshot.exists()){
                                a=3;
                                showDialogBasket(getContext().getResources().getText(R.string.itemalreadypurchased).toString());
                            }else {}
                            if(a!=0 && a!=3){
                                if(a==1){
                                    firebaseModel.getUsersReference().child(userInformation.getNick()).child("basket")
                                            .child(clothesViewing.getUid()).removeValue();
                                }else if (a==2){
                                    Clothes clothes=clothesViewing;
                                    clothes.setBuffer(null);
                                    firebaseModel.getUsersReference().child(userInformation.getNick()).child("basket")
                                            .child(clothesViewing.getUid()).setValue(clothes);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public void checkClothes(){
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("basket").
                child(String.valueOf(clothesViewing.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    a=1;
                    inBasket.setVisibility(View.VISIBLE);
                    notInBasket.setVisibility(View.GONE);
                }else {
                    a=2;
                    inBasket.setVisibility(View.GONE);
                    notInBasket.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkClothesOnBuy(){
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("clothes")
                .child(String.valueOf(clothesViewing.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    buyClothesBottom.setText(R.string.purchased);
                }else {
                    buyClothesBottom.setText(R.string.buy);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(ClothesViewingProfileOther.newInstance(fragment, userInformation, bundle), userInformation, bundle, clothesViewing,"clothes",null,null,"all"), getActivity());
                bottomSheetDialog.dismiss();
            }
        });

        linearTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(ClothesViewingProfileOther.newInstance(fragment, userInformation, bundle), userInformation, bundle, clothesViewing,"clothes",null,null,"telegram"), getActivity());
                bottomSheetDialog.dismiss();
            }
        });
        linearInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(InstagramShareFragment.newInstance(ClothesViewingProfileOther.newInstance(fragment, userInformation, bundle), userInformation, bundle, clothesViewing,"clothes",null,null,"instagram"), getActivity());
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
                                RecentMethods.setCurrentFragment(GroupChatFragment.newInstance(userInformation, bundle, ClothesViewingProfileOther.newInstance(fragment, userInformation, bundle), chat),getActivity());
                                bottomSheetDialog.dismiss();
                            }
                        });
                    }else{
                        RecentMethods.setCurrentFragment(MessageFragment.newInstance(userInformation, bundle, ClothesViewingProfileOther.newInstance(fragment, userInformation, bundle), chat),getActivity());
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

    public void showDialogBasket(String textInDialog){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.Text);
        text.setText(textInDialog);
        RelativeLayout relative=dialog.findViewById(R.id.Delete_relative_layout);


        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_buying);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.acceptText);

        RelativeLayout no=dialog.findViewById(R.id.no);
        RelativeLayout yes=dialog.findViewById(R.id.yes);


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clothesViewing.getCurrencyType().equals("dollar")){

                }else {
                    if(schoolyCoins>=clothesPrise){
                        firebaseModel.getUsersReference().child(nick).child("clothes")
                                .child(String.valueOf(clothesViewing.getUid()))
                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    DataSnapshot snapshot=task.getResult();
                                    if(snapshot.exists()){
                                        Toast.makeText(getContext(), R.string.itempurchased, Toast.LENGTH_SHORT).show();
                                    }else {
                                        if(clothesViewing.getBodyType()==null){
                                            firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes")
                                                    .child(clothesViewing.getUid()).child("bodyType").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                DataSnapshot snapshot5=task.getResult();
                                                                clothesViewing.setBodyType(snapshot5.getValue(String.class));
                                                                buyAfterCheck();
                                                            }
                                                        }
                                                    });
                                        }else {
                                            buyAfterCheck();
                                        }
                                    }
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), R.string.notenoughcoins, Toast.LENGTH_SHORT).show();
                        RecentMethods.setCurrentFragment(CoinsFragmentSecond.newInstance(ViewingClothesPopular.newInstance(userInformation,bundle,fragment),userInformation,bundle
                        ), getActivity());
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void buyAfterCheck(){
        Clothes clothes=clothesViewing;
        clothes.setBuffer(null);
        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes")
                .child(clothesViewing.getUid()).child("purchaseNumber").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot1=task.getResult();
                            Long purchaseNumber=snapshot1.getValue(Long.class);
                            clothesViewing.setPurchaseNumber(purchaseNumber);
                            firebaseModel.getUsersReference().child(userInformation.getNick()).child("clothes")
                                    .child(clothesViewing.getUid()).setValue(clothes);
                            firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes")
                                    .child(clothesViewing.getUid()).child("purchaseNumber")
                                    .setValue(clothesViewing.getPurchaseNumber() + 1);
                            firebaseModel.getUsersReference().child(clothesViewing.getCreator()).child("myClothes").
                                    child(clothesViewing.getUid()).child("purchaseNumber")
                                    .setValue(clothesViewing.getPurchaseNumber() + 1);
                        }
                    }
                });
        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes")
                .child(clothesViewing.getUid()).child("purchaseToday").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot1=task.getResult();
                            Long purchaseToday=snapshot1.getValue(Long.class);
                            clothesViewing.setPurchaseToday(purchaseToday);
                            firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes")
                                    .child(clothesViewing.getUid()).child("purchaseToday")
                                    .setValue(clothesViewing.getPurchaseToday() + 1);
                            firebaseModel.getUsersReference().child(clothesViewing.getCreator()).child("myClothes").
                                    child(clothesViewing.getUid()).child("purchaseToday")
                                    .setValue(clothesViewing.getPurchaseToday() + 1);
                        }
                    }
                });
        if (clothesViewing.getCreator().equals("Schooly")) {

        } else {
            String numToBase = firebaseModel.getReference().child("users")
                    .child(clothesViewing.getCreator()).child("nontifications").push().getKey();
            firebaseModel.getReference().child("users")
                    .child(clothesViewing.getCreator()).child("nontifications")
                    .child(numToBase).setValue(new Nontification(userInformation.getNick(), "не отправлено", "одежда"
                            , "", clothesViewing.getClothesTitle(), clothesViewing.getClothesImage(), "не просмотрено", numToBase, 0));
        }
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("basket").
                child(clothesViewing.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            DataSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                firebaseModel.getUsersReference().child(userInformation.getNick()).child("basket")
                                        .child(clothesViewing.getUid()).removeValue();
                            } else {
                            }
                        }
                    }
                });
        schoolyCoins = schoolyCoins - clothesPrise;
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("money").setValue(schoolyCoins);
        RecentMethods.GetMoneyFromBase(userInformation.getNick(), firebaseModel, new Callbacks.MoneyFromBase() {
            @Override
            public void GetMoneyFromBase(long money) {
                schoolyCoins = money;
                schoolyCoinCV.setText(String.valueOf(money));
                userInformation.setmoney(money);
            }
        });
    }

    public void showDialogAlreadyBuy(String textInDialog){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.Text);
        text.setText(textInDialog);
        RelativeLayout relative=dialog.findViewById(R.id.Delete_relative_layout);


        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}