package com.example.smart_ei_manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExpenseActivity extends AppCompatActivity {

   private EditText editTextAmount, editTextNote, editTextDate, etOtherExpenseCategory;
    private Spinner spinnerCategory;
    Button buttonSave;
    private AppDatabase db;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        editTextAmount = findViewById(R.id.etAmount);
        editTextNote = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        editTextDate = findViewById(R.id.etDate);
        etOtherExpenseCategory = findViewById(R.id.etOtherExpenseCategory);
        buttonSave = findViewById(R.id.btnSaveExpense);

        db = AppDatabase.getDatabase(this);

        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.expense_categories));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinnerCategory.getSelectedItem().toString();
                etOtherExpenseCategory.setVisibility(selected.equals("Other") ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateDateLabel();

        editTextDate.setOnClickListener(v -> new DatePickerDialog(
                ExpenseActivity.this,
                (view, year, month, dayOfMonth ) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        buttonSave.setOnClickListener(v -> saveExpense());
    }

    private void updateDateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextDate.setText(sdf.format(calendar.getTime()));
    }

    private void saveExpense() {
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        String customCategory = etOtherExpenseCategory.getText().toString().trim();
        String category = selectedCategory.equals("Other") ? customCategory : selectedCategory;

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
