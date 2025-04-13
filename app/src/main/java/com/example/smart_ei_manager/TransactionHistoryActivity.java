package com.example.smart_ei_manager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_ei_manager.adapter.TransactionAdapter;
import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private AppDatabase db;
    private EditText etSearch;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        @SuppressLint("WrongViewCast")
        androidx.appcompat.widget.AppCompatTextView titleText = findViewById(R.id.tvTitle);
        if (titleText != null) {
            titleText.setText(getString(R.string.smart_e_i_manager));
        }

        etSearch = findViewById(R.id.etSearch);
        AtomicReference<RecyclerView> recyclerTransactions = new AtomicReference<>(findViewById(R.id.recyclerTransactions));
        recyclerTransactions.get().setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getInstance(this);

        new Thread(() -> {
            transactionList = db.transactionDao().getAllTransactions();
            runOnUiThread(() -> {
                adapter = new TransactionAdapter(this, transactionList, new TransactionAdapter.OnItemClickListener() {
                    @Override
                    public void onEditClick(Transaction transaction) {
                        Intent intent = new Intent(TransactionHistoryActivity.this, EditTransactionActivity.class);
                        intent.putExtra("transaction_id", transaction.getId());
                        startActivityForResult(intent, 100);
                        Toast.makeText(TransactionHistoryActivity.this, "Edit Transaction Here!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDeleteClick(Transaction transaction) {
                        new AlertDialog.Builder(TransactionHistoryActivity.this)
                                .setTitle("Delete Transaction")
                                .setMessage("Are you sure you want to delete this transaction?")
                                .setPositiveButton("Yes", (dialog, which) -> new Thread(() -> {
                                    db.transactionDao().deleteTransaction(transaction);
                                    List<Transaction> updatedList = db.transactionDao().getAllTransactions();
                                    runOnUiThread(() -> {
                                        transactionList = updatedList;
                                        adapter.setTransactions(updatedList);
                                        Toast.makeText(TransactionHistoryActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    });
                                }).start())
                                .setNegativeButton("No", null)
                                .show();
                    }
                });

                recyclerTransactions.get().setAdapter(adapter);
                setupSearchFilter();
            });
        }).start();
    }

    private void setupSearchFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterTransactions(String keyword) {
        List<Transaction> filteredList = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getNote().toLowerCase().contains(keyword.toLowerCase()) ||
                    transaction.getCategory().toLowerCase().contains(keyword.toLowerCase()) ||
                    String.valueOf(transaction.getAmount()).contains(keyword)) {
                filteredList.add(transaction);
            }
        }
        adapter.setTransactions(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            transactionList = db.transactionDao().getAllTransactions();
            runOnUiThread(() -> {
                if (adapter != null) {
                    adapter.setTransactions(transactionList);
                }
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            new Thread(() -> {
                List<Transaction> updatedList = db.transactionDao().getAllTransactions();
                runOnUiThread(() -> {
                    transactionList = updatedList;
                    adapter.setTransactions(updatedList);
                    Toast.makeText(this, "Updated List", Toast.LENGTH_SHORT).show();
                });
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transaction_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            showSortMenu();
            return true;
        } else if (item.getItemId() == R.id.action_filter) {
            showDateRangePicker();
            return true;
        } else if (item.getItemId() == R.id.action_overview) {
            Intent intent = new Intent(TransactionHistoryActivity.this, OverviewActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Opening Overview", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortMenu() {
        View anchorView = findViewById(R.id.etSearch);
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenu().add("Date ↓ Newest First");
        popupMenu.getMenu().add("Date ↑ Oldest First");
        popupMenu.getMenu().add("Amount ↓ High to Low");
        popupMenu.getMenu().add("Amount ↑ Low to High");

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String selected = menuItem.getTitle().toString();
            switch (selected) {
                case "Date ↓ Newest First": sortTransactionsByDateDesc(); break;
                case "Date ↑ Oldest First": sortTransactionsByDateAsc(); break;
                case "Amount ↓ High to Low": sortTransactionsByAmountDesc(); break;
                case "Amount ↑ Low to High": sortTransactionsByAmountAsc(); break;
            }
            return true;
        });

        popupMenu.show();
    }

    private void showDateRangePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog startDatePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar startCal = Calendar.getInstance();
            startCal.set(year, month, dayOfMonth);
            String startDate = sdf.format(startCal.getTime());

            DatePickerDialog endDatePicker = new DatePickerDialog(this, (view2, year2, month2, dayOfMonth2) -> {
                Calendar endCal = Calendar.getInstance();
                endCal.set(year2, month2, dayOfMonth2);
                String endDate = sdf.format(endCal.getTime());
                filterTransactionsByDateRange(startDate, endDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            endDatePicker.setTitle("Select End Date");
            endDatePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        startDatePicker.setTitle("Select Start Date");
        startDatePicker.show();
    }

    private void filterTransactionsByDateRange(String startDateStr, String endDateStr) {
        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            List<Transaction> filteredList = new ArrayList<>();
            for (Transaction transaction : transactionList) {
                Date transactionDate = sdf.parse(transaction.getDate());
                if (transactionDate != null && !transactionDate.before(startDate) && !transactionDate.after(endDate)) {
                    filteredList.add(transaction);
                }
            }

            adapter.setTransactions(filteredList);
            Toast.makeText(this, "Filtered from " + startDateStr + " to " + endDateStr, Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Date parse error!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sortTransactionsByDateDesc() {
        transactionList.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        adapter.setTransactions(transactionList);
    }

    private void sortTransactionsByDateAsc() {
        transactionList.sort(Comparator.comparing(Transaction::getDate));
        adapter.setTransactions(transactionList);
    }

    private void sortTransactionsByAmountDesc() {
        transactionList.sort((t1, t2) -> Double.compare(t2.getAmount(), t1.getAmount()));
        adapter.setTransactions(transactionList);
    }

    private void sortTransactionsByAmountAsc() {
        transactionList.sort(Comparator.comparingDouble(Transaction::getAmount));
        adapter.setTransactions(transactionList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
