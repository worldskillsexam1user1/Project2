package com.aghajari.myapplication.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history")
public class Table {

   @PrimaryKey(autoGenerate = true)
   private int id;

   @ColumnInfo()
   private String name;

   @ColumnInfo()
   private String photo;

   @ColumnInfo()
   private int fieldId;

   @ColumnInfo()
   private String field;

   @ColumnInfo
   private int h;

   @ColumnInfo
   private int m;

   @ColumnInfo
   private int s;

   @ColumnInfo
   private int year;

   @ColumnInfo
   private int month;

   @ColumnInfo
   private int day;

   @ColumnInfo
   private long time;

   @ColumnInfo
   private long time2;

   public Table(String name, String photo, int fieldId, String field, int h, int m, int s, int year, int month, int day, long time) {
      this.name = name;
      this.photo = photo;
      this.fieldId = fieldId;
      this.field = field;
      this.h = h;
      this.m = m;
      this.s = s;
      this.year = year;
      this.month = month;
      this.day = day;
      this.time2 = (h * 60 * 1000L) + (m * 1000L) + s;
   }

   public long getTime2() {
      return time2;
   }

   public void setTime2(long time2) {
      this.time2 = time2;
   }

   public long getTime() {
      return time;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public String getPhoto() {
      return photo;
   }

   public void setPhoto(String photo) {
      this.photo = photo;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getFieldId() {
      return fieldId;
   }

   public void setFieldId(int fieldId) {
      this.fieldId = fieldId;
   }

   public String getField() {
      return field;
   }

   public int getId() {
      return id;
   }

   public void setField(String field) {
      this.field = field;
   }

   public int getH() {
      return h;
   }

   public void setH(int h) {
      this.h = h;
   }

   public int getM() {
      return m;
   }

   public void setM(int m) {
      this.m = m;
   }

   public int getS() {
      return s;
   }

   public void setS(int s) {
      this.s = s;
   }

   public int getYear() {
      return year;
   }

   public void setYear(int year) {
      this.year = year;
   }

   public int getMonth() {
      return month;
   }

   public void setMonth(int month) {
      this.month = month;
   }

   public int getDay() {
      return day;
   }

   public void setDay(int day) {
      this.day = day;
   }
}