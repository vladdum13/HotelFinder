package com.example.hotelfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hotelfinder.data.Hotel;
import com.example.hotelfinder.data.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class HotelActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Spinner myspiner;
    private Toolbar toolbar;
    private ImageView imageView;
    private TextView nameView;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        myspiner = findViewById(R.id.spinner2);
        ArrayAdapter<String> myadapter = new ArrayAdapter<String>(HotelActivity.this, R.layout.spinner_item, getResources().getStringArray(R.array.names_spinner4));
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspiner.setAdapter(myadapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layout = findViewById(R.id.hotel_content);
        imageView = findViewById(R.id.hotel_image);
        nameView = findViewById(R.id.hotel_name);

        LinearLayout.LayoutParams layoutParams_hotel = new LinearLayout.LayoutParams(
                1000, 800);
        LinearLayout.LayoutParams layoutParams_img = new LinearLayout.LayoutParams(
                800, 600);
        LinearLayout.LayoutParams layoutParams_desc = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Intent intent = getIntent();
        Hotel hotel = (Hotel) intent.getSerializableExtra("hotel_data");

        imageView.setLayoutParams(layoutParams_hotel);
        nameView.setText(hotel.getName());
        StorageReference hotelRef = storageRef.child(hotel.getName() + "/" + hotel.getImage());
        try {
            File hotelImage = File.createTempFile(hotel.getId_hotel(), "jpg");
            hotelImage.deleteOnExit();
            hotelRef.getFile(hotelImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap hotelBitmap = BitmapFactory.decodeFile(hotelImage.getAbsolutePath());
                    Drawable hotelDrawable = new BitmapDrawable(getResources(), hotelBitmap);

                    imageView.setBackground(hotelDrawable);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("image_get_err_hotel", "Error getting image");
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Query rooms = db.collection("rooms").whereEqualTo("id_hotel", hotel.getId_hotel());
        rooms.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Room room = document.toObject(Room.class);
                        listRoom(hotel, room, layoutParams_img, layoutParams_desc);
                    }
                } else {
                    Log.w("room_err", "Error getting documents: ", task.getException());
                }
            }
        });

        myspiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (parentView.getItemAtPosition(position).toString().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
                if (parentView.getItemAtPosition(position).toString().equals("Reservations")) {
                    Intent intent = new Intent(getApplicationContext(), ReservationHistoryActivity.class);
                    startActivity(intent);
                }
                if (parentView.getItemAtPosition(position).toString().equals("Account")) {
                    Intent intent = new Intent(getApplicationContext(), Account_change.class);
                    startActivity(intent);
                }
                if (parentView.getItemAtPosition(position).toString().equals("Home")) {
                    Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void listRoom(Hotel hotel, Room room, LinearLayout.LayoutParams layoutParams_img, LinearLayout.LayoutParams layoutParams_desc) {
        ImageButton imageButton = new ImageButton(this);
        TextView title = new TextView(this);
        TextView description = new TextView(this);
        StorageReference roomRef = storageRef.child(hotel.getName() + "/Rooms/" + room.getImage());

        imageButton.setLayoutParams(layoutParams_img);
        title.setLayoutParams(layoutParams_desc);
        description.setLayoutParams(layoutParams_desc);

        try {
            File roomImage = File.createTempFile(room.getName(), "jpg");
            roomImage.deleteOnExit();
            roomRef.getFile(roomImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap roomBitmap = BitmapFactory.decodeFile(roomImage.getAbsolutePath());
                    Drawable roomDrawable = new BitmapDrawable(getResources(), roomBitmap);

                    imageButton.setBackground(roomDrawable);
                    Log.d("image_get_suc", "Success getting image");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("image_get_err", "Error getting image");
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                intent.putExtra("hotel_data", hotel);
                intent.putExtra("room_data", room);
                startActivity(intent);
            }
        });

        title.setPadding(0, 10, 0 , 0);
        title.setText(room.getName() + " Room");
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f);

        description.setPadding(0, 0, 0, 10);
        description.setText(room.getCapacity() + "\t\t\t\t\t\t" + room.getPrice().toString());
        description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f);
        description.setCompoundDrawablesWithIntrinsicBounds(R.drawable.person_icon, 0, R.drawable.euro_icon, 0);

        layout.addView(title);
        layout.addView(imageButton);
        layout.addView(description);
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