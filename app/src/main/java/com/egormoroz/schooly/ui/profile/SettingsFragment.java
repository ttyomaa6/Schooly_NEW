package com.egormoroz.schooly.ui.profile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.Mining.Miner;
import com.egormoroz.schooly.ui.main.RegisrtationstartFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.profile.Wardrobe.CreateLookFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {


    String type,nick;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;
    GoogleSignInClient signInClient;
    GoogleSignInOptions gso;

    public SettingsFragment(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.type = type;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static SettingsFragment newInstance(String type, Fragment fragment
            ,UserInformation userInformation,Bundle bundle) {
        return new SettingsFragment(type,fragment,userInformation,bundle);

    }

    FirebaseModel firebaseModel=new FirebaseModel();
    TextView  userNick,userNumber,userPassword,changePassword,blackList,exitAccount,
            privacyPolicy,rules,support,saved;
    String userNickString;
    SwitchMaterial privateAccountSwitch,chatsSwitch,groupChatsSwitch,profileSwitch;
    boolean checkType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View root =inflater.inflate(R.layout.fragment_settings,container,false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        chatsSwitch=view.findViewById(R.id.chatsSwitch);
        groupChatsSwitch=view.findViewById(R.id.groupChatsSwitch);
        profileSwitch=view.findViewById(R.id.profileSwitch);
        privacyPolicy=view.findViewById(R.id.privacyPolicy);
        rules=view.findViewById(R.id.rules);
        saved=view.findViewById(R.id.saved);
        userNick=view.findViewById(R.id.userNick);
        support=view.findViewById(R.id.support);
        exitAccount=view.findViewById(R.id.exitAccount);
        changePassword=view.findViewById(R.id.changePassword);
        privateAccountSwitch=view.findViewById(R.id.privateAccountSwitch);
        blackList=view.findViewById(R.id.blackList);
        userPassword=view.findViewById(R.id.userPassword);
        userNumber=view.findViewById(R.id.userNumber);
        userNick.setText(nick);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("44838623612-du9vom4g3h9nvkoi4ml7aseaudolkoi1.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(getActivity(), gso);

        ImageView imageViewBack = view.findViewById(R.id.backtomainfromsettings);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(ProfileFragment.newInstance(type, nick,fragment,userInformation,bundle), getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                RecentMethods.setCurrentFragment(ProfileFragment.newInstance(type, nick, fragment,userInformation,bundle), getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(SavedFragment.newInstance(type,fragment,userInformation,bundle), getActivity());
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1RXfSdPJy_DcwD45m_u4jJX960wAtixPgwRBK_WGwJLE/edit?usp=sharing"));
                startActivity(browserIntent);
            }
        });

        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pages.flycricket.io/sholly/terms.html"));
                startActivity(browserIntent);
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(SupportFragment.newInstance(type, fragment, userInformation, bundle), getActivity());
            }
        });

        exitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        blackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(BlackListFragment.newInstance(type,fragment,userInformation,bundle), getActivity());
            }
        });


        checkRegistrationType();
        checkSwitches();
    }

    public void clearRequests(){
        firebaseModel.getUsersReference().child(nick).child("requests")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot= task.getResult();
                    ArrayList<Subscriber> subscribersList = new ArrayList<>();
                    for (DataSnapshot snap:snapshot.getChildren()){
                        Subscriber subscriber=new Subscriber();
                        subscriber.setSub(snap.getValue(String.class));
                        subscribersList.add(subscriber);
                    }
                    if(subscribersList.size()>0){
                        for (int i=0;i<subscribersList.size();i++){
                            Subscriber requestSub=subscribersList.get(i);
                            firebaseModel.getUsersReference().child(nick).child("requests")
                                    .child(requestSub.getSub()).removeValue();
                            firebaseModel.getReference().child("users").child(nick).child("subscribers")
                                    .child(requestSub.getSub()).setValue(requestSub.getSub());
                            firebaseModel.getReference().child("users").child(requestSub.getSub()).child("subscription")
                                    .child(nick).setValue(nick);
                        }
                    }
                }
            }
        });
    }

    public void clearRequestsNonts(){
        RecentMethods.getNontificationsList(nick, firebaseModel, new Callbacks.getNontificationsList() {
            @Override
            public void getNontificationsList(ArrayList<Nontification> nontifications) {
                for (int i=0;i<nontifications.size();i++){
                    Nontification nontification=nontifications.get(i);
                    if(nontification.getTypeView().equals("запрос")){
                        firebaseModel.getUsersReference().child(nick).child("nontifications")
                                .child(nontification.getUid()).child("typeView")
                                .setValue("обычный");
                    }
                }
            }
        });
    }

    public void checkRegistrationType(){
        if(userInformation.getPhone().equals("unknown")) {
            userNumber.setText(getContext().getResources().getText(R.string.loginwithGoogle));
        }else {
            userNumber.setText(userInformation.getPhone());
        }
        if(userInformation.getPassword().equals("unknown")) {
            userPassword.setText(getContext().getResources().getText(R.string.loginwithGoogle));
            changePassword.setVisibility(View.GONE);
        }else {
            userPassword.setText(userInformation.getPassword());
            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecentMethods.setCurrentFragment(PasswordFragment.newInstance(type,fragment,userInformation,bundle), getActivity());
                }
            });
        }
    }

    public  void checkSwitches(){
        if(userInformation.getChatsNontsType().equals("open")){
            chatsSwitch.setChecked(true);
        }else {
            chatsSwitch.setChecked(false);
        }

        chatsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkType=true;
                if (checkType=chatsSwitch.isChecked()){
                    firebaseModel.getUsersReference().child(nick)
                            .child("chatsNontsType").setValue("open");
                }else {
                    firebaseModel.getUsersReference().child(nick)
                            .child("chatsNontsType").setValue("close");
                }
                firebaseModel.getUsersReference().child(nick)
                        .child("chatsNontsType").get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                DataSnapshot snapshot= task.getResult();
                                userInformation.setChatsNontsType(snapshot.getValue(String.class));
                            }
                        });
            }
        });

        if(userInformation.getGroupChatsNontsType().equals("open")){
            groupChatsSwitch.setChecked(true);
        }else {
            groupChatsSwitch.setChecked(false);
        }

        groupChatsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkType=true;
                if (checkType=groupChatsSwitch.isChecked()){
                    firebaseModel.getUsersReference().child(nick)
                            .child("groupChatsNontsType").setValue("open");
                }else {
                    firebaseModel.getUsersReference().child(nick)
                            .child("groupChatsNontsType").setValue("close");
                }
                firebaseModel.getUsersReference().child(nick)
                        .child("groupChatsNontsType").get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                DataSnapshot snapshot= task.getResult();
                                userInformation.setGroupChatsNontsType(snapshot.getValue(String.class));
                            }
                        });
            }
        });

        if(userInformation.getProfileNontsType().equals("open")){
            profileSwitch.setChecked(true);
        }else {
            profileSwitch.setChecked(false);
        }

        profileSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkType=true;
                if (checkType=profileSwitch.isChecked()){
                    firebaseModel.getUsersReference().child(nick)
                            .child("profileNontsType").setValue("open");
                }else {
                    firebaseModel.getUsersReference().child(nick)
                            .child("profileNontsType").setValue("close");
                }
                firebaseModel.getUsersReference().child(nick)
                        .child("profileNontsType").get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                DataSnapshot snapshot= task.getResult();
                                userInformation.setProfileNontsType(snapshot.getValue(String.class));
                            }
                        });
            }
        });


        if(userInformation.getAccountType().equals("open")){
            privateAccountSwitch.setChecked(false);
        }else {
            privateAccountSwitch.setChecked(true);
        }

        privateAccountSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkType=true;
                if (checkType=privateAccountSwitch.isChecked()){
                    firebaseModel.getUsersReference().child(nick)
                            .child("accountType").setValue("close");
                }else {
                    firebaseModel.getUsersReference().child(nick)
                            .child("accountType").setValue("open");
                    clearRequests();
                    clearRequestsNonts();
                }
                firebaseModel.getUsersReference().child(nick)
                        .child("accountType").get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                DataSnapshot snapshot= task.getResult();
                                userInformation.setAccountType(snapshot.getValue(String.class));
                            }
                        });
            }
        });
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_layout_blacklist);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView complainTitle = dialog.findViewById(R.id.complainText);
        RelativeLayout no=dialog.findViewById(R.id.no);
        RelativeLayout yes=dialog.findViewById(R.id.yes);

        complainTitle.setText(getContext().getResources().getText(R.string.exitAccount)+"?");

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                signInClient.signOut().addOnCompleteListener(getActivity(),
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                ((MainActivity)getActivity()).stopHandler();
                                RecentMethods.setCurrentFragment(RegisrtationstartFragment.newInstance(userInformation,bundle), getActivity());
                            }
                        });
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
