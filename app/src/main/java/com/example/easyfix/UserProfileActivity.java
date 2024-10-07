package com.example.easyfix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextFullName, editTextPhoneNumber, editTextEmail, editTextLocation, editTextAge;
    private ImageView imageViewProfilePhoto;
    private Button buttonSave;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private String currentImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        currentUser = mAuth.getCurrentUser();

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextAge = findViewById(R.id.editTextAge);
        imageViewProfilePhoto = findViewById(R.id.imageViewProfilePhoto);
        buttonSave = findViewById(R.id.buttonSave);

        imageViewProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        if (currentUser != null) {
            fetchUserProfile(currentUser.getUid());
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageViewProfilePhoto);
        }
    }

    private void fetchUserProfile(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        UserAccount user = document.toObject(UserAccount.class);
                        if (user != null) {
                            editTextFullName.setText(user.getFullName());
                            editTextPhoneNumber.setText(user.getPhoneNumber());
                            editTextEmail.setText(currentUser.getEmail());
                            editTextLocation.setText(user.getLocation());
                            editTextAge.setText(String.valueOf(user.getAge()));
                            currentImageUrl = user.getImageUrl();
                            if (currentImageUrl != null) {
                                Picasso.get().load(currentImageUrl).into(imageViewProfilePhoto);
                            }
                        }
                    } else {
                        Toast.makeText(UserProfileActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Error fetching profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserProfile() {
        final String fullName = editTextFullName.getText().toString();
        final String phoneNumber = editTextPhoneNumber.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String location = editTextLocation.getText().toString();
        final int age = Integer.parseInt(editTextAge.getText().toString());

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child("profile_images/" + currentUser.getUid() + ".jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            updateUserProfile(fullName, phoneNumber, email, location, age, imageUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateUserProfile(fullName, phoneNumber, email, location, age, currentImageUrl);
        }
    }

    private void updateUserProfile(String fullName, String phoneNumber, String email, String location, int age, String imageUrl) {
        UserAccount user = new UserAccount(fullName, phoneNumber, currentUser.getUid(), imageUrl, location, age);

        db.collection("users").document(currentUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(UserProfileActivity.this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
