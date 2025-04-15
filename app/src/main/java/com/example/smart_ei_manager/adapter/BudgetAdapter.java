package com.example.smart_ei_manager.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_ei_manager.R;
import com.example.smart_ei_manager.model.Budget;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private List<Budget> budgetList;
    private final OnItemClickListener listener;

    public BudgetAdapter(List<Budget> budgetList, OnItemClickListener listener) {
        this.budgetList = budgetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgetList.get(position);
        holder.tvCategory.setText(budget.getCategory());
        holder.tvAmount.setText("Rs. " + budget.getAmount());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(budget));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(budget));
    }

    @Override
    public int getItemCount() {
        return budgetList != null ? budgetList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBudgets(List<Budget> newList) {
        this.budgetList = newList;
        notifyDataSetChanged();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount;
        View btnEdit, btnDelete;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvBudgetCategory);
            tvAmount = itemView.findViewById(R.id.tvBudgetAmount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnItemClickListener {
        void onEditClick(Budget budget);
        void onDeleteClick(Budget budget);
    }
}
