package com.example.smart_ei_manager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.smart_ei_manager.model.Budget;

import java.util.List;

@Dao
public interface BudgetDao {

    @Insert
    void insertBudget(Budget budget);

    @Update
    void updateBudget(Budget budget);

    @Delete
    void deleteBudget(Budget budget);

    @Query("SELECT * FROM budgets")
    List<Budget> getAllBudgets();

    @Query("SELECT * FROM budgets WHERE category = :category")
    List<Budget> getBudgetsByCategory(String category);
}
