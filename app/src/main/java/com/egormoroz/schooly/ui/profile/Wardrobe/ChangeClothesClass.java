package com.egormoroz.schooly.ui.profile.Wardrobe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.ui.main.GenderFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;

import java.nio.Buffer;

public class ChangeClothesClass   {

    static Clothes clothes;
    static UserInformation userInformation;
    static Bundle bundle;
    static Callbacks.LoadTryOnClothes loadTryOnClothes;
    static FirebaseModel firebaseModel=new FirebaseModel();
    static Buffer b;
    Activity activity;

    public ChangeClothesClass(Clothes clothes, UserInformation userInformation, Bundle bundle,Activity activity1, Callbacks.LoadTryOnClothes loadTryOnClothes){
        firebaseModel.initAll();
        ChangeClothesClass.clothes =clothes;
        ChangeClothesClass.userInformation =userInformation;
        ChangeClothesClass.bundle =bundle;
        ChangeClothesClass.loadTryOnClothes =loadTryOnClothes;
        activity=activity1;
        checkMaskClothes(clothes,loadTryOnClothes);
    }

    public static ChangeClothesClass newInstance(Clothes clothes, UserInformation userInformation, Bundle bundle,Activity activity,Callbacks.LoadTryOnClothes loadTryOnClothes) {
        return new ChangeClothesClass(clothes,userInformation,bundle,activity,loadTryOnClothes);

    }

    public void checkMaskClothes(Clothes clothes,Callbacks.LoadTryOnClothes loadTryOnClothes){
        String type=clothes.getBodyType();
        int a=0;
        int c=0;
        b=clothes.getBuffer();
        Clothes clothesToChange=new Clothes();
        if(userInformation.getLookClothes().size()==0){
            clothes.setBuffer(null);
            firebaseModel.getUsersReference().child(userInformation.getNick()).child("lookClothes")
                    .child(clothes.getUid()).setValue(clothes);
            if(a==0){
                clothes.setBuffer(b);
                Log.d("#####", "dd");
                //когда первый элемент одежды
                loadTryOnClothes.loadTryOnClothes(clothes, null, activity.getResources().getString(R.string.clothesputon), null,activity);
                a++;
            }
        }  else{
            for(int i=0;i<userInformation.getLookClothes().size();i++){
                Clothes clothes1=userInformation.getLookClothes().get(i);
                Log.d("####", "cl   "+clothes1.getUid()+"     "+clothes.getUid());
                if(clothes1.getUid().equals(clothes.getUid())){
                    if (!clothes.getBodyType().equals("foot")&&!clothes.getBodyType().equals("body")&&!clothes.getBodyType().equals("leg")){
                        //снимать одежду и убрать из активных
                        loadTryOnClothes.loadTryOnClothes(null, clothes, activity.getResources().getString(R.string.clothesoff), clothes.getUid(),activity);
                    }else {
                        //не раздевать персонажа, одежда уже надета
                        loadTryOnClothes.loadTryOnClothes(null, null, activity.getResources().getString(R.string.cantchange), null,activity);
                    }
                    break;
                }if(clothes1.getBodyType().equals(type)){
                    clothesToChange=clothes1;
                    c++;
                }
                if(i==userInformation.getLookClothes().size()-1){
                    if(c==1){
                        firebaseModel.getUsersReference().child(userInformation.getNick()).child("lookClothes")
                                .child(clothesToChange.getUid()).removeValue();
                        clothes.setBuffer(null);
                        firebaseModel.getUsersReference().child(userInformation.getNick()).child("lookClothes")
                                .child(clothes.getUid()).setValue(clothes);
                    }     else{
                        clothes.setBuffer(null);
                        firebaseModel.getUsersReference().child(userInformation.getNick()).child("lookClothes")
                                .child(clothes.getUid()).setValue(clothes);
                    }
                    if(a==0){
                        clothes.setBuffer(b);
                        if(clothesToChange.getClothesTitle()!=null){
                            Log.d("#####", "dd111   "+clothesToChange.getClothesTitle());
                            loadTryOnClothes.loadTryOnClothes(clothes, clothesToChange, activity.getResources().getString(R.string.clothesputon), clothesToChange.getUid(),activity);

                        }else{
                            loadTryOnClothes.loadTryOnClothes(clothes, null, activity.getResources().getString(R.string.clothesputon), null,activity);

                        }
                        a++;
                    }
                }
            }
        }
    }
}
