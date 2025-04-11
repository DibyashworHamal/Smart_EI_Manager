package com.example.smart_ei_manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseActivity extends AppCompatActivity {

    EditText editTextAmount, editTextNote;
    Spinner spinnerCategory;
    EditText editTextDate;
    Button buttonSave;
    Calendar calendar;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        // Find views
        editTextAmount = findViewById(R.id.etAmount);
        editTextNote = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        editTextDate = findViewById(R.id.etDate);
        buttonSave = findViewById(R.id.btnSaveExpense);

        // Room DB init
        db = AppDatabase.getDatabase(this);
        calendar = Calendar.getInstance();

        // Set category spinner from strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set default date
        updateDateLabel();

        // Show DatePickerDialog on clicking date field
        editTextDate.setOnClickListener(v -> new DatePickerDialog(
                ExpenseActivity.this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        // Save button action
        buttonSave.setOnClickListener(v -> saveExpense());
    }

    private void updateDateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextDate.setText(sdf.format(calendar.getTime()));
    }

    private void saveExpense() {
        String category = spinnerCategory.getSelectedItem().toString();
        String amountStr = editTextAmount.getText().toString().trim();
        String note = editTextNote.getText().toString().trim();
        String date = editTextDate.getText().toString();

        if (amountStr.isEmpty() || date.isEmpty() || category.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        Transaction transaction = new Transaction("expense", category, amount, note, date);

        new Thread(() -> {
            db.transactionDao().insert(transaction);
            runOnUiThread(() -> {
                Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
