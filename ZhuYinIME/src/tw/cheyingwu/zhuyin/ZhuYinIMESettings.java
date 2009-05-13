package tw.cheyingwu.zhuyin;

import tw.cheyingwu.zhuyin.R;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ZhuYinIMESettings extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}