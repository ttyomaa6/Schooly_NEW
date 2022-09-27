package com.egormoroz.schooly.ui.profile;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.people.PeopleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class ComplainFragmentToBase extends Fragment {

    public ComplainFragmentToBase(String otherUserNick,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.otherUserNick = otherUserNick;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static ComplainFragmentToBase newInstance(String otherUserNick,Fragment fragment
            ,UserInformation userInformation,Bundle bundle) {
        return new ComplainFragmentToBase(otherUserNick,fragment,userInformation,bundle);
    }

    Bundle bundle;
    UserInformation userInformation;
    Fragment fragment;
    FirebaseModel firebaseModel=new FirebaseModel();
    RecyclerView recyclerView;
    TextView reasonText;
    RelativeLayout sendReason;
    ImageView back;
    String reasonTextString,otherUserNick,descriptionText,nick;
    EditText addDescriptionEdit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View root =inflater.inflate(R.layout.fragment_complaintobase,container,false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();

        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        reasonText=view.findViewById(R.id.reasonText);
        sendReason=view.findViewById(R.id.sendReasons);
        addDescriptionEdit=view.findViewById(R.id.addDescriptionEdit);
        back=view.findViewById(R.id.backtoOtherUser);
        ComplainAdapter.complain(new ComplainAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Reason reason) {
                reasonTextString=reason.getReason();
            }
        });
        reasonText.setText(reasonTextString);
        sendReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionText=addDescriptionEdit.getText().toString();
                String uid=firebaseModel.getReference().child("AppData").child("complains").push().getKey();
                firebaseModel.getReference().child("AppData").child("complains").child(uid)
                        .setValue(new Complain(otherUserNick,nick, reasonTextString,descriptionText,uid,new NewsItem()));
                Toast.makeText(getContext(), R.string.complaintsent, Toast.LENGTH_SHORT).show();
                RecentMethods.setCurrentFragment(ProfileFragment.newInstance("other", otherUserNick, fragment,userInformation,bundle), getActivity());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(ComplainFragment.newInstance(otherUserNick,fragment,userInformation,bundle), getActivity());
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(ComplainFragment.newInstance(otherUserNick,fragment,userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
}
