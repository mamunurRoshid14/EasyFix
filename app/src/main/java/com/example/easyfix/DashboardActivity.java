package com.example.easyfix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button btnFindService, btnCompleteProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnFindService = findViewById(R.id.btnFindService);
        btnCompleteProfile = findViewById(R.id.btnCompleteProfile);

        /*btnFindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the find service logic here
                Intent intent = new Intent(DashboardActivity.this, FindServiceActivity.class);
                startActivity(intent);
            }
        });

        btnCompleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the complete profile logic here
                Intent intent = new Intent(DashboardActivity.this, CompleteProfileActivity.class);
                startActivity(intent);
            }
        });*/
    }
}
