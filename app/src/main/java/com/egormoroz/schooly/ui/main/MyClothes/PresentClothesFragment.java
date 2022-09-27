package com.egormoroz.schooly.ui.main.MyClothes;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.coins.CoinsMainFragment;
import com.egormoroz.schooly.ui.coins.SendMoneyFragment;
import com.egormoroz.schooly.ui.coins.TransferHistoryFragment;
import com.egormoroz.schooly.ui.coins.TransferMoneyAdapter;
import com.egormoroz.schooly.ui.coins.TransferMoneyFragment;
import com.egormoroz.schooly.ui.main.Mining.Miner;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class PresentClothesFragment extends Fragment {

    ArrayList<UserInformation> listAdapterPeople=new ArrayList<UserInformation>();
    RecyclerView peopleRecyclerView;
    EditText searchUser;
    FirebaseModel firebaseModel=new FirebaseModel();
    String userNameToProfile,userName,nick;
    ImageView back;
    TextView emptySubscriptionList;
    ArrayList<Subscriber> userFromBase;
    Clothes clothes;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;

    public PresentClothesFragment(Clothes clothes,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.clothes = clothes;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static PresentClothesFragment newInstance(Clothes clothes,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new PresentClothesFragment(clothes,fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_presentclothes, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
//        AppBarLayout abl = getActivity().findViewById(R.id.AppBarLayout);
//        abl.setVisibility(abl.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        peopleRecyclerView=view.findViewById(R.id.peoplerecycler);
        searchUser=view.findViewById(R.id.searchuser);
        back=view.findViewById(R.id.backtocoins);
        emptySubscriptionList=view.findViewById(R.id.emptySubscriptionList);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        });
        firebaseModel.initAll();
        if(userInformation.getSubscription()==null){
            RecentMethods.getSubscriptionList(nick, firebaseModel, new Callbacks.getFriendsList() {
                @Override
                public void getFriendsList(ArrayList<Subscriber> friends) {
                    if (friends.size()==0){
                        emptySubscriptionList.setVisibility(View.VISIBLE);
                        peopleRecyclerView.setVisibility(View.GONE);
                    }else {
                        emptySubscriptionList.setVisibility(View.GONE);
                        PresentClothesAdapter presentClothesAdapter = new PresentClothesAdapter(friends,clothes,userInformation);
                        peopleRecyclerView.setAdapter(presentClothesAdapter);
                        PresentClothesAdapter.ItemClickListener itemClickListener = new PresentClothesAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(int alreadyHave, int position) {
                                Subscriber user = presentClothesAdapter.getItem(position);
                                userNameToProfile = user.getSub();
                                showDialog(alreadyHave);
                            }
                        };
                        presentClothesAdapter.setClickListener(itemClickListener);
                    }
                }
            });
        }else {
            if (userInformation.getSubscription().size()==0){
                emptySubscriptionList.setVisibility(View.VISIBLE);
                peopleRecyclerView.setVisibility(View.GONE);
            }else {
                emptySubscriptionList.setVisibility(View.GONE);
                peopleRecyclerView.setVisibility(View.VISIBLE);
                PresentClothesAdapter presentClothesAdapter = new PresentClothesAdapter(userInformation.getSubscription(),clothes,userInformation);
                peopleRecyclerView.setAdapter(presentClothesAdapter);
                PresentClothesAdapter.ItemClickListener itemClickListener = new PresentClothesAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int alreadyHave, int position) {
                        Subscriber user = presentClothesAdapter.getItem(position);
                        userNameToProfile = user.getSub();
                        showDialog(alreadyHave);
                    }
                };
                presentClothesAdapter.setClickListener(itemClickListener);
            }
        }
        initUserEnter();
    }

    public void initUserEnter() {
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userName = String.valueOf(searchUser.getText()).trim();
                userName = userName.toLowerCase();
                if(userInformation.getSubscription()==null){
                    firebaseModel.getUsersReference().child(nick).child("subscription").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot= task.getResult();
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
                                    emptySubscriptionList.setVisibility(View.VISIBLE);
                                    peopleRecyclerView.setVisibility(View.GONE);
                                }else{
                                    emptySubscriptionList.setVisibility(View.GONE);
                                    peopleRecyclerView.setVisibility(View.VISIBLE);
                                    PresentClothesAdapter presentClothesAdapter=new PresentClothesAdapter(userFromBase,clothes,userInformation);
                                    peopleRecyclerView.setAdapter(presentClothesAdapter);
                                    PresentClothesAdapter.ItemClickListener itemClickListener=new PresentClothesAdapter.ItemClickListener() {
                                        @Override
                                        public void onItemClick(int alreadyHave,int position) {
                                            Subscriber user = presentClothesAdapter.getItem(position);
                                            userNameToProfile=user.getSub();
                                            showDialog(alreadyHave);
                                        }
                                    };
                                    presentClothesAdapter.setClickListener(itemClickListener);
                                }
                            }
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
                        emptySubscriptionList.setVisibility(View.VISIBLE);
                        peopleRecyclerView.setVisibility(View.GONE);
                    }else{
                        emptySubscriptionList.setVisibility(View.GONE);
                        peopleRecyclerView.setVisibility(View.VISIBLE);
                        PresentClothesAdapter presentClothesAdapter=new PresentClothesAdapter(userFromBase,clothes,userInformation);
                        peopleRecyclerView.setAdapter(presentClothesAdapter);
                        PresentClothesAdapter.ItemClickListener itemClickListener=new PresentClothesAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(int alreadyHave,int position) {
                                Subscriber user = presentClothesAdapter.getItem(position);
                                userNameToProfile=user.getSub();
                                showDialog(alreadyHave);
                            }
                        };
                        presentClothesAdapter.setClickListener(itemClickListener);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void showDialog(int alreadyHave){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.buy_miner_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.acceptText);
        text.setText(getContext().getResources().getText(R.string.present)+" "+clothes.getClothesTitle()+" "+getContext().getResources().getString(R.string.givetext)+" "+userNameToProfile);

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
                if (alreadyHave==1) {
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.givealreadytext)
                            + userNameToProfile + getContext().getResources().getString(R.string.alreadнhasthisgift), Toast.LENGTH_SHORT).show();
                } else {
                    Clothes clothes1=clothes;
                    clothes1.setBuffer(null);
                    firebaseModel.getUsersReference().child(userNameToProfile).child("clothes")
                            .child(clothes.getUid()).setValue(clothes1);
                    String numToBase = firebaseModel.getReference().child("users")
                            .child(userNameToProfile).child("nontifications").push().getKey();
                    firebaseModel.getReference().child("users")
                            .child(userNameToProfile).child("nontifications")
                            .child(numToBase).setValue(new Nontification(nick, "не отправлено", "подарок"
                            , "", clothes.getClothesTitle(), clothes.getClothesImage(), "не просмотрено", numToBase,0));
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.giftsent), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}