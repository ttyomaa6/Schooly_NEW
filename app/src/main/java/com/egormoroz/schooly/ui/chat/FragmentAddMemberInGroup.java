package com.egormoroz.schooly.ui.chat;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.AlreadySearchAdapter;
import com.egormoroz.schooly.ui.people.PeopleAdapter;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;

import java.util.ArrayList;

public class FragmentAddMemberInGroup extends Fragment {
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;
    ImageView back;
    TextView ready,emptyListSuggested;
    RecyclerView membersRecycler,suggestedRecycler;
    FirebaseModel firebaseModel=new FirebaseModel();
    ArrayList<UserPeopleAdapter> dialogMembers=new ArrayList<>();
    ArrayList<UserPeopleAdapter> suggestedList=new ArrayList<>();
    ArrayList<String> membersNicksList=new ArrayList<>();
    Chat chat;


    public FragmentAddMemberInGroup(UserInformation userInformation,Bundle bundle,Fragment fragment,Chat chat) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
        this.chat=chat;
    }

    public static FragmentAddMemberInGroup newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment,Chat chat) {
        return new FragmentAddMemberInGroup(userInformation,bundle,fragment,chat);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseModel.initAll();
        return inflater.inflate(R.layout.fragment_add_group_member, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back=view.findViewById(R.id.back_tochat);
        suggestedRecycler=view.findViewById(R.id.recyclerSuggested);
        membersRecycler=view.findViewById(R.id.recyclerMembers);
        ready=view.findViewById(R.id.ready);
        emptyListSuggested=view.findViewById(R.id.emptySubscriptionList);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        loadSuggested();

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogMembers.size()>0){
                    ArrayList<UserPeopleAdapter> addedMembers=new ArrayList<>(chat.getMembers());
                    addedMembers.addAll(dialogMembers);
                    ArrayList<UserPeopleAdapter> membersWithoutAdded=new ArrayList<>(chat.getMembers());
                    for(int i=0;i<membersWithoutAdded.size();i++){
                        UserPeopleAdapter userPeopleAdapter=membersWithoutAdded.get(i);
                        firebaseModel.getUsersReference().child(userPeopleAdapter.getNick()).child("Dialogs")
                                .child(chat.getChatId()).child("members").setValue(addedMembers);
                    }
                    chat.setMembers(addedMembers);
                    for(int i=0;i<dialogMembers.size();i++){
                        UserPeopleAdapter userPeopleAdapter=dialogMembers.get(i);
                        firebaseModel.getUsersReference().child(userPeopleAdapter.getNick()).child("Dialogs")
                                .child(chat.getChatId()).setValue(chat);
                    }
                    bundle.putSerializable(chat.getChatId()+"MEMBERS",chat.getMembers());
                    RecentMethods.setCurrentFragment(fragment, getActivity());
                }
            }
        });

    }

    public void loadSuggested(){
        for (int i=0;i<chat.getMembers().size();i++){
            UserPeopleAdapter userPeopleAdapter=chat.getMembers().get(i);
            membersNicksList.add(userPeopleAdapter.getNick());
            if(i==chat.getMembers().size()-1){
                if(userInformation.getSubscription()==null){
                    RecentMethods.getSubscriptionList(userInformation.getNick(), firebaseModel, new Callbacks.getFriendsList() {
                        @Override
                        public void getFriendsList(ArrayList<Subscriber> friends) {
                            userInformation.setSubscription(friends);
                            for(int i=0;i<friends.size();i++){
                                UserPeopleAdapter userPeopleAdapter=new UserPeopleAdapter();
                                Subscriber sub=friends.get(i);
                                userPeopleAdapter.setNick(sub.getSub());
                                if(!membersNicksList.contains(sub.getSub()))
                                suggestedList.add(userPeopleAdapter);
                            }
                            if (suggestedList.size()==0){
                                emptyListSuggested.setVisibility(View.VISIBLE);
                                suggestedRecycler.setVisibility(View.GONE);
                            }else {
                                emptyListSuggested.setVisibility(View.GONE);
                                suggestedRecycler.setVisibility(View.VISIBLE);
                                PeopleAdapter peopleAdapter = new PeopleAdapter(suggestedList,userInformation);
                                suggestedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                suggestedRecycler.setAdapter(peopleAdapter);
                                PeopleAdapter.ItemClickListener clickListener =
                                        new PeopleAdapter.ItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position,String avatar,String bio) {
                                                UserPeopleAdapter user = peopleAdapter.getItem(position);
                                                checkList(user.getNick());
                                            }
                                        };
                                peopleAdapter.setClickListener(clickListener);
                            }
                        }
                    });
                }else {
                    if (userInformation.getSubscription().size()==0){
                        emptyListSuggested.setVisibility(View.VISIBLE);
                        suggestedRecycler.setVisibility(View.GONE);
                    }else {
                        emptyListSuggested.setVisibility(View.GONE);
                        suggestedRecycler.setVisibility(View.VISIBLE);
                        for(int s=0;s<userInformation.getSubscription().size();s++){
                            UserPeopleAdapter userPeopleAdapter1=new UserPeopleAdapter();
                            Subscriber sub=userInformation.getSubscription().get(s);
                            userPeopleAdapter1.setNick(sub.getSub());
                            if(!membersNicksList.contains(sub.getSub()))
                            suggestedList.add(userPeopleAdapter1);
                        }
                        if (suggestedList.size()==0){
                            emptyListSuggested.setVisibility(View.VISIBLE);
                            suggestedRecycler.setVisibility(View.GONE);
                        }else {
                            emptyListSuggested.setVisibility(View.GONE);
                            suggestedRecycler.setVisibility(View.VISIBLE);
                            PeopleAdapter peopleAdapter = new PeopleAdapter(suggestedList,userInformation);
                            suggestedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                            suggestedRecycler.setAdapter(peopleAdapter);
                            PeopleAdapter.ItemClickListener clickListener =
                                    new PeopleAdapter.ItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position,String avatar,String bio) {
                                            UserPeopleAdapter user = peopleAdapter.getItem(position);
                                            checkList(user.getNick());
                                        }
                                    };
                            peopleAdapter.setClickListener(clickListener);
                        }
                    }
                }
            }
        }
    }

    public void loadMembers(ArrayList<UserPeopleAdapter> members){
        AlreadySearchAdapter alreadySearchAdapter=new AlreadySearchAdapter(members,userInformation);
        membersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        membersRecycler.setAdapter(alreadySearchAdapter);
        AlreadySearchAdapter.ItemClickListener itemClickListener=new AlreadySearchAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position,String type) {
                UserPeopleAdapter user = alreadySearchAdapter.getItem(position);
                reloadList(user.getNick());
            }
        };
        alreadySearchAdapter.setClickListener(itemClickListener);
    }

    public void reloadList(String userNick){
        for(int i=0;i<dialogMembers.size();i++){
            UserPeopleAdapter userPeopleAdapter=dialogMembers.get(i);
            if(userNick.equals(userPeopleAdapter.getNick())){
                dialogMembers.remove(i);
            }
        }
        loadMembers(dialogMembers);
    }

    public void checkList(String userNick){
        int a=0;
        if(dialogMembers.size()!=0){
            for(int i=0;i<dialogMembers.size();i++){
                UserPeopleAdapter userPeopleAdapter=dialogMembers.get(i);
                if(userNick.equals(userPeopleAdapter.getNick())){
                    a++;
                }
            }
            if(a==0){
                UserPeopleAdapter userPeopleAdapter=new UserPeopleAdapter();
                userPeopleAdapter.setNick(userNick);
                dialogMembers.add(userPeopleAdapter);
                loadMembers(dialogMembers);
            }
        }else {
            UserPeopleAdapter userPeopleAdapter=new UserPeopleAdapter();
            userPeopleAdapter.setNick(userNick);
            dialogMembers.add(userPeopleAdapter);
            loadMembers(dialogMembers);
        }
    }
}
