package com.timen4.ronnny.timemovies.bean;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.timen4.ronnny.timemovies.db.AppDatabase;

/**
 * Created by ronny on 2017/3/4.
 */
@Table(database = AppDatabase.class,name = SortResult.NAME,allFields = true,insertConflict = ConflictAction.IGNORE)
public class SortResult {

    public final static String NAME="Sort";

    @PrimaryKey(autoincrement = true)
    @Unique
    public int _id;
    @Unique(onUniqueConflict = ConflictAction.IGNORE)
    public String sort;
}
