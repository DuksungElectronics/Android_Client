package com.example.qr_login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity3 extends AppCompatActivity {
    private EditText phoneNumberEditText;
    private Button signInButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        phoneNumberEditText = findViewById(R.id.phone_login);
        signInButton = findViewById(R.id.logbutton);

        mAuth = FirebaseAuth.getInstance();
        // 로그인
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "+82" + phoneNumberEditText.getText().toString();

                // 번호가 유효한지 검사
                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNumberEditText.setError("Phone number cannot be empty");
                    return;
                }

                // 전화번호 등록되어있을때
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.getPhoneNumber() != null && user.getPhoneNumber().equals(phoneNumber)) { // 일치 확인
                    // 회원가입 완료되었다면
                    startActivity(new Intent(MainActivity3.this, MainActivity2.class).putExtra("phone", user.getPhoneNumber())); // 맞으면 MainActivity로 이동
                    finish();
                } else {
                    // 회원가입되어있지 않은 번호라면
                    phoneNumberEditText.setError("This phone number is not registered");
                }
            }
        });
    }
}
