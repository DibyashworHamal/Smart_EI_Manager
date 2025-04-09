package com.example.smart_ei_manager;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        pieChart = findViewById(R.id.pieChart);
        TextView tvIncome = findViewById(R.id.tvIncome);
        TextView tvExpenses = findViewById(R.id.tvExpenses);
        TextView tvSavings = findViewById(R.id.tvSavings);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        Button btnAddIncome = findViewById(R.id.btnAddIncome);
        Button btnAddExpense = findViewById(R.id.btnAddExpense);

        float incomeAmount = 3500f;
        float expenseAmount = 1200f;
        float savings = incomeAmount - expenseAmount;

        tvIncome.setText("Rs. " + incomeAmount);
        tvExpenses.setText("Rs. " + expenseAmount);
        tvSavings.setText("Rs. " + savings);
        tvTotalAmount.setText("Total: Rs. " + incomeAmount);

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
        dataSet.setColors(new int[] {
                getResources().getColor(R.color.orange),
                getResources().getColor(R.color.indigo)
        });
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(getResources().getColor(android.R.color.white));

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
            startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
            Toast.makeText(this, "Settings Opened", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}