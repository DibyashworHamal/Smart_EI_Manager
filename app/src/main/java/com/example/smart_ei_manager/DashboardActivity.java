package com.example.smart_ei_manager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;

import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvIncome, tvExpenses, tvSavings;
    private ImageView imgToggleBalance;

    private float totalIncome = 0f;
    private float totalExpense = 0f;
    private boolean isBalanceVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DashboardActivity", "onCreate called");
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        pieChart = findViewById(R.id.pieChart);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpenses = findViewById(R.id.tvExpenses);
        tvSavings = findViewById(R.id.tvSavings);
        Button btnAddIncome = findViewById(R.id.btnAddIncome);
        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        imgToggleBalance = findViewById(R.id.imgToggleBalance);

        btnAddIncome.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, IncomeActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Add Income Here!", Toast.LENGTH_SHORT).show();
        });

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ExpenseActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Add Expense Here!", Toast.LENGTH_SHORT).show();
        });

        imgToggleBalance.setOnClickListener(v -> toggleBalanceVisibility());
    }

    private void setupPieChart(float income, float expense) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(income, "Income"));
        entries.add(new PieEntry(expense, "Expenses"));

        PieDataSet dataSet = new PieDataSet(entries, "Income vs Expenses");
        dataSet.setColors(
                ContextCompat.getColor(this, R.color.orange),
                ContextCompat.getColor(this, R.color.indigo)
        );
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.white));

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setCenterText("Overview");
        pieChart.setCenterTextSize(16f);
        pieChart.animateY(1400);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
    }

    @SuppressLint("SetTextI18n")
    private void toggleBalanceVisibility() {
        if (isBalanceVisible) {
            tvIncome.setText("Rs. ****");
            tvExpenses.setText("Rs. ****");
            tvSavings.setText("Rs. ****");
            imgToggleBalance.setImageResource(R.drawable.ic_visibility_off);
            isBalanceVisible = false;
        } else {
            float savings = totalIncome - totalExpense;
            tvIncome.setText("Rs. " + totalIncome);
            tvExpenses.setText("Rs. " + totalExpense);
            tvSavings.setText("Rs. " + savings);
            imgToggleBalance.setImageResource(R.drawable.ic_visibility);
            isBalanceVisible = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_history) {
            Intent intent = new Intent(DashboardActivity.this, TransactionHistoryActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Transaction History Opened", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Settings Opened", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(DashboardActivity.this, AboutActivity.class);
            startActivity(intent);
            Toast.makeText(this, "About Opened", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_logout) {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        AppDatabase db = AppDatabase.getDatabase(this);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Transaction> transactions = db.transactionDao().getAllTransactions();

            float income = 0f;
            float expense = 0f;

            for (Transaction t : transactions) {
                if (t.getType().equalsIgnoreCase("income")) {
                    income += (float) t.getAmount();
                } else if (t.getType().equalsIgnoreCase("expense")) {
                    expense += (float) t.getAmount();
                }
            }

            float savings = income - expense;

            totalIncome = income;
            totalExpense = expense;

            runOnUiThread(() -> {
                if (isBalanceVisible) {
                    tvIncome.setText("Rs. " + totalIncome);
                    tvExpenses.setText("Rs. " + totalExpense);
                    tvSavings.setText("Rs. " + savings);
                } else {
                    tvIncome.setText("Rs. ****");
                    tvExpenses.setText("Rs. ****");
                    tvSavings.setText("Rs. ****");
                }

                setupPieChart(totalIncome, totalExpense);
            });
        });
    }
}
