package com.egormoroz.schooly.ui.coins;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.CircularArray;
import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;

public class AdsFragment extends Fragment {

    private FirebaseModel firebaseModel = new FirebaseModel();
    Fragment fragment;
    UserInformation userInformation;
    Bundle bundle;
    private RewardedAd rewardedAd1;
    private long timeRemaining;
    boolean isLoading;
    long adCount=0;
    CircularProgressIndicator circularProgressIndicator;

    public AdsFragment(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        this.fragment=fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static AdsFragment newInstance(Fragment fragment,UserInformation userInformation,Bundle bundle) {
        return new AdsFragment(fragment,userInformation,bundle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ad, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadRewardedAd();
            }
        });
        circularProgressIndicator=root.findViewById(R.id.progressIndicator);
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);



    }

    private void loadRewardedAd() {
        if (rewardedAd1 == null) {
            isLoading = true;
            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
            RewardedAd.load(
                    getActivity(),
                    "ca-app-pub-5681772686423393/5268640257",
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d("###", loadAdError.getMessage()+"    f");
                            rewardedAd1 = null;
                            isLoading = false;
                            Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            rewardedAd1 = rewardedAd;
                            showRewardedVideo();
                            isLoading = false;
                        }
                    });
        }
    }

    private void showRewardedVideo() {
        if (rewardedAd1 == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }

        rewardedAd1.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        circularProgressIndicator.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        rewardedAd1 = null;
                        Toast.makeText(
                                getContext(), "Failed to show full screen content", Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        rewardedAd1= null;
                        Toast.makeText(getContext(), "Dismissed full screen content", Toast.LENGTH_SHORT)
                                .show();
                        loadRewardedAd();
                    }
                });
        Activity activityContext = getActivity();
        rewardedAd1.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        firebaseModel.getUsersReference().child(userInformation.getNick())
                                .child("adCount").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    DataSnapshot snapshot=task.getResult();
                                    if(snapshot.exists()){
                                        adCount=snapshot.getValue(Long.class);
                                    }
                                    firebaseModel.getUsersReference().child(userInformation.getNick())
                                            .child("adCount").setValue(adCount+1);
                                    firebaseModel.getUsersReference().child(userInformation.getNick())
                                            .child("money").setValue(userInformation.getmoney()+25);
                                    Toast.makeText(getContext(),R.string.coinsreceived , Toast.LENGTH_SHORT).show();
                                    RecentMethods.setCurrentFragment(fragment, getActivity());
                                }
                            }
                        });
                    }
                });
    }


}
