package com.ctp.android.ppm.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Used to store the last logged user in the DB.
 * 
 * @author kbiels
 *
 */
public class LoginDbAdapter {

	public static final String KEY_ID = "_iduser";
	public static final String KEY_USER = "user";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_AUTOLOGIN = "autologin";
	public static final String DATABASE_NAME = "ctpppm";
	private static final String DATABASE_TABLE = "users";
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_CREATE = "create table users (_iduser integer primary key autoincrement, "
			+ "user text not null, password text not null, autologin BOOLEAN default 0);";
	public static final String TAG = "LoginDbAdapter";

	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mDb;
	private Context mContext;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param context
	 *            the Context within which to work
	 */
	public LoginDbAdapter(Context context) {
		mContext = context;
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public LoginDbAdapter open() {
		mDatabaseHelper = new DatabaseHelper(mContext);
		mDb = mDatabaseHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the database connection.
	 */
	public void close() {
		mDatabaseHelper.close();
		mDb.close();
	}

	/**
	 * Saving a given user in the db, when successful returning the rowId. 
	 * If not successful returning -1.
	 * 
	 * @param username
	 * @param password
	 * @param autologin
	 * @return rowId when saved, -1 when received error
	 */
	public long saveNewUser(String username, String password, int autologin) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_USER, username);
		contentValues.put(KEY_PASSWORD, password);
		contentValues.put(KEY_AUTOLOGIN, autologin);
		
		return mDb.insert(DATABASE_TABLE, null, contentValues);
	}
	
	/**
	 * Returns a cursor positioned at the last logged user, which will be the one with the biggest id. 
	 * @return Cursor over the last logged user
	 */
	public Cursor fetchLastUserLogged() {
		Cursor cursor =
				mDb.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_USER, KEY_PASSWORD, KEY_AUTOLOGIN},
						null, null, null, null, KEY_ID, null);
		if(cursor != null) {
			cursor.moveToLast();
		}
			
		return cursor;
	}
}
