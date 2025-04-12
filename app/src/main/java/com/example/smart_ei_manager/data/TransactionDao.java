package com.example.smart_ei_manager.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.smart_ei_manager.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);
    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    List<Transaction> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    Transaction getTransactionById(int id);
    @Insert
    void insertTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);
    @Update
    void updateTransaction(Transaction transaction);
}
