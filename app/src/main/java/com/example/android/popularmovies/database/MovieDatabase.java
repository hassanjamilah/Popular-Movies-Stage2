package com.example.android.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MovieEntity.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    private static final Object lock = new Object();
    private static final String DATABASE_NAME = "MvoieDatabase";
    private static MovieDatabase mInstance;

    public static MovieDatabase getmInstance(Context context) {
        if (mInstance == null) {
            synchronized (lock) {
                mInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, DATABASE_NAME
                ).build();
            }
        }
        return mInstance;
    }

    public abstract MovieDAO movieDAO();
}
