package tv.tapin.android.duck;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class Application extends android.app.Application {
	
	 
    @Override
    public void onCreate(){
    	 
    	AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);
        UAirship.takeOff(this, options);
        PushManager.shared().setIntentReceiver(IntentReceiver.class);
        PushManager.enablePush();
        
        String apid = PushManager.shared().getAPID();
        Logger.info("onCreate - App APID: " + apid);
        
    }
}
