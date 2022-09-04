package com.egormoroz.schooly.ui.main;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.egormoroz.schooly.ErrorList;
import com.egormoroz.schooly.FirebaseModel;
import com.egormoroz.schooly.R;
import com.egormoroz.schooly.RecentMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PhoneCodeActivity extends AppCompatActivity {
    final int PhoneSMSResend = 30000;
    final int Second = 1000;
    String TAG = "############";
    String phone;
    ImageView backButton;
    CountDownTimer timer;
    Context context;
    EditText SMSCode;
    ImageView continueButton;
    OnVerificationStateChangedCallbacks callbacks;
    Intent data;
    String currentVerificationCode;
    PhoneAuthProvider.ForceResendingToken currentResendToken;
    FirebaseAuth AuthenticationBase;
    TextView timerText, errorText, resendCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_phonecode);
        initElements();
        initData();
        initFirebase();
        sendSMS();
        resendSMS();
        enterCode();
        Log.d("AAA", "start");
    }
    //////////////////////// INITIALIZATION //////////////////
    public void initElements(){
        resendCode = findViewById(R.id.resendCodeTextView);
        backButton = findViewById(R.id.backfromphonecode);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
        SMSCode = findViewById(R.id.VerificationCode);
        continueButton = findViewById(R.id.continueButton);
        timerText = findViewById(R.id.time);
        errorText = findViewById(R.id.errorMessage);
        context = getBaseContext();
    }
    public void initData(){
        data = new Intent();
        phone = getIntent().getStringExtra("Phone");
        Log.d("###", phone);
    }
    public void initFirebase(){
        AuthenticationBase = FirebaseAuth.getInstance();
    }
    //////////////////////// TOOLS ///////////////////////////
    public void startTimer(){
        resendCode.setVisibility(View.GONE);
        resendCode.setEnabled(false);
        timer = new CountDownTimer(PhoneSMSResend, Second) {
            @Override
            public void onTick(long l) {
                timerText.setTextColor(getColor(R.color.app_grey));
                timerText.setText(timerText.getContext().getResources().getText(R.string.youcanresendafter).toString() + l / Second);
            }
            @Override
            public void onFinish() {
                timerText.setVisibility(View.GONE);
                resendCode.setVisibility(View.VISIBLE);
                resendCode.setEnabled(true);
                timerText.setText(timerText.getContext().getResources().getText(R.string.youcanresend).toString());
            }
        };
        timer.start();
    }
    /////////////////////// SMS CODE METHODS /////////////////
    public void sendSMS(){
        callbacks = new OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential credential) {
                Log.d(TAG, "Verification completed with: " + credential);
                signInWithPhoneAuthCredential(credential);
            }
            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                data.putExtra("IsPhoneValid", false);
                setResult(RESULT_OK, data);
                Log.d("####", "q  "+e);
                finish();
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                currentVerificationCode = verificationId;
                currentResendToken = token;
                startTimer();
            }
        };
        phoneVerification(phone);
    }
    public void enterCode(){
        SMSCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(String.valueOf(SMSCode.getText()).trim().length() != 6)
                    continueButton.setEnabled(false);
                else {
                    continueButton.setEnabled(true);
                    continueButton.setBackground(getDrawable(R.drawable.corners25appcolor));
                }
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = String.valueOf(SMSCode.getText()).trim();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(currentVerificationCode, code);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }
    public void resendSMS(){
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS();
                startTimer();
            }
        });
    }
    ////////////////////// PHONE VERIFICATION METHODS ////////
    public void phoneVerification(String phone){
        Log.d("####", "fgg");
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(AuthenticationBase)
                .setPhoneNumber(phone)       // Phone number to verify
                .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        AuthenticationBase.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            user.delete();
                            data.putExtra("IsPhoneValid", true);
                            Log.d("####", "fucknk");
                            setResult(RESULT_OK, data);
                            finish();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                RecentMethods.showErrorMessage(ErrorList.WRONG_SMS_CODE_ERROR, errorText);
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
