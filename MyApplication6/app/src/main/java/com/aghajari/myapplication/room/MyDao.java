package com.aghajari.myapplication.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyDao {

   @Insert()
   void insert(Table table);

   @Query("DELETE FROM `history`")
   void deleteAll();


   @Query("SELECT  * FROM `history` WHERE fieldId = :id GROUP BY name ORDER BY time2")
   LiveData<List<Table>> selectByField(int id);

}