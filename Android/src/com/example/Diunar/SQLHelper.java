package com.example.Diunar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
 
    private static final String DB_NAME = "mydatas.db";
    private static final int version = 1;
     
    public SQLHelper(Context context) {
        super(context, DB_NAME, null, version);
        // TODO Auto-generated constructor stub
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table Item(title varchar(100) not null , "
				   + "tag varchar(100) not null ,"
				   + "type varchar(30) ,"
				   + "time varchar(30) ,"
				   + "place varchar(100) ,"
				   + "x varchar(30) ,"
				   + "y varchar(30) ,"
				   + "contactInfo varchar(30) ,"
				   + "description  varchar(300) );"; 
        db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
 
}
