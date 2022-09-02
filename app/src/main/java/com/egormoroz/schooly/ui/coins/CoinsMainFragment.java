package com.egormoroz.schooly.ui.coins;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FacePart;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.Nontification;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.CreateCharacter.CharacterAdapter;
import com.egormoroz.schooly.ui.main.CreateCharacter.EyebrowsFragment;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.Mining.Miner;
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;

import java.nio.Buffer;

public class CoinsMainFragment extends Fragment {
    private FirebaseModel firebaseModel = new FirebaseModel();
    LinearLayout oneLinear,twoLinear,fiveLinear
            ,sevenLinear,tenLinear,twentyLinear;
    TextView oneS,twoS,fiveS
            ,sevenS,tenS,twentyS;
    RelativeLayout transferMoney;
    TextView oneD,twoD,fiveD
            ,sevenD,tenD,twentyD;
    ImageView oneImage,twoImage,fiveImage
            ,sevenImage,tenImage,twentyImage;
    String nick;
    UserInformation userInformation;
    Bundle bundle;
    Buffer buffer;
    long adCount=0;
    RelativeLayout adRelative,allPremium;

    public CoinsMainFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static CoinsMainFragment newInstance(UserInformation userInformation,Bundle bundle) {
        return new CoinsMainFragment(userInformation,bundle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coins, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.VISIBLE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        OnBackPressedCallback callback1 = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation, bundle), getActivity());
            }
        };
        adRelative=view.findViewById(R.id.adRelative);
        adRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseModel.getUsersReference().child(userInformation.getNick()).child("adCount")
                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot=task.getResult();
                            if(snapshot.exists()){
                                adCount=snapshot.getValue(Long.class);
                            }
                            if(adCount<5){
                                RecentMethods.setCurrentFragment(AdsFragment.newInstance(CoinsMainFragment.newInstance(userInformation, bundle), userInformation, bundle), getActivity());
                            }else{
                                Toast.makeText(getContext(),R.string.watch5ad , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        firebaseModel.initAll();
        Float x=-0.01f;
        Float y=0.21f;
        Float z=-0.17f;
        Float transformRatio=9.9f;
        Float x1=0f;
        Float y1=0f;
        Float z1=0f;
        Float transformRatio1=0f;


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback1);
        oneLinear=view.findViewById(R.id.oneThousand);
        twoLinear=view.findViewById(R.id.twoThousand);
        fiveLinear=view.findViewById(R.id.fiveThousand);
        sevenLinear=view.findViewById(R.id.sevenThousand);
        tenLinear=view.findViewById(R.id.tenThousand);
        twentyLinear=view.findViewById(R.id.twentyThousand);
        transferMoney=view.findViewById(R.id.transferMoney);
        oneS=view.findViewById(R.id.oneCoinsText);
        twoS=view.findViewById(R.id.twoCoinsText);
        fiveS=view.findViewById(R.id.fiveCoinsText);
        sevenS=view.findViewById(R.id.sevenCoinsText);
        tenS=view.findViewById(R.id.tenCoinsText);
        twentyS=view.findViewById(R.id.twentyCoinsText);
        oneD=view.findViewById(R.id.oneDollarText);
        twoD=view.findViewById(R.id.twoDollarText);
        fiveD=view.findViewById(R.id.fiveDollarText);
        sevenD=view.findViewById(R.id.sevenDollarText);
        tenD=view.findViewById(R.id.tenDollarText);
        twentyD=view.findViewById(R.id.twentyDollarText);
        oneImage=view.findViewById(R.id.oneCoinsImage);
        twoImage=view.findViewById(R.id.twoCoinsImage);
        fiveImage=view.findViewById(R.id.threeCoinsImage);
        sevenImage=view.findViewById(R.id.sevenCoinsImage);
        tenImage=view.findViewById(R.id.tenCoinsImage);
        twentyImage=view.findViewById(R.id.twentyCoinsImage);
        allPremium=view.findViewById(R.id.allPremium);
        transferMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(TransferMoneyFragment.newInstance(CoinsMainFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
            }
        });
        allPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        oneLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        twoLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        fiveLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        sevenLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        tenLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        twentyLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    public void showDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_layout_sell_clothes);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout sellRelative=dialog.findViewById(R.id.sellRelative);


        sellRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
