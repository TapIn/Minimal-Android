package tv.tapin.android.duck;

import java.util.LinkedHashMap;
import java.util.Map;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class Launch extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.setSharedPreferencesString(this, Utilities.ANDROID_ID_PREF, Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID));
        enroll();
        launchNextActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_launch, menu);
        return true;
    }
    
    public void enroll() {
    	Map<String, String> data = new LinkedHashMap<String,String>();
        data.put("phone_id", Utilities.getSharedPreferenceString(this, Utilities.ANDROID_ID_PREF));
		Utilities.postAsync(getString(R.string.endpoint_root) + "enroll", data);
    }
    
    public void launchNextActivity() {
    	Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
