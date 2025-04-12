package com.example.smart_ei_manager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_ei_manager.data.AppDatabase;
import com.example.smart_ei_manager.model.Transaction;

public class EditTransactionActivity extends AppCompatActivity {

    private EditText editAmount, editCategory, editDate, editNote;

    private AppDatabase db;
    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        editAmount = findViewById(R.id.editAmount);
        editCategory = findViewById(R.id.editCategory);
        editDate = findViewById(R.id.editDate);
        editNote = findViewById(R.id.editNote);
        Button btnUpdateTransaction = findViewById(R.id.btnUpdateTransaction);

        db = AppDatabase.getInstance(this);

        int transactionId = getIntent().getIntExtra("transaction_id", -1);
        if (transactionId == -1) {
            Toast.makeText(this, "Invalid transaction ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            transaction = db.transactionDao().getTransactionById(transactionId);
            runOnUiThread(() -> {
                if (transaction != null) {
                    editAmount.setText(String.valueOf(transaction.getAmount()));
                    editCategory.setText(transaction.getCategory());
                    editDate.setText(transaction.getDate());
                    editNote.setText(transaction.getNote());
                }
            });
        }).start();

        btnUpdateTransaction.setOnClickListener(v -> {
            String amountStr = editAmount.getText().toString().trim();
            String category = editCategory.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String note = editNote.getText().toString().trim();

            if (amountStr.isEmpty() || category.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            transaction.setAmount(amount);
            transaction.setCategory(category);
            transaction.setDate(date);
            transaction.setNote(note);

            new Thread(() -> {
                db.transactionDao().updateTransaction(transaction);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Transaction Updated Successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Set result code to indicate successful update
                    finish(); // Close and go back to transaction history
                });
            }).start();
        });
    }
}
