package com.example.smart_ei_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        TextView tvSetBudget = findViewById(R.id.tvSetBudget);
        tvSetBudget.setOnClickListener(v -> {
            startActivity(new Intent(this, AddBudgetActivity.class));
            Toast.makeText(this, "Set Budget Here!", Toast.LENGTH_SHORT).show();
        });
    }

}
