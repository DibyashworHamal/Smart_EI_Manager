package com.example.smart_ei_manager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_ei_manager.R;
import com.example.smart_ei_manager.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final Context context;
    private List<Transaction> transactionList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Transaction transaction);
        void onDeleteClick(Transaction transaction);
    }

    public TransactionAdapter(Context context, List<Transaction> transactionList, OnItemClickListener listener) {
        this.context = context;
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvCategory.setText(transaction.getCategory());
        holder.tvAmount.setText("Rs. " + transaction.getAmount());
        holder.tvDate.setText(transaction.getDate());
        holder.tvNote.setText(transaction.getNote());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(transaction));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory, tvAmount, tvDate, tvNote;
        ImageButton btnEdit, btnDelete;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Optional: method to update list
    @SuppressLint("NotifyDataSetChanged")
    public void setTransactions(List<Transaction> transactions) {
        this.transactionList = transactions;
        notifyDataSetChanged();
    }
}
