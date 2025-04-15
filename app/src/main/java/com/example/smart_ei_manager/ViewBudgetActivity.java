package com.example.smart_ei_manager;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_ei_manager.adapter.BudgetAdapter;
import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Budget;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ViewBudgetActivity extends AppCompatActivity {

    private EditText etSearchBudget;
    private BudgetAdapter budgetAdapter;
    private List<Budget> allBudgets = new ArrayList<>();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_budget);

        RecyclerView recyclerBudgets = findViewById(R.id.recyclerBudgets);
        etSearchBudget = findViewById(R.id.etSearchBudget);

        recyclerBudgets.setLayoutManager(new LinearLayoutManager(this));

        budgetAdapter = new BudgetAdapter(new ArrayList<>(), new BudgetAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Budget budget) {
                showEditBudgetDialog(budget);
            }

            @Override
            public void onDeleteClick(Budget budget) {
                showDeleteConfirmation(budget);
            }
        });

        recyclerBudgets.setAdapter(budgetAdapter);

        db = AppDatabase.getInstance(this);
        loadBudgetsFromDatabase();
        setupSearchFilter();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }

    }

    private void loadBudgetsFromDatabase() {
        new Thread(() -> {
            // ✅ corrected
            allBudgets = db.budgetDao().getAllBudgets();
            runOnUiThread(() -> budgetAdapter.setBudgets(allBudgets));
        }).start();
    }

    private void setupSearchFilter() {
        etSearchBudget.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBudgets(s.toString());
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterBudgets(String keyword) {
        List<Budget> filteredList = new ArrayList<>();
        for (Budget budget : allBudgets) {
            if (budget.getCategory().toLowerCase().contains(keyword.toLowerCase()) ||
                    String.valueOf(budget.getAmount()).contains(keyword)) {
                filteredList.add(budget);
            }
        }
        budgetAdapter.setBudgets(filteredList);
    }

    private void showEditBudgetDialog(Budget budget) {
        runOnUiThread(() -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Edit Budget for " + budget.getCategory());

            final EditText input = new EditText(this);
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setText(String.valueOf(budget.getAmount()));
            builder.setView(input);

            builder.setPositiveButton("Update", (dialog, which) -> {
                String updatedText = input.getText().toString().trim();
                if (!updatedText.isEmpty()) {
                    double newAmount = Double.parseDouble(updatedText);
                    budget.setAmount(newAmount);

                    new Thread(() -> {
                        db.budgetDao().updateBudget(budget);
                        runOnUiThread(() -> {
                            loadBudgetsFromDatabase(); // refresh
                            Toast.makeText(ViewBudgetActivity.this, "Budget updated", Toast.LENGTH_SHORT).show();
                                });
                    }).start();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });
    }

    private void showDeleteConfirmation(Budget budget) {
        runOnUiThread(() -> new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Budget")
                .setMessage("Are you sure you want to delete the budget for " + budget.getCategory() + "?")
                .setPositiveButton("Yes", (dialog, which) -> new Thread(() -> {
                    db.budgetDao().deleteBudget(budget);

                    runOnUiThread(() -> {
                        loadBudgetsFromDatabase();
                        Toast.makeText(this, "Budget deleted!", Toast.LENGTH_SHORT).show();

                        View view = findViewById(android.R.id.content);
                        Snackbar.make(view, "Budget deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", v -> new Thread(() -> {
                                    db.budgetDao().insertBudget(budget);
                                    runOnUiThread(() -> {
                                        loadBudgetsFromDatabase();
                                        Toast.makeText(this, "Budget restored", Toast.LENGTH_SHORT).show();
                                    });
                                }).start()).show();
                    });
                }).start())
                .setNegativeButton("No", null)
                .show());
    }

}
