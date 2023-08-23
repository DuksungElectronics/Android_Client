package com.example.admin_manager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter; //리스트뷰와 데이터 연결하는 어댑터 객체
    private List<String> userList;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users"); // users경로 접근

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // 기존 리스트 초기화

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    user newUser = childSnapshot.getValue(user.class);
                    if (newUser != null) {
                        String userNumber = newUser.getUserNumber();
                        String timestamp = newUser.getTimestamp();
                        String userInfo = "사용자: " + "0" + userNumber + "\n출입시간: " + timestamp;
                        userList.add(userInfo);
                    }
                }

                adapter.notifyDataSetChanged(); // 리스트뷰 갱신
            }
            // db에서 데이터를 읽어올때 예외 발생하면 처리
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to retrieve users: " + databaseError.getMessage());
            }
        };

        mDatabase.addValueEventListener(valueEventListener);
    }
}