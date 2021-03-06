package tv.tapin.android.duck;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;
 
public class IntentReceiver extends BroadcastReceiver {
 
        private static final String logTag = "tv.tapin.android.duck";
        private static final String EXTRA_VIDEO = "video";
        private static final String EXTRA_UPDATE = "update";
        private static final String EXTRA_START_APP = "app";
        private static final String EXTRA_URI = "uri";
        private Context context;
 
        @Override
        public void onReceive(Context context, Intent intent) {
                Log.i(logTag, "Received intent: " + intent.toString());
                this.context = context;
                String action = intent.getAction();
                
                JSONObject properties = new JSONObject();               
 
                if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {
 
                        int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);
                        Application.mMixpanel.track("Notification Received", null);
 
                        Log.i(logTag, "Received push notification. Alert: "
                                        + intent.getStringExtra(PushManager.EXTRA_ALERT)
                                        + " [NotificationID="+id+"]");
 
                        logPushExtras(intent);
 
                } else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {
 
                        Log.i(logTag, "User clicked notification. Message: " + intent.getStringExtra(PushManager.EXTRA_ALERT));
                        
                        if (intent.getExtras().containsKey(EXTRA_VIDEO)) {
                        	try {
								properties.put("type", EXTRA_VIDEO);
							} catch (JSONException e) { }
                        	launchVideo(intent);                        	
                        }
                        else if (intent.getExtras().containsKey(EXTRA_UPDATE)) {
                        	try {
								properties.put("type", EXTRA_UPDATE);
							} catch (JSONException e) { }
                        	launchUpdate(intent);                        	
                        }
                        else if (intent.getExtras().containsKey(EXTRA_START_APP)) {
                        	try {
								properties.put("type", EXTRA_START_APP);
							} catch (JSONException e) { }
                        	launchApp(intent);                        	
                        }
                        else if (intent.getExtras().containsKey(EXTRA_URI)) {
                        	try {
								properties.put("type", EXTRA_URI);
							} catch (JSONException e) { }
                        	launchUri(intent);                        	
                        }                        
                        Application.mMixpanel.track("Notification Opened", properties);
                        logPushExtras(intent);
 

                } else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
                        Log.i(logTag, "Registration complete. APID:" + intent.getStringExtra(PushManager.EXTRA_APID)
                                        + ". Valid: " + intent.getBooleanExtra(PushManager.EXTRA_REGISTRATION_VALID, false));
                        logPushExtras(intent);
                        
                        Application.mMixpanel.track("Notification Registration Finished", null);
                        
                        Utilities.setSharedPreferencesString(context, Utilities.APID_PREF, PushManager.shared().getAPID());
                        Utilities.enroll(context);
                }
                
                
                
                Application.mMixpanel.flush();
                    
        }
 
        /**
         * Log the values sent in the payload's "extra" dictionary.
         *
         * @param intent A PushManager.ACTION_NOTIFICATION_OPENED or ACTION_PUSH_RECEIVED intent.
         */
        private void logPushExtras(Intent intent) {
                Set<String> keys = intent.getExtras().keySet();
                for (String key : keys) {
 
                        //ignore standard extra keys (GCM + UA)
                        List<String> ignoredKeys = (List<String>)Arrays.asList(
                                        "collapse_key",//GCM collapse key
                                        "from",//GCM sender
                                        PushManager.EXTRA_REGISTRATION_VALID);
                                        //PushManager.EXTRA_NOTIFICATION_ID,//int id of generated notification (ACTION_PUSH_RECEIVED only)
                                        //PushManager.EXTRA_PUSH_ID,//internal UA push id
                                        //PushManager.EXTRA_ALERT);//ignore alert
                        if (ignoredKeys.contains(key)) {
                                continue;
                        }
                        Log.i(logTag, "Push Notification Extra: ["+key+" : " + intent.getStringExtra(key) + "]");
                }
        }
        
        private void launchVideo(Intent intent) {
        	String videoID = intent.getStringExtra(EXTRA_VIDEO); 	
        	Uri uri = Uri.parse("http://ssc.studentrnd.org/watch/" + videoID + "?phone_id=" + Utilities.getSharedPreferenceString(context, Utilities.ANDROID_ID_PREF));
        	launchViewIntent(intent, uri);
        }
        
        private void launchUpdate(Intent intent) {	
        	Uri uri = Uri.parse(context.getString(R.string.update_url));
        	launchViewIntent(intent, uri);
        }
        
        private void launchApp(Intent intent) {
            Intent launch = new Intent(Intent.ACTION_MAIN);
            launch.setClass(UAirship.shared().getApplicationContext(), Launch.class);
            launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            UAirship.shared().getApplicationContext().startActivity(launch);
        }
        
        private void launchUri(Intent intent) {
        	Uri uri = Uri.parse(intent.getStringExtra(EXTRA_URI));
        	launchViewIntent(intent, uri);

        }
        
        private void launchViewIntent(Intent intent, Uri uri) {
        	Intent launch = new Intent(Intent.ACTION_VIEW, uri);
        	launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        	UAirship.shared().getApplicationContext().startActivity(launch);
        }
}