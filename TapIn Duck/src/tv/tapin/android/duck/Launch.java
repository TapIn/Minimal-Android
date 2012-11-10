package tv.tapin.android.duck;

import com.urbanairship.push.PushManager;

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
        Utilities.setSharedPreferencesString(this, Utilities.APID_PREF, PushManager.shared().getAPID());

        Utilities.enroll(this);
        launchNextActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_launch, menu);
        return true;
    }
     
    public void launchNextActivity() {
    	Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}