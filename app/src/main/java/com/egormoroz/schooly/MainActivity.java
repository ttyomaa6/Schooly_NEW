package com.egormoroz.schooly;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.egormoroz.schooly.ui.chat.Chat;
import com.egormoroz.schooly.ui.coins.CoinsMainFragment;
import com.egormoroz.schooly.ui.coins.Transfer;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.Mining.Miner;
import com.egormoroz.schooly.ui.main.RegisrtationstartFragment;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.NewsFragment;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.egormoroz.schooly.ui.people.PeopleFragment;
import com.egormoroz.schooly.ui.people.UserPeopleAdapter;
import com.egormoroz.schooly.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

  private FirebaseDatabase database;
  private FirebaseAuth AuthenticationBase;
  public static String currentUserID;
  UserInformation userInformation;
  RelativeLayout relativeLayout;
  TextView s,loading;
  CoordinatorLayout fragmentContainer;
  String TAG="###";
  Bundle bundle=new Bundle();
  int a=0;
  private static final String CHANNEL_ID = "channel";
  FirebaseModel firebaseModel=new FirebaseModel();
  FirebaseModel firebaseNewsModel = new FirebaseModel();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    firebaseNewsModel.initNewsDatabase();
    fragmentContainer = findViewById(R.id.fragment_container);
    relativeLayout=findViewById(R.id.rel);
    s=findViewById(R.id.s);
    loading=findViewById(R.id.load);
    bundle.putString("START_BUNDLE", "Sholly");
    initFirebase();
    firebaseModel.initAll();
    ///////////Authorization block
    IsEntered();
    FirebaseUser mFirebaseUser = AuthenticationBase.getCurrentUser();
    if(mFirebaseUser != null) {
      currentUserID = mFirebaseUser.getUid();
    }
  }

  public void checkMining(){
    ((SchoolyApplication)getApplication()).checkActiveMiners();
    ((SchoolyApplication)getApplication()).startMining();
  }

  public void stopHandler(){
    ((SchoolyApplication)getApplication()).stopHandler();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_top_nav, menu);
    return true;
  }

  public void setCurrentFragment(Fragment fragment) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.frame, fragment);
    ft.commit();
  }
  void RegistrationOrEnter(){
    s.setVisibility(View.GONE);
    loading.setVisibility(View.GONE);
    setCurrentFragment(RegisrtationstartFragment.newInstance(userInformation,bundle));
  }
  public void IsEntered(){
    FirebaseUser user = AuthenticationBase.getCurrentUser();
    RecentMethods.hasThisUser(AuthenticationBase, user,
            new Callbacks.hasGoogleUser() {
              @Override
              public void hasGoogleUserCallback(boolean hasThisUser) {
                if(hasThisUser) {
                  Log.d("AAA", "current user: " + user.getEmail());
                  RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
                    @Override
                    public void PassUserNick(String nick) {
                      firebaseModel.getUsersReference().child(nick).get()
                              .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                  if (task.isSuccessful()){
                                    DataSnapshot snapshot=task.getResult();
                                    userInformation = RecentMethods.ValidateSnapToUserInformation(snapshot);
                                    getMyClothes(nick);
                                    final DatabaseReference connectedRef = database.getReference(".info/connected");
                                    getLists();
                                  }
                                }
                              });
                    }
                  });
                }
                else
                  RegistrationOrEnter();
              }
            });
  }


  public void initFirebase(){
    database = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl);
    AuthenticationBase = FirebaseAuth.getInstance();
  }

  public void getMyClothes(String nick){
    RecentMethods.getMyClothes(nick, firebaseModel, new Callbacks.GetClothes() {
      @Override
      public void getClothes(ArrayList<Clothes> allClothes) {
        Collections.reverse(allClothes);
        userInformation.setMyClothes(allClothes);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        s.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @SuppressLint("NonConstantResourceId")
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.bottom_nav_home:
                if(ProfileFragment.getLoadValue()==0){
                  setCurrentFragment(MainFragment.newInstance(userInformation,bundle));
                }
                return true;
              case R.id.bottom_nav_news:
                if(ProfileFragment.getLoadValue()==0){
                  setCurrentFragment(NewsFragment.newInstance(userInformation,bundle));
                }
                return true;
              case R.id.bottom_nav_coins:
                if(ProfileFragment.getLoadValue()==0){
                  setCurrentFragment(CoinsMainFragment.newInstance(userInformation,bundle));
                }
                return true;
              case R.id.bottom_nav_people:
                if(ProfileFragment.getLoadValue()==0){
                  setCurrentFragment(PeopleFragment.newInstance(userInformation,bundle));
                }
                return true;
              case R.id.bottom_nav_profile:
                  if(userInformation.getNick()==null){
                    RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
                      @Override
                      public void PassUserNick(String nick) {
                        setCurrentFragment(ProfileFragment.newInstance("user", nick,MainFragment.newInstance(userInformation,bundle),userInformation,bundle));
                      }
                    });
                  }else {
                    setCurrentFragment(ProfileFragment.newInstance("user", userInformation.getNick(),MainFragment.newInstance(userInformation,bundle),userInformation,bundle));
                  }
                CoordinatorLayout.LayoutParams coordinatorLayoutParams = (CoordinatorLayout.LayoutParams) fragmentContainer.getLayoutParams();
                coordinatorLayoutParams.setBehavior(null);
                return true;
            }
            return false;
          }
        });
        setCurrentFragment(MainFragment.newInstance(userInformation,bundle));
      }
    });
  }

  public void getLists(){
    RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
      @Override
      public void PassUserNick(String nick) {
        RecentMethods.getClothesInBasket(nick, firebaseModel, new Callbacks.GetClothes() {
          @Override
          public void getClothes(ArrayList<Clothes> allClothes) {
            userInformation.setClothesBasket(allClothes);
          }
        });
        RecentMethods.getClothesInWardrobe(nick, firebaseModel, new Callbacks.GetClothes() {
          @Override
          public void getClothes(ArrayList<Clothes> allClothes) {
            userInformation.setClothes(allClothes);
          }
        });
        RecentMethods.getSubscribersList(nick, firebaseModel, new Callbacks.getSubscribersList() {
          @Override
          public void getSubscribersList(ArrayList<Subscriber> subscribers) {
            userInformation.setSubscribers(subscribers);
          }
        });
        RecentMethods.getSubscriptionList(nick, firebaseModel, new Callbacks.getFriendsList() {
          @Override
          public void getFriendsList(ArrayList<Subscriber> friends) {
            userInformation.setSubscription(friends);
          }
        });
        RecentMethods.getNontificationsList(nick, firebaseModel, new Callbacks.getNontificationsList() {
          @Override
          public void getNontificationsList(ArrayList<Nontification> nontifications) {
            Collections.reverse(nontifications);
            userInformation.setNotifications(nontifications);
          }
        });
        RecentMethods.getMyLookClothes(nick, firebaseModel, new Callbacks.getLookClothes() {
          @Override
          public void getLookClothes(ArrayList<Clothes> clothesArrayList) {
            userInformation.setLookClothes(clothesArrayList);
          }
        });
        RecentMethods.getLooksList(nick, firebaseNewsModel, new Callbacks.getLooksList() {
          @Override
          public void getLooksList(ArrayList<NewsItem> look) {
            Collections.reverse(look);
            userInformation.setLooks(look);
          }
        });

        RecentMethods.getTransferHistory(nick, firebaseModel, new Callbacks.getTransferHistory() {
          @Override
          public void getTransferHistory(ArrayList<Transfer> transfers) {
            Collections.reverse(transfers);
            userInformation.setTransfers(transfers);
          }
        });
        RecentMethods.getSavedLooks(nick, firebaseModel, new Callbacks.getSavedLook() {
          @Override
          public void getSavedLook(ArrayList<NewsItem> newsItems) {
            Collections.reverse(newsItems);
            userInformation.setSavedLooks(newsItems);
          }
        });
        RecentMethods.getBlackList(nick, firebaseModel, new Callbacks.getSubscribersList() {
          @Override
          public void getSubscribersList(ArrayList<Subscriber> subscribers) {
            userInformation.setBlackList(subscribers);
          }
        });
        RecentMethods.getAlreadySearched(nick, firebaseModel, new Callbacks.GetAlreadySearched() {
          @Override
          public void getAlreadySearched(ArrayList<UserPeopleAdapter> searchedUserFromBase) {
            userInformation.setAlreadySearched(searchedUserFromBase);
          }
        });
        RecentMethods.GetMoneyFromBase(nick, firebaseModel, new Callbacks.MoneyFromBase() {
          @Override
          public void GetMoneyFromBase(long money) {
            userInformation.setmoney(money);
          }
        });
        RecentMethods.GetActiveMiner(nick, firebaseModel, new Callbacks.GetActiveMiners() {
          @Override
          public void GetActiveMiners(ArrayList<Miner> activeMinersFromBase) {
            userInformation.setMiners(activeMinersFromBase);
          }
        });
        RecentMethods.MyMinersFromBase(nick, firebaseModel, new Callbacks.GetMyMinerFromBase() {
          @Override
          public void GetMyMinerFromBase(ArrayList<Miner> myMinersFromBase) {
            userInformation.setMyMiners(myMinersFromBase);
          }
        });
        RecentMethods.getClothes(firebaseModel, new Callbacks.GetClothes() {
          @Override
          public void getClothes(ArrayList<Clothes> allClothes) {
            bundle.putSerializable("ALL_CLOTHES", allClothes);
          }
        });
        RecentMethods.getDialogs(nick, firebaseModel, new Callbacks.loadDialogs() {
          @Override
          public void LoadData(ArrayList<Chat> dialogs,ArrayList<Chat> talksArrayList) {
            userInformation.setChats(dialogs);
            userInformation.setTalksArrayList(talksArrayList);
          }
        });
        RecentMethods.loadViewedNews(nick, firebaseModel, new Callbacks.LoadViewedNews() {
          @Override
          public void getViewedNews(ArrayList<String> viewedNews) {
            userInformation.setViewedNews(viewedNews);
          }
        });

      }
    });
  }
  @Override
  protected void onStop(){
    super.onStop();
    Log.d(TAG, "onStop");
  }
}