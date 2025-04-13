package com.example.smart_ei_manager;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewActivity extends AppCompatActivity {

    private BarChart barChart;
    private BarChart barChartIncome;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        barChart = findViewById(R.id.barChart);
        barChartIncome = findViewById(R.id.barChartIncome);
        db = AppDatabase.getInstance(this);

        loadCategoryData();        // Expense bar chart
        loadIncomeCategoryData();  // Income bar chart
    }

    private void loadCategoryData() {
        new Thread(() -> {
            List<Transaction> transactions = db.transactionDao().getAllTransactions();
            Map<String, Float> categoryMap = new HashMap<>();

            for (Transaction t : transactions) {
                if (t.getType().equalsIgnoreCase("expense")) {
                    float current = categoryMap.getOrDefault(t.getCategory(), 0f);
                    categoryMap.put(t.getCategory(), current + (float) t.getAmount());
                }
            }

            List<BarEntry> entries = new ArrayList<>();
            List<String> categoryLabels = new ArrayList<>();
            int index = 0;

            for (Map.Entry<String, Float> entry : categoryMap.entrySet()) {
                entries.add(new BarEntry(index, entry.getValue()));
                categoryLabels.add(entry.getKey());
                index++;
            }

            BarDataSet dataSet = new BarDataSet(entries, "Expenses by Category");
            dataSet.setColors(Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(14f);

            BarData data = new BarData(dataSet);

            runOnUiThread(() -> setupBarChart(barChart, data, categoryLabels));
        }).start();
    }

    private void loadIncomeCategoryData() {
        new Thread(() -> {
            List<Transaction> transactions = db.transactionDao().getAllTransactions();
            Map<String, Float> incomeMap = new HashMap<>();

            for (Transaction t : transactions) {
                if (t.getType().equalsIgnoreCase("income")) {
                    float current = incomeMap.getOrDefault(t.getCategory(), 0f);
                    incomeMap.put(t.getCategory(), current + (float) t.getAmount());
                }
            }

            List<BarEntry> entries = new ArrayList<>();
            List<String> categoryLabels = new ArrayList<>();
            int index = 0;

            for (Map.Entry<String, Float> entry : incomeMap.entrySet()) {
                entries.add(new BarEntry(index, entry.getValue()));
                categoryLabels.add(entry.getKey());
                index++;
            }

            BarDataSet dataSet = new BarDataSet(entries, "Income by Category");
            dataSet.setColors(Color.CYAN, Color.YELLOW, Color.LTGRAY, Color.GREEN, Color.BLUE);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(14f);

            BarData data = new BarData(dataSet);

            runOnUiThread(() -> setupBarChart(barChartIncome, data, categoryLabels));
        }).start();
    }

    private void setupBarChart(BarChart chart, BarData data, List<String> labels) {
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.setExtraBottomOffset(30f);
        chart.animateY(1000);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < labels.size()) {
                    return labels.get((int) value);
                } else {
                    return "";
                }
            }
        });

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setLabelRotationAngle(25f);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(8f);

        chart.getAxisRight().setEnabled(false);
        chart.invalidate();
    }
}
