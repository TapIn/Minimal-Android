package tv.tapin.android.duck;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class Application extends android.app.Application {
	public static MixpanelAPI mMixpanel;
	 
    @Override
    public void onCreate(){
    	 
    	AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);
        UAirship.takeOff(this, options);
        PushManager.shared().setIntentReceiver(IntentReceiver.class);
        PushManager.enablePush();
        
        String apid = PushManager.shared().getAPID();
        Logger.info("onCreate - App APID: " + apid);
        
        mMixpanel = MixpanelAPI.getInstance(this, getString(R.string.mixpanel_token));
        Application.mMixpanel.track("App Open", null);
        mMixpanel.flush();
    }
}
