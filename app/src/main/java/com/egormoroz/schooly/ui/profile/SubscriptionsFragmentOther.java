package com.egormoroz.schooly.ui.profile;

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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubscriptionsFragmentOther extends Fragment {
    FirebaseModel firebaseModel=new FirebaseModel();
    RecyclerView recyclerView;
    ImageView back;
    String otherUserNick,userNameToProfile,userName,nick;
    TextView emptyList;
    EditText searchUser;
    UserInformation userInformation,info;
    Fragment fragment;
    Bundle bundle;
    ArrayList<Subscriber> userFromBase,allSubs;

    public SubscriptionsFragmentOther(Fragment fragment,String otherUserNick,UserInformation userInformation,Bundle bundle) {
        this.fragment=fragment;
        this.otherUserNick=otherUserNick;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static SubscriptionsFragmentOther newInstance( Fragment fragment,String otherUserNick
            ,UserInformation userInformation,Bundle bundle) {
        return new SubscriptionsFragmentOther(fragment,otherUserNick,userInformation,bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putString(otherUserNick+"EDIT_SUBSCRIPTIONS_OTHER_TAG",searchUser.getText().toString().trim());
        bundle.putSerializable(otherUserNick+"SEARCH_SUBSCRIPTIONS_OTHER_LIST", userFromBase);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_subscriptionother, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        recyclerView=view.findViewById(R.id.friendsRecycler);
        emptyList=view.findViewById(R.id.emptySubscriptionListOther);
        back=view.findViewById(R.id.back_toprofile);
        searchUser=view.findViewById(R.id.searchuser);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", otherUserNick,fragment,userInformation,bundle),
                        getActivity());
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other",otherUserNick,fragment,userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        if(bundle!=null){
            if(bundle.getString(otherUserNick+"EDIT_SUBSCRIPTIONS_OTHER_TAG")!=null){
                String textEdit=bundle.getString(otherUserNick+"EDIT_SUBSCRIPTIONS_OTHER_TAG").trim();
                if(textEdit.length()>0){
                    searchUser.setText(textEdit);
                    userFromBase= (ArrayList<Subscriber>) bundle.getSerializable(otherUserNick+"SEARCH_SUBSCRIPTIONS_OTHER_LIST");
                    if (userFromBase.size()==0){
                        emptyList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        emptyList.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        SubscriptionsAdapterOther subscriptionsAdapterOther = new SubscriptionsAdapterOther(userFromBase,userInformation);
                        recyclerView.setAdapter(subscriptionsAdapterOther);
                        SubscriptionsAdapterOther.ItemClickListener clickListener =
                                new SubscriptionsAdapterOther.ItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Subscriber subscriber = subscriptionsAdapterOther.getItem(position);
                                        userNameToProfile = subscriber.getSub();
                                        firebaseModel.getUsersReference().child(userNameToProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(!snapshot.exists()){
                                                    Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                                }else {
                                                    if(userNameToProfile.equals(nick)){
                                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback",nick,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),getActivity());
                                                    }else {
                                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),
                                                                getActivity());
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                };
                        subscriptionsAdapterOther.setClickListener(clickListener);
                    }
                }else {
                    putSubscriptionListInAdapter();
                }
            }else {
                putSubscriptionListInAdapter();
            }
        }
        initUserEnter();
    }
    public void putSubscriptionListInAdapter(){
        if(bundle!=null){
            if(bundle.getSerializable(otherUserNick+"PROFILE_OTHER_BUNDLE")!=null){
                info= (UserInformation) bundle.getSerializable(otherUserNick+"PROFILE_OTHER_BUNDLE");
                if(info.getSubscription()!=null){
                    if (info.getSubscription().size()==0){
                        emptyList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        emptyList.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        SubscriptionsAdapterOther subscriptionsAdapterOther = new SubscriptionsAdapterOther(info.getSubscription(),userInformation);
                        recyclerView.setAdapter(subscriptionsAdapterOther);
                        SubscriptionsAdapterOther.ItemClickListener clickListener =
                                new SubscriptionsAdapterOther.ItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Subscriber user = subscriptionsAdapterOther.getItem(position);
                                        userNameToProfile=user.getSub();
                                        firebaseModel.getUsersReference().child(userNameToProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(!snapshot.exists()){
                                                    Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                                }else {
                                                    if(userNameToProfile.equals(nick)){
                                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback",nick,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),getActivity());
                                                    }else {
                                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),
                                                                getActivity());
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                };
                        subscriptionsAdapterOther.setClickListener(clickListener);
                    }
                }else{
                    getOtherSubsList();
                }
            }else{
                getOtherSubsList();
            }
        }
    }

    public void getOtherSubsList(){
        RecentMethods.getSubscriptionList(otherUserNick, firebaseModel, new Callbacks.getFriendsList() {
            @Override
            public void getFriendsList(ArrayList<Subscriber> friends) {
                if (friends.size()==0){
                    emptyList.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    emptyList.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    SubscriptionsAdapterOther subscriptionsAdapterOther = new SubscriptionsAdapterOther(friends,userInformation);
                    recyclerView.setAdapter(subscriptionsAdapterOther);
                    SubscriptionsAdapterOther.ItemClickListener clickListener =
                            new SubscriptionsAdapterOther.ItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Subscriber user = subscriptionsAdapterOther.getItem(position);
                                    userNameToProfile=user.getSub();
                                    firebaseModel.getUsersReference().child(userNameToProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(!snapshot.exists()){
                                                Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                            }else {
                                                if(userNameToProfile.equals(nick)){
                                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback",nick,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),getActivity());
                                                }else {
                                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),
                                                            getActivity());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            };
                    subscriptionsAdapterOther.setClickListener(clickListener);
                }
            }
        });
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
                if(allSubs==null){
                    firebaseModel.getUsersReference().child(otherUserNick).child("subscription")
                            .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot= task.getResult();
                                userFromBase = new ArrayList<>();
                                allSubs=new ArrayList<>();
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Subscriber subscriber = new Subscriber();
                                    subscriber.setSub(snap.getValue(String.class));
                                    allSubs.add(subscriber);
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
                                if (userFromBase.size()==0){
                                    emptyList.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }else {
                                    emptyList.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    SubscriptionsAdapterOther subscriptionsAdapterOther = new SubscriptionsAdapterOther(userFromBase,userInformation);
                                    recyclerView.setAdapter(subscriptionsAdapterOther);
                                    SubscriptionsAdapterOther.ItemClickListener clickListener =
                                            new SubscriptionsAdapterOther.ItemClickListener() {
                                                @Override
                                                public void onItemClick(View view, int position) {
                                                    Subscriber subscriber = subscriptionsAdapterOther.getItem(position);
                                                    userNameToProfile = subscriber.getSub();
                                                    firebaseModel.getUsersReference().child(userNameToProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(!snapshot.exists()){
                                                                Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                                            }else {
                                                                if(userNameToProfile.equals(nick)){
                                                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback",nick,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),getActivity());
                                                                }else {
                                                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),
                                                                            getActivity());
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            };
                                    subscriptionsAdapterOther.setClickListener(clickListener);
                                }
                            }
                        }
                    });
                }else {
                    userFromBase=new ArrayList<>();
                    for (int s=0;s<allSubs.size();s++) {
                        Subscriber subscriber =allSubs.get(s);
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
                    if (userFromBase.size()==0){
                        emptyList.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        emptyList.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        SubscriptionsAdapterOther subscriptionsAdapterOther = new SubscriptionsAdapterOther(userFromBase,userInformation);
                        recyclerView.setAdapter(subscriptionsAdapterOther);
                        SubscriptionsAdapterOther.ItemClickListener clickListener =
                                new SubscriptionsAdapterOther.ItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Subscriber subscriber = subscriptionsAdapterOther.getItem(position);
                                        userNameToProfile = subscriber.getSub();
                                        firebaseModel.getUsersReference().child(userNameToProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(!snapshot.exists()){
                                                    Toast.makeText(getContext(), R.string.usernotfound, Toast.LENGTH_SHORT).show();
                                                }else {
                                                    if(userNameToProfile.equals(nick)){
                                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback",nick,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),getActivity());
                                                    }else {
                                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile,SubscriptionsFragmentOther.newInstance(fragment,otherUserNick,userInformation,bundle),userInformation,bundle),
                                                                getActivity());
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                };
                        subscriptionsAdapterOther.setClickListener(clickListener);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}