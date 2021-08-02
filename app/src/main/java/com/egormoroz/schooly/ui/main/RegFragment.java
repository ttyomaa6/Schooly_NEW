package com.egormoroz.schooly.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.egormoroz.schooly.CONST;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

public class RegFragment extends Fragment {
    public static RegFragment newInstance(){return new RegFragment();}
    int RC_SIGN_IN = 175;
    final int GOOGLE_SIGN_IN = 101;
    final String databaseUrl = CONST.RealtimeDatabaseUrl;
    final int Phone_Request_Code = 102;
    private static final String TAG = "###########";
    boolean isPhoneValid = false;
    FirebaseAuth AuthenticationBase;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;
    FirebaseDatabase database;
    DatabaseReference reference;
    RelativeLayout GoogleEnter;
    EditText passwordEditText, nickNameEditText, phoneEditText;
    TextView continueRegistrationButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registration, container, false);
        appBarInit();
        ////////////Init references
        initElements(root);
        //////////Init network references
        initFirebase();
        //////////////Google Authorization
        GoogleAuthorization();
        ////////////Phone + password registration
        PasswordAuthorization();
        return root;
    }
    public void setCurrentFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }
    public void AuthorizationThrowGoogle(){
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            case GOOGLE_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                }
                break;
            case Phone_Request_Code:
                isPhoneValid = data.getExtras().getBoolean("IsPhoneValid");
                String phone = String.valueOf(phoneEditText.getText()).trim();
                String password = String.valueOf(passwordEditText.getText()).trim();
                String nick = String.valueOf(nickNameEditText.getText()).trim();
                if(isPhoneValid)
                    createNewEmailUser(makeEmail(phone), password, nick);
                break;
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        AuthenticationBase.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = AuthenticationBase.getCurrentUser();
                            setCurrentFragment(MainFragment.newInstance());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView gotostartregfromreg = view.findViewById(R.id.arrow);
        gotostartregfromreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setCurrentFragment(RegisrtationstartFragment.newInstance());
            }
        });
    }
    public void RegistrationPhonePassword(){
        String nickName = String.valueOf(nickNameEditText.getText()).trim();
        String phone = String.valueOf(phoneEditText.getText()).trim();
        String password = String.valueOf(passwordEditText.getText()).trim();
        if(!isPasswordCorrect(password))
            return;
        if(!isNickCorrect(nickName))
            return;
        if(!isPhoneValid(phone))
            return;
    }
    boolean isPhoneValid(String phone){
        Intent phoneIntent = new Intent(getActivity(), PhoneCodeActivity.class);
        phoneIntent.putExtra("Phone", phone);
        startActivityForResult(phoneIntent, Phone_Request_Code);
        return isPhoneValid;
    }
    boolean isPasswordCorrect(String password){
        boolean digits = false, characters = false;
        for(char c : password.toCharArray()){
            if(isDigit(c))
                digits = true;
            else if(isLetter(c))
                characters = true;
            else
                return false;
        }
        return digits && characters && password.length() > 8;
    }
    boolean isNickCorrect(String nickname){
        return true;
    }
    public void initElements(View root){
        nickNameEditText = root.findViewById(R.id.editnickregistration);
        passwordEditText = root.findViewById(R.id.editpassword);
        phoneEditText = root.findViewById(R.id.editphone);
        GoogleEnter = root.findViewById(R.id.GoogleEnter);
        continueRegistrationButton = root.findViewById(R.id.next);
    }
    public void initFirebase(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(getActivity(), gso);
        AuthenticationBase = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(databaseUrl);
        reference = database.getReference("users");
    }
    public void GoogleAuthorization(){
        GoogleEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthorizationThrowGoogle();
            }
        });
    }
    public void PasswordAuthorization(){
        continueRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationPhonePassword();
            }
        });
    }
    public void appBarInit(){
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
    }
    public void createNewEmailUser(String email, String password, String nick){
        AuthenticationBase.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = AuthenticationBase.getCurrentUser();
                            UserInformation info = new UserInformation(nick, getPhone(email), user.getUid(),
                                    "AVA", password, "Helicopter",  1000);
                            reference.child(user.getUid()).setValue(info);
                            setCurrentFragment(MainFragment.newInstance());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    String makeEmail(String phone){
        String email = "schooly";
        for(int i = 1; i < phone.length(); i++)
            email += phone.toCharArray()[i];
        email += "@gmail.com";
        return email;
    }
    String getPhone(String email){
        String res = email;
        res = res.replace("schooly", "");
        res = res.replace("@gmail.com", "");
        return "+" + res;
    }
}