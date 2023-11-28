package com.example.hotelfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hotelfinder.data.Reservation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ReservationHistoryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Spinner myspiner;
    private Toolbar toolbar;
    private HorizontalScrollView layout;
    TableLayout tableLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_history);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        myspiner = findViewById(R.id.spinner3);
        ArrayAdapter<String> myadapter = new ArrayAdapter<String>(ReservationHistoryActivity.this, R.layout.spinner_item, getResources().getStringArray(R.array.names_spinner2));
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspiner.setAdapter(myadapter);

        layout = findViewById(R.id.layout_room);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                if (parentView.getItemAtPosition(position).toString().equals("Account")) {
                    Intent intent = new Intent(getApplicationContext(), Account_change.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        tableLayout = findViewById(R.id.tableLayout);
        populateTable2();
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
                            ArrayList<String> ids = (ArrayList<String>) document.getData().get("reservation_ids");
                            for (String item : ids) {
                                Query ref = db.collection("reservations").whereEqualTo("id", item);
                                ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot querySnapshot2 = task.getResult();
                                            if(querySnapshot2 != null)
                                            {
                                                for (QueryDocumentSnapshot doc:querySnapshot2) {
                                                    Reservation reservation = doc.toObject(Reservation.class);
                                                    DocumentReference reference = doc.getReference();
                                                    populateTable(reservation.getId(),reservation.getHotel(),String.valueOf(reservation.getRoom()),reservation.getPeriod(),reservation.getStatus(),reference);
                                                }
                                            }
                                        } else {
                                            Log.w("reservation_info_err", "Error getting reservation.", task.getException());
                                        }
                                    }
                                });
                            }
                        }
                    }


                } else {
                    Log.w("reservation_err", "Error getting reservations.", task.getException());
                }
            }
        });
    }

    protected void populateTable(String id, String hotel, String room, String period, String status, DocumentReference reference)
    {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView column1TextView = new TextView(this);
        column1TextView.setText(id);
        column1TextView.setPadding(0,0,20,0);
        tableRow.addView(column1TextView);

        TextView column2TextView = new TextView(this);
        column2TextView.setText(hotel);
        column2TextView.setPadding(0,0,20,0);
        tableRow.addView(column2TextView);

        TextView column3TextView = new TextView(this);
        column3TextView.setText(room);
        column3TextView.setPadding(0,0,20,0);
        tableRow.addView(column3TextView);

        TextView column4TextView = new TextView(this);
        column4TextView.setText(period);
        column4TextView.setPadding(0,0,20,0);
        tableRow.addView(column4TextView);

        TextView column5TextView = new TextView(this);
        column5TextView.setText(status);
        column5TextView.setPadding(0,0,20,0);
        tableRow.addView(column5TextView);

        if(!Objects.equals(status, "finished") && !Objects.equals(status, "canceled")) {
            Button cancelButton = new Button(this);
            cancelButton.setText("Cancel");
            tableRow.addView(cancelButton);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference.update("status","canceled");
                    column5TextView.setText("cancelled");
                    tableRow.removeView(cancelButton);
                }
            });
        }
        tableLayout.addView(tableRow);
    }
    protected void populateTable2()
    {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView column1TextView = new TextView(this);
        column1TextView.setText("ID");
        column1TextView.setPadding(0,0,20,0);
        tableRow.addView(column1TextView);

        TextView column2TextView = new TextView(this);
        column2TextView.setText("HOTEL");
        column2TextView.setPadding(0,0,20,0);
        tableRow.addView(column2TextView);

        TextView column3TextView = new TextView(this);
        column3TextView.setText("ROOM");
        column3TextView.setPadding(0,0,20,0);
        tableRow.addView(column3TextView);

        TextView column4TextView = new TextView(this);
        column4TextView.setText("PERIOD");
        column4TextView.setPadding(0,0,20,0);
        tableRow.addView(column4TextView);

        TextView column5TextView = new TextView(this);
        column5TextView.setText("STATUS");
        column5TextView.setPadding(0,0,20,0);
        tableRow.addView(column5TextView);

        TextView column6TextView = new TextView(this);
        column6TextView.setText("CANCEL");
        tableRow.addView(column6TextView);

        tableLayout.addView(tableRow);
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
