package tw.cheyingwu.zhuyin;

import java.util.List;

import tw.cheyingwu.zhuyin.R;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 * Setting activity of Pinyin IME.
 */
public class ZhuYinIMESettingsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static String TAG = "SettingsActivity";

    private CheckBoxPreference mKeySoundPref;
    private CheckBoxPreference mVibratePref;
    private CheckBoxPreference mDefaultIMPref;
    private ListPreference mCandidateCntPref;
    //private CheckBoxPreference mPredictionPref;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        PreferenceScreen prefSet = getPreferenceScreen();

        mKeySoundPref = (CheckBoxPreference) prefSet
                .findPreference(getString(R.string.sound_on));
        mVibratePref = (CheckBoxPreference) prefSet
                .findPreference(getString(R.string.vibrate_on));
        mDefaultIMPref = (CheckBoxPreference) prefSet
        .findPreference(getString(R.string.default_im));        
        mCandidateCntPref = (ListPreference) prefSet
        .findPreference(getString(R.string.candidate_cnt));
//        mPredictionPref = (CheckBoxPreference) prefSet
//                .findPreference(getString(R.string.setting_prediction_key));
        
        prefSet.setOnPreferenceChangeListener(this);
        
        ZhuYinIMESettings.getInstance(PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()));

//        updatePreference(prefSet, getString(R.string.setting_advanced_key));
        
        updateWidgets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWidgets();
    }

    @Override
    protected void onDestroy() {
        ZhuYinIMESettings.releaseInstance();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ZhuYinIMESettings.setKeySound(mKeySoundPref.isChecked());
        ZhuYinIMESettings.setVibrate(mVibratePref.isChecked());
        ZhuYinIMESettings.setDefaultIM(mDefaultIMPref.isChecked());
        ZhuYinIMESettings.setCandidateCnt(Integer.parseInt(mCandidateCntPref.getEntry().toString()));
//        Settings.setPrediction(mPredictionPref.isChecked());

        ZhuYinIMESettings.writeBack();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    private void updateWidgets() {
        mKeySoundPref.setChecked(ZhuYinIMESettings.getKeySound());
        mVibratePref.setChecked(ZhuYinIMESettings.getVibrate());
        mDefaultIMPref.setChecked(ZhuYinIMESettings.getDefaultIM());
        mCandidateCntPref.setDefaultValue(String.valueOf(ZhuYinIMESettings.getCandidateCnt()));
//        mPredictionPref.setChecked(Settings.getPrediction());
    }

/*
    public void updatePreference(PreferenceGroup parentPref, String prefKey) {
        Preference preference = parentPref.findPreference(prefKey);
        if (preference == null) {
            return;
        }
        Intent intent = preference.getIntent();
        if (intent != null) {
            PackageManager pm = getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
            int listSize = list.size();
            if (listSize == 0)
                parentPref.removePreference(preference);
        }
    }
*/
}
