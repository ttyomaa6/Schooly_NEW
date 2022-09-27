package com.egormoroz.schooly.ui.chat;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class

DialogsFragment extends Fragment {

    ImageView backToMainFromChat;
    String getEditText;
    EditText editText;
    TabLayout tabLayout;
    RecyclerView recyclerView;
    FirebaseModel firebaseModel=new FirebaseModel();
    TextView noChats;
    private ViewPager2 viewPager;
    ArrayList<Chat> searchDialogsArrayList;
    ArrayList<Chat> allChats=new ArrayList<>();
    UserInformation userInformation;
    FragmentAdapter fragmentAdapter;
    Bundle bundle;
    int tabLayoutPosition;
    Fragment fragment;

    public DialogsFragment(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.fragment=fragment;
    }

    public static DialogsFragment newInstance(UserInformation userInformation,Bundle bundle,Fragment fragment) {
        return new DialogsFragment(userInformation,bundle,fragment);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putString("EDIT_DIALOGS_TAG",editText.getText().toString().trim());
        bundle.putInt("TAB_INT_DIALOGS", tabLayoutPosition);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_default_dialogs, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        firebaseModel.initAll();
        viewPager=view.findViewById(R.id.frcont);
        tabLayout = (TabLayout) view.findViewById(R.id.tabschat);
        noChats=view.findViewById(R.id.noChats);
        recyclerView=view.findViewById(R.id.recyclerView);
        editText=view.findViewById(R.id.editText);
        loadChats();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                RecentMethods.setCurrentFragment(fragment,getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        if (bundle!=null){
            tabLayoutPosition=bundle.getInt("TAB_INT_DIALOGS");
            if(bundle.getString("EDIT_DIALOGS_TAG")!=null){
                String bundleEditText=bundle.getString("EDIT_DIALOGS_TAG").trim();
                if(bundleEditText.length()!=0){
                    editText.setText(bundleEditText);
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    searchChats(bundleEditText.toLowerCase());
                }
            }
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getEditText=editText.getText().toString().toLowerCase();
                if (getEditText.length()>0){
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    searchChats(getEditText.toLowerCase());

                }else if(getEditText.length()==0){
                    viewPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    noChats.setVisibility(View.GONE);
                    FragmentManager fm = getChildFragmentManager();
                    fragmentAdapter = new FragmentAdapter(fm, getLifecycle());
                    viewPager.setAdapter(fragmentAdapter);
                    viewPager.setCurrentItem(tabLayoutPosition, false);

                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            tabLayoutPosition=tab.getPosition();
                            viewPager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                    tabLayout.selectTab(tabLayout.getTabAt(tabLayoutPosition));
                    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            tabLayoutPosition=position;
                            tabLayout.selectTab(tabLayout.getTabAt(position));
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        FragmentManager fm = getChildFragmentManager();
        fragmentAdapter = new FragmentAdapter(fm, getLifecycle());
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(tabLayoutPosition, false);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.personal));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.talks));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayoutPosition=tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.selectTab(tabLayout.getTabAt(tabLayoutPosition));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayoutPosition=position;
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        backToMainFromChat=(ImageView) view.findViewById(R.id.backtomainfromchat);
        backToMainFromChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation, bundle), getActivity());
            }
        });
    }

    public void searchChats(String textEdit){
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
                            //chat.setUnreadMessages(snap.child("unreadMessages").getValue(Long.class));
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
                            DialogAdapter dialogAdapter=new DialogAdapter(searchDialogsArrayList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(dialogAdapter);
                            noChats.setVisibility(View.GONE);
                            DialogAdapter.ItemClickListener itemClickListener=new DialogAdapter.ItemClickListener() {
                                @Override
                                public void onItemClick(Chat chat) {
                                    Intent chatIntent = new Intent(getContext(), MessageFragment.class);
                                    chatIntent.putExtra("curUser", userInformation.getNick());
                                    chatIntent.putExtra("othUser", chat.getName());
                                    startActivity(chatIntent);
                                }
                            };
                            dialogAdapter.setClickListener(itemClickListener);
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
                DialogAdapter dialogAdapter=new DialogAdapter(searchDialogsArrayList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(dialogAdapter);
                noChats.setVisibility(View.GONE);
                DialogAdapter.ItemClickListener itemClickListener=new DialogAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(Chat chat) {
                        RecentMethods.setCurrentFragment(MessageFragment.newInstance(userInformation, bundle, DialogsFragment.newInstance(userInformation, bundle,fragment),chat), getActivity());
                    }
                };
                dialogAdapter.setClickListener(itemClickListener);
            }
        }
    }

    public class FragmentAdapter extends FragmentStateAdapter {

        public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment ( int position){
            switch (position){
                case 0:
                    return new ChatsFragment(userInformation,bundle,fragment);
                case 1:
                    return new GroupsFragment(userInformation,bundle,fragment);
            }
            return null;
        }


        @Override
        public int getItemCount() {
            return 2;
        }

    }

    public  void loadChats(){
        if(userInformation.getChats()==null || userInformation.getTalksArrayList()==null){
            RecentMethods.getDialogs(userInformation.getNick(), firebaseModel, new Callbacks.loadDialogs() {
                @Override
                public void LoadData(ArrayList<Chat> dialogs,ArrayList<Chat> talksArrayList) {
                    userInformation.setChats(dialogs);
                    userInformation.setTalksArrayList(talksArrayList);
                    allChats.addAll(dialogs);
                    allChats.addAll(talksArrayList);
                }
            });
        }else{
            allChats.addAll(userInformation.getChats());
            allChats.addAll(userInformation.getTalksArrayList());
        }
    }
}

