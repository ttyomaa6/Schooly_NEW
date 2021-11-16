package com.egormoroz.schooly.ui.main;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.egormoroz.schooly.CONST;
import com.egormoroz.schooly.Callbacks;
import com.egormoroz.schooly.ErrorList;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class RegFragment extends Fragment {
    public static RegFragment newInstance() {
        return new RegFragment();
    }
    final int GOOGLE_SIGN_IN = 101;
    final String databaseUrl = CONST.RealtimeDatabaseUrl;
    final int Phone_Request_Code = 102;
    private static final String TAG = "###########";
    private static Pattern VALID_PHONE_NUMBER = Pattern.compile("^[0-9.()-]{10,25}$");
    boolean isPhoneValid = false;
    FirebaseAuth AuthenticationBase;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;
    FirebaseDatabase database;
    DatabaseReference reference;
    RelativeLayout GoogleEnter;
    EditText passwordEditText, nickNameEditText, phoneEditText;
    TextView continueRegistrationButton, errorTextnickname, errorTextphone, errorTextPassword;
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
                if (isPhoneValid)
                    createNewEmailUser(RecentMethods.makeEmail(phone), password, nick);
                break;
        }
    }
    @Override
    public void onViewCreated(@Nullable View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView gotostartregfromreg = view.findViewById(R.id.arrow);
        gotostartregfromreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentMethods.setCurrentFragment(new RegisrtationstartFragment(), getActivity());
            }
        });
    }
    /////////////////////// INITIALIZATION /////////////////////
    public void initElements(View root) {
        errorTextPassword = root.findViewById(R.id.errorpassword);
        errorTextnickname = root.findViewById(R.id.errornickname);
        errorTextphone = root.findViewById(R.id.errorphone);
        nickNameEditText = root.findViewById(R.id.editnickregistration);
        nickNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nickName = String.valueOf(s);
                RecentMethods.isNickCorrect(nickName, reference, errorTextnickname);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        passwordEditText = root.findViewById(R.id.editpassword);
        phoneEditText = root.findViewById(R.id.editphone);
        GoogleEnter = root.findViewById(R.id.GoogleEnter);
        continueRegistrationButton = root.findViewById(R.id.next);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String password = String.valueOf(passwordEditText.getText()).trim();
                isPasswordCorrect(password);
            }
        });
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = String.valueOf(phoneEditText.getText()).trim();
                if (!isPhoneCorrect(phone))
                    RecentMethods.showErrorMessage(ErrorList.PHONE_ERROR, errorTextphone);
                else
                    RecentMethods.showErrorMessage(ErrorList.NOTHING, errorTextphone);
            }
        });
    }
    public void appBarInit() {
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
    }
    /////////////////////// FIREBASE METHODS //////////////////
    public void initFirebase() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(getActivity(), gso);
        AuthenticationBase = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(databaseUrl);
        reference = database.getReference("users");
    }
    /////////////////////// CHECK METHODS /////////////////////
    boolean isPhoneValid(String phone) {
        Intent phoneIntent = new Intent(getActivity(), PhoneCodeActivity.class);
        phoneIntent.putExtra("Phone", phone);
        startActivityForResult(phoneIntent, Phone_Request_Code);
        return isPhoneValid;
    }
    boolean isPasswordCorrect(String password) {
        boolean digits = false, characters = false;
        for (char c : password.toCharArray()) {
            if (isDigit(c)) digits = true;
            else if (isLetter(c)) characters = true;
            else return false;
        }
        if (password.length() < 8)
            RecentMethods.showErrorMessage(ErrorList.NOT_ENOUGH_SYMBOL_ERROR, errorTextPassword);
        else
            RecentMethods.showErrorMessage(ErrorList.NOTHING, errorTextPassword);
        if (!digits && password.length() >= 8)
            RecentMethods.showErrorMessage(ErrorList.NOT_ONLY_LETTERS, errorTextPassword);
        if (!characters && password.length() >= 8)
            RecentMethods.showErrorMessage(ErrorList.NOT_ONLY_NUMBERS_ERROR, errorTextPassword);
        return digits && characters && password.length() > 8;
    }
    public boolean isPhoneCorrect(String phone) {
        if (phone.length() == 0) {
            RecentMethods.showErrorMessage(ErrorList.ERROR_EMPTY_PHONE, errorTextphone);
            return false;
        }
        char p[] = phone.toCharArray();
        if (p[0] != '+')
            return false;
        phone = phone.replace(p[0] + "", "");
        Matcher m = VALID_PHONE_NUMBER.matcher(phone);
        return m.matches();
    }
    ////////////////////// AUTHORIZATION METHODS(GOOGLE) //////
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        AuthenticationBase.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            String nick = String.valueOf(nickNameEditText.getText()).trim();
                            if(nick.isEmpty())
                                RecentMethods.setCurrentFragment(NicknameFragment.newInstance(), getActivity());
                            else
                                RecentMethods.setCurrentFragment(MainFragment.newInstance(), getActivity());
                        } else {
                            GoogleEnter.setEnabled(true);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    public void AuthorizationThrowGoogle() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }
    public void GoogleAuthorization() {
        GoogleEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleEnter.setEnabled(false);
                RecentMethods.hasThisUser(AuthenticationBase, AuthenticationBase.getCurrentUser(),
                        new Callbacks.hasGoogleUser() {
                            @Override
                            public void hasGoogleUserCallback(boolean hasThisUser) {
                                if(hasThisUser)
                                    RecentMethods.setCurrentFragment(new EnterFragment(), getActivity());
                                else
                                    AuthorizationThrowGoogle();
                            }
                        });
            }
        });
    }
    ////////////////////// AUTHORIZATION METHODS(PHONE) ///////
    public void RegistrationPhonePassword() {
        String nickError = String.valueOf(errorTextnickname.getText()).trim();
        String phone = String.valueOf(phoneEditText.getText()).trim();
        String password = String.valueOf(passwordEditText.getText()).trim();
        if (!isPasswordCorrect(password))
            return;
        if (!nickError.isEmpty())
            return;
        if (!isPhoneValid(phone))
            return;
    }
    public void PasswordAuthorization() {
        continueRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationPhonePassword();
                if (passwordEditText.getText().length() == 0)
                    errorTextPassword.setText("You didn't enter your password");
                if (nickNameEditText.getText().length() == 0)
                    errorTextnickname.setText("You didn't enter your nickname");
                if (phoneEditText.getText().length() == 0)
                    errorTextphone.setText("You didn't enter your phone");
            }
        });
    }
    public void createNewEmailUser(String email, String password, String nick) {
        AuthenticationBase.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = AuthenticationBase.getCurrentUser();
                            UserInformation res = new UserInformation(nick, RecentMethods.getPhone(email), user.getUid(),
                                    6, password, "Helicopter", 1000, "Miners",1,100,0, "", "", "","");
                            reference.child(nick).setValue(res);
                            RecentMethods.setCurrentFragment(MainFragment.newInstance(), getActivity());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}