package com.egormoroz.schooly.ui.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.MainFragment;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    FirebaseModel firebaseModel = new FirebaseModel();
    ViewPager2 viewPager2;
    DatabaseReference ref;
    UserInformation userInformation;
    Bundle bundle;
    NewsAdapter.itemChanged onItemChangedListener;
    NewsAdapter newsAdapter;
    ArrayList<NewsItem> newsArrayList=new ArrayList<>();
    CircularProgressIndicator circularProgressIndicator;
    int currentItem;

    public NewsFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static NewsFragment newInstance(UserInformation userInformation, Bundle bundle) {
        return new NewsFragment(userInformation,bundle);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bundle.putInt("CURRENTPOSITION", viewPager2.getCurrentItem());
        bundle.putSerializable("NEWSLIST", newsArrayList);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(View.VISIBLE);
        firebaseModel.initAll();

        OnBackPressedCallback callback1 = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation, bundle), getActivity());
            }
        };
        onItemChangedListener = new NewsAdapter.itemChanged() {
            @Override
            public void onItemChanged(int position, String type) {
                if(type.equals("like")){
                    //newsAdapter.notifyItemChanged(position);
                }
            }
        };

        currentItem=bundle.getInt("CURRENTPOSITION");

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback1);
        circularProgressIndicator=root.findViewById(R.id.progressIndicator);
        viewPager2 = root.findViewById(R.id.picturenewspager);
        ref = FirebaseDatabase.getInstance().getReference("news");


        loadNews();
       return root;
    }


    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void isSubscribed(String username, DataSnapshot data) {

        RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
            @Override
            public void PassUserNick(String nick) {
                Log.d("###", username);
                Query query = firebaseModel.getUsersReference().child(nick).child("subscription").child(username);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            remoteImages.add(new NewsItem(data.child("imageUrl").getValue().toString(),
//                                    data.child("itemDescription").getValue().toString(),
//                                    data.child("likesCount").getValue().toString(),
//                                    data.child("newsID").getValue().toString()));
//                            viewPager2.setAdapter(new NewsAdapter(remoteImages));
//
//                            viewPager2.setOffscreenPageLimit(3);
//                        } else if (nick.equals(username))
//                            remoteImages.add(new NewsItem(data.child("imageUrl").getValue().toString(),
//                                    data.child("itemDescription").getValue().toString(),
//                                    data.child("likesCount").getValue().toString(),
//                                    data.child("newsID").getValue().toString()));
//                        viewPager2.setAdapter(new NewsAdapter(remoteImages));
//
//                        Log.d("news", String.valueOf(remoteImages.size()));
//                        viewPager2.setOffscreenPageLimit(3);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void isOpen(String username, DataSnapshot data){
        Query query = firebaseModel.getUsersReference().child(username).child("accountType");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.equals("open")) {
//                    remoteImages.add(new NewsItem(data.child("imageUrl").getValue().toString(),
//                            data.child("itemDescription").getValue().toString(),
//                            data.child("likesCount").getValue().toString(),
//                            data.child("newsID").getValue().toString()));
//                    viewPager2.setAdapter(new NewsAdapter(remoteImages));
//
//                    Log.d("news", String.valueOf(remoteImages.size()));
//                    viewPager2.setOffscreenPageLimit(3);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadNews(){
        if(bundle.getSerializable("NEWSLIST")==null){
            LoadNewsTread loadNewsTread=new LoadNewsTread(userInformation, new Callbacks.loadNewsTread() {
                @Override
                public void LoadNews(NewsItem newsItem) {
                    Log.d("####", "dd   "+newsItem);
                    circularProgressIndicator.setVisibility(View.GONE);
                    if(newsAdapter==null){
                        newsArrayList.add(newsItem);
                        newsAdapter=new NewsAdapter(newsArrayList, userInformation, bundle,NewsFragment.newInstance(userInformation, bundle),getActivity(), onItemChangedListener);

                        viewPager2.setAdapter(newsAdapter);
                    }else{
                        newsArrayList.add(newsItem);
                        Log.d("####", "dd 1  "+newsArrayList.size());
                        newsAdapter.notifyItemChanged(newsArrayList.size()-1);
                        viewPager2.setCurrentItem(currentItem);
                    }

                }
            });
        }else {
            newsArrayList= (ArrayList<NewsItem>) bundle.getSerializable("NEWSLIST");
            newsAdapter=new NewsAdapter(newsArrayList, userInformation, bundle,NewsFragment.newInstance(userInformation, bundle),getActivity(), onItemChangedListener);
            viewPager2.setAdapter(newsAdapter);
            viewPager2.setCurrentItem(currentItem);
        }
    }

}