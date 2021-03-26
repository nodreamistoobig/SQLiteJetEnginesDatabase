package com.example.sqlapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {
    SQLiteDatabase sqlDB;

    public void create() {
        sqlDB.execSQL("CREATE TABLE IF NOT EXISTS Engines(_id int, Name varchar, Producer varchar, Thrust int);");
    }


    public int getCount(){
        Cursor cursor = sqlDB.rawQuery("SELECT COUNT(*) FROM Engines", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public void insert(int count, String name, String producer, String thrust){
        sqlDB.execSQL("INSERT INTO Engines  VALUES(" + count + " , '" + name + "' , '" + producer + "', " + thrust + " );");
    }

    public void update(int id, String name, String producer, String thrust){
        sqlDB.execSQL("UPDATE Engines SET Name = '" + name + "' , Producer = '" + producer + "', Thrust = '" + thrust + "' WHERE _id = " + id);
    }

    public Cursor selectAll(){
        return sqlDB.rawQuery("SELECT * FROM Engines", null);
    }

    public Cursor sortByColumn(String column, String direction){
        return sqlDB.rawQuery("SELECT * FROM Engines ORDER BY " + column + " " + direction, null);
    }
}
