package tv.tapin.android.duck;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.util.Log;
 
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;
 
public class IntentReceiver extends BroadcastReceiver {
 
        private static final String logTag = "tv.tapin.android.duck";
        private static final String EXTRA_VIDEO = "video";
        private static final String EXTRA_UPDATE = "update";
        private static final String EXTRA_START_APP = "app";
        private Context context;
 
        @Override
        public void onReceive(Context context, Intent intent) {
                Log.i(logTag, "Received intent: " + intent.toString());
                this.context = context;
                String action = intent.getAction();
 
                if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {
 
                        int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);
 
                        Log.i(logTag, "Received push notification. Alert: "
                                        + intent.getStringExtra(PushManager.EXTRA_ALERT)
                                        + " [NotificationID="+id+"]");
 
                        logPushExtras(intent);
 
                } else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {
 
                        Log.i(logTag, "User clicked notification. Message: " + intent.getStringExtra(PushManager.EXTRA_ALERT));
                        
                        //Log.i(logTag, intent.getExtras().getString("extra"));
                        
                        if (intent.getExtras().containsKey(EXTRA_VIDEO)) {
                        	launchVideo(intent);                        	
                        }
                        else if (intent.getExtras().containsKey(EXTRA_UPDATE)) {
                        	launchUpdate(intent);                        	
                        }
                        else if (intent.getExtras().containsKey(EXTRA_START_APP)) {
                        	launchApp(intent);                        	
                        }
                        
                        logPushExtras(intent);
 

                } else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
                        Log.i(logTag, "Registration complete. APID:" + intent.getStringExtra(PushManager.EXTRA_APID)
                                        + ". Valid: " + intent.getBooleanExtra(PushManager.EXTRA_REGISTRATION_VALID, false));
                        logPushExtras(intent);
                        
                        Utilities.setSharedPreferencesString(context, Utilities.APID_PREF, PushManager.shared().getAPID());
                        Utilities.enroll(context);
                }
                    
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
                        /*List<String> ignoredKeys = (List<String>)Arrays.asList(
                                        "collapse_key",//GCM collapse key
                                        "from",//GCM sender
                                        PushManager.EXTRA_NOTIFICATION_ID,//int id of generated notification (ACTION_PUSH_RECEIVED only)
                                        PushManager.EXTRA_PUSH_ID,//internal UA push id
                                        PushManager.EXTRA_ALERT);//ignore alert
                        if (ignoredKeys.contains(key)) {
                                continue;
                        }*/
                        Log.i(logTag, "Push Notification Extra: ["+key+" : " + intent.getStringExtra(key) + "]");
                }
        }
        
        private void launchVideo(Intent intent) {
        	String videoID = intent.getStringExtra(EXTRA_VIDEO); 	
        	Uri uri = Uri.parse("http://ssc.studentrnd.org/watch/" + videoID + "?phone_id=" + Utilities.getSharedPreferenceString(context, Utilities.ANDROID_ID_PREF));
        	Intent launch = new Intent(Intent.ACTION_VIEW, uri);
        	launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        	UAirship.shared().getApplicationContext().startActivity(launch);
        }
        
        private void launchUpdate(Intent intent) {	
        	Uri uri = Uri.parse(context.getString(R.string.update_url));
        	Intent launch = new Intent(Intent.ACTION_VIEW, uri);
        	launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        	UAirship.shared().getApplicationContext().startActivity(launch);
        }
        
        private void launchApp(Intent intent) {
            Intent launch = new Intent(Intent.ACTION_MAIN);
            launch.setClass(UAirship.shared().getApplicationContext(), Launch.class);
            launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            UAirship.shared().getApplicationContext().startActivity(launch);
        }
        
        
        
        private void handleRegistration(Intent intent) {
            String registrationId = intent.getStringExtra("registration_id");
            String error = intent.getStringExtra("error");
            String unregistered = intent.getStringExtra("unregistered");
            
            /*
            String registrationId = PushManager.EXTRA_GCM_REGISTRATION_ID;
            String error = PushManager.EXTRA_REGISTRATION_ERROR;
            String unregistered = PushManager.EXTRA_REGISTRATION_VALID;
            */
            
            // registration succeeded
            if (registrationId != null) {
            	//TODO
                // store registration ID on shared preferences
                // notify 3rd-party server about the registered ID
            }
                
            // unregistration succeeded
            if (unregistered != null) {
            	//TODO
                // get old registration ID from shared preferences
                // notify 3rd-party server about the unregistered ID
            } 
                
            // last operation (registration or unregistration) returned an error;
            if (error != null) {
                if ("SERVICE_NOT_AVAILABLE".equals(error)) {
                   // optionally retry using exponential back-off
                   // (see Advanced Topics)
                } else {
                    // Unrecoverable error, log it
                    Log.i(logTag, "Received error: " + error);
                }
            }
        }
}