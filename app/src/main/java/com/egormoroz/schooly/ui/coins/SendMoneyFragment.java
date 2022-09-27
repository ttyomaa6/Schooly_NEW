package com.egormoroz.schooly.ui.coins;

import android.os.Bundle;
import android.util.Log;
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

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.Subscriber;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.profile.ComplainFragmentToBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ServerValue;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class SendMoneyFragment extends Fragment {

    ImageView backToCoins;
    TextView otherUserNickText,balance;
    EditText sum;
    String otherUserNick,sumText,nick;
    long moneyBase,moneyBaseOther,sumLong;
    RelativeLayout transfer;

    FirebaseModel firebaseModel=new FirebaseModel();
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;

    public SendMoneyFragment(String otherUserNick,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.otherUserNick = otherUserNick;
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static SendMoneyFragment newInstance(String otherUserNick,Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new SendMoneyFragment(otherUserNick,fragment,userInformation,bundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sendcoins, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        firebaseModel.initAll();
        otherUserNickText=view.findViewById(R.id.toWhomTransfer);
        sum=view.findViewById(R.id.sum);
        balance=view.findViewById(R.id. balance);
        otherUserNickText.setText(otherUserNick);
        transfer=view.findViewById(R.id.createBigButtonRecycler);
        backToCoins=view.findViewById(R.id.backtocoins);
        balance.setText(String.valueOf(userInformation.getmoney()));
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMoney();
            }
        });
        backToCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(TransferMoneyFragment.newInstance(fragment,userInformation,bundle), getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(TransferMoneyFragment.newInstance(fragment,userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

    }

    public void sendMoney(){
        sumText=sum.getText().toString();
        if(sumText.contains("-")){
            Toast.makeText(getContext(), getContext().getResources().getText(R.string.wrongamountentered), Toast.LENGTH_SHORT).show();
        }else{
            if (sumText.length()==0){
                Toast.makeText(getContext(), getContext().getResources().getText(R.string.specifythetransferamount), Toast.LENGTH_SHORT).show();
            }else {
                sumLong=Long.parseLong(String.valueOf(sumText));
                if(sumLong==0){
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.transferamountmustbegreaterthanzero), Toast.LENGTH_SHORT).show();
                }else if(sumLong>userInformation.getmoney()){
                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.insufficientfundstotransfer), Toast.LENGTH_SHORT).show();
                }else if(sumLong<userInformation.getmoney() && !sumText.equals("0")){
                    firebaseModel.getUsersReference().child(otherUserNick).child("money").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                DataSnapshot snapshot=task.getResult();
                                moneyBaseOther=snapshot.getValue(Long.class);
                                firebaseModel.getUsersReference().child(otherUserNick).child("money")
                                        .setValue(sumLong+moneyBaseOther);
                                firebaseModel.getUsersReference().child(nick).child("money")
                                        .setValue(userInformation.getmoney()-sumLong);
                                Toast.makeText(getContext(), getContext().getResources().getText(R.string.translationdone), Toast.LENGTH_SHORT).show();
                                String num=firebaseModel.getUsersReference().child(otherUserNick).child("transferHistory")
                                        .push().getKey();
                                firebaseModel.getUsersReference().child(otherUserNick).child("transferHistory")
                                        .child(num).setValue(new Transfer(sumLong, nick, "from"));
                                firebaseModel.getUsersReference().child(nick).child("transferHistory")
                                        .child(num).setValue(new Transfer(sumLong, otherUserNick, "to"));
                                String uid=firebaseModel.getReference().child("users")
                                        .child(otherUserNick).child("nontifications").push().getKey();
                                firebaseModel.getReference().child("users")
                                        .child(otherUserNick).child("nontifications")
                                        .child(uid).setValue(new Nontification(nick,"не отправлено","перевод"
                                        , "",String.valueOf(sumLong)," ","не просмотрено",uid,0));
                                RecentMethods.setCurrentFragment(TransferMoneyFragment.newInstance(fragment,userInformation,bundle), getActivity());
                            }
                        }
                    });
                }
            }
        }
    }
}
