
package tw.cheyingwu.zhuyin;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Class used to maintain settings.
 */
public class ZhuYinIMESettings {
    private static final String ANDPY_CONFS_KEYSOUND_KEY = "Sound";
    private static final String ANDPY_CONFS_VIBRATE_KEY = "Vibrate";
    private static final String ANDPY_CONFS_DEFAULTIM_KEY = "DefaultIM";
    private static final String ANDPY_CONFS_CANDIDATECNT_KEY = "CandidateCnt";
    //private static final String ANDPY_CONFS_PREDICTION_KEY = "Prediction";
    
    private static boolean mKeySound;
    private static boolean mVibrate;
    private static boolean mDefaultIM;
    private static int mCandidateCnt;
    //private static boolean mPrediction;
    
    
    private static ZhuYinIMESettings mInstance = null;

    private static int mRefCount = 0;

    private static SharedPreferences mSharedPref = null;

    protected ZhuYinIMESettings(SharedPreferences pref) {
        mSharedPref = pref;
        initConfs();
    }

    public static ZhuYinIMESettings getInstance(SharedPreferences pref) {
        if (mInstance == null) {
            mInstance = new ZhuYinIMESettings(pref);
        }
        assert (pref == mSharedPref);
        mRefCount++;
        return mInstance;
    }

    public static void writeBack() {
        Editor editor = mSharedPref.edit();
        editor.putBoolean(ANDPY_CONFS_VIBRATE_KEY, mVibrate);
        editor.putBoolean(ANDPY_CONFS_KEYSOUND_KEY, mKeySound);
        editor.putBoolean(ANDPY_CONFS_DEFAULTIM_KEY, mDefaultIM);
        editor.putInt(ANDPY_CONFS_CANDIDATECNT_KEY, mCandidateCnt);
        //editor.putBoolean(ANDPY_CONFS_PREDICTION_KEY, mPrediction);
        editor.commit();
    }

    public static void releaseInstance() {
        mRefCount--;
        if (mRefCount == 0) {
            mInstance = null;
        }
    }

    private void initConfs() {
        mKeySound = mSharedPref.getBoolean(ANDPY_CONFS_KEYSOUND_KEY, false);
        mVibrate = mSharedPref.getBoolean(ANDPY_CONFS_VIBRATE_KEY, false);
        mDefaultIM = mSharedPref.getBoolean(ANDPY_CONFS_DEFAULTIM_KEY, true);
        mCandidateCnt = mSharedPref.getInt(ANDPY_CONFS_CANDIDATECNT_KEY, 50);
//        mPrediction = mSharedPref.getBoolean(ANDPY_CONFS_PREDICTION_KEY, true);
    }

    public static boolean getKeySound() {
        return mKeySound;
    }

    public static void setKeySound(boolean v) {
        if (mKeySound == v) return;
        mKeySound = v;
    }

    public static boolean getVibrate() {
        return mVibrate;
    }

    public static void setVibrate(boolean v) {
        if (mVibrate == v) return;
        mVibrate = v;
    }
    
    public static boolean getDefaultIM() {
        return mDefaultIM;
    }

    public static void setDefaultIM(boolean v) {
        if (mDefaultIM == v) return;
        mDefaultIM = v;
    }
    
    public static int getCandidateCnt() {
        return mCandidateCnt;
    }

    public static void setCandidateCnt(int v) {
        if (mCandidateCnt == v) return;
        mCandidateCnt = v;
    }
    
/*
    public static boolean getPrediction() {
        return mPrediction;
    }

    public static void setPrediction(boolean v) {
        if (mPrediction == v) return;
        mPrediction = v;
    }
*/
}
