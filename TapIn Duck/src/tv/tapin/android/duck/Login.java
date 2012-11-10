package tv.tapin.android.duck;

import java.util.LinkedHashMap;
import java.util.Map;

import tv.tapin.android.duck.Utilities.Callback;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class Login extends Activity {
	public static final String USERNAME_PREF = "username";
	public static final String PASSWORD_PREF = "password";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLoggedIn())
        	tryLogin();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_login);
    }
    
    @Override
    public void onDestroy() {
    	Application.mMixpanel.flush();
    	super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
    public void loginClick(View view) {
    	EditText editTextUsername = (EditText) findViewById(R.id.edit_username);
        String username = editTextUsername.getText().toString();
    	EditText editTextPassword = (EditText) findViewById(R.id.edit_username);
        String password = editTextPassword.getText().toString();
    	
    	setLoginData(username, password);
    	
    	Application.mMixpanel.track("Login", null);
    	
    	tryLogin();
    }
    
    public void tryLogin() {
    	final ProgressDialog progressDiolog = ProgressDialog.show(this, "", "Logging in", true, false);
    	    	
        Map<String, String> data = new LinkedHashMap<String,String>();
        data.put("phone_id", Utilities.getSharedPreferenceString(this, Utilities.ANDROID_ID_PREF));
        data.put("username", Utilities.getSharedPreferenceString(this, USERNAME_PREF));
        
    	Callback<String> callback = new Callback<String>() {
    		public void Function(String result) {
    			if (!result.equals("1")) {
    				clearLoginData();
    				final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
    				builder.setTitle("Login info incorrect");
    				builder.setCancelable(true);
    				builder.setNeutralButton("Okay",
    						new DialogInterface.OnClickListener() {
    								public void onClick(DialogInterface dialog, int which) {}
    							});
    				Login.this.runOnUiThread(new Runnable() {
	    	    	     public void run() {
	    	    	    	progressDiolog.dismiss();
	    	    	    	AlertDialog dialog = builder.create();
    						dialog.show();
	    	    	     }});
    			} else {
    				progressDiolog.dismiss();
    				launchActivitySelect();
    			}
    		}				
    	};
        
		Utilities.postAsync(getString(R.string.endpoint_root) + "associate", data, callback);
    }
    
    public void setLoginData(String username, String password) {
    	Utilities.setSharedPreferencesString(this, USERNAME_PREF, username);
    	//Utilities.setSharedPreferencesString(this, PASSWORD_PREF, password);
		
	}
    
    public static void logout(Context context) {
    	Utilities.clearSharedPreferencesString(context, USERNAME_PREF);
    	//Utilities.clearSharedPreferencesString(context, PASSWORD_PREF);
    	
    	Application.mMixpanel.track("Logout", null);
    }
    
    public void clearLoginData() {
    	logout(Login.this);
    }

	public boolean isLoggedIn() {
    	if (Utilities.getSharedPreferenceString(this, USERNAME_PREF) != null)
    			/* && (Utilities.getSharedPreferenceString(this, PASSWORD_PREF) != null)) */ {
    		return true;
    	}
    	else return false;
    }
    
    public void launchActivitySelect() {
    	Intent intent = new Intent(this, Select.class);
        startActivity(intent);
        finish();
    }
}