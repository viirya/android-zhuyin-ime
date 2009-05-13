package tw.cheyingwu.zhuyin;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

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

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
	}

	private DatabaseHelper mOpenHelper;

	public ZhuYinDictionaryProvider(Context ctx) {
		this.context = ctx;
		mOpenHelper = new DatabaseHelper(this.context);
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
