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

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;

public class Main extends Activity {
	public final static String VIDEO_URI = "tv.tapin.android.duck.VIDEO_URI";
	public final static int ACTION_TAKE_VIDEO = 1;
	public final static int ACTION_CHOOSE_VIDEO = 2;
	public static String android_id = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        
        android_id = "a" + Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
               
        new Thread(){
    		public void run(){
    			Map<String, String> data = new LinkedHashMap<String,String>();
    	        data.put("phone_id", android_id);
    			post(getString(R.string.endpoint_root) + "enroll", data);
    		}
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void recordVideo(View view) {
    	Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }
    
    public void chooseVideo(View view) {
    	Intent videoChooseIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
    	videoChooseIntent.setType("video/*");
    	startActivityForResult(videoChooseIntent, ACTION_CHOOSE_VIDEO);
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	if ((requestCode == ACTION_TAKE_VIDEO) && (resultCode == RESULT_OK))  {
    		handleVideoUri(data);
    	}
    	else if ((requestCode == ACTION_CHOOSE_VIDEO) && (resultCode == RESULT_OK))  {
    		handleVideoUri(data);
    	}
    }
    
    private void handleVideoUri(Intent intent) {
        Uri videoUri = intent.getData();
        Intent uploadIntent = new Intent(this, Upload.class);
        uploadIntent.putExtra(VIDEO_URI, videoUri);
        startActivity(uploadIntent);
    }
    
    public static String post(String urlString, Map<String, String> data) {
    	String rdata = "";
    	String encodedData = "";
     	try {
     		for (Map.Entry<String, String> entry : data.entrySet()) {
     			encodedData += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
     		}
     		System.out.println(encodedData);
    		//Send the request
    		URL url = new URL(urlString);
    		URLConnection conn = url.openConnection();
    		conn.setDoOutput(true);
    		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    		wr.write(encodedData);
    		wr.flush();
    		// Get the response
    		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		String line;
    		while ((line = rd.readLine()) != null) {
    			rdata += line;
    			}
    		   	wr.close();
    		   	rd.close();

     	} catch (IOException ex) {
     		System.err.println(ex.getMessage());
     		System.err.println(ex.getStackTrace());
     		System.err.println("response: " + rdata);
     	}

     	System.out.println("response: " + rdata);
     	return rdata;
    }
}
