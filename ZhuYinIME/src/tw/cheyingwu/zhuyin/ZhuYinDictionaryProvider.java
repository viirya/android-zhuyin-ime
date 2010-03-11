package tw.cheyingwu.zhuyin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class ZhuYinDictionaryProvider extends ContentProvider {
	
	private static final String TAG = "ZhuYinIME";
	private static final String DBWORDS_NAME = "ZhuYinWords.db";
	private static final String DBPHRASES_NAME = "ZhuYinPhrases.db";
	private static final int DATABASE_VERSION = 2010022604;
	private static final String NOTES_TABLE_NAME = "zi";
	private static final Integer INPUT_DB_FILES = 10; // According to ZhuYin.dbx in assets

	private static HashMap<String, String> sNotesProjectionMap;
	private static HashMap<String, String> codeMap;
	private static String mSearchCode;

	private Context context;
	private SQLiteDatabase dbWords;
	private SQLiteDatabase dbPhrases;

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private class DatabaseHelper extends SQLiteOpenHelper {

		private String DB_PATH = "/data/data/tw.cheyingwu.zhuyin/databases/";
		private final Context myContext;
		private String dbName;

		DatabaseHelper(Context context, String DBName) {
			super(context, DBName, null, DATABASE_VERSION);
			this.dbName = DBName;
			this.myContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db){

			// Nothing to do here, since we already have data!!
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}

		/**
		 * Creates a empty database on the system and rewrites it with your own
		 * database.
		 * */
		public void createDataBase() throws IOException {

			boolean dbExist = checkDataBase();

			if (dbExist) {
				// do nothing - database already exist
			} else {

				// By calling this method and empty database will be created
				// into the default system path
				// of your application so we are gonna be able to overwrite that
				// database with our database.
				this.getReadableDatabase();
				this.close();

				try {
					copyDataBase();

				} catch (IOException e) {

					throw new Error("Error copying database");

				}
			}

		}

		/**
		 * Check if the database already exist to avoid re-copying the file each
		 * time you open the application.
		 * 
		 * @return true if it exists, false if it doesn't
		 */
		private boolean checkDataBase() {

			SQLiteDatabase checkDB = null;

			try {
				String myPath = DB_PATH + this.dbName;
				Boolean exists = (new File(myPath)).exists();
				if(exists) {
					checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
				}

			} catch (SQLiteException e) {
				//Log.i(TAG, "database does't exist yet");
			}

			if (checkDB != null) {
				checkDB.close();
			}

			return checkDB != null ? true : false;
		}
		
		/**
		 * Copies your database from your local assets-folder to the just
		 * created empty database in the system folder, from where it can be
		 * accessed and handled. This is done by transfering bytestream.
		 * */
		private void copyDataBase() throws IOException {

			//Log.i(TAG, "start to copy database");

			InputStream myInput;
			String outFileName;
			OutputStream myOutput;
			Integer fIdx;
			
			// Path to the just created empty db
			outFileName = DB_PATH + this.dbName;
			// Open the empty db as the output stream
			myOutput = new FileOutputStream(outFileName);

			for(fIdx = 0; fIdx < INPUT_DB_FILES; fIdx++) {

				// Open your local db as the input stream
				try {
					myInput = myContext.getAssets().open(this.dbName + fIdx.toString());
				} catch (IOException e)
				{
					// No such file
					System.out.println("Unable to open input file " + this.dbName + fIdx.toString() + "!!");
					break;
				}			

				// transfer bytes from the inputfile to the outputfile
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer)) > 0) {
					myOutput.write(buffer, 0, length);
				}

				myInput.close();
			}
				
			//Log.i(TAG, "copy database done");

			// Close the streams
			myOutput.flush();
			myOutput.close();			
		}	
	}

	private void ZhuYinDictionaryCodeMap() {

		codeMap = new HashMap<String, String>();
		
		codeMap.put("12549", "10");
		codeMap.put("12550", "11");
		codeMap.put("12551", "12");
		codeMap.put("12552", "13");
		codeMap.put("12553", "14");
		codeMap.put("12554", "15");
		codeMap.put("12555", "16");
		codeMap.put("12556", "17");
		codeMap.put("12557", "18");
		codeMap.put("12558", "19");
		codeMap.put("12559", "1A");
		codeMap.put("12560", "1B");
		codeMap.put("12561", "1C");
		codeMap.put("12562", "1D");
		codeMap.put("12563", "1E");
		codeMap.put("12564", "1F");
		codeMap.put("12565", "1G");
		codeMap.put("12566", "1H");
		codeMap.put("12567", "1I");
		codeMap.put("12568", "1J");
		codeMap.put("12569", "1K");
		codeMap.put("12570", "20");
		codeMap.put("12571", "21");
		codeMap.put("12572", "22");
		codeMap.put("12573", "23");
		codeMap.put("12574", "24");
		codeMap.put("12575", "25");
		codeMap.put("12576", "26");
		codeMap.put("12577", "27");
		codeMap.put("12578", "28");
		codeMap.put("12579", "29");
		codeMap.put("12580", "2A");
		codeMap.put("12581", "2B");
		codeMap.put("12582", "2C");
		codeMap.put("12583", "30");
		codeMap.put("12584", "31");
		codeMap.put("12585", "32");
		codeMap.put("729",  "40");
		codeMap.put("714",  "41");		
		codeMap.put("711",  "42");
		codeMap.put("715",  "43");		
	}

	private String transDBCode(String code) {

		String subCode;
		String transCode = "";
		while(code.length()>0)
		{
			if(code.substring(0, 1).matches("7"))
				subCode = code.substring(0, 3);
			else
				subCode = code.substring(0, 5);
			code = code.replaceFirst(subCode, "");
			transCode += codeMap.get(subCode);
		}
		return transCode;
	}
	
		
	private DatabaseHelper mOpenHelperWords;
	private DatabaseHelper mOpenHelperPhrases;
	
	public ZhuYinDictionaryProvider(Context ctx) {
		this.context = ctx;
		mOpenHelperWords = new DatabaseHelper(this.context, DBWORDS_NAME);
		mOpenHelperPhrases = new DatabaseHelper(this.context, DBPHRASES_NAME);

		try {
			mOpenHelperWords.createDataBase();
			mOpenHelperPhrases.createDataBase();
		} catch (IOException ioe) {
			// throw new Error("Unable to create database");
			Toast.makeText(ctx, R.string.db_create_error, Toast.LENGTH_SHORT).show();
		}
		
		ZhuYinDictionaryCodeMap();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		//Log.i(TAG, "CREATE TABLE");
		mOpenHelperWords = new DatabaseHelper(getContext(), DBWORDS_NAME);
		mOpenHelperPhrases = new DatabaseHelper(getContext(), DBPHRASES_NAME);
		return true;
	}

/*
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		//SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		//qb.setTables(NOTES_TABLE_NAME);
		//qb.setProjectionMap(sNotesProjectionMap);

		// If no sort order is specified use the default
		//String orderBy = sortOrder;

		// Get the database and run the query
		//SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		//Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		//c.setNotificationUri(getContext().getContentResolver(), uri);
		//return c;
		return null;
	}
*/
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ZhuYinDictionaryProvider open() {
		dbWords = mOpenHelperWords.getWritableDatabase();
		dbPhrases = mOpenHelperPhrases.getWritableDatabase();
		return this;
	}

	public void close() {
		mOpenHelperWords.close();
		mOpenHelperPhrases.close();
	}

	public void setSearchCode(String code) {
		mSearchCode = transDBCode(code);
	}
	
	public void useWords(String word) {
		String code = mSearchCode;
		if(word.length() > 1)
			dbPhrases.execSQL("update phrases_" + code.substring(0, 2) + " set use=use+1 where word='" + word + "'");
		else	
			dbWords.execSQL("update words_" + code.substring(0, 2) + " set use=use+1 where word='" + word + "'");
		//Log.i(TAG, "update words set use=use+1 where word='" + code + "'");
	}	

	public Cursor getPhrases(Integer limit) {
		String code = mSearchCode;
		Cursor mCursor = dbPhrases.query(true, "phrases_" + code.substring(0, 2), new String[] { "word" }, "code LIKE '" + code + "%' group by word", null, null, null, "use DESC, frequency DESC", limit.toString());
		//Log.i(TAG, "getWordsRough");
		return mCursor;
	}

	public Cursor getWordsExactly(Integer limit) {
		String code = mSearchCode;
		Cursor mCursor = dbWords.query(true, "words_" + code.substring(0, 2), new String[] { "word" }, "code='" + code + "' group by word", null, null, null, "use DESC, frequency DESC", limit.toString());
		//Log.i(TAG, "getWordsExactly");
		return mCursor;
	}

	public Cursor getWordsRough(Integer limit) {
		String code = mSearchCode;
		Cursor mCursor = dbWords.query(true, "words_" + code.substring(0, 2), new String[] { "word" }, "code like '" + code + "%' and code!='" + code + "' group by word", null, null, null, "use DESC, frequency DESC", limit.toString());
		//Log.i(TAG, "getWordsExactly");
		return mCursor;
	}

}
