package com.egormoroz.schooly.ui.coins;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.CreateCharacter.BodyFragment;
import com.egormoroz.schooly.ui.main.CreateCharacter.CharacterAdapter;
import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.PeopleAdapter;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.egormoroz.schooly.ui.profile.SubscriberFragment;
import com.egormoroz.schooly.ui.profile.SubscribersAdapter;
import com.egormoroz.schooly.ui.profile.SubscriptionsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TransferMoneyFragment extends Fragment {

    ArrayList<UserInformation> listAdapterPeople=new ArrayList<UserInformation>();
    RecyclerView peopleRecyclerView;
    EditText searchUser;
    FirebaseModel firebaseModel=new FirebaseModel();
    String userNameToProfile,userName,nick;
    ImageView backToCoins,transferHistory;
    TextView emptySubscriptionList,emptySearchSubscriptionList;
    Bundle bundle;
    ArrayList<Subscriber> userFromBase;
    Fragment fragment;
    UserInformation userInformation;

    public TransferMoneyFragment(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment = fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static TransferMoneyFragment newInstance(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new TransferMoneyFragment(fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transfermoney, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putString("EDIT_TRANSFER_MONEY_TAG",searchUser.getText().toString().trim());
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        peopleRecyclerView=view.findViewById(R.id.peoplerecycler);
        searchUser=view.findViewById(R.id.searchuser);
        backToCoins=view.findViewById(R.id.backtocoins);
        emptySubscriptionList=view.findViewById(R.id.emptySubscriptionList);
        emptySearchSubscriptionList=view.findViewById(R.id.emptySearchSubscriptionList);
        transferHistory=view.findViewById(R.id.transferHistory);
        transferHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(TransferHistoryFragment.newInstance(fragment,userInformation,bundle), getActivity());
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        backToCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        });
        firebaseModel.initAll();
        if(bundle!=null){
            if(bundle.getString("EDIT_TRANSFER_MONEY_TAG")!=null){
                String textEdit=bundle.getString("EDIT_TRANSFER_MONEY_TAG");
                if(textEdit.length()>0){
                    searchUser.setText(textEdit);
                    initUserEnter(textEdit);
                }else {
                    setSubsInAdapter();
                }
            }else{
                setSubsInAdapter();
            }
        }
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userName = String.valueOf(searchUser.getText()).trim();
                userName = userName.toLowerCase();
                if(userName.length()>0){
                    initUserEnter(userName);
                    emptySubscriptionList.setVisibility(View.GONE);
                }else{
                    if(userInformation.getSubscription().size()==0){
                        emptySubscriptionList.setVisibility(View.VISIBLE);
                    }else {
                        emptySubscriptionList.setVisibility(View.GONE);
                    }
                    emptySearchSubscriptionList.setVisibility(View.GONE);
                    peopleRecyclerView.setVisibility(View.VISIBLE);
                    TransferMoneyAdapter transferMoneyAdapter = new TransferMoneyAdapter(userInformation.getSubscription());
                    peopleRecyclerView.setAdapter(transferMoneyAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setSubsInAdapter() {
        if (userInformation.getSubscription() == null) {
            RecentMethods.getSubscriptionList(nick, firebaseModel, new Callbacks.getFriendsList() {
                @Override
                public void getFriendsList(ArrayList<Subscriber> friends) {
                    userInformation.setSubscription(friends);
                    if (friends.size() == 0) {
                        emptySubscriptionList.setVisibility(View.VISIBLE);
                        peopleRecyclerView.setVisibility(View.GONE);
                    } else {
                        emptySubscriptionList.setVisibility(View.GONE);
                        peopleRecyclerView.setVisibility(View.VISIBLE);
                        TransferMoneyAdapter transferMoneyAdapter = new TransferMoneyAdapter(friends);
                        peopleRecyclerView.setAdapter(transferMoneyAdapter);
                        TransferMoneyAdapter.ItemClickListener itemClickListener = new TransferMoneyAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Subscriber user = transferMoneyAdapter.getItem(position);
                                userNameToProfile = user.getSub();
                                RecentMethods.setCurrentFragment(SendMoneyFragment.newInstance(userNameToProfile, fragment, userInformation,bundle), getActivity());
                            }
                        };
                        transferMoneyAdapter.setClickListener(itemClickListener);
                    }
                }
            });
        } else {
            if (userInformation.getSubscription().size() == 0) {
                emptySubscriptionList.setVisibility(View.VISIBLE);
                peopleRecyclerView.setVisibility(View.GONE);
            } else {
                emptySubscriptionList.setVisibility(View.GONE);
                peopleRecyclerView.setVisibility(View.VISIBLE);
                TransferMoneyAdapter transferMoneyAdapter = new TransferMoneyAdapter(userInformation.getSubscription());
                peopleRecyclerView.setAdapter(transferMoneyAdapter);
                TransferMoneyAdapter.ItemClickListener itemClickListener = new TransferMoneyAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Subscriber user = transferMoneyAdapter.getItem(position);
                        userNameToProfile = user.getSub();
                        RecentMethods.setCurrentFragment(SendMoneyFragment.newInstance(userNameToProfile, fragment, userInformation,bundle), getActivity());
                    }
                };
                transferMoneyAdapter.setClickListener(itemClickListener);
            }
        }
    }

    public void initUserEnter(String textEdit) {
        if(userInformation.getSubscription()==null){
            Query query = firebaseModel.getUsersReference().child(nick).child("subscription");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userFromBase = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Subscriber subscriber = new Subscriber();
                        subscriber.setSub(snap.getValue(String.class));
                        String nick = subscriber.getSub();
                        int valueLetters = textEdit.length();
                        nick = nick.toLowerCase();
                        if (nick.length() < valueLetters) {
                            if (nick.equals(textEdit))
                                userFromBase.add(subscriber);
                        } else {
                            nick = nick.substring(0, valueLetters);
                            if (nick.equals(textEdit))
                                userFromBase.add(subscriber);
                        }

                    }
                    if (userFromBase.size() == 0) {
                        emptySearchSubscriptionList.setVisibility(View.VISIBLE);
                        peopleRecyclerView.setVisibility(View.GONE);
                    }else{
                        emptySearchSubscriptionList.setVisibility(View.GONE);
                        peopleRecyclerView.setVisibility(View.VISIBLE);
                        TransferMoneyAdapter transferMoneyAdapter=new TransferMoneyAdapter(userFromBase);
                        peopleRecyclerView.setAdapter(transferMoneyAdapter);
                        TransferMoneyAdapter.ItemClickListener itemClickListener=new TransferMoneyAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Subscriber user = transferMoneyAdapter.getItem(position);
                                userNameToProfile=user.getSub();
                                RecentMethods.setCurrentFragment(SendMoneyFragment.newInstance(userNameToProfile,fragment,userInformation,bundle), getActivity());
                            }
                        };
                        transferMoneyAdapter.setClickListener(itemClickListener);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            userFromBase=new ArrayList<>();
            for (int i=0;i<userInformation.getSubscription().size();i++) {
                Subscriber subscriber = userInformation.getSubscription().get(i);
                String nick = subscriber.getSub();
                int valueLetters = textEdit.length();
                nick = nick.toLowerCase();
                if (nick.length() < valueLetters) {
                    if (nick.equals(textEdit))
                        userFromBase.add(subscriber);
                } else {
                    nick = nick.substring(0, valueLetters);
                    if (nick.equals(textEdit))
                        userFromBase.add(subscriber);
                }

            }
            if (userFromBase.size() == 0) {
                emptySearchSubscriptionList.setVisibility(View.VISIBLE);
                peopleRecyclerView.setVisibility(View.GONE);
            }else{
                emptySearchSubscriptionList.setVisibility(View.GONE);
                peopleRecyclerView.setVisibility(View.VISIBLE);
                TransferMoneyAdapter transferMoneyAdapter=new TransferMoneyAdapter(userFromBase);
                peopleRecyclerView.setAdapter(transferMoneyAdapter);
                TransferMoneyAdapter.ItemClickListener itemClickListener=new TransferMoneyAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Subscriber user = transferMoneyAdapter.getItem(position);
                        userNameToProfile=user.getSub();
                        RecentMethods.setCurrentFragment(SendMoneyFragment.newInstance(userNameToProfile,fragment,userInformation,bundle), getActivity());
                    }
                };
                transferMoneyAdapter.setClickListener(itemClickListener);
            }
        }
    }
}

