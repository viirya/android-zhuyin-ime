package tw.cheyingwu.zhuyin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import android.app.AlertDialog;
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
import android.util.Log;
import android.widget.Toast;

public class ZhuYinDictionaryProvider extends ContentProvider {
	private static final String TAG = "ZhuYinIME";
	private static final String DATABASE_NAME = "ZhuYin.db";
	private static final int DATABASE_VERSION = 2;
	private static final String NOTES_TABLE_NAME = "zi";

	private static HashMap<String, String> sNotesProjectionMap;

	private Context context;
	private SQLiteDatabase db;

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		private static String DB_PATH = "/data/data/tw.cheyingwu.zhuyin/databases/";
		private final Context myContext;


		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.myContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			String sql = "CREATE TABLE zi (id INTEGER PRIMARY KEY  AUTOINCREMENT , zcode VARCHAR, zword VARCHAR);";
			Log.i(TAG, "CREATE TABLE:" + sql);
			// db.execSQL("CREATE TABLE " + NOTES_TABLE_NAME + " ("
			// + "id" + " INTEGER PRIMARY KEY,"
			// + "zcode" + " VARCHAR,"
			// + "zword" + " VARCHAR"
			// + ");");
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
		
		/**
	     * Creates a empty database on the system and rewrites it with your own database.
	     * */
	    public void createDataBase() throws IOException{
	 
	    	boolean dbExist = checkDataBase();
	 
	    	if (dbExist) {
	    		//do nothing - database already exist
	    	} else {
	 
	    		//By calling this method and empty database will be created into the default system path
	            //of your application so we are gonna be able to overwrite that database with our database.	    		
	        	this.getReadableDatabase();
	 
	        	try {
	    			copyDataBase();
	
	    		} catch (IOException e) {
	 
	        		throw new Error("Error copying database");
	 
	        	}
	    	}
	 
	    }

		
		/**
	     * Check if the database already exist to avoid re-copying the file each time you open the application.
	     * @return true if it exists, false if it doesn't
	     */
	    private boolean checkDataBase(){
	 
	    	SQLiteDatabase checkDB = null;
	 
	    	try {
	    		String myPath = DB_PATH + DATABASE_NAME;
	    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	 
	    	} catch (SQLiteException e){	 
	    		Log.i(TAG, "database does't exist yet");
	    	}
	 
	    	if (checkDB != null) {	 
	    		checkDB.close();	 
	    	}
	 
	    	return checkDB != null ? true : false;
	    }
	    
	    /**
	     * Copies your database from your local assets-folder to the just created empty database in the
	     * system folder, from where it can be accessed and handled.
	     * This is done by transfering bytestream.
	     * */
	    private void copyDataBase() throws IOException {
	    	
	    	Log.i(TAG, "start to copy database");
	 
	    	//Open your local db as the input stream
	    	InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DATABASE_NAME;
	 
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	//transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	    	
	    	Log.i(TAG, "copy database done");
	 
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 
	    }
	
	}

	private DatabaseHelper mOpenHelper;

	public ZhuYinDictionaryProvider(Context ctx) {
		this.context = ctx;
		mOpenHelper = new DatabaseHelper(this.context);
							
		try {			
			mOpenHelper.createDataBase();
		} catch (IOException ioe) { 
			//throw new Error("Unable to create database");
			Toast.makeText(ctx, R.string.db_create_error, Toast.LENGTH_SHORT).show();			
		}

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
		Log.i(TAG, "CREATE TABLE");
		
		mOpenHelper = new DatabaseHelper(getContext());
		
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(NOTES_TABLE_NAME);
		qb.setProjectionMap(sNotesProjectionMap);

		// If no sort order is specified use the default
		String orderBy = sortOrder;

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ZhuYinDictionaryProvider open() {
		db = mOpenHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mOpenHelper.close();
	}

	public Cursor getWords(String zcode) {
		Cursor mCursor = db.query(true, "zi", new String[] { "zcode",
				"zword" }, "zcode" + " LIKE '" + zcode + "%'", null, null, null, null, null);
		Log.i(TAG, "getWords");
		return mCursor;
	}
}
