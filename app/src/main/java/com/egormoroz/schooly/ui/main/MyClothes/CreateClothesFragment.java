package com.egormoroz.schooly.ui.main.MyClothes;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class CreateClothesFragment extends Fragment {

    FirebaseModel firebaseModel=new FirebaseModel();
    EditText editTextClothes,editClothesPrice,addDescriptionEdit;
    ImageView addModelFile,addModelImage,modelPhoto;
    TextView before,criteria
            ,noTitle,noModel,noPhoto,noSum,exclusivePremium,modelFile;
    RelativeLayout publish;
    RadioGroup radioGroup,radioGroupCurrency,radioGroupExclusive;
    private String checker = "", myUrl = "";
    private Uri fileUri;
    private StorageTask uploadTask;
    RadioButton radioButton1,radioButton2,radioButton3,radioButton4
            ,radioButton5,radioButton6,radioButton7,radioButton8
            ,radioButton9,radioButton10,radioButton11
            ,radioButton12,radioButton13,radioButtonCoin,radioButtonDollar
            ,radioButtonExclusiveYes,radioButtonExclusiveNo;
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;
    String premiumType,modelApplication,imageApplication,currencyType,bodyType,type,exclusiveType
            ,nick;

    public CreateClothesFragment(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment = fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static CreateClothesFragment newInstance(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new CreateClothesFragment(fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_createclothes, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putString("EDIT_CLOTHES_NAME_TAG",editTextClothes.getText().toString().trim());
        bundle.putString("EDIT_CLOTHES_PRICE_TAG", editClothesPrice.getText().toString().trim());
        bundle.putString("EDIT_CLOTHES_DESCRIPTION_TAG", addDescriptionEdit.getText().toString().trim());
        bundle.putString("MODEL_CLOTHES_REQUEST", modelApplication);
        bundle.putString("IMAGE_CLOTHES_REQUEST", imageApplication);
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        editClothesPrice=view.findViewById(R.id.editClothesPrice);
        editTextClothes=view.findViewById(R.id.editTextClothes);
        addDescriptionEdit=view.findViewById(R.id.addDescriptionEdit);
        radioGroupCurrency=view.findViewById(R.id.radioGroupCurrency);
        addModelFile=view.findViewById(R.id.addModelFile);
        addModelImage=view.findViewById(R.id.addModelImage);
        modelPhoto=view.findViewById(R.id.modelPhoto);
        radioGroupExclusive=view.findViewById(R.id.radioGroupExclusive);
        radioButtonExclusiveYes=view.findViewById(R.id.radioButtonExclusiveYes);
        radioButtonExclusiveNo=view.findViewById(R.id.radioButtonExclusiveNo);
        before=view.findViewById(R.id.before);
        criteria=view.findViewById(R.id.criteria);
        noPhoto=view.findViewById(R.id.noPhoto);
        noModel=view.findViewById(R.id.noFile);
        noSum=view.findViewById(R.id.noSum);
        exclusivePremium=view.findViewById(R.id.exclusivePremium);
        modelFile=view.findViewById(R.id.modelFile);
        noTitle=view.findViewById(R.id.noEnterTitleText);
        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(CriteriaFragment.newInstance(fragment,userInformation,bundle), getActivity());
            }
        });
        criteria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(CriteriaFragment.newInstance(fragment,userInformation,bundle), getActivity());
            }
        });
        publish=view.findViewById(R.id.publish);
        radioGroup=view.findViewById(R.id.radioGroup);
        radioButton1=view.findViewById(R.id.radio_button_1);
        radioButton2=view.findViewById(R.id.radio_button_2);
        radioButton3=view.findViewById(R.id.radio_button_3);
        radioButton4=view.findViewById(R.id.radio_button_4);
        radioButton5=view.findViewById(R.id.radio_button_5);
        radioButton6=view.findViewById(R.id.radio_button_6);
        radioButton7=view.findViewById(R.id.radio_button_7);
        radioButton8=view.findViewById(R.id.radio_button_8);
        radioButton9=view.findViewById(R.id.radio_button_9);
        radioButton10=view.findViewById(R.id.radio_button_10);
        radioButton11=view.findViewById(R.id.radio_button_11);
        radioButton12=view.findViewById(R.id.radio_button_12);
        radioButton13=view.findViewById(R.id.radio_button_13);
        radioButtonCoin=view.findViewById(R.id.schoolyCoinRadio);
        radioButtonDollar=view.findViewById(R.id.dollarRadio);
        addModelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "image";
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 443);
            }
        });
        addModelFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "model";
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 443);
            }
        });

        ImageView backtoMyClothes=view.findViewById(R.id.back_tomyclothes);
        backtoMyClothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
                    @Override
                    public void PassUserNick(String nick) {
                        RecentMethods.setCurrentFragment(fragment, getActivity());
                    }
                });
            }
        });
        getCreateClothesBundle();
        firebaseModel.getUsersReference().child(nick)
                .child("version").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot= task.getResult();
                    premiumType=snapshot.getValue(String.class);
                }
            }
        });

        radioButtonDollar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(premiumType.equals("premium")){
                    radioButtonDollar.setChecked(true);
                }else {
                    radioButtonDollar.setChecked(false);
                    radioButtonCoin.setChecked(true);
                    showDialogDollarCurrency();
                }
            }
        });
        radioGroupExclusive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int idExclusive=radioGroupExclusive.getCheckedRadioButtonId();
                switch(idExclusive){
                    case R.id.radioButtonExclusiveYes:
                        exclusivePremium.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radioButtonExclusiveNo:
                        exclusivePremium.setVisibility(View.GONE);
                        break;
                }
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextClothes.getText().toString().length()==0){
                    noTitle.setVisibility(View.VISIBLE);
                }else {
                    noTitle.setVisibility(View.GONE);
                }
                if(editClothesPrice.getText().toString().length()==0 ||
                        editClothesPrice.getText().toString().equals("0") ){
                    noSum.setVisibility(View.VISIBLE);
                }else {
                    if(editClothesPrice.getText().toString().contains("-")){
                        noSum.setVisibility(View.VISIBLE);
                        noSum.setText(getContext().getResources().getText(R.string.incorrectlyenteredclothingprice));
                    }else {
                        noSum.setVisibility(View.GONE);
                    }
                }
                if(modelPhoto.getVisibility()==View.GONE){
                    noPhoto.setVisibility(View.VISIBLE);
                }else {
                    noPhoto.setVisibility(View.GONE);
                }
                if(editTextClothes.getText().toString().length()>0 && editClothesPrice.getText().toString().length()>0 &&
                        !editClothesPrice.getText().toString().equals("0")&& modelPhoto.getVisibility()==View.VISIBLE
                        &&  !editClothesPrice.getText().toString().contains("-")
                ){
                    showDialogSendClothes();
                }else {
                    showDialog();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 443 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            if (checker.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images");

                DatabaseReference userMessageKeyRef = firebaseModel.getUsersReference().child(nick).child("imageApplication").push();
                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "png");
                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        firebaseModel.getUsersReference().child(nick).child("imageApplication").setValue(myUrl);

                        firebaseModel.getUsersReference().child(nick).child("imageApplication").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    DataSnapshot snapshot= task.getResult();
                                    modelPhoto.setVisibility(View.VISIBLE);
                                    imageApplication=snapshot.getValue(String.class);
                                    Picasso.get().load(imageApplication).into(modelPhoto);
                                    noPhoto.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });

            }
            else if (checker.equals("model")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("model");

                DatabaseReference userMessageKeyRef = firebaseModel.getUsersReference().child(nick).child("modelApplication").push();
                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "glb");
                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        firebaseModel.getUsersReference().child(nick).child("modelApplication").setValue(myUrl);

                        firebaseModel.getUsersReference().child(nick).child("modelApplication").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    DataSnapshot snapshot= task.getResult();
                                    modelApplication=snapshot.getValue(String.class);
                                    noModel.setVisibility(View.GONE);
                                    modelFile.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }
                });
            }
        }
    }

    public void getCreateClothesBundle(){
        if(bundle!=null){
            if(bundle.getString("EDIT_CLOTHES_NAME_TAG")!=null){
                editTextClothes.setText(bundle.getString("EDIT_CLOTHES_NAME_TAG"));
            }
            if(bundle.getString("EDIT_CLOTHES_PRICE_TAG")!=null){
                editClothesPrice.setText(bundle.getString("EDIT_CLOTHES_PRICE_TAG"));
            }
            if(bundle.getString("EDIT_CLOTHES_DESCRIPTION_TAG")!=null){
                addDescriptionEdit.setText(bundle.getString("EDIT_CLOTHES_DESCRIPTION_TAG"));
            }
            if(bundle.getString("MODEL_CLOTHES_REQUEST")!=null){
               // modelScene.setVisibility(View.VISIBLE);
                //loadModels(Uri.parse(bundle.getString("MODEL_CLOTHES_REQUEST")), modelScene, CreateClothesFragment.this, 0.25f);
                noModel.setVisibility(View.GONE);
            }
            if(bundle.getString("IMAGE_CLOTHES_REQUEST")!=null){
                modelPhoto.setVisibility(View.VISIBLE);
                Picasso.get().load(bundle.getString("IMAGE_CLOTHES_REQUEST")).into(modelPhoto);
                noPhoto.setVisibility(View.GONE);
            }
        }
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_fulldata);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout yes=dialog.findViewById(R.id.yes);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialogSendClothes(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_send_clothes_application);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout sendRelative=dialog.findViewById(R.id.sendRelative);


        sendRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int num =random.nextInt(1000000000);
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                switch(radioButtonID){
                    case R.id.radio_button_1:
                        bodyType="foot";
                        type="shoes";
                        break;
                    case R.id.radio_button_2:
                        bodyType="pants";
                        type="clothes";
                        break;
                    case R.id.radio_button_3:
                        bodyType="shorts";
                        type="clothes";
                        break;
                    case R.id.radio_button_4:
                        bodyType="belt";
                        type="clothes";
                        break;
                    case R.id.radio_button_5:
                        bodyType="tshirt";
                        type="clothes";
                        break;
                    case R.id.radio_button_6:
                        bodyType="shirt";
                        type="clothes";
                        break;
                    case R.id.radio_button_7:
                        bodyType="longsleeve";
                        type="clothes";
                        break;
                    case R.id.radio_button_8:
                        bodyType="glasses";
                        type="accessories";
                        break;
                    case R.id.radio_button_9:
                        bodyType="cap";
                        type="hats";
                        break;
                    case R.id.radio_button_10:
                        bodyType="panama";
                        type="hats";
                        break;
                    case R.id.radio_button_11:
                        bodyType="skirt";
                        type="clothes";
                        break;
                    case R.id.radio_button_12:
                        bodyType="top";
                        type="clothes";
                        break;
                    case R.id.radio_button_13:
                        bodyType="bag";
                        type="accessories";
                        break;
                }
                int idCurrency=radioGroupCurrency.getCheckedRadioButtonId();
                switch(idCurrency){
                    case R.id.schoolyCoinRadio:
                        currencyType="coin";
                        break;
                    case R.id.dollarRadio:
                        currencyType="dollar";
                        break;
                }
                int idExclusive=radioGroupExclusive.getCheckedRadioButtonId();
                switch(idExclusive){
                    case R.id.radioButtonExclusiveYes:
                        exclusiveType="exclusive";
                        break;
                    case R.id.radioButtonExclusiveNo:
                        exclusiveType="no";
                        break;
                }
                String uid=firebaseModel.getReference().child("AppData").child("clothesReqests").push().getKey();
                firebaseModel.getReference().child("AppData").child("clothesReqests").child(uid)
                        .setValue(new ClothesRequest(type, imageApplication, Long.valueOf(editClothesPrice.getText().toString()), editTextClothes.getText().toString()
                                , 111, nick, currencyType,addDescriptionEdit.getText().toString() ,modelApplication , bodyType,uid,exclusiveType));
                Toast.makeText(getContext(), getContext().getResources().getText(R.string.applicationsent), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialogDollarCurrency(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_layout_dollar_currency);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout relative=dialog.findViewById(R.id.Delete_relative_layout);

        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}