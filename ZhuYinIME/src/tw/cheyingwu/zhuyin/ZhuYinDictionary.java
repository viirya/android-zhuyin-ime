package tw.cheyingwu.zhuyin;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.UserDictionary.Words;
import android.util.Log;

import com.android.inputmethod.latin.Dictionary;
import com.android.inputmethod.latin.WordComposer;

public class ZhuYinDictionary extends Dictionary {
	private static final String TAG = "ZhuYinIME";
	
    private static final String[] PROJECTION = {
        Words._ID,
        Words.WORD,
        Words.FREQUENCY
    };
	
	private Context mContext;

	public ZhuYinDictionary(Context context) {
		mContext = context;
	}

	@Override
	public void getWords(WordComposer composer, WordCallback callback) {
		Log.i(TAG, "getWords:getTypedWord:"+composer.getTypedWord().toString().length());
		Log.i(TAG, "getWords:getCodesAt:"+composer.getTypedWord().length());
		Log.i(TAG, "getWords:composer's length:"+composer.getCodesAt(0).length);
		
		String code="";
		for(int i=0;i<composer.getTypedWord().length();i++){
			code += String.valueOf(composer.getCodesAt(i)[0]);
		}
		Log.i(TAG, "getWords:code:"+code);
		String[] result=this.loadWordDB(code);

		for(String s: result){
			char[] word = s.toCharArray();			
			callback.addWord(word, 0, word.length, 10);
		}
		
		
	}

	@Override
	public boolean isValidWord(CharSequence word) {
		// TODO Auto-generated method stub
		return false;
	}
	public String[] loadWordDB(String code){
		char[] word=null;
		ZhuYinDictionaryProvider zdb = new ZhuYinDictionaryProvider(mContext);
		zdb.open();
		
		Cursor cursor = zdb.getWords(code);
		ArrayList<String> result = new ArrayList<String>();
		if(cursor.moveToFirst()){
			while(!cursor.isAfterLast()){
				String aword = cursor.getString(1);
				result.add(aword);
				cursor.moveToNext();
			}
		}

		zdb.close();
		return result.toArray(new String[0]);
	}
}
