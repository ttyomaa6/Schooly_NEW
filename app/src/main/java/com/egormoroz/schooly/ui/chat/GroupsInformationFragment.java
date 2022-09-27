package com.egormoroz.schooly.ui.chat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.people.PeopleAdapter;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class GroupsInformationFragment extends Fragment {

    TextView noMaterials,addNewMember, nick,leaveChat;
    ImageView avatar, back;
    RecyclerView recyclerMembers,recyclerMaterials;
    SwitchMaterial switchMaterial;
    FirebaseModel firebaseModel=new FirebaseModel();
    boolean checkType;
    ArrayList<UserPeopleAdapter> members=new ArrayList<>();
    ArrayList<String> materials=new ArrayList<>();
    String  otherUserNick;
    UserInformation userInformation;
    Bundle bundle;
    Fragment fragment;
    Chat chat;

    public GroupsInformationFragment(UserInformation userInformation, Bundle bundle, Fragment fragment, Chat chat) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
        this.chat=chat;
    }

    public static GroupsInformationFragment newInstance(UserInformation userInformation, Bundle bundle, Fragment fragment, Chat chat) {
        return new GroupsInformationFragment(userInformation,bundle,fragment,chat);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_groupinfo, container, false);
        firebaseModel.initAll();
        switchMaterial = root.findViewById(R.id.nontsSwitch);
        noMaterials = root.findViewById(R.id.noMaterials);
        recyclerMembers = root.findViewById(R.id.recyclerMembers);
        leaveChat=root.findViewById(R.id.leaveTalk);
        recyclerMaterials=root.findViewById(R.id.recyclerMaterials);
        back = root.findViewById(R.id.back_tochat);
        addNewMember=root.findViewById(R.id.addNewMember);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        otherUserNick=chat.getName();
        Intent intentReceived = new Intent();
        Bundle data = intentReceived.getExtras();

        if (data != null) {

        }
        else {

        }

        if(chat.getType().equals("talk")){
            leaveChat.setVisibility(View.VISIBLE);
            leaveChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
        }

        loadChatMembers();
        loadChatMaterial();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                RecentMethods.setCurrentFragment(fragment,getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        Query query=firebaseModel.getUsersReference().child(userInformation.getNick()).child("chatsNontsType");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String accountType = snapshot.getValue(String.class);
                if(accountType.equals("close")){
                    switchMaterial.setChecked(true);
                }else {
                    switchMaterial.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switchMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkType=true;
                Query query=firebaseModel.getUsersReference().child(userInformation.getNick())
                        .child("Dialogs").child(chat.getName()).child("chatsNontsType");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (checkType=switchMaterial.isChecked()){
                            firebaseModel.getUsersReference().child(userInformation.getNick())
                                    .child("chatsNontsType").setValue("close");
                        }else {
                            firebaseModel.getUsersReference().child(userInformation.getNick())
                                    .child("chatsNontsType").setValue("open");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        });

    }

    public void loadChatMembers(){
        if(bundle.getSerializable(chat.getChatId()+"MEMBERS")==null){
            firebaseModel.getUsersReference().child(userInformation.getNick()).child("Dialogs")
                    .child(chat.getChatId()).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                for(DataSnapshot snap:snapshot.getChildren()){
                                    UserPeopleAdapter userPeopleAdapter=snap.getValue(UserPeopleAdapter.class);
                                    members.add(userPeopleAdapter);
                                }
                                bundle.putSerializable(chat.getChatId()+"MEMBERS",members);
                                chat.setMembers(members);
                                addNewMember.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RecentMethods.setCurrentFragment(FragmentAddMemberInGroup.newInstance(userInformation, bundle,
                                                GroupsInformationFragment.newInstance(userInformation,bundle,fragment,chat),chat),getActivity());
                                    }
                                });
                                PeopleAdapter peopleAdapter=new PeopleAdapter(members, userInformation);
                                recyclerMembers.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerMembers.setAdapter(peopleAdapter);
                                PeopleAdapter.ItemClickListener clickListener =
                                        new PeopleAdapter.ItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position,String avatar,String bio) {
                                                UserPeopleAdapter user = peopleAdapter.getItem(position);
                                                String userNameToProfile = user.getNick();
                                                if (userNameToProfile.equals(nick)) {
                                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", userInformation.getNick(), ChatInformationFragment.newInstance(userInformation,bundle,fragment,chat),userInformation,bundle), getActivity());
                                                } else {
                                                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, ChatInformationFragment.newInstance(userInformation,bundle,fragment,chat),userInformation,bundle),
                                                            getActivity());
                                                    firebaseModel.getReference("users").child(userInformation.getNick()).child("alreadySearched").child(userNameToProfile)
                                                            .setValue(new UserPeopleAdapter(userNameToProfile, avatar, bio));
                                                }
                                            }
                                        };
                                peopleAdapter.setClickListener(clickListener);
                            }
                        }
                    });
        }else{
            members= (ArrayList<UserPeopleAdapter>) bundle.getSerializable(chat.getChatId()+"MEMBERS");
            PeopleAdapter peopleAdapter=new PeopleAdapter(members, userInformation);
            recyclerMembers.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerMembers.setAdapter(peopleAdapter);
            chat.setMembers(members);
            addNewMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecentMethods.setCurrentFragment(FragmentAddMemberInGroup.newInstance(userInformation, bundle,
                            GroupsInformationFragment.newInstance(userInformation,bundle,fragment,chat),chat),getActivity());
                }
            });
            PeopleAdapter.ItemClickListener clickListener =
                    new PeopleAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position,String avatar,String bio) {
                            UserPeopleAdapter user = peopleAdapter.getItem(position);
                            String userNameToProfile = user.getNick();
                            if (userNameToProfile.equals(nick)) {
                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", userInformation.getNick(), ChatInformationFragment.newInstance(userInformation,bundle,fragment,chat),userInformation,bundle), getActivity());
                            } else {
                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, ChatInformationFragment.newInstance(userInformation,bundle,fragment,chat),userInformation,bundle),
                                        getActivity());
                                firebaseModel.getReference("users").child(userInformation.getNick()).child("alreadySearched").child(userNameToProfile)
                                        .setValue(new UserPeopleAdapter(userNameToProfile, avatar, bio));
                            }
                        }
                    };
            peopleAdapter.setClickListener(clickListener);
        }
    }
    public void loadChatMaterial(){
        if(bundle.getSerializable(chat.getChatId()+"MATERIALS")==null){
            firebaseModel.getUsersReference().child(userInformation.getNick()).child("Dialogs")
                    .child(chat.getChatId()).child("dialogueMaterials").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                ArrayList<String> dialogueMaterials=new ArrayList<>();
                                for(DataSnapshot snap:snapshot.getChildren()){
                                    String image=snap.getValue(String.class);
                                    dialogueMaterials.add(image);
                                }
                                bundle.putSerializable(chat.getChatId()+"MATERIALS",dialogueMaterials);
                                if(dialogueMaterials.size()==0){
                                    noMaterials.setVisibility(View.VISIBLE);
                                } else{
                                    Collections.reverse(dialogueMaterials);
                                    DialogueMaterialsAdapter dialogueMaterialsAdapter=new DialogueMaterialsAdapter(dialogueMaterials, userInformation);
                                    recyclerMaterials.setLayoutManager(new GridLayoutManager(getContext(), 3));
                                    recyclerMaterials.setAdapter(dialogueMaterialsAdapter);
                                }

                            }
                        }
                    });
        }else {
            materials= (ArrayList<String>) bundle.getSerializable(chat.getChatId()+"MATERIALS");
            if(materials.size()==0){
                noMaterials.setVisibility(View.VISIBLE);
            } else{
                DialogueMaterialsAdapter dialogueMaterialsAdapter=new DialogueMaterialsAdapter(materials, userInformation);
                recyclerMaterials.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerMaterials.setAdapter(dialogueMaterialsAdapter);
            }
        }
    }

    public void showDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_layout_blacklist);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.complainText);
        text.setText(R.string.leaveTalk);

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
                for(int i=0;i<chat.getMembers().size();i++){
                    UserPeopleAdapter userPeopleAdapter=chat.getMembers().get(i);
                    if(userPeopleAdapter.getNick().equals(userInformation.getNick()))
                        chat.getMembers().remove(i);
                    if(i==chat.getMembers().size()-1){
                        for(int s=0;s<chat.getMembers().size();s++){
                            UserPeopleAdapter userPeopleAdapter1=chat.getMembers().get(s);
                            firebaseModel.getUsersReference().child(userPeopleAdapter1.getNick())
                                    .child("Dialogs").child(chat.getChatId()).child("members").setValue(chat.getMembers());
                        }
                    }
                }
                firebaseModel.getUsersReference().child(userInformation.getNick()).child("Dialogs").child(chat.getChatId()).removeValue();
                dialog.dismiss();
                Toast.makeText(getContext(), R.string.chatDeleted, Toast.LENGTH_SHORT).show();
                RecentMethods.setCurrentFragment(DialogsFragment.newInstance(userInformation, bundle, MainFragment.newInstance(userInformation, bundle)),getActivity());
            }
        });

        dialog.show();
    }

}
