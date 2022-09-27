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
//        Float x=-0.01f;
//        Float y=0.21f;
//        Float z=-0.17f;
//        Float transformRatio=9.9f;
//        Float x1=0f;
//        Float y1=0f;
//        Float z1=0f;
//        Float transformRatio1=0f;//      \\\\\\\\       String uid8=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid7=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid9=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid10=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid8)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fwhite%20half%20glasses.png?alt=media&token=1fd95e29-4c0c-4002-ad9e-647092c3cdd2"
//                        , 40, "white half glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fwhite%20half%20glasses%20(1).glb?alt=media&token=c538aa38-ad96-4d41-91c0-819902ea2220",
//                        0, "glasses", uid8, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid9)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fred%20square%20glasses.png?alt=media&token=42044118-5de2-4195-902a-d24e12537564"
//                        , 40, "red square glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fred%20square%20glasses%20(1).glb?alt=media&token=02b8b24e-7fd8-4a98-b7df-e15988b1d4a6",
//                        0, "glasses", uid9, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid7)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fred%20oval%20glasses.png?alt=media&token=21c7a27e-95c2-4889-9bed-5252835cd387"
//                        , 40, "red oval glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fred%20oval%20glasses%20(1).glb?alt=media&token=eb894846-cf8f-4e5b-a262-510934c3c548",
//                        0, "glasses", uid7, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid10)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fpurple%20square%20glasses.png?alt=media&token=8af33dc0-414f-408c-9fa8-e0c18a253721"
//                        , 40, "purple square glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fpurple%20square%20glasses%20(1).glb?alt=media&token=94d6f7f0-1cac-49ca-a401-23a056c0edf9",
//                        0, "glasses", uid10, "no",buffer , x1,y1,z1,transformRatio1));
//
/////
//        String uid81=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid71=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid91=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid101=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid81)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fpink%20square%20glasses.png?alt=media&token=8c3c1ffd-ab0a-4000-986b-0727221fb820"
//                        , 40, "pink square glasses", 0, 0, "Sholly", "coin", "",  "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fpink%20square%20glasses%20(1).glb?alt=media&token=5c779702-71a8-493b-9521-81535748ce6e",
//                        0, "glasses", uid81, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid91)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fpink%20oval%20glasses.png?alt=media&token=5548833c-32cf-4337-823a-c1fd52348c75"
//                        , 40, "pink oval glasses", 0, 0, "Sholly", "coin", "",  "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fpink%20oval%20glasses%20(1).glb?alt=media&token=00dc6843-5b32-47bf-9e9e-f28f22726cd8",
//                        0, "glasses", uid91, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid71)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fgreen%20oval%20glasses.png?alt=media&token=f433b986-16f3-45a9-a73f-7097cb2a0efd"
//                        , 40, "green oval glasses", 0, 0, "Sholly", "coin", "",  "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fgreen%20oval%20glasses%20(1).glb?alt=media&token=bd0a201c-319c-4948-ac18-722e2c9bc6d9",
//                        0, "glasses", uid71, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid101)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fblue%20square%20glasses.png?alt=media&token=be138cb7-c667-41a6-8052-6c6dd1b542ab"
//                        , 40, "blue square glasses", 0, 0, "Sholly", "coin", "",  "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fblue%20square%20glasses%20(1).glb?alt=media&token=9b3fa6fe-210b-41cb-aff7-4753aecd5001",
//                        0, "glasses", uid101, "no",buffer , x1,y1,z1,transformRatio1));
//
//        String uid82=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid72=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid92=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid102=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid82)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fblue%20oval%20glasses.png?alt=media&token=512c2499-c3ef-4b50-ab7f-a66f6dd8d108"
//                        , 40, "blue oval glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fblue%20oval%20glasses%20(1).glb?alt=media&token=06575c34-7c52-4537-895b-e45f25770d62",
//                        0, "glasses", uid82, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid92)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fblue%20half%20glasses.png?alt=media&token=5c1cdd1e-655e-4bb9-ba21-6485cb0db379"
//                        , 40, "blue half glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fblue%20half%20glasses%20(1).glb?alt=media&token=cce62f08-896c-43de-a456-3a0342ce794f",
//                        0, "glasses", uid92, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid72)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fblack%20square%20glasses.png?alt=media&token=b70da888-8959-4441-99e9-66ad841be766"
//                        , 40, "black square glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fblack%20square%20glasses%20(1).glb?alt=media&token=b9761ae5-f70a-4f34-b0ff-395c9fddde7b",
//                        0, "glasses", uid72, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid102)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fblack%20oval%20glasses.png?alt=media&token=c34ee63c-bd06-450e-913f-92c6d5860d6c"
//                        , 40, "black oval glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fblack%20oval%20glasses%20(1).glb?alt=media&token=49bdf136-2950-4f43-9764-97196a77f1f4",
//                        0, "glasses", uid102, "no",buffer , x1,y1,z1,transformRatio1));
//
/////
//        String uid83=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid73=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        String uid93=firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").push().getKey();
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid83)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fblack%20half%20glasses.png?alt=media&token=3fc97988-3729-4835-b5e4-d8dd46e3be43"
//                        , 40, "black half glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fblack%20half%20glasses%20(1).glb?alt=media&token=9b6c6446-d060-4aaa-b51a-b22f79353467",
//                        0, "glasses", uid83, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid73)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fyellow%20square%20glasses.png?alt=media&token=d021e195-d65c-42be-98b6-c4b59e79a38a"
//                        , 40, "yellow square glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fyellow%20square%20glasses%20(1).glb?alt=media&token=37132fc6-5f0b-4e00-a5c1-18c1e0cb7229",
//                        0, "glasses", uid73, "no",buffer , x1,y1,z1,transformRatio1));
//        firebaseModel.getReference().child("AppData").child("Clothes").child("AllClothes").child(uid93)
//                .setValue(new Clothes("accessories", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fwhite%20square%20glasses.png?alt=media&token=e9963c67-fb5b-4039-a226-e1f6b0487af3"
//                        , 40, "white square glasses", 0, 0, "Sholly", "coin", "", "https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/3d%20models%2Fwhite%20square%20glasses%20(1).glb?alt=media&token=72abfbf5-8841-4e92-b2fe-758bb3d2eb79",
//                        0, "glasses", uid93, "no",buffer , x1,y1,z1,transformRatio1));
//






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
