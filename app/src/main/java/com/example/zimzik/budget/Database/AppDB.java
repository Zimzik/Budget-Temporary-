package com.example.zimzik.budget.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Member.class, Period.class}, version = 1, exportSchema = false)
public abstract class AppDB extends RoomDatabase {
    private static AppDB sInstance;

    private final PeriodRepo mPeriodRepo = new PeriodRepo(monthDao());
    private final MemberRepo mMemberRepo = new MemberRepo(memberDao());

    public synchronized static AppDB getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDB.class, "chorusMembers.db").build();
        }
        return sInstance;
    }

    public abstract MemberDao memberDao();

    public abstract PeriodDao monthDao();

    public PeriodRepo getPeriodRepo() {
        return mPeriodRepo;
    }

    public MemberRepo getMemberRepo() {
        return mMemberRepo;
    }
}