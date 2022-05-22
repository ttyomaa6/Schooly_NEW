package com.egormoroz.schooly.ui.news;

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
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.chat.User;
import com.egormoroz.schooly.ui.coins.CoinsFragmentSecond;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.Shop.FittingFragment;
import com.egormoroz.schooly.ui.main.Shop.NewClothesAdapter;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothesPopular;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.egormoroz.schooly.ui.profile.SendLookAdapter;
import com.egormoroz.schooly.ui.profile.Wardrobe.ConstituentsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ViewingClothesNews extends Fragment {

    Fragment fragment;
    UserInformation userInformation;
    String nick;
    Bundle bundle;

    public ViewingClothesNews(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment = fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static ViewingClothesNews newInstance(Fragment fragment, UserInformation userInformation,Bundle bundle) {
        return new ViewingClothesNews(fragment,userInformation,bundle);

    }


    TextView clothesPriceCV,clothesTitleCV,schoolyCoinCV,buyClothesBottom
            ,purchaseNumber,creator,description,noDescription,fittingClothes;
    ImageView clothesImageCV,back,coinsImage,dollarImage,inBasket,notInBasket,send;
    long schoolyCoins,clothesPrise;
    RelativeLayout checkBasket;
    int a=0;
    RecyclerView recyclerView;
    ArrayList<Subscriber> userFromBase;
    SendLookAdapter.ItemClickListener itemClickListener;
    TextView emptyList;
    EditText editText,messageEdit;
    String userName;
    LinearLayout linearElse,linearTelegram,linearInstagram;
    Clothes clothesViewing;
    private FirebaseModel firebaseModel = new FirebaseModel();
    NewClothesAdapter.ViewHolder viewHolder;
    LinearLayout coinsLinear;
    String clothesPriceString,otherUserNickString;

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
        send=view.findViewById(R.id.send);
        creator=view.findViewById(R.id.creator);
        checkBasket=view.findViewById(R.id.checkBasket);
        clothesPriceCV=view.findViewById(R.id.clothesPricecv);
        back=view.findViewById(R.id.back_toshop);
        buyClothesBottom=view.findViewById(R.id.buyClothesBottom);
        purchaseNumber=view.findViewById(R.id.purchaseNumberViewing);
        coinsLinear=view.findViewById(R.id.linearCoins);
        fittingClothes=view.findViewById(R.id.fittingClothes);
        schoolyCoinCV.setText(String.valueOf(userInformation.getmoney()));
        schoolyCoins=userInformation.getmoney();
        fittingClothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(FittingFragment.newInstance(ViewingClothesNews.newInstance(fragment,userInformation,bundle),userInformation,bundle), getActivity());
            }
        });
        coinsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(CoinsFragmentSecond.newInstance(ViewingClothesNews.newInstance(fragment,userInformation,bundle),userInformation,bundle), getActivity());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });



        ConstituentsAdapter.singeClothesInfo(new ConstituentsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Clothes clothes) {
                clothesViewing=clothes;
                clothesPriceCV.setText(String.valueOf(clothes.getClothesPrice()));
                clothesTitleCV.setText(clothes.getClothesTitle());
                clothesPrise=clothes.getClothesPrice();
                creator.setText(clothesViewing.getCreator());
                schoolyCoins=userInformation.getmoney();
                creator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clothesViewing.getCreator().equals(userInformation.getNick())) {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", userInformation.getNick(), ViewingClothes.newInstance(fragment,userInformation,bundle),userInformation,bundle), getActivity());
                        }else {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", clothesViewing.getCreator(), ViewingClothes.newInstance(fragment,userInformation,bundle),userInformation,bundle), getActivity());
                        }
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
                    showDialogAlreadyBuy("Предмет куплен");
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
                firebaseModel.getUsersReference().child(userInformation.getNick()).child("clothes")
                        .child(clothesViewing.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot= task.getResult();
                            if(snapshot.exists()){
                                a=3;
                                showDialogBasket("Предмет уже куплен");
                            }else {}
                            if(a!=0 && a!=3){
                                if(a==1){
                                    firebaseModel.getUsersReference().child(userInformation.getNick()).child("basket")
                                            .child(clothesViewing.getUid()).removeValue();
                                }else if (a==2){
                                    firebaseModel.getUsersReference().child(userInformation.getNick()).child("basket")
                                            .child(clothesViewing.getUid()).setValue(clothesViewing);
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
                    buyClothesBottom.setText("Куплено");
                }else {
                    buyClothesBottom.setText("Купить");
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

                    String messageSenderRef = otherUserNick + "/Chats/" + userInformation.getNick() + "/Messages";
                    String messageReceiverRef = userInformation.getNick()  + "/Chats/" + otherUserNick+ "/Messages";
                    otherUserNickString=otherUserNick;

                    DatabaseReference userMessageKeyRef = firebaseModel.getUsersReference().child(userInformation.getNick() ).child("Chats").child(otherUserNick).child("Messages").push();
                    String messagePushID = userMessageKeyRef.getKey();

                    Map<String, String> messageTextBody = new HashMap<>();
                    messageTextBody.put("message", messageText);
                    messageTextBody.put("type", "text");
                    messageTextBody.put("from", userInformation.getNick() );
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
        if(userInformation.getSubscription()==null){
            RecentMethods.getSubscriptionList(userInformation.getNick(), firebaseModel, new Callbacks.getFriendsList() {
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
        }else {
            if (userInformation.getSubscription().size()==0){
                emptyList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else {
                SendLookAdapter sendLookAdapter = new SendLookAdapter(userInformation.getSubscription(),itemClickListener);
                recyclerView.setAdapter(sendLookAdapter);
            }
        }

        initUserEnter();

        bottomSheetDialog.show();
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
                    Query query = firebaseModel.getUsersReference().child(userInformation.getNick()).child("subscription");
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
                                SendLookAdapter sendLookAdapter = new SendLookAdapter(userFromBase,itemClickListener);
                                recyclerView.setAdapter(sendLookAdapter);
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
                        SendLookAdapter sendLookAdapter = new SendLookAdapter(userFromBase,itemClickListener);
                        recyclerView.setAdapter(sendLookAdapter);
                    }
                }
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
                firebaseModel.getUsersReference().child(userInformation.getNick()).child("Chats").child(otherUserNickString).child("LastMessage").setValue(Message);
                firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(userInformation.getNick()).child("LastMessage").setValue(Message);
                break;
            case "voice":
                addType("voice");
                firebaseModel.getUsersReference().child(userInformation.getNick()).child("Chats").child(otherUserNickString).child("LastMessage").setValue("Голосовое сообщение");
                firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(userInformation.getNick()).child("LastMessage").setValue("Голосовое сообщение");
                break;
            case "image":
                firebaseModel.getUsersReference().child(userInformation.getNick()).child("Chats").child(otherUserNickString).child("LastMessage").setValue("Фотография");
                firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(userInformation.getNick()).child("LastMessage").setValue("Фотография");
                addType("image");
                break;
        }
        Calendar calendar = Calendar.getInstance();
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("Chats").child(otherUserNickString).child("LastTime").setValue(RecentMethods.getCurrentTime());
        firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(userInformation.getNick()).child("LastTime").setValue(RecentMethods.getCurrentTime());
        firebaseModel.getUsersReference().child(userInformation.getNick()).child("Chats").child(otherUserNickString).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
        firebaseModel.getUsersReference().child(otherUserNickString).child("Chats").child(userInformation.getNick()).child("TimeMill").setValue(calendar.getTimeInMillis() * -1);
    }

    public void showDialogBasket(String textInDialog){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.Text);
        text.setText(textInDialog);
        RelativeLayout relative=dialog.findViewById(R.id.Relative);


        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
                        firebaseModel.getUsersReference().child(userInformation.getNick()).child("clothes")
                                .child(String.valueOf(clothesViewing.getUid())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DataSnapshot snapshot = task.getResult();
                                    if (snapshot.exists()) {
                                        Toast.makeText(getContext(), "Предмет куплен", Toast.LENGTH_SHORT).show();
                                    } else {
                                        firebaseModel.getUsersReference().child(userInformation.getNick()).child("clothes")
                                                .child(clothesViewing.getUid()).setValue(clothesViewing);
                                        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes")
                                                .child(clothesViewing.getUid()).child("purchaseNumber")
                                                .setValue(clothesViewing.getPurchaseNumber() + 1);
                                        firebaseModel.getReference().child(clothesViewing.getCreator()).child("myClothes").
                                                child(clothesViewing.getUid()).child("purchaseNumber")
                                                .setValue(clothesViewing.getPurchaseNumber() + 1);
                                        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes")
                                                .child(clothesViewing.getUid()).child("purchaseToday")
                                                .setValue(clothesViewing.getPurchaseToday() + 1);
                                        firebaseModel.getReference().child(clothesViewing.getCreator()).child("myClothes").
                                                child(clothesViewing.getUid()).child("purchaseToday")
                                                .setValue(clothesViewing.getPurchaseToday() + 1);
                                        if (clothesViewing.getCreator().equals("Schooly")) {

                                        } else {
                                            String numToBase = firebaseModel.getReference().child("users")
                                                    .child(clothesViewing.getCreator()).child("nontifications").push().getKey();
                                            Date date = new Date();
                                            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd hh:mm a");
                                            String dateAndTime = formatter.format(date);
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
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "Не хватает коинов", Toast.LENGTH_SHORT).show();
                        RecentMethods.setCurrentFragment(CoinsFragmentSecond.newInstance(ViewingClothes.newInstance(fragment,userInformation,bundle),userInformation,bundle), getActivity());
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialogAlreadyBuy(String textInDialog){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.Text);
        text.setText(textInDialog);
        RelativeLayout relative=dialog.findViewById(R.id.Relative);


        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
