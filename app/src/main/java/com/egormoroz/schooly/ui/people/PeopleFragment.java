package com.egormoroz.schooly.ui.people;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import com.egormoroz.schooly.ui.news.ViewingClothesNews;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PeopleFragment extends Fragment {
    ArrayList<UserInformation> listAdapterPeople=new ArrayList<UserInformation>();
    RecyclerView peopleRecyclerView;
    EditText searchUser;
    String userName,userNameToProfile,avatar,bio,nick;


    UserInformation userInformation;

    public PeopleFragment(UserInformation userInformation) {
        this.userInformation=userInformation;
    }

    public static PeopleFragment newInstance( UserInformation userInformation) {
        return new PeopleFragment(userInformation);

    }
    FirebaseModel firebaseModel=new FirebaseModel();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        peopleRecyclerView=view.findViewById(R.id.peoplerecycler);
        searchUser=view.findViewById(R.id.searchuser);
        firebaseModel.initAll();
        setAlreadySearchedInAdapter();
        initUserEnter();
        setPeopleData();
    }

    public void setAlreadySearchedInAdapter(){
        if(userInformation.getAlreadySearched()==null){
            RecentMethods.getAlreadySearched(nick, firebaseModel, new Callbacks.GetAlreadySearched() {
                @Override
                public void getAlreadySearched(ArrayList<UserPeopleAdapter> searchedUserFromBase) {
                    userInformation.setAlreadySearched(searchedUserFromBase);
                    AlreadySearchAdapter alreadySearchAdapter=new AlreadySearchAdapter(searchedUserFromBase);
                    peopleRecyclerView.setAdapter(alreadySearchAdapter);
                    AlreadySearchAdapter.ItemClickListener itemClickListener=new AlreadySearchAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            UserPeopleAdapter user = alreadySearchAdapter.getItem(position);
                            userNameToProfile = user.getNick();
                            if (userNameToProfile.equals(nick)) {
                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", nick, PeopleFragment.newInstance(userInformation),userInformation), getActivity());
                            } else {
                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, PeopleFragment.newInstance(userInformation),userInformation),
                                        getActivity());
                            }
                        }
                    };
                    alreadySearchAdapter.setClickListener(itemClickListener);
                }
            });
        }else {
            AlreadySearchAdapter alreadySearchAdapter=new AlreadySearchAdapter(userInformation.getAlreadySearched());
            peopleRecyclerView.setAdapter(alreadySearchAdapter);
            AlreadySearchAdapter.ItemClickListener itemClickListener=new AlreadySearchAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    UserPeopleAdapter user = alreadySearchAdapter.getItem(position);
                    userNameToProfile = user.getNick();
                    if (userNameToProfile.equals(nick)) {
                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", nick, PeopleFragment.newInstance(userInformation),userInformation), getActivity());
                    } else {
                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, PeopleFragment.newInstance(userInformation),userInformation),
                                getActivity());
                    }
                }
            };
            alreadySearchAdapter.setClickListener(itemClickListener);
        }
    }
    public void setPeopleData(){
        listAdapterPeople.add(new UserInformation("nick", "fidjfif", "gk",
                "6", "password", "Helicopter", 1000, new ArrayList<>(),new ArrayList<>(),1,100,0, new ArrayList<>()
                , new ArrayList<>(), ""," ","open","open","open","open",
                new ArrayList<>(),"regular", new ArrayList<>(),0,new ArrayList<>(),new ArrayList<>()
        ,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()));
    }
    public void initUserEnter(){
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (String.valueOf(searchUser.getText()).trim().length()==0){
                    Query query3=firebaseModel.getUsersReference().child(nick).child("alreadySearched");
                    query3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<UserPeopleAdapter> searchedUserFromBase=new ArrayList<>();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                UserPeopleAdapter upa=new UserPeopleAdapter();
                                upa.setNick(snap.child("nick").getValue(String.class));
                                upa.setBio(snap.child("bio").getValue(String.class));
                                upa.setAvatar(snap.child("avatar").getValue(String.class));
                                searchedUserFromBase.add(upa);
                            }
                            AlreadySearchAdapter alreadySearchAdapter=new AlreadySearchAdapter(searchedUserFromBase);
                            peopleRecyclerView.setAdapter(alreadySearchAdapter);
                            AlreadySearchAdapter.ItemClickListener itemClickListener=new AlreadySearchAdapter.ItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    UserPeopleAdapter user = alreadySearchAdapter.getItem(position);
                                    userNameToProfile = user.getNick();
                                    if (userNameToProfile.equals(nick)) {
                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", nick, PeopleFragment.newInstance(userInformation),userInformation), getActivity());
                                    } else {
                                        RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, PeopleFragment.newInstance(userInformation),userInformation),
                                                getActivity());
                                    }
                                }
                            };
                            alreadySearchAdapter.setClickListener(itemClickListener);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    userName = String.valueOf(searchUser.getText()).trim();
                    userName = userName.toLowerCase();
                    Query query = firebaseModel.getReference("usersNicks");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<UserPeopleAdapter> userFromBase = new ArrayList<>();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                UserPeopleAdapter upa = new UserPeopleAdapter();
                                upa.setNick(snap.child("nick").getValue(String.class));
                                upa.setBio(snap.child("bio").getValue(String.class));
                                upa.setAvatar(snap.child("avatar").getValue(String.class));
                                String nickName = upa.getNick();
                                String nick1 = nickName;
                                int valueLetters = userName.length();
                                nick1 = nick1.toLowerCase();
                                if (nick1.length() < valueLetters) {
                                    if (nick1.equals(userName))
                                        userFromBase.add(upa);
                                } else {
                                    nick1 = nick1.substring(0, valueLetters);
                                    if (nick1.equals(userName))
                                        userFromBase.add(upa);
                                }

                            }
                            PeopleAdapter peopleAdapter = new PeopleAdapter(userFromBase);
                            peopleRecyclerView.setAdapter(peopleAdapter);
                            PeopleAdapter.ItemClickListener clickListener =
                                    new PeopleAdapter.ItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            UserPeopleAdapter user = peopleAdapter.getItem(position);
                                            userNameToProfile = user.getNick();
                                            if (userNameToProfile.equals(nick)) {
                                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", nick, PeopleFragment.newInstance(userInformation),userInformation), getActivity());
                                            } else {
                                                Query querySearchedAvatar = firebaseModel.getUsersReference().child(userNameToProfile).child("avatar");
                                                querySearchedAvatar.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        avatar = snapshot.getValue(String.class);
                                                        Query querySearchedBio = firebaseModel.getUsersReference().child(userNameToProfile).child("bio");
                                                        querySearchedBio.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                bio = snapshot.getValue(String.class);
                                                                firebaseModel.getUsersReference().child(nick).child("alreadySearched").child(userNameToProfile)
                                                                        .setValue(new UserPeopleAdapter(userNameToProfile, avatar, bio));
                                                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, PeopleFragment.newInstance(userInformation),userInformation),
                                                                        getActivity());
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }
                                    };
                            peopleAdapter.setClickListener(clickListener);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}