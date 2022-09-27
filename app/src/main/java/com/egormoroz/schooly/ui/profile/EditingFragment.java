package com.egormoroz.schooly.ui.profile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.CreateCharacter.CreateCharacterFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

public class EditingFragment extends Fragment {
    FirebaseModel firebaseModel=new FirebaseModel();
    EditText nickEdit,bioEdit;
    String nickname,nick;
    RelativeLayout agree;
    String type;
    ImageView personImage;
    TextView textViewEditFace;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;

    public EditingFragment(String type,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.type = type;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static EditingFragment newInstance(String type, Fragment fragment, UserInformation userInformation,Bundle bundle) {
        return new EditingFragment(type,fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editing, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        textViewEditFace=view.findViewById(R.id.editing);
        textViewEditFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(CreateCharacterFragment.newInstance(userInformation, bundle,
                        EditingFragment.newInstance(type, fragment, userInformation, bundle), "editing"), getActivity());
            }
        });
        ImageView arrowtoprofileediting = view.findViewById(R.id.back_toprofile);
        arrowtoprofileediting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(ProfileFragment.newInstance(type, nick,fragment,userInformation,bundle), getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(ProfileFragment.newInstance(type, nick,fragment,userInformation,bundle), getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        personImage=view.findViewById(R.id.personImage);
        Picasso.get().load(userInformation.getPersonImage()).into(personImage);
        nickEdit=view.findViewById(R.id.edittextnickname);
        bioEdit=view.findViewById(R.id.edittextbio);
        agree=view.findViewById(R.id.agree);
        nickEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        nickEdit.setText(nick);
        nickname=nick;
        bioEdit.setText(userInformation.getBio());
        changeNick();
        changeBio();
    }

    public void changeNick(){
        nickEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void changeBio(){
        agree.setVisibility(View.VISIBLE);
        agree.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(bioEdit.getText().toString().length()>=300){
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.profiledescriptioncannotbemorethan200characters), Toast.LENGTH_SHORT).show();
                }else if (userInformation.getBio().equals(bioEdit.getText().toString())){
                    Toast.makeText(getContext(), R.string.nochangeshavebeenintroduced, Toast.LENGTH_SHORT).show();
                }else if(!userInformation.getBio().equals(bioEdit.getText().toString()) && bioEdit.getText().toString().length()<300){
                    String bioText= String.valueOf(bioEdit.getText().toString().trim());
                    firebaseModel.getUsersReference().child(nick).child("bio").setValue(bioText);
                    Toast.makeText(getContext(), R.string.changessaved, Toast.LENGTH_SHORT).show();
                    firebaseModel.getUsersReference().child(nick).child("bio").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                userInformation.setBio(snapshot.getValue(String.class));
                                RecentMethods.setCurrentFragment(ProfileFragment.newInstance(type, nick,fragment,userInformation,bundle), getActivity());
                            }
                        }
                    });
                }

            }
        });
    }

    public void showDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout Relative=dialog.findViewById(R.id.Delete_relative_layout);
        TextView textView=dialog.findViewById(R.id.Text);
        textView.setText(R.string.changenotavailable);

        Relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}