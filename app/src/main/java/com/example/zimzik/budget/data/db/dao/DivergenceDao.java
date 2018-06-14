package com.example.zimzik.budget.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.zimzik.budget.data.db.models.Divergence;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface DivergenceDao {
    @Insert
    void insertDivergence(Divergence divergence);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateDivergence(Divergence divergence);

    @Delete
    void delete(Divergence divergence);

    @Query("SELECT * FROM Divergence")
    Single<List<Divergence>> getAll();
}
