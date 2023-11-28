package com.example.hotelfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MySignUpActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private EditText editTextName,editTextPassword, editTextVerify, editTextEmail, editTextPhone;
    private Button signup, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sign_up);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editTextName = findViewById(R.id.signup_name);
        editTextPassword = findViewById(R.id.signup_password);
        editTextVerify = findViewById(R.id.signup_verify);
        editTextEmail = findViewById(R.id.signup_email);
        editTextPhone = findViewById(R.id.signup_phone);
        signup = findViewById(R.id.signup_button);
        back = findViewById(R.id.back_button2);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, password, verify, email, phone;
                name = String.valueOf(editTextName.getText());
                password = String.valueOf(editTextPassword.getText());
                verify = String.valueOf(editTextVerify.getText());
                email = String.valueOf(editTextEmail.getText());
                phone = String.valueOf(editTextPhone.getText());

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(verify) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "Complete all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!password.equals(verify)) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("email", email);
                user.put("password", password);
                user.put("phone", phone);
                user.put("reservation_ids", Collections.emptyList());

                firestore.collection("clients")
                                .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("firestore_add_success", "DocumentSnapshot added with ID: " + documentReference.getId());
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("firestore_add_failure", "Error adding document", e);
                                                    }
                                                });

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }
}