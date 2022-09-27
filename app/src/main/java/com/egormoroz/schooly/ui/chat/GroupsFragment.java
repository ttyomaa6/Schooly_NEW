package com.egormoroz.schooly.ui.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class GroupsFragment extends Fragment
{
    View groupFragmentView;
    DatabaseReference GroupRef;
    FirebaseModel firebaseModel = new FirebaseModel();
    RecyclerView chatsList;
    TextView noChats,createGroup;
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;
    private  MessageAdapter messageAdapter;
    public GroupsFragment(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
    }

    public static GroupsFragment newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        return new GroupsFragment(userInformation,bundle,fragment);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        groupFragmentView =  inflater.inflate(R.layout.fragment_groups, container, false);
        firebaseModel.initAll();
        return groupFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noChats=view.findViewById(R.id.noChatsGroup);
        chatsList = (RecyclerView) view.findViewById(R.id.chats_listGroup);
        createGroup=view.findViewById(R.id.addChat);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecentMethods.setCurrentFragment(CreateGroupFragment.newInstance(userInformation, bundle, DialogsFragment.newInstance(userInformation, bundle, fragment)), getActivity());
            }
        });
        loadChats();
        GroupRef = firebaseModel.getUsersReference().child("Groups");
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().requestLayout();

    }

    public  void loadChats(){
        if(userInformation.getTalksArrayList()==null){
            RecentMethods.getDialogs(userInformation.getNick(), firebaseModel, new Callbacks.loadDialogs() {
                @Override
                public void LoadData(ArrayList<Chat> dialogs,ArrayList<Chat> talksArrayList) {
                    if(talksArrayList.size()==0){
                        noChats.setVisibility(View.VISIBLE);
                        chatsList.setVisibility(View.GONE);
                    }   else {
                        DialogAdapter dialogAdapter=new DialogAdapter(talksArrayList);
                        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
                        chatsList.setAdapter(dialogAdapter);
                        DialogAdapter.ItemClickListener itemClickListener=new DialogAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(Chat chat) {
                                RecentMethods.loadChatMembers(userInformation.getNick(), chat.getChatId(), firebaseModel, new Callbacks.GetChatMembers() {
                                    @Override
                                    public void getChatMembers(ArrayList<UserPeopleAdapter> chatMembers) {
                                        chat.setMembers(chatMembers);
                                        RecentMethods.setCurrentFragment(GroupChatFragment.newInstance(userInformation, bundle, DialogsFragment.newInstance(userInformation, bundle,fragment),chat), getActivity());
                                    }
                                });
                            }
                        };
                        dialogAdapter.setClickListener(itemClickListener);
                    }
                }
            });
        }else{
            if(userInformation.getTalksArrayList().size()==0){
                chatsList.setVisibility(View.GONE);
                noChats.setVisibility(View.VISIBLE);
            }else {
                DialogAdapter dialogAdapter=new DialogAdapter(userInformation.getTalksArrayList());
                chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
                chatsList.setAdapter(dialogAdapter);
                DialogAdapter.ItemClickListener itemClickListener=new DialogAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(Chat chat) {
                        RecentMethods.loadChatMembers(userInformation.getNick(), chat.getChatId(), firebaseModel, new Callbacks.GetChatMembers() {
                            @Override
                            public void getChatMembers(ArrayList<UserPeopleAdapter> chatMembers) {
                                chat.setMembers(chatMembers);
                                RecentMethods.setCurrentFragment(GroupChatFragment.newInstance(userInformation, bundle, DialogsFragment.newInstance(userInformation, bundle,fragment),chat), getActivity());
                            }
                        });
                    }
                };
                dialogAdapter.setClickListener(itemClickListener);
            }
        }
    }
}
