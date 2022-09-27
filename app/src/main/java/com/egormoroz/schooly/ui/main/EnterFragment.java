package com.egormoroz.schooly.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.CONST;
import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.MainActivity;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.egormoroz.schooly.ui.main.Mining.MiningFragment;
import com.google.android.filament.Camera;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EnterFragment extends Fragment {
    UserInformation userInformation;
    Bundle bundle;

    public EnterFragment(UserInformation userInformation,Bundle bundle) {
        this.userInformation=userInformation;
        this.bundle=bundle;
    }

    public static EnterFragment newInstance(UserInformation userInformation,Bundle bundle) {
        return new EnterFragment(userInformation,bundle);
    }
    EditText phoneEditText;
    EditText passwordEditText;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;
    RelativeLayout GoogleEnter;
    FirebaseAuth AuthenticationBase;
    TextView continueButton;
    int GOOGLE_SIGN_IN = 101;
    private static final String TAG = "###########";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_enter, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        initElements(root);

        ////////////////Init network references
        initFirebase();
        ///////////////Authorization throw google
        GoogleAuthorizationClick();
        /////////////Simple authorization
        PasswordPhoneAuthorizationClick();
        return root;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                RecentMethods.setCurrentFragment(RegFragment.newInstance(userInformation, bundle), getActivity());
                Log.w(TAG, "Google sign in failed", e);
            }
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
                            FirebaseModel firebaseModel=new FirebaseModel();
                            firebaseModel.initAll();
                            RecentMethods.UserNickByUid(firebaseModel.getUser().getUid(), firebaseModel, new Callbacks.GetUserNickByUid() {
                                @Override
                                public void PassUserNick(String nick) {
                                    userInformation.setNick(nick);
                                    ((MainActivity)getActivity()).IsEntered();
                                    ((MainActivity)getActivity()).checkMining();
                                    RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation, bundle), getActivity());
                                }
                            });
                        } else {
                            GoogleEnter.setEnabled(true);
                            // If sign in fails, display a message to the user.
                            RecentMethods.setCurrentFragment(RegFragment.newInstance(userInformation, bundle), getActivity());
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    @Override
    public void onViewCreated(@Nullable View view,@NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView gotostartreg = view.findViewById(R.id.arrowtostartreg);
        gotostartreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(new RegisrtationstartFragment(userInformation,bundle), getActivity());
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                RecentMethods.setCurrentFragment(new RegisrtationstartFragment(userInformation,bundle), getActivity());
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
    ///////////////////////////// INITIALIZATION ////////////////
    public void initElements(View root){
        GoogleEnter = root.findViewById(R.id.GoogleEnter);
        phoneEditText = root.findViewById(R.id.egitnick);
        passwordEditText = root.findViewById(R.id.editpassworgenter);
        continueButton = root.findViewById(R.id.next);
    }
    public void initFirebase(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("44838623612-du9vom4g3h9nvkoi4ml7aseaudolkoi1.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(getActivity(), gso);
        AuthenticationBase = FirebaseAuth.getInstance();
    }
    ///////////////////////////// AUTHORIZATION METHODS(GOOGLE) /
    public void GoogleAuthorizationClick(){
        GoogleEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleEnter.setEnabled(false);
                AuthorizationThrowGoogle();
            }
        });
    }
    public void AuthorizationThrowGoogle(){
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }
    //////////////////////////// AUTHORIZATION METHODS(PHONE) ///
    public void PasswordPhoneAuthorizationClick(){
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordPhoneAuthorization();
                Log.d("#####", "dd");
            }
        });
    }
    public void PasswordPhoneAuthorization(){
        String phone = String.valueOf(phoneEditText.getText()).trim();
        String password = String.valueOf(passwordEditText.getText()).trim();
        if(phone.length() == 0 || password.length() == 0)
            return;
        AuthenticationBase.signInWithEmailAndPassword(RecentMethods.makeEmail(phone), password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = AuthenticationBase.getCurrentUser();
                            RecentMethods.setCurrentFragment(MainFragment.newInstance(userInformation,bundle), getActivity());
                        } else {
                            Log.d("AAAA", RecentMethods.makeEmail(phone)+"  "+password);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }
}
