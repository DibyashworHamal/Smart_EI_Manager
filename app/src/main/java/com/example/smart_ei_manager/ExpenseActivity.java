package com.example.smart_ei_manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class ExpenseActivity extends AppCompatActivity {

    EditText etAmount, etNote, etDate;
    Spinner spinnerCategory;
    Button btnSaveExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        etDate = findViewById(R.id.etDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);

        String[] categories = {"Food", "Transport", "Shopping", "Bills", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        etDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) ->
                            etDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                    year, month, day);
            datePickerDialog.show();
        });

        btnSaveExpense.setOnClickListener(v -> {
            String amount = etAmount.getText().toString();
            String desc = etNote.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();
            String date = etDate.getText().toString();

            if (amount.isEmpty() || desc.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Expense Saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
