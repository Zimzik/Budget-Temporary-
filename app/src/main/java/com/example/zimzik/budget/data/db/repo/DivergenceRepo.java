package com.example.zimzik.budget.data.db.repo;

import com.example.zimzik.budget.data.db.dao.DivergenceDao;
import com.example.zimzik.budget.data.db.models.Divergence;

import io.reactivex.Completable;
import io.reactivex.Single;

public class DivergenceRepo {
    private final DivergenceDao mDivergenceDao;

    public DivergenceRepo(DivergenceDao divergenceDao) {
        mDivergenceDao = divergenceDao;
    }

    public Completable insertDivergence(Divergence divergence) {
        return Completable.fromAction(() -> mDivergenceDao.insertDivergence(divergence));
    }

    public Completable updateDivergence(Divergence divergence) {
        return Completable.fromAction(() -> mDivergenceDao.updateDivergence(divergence));
    }

    public Completable delete(Divergence divergence) {
        return Completable.fromAction(() -> mDivergenceDao.delete(divergence));
    }

    public Single<Divergence> getAll() {
        return mDivergenceDao.getAll();
    }
}
