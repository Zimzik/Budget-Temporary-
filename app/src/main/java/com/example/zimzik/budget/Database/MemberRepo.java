package com.example.zimzik.budget.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class MemberRepo {
    private final MemberDao mMemberDao;

    MemberRepo(MemberDao memberDao) {this.mMemberDao = memberDao;}

    public Completable insertAll(Member member) {
        return Completable.fromAction(() -> mMemberDao.insertAll(member));
    }


    public Completable delete(Member member) {
        return Completable.fromAction(() -> mMemberDao.delete(member));
    }

    /*public Single<List<Member>> getAllMembers() {
        return mMemberDao.getAllMembers();
    }*/

    public Completable update(Member member) {
        return Completable.fromAction(() -> mMemberDao.update(member));
    }
}
