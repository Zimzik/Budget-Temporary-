package com.example.zimzik.budget.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.zimzik.budget.data.db.dao.DivergenceDao;
import com.example.zimzik.budget.data.db.dao.RevenueDao;
import com.example.zimzik.budget.data.db.models.Divergence;
import com.example.zimzik.budget.data.db.models.Member;
import com.example.zimzik.budget.data.db.dao.MemberDao;
import com.example.zimzik.budget.data.db.models.Revenue;
import com.example.zimzik.budget.data.db.repo.DivergenceRepo;
import com.example.zimzik.budget.data.db.repo.MemberRepo;
import com.example.zimzik.budget.data.db.models.Period;
import com.example.zimzik.budget.data.db.dao.PeriodDao;
import com.example.zimzik.budget.data.db.repo.PeriodRepo;
import com.example.zimzik.budget.data.db.repo.RevenuesRepo;

@Database(entities = {Member.class, Period.class, Revenue.class, Divergence.class}, version = 1, exportSchema = false)
public abstract class AppDB extends RoomDatabase {
    private static AppDB sInstance;

    private final PeriodRepo mPeriodRepo = new PeriodRepo(monthDao());
    private final MemberRepo mMemberRepo = new MemberRepo(memberDao());
    private final RevenuesRepo mRevenuesRepo = new RevenuesRepo(revenueDao());
    private final DivergenceRepo mDivergenceRepo = new DivergenceRepo(divergenceDao());


    public synchronized static AppDB getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDB.class, "chorusMembers.db").build();
        }
        return sInstance;
    }

    public abstract MemberDao memberDao();

    public abstract PeriodDao monthDao();

    public abstract RevenueDao revenueDao();

    public abstract DivergenceDao divergenceDao();

    public PeriodRepo getPeriodRepo() {
        return mPeriodRepo;
    }

    public MemberRepo getMemberRepo() {
        return mMemberRepo;
    }

    public RevenuesRepo getRevenueRepo() { return mRevenuesRepo; }

    public DivergenceRepo getDivergenceRepo() {return  mDivergenceRepo; }
}