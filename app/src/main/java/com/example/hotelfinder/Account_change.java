package com.example.hotelfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.hotelfinder.data.Reservation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Account_change extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Spinner myspiner;
    private Toolbar toolbar;
    private EditText editpass,editverifipass, oldpass;

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change);

        db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        myspiner = findViewById(R.id.spinner4);
        ArrayAdapter<String> myadapter = new ArrayAdapter<String>(Account_change.this, R.layout.spinner_item, getResources().getStringArray(R.array.names_spinner3));
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspiner.setAdapter(myadapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        oldpass = findViewById(R.id.old_pass);
        editverifipass = findViewById(R.id.new_pass_verify);
        editpass = findViewById(R.id.new_pass);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opass = String.valueOf(oldpass.getText());
                String npass = String.valueOf(editpass.getText());
                String vpass = String.valueOf(editverifipass.getText());

                if (TextUtils.isEmpty(opass) || TextUtils.isEmpty(npass) || TextUtils.isEmpty(vpass)) {
                    Toast.makeText(getApplicationContext(), "Complete all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!npass.equals(vpass)) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                String email = user.getEmail();
                assert email != null;
                Query client = db.collection("clients").whereEqualTo("email", email);
                client.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                     String password = (String) document.getData().get("password");
                                    if (!Objects.equals(password, opass)) {
                                        Toast.makeText(getApplicationContext(), "Invalid old password", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    user.updatePassword(npass);
                                    document.getReference().update("password",npass);
                                    Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Log.w("user_err", "Error getting user.", task.getException());
                        }
                    }
                });
            }
        });

        myspiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (parentView.getItemAtPosition(position).toString().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
                if (parentView.getItemAtPosition(position).toString().equals("Home")) {
                    Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                    startActivity(intent);
                    finish();
                }
                if (parentView.getItemAtPosition(position).toString().equals("Reservations")) {
                    Intent intent = new Intent(getApplicationContext(), ReservationHistoryActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}