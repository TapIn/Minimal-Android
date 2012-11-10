package tv.tapin.android.duck;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Select extends Activity {
	public final static String VIDEO_URI = "tv.tapin.android.duck.VIDEO_URI";
	public final static int ACTION_TAKE_VIDEO = 1;
	public final static int ACTION_CHOOSE_VIDEO = 2;
	public final static int ACTION_LOGOUT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_select);        
    }
    
    @Override
    public void onDestroy() {
    	Application.mMixpanel.flush();
    	super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_select, menu);
        return true;
    } 
    
    public void recordVideo(View view) {
    	Application.mMixpanel.track("Record", null);
    	Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }
    
    public void chooseVideo(View view) {
    	Application.mMixpanel.track("Upload", null);
    	Intent videoChooseIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
    	videoChooseIntent.setType("video/*");
    	startActivityForResult(videoChooseIntent, ACTION_CHOOSE_VIDEO);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void logout() {
    	Login.logout(this);
    	Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	if ((requestCode == ACTION_TAKE_VIDEO) && (resultCode == RESULT_OK))  {
    		handleVideoUri(data);
    	}
    	else if ((requestCode == ACTION_CHOOSE_VIDEO) && (resultCode == RESULT_OK))  {
    		handleVideoUri(data);
    	}
    	else if ((requestCode == ACTION_LOGOUT) && (resultCode == RESULT_OK))  {
    		logout();
    	}
    }
    
    private void handleVideoUri(Intent intent) {
        Uri videoUri = intent.getData();
        Intent uploadIntent = new Intent(this, Upload.class);
        uploadIntent.putExtra(VIDEO_URI, videoUri);
        startActivityForResult(uploadIntent, ACTION_LOGOUT);
    }
}
