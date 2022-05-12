package com.aghajari.myapplication.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class DBViewModel extends ViewModel {

   private MyRepository mRepository;


   public DBViewModel () {
       super();
       mRepository = new MyRepository();
   }

   public LiveData<List<Table>> getListTab2(int id) {
       return mRepository.list(id);
   }
   public void insert(Table word) { mRepository.insert(word); }
}
