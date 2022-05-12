package com.aghajari.myapplication.room;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MyRepository {

    private MyDao myDao;


    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    public MyRepository() {
        Database db = Database.getDatabase();
        myDao = db.myDao();
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Table table) {
        Database.databaseWriteExecutor.execute(() -> {
            myDao.insert(table);
        });
    }

    LiveData<List<Table>> list(int id){
        return myDao.selectByField(id);
    }
}