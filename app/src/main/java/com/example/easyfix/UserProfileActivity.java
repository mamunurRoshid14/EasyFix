package com.example.easyfix;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private EditText editTextFullName, editTextPhoneNumber, editTextEmail, editTextLocation, editTextAge;
    private ImageView imageViewProfilePhoto;
    private Spinner spinnerTypeOfService;
    private Button buttonSave, buttonUpdateLocation;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private FusedLocationProviderClient fusedLocationClient;

    private double latitude;
    private double longitude;
    private String currentImageUrl;

    // Define the ArrayAdapter as a member variable
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        currentUser = mAuth.getCurrentUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextAge = findViewById(R.id.editTextAge);
        imageViewProfilePhoto = findViewById(R.id.imageViewProfilePhoto);
        buttonSave = findViewById(R.id.buttonSave);
        buttonUpdateLocation = findViewById(R.id.buttonUpdateLocation);
        spinnerTypeOfService = findViewById(R.id.spinnerTypeOfService);

        // Set up the Spinner with array of service types
        adapter = ArrayAdapter.createFromResource(this,
                R.array.type_of_service_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfService.setAdapter(adapter);

        imageViewProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonUpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
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
                            latitude = user.getLatitude();
                            longitude = user.getLongitude();

                            if (currentImageUrl != null) {
                                Picasso.get().load(currentImageUrl).into(imageViewProfilePhoto);
                            }

                            // Set spinner selection based on user's service type
                            String userTypeOfService = user.getTypeofService();
                            if (userTypeOfService != null) {
                                int spinnerPosition = adapter.getPosition(userTypeOfService);
                                spinnerTypeOfService.setSelection(spinnerPosition);
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

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Toast.makeText(UserProfileActivity.this, "Location updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserProfile() {
        String fullName = editTextFullName.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        String email = editTextEmail.getText().toString();
        String location = editTextLocation.getText().toString();
        int age = Integer.parseInt(editTextAge.getText().toString());
        String typeofService = spinnerTypeOfService.getSelectedItem().toString();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child("profile_images/" + currentUser.getUid() + ".jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            updateUserProfile(fullName, phoneNumber, email, location, age, latitude, longitude, typeofService, imageUrl);
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
            updateUserProfile(fullName, phoneNumber, email, location, age, latitude, longitude, typeofService, currentImageUrl);
        }
    }

    private void updateUserProfile(String fullName, String phoneNumber, String email, String location, int age, double latitude, double longitude, String typeofService, String imageUrl) {
        UserAccount user = new UserAccount(fullName, phoneNumber, currentUser.getUid(), imageUrl, location, age, latitude, longitude, typeofService);

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
