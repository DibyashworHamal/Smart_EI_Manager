package com.example.smart_ei_manager;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Budget;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class AddBudgetActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText etOtherCategory, etBudgetAmount;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        etOtherCategory = findViewById(R.id.etOtherCategory);
        etBudgetAmount = findViewById(R.id.etBudgetAmount);
        Button btnSaveBudget = findViewById(R.id.btnSaveBudget);
        ImageButton btnViewBudgetHistory = findViewById(R.id.btnViewBudgetHistory);

        db = AppDatabase.getInstance(this);

        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.expense_categories));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Show/hide custom category input
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = categories.get(position);
                etOtherCategory.setVisibility(selected.equals("Other") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSaveBudget.setOnClickListener(v -> saveBudget());

        btnViewBudgetHistory.setOnClickListener(v -> {
            Intent intent = new Intent(AddBudgetActivity.this, ViewBudgetActivity.class);
            startActivity(intent);
        });
    }

    private void saveBudget() {
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        String customCategory = etOtherCategory.getText().toString().trim();

        String category = selectedCategory.equals("Other") ? customCategory : selectedCategory;
        if (category.isEmpty()) {
            Toast.makeText(this, "Please enter a category", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = etBudgetAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Budget> existing = db.budgetDao().getBudgetsByCategory(category);
            if (!existing.isEmpty()) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Budget already set for this category", Toast.LENGTH_SHORT).show());
            } else {
                Budget budget = new Budget();
                budget.setCategory(category);
                budget.setAmount(amount);
                db.budgetDao().insertBudget(budget);

                runOnUiThread(() ->
                        Toast.makeText(this, "Budget Saved", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddBudgetActivity.this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
