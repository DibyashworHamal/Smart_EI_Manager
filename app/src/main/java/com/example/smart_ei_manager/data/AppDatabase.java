package com.example.smart_ei_manager.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smart_ei_manager.model.Transaction;
import com.example.smart_ei_manager.model.Transaction;
import com.example.smart_ei_manager.model.Budget;
import com.example.smart_ei_manager.dao.TransactionDao;
import com.example.smart_ei_manager.dao.BudgetDao;  // ✅ Import this

/** @noinspection ALL*/
@Database(entities = {Transaction.class, Budget.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract TransactionDao transactionDao();
    public abstract BudgetDao budgetDao();
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "smart_ei_manager_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static AppDatabase getDatabase(Context context) {
        return getInstance(context);
    }
 }
