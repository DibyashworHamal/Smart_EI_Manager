package com.example.smart_ei_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        TextView tvSetBudget = findViewById(R.id.tvSetBudget);
        tvSetBudget.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AddBudgetActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Set Budget Here!", Toast.LENGTH_SHORT).show();
            finish();
        });


    }
}