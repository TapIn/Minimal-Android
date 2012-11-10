package tv.tapin.android.duck;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Upload extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Uri videoUri = (Uri) getIntent().getExtras().get(Select.VIDEO_URI);
        
        startUpload(videoUri);
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_upload, menu);
        return true;
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
    	setResult(RESULT_OK);
        finish();
    }
    
    public void toSelectActivity(View view) {
    	finish();
    }
    
    private void startUpload(Uri videoUri) {
    	System.out.println(videoUri.getPath());
    	InputStream video = null;
		try {
			video = getContentResolver().openInputStream(videoUri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	final String videoName = (System.currentTimeMillis() / 1000L) + "-" + Utilities.getSharedPreferenceString(this, Utilities.ANDROID_ID_PREF) + ".mp4";
        
    	AWSCredentials myCredentials = new BasicAWSCredentials(getString(R.string.aws_access_key), getString(R.string.aws_secret_key));
    	final TransferManager transferManager = new TransferManager(myCredentials);
    	
    	ObjectMetadata metadata = new ObjectMetadata();
    	try {
			metadata.setContentLength(video.available());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	PutObjectRequest request = new PutObjectRequest(getString(R.string.aws_bucket_name), videoName, video, metadata).withCannedAcl(CannedAccessControlList.PublicRead);	
    	final com.amazonaws.services.s3.transfer.Upload upload = transferManager.upload(request);
    	new Thread(){
    		public void run(){
    	    	try {
					upload.waitForCompletion();
				} catch (AmazonServiceException e) {
					e.printStackTrace();
				} catch (AmazonClientException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    	    	runOnUiThread(new Runnable() {
    	    	     public void run() {
    	    	    	 	uploadComplete();
    	    	    	 }
    	    	});
    		}
    	}.start();
    	
    	
    	final Map<String, String> data = new LinkedHashMap<String,String>();
        data.put("phone_id", Utilities.getSharedPreferenceString(this, Utilities.ANDROID_ID_PREF));
        data.put("file", videoName);
    	
    	Utilities.postAsync(getString(R.string.endpoint_root) + "upload", data);
    }
    
    private void uploadComplete() {
    	Button backButton = (Button) findViewById(R.id.button_to_select_activity);
    	ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    	TextView uploading = (TextView) findViewById(R.id.uploading_textView);
    	TextView complete = (TextView) findViewById(R.id.complete_textView);
    	progressBar.setVisibility(View.GONE);
    	uploading.setVisibility(View.GONE);
    	backButton.setVisibility(View.VISIBLE);
    	complete.setVisibility(View.VISIBLE);
    }
}
