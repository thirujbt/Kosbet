package com.kosbet.dao;





import java.io.IOException;








import java.util.ArrayList;
import java.util.List;


import com.kosbet.bean.Listpackage;
import com.kosbet.java.DataBaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class ListPackageDao {

	protected static final String TAG = "DataAdapter";

	private  Context mContext;
	private SQLiteDatabase mDb;
	private DataBaseHelper mDbHelper;

	public ListPackageDao(Context context) {
		this.mContext = context;
		mDbHelper = new DataBaseHelper(mContext);
	}

	public ListPackageDao createDatabase() throws SQLException {
		try {
			mDbHelper.createDataBase();
		} catch (IOException mIOException) {
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public ListPackageDao open() throws SQLException {
		try {
			mDbHelper.openDataBase();
			mDbHelper.close();
			mDb = mDbHelper.getReadableDatabase();
		} catch (SQLException mSQLException) {
			Log.e(TAG, "open >>" + mSQLException.toString());
			throw mSQLException;
		}
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public int insertlist(String string, String iemi, String applicationnme,
			String packnme, String string2) {
		
		int row=0;
		try {
		String sql="insert into listpackage (commander_id,imei,applicationname,packagename,response) values('"+string+"','"+iemi+"','"+applicationnme+"','"+packnme+"','"+string2+"')";
			System.out.println("sql" + sql);
			mDb.execSQL(sql);
			row=1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	}

	public List<Listpackage> select(String string, String iemi, String string2) {
		
		 List<Listpackage> llp=new ArrayList<Listpackage>();
		 try{
			Cursor cursor = mDb.rawQuery("select * from listpackage where imei='"+iemi+"' and commander_id='ANN' and response='1'",null);
			int length=cursor.getCount();
			if(length!=0)
			{
			while (cursor.moveToNext()){
				System.out.println("inside cursor");
				Listpackage lpack=new Listpackage();
				lpack.setCommanderid(cursor.getString(0));
				lpack.setImei(cursor.getString(1));
				lpack.setApplicationname(cursor.getString(2));
				lpack.setPackagename(cursor.getString(3));
				lpack.setResponse(cursor.getString(4));
				System.out.println(cursor.getString(1));
				llp.add(lpack);		
			}
			cursor.close();
			}
			cursor.close();
		 }
		 catch(Exception e){
			 
			 
		 }
		 System.out.println(llp);
	return llp;		
	}
	
//	public boolean delete(String str){
//		database=black.getWritableDatabase();
//		if(database.isOpen()){
//			if(database.delete("info", "packName=?", new String[]{str})==1){
//				close();
//				return true;
//			}else{
//				close();
//				return false;
//			}		
//		}
//		return false;
//	}
	public List<Listpackage> getAll(){
		List<Listpackage> packs=new ArrayList<Listpackage>();
		try{
			Cursor cursor=mDb.rawQuery("select packName from info",null);
			while(cursor.moveToNext()){
				Listpackage lpack=new Listpackage();
				lpack.setPackagename(cursor.getString(0));
				packs.add(lpack);
				
			}
//			cursor.close();
//			close();
					
		}
	 
	 catch(Exception e){
		 
	 }
		
		return packs;
		
	}

	public int insetpack(String packName) {
		int row=0;
		try {
			//String latlong ="insert into [latlongval] (Latitude,Longitude,IMEI,Time,Date) values("+latitude+","+longtude+","+iMEI_Number+","+todaysdate+","+todaystime+")" ;
		String sql="insert into info (packName) values('"+packName+"')";
			System.out.println("sql" + sql);
			mDb.execSQL(sql);
			row=1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	}
	public void delete(){
		
				try
				{
		mDb.execSQL("delete from listpackage");
				
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
