package tv.tapin.android.duck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.urbanairship.Logger;

import android.content.Context;
import android.content.SharedPreferences;

public class Utilities {
	public static final String PREFS_NAME = "SSCPrefs";
	public static String ANDROID_ID_PREF = "android_id";
	public static String APID_PREF = "apid";
	
	private static interface Callbackable<T> {
		void Function();
		void Function(T result);
    }
	
	//Usage:
	//
	//  Callback<String> callback = new Callback<String>() {
	//       public void Function(String string) {}				
	//  };
	public static abstract class Callback<T> implements Callbackable<T> {
		@Override
		public void Function() {}

		@Override
		public void Function(T result) {}
	}
	
	public static String get(String urlString, Map<String, String> data) {
    	String response = "";
    	String encodedData = "";
     	try {
     		for (Map.Entry<String, String> entry : data.entrySet()) {
     			if (entry.getValue() == null)
     				entry.setValue("");
     			encodedData += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
     		}
     		System.out.println("GET:" + encodedData);
    		//Send the request
    		URL url = new URL(urlString + "?" + encodedData);
    		URLConnection connection = url.openConnection();
    		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;");
    		// Get the response
    		BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    		String line;
    		while ((line = read.readLine()) != null) {
    			response += line;
    			}
    		   	read.close();

     	} catch (IOException ex) {
     		System.err.println(ex.getMessage());
     		System.err.println(ex.getStackTrace());
     		System.err.println("response: " + response);
     	}

     	System.out.println("response: " + response);
     	return response;
    }
	
	public static void getAsync(final String urlString, final Map<String, String> data, final Callback<String> callback) {
		new Thread(){
    		public void run(){
    			callback.Function(Utilities.get(urlString, data));
    		}
        }.start();
	}
	
	public static void getAsync(final String urlString, final Map<String, String> data) {
		  Callback<String> callback = new Callback<String>() {
		       public void Function() {}
		  };
		  getAsync(urlString, data, callback);
	}
	
	public static String post(String urlString, Map<String, String> data) {
    	String response = "";
    	String encodedData = "";
     	try {
     		for (Map.Entry<String, String> entry : data.entrySet()) {
     			if (entry.getValue() == null) {
     				entry.setValue("");
     			}
     			encodedData += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
     		}
     		System.out.println("POST:" + urlString + " + " + encodedData);
    		//Send the request
    		URL url = new URL(urlString);
    		URLConnection connection = url.openConnection();
    		connection.setDoOutput(true);
    		OutputStreamWriter write = new OutputStreamWriter(connection.getOutputStream());
    		write.write(encodedData);
    		write.flush();
    		// Get the response
    		BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    		String line;
    		while ((line = read.readLine()) != null) {
    			response += line;
    			}
    		   	write.close();
    		   	read.close();

     	} catch (IOException ex) {
     		System.err.println(ex.getMessage());
     		ex.printStackTrace();
     		System.err.println("response: " + response);
     	}

     	System.out.println("response: " + response);
     	return response;
    }
	
	public static void postAsync(final String urlString, final Map<String, String> data, final Callback<String> callback) {
		new Thread(){
    		public void run(){
    			callback.Function(Utilities.post(urlString, data));
    		}
        }.start();
	}
	
	public static void postAsync(final String urlString, final Map<String, String> data) {
		  Callback<String> callback = new Callback<String>() {
		       public void Function() {}
		  };
		  postAsync(urlString, data, callback);
	}
	
	private static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(PREFS_NAME, 0);
	}
	
	private static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
		return getSharedPreferences(context).edit();
	}
	
	public static String getSharedPreferenceString(Context context, String key) {
		return getSharedPreferences(context).getString(key, null);
	}
	
	public static void setSharedPreferencesString(Context context, String key, String value) {
		SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
		if ((key != null) && (value != null)) {
			editor.putString(key, value);
		}
		editor.apply();
	}
	
	public static void clearSharedPreferencesString(Context context, String key) {
		SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
		editor.remove(key);
		editor.apply();
	}
	
    public static void enroll(Context context) {
    	Map<String, String> data = new LinkedHashMap<String,String>();
        data.put("phone_id", Utilities.getSharedPreferenceString(context, Utilities.ANDROID_ID_PREF));
        data.put("push", Utilities.getSharedPreferenceString(context, Utilities.APID_PREF));
        Logger.info(data.toString());
		Utilities.postAsync(context.getString(R.string.endpoint_root) + "enroll", data);	
    }
}
