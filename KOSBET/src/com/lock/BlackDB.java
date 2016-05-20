package com.lock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

public class BlackDB extends SQLiteOpenHelper {

	public BlackDB(Context context) {
		super(context, "black.db", null, 1);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table info("
				+ "id integer not null primary key autoincrement,packName varchar(40) not null);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		

	}

}
