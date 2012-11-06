package tv.tapin.android.duck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Menu;
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

        Uri videoUri = (Uri) getIntent().getExtras().get(Main.VIDEO_URI);
        
        startUpload(videoUri);
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_upload, menu);
        return true;
    }
    
    public void toMainActivity(View view) {
    	finish();
    }
    
    private void startUpload(Uri videoUri) {
    	System.out.println(videoUri.getPath());
    	InputStream video = null;
		try {
			video = getContentResolver().openInputStream(videoUri);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	final String videoName = (System.currentTimeMillis() / 1000L) + "-" + Main.android_id + ".mp4";
        
    	AWSCredentials myCredentials = new BasicAWSCredentials(getString(R.string.aws_access_key), getString(R.string.aws_secret_key));
    	final TransferManager tm = new TransferManager(myCredentials);
    	
    	ObjectMetadata metadata = new ObjectMetadata();
    	try {
			metadata.setContentLength(video.available());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	final PutObjectRequest request = new PutObjectRequest(getString(R.string.aws_bucket_name), videoName, video, metadata).withCannedAcl(CannedAccessControlList.PublicRead);	
    	new Thread(){
    		public void run(){
    	    	tm.upload(request);	
    	    	runOnUiThread(new Runnable() {
    	    	     public void run() {
    	    	    	 new Thread(){
    	    	     		public void run() {
    	    	     			try {
    	    	     				Thread.sleep(1000);
    	    	     			} catch (InterruptedException e) {
    	    	     				// TODO Auto-generated catch block
    	    	     				e.printStackTrace();
    	    	     			}
    	    	     			runOnUiThread(new Runnable() {
    	    	    	    	     public void run() {
    	    	    	    	    	 uploadComplete();
    	    	    	    	     }
    	    	     			});
    	    	     		}
    	    	    	 }.start();
    	    	    }
    	    	});
    		}
    	}.start();
    	
    	new Thread(){
    		public void run(){
    			Map<String, String> data = new LinkedHashMap<String,String>();
    	        data.put("phone_id", Main.android_id);
    	        data.put("file", videoName);
    			Main.post(getString(R.string.endpoint_root) + "upload", data);
    		}
        }.start();
    }
    
    private void uploadComplete() {
    	Button backButton = (Button) findViewById(R.id.button_to_main_activity);
    	ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    	TextView uploading = (TextView) findViewById(R.id.uploading_textView);
    	TextView complete = (TextView) findViewById(R.id.complete_textView);
    	progressBar.setVisibility(View.GONE);
    	uploading.setVisibility(View.GONE);
    	backButton.setVisibility(View.VISIBLE);
    	complete.setVisibility(View.VISIBLE);
    }
}
