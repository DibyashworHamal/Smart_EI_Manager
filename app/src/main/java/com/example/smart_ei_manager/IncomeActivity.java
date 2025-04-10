package com.example.smart_ei_manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class IncomeActivity extends AppCompatActivity {

    private EditText etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        Spinner spinnerCategory = findViewById(R.id.spinnerIncomeCategory);
        EditText etAmount = findViewById(R.id.etIncomeAmount);
        EditText etNote = findViewById(R.id.etIncomeNote);
        etDate = findViewById(R.id.etIncomeDate);
        Button btnSave = findViewById(R.id.btnSaveIncome);

        String[] sources = {"Salary", "Freelance", "Gift", "Investment", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sources);
        spinnerCategory.setAdapter(adapter);

        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> {
            // Save logic
            Toast.makeText(this, "Income saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
