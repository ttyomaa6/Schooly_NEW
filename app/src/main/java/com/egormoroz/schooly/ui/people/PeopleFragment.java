package com.egormoroz.schooly.ui.people;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Person;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.ViewingClothesNews;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.Buffer;
import java.util.ArrayList;

public class PeopleFragment extends Fragment {
    ArrayList<UserInformation> listAdapterPeople=new ArrayList<UserInformation>();
    RecyclerView peopleRecyclerView;
    EditText searchUser;
    String userName,userNameToProfile,nick;
    ArrayList<UserPeopleAdapter> userFromBase,searchUserFromBase;
    TextView userNotSearch;
    UserInformation userInformation;
    Bundle bundle;
    ArrayList<UserPeopleAdapter> recommendationList=new ArrayList<>();

    public PeopleFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static PeopleFragment newInstance( UserInformation userInformation,Bundle bundle) {
        return new PeopleFragment(userInformation,bundle);

    }
    FirebaseModel firebaseModel=new FirebaseModel();
    static Buffer b;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putString("EDIT_SEARCH_PEOPLE_TAG",searchUser.getText().toString().trim());
        bundle.putSerializable("SEARCH_PEOPLE_LIST", searchUserFromBase);
        if(recommendationList.size()!=0)
        bundle.putSerializable("RECOMMENDATIONPEOPLELIST",recommendationList);
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        peopleRecyclerView=view.findViewById(R.id.peoplerecycler);
        OnBackPressedCallback callback1 = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation, bundle), getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback1);
        searchUser=view.findViewById(R.id.searchuser);
        userNotSearch=view.findViewById(R.id.notSearch);
        firebaseModel.initAll();
        setPeopleData();
            if(bundle.getString("EDIT_SEARCH_PEOPLE_TAG")!=null){
                String searchText=bundle.getString("EDIT_SEARCH_PEOPLE_TAG").trim();
                if(searchText.length()>0){
                    searchUser.setText(searchText);
                    searchUserFromBase= (ArrayList<UserPeopleAdapter>) bundle.getSerializable("SEARCH_PEOPLE_LIST");
                    if(searchUserFromBase.size()==0){
                        userNotSearch.setVisibility(View.VISIBLE);
                        peopleRecyclerView.setVisibility(View.GONE);
                    }else{
                        peopleRecyclerView.setVisibility(View.VISIBLE);
                        userNotSearch.setVisibility(View.GONE);
                        PeopleAdapter peopleAdapter = new PeopleAdapter(searchUserFromBase,userInformation);
                        peopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        peopleRecyclerView.setAdapter(peopleAdapter);
                        PeopleAdapter.ItemClickListener clickListener =
                                new PeopleAdapter.ItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position,String avatar,String bio) {
                                        UserPeopleAdapter user = peopleAdapter.getItem(position);
                                        userNameToProfile = user.getNick();
                                        if (userNameToProfile.equals(nick)) {
                                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", nick, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
                                        } else {
                                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle),
                                                    getActivity());
                                            firebaseModel.getReference("users").child(nick).child("alreadySearched").child(userNameToProfile)
                                                    .setValue(new UserPeopleAdapter(userNameToProfile, avatar, bio));
                                        }
                                    }
                                };
                        peopleAdapter.setClickListener(clickListener);
                    }
                }
                else {
                    loadRecommendations();
                }
            }else  {
                loadRecommendations();
            }
        getUsersNicks();
        initUserEnter();
    }

    public void getUsersNicks(){
        firebaseModel.getReference("usersNicks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userFromBase = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    UserPeopleAdapter upa = new UserPeopleAdapter();
                    upa.setNick(snap.child("nick").getValue(String.class));
                    upa.setBio(snap.child("bio").getValue(String.class));
                    upa.setAvatar(snap.child("avatar").getValue(String.class));
                    userFromBase.add(upa);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setAlreadySearchedInAdapter(){
        peopleRecyclerView.setVisibility(View.VISIBLE);
        userNotSearch.setVisibility(View.GONE);
        if(userInformation.getAlreadySearched()==null){
            checkAlreadySearchedFromBase();
        }
        else {
            AlreadySearchAdapter alreadySearchAdapter=new AlreadySearchAdapter(userInformation.getAlreadySearched(),userInformation);
            peopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            peopleRecyclerView.setAdapter(alreadySearchAdapter);
            AlreadySearchAdapter.ItemClickListener itemClickListener=new AlreadySearchAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position,String type) {
                    UserPeopleAdapter user = alreadySearchAdapter.getItem(position);
                    userNameToProfile = user.getNick();
                    if(type.equals("profile")){
                        if (userNameToProfile.equals(nick)) {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", nick, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
                        }
                        else {
                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle),
                                    getActivity());
                        }
                    }
                    else {
                        firebaseModel.getUsersReference().child(nick).child("alreadySearched")
                                .child(userNameToProfile).removeValue();
                    }
                }
            };
            alreadySearchAdapter.setClickListener(itemClickListener);
        }
    }

    public void checkAlreadySearchedFromBase(){
        RecentMethods.getAlreadySearched(nick, firebaseModel, new Callbacks.GetAlreadySearched() {
            @Override
            public void getAlreadySearched(ArrayList<UserPeopleAdapter> searchedUserFromBase) {
                userInformation.setAlreadySearched(searchedUserFromBase);
                AlreadySearchAdapter alreadySearchAdapter=new AlreadySearchAdapter(searchedUserFromBase,userInformation);
                peopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                peopleRecyclerView.setAdapter(alreadySearchAdapter);
                AlreadySearchAdapter.ItemClickListener itemClickListener=new AlreadySearchAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position,String type) {
                        UserPeopleAdapter user = alreadySearchAdapter.getItem(position);
                        userNameToProfile = user.getNick();
                        if(type.equals("profile")){
                            if (userNameToProfile.equals(nick)) {
                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", nick, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
                            }
                            else {
                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle),
                                        getActivity());
                            }
                        }
                        else {
                            firebaseModel.getUsersReference().child(nick).child("alreadySearched")
                                    .child(userNameToProfile).removeValue();
                        }
                    }
                };
                alreadySearchAdapter.setClickListener(itemClickListener);
            }
        });
    }
    public void setPeopleData(){
        listAdapterPeople.add(new UserInformation("nick", "fidjfif", "gk",
                "6", "password", "Helicopter", 1000, new ArrayList<>(),new ArrayList<>(),1,100,0, new ArrayList<>()
                , new ArrayList<>(), ""," ","open","open","open","open",
                new ArrayList<>(),"regular", new ArrayList<>(),0,new ArrayList<>(),new ArrayList<>()
                ,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()
                ,new ArrayList<Clothes>(),new Person(new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart(), new FacePart("", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fma.glb?alt=media&token=f7430695-13cb-4365-8910-c61b59a96acf", "",b ,  "",0f,0f,0f),
                new FacePart(), new FacePart()),
                new ArrayList<>(),new ArrayList<>(),"","",new ArrayList<>()));
    }
    public void initUserEnter(){
        searchUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(searchUser.getText().toString().length()==0)
                    setAlreadySearchedInAdapter();
                }
            }
        });
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (String.valueOf(searchUser.getText()).trim().length()==0){
                    setAlreadySearchedInAdapter();
                }else {
                    userName = String.valueOf(searchUser.getText()).trim();
                    userName = userName.toLowerCase();
                    searchUserFromBase=new ArrayList<>();
                    for (int s=0;s<userFromBase.size();s++) {
                        UserPeopleAdapter upa = userFromBase.get(s);
                        String nickName = upa.getNick();
                        String nick1 = nickName;
                        int valueLetters = userName.length();
                        nick1 = nick1.toLowerCase();
                        if (nick1.length() < valueLetters) {
                            if (nick1.equals(userName))
                                searchUserFromBase.add(upa);
                        } else {
                            nick1 = nick1.substring(0, valueLetters);
                            if (nick1.equals(userName))
                                searchUserFromBase.add(upa);
                        }

                    }
                    if(searchUserFromBase.size()==0){
                        userNotSearch.setVisibility(View.VISIBLE);
                        peopleRecyclerView.setVisibility(View.GONE);
                    }else{
                        peopleRecyclerView.setVisibility(View.VISIBLE);
                        userNotSearch.setVisibility(View.GONE);
                        PeopleAdapter peopleAdapter = new PeopleAdapter(searchUserFromBase,userInformation);
                        peopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        peopleRecyclerView.setAdapter(peopleAdapter);
                        PeopleAdapter.ItemClickListener clickListener =
                                new PeopleAdapter.ItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position,String avatar,String bio) {
                                        UserPeopleAdapter user = peopleAdapter.getItem(position);
                                        userNameToProfile = user.getNick();
                                        if (userNameToProfile.equals(nick)) {
                                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", nick, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
                                        } else {
                                            RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle),
                                                    getActivity());
                                            firebaseModel.getReference("users").child(nick).child("alreadySearched").child(userNameToProfile)
                                                    .setValue(new UserPeopleAdapter(userNameToProfile, avatar, bio));
                                        }
                                    }
                                };
                        peopleAdapter.setClickListener(clickListener);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void loadRecommendations(){
        Log.d("#####", "G G11 ");
        if(bundle.getSerializable("RECOMMENDATIONPEOPLELIST")==null){
//            RecomendationThread getRecThread = new RecomendationThread(nick, userInformation, new Callbacks.getRecommendationsThread() {
//                            @Override
//                            public void getRecommendationsInterface(ArrayList<UserPeopleAdapter> recommendationsList) {
//                                Log.d("#####", "G G "+recommendationsList);
//                                if(recommendationsList.size()==0 || recommendationsList.size()< 14){
//                                    firebaseModel.getReference().child("usersNicks").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
//                                           if(task.isSuccessful()){
//                                               DataSnapshot snapshot=task.getResult();
//                                               ArrayList <UserPeopleAdapter> userPeopleAdapters=new ArrayList<>();
//                                               for(  DataSnapshot snap  :snapshot.getChildren()) {
//                                                    UserPeopleAdapter userPeopleAdapter=snap.getValue( UserPeopleAdapter.class);
//                                                    userPeopleAdapters.add(userPeopleAdapter);
//                                                    if(userPeopleAdapters.size()==49){
//                                                        break;
//                                                    }
//                                               }
//                                               initRecommendationAdapter(userPeopleAdapters);
//                                           }
//                                        }
//                                    });
//                                }else{
//                                    initRecommendationAdapter(recommendationsList);
//                                }
//                            }
//                        });
            firebaseModel.getReference().child("usersNicks").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        DataSnapshot snapshot=task.getResult();
                        ArrayList <UserPeopleAdapter> userPeopleAdapters=new ArrayList<>();
                        for(  DataSnapshot snap  :snapshot.getChildren()) {
                            UserPeopleAdapter userPeopleAdapter=snap.getValue( UserPeopleAdapter.class);
                            if(!userInformation.getSubscription().contains(userPeopleAdapter.getNick()) && !userInformation.getNick().equals(userPeopleAdapter.getNick()))userPeopleAdapters.add(userPeopleAdapter);
                            if(userPeopleAdapters.size()==49){
                                break;
                            }
                        }
                        initRecommendationAdapter(userPeopleAdapters);
                    }
                }
            });
        }else {
            recommendationList= (ArrayList<UserPeopleAdapter>) bundle.getSerializable("RECOMMENDATIONPEOPLELIST");
            initRecommendationAdapter(recommendationList);
        }
    }

    public void initRecommendationAdapter(ArrayList<UserPeopleAdapter> recommendationsList){
        RecomendationAdapter recomendationAdapter=new RecomendationAdapter(recommendationsList,userInformation);
        peopleRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        peopleRecyclerView.setAdapter(recomendationAdapter);
        recommendationList=recommendationsList;
        RecomendationAdapter.ItemClickListener itemClickListener=new RecomendationAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String avatar, String bio) {
                UserPeopleAdapter user = recomendationAdapter.getItem(position);
                userNameToProfile = user.getNick();
                if (userNameToProfile.equals(nick)) {
                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("userback", nick, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
                }
                else {
                    RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", userNameToProfile, PeopleFragment.newInstance(userInformation,bundle),userInformation,bundle),
                            getActivity());
                }
            }
        };
        recomendationAdapter.setClickListener(itemClickListener);
    }
}