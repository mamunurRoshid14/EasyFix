package com.example.easyfix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class UserProfileActivity extends AppCompatActivity {

    private EditText editTextFullName, editTextPhoneNumber;
    private ImageView imageViewProfilePhoto;
    private Button buttonSave;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        imageViewProfilePhoto = findViewById(R.id.imageViewProfilePhoto);
        buttonSave = findViewById(R.id.buttonSave);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Handle image selection
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            imageViewProfilePhoto.setImageURI(uri);
                            imageUri = uri;
                        }
                    }
                });

        imageViewProfilePhoto.setOnClickListener(v -> mGetContent.launch("image/*"));

        buttonSave.setOnClickListener(v -> saveUserProfile());
    }

    private void saveUserProfile() {
        String fullName = editTextFullName.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();

        if (imageUri != null) {
            uploadImageAndSaveUserData(fullName, phoneNumber, userId, imageUri);
        } else {
            saveUserData(fullName, phoneNumber, userId, null);
        }
    }

    private void uploadImageAndSaveUserData(String fullName, String phoneNumber, String userId, Uri imageUri) {
        String imageName = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("profile_images/" + imageName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                saveUserData(fullName, phoneNumber, userId, downloadUrl.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                saveUserData(fullName, phoneNumber, userId, null);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        saveUserData(fullName, phoneNumber, userId, null);
                    }
                });
    }

    private void saveUserData(String fullName, String phoneNumber, String userId, String imageUrl) {
        UserAccount user = new UserAccount(fullName, phoneNumber, userId, imageUrl);

        db.collection("users")
                .document(userId)
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserProfileActivity.this, "Profile saved", Toast.LENGTH_SHORT).show();
                        // Navigate back to DashboardActivity
                        Intent intent = new Intent(UserProfileActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileActivity.this, "Error saving profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
