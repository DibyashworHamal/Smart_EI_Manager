package com.example.smart_ei_manager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import android.content.Intent;
import android.widget.Toast;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;


public class DashboardActivity extends AppCompatActivity {

    private PieChart pieChart;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DashboardActivity", "onCreate called");
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        pieChart = findViewById(R.id.pieChart);
        TextView tvIncome = findViewById(R.id.tvIncome);
        TextView tvExpenses = findViewById(R.id.tvExpenses);
        TextView tvSavings = findViewById(R.id.tvSavings);
        Button btnAddIncome = findViewById(R.id.btnAddIncome);
        Button btnAddExpense = findViewById(R.id.btnAddExpense);

        float incomeAmount = 3500f;
        float expenseAmount = 1200f;
        float savings = incomeAmount - expenseAmount;

        tvIncome.setText("Rs. " + incomeAmount);
        tvExpenses.setText("Rs. " + expenseAmount);
        tvSavings.setText("Rs. " + savings);

        setupPieChart(incomeAmount, expenseAmount);

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
    }
       private void setupPieChart(float income, float expense) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(income, "Income"));
        entries.add(new PieEntry(expense, "Expenses"));

        PieDataSet dataSet = new PieDataSet(entries, "Income vs Expenses");
        dataSet.setColors(ContextCompat.getColor(this, R.color.orange),
                ContextCompat.getColor(this, R.color.indigo));
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Settings Opened", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(DashboardActivity.this, AboutActivity.class);
            startActivity(intent);
            Toast.makeText(this, "About Opened", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.action_logout) {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish();
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

            float totalIncome = 0f;
            float totalExpense = 0f;

            for (Transaction t : transactions) {
                if (t.getType().equalsIgnoreCase("income")) {
                    totalIncome += (float) t.getAmount();
                } else if (t.getType().equalsIgnoreCase("expense")) {
                    totalExpense += (float) t.getAmount();
                }
            }

            float savings = totalIncome - totalExpense;

            float finalTotalIncome = totalIncome;
            float finalTotalExpense = totalExpense;
            runOnUiThread(() -> {
                TextView tvIncome = findViewById(R.id.tvIncome);
                TextView tvExpenses = findViewById(R.id.tvExpenses);
                TextView tvSavings = findViewById(R.id.tvSavings);

                tvIncome.setText("Rs. " + finalTotalIncome);
                tvExpenses.setText("Rs. " + finalTotalExpense);
                tvSavings.setText("Rs. " + savings);

                setupPieChart(finalTotalIncome, finalTotalExpense);
            });
        });
    }

}