package com.egormoroz.schooly;
import static java.text.DateFormat.getDateTimeInstance;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class FirebaseModel {
    public FirebaseAuth AuthenticationBase;
    public FirebaseDatabase Database;
    public DatabaseReference reference;
    public void initAll(){
        AuthenticationBase = FirebaseAuth.getInstance();
        Database = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseUrl);
        reference = Database.getReference();
    }
    public void initNewsDatabase(){
        Database = FirebaseDatabase.getInstance(CONST.RealtimeDatabaseNewsUrl);
        reference = Database.getReference();
    }

    public void initAppDataDatabase(){
        Database=FirebaseDatabase.getInstance(CONST.RealtimeDatabaseAppData);
        reference=Database.getReference();
    }
    public DatabaseReference getReference(String path){
        String pathArr[] = path.split("/");
        reference = Database.getReference();
        for(String cur : pathArr)
            reference = reference.child(cur);
        return reference;
    }
    public DatabaseReference getReference(){
        return reference;
    }
    public DatabaseReference getUsersReference(){
        return getReference().child("users");
    }
    public FirebaseUser getUser(){
        return AuthenticationBase.getCurrentUser();
    }
}