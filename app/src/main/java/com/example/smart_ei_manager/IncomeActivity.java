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
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

public class IncomeActivity extends AppCompatActivity {

    private EditText etDate, etAmount, etNote, etOtherCategory;
    private Spinner spinnerCategory;
    private AppDatabase db;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        spinnerCategory = findViewById(R.id.spinnerIncomeCategory);
        etAmount = findViewById(R.id.etIncomeAmount);
        etNote = findViewById(R.id.etIncomeNote);
        etDate = findViewById(R.id.etIncomeDate);
        etOtherCategory = findViewById(R.id.etOtherIncomeCategory);
        Button btnSaveIncome = findViewById(R.id.btnSaveIncome);

        db = AppDatabase.getDatabase(this);

        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.income_categories));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = categories.get(position);
                etOtherCategory.setVisibility(selected.equals("Other") ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        updateDateLabel();
        etDate.setOnClickListener(v -> new DatePickerDialog(
                IncomeActivity.this,
                (view, year , month , dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        btnSaveIncome.setOnClickListener(v -> saveIncome());
    }

    private void updateDateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(sdf.format(calendar.getTime()));
    }

    private void saveIncome() {
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        String customCategory = etOtherCategory.getText().toString().trim();
        String category = selectedCategory.equals("Other") ? customCategory : selectedCategory;

        String amountStr = etAmount.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        String date = etDate.getText().toString();

        if (amountStr.isEmpty() || date.isEmpty() || category.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Transaction transaction = new Transaction("income", category, amount, note, date);

        new Thread(() -> {
            db.transactionDao().insert(transaction);
            runOnUiThread(() -> {
                Toast.makeText(this, "Income saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
