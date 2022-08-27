package com.egormoroz.schooly.ui.main.Mining;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.coins.CoinsFragmentSecond;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.MoreMoneyFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class MiningFragment extends Fragment {

    UserInformation userInformation;
    Bundle bundle;

    public MiningFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static MiningFragment newInstance(UserInformation userInformation,Bundle bundle) {
        return new MiningFragment(userInformation,bundle);
    }

    ArrayList<Miner> listAdapterMiner = new ArrayList<Miner>();
    ArrayList<Miner> listAdapterAverageMiner = new ArrayList<Miner>();
    ArrayList<Miner> listAdapterStrongMiner = new ArrayList<Miner>();
    ArrayList<Miner> listAdapterActiveMiner = new ArrayList<Miner>();
    private FirebaseModel firebaseModel = new FirebaseModel();
    String todayMiningFormatted,nick;
    LinearLayout coinsLinear;
    TextView  schoolycoinminer, myminers, todayminingText
            , getMore,buy,numderOfActiveMiners,emptyActiveMiners,addActiveMiners;
    RecyclerView activeminersrecyclerview,weakminersrecyclerview,averageminersrecyclerview,strongminersrecyclerview;
    WeakMinersAdapter.ItemClickListener itemClickListener;
    StrongMinersAdapter.ItemClickListener itemClickListenerStrong;
    AverageMinersAdapter.ItemClickListener itemClickListenerAverage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mining, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;
    }

    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nick=userInformation.getNick();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation,bundle), getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        coinsLinear=view.findViewById(R.id.linearCoins);
        coinsLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(CoinsFragmentSecond.newInstance(MiningFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
            }
        });
        myminers = view.findViewById(R.id.myminers);
        if(userInformation.getMyMiners()==null){
            firebaseModel.getUsersReference().child(nick).child("miners")
                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        DataSnapshot snapshot= task.getResult();
                        ArrayList<Miner> myMinersFromBase=new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Miner miner = new Miner();
                            miner.setInHour(snap.child("inHour").getValue(Long.class));
                            miner.setMinerPrice(snap.child("minerPrice").getValue(Long.class));
                            miner.setMinerImage(snap.child("minerImage").getValue(String.class));
                            myMinersFromBase.add(miner);
                        }
                        Log.d("####", "a "+myMinersFromBase);
                        userInformation.setMyMiners(myMinersFromBase);
                        myminers.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecentMethods.setCurrentFragment(MyMinersFragment.newInstance(userInformation,bundle), getActivity());
                            }
                        });
                    }
                }
            });
        }else{
            myminers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecentMethods.setCurrentFragment(MyMinersFragment.newInstance(userInformation,bundle), getActivity());
                }
            });
        }
        ImageView backtomainfrommining = view.findViewById(R.id.backtomainfrommining);
        backtomainfrommining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation,bundle), getActivity());
            }
        });
        getMore=view.findViewById(R.id.getMore);
        getMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(MoreMoneyFragment.newInstance(MiningFragment.newInstance(userInformation,bundle),userInformation,bundle), getActivity());
            }
        });
        activeminersrecyclerview = view.findViewById(R.id.activeminersrecyclerview);
        schoolycoinminer = view.findViewById(R.id.schoolycoin);
        todayminingText = view.findViewById(R.id.todaymining);
        numderOfActiveMiners=view.findViewById(R.id.numbersactiveminers);
        buy=view.findViewById(R.id.buy);
        addActiveMiners=view.findViewById(R.id.addActiveMiner);
        emptyActiveMiners=view.findViewById(R.id.emptyMiners);
        schoolycoinminer.setText(String.valueOf(userInformation.getmoney()));
        weakminersrecyclerview=view.findViewById(R.id.allminersrecyclerview);
        averageminersrecyclerview=view.findViewById(R.id.averageminersrecyclerview);
        strongminersrecyclerview=view.findViewById(R.id.strongminersrecyclerview);

        itemClickListener=new WeakMinersAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position,Miner miner,String type,long money) {
                if(money<miner.getMinerPrice()){
                    showDialog(getContext().getResources().getString(R.string.notenoughcoins) );
                }else{
                    showDialog(position,miner,type,money);
                }
            }
        };
        itemClickListenerStrong=new StrongMinersAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Miner miner, String type,long money) {
                if(money<miner.getMinerPrice()){
                    showDialog(getContext().getResources().getString(R.string.notenoughcoins));
                }else{
                    showDialog(position,miner,type,money);
                }
            }
        };
        itemClickListenerAverage=new AverageMinersAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, Miner miner, String type,long money) {
                if(money<miner.getMinerPrice()){
                    showDialog(getContext().getResources().getString(R.string.notenoughcoins));
                }else{
                    showDialog(position,miner,type,money);
                }
            }
        };
        todayMiningFormatted = new DecimalFormat("#0.00").format(userInformation.getTodayMining());
        todayminingText.setText("+"+todayMiningFormatted);
        setMiningMoney();
        GetDataFromBase();
        getActiveMinersFromBase();
    }


    public void GetDataFromBase(){
        if(bundle.getSerializable("WEAK_MINERS")==null){
            RecentMethods.AllminersFromBase(firebaseModel, new Callbacks.GetMinerFromBase() {
                @Override
                public void GetMinerFromBase(ArrayList<Miner> minersFromBase) {
                    bundle.putSerializable("WEAK_MINERS", minersFromBase);
                    listAdapterMiner.addAll(minersFromBase);
                    WeakMinersAdapter allMinersAdapter=new WeakMinersAdapter(listAdapterMiner,itemClickListener,userInformation);
                    weakminersrecyclerview.setAdapter(allMinersAdapter);
                    weakminersrecyclerview.addItemDecoration(new WeakMinersAdapter.SpaceItemDecoration());
                }
            });
        }else{
            listAdapterMiner= (ArrayList<Miner>) bundle.getSerializable("WEAK_MINERS");
            WeakMinersAdapter allMinersAdapter=new WeakMinersAdapter(listAdapterMiner,itemClickListener,userInformation);
            weakminersrecyclerview.setAdapter(allMinersAdapter);
            weakminersrecyclerview.addItemDecoration(new WeakMinersAdapter.SpaceItemDecoration());
        }
        if(bundle.getSerializable("AVERAGE_MINERS")==null){
            RecentMethods.AverageMinersFromBase(firebaseModel, new Callbacks.GetMinerFromBase() {
                @Override
                public void GetMinerFromBase(ArrayList<Miner> minersFromBase) {
                    bundle.putSerializable("AVERAGE_MINERS", minersFromBase);
                    listAdapterAverageMiner.addAll(minersFromBase);
                    AverageMinersAdapter avarageMinersAdapter=new AverageMinersAdapter(listAdapterAverageMiner,itemClickListenerAverage,userInformation);
                    averageminersrecyclerview.setAdapter(avarageMinersAdapter);
                    averageminersrecyclerview.addItemDecoration(new AverageMinersAdapter.SpaceItemDecoration());
                }
            });
        }else{
            listAdapterAverageMiner= (ArrayList<Miner>) bundle.getSerializable("AVERAGE_MINERS");
            AverageMinersAdapter avarageMinersAdapter=new AverageMinersAdapter(listAdapterAverageMiner,itemClickListenerAverage,userInformation);
            averageminersrecyclerview.setAdapter(avarageMinersAdapter);
            averageminersrecyclerview.addItemDecoration(new AverageMinersAdapter.SpaceItemDecoration());
        }
        if(bundle.getSerializable("STRONG_MINERS")==null){
            RecentMethods.StrongMinersFromBase(firebaseModel, new Callbacks.GetMinerFromBase() {
                @Override
                public void GetMinerFromBase(ArrayList<Miner> minersFromBase) {
                    bundle.putSerializable("STRONG_MINERS", minersFromBase);
                    listAdapterStrongMiner.addAll(minersFromBase);
                    StrongMinersAdapter strongMinersAdapter=new StrongMinersAdapter(listAdapterStrongMiner,itemClickListenerStrong,userInformation);
                    strongminersrecyclerview.setAdapter(strongMinersAdapter);
                    strongminersrecyclerview.addItemDecoration(new StrongMinersAdapter.SpaceItemDecoration());
                }
            });
        }else{
            listAdapterStrongMiner= (ArrayList<Miner>) bundle.getSerializable("STRONG_MINERS");
            StrongMinersAdapter strongMinersAdapter=new StrongMinersAdapter(listAdapterStrongMiner,itemClickListenerStrong,userInformation);
            strongminersrecyclerview.setAdapter(strongMinersAdapter);
            strongminersrecyclerview.addItemDecoration(new StrongMinersAdapter.SpaceItemDecoration());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public void getActiveMinersFromBase(){
        if(userInformation.getMiners()==null){
            RecentMethods.GetActiveMiner(nick, firebaseModel,
                    new Callbacks.GetActiveMiners() {
                        @RequiresApi(api = Build.VERSION_CODES.P)
                        @Override
                        public void GetActiveMiners(ArrayList<Miner> activeMinersFromBase) {
                            numderOfActiveMiners.setText(String.valueOf(activeMinersFromBase.size())+"/3");
                            userInformation.setMiners(activeMinersFromBase);
                            if(activeMinersFromBase.size()==0) {
                                emptyActiveMiners.setVisibility(View.VISIBLE);
                                addActiveMiners.setVisibility(View.VISIBLE);
                                addActiveMiners.setOutlineAmbientShadowColor(Color.parseColor("#F3A2E5"));
                                addActiveMiners.setOutlineAmbientShadowColor(Color.parseColor("#F3A2E5"));
                                addActiveMiners.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RecentMethods.setCurrentFragment(MyMinersFragment.newInstance(userInformation,bundle), getActivity());
                                    }
                                });
                                emptyActiveMiners.setText(getContext().getResources().getText(R.string.addactiveminers));
                            }else {
                                emptyActiveMiners.setVisibility(View.GONE);
                                addActiveMiners.setVisibility(View.GONE);
                            }
                            listAdapterActiveMiner.addAll(activeMinersFromBase);
                            ActiveMinersAdapter activeMinersAdapter=new ActiveMinersAdapter(listAdapterActiveMiner,userInformation);
                            activeminersrecyclerview.setAdapter(activeMinersAdapter);
                        }
                    });
        }else {
            numderOfActiveMiners.setText(String.valueOf(userInformation.getMiners().size())+"/3");
            if(userInformation.getMiners().size()==0) {
                emptyActiveMiners.setVisibility(View.VISIBLE);
                addActiveMiners.setVisibility(View.VISIBLE);
                addActiveMiners.setOutlineAmbientShadowColor(Color.parseColor("#F3A2E5"));
                addActiveMiners.setOutlineAmbientShadowColor(Color.parseColor("#F3A2E5"));
                addActiveMiners.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecentMethods.setCurrentFragment(MyMinersFragment.newInstance(userInformation,bundle), getActivity());
                    }
                });
                emptyActiveMiners.setText(getContext().getResources().getText(R.string.addactiveminers));
            }else {
                emptyActiveMiners.setVisibility(View.GONE);
                addActiveMiners.setVisibility(View.GONE);
            }
            listAdapterActiveMiner.addAll(userInformation.getMiners());
            ActiveMinersAdapter activeMinersAdapter=new ActiveMinersAdapter(listAdapterActiveMiner,userInformation);
            activeminersrecyclerview.setAdapter(activeMinersAdapter);
        }
    }


    public void showDialog(int pos,Miner miner,String type,long money){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.buy_miner_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.acceptText);

        RelativeLayout no=dialog.findViewById(R.id.no);
        RelativeLayout yes=dialog.findViewById(R.id.yes);


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("weak")){
                    RecentMethods.GetMoneyFromBase(nick, firebaseModel, new Callbacks.MoneyFromBase() {
                        @Override
                        public void GetMoneyFromBase(long money) {
                            RecentMethods.buyWeakMiner(String.valueOf(pos), firebaseModel, new Callbacks.buyMiner() {
                                @Override
                                public void buyMiner(Miner miner) {
                                    firebaseModel.getReference("users").child(nick)
                                            .child("miners").child(String.valueOf(pos)+type).setValue(miner);
                                    updateMiners();
                                }
                            });
                        }
                    });
                }else if(type.equals("medium")){
                    RecentMethods.GetMoneyFromBase(nick, firebaseModel, new Callbacks.MoneyFromBase() {
                        @Override
                        public void GetMoneyFromBase(long money) {
                            RecentMethods.buyAverageMiner(String.valueOf(pos), firebaseModel, new Callbacks.buyMiner() {
                                @Override
                                public void buyMiner(Miner miner) {
                                    firebaseModel.getReference("users").child(nick)
                                            .child("miners").child(String.valueOf(pos)+type).setValue(miner);
                                    updateMiners();
                                }
                            });
                        }
                    });
                }
                else if(type.equals("strong")){
                    RecentMethods.GetMoneyFromBase(nick, firebaseModel, new Callbacks.MoneyFromBase() {
                        @Override
                        public void GetMoneyFromBase(long money) {
                            RecentMethods.buyStrongMiner(String.valueOf(pos), firebaseModel, new Callbacks.buyMiner() {
                                @Override
                                public void buyMiner(Miner miner) {
                                    firebaseModel.getReference("users").child(nick)
                                            .child("miners").child(String.valueOf(pos)+type).setValue(miner);
                                    updateMiners();
                                }
                            });
                        }
                    });
                }
                firebaseModel.getUsersReference().child(nick).child("money").setValue(money-miner.getMinerPrice());
                RecentMethods.GetMoneyFromBase(nick, firebaseModel, new Callbacks.MoneyFromBase() {
                    @Override
                    public void GetMoneyFromBase(long money) {
                        schoolycoinminer.setText(String.valueOf(money));
                        userInformation.setmoney(money);
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialog(String textInDialog){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text=dialog.findViewById(R.id.Text);
        text.setText(textInDialog);
        RelativeLayout relative=dialog.findViewById(R.id.Delete_relative_layout);


        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void updateMiners(){
        RecentMethods.MyMinersFromBase(nick, firebaseModel, new Callbacks.GetMyMinerFromBase() {
            @Override
            public void GetMyMinerFromBase(ArrayList<Miner> myMinersFromBase) {
                userInformation.setMyMiners(myMinersFromBase);
            }
        });
    }

//    public void getSchoolyCoin(){
//        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
//            @Override
//            public void PassUserNick(String nick) {
//                RecentMethods.GetTodayMining(nick, firebaseModel, new Callbacks.GetTodayMining() {
//                    @Override
//                    public void GetTodayMining(double todayMiningFromBase) {
//                        String todayMiningFormatted = new DecimalFormat("#0.00").format(todayMiningFromBase);
//                        todayminingText.setText(todayMiningFormatted);
//                    }
//                });
//            }
//        });
//    }

    public void setMiningMoney(){
        RecentMethods.GetTodayMiningValue(nick, firebaseModel, new Callbacks.GetTodayMining() {
            @Override
            public void GetTodayMining(double todayMiningFromBase) {
                todayMiningFormatted = new DecimalFormat("#0.00").format(todayMiningFromBase);
                todayminingText.setText("+"+todayMiningFormatted);
            }
        });
    }

//    public void addWeakMiners(){
//        listAdapterMiner.add(new Miner(5,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,100));
//        listAdapterMiner.add(new Miner(7,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,150));
//        listAdapterMiner.add(new Miner(13,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,200));
//        listAdapterMiner.add(new Miner(17,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,240));
//        listAdapterMiner.add(new Miner(20,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,270));
//    }
//
//    public void addAverageMiners(){
//        listAdapterAverageMiner.add(new Miner(24,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,300));
//        listAdapterAverageMiner.add(new Miner(28,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,340));
//        listAdapterAverageMiner.add(new Miner(32,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,380));
//        listAdapterAverageMiner.add(new Miner(35,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,420));
//        listAdapterAverageMiner.add(new Miner(38,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,450));
//    }
//
//    public void addStrongMiners(){
//        listAdapterStrongMiner.add(new Miner(42,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,480));
//        listAdapterStrongMiner.add(new Miner(45,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,520));
//        listAdapterStrongMiner.add(new Miner(48,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,560));
//        listAdapterStrongMiner.add(new Miner(52,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,600));
//        listAdapterStrongMiner.add(new Miner(56,"https://firebasestorage.googleapis.com/v0/b/schooly-47238.appspot.com/o/clothes%2Fjordan.jpg?alt=media&token=823b2a10-1dcd-47c5-8170-b5a4fb155500"
//                ,640));
//    }
//
//    public void puyInBase(){
//        firebaseModel.getReference().child("AppData").child("AllMiners").child("Weak").setValue(listAdapterMiner);
//        firebaseModel.getReference().child("AppData").child("AllMiners").child("Average").setValue(listAdapterAverageMiner);
//        firebaseModel.getReference().child("AppData").child("AllMiners").child("Strong").setValue(listAdapterStrongMiner);
//    }

}