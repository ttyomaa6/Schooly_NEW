//package com.egormoroz.schooly.ui.coins;
//
//import android.app.Fragment;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.egormoroz.schooly.Callbacks;
//import com.egormoroz.schooly.FirebaseModel;
//import com.egormoroz.schooly.R;
//import com.egormoroz.schooly.RecentMethods;
//import com.egormoroz.schooly.ui.main.Shop.ViewingClothes;
//import com.egormoroz.schooly.ui.main.UserInformation;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//
//public class BuyCoinsFragment extends Fragment {
//
//    private FirebaseModel firebaseModel = new FirebaseModel();
//    String schoolyCoin, dollar,image;
//    TextView customer,purchase,textAcceptPayment,price;
//    ImageView backImage;
//    Fragment fragment;
//    UserInformation userInformation;
//
//
//    public BuyCoinsFragment(String schoolyCoin,String dollar,Fragment fragment,UserInformation userInformation) {
//        this.schoolyCoin = schoolyCoin;
//        this.dollar=dollar;
//        this.fragment=fragment;
//        userInformation=userInformation;
//    }
//
//    public static BuyCoinsFragment newInstance(String schoolyCoin,String dollar,Fragment fragment,UserInformation userInformation) {
//        return new BuyCoinsFragment(schoolyCoin,dollar,fragment,userInformation);
//
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_payment, container, false);
//        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
//        bnv.setVisibility(bnv.GONE);
//        firebaseModel.initAll();
//        return root;
//    }
//
//    @Override
//    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
//        super.onViewCreated(view, savedInstanceState);
//
//        price=view.findViewById(R.id.price);
//        price.setText(dollar);
//        backImage=view.findViewById(R.id.back_coins);
//        backImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RecentMethods.setCurrentFragment(fragment, getActivity());
//            }
//        });
//
//    }
//}
