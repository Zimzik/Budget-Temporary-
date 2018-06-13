package com.example.zimzik.budget.data.db.repo;

import com.example.zimzik.budget.data.db.dao.RevenueDao;
import com.example.zimzik.budget.data.db.models.Revenue;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class RevenuesRepo {
    private final RevenueDao mRevenueDao;

    public RevenuesRepo(RevenueDao revenueDao) {
        mRevenueDao = revenueDao;
    }

    public Completable insertRevenue(Revenue revenue) {
        return Completable.fromAction(() -> mRevenueDao.insertRevenyue(revenue));
    }

    public Completable ipdateRevenue(Revenue revenue) {
        return Completable.fromAction(() -> mRevenueDao.updateRevenue(revenue));
    }

    public Completable delete(Revenue revenue) {
        return Completable.fromAction(() -> mRevenueDao.delete(revenue));
    }

    public Single<List<Revenue>> getAll() {
        return mRevenueDao.getAll();
    }
}
