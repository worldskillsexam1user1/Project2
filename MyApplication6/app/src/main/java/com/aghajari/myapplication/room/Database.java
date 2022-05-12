package com.aghajari.myapplication.room;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aghajari.myapplication.MyApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {Table.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

   public abstract MyDao myDao();

   private static volatile Database INSTANCE;
   private static final int NUMBER_OF_THREADS = 4;
   static final ExecutorService databaseWriteExecutor =
        Executors.newFixedThreadPool(NUMBER_OF_THREADS);

   public static Database getDatabase() {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(MyApplication.context,
                            Database.class, "database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}