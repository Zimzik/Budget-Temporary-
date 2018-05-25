package com.example.zimzik.budget.Database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface MemberDao {
    @Insert
    void insertAll(Member member);

    @Delete
    void delete(Member member);

    @Query("Select * FROM member")
    //Single<List<Member>> getAllMembers();
    List<Member> getAllMembers();

    @Update
    void update(Member member);
}
