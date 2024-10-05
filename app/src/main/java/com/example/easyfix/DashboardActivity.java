package com.example.easyfix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private Button btnFindService, btnCompleteProfile, btnSignOut;
    private TextView tvUserEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        btnFindService = findViewById(R.id.btnFindService);
        btnCompleteProfile = findViewById(R.id.btnCompleteProfile);
        btnSignOut = findViewById(R.id.btnSignOut);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        if (currentUser != null) {
            tvUserEmail.setText(currentUser.getEmail());
        }

/*        btnFindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, FindServiceActivity.class);
                startActivity(intent);
            }
        });

        btnCompleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, CompleteProfileActivity.class);
                startActivity(intent);
            }
        });*/

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
