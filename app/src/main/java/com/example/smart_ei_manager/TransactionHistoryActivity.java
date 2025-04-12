package com.example.smart_ei_manager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_ei_manager.adapter.TransactionAdapter;
import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Transaction;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/** @noinspection deprecation*/
public class TransactionHistoryActivity extends AppCompatActivity {

    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TransactionHistoryActivity", "onCreate called");
        setContentView(R.layout.activity_transaction_history);

        if (getIntent().getExtras() != null) {
            Log.d("TransactionHistoryActivity", "Intent Extras: " + getIntent()
                    .getExtras().toString());
        } else {
            Log.d("TransactionHistoryActivity", "No Intent Extras Received");
        }

        AtomicReference<RecyclerView> recyclerTransactions = new AtomicReference<>(findViewById(R.id.recyclerTransactions));
        recyclerTransactions.get().setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getInstance(this);
        Log.d("TransactionHistoryActivity", "Database instance: " + db);
        new Thread(() -> {
            transactionList = db.transactionDao().getAllTransactions();

            runOnUiThread(() -> {
                adapter = new TransactionAdapter(this, transactionList, new TransactionAdapter.OnItemClickListener() {
                    @Override
                    public void onEditClick(Transaction transaction) {
                        Intent intent = new Intent(TransactionHistoryActivity.this, EditTransactionActivity.class);
                        intent.putExtra("transaction_id", transaction.getId());
                        startActivity(intent);
                        Toast.makeText(TransactionHistoryActivity.this, "Edit Transaction Here!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDeleteClick(Transaction transaction) {
                        new AlertDialog.Builder(TransactionHistoryActivity.this)
                                .setTitle("Delete Transaction")
                                .setMessage("Are you sure you want to delete this transaction?")
                                .setPositiveButton("Yes", (dialog, which) -> new Thread(() -> {
                                    db.transactionDao().deleteTransaction(transaction);

                                    // Fetch updated list
                                    List<Transaction> updatedList = db.transactionDao().getAllTransactions();

                                    runOnUiThread(() -> {
                                        adapter.setTransactions(updatedList);
                                        Toast.makeText(TransactionHistoryActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    });
                                }).start())
                                .setNegativeButton("No", null)
                                .show();
                    }


                });

                recyclerTransactions.set(findViewById(R.id.recyclerTransactions));
                recyclerTransactions.get().setLayoutManager(new LinearLayoutManager(this));
                recyclerTransactions.get().setAdapter(adapter);
            });
        }).start();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Ensures activity is properly closed and triggers onResume in Dashboard
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

}
