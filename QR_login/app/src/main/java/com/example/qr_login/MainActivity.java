package com.example.qr_login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private EditText phoneEditText, verificationCodeEditText;
    private Button sendVerificationButton, verifyCodeButton;

    private TextView sign;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneEditText = findViewById(R.id.phone_editText);
        verificationCodeEditText = findViewById(R.id.qrcode_editText);
        sendVerificationButton = findViewById(R.id.phone_btn);
        verifyCodeButton = findViewById(R.id.qrcode_btn);

        sign = findViewById(R.id.Signin);

        // 이미 회원이라면,,
        sign.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity3.class);
            startActivity(intent);
        });


        mAuth = FirebaseAuth.getInstance();

        //인증번호 받기 버튼 클릭시
        sendVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "+82" + phoneEditText.getText().toString(); // +82는 한국 라서100000000으로 입력해야함
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                        60, //타임아웃시간
                        TimeUnit.SECONDS,
                        MainActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            // 전화번호 인증 완료
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }
                            // 전화번호 인증 실패
                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(MainActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    Toast.makeText(MainActivity.this, "SMS quota exceeded", Toast.LENGTH_SHORT).show(); // SMS 초과
                                }
                            }
                            // 인증 코드 성공적으로 전송
                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                mVerificationId = verificationId;
                                mResendToken = token;
                                Toast.makeText(MainActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show(); //인증번호 보내기
                                verificationCodeEditText.setEnabled(true); ///////////////////////////////
                                verifyCodeButton.setEnabled(true);
                            }
                        });
            }
        });
        //인증번호 확인
        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = verificationCodeEditText.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode); //인증번호 확인
                signInWithPhoneAuthCredential(credential);
            }
        });
    }
    //전화번호를 이용해서 로그인
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String inputText = phoneEditText.getText().toString();
                            String userNumber = "{\"userNumber\":\"" + inputText + "\"}";
                            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                            intent.putExtra("userNumber", userNumber);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}