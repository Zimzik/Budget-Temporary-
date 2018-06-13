package com.example.zimzik.budget.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.zimzik.budget.data.db.models.Revenue;

import java.util.List;

import io.reactivex.Single;
@Dao
public interface RevenueDao {

    @Insert
    void insertRevenyue(Revenue revenue);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateRevenue(Revenue revenue);

    @Delete
    void delete(Revenue revenue);

    @Query("SELECT * FROM Revenue")
    Single<List<Revenue>> getAll();
}
