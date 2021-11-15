package com.egormoroz.schooly;

import com.egormoroz.schooly.ui.main.Mining.Miner;
import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;

import java.util.ArrayList;

public class Callbacks {
    public interface UniqueNick{
        void uniqueNicknameCallback(boolean isUnique);
    }
    public interface hasGoogleUser{
        void hasGoogleUserCallback(boolean hasThisUser);
    }
    public interface passUserDataBetweenFragments{
        void passUserData(UserInformation info);
    }
    public interface LoadUserDataInterface{
        void LoadData(ArrayList<UserInformation> data);
    }
    public interface PassLoadUserDataInterface{
        void PassData(ArrayList<UserInformation> data);
    }
    public interface GetUserNickByUid{
        void PassUserNick(String nick);
    }
    public interface GetMinerFromBase{
        void GetMinerFromBase(ArrayList<Miner> minersFromBase);
    }

    public interface buyMiner{
        void buyMiner(Miner miner);
    }
    public interface HasUid{
        void HasUidCallback(boolean HasUid);
    }
    public interface GetMyMinerFromBase{
        void GetMyMinerFromBase(ArrayList<Miner> myMinersFromBase);
    }
    public interface GetActiveMiners{
        void GetActiveMiners(ArrayList<Miner> activeMinersFromBase);
    }
    public interface MoneyFromBase{
        void GetMoneyFromBase(long money);
    }
    public interface GetTodayMining{
        void GetTodayMining(double todayMiningFromBase);
    }
    public interface GetTimesTamp{
        void GetTimesTamp(long timesTamp);
    }
    public interface GetUserNicks{
        void GetUsersNicks(ArrayList<String> userNicks);
    }

    public interface GetBio{
        void GetBiography(String bio);
    }
    public interface GetClothes{
        default void getClothes(ArrayList<Clothes> allClothes) {
        }
    }
    public interface getFriendsList{
        default void getFriendsList(ArrayList<Subscriber> friends){}
    }
    public interface getAmountOfFriends{
        default void getAmountOfFriends(int amount){}
    }
    public interface getSubscribersList{
        default void getSubscribersList(ArrayList<Subscriber> subscribers){}
    }
    public interface getAmountOfSubscribers{
        default void getAmountOfSubscribers(int amount){}
    }
    public interface friendQuery{
        default void friendQuery(){}
    }
}