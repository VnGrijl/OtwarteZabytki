package pl.otwartezabytki.android;

import java.io.File;

import pl.otwartezabytki.android.dataobjects.DataRelicDetails;
import pl.otwartezabytki.android.tools.MediaUriHelper;
import pl.otwartezabytki.android.tools.ResolutionDependent;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public class RelicEditActivity extends SherlockActivity {

	public final static String EDITORADD = "pl.otwartezabytki.android.EDITORADD";
	public final static String EXTRA_EDITRELIC = "pl.otwartezabytki.android.EDITRELIC";
	public final static String EXTRA_LONGLAT = "pl.otwartezabytki.android.LONGLAT";
	private static int CAMERA_CODE = 50;
	private static int GALLERY_CODE = 40;
	private static int MAP_CODE = 30;
	
	private Uri mediaFileUri;
	private boolean isEdited;
	
	ImageView relic_edit_img;
	ImageView relic_edit_map;
	EditText relic_edit_latitude;
	EditText relic_edit_longitude;
	Spinner voivodshipSpinner;
	
	// Details
	DataRelicDetails relicDetailsData;
	private long id;
	private int relic_id;
	private int android_id;
	private String voivodship;
	private String mediaFilePath="";
	private String mainPhotoURL="";
	private String mainPhotoPath="";
	private String state="";
	private String opinion="";
	
	BitmapFactory.Options bitmapOptions;
	int imageSize;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_relic_edit);
		createCustomActionBar();
		isEdited = getIntent().getBooleanExtra(EDITORADD, false);
		relic_edit_img = (ImageView) findViewById(R.id.relic_edit_img);
        relic_edit_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openContextMenu(v);
				
			}
		});
        registerForContextMenu(relic_edit_img);
        voivodshipSpinner = (Spinner) findViewById(R.id.relic_edit_voivodship_spinner);        
        voivodshipSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long arg3) {
				if(pos > 0){
					voivodship = parent.getItemAtPosition(pos).toString().split("\\s+")[1];
				}
				else{
					voivodship = "";
					((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.hint));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
		});
        
        
        relic_edit_map = (ImageView) findViewById(R.id.relic_edit_map);
        relic_edit_map.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startMapGetActivity();
				
			}
		});
        relic_edit_latitude = (EditText) findViewById(R.id.relic_edit_latitude_id);
        relic_edit_longitude = (EditText) findViewById(R.id.relic_edit_longitude_id);
        
        imageSize = ResolutionDependent.getDetailImageDimensions(getResources().getDisplayMetrics().densityDpi);
    	bitmapOptions = new BitmapFactory.Options();
    	bitmapOptions.inSampleSize=8;
		
		if(isEdited){
			relicDetailsData = getIntent().getParcelableExtra(EXTRA_EDITRELIC);
			relic_id = relicDetailsData.getRelic_id();
			android_id = relicDetailsData.getAndroid_id();
			id = relicDetailsData.getId();
			mainPhotoURL = relicDetailsData.getMainPhotoURL();
			mainPhotoPath = relicDetailsData.getMainPhotoPath();
			setRelicParameters();
		}else{ // add
			id = 0;
			relic_id = 0;
			android_id = 0;
		}
		
						
        
	}
	
	private void createCustomActionBar(){
		final LayoutInflater inflater = (LayoutInflater) getSupportActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_done_cancel, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	if(getRelicParameters()){
                    		Intent result = new Intent();
                    		result.putExtra(RelicEditActivity.EXTRA_EDITRELIC, relicDetailsData);
                    		setResult(RESULT_OK, result);
                            finish();
                    	}else{
                    		((EditText)findViewById(R.id.relic_edit_identification_id)).setHintTextColor(getResources().getColor(R.color.mill));
                    		Toast.makeText(getBaseContext(), R.string.relic_edit_validate, Toast.LENGTH_SHORT).show();
                    	}                    		                    	
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	setResult(RESULT_CANCELED);
                        finish();
                    }
                });
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	    menu.add(Menu.NONE,0,0,R.string.action_camera);
	    menu.add(Menu.NONE,1,1,R.string.action_gallery);
	    if(!mediaFilePath.equals("")){
	    	menu.add(Menu.NONE,2,2,R.string.action_remove_photo);
	    }
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
        case 0:
        	startCameraActivity();
            return true;
        case 1:
        	startGalleryActivity();
            return true;
        case 2:
        	setMainPhotoBlank();
            return true;    
        default:
            return true;
		}

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_CODE) {
	        if (resultCode == Activity.RESULT_OK) {
	        	Log.d("Otwarte Zabytki", "Camera Result");
	        	SingleScan singleScan = new SingleScan(mediaFilePath, "image/jpeg");
	        	MediaScannerConnection connection = new MediaScannerConnection(RelicEditActivity.this, singleScan);
	        	singleScan.connection = connection;
	        	connection.connect(); 
	        	Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mediaFilePath,bitmapOptions), imageSize, imageSize);
	        	relic_edit_img.setImageBitmap(ThumbImage);
	        	return;
	        }
    	}
    	
    	if (requestCode == GALLERY_CODE){
	    	if (resultCode == Activity.RESULT_OK) {
	    		Log.d("Otwarte Zabytki", "Gallery Result");
	    		Uri uri = data.getData();
	    		mediaFilePath = MediaUriHelper.getPathFromUri(getBaseContext(),uri);
	    		Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mediaFilePath,bitmapOptions), imageSize, imageSize);
	    		relic_edit_img.setImageBitmap(ThumbImage);
	        	return;
	    	}
	    }
    	
    	if (requestCode == MAP_CODE){
	    	if (resultCode == Activity.RESULT_OK) {
	    		Log.d("Otwarte Zabytki", "Map Result");
	    		double[] latlng = data.getDoubleArrayExtra(MapGetActivity.LATLNG);
	    		relic_edit_latitude.setText(Double.toString(latlng[0]));
	    		relic_edit_longitude.setText(Double.toString(latlng[1]));
	        	return;
	    	}
	    }
    	   	
	}
	
	// Start Activities
	
	public void startMapGetActivity(){
		Intent intent = new Intent(RelicEditActivity.this, MapGetActivity.class);
		String[] coordinates = new String[2];
		coordinates[0] = relic_edit_longitude.getText().toString();
		coordinates[1] = relic_edit_latitude.getText().toString();
		intent.putExtra(EXTRA_LONGLAT, coordinates);
		startActivityForResult(intent,MAP_CODE);
	}
	
	public void startCameraActivity(){
		mediaFileUri = Uri.fromFile(new File(mediaFilePath = MediaUriHelper.getOutputMediaFilePath())); 				    
    	Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	camera.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
    	startActivityForResult(camera, CAMERA_CODE);
	}
	
	public void startGalleryActivity(){
		Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    	startActivityForResult(gallery, GALLERY_CODE );
	}

	// Sets parameters
	
	public void setRelicParameters(){
		((EditText)findViewById(R.id.relic_edit_identification_id)).setText(relicDetailsData.getIdentification());
		if(!relicDetailsData.getDating().equals("")){
			((EditText)findViewById(R.id.relic_edit_dating_id)).setText(relicDetailsData.getDating());
		}
		if(!relicDetailsData.getLongitude().equals("")){
			((EditText)findViewById(R.id.relic_edit_longitude_id)).setText(relicDetailsData.getLongitude());
		}
		if(!relicDetailsData.getLatitude().equals("")){
			((EditText)findViewById(R.id.relic_edit_latitude_id)).setText(relicDetailsData.getLatitude());
		}
		if(!relicDetailsData.getStreet().equals("")){
			((EditText)findViewById(R.id.relic_edit_street_id)).setText(relicDetailsData.getStreet());
		}
		if(!relicDetailsData.getPlace().equals("")){
			((EditText)findViewById(R.id.relic_edit_place_id)).setText(relicDetailsData.getPlace());
		}
		if(!relicDetailsData.getCommune().equals("")){
			((EditText)findViewById(R.id.relic_edit_commune_id)).setText(relicDetailsData.getCommune());
		}
		if(!relicDetailsData.getDistrict().equals("")){
			((EditText)findViewById(R.id.relic_edit_district_id)).setText(relicDetailsData.getDistrict());
		}
		// Wojewodztwo
		if(relicDetailsData.getVoivodeship().equals("")){
			voivodshipSpinner.setSelection(0);
		}else{
			TypedArray array = getResources().obtainTypedArray(R.array.voivodships); 
			voivodship = relicDetailsData.getVoivodeship();
			Log.d("Otwarte Zabytki", voivodship);
			for(int i=0;i<array.length();i++){
				if(array.getString(i).equals("woj. "+voivodship)){
					voivodshipSpinner.setSelection(i);
				}				
			}
			
			array.recycle();
		}
		if(!relicDetailsData.getDescription().equals("")){
			((EditText)findViewById(R.id.relic_edit_description_id)).setText(relicDetailsData.getDescription());
		}
		
	}
	
	
	public void setMainPhotoBlank(){
		mediaFilePath = "";
		relic_edit_img.setImageResource(R.drawable.ic_default_relic);
	}
	
	
	
	public boolean getRelicParameters(){
		if(((EditText)findViewById(R.id.relic_edit_identification_id)).getText().toString().equals("")){
			return false;
		}
		if(!mediaFilePath.equals("")){
			mainPhotoPath = mediaFilePath;			
		}
		relicDetailsData = new DataRelicDetails(
				id,
				relic_id, 
				android_id,
				mainPhotoURL,
				mainPhotoPath,
				((EditText)findViewById(R.id.relic_edit_identification_id)).getText().toString(), 
				((EditText)findViewById(R.id.relic_edit_description_id)).getText().toString(), 
				state, 
				((EditText)findViewById(R.id.relic_edit_dating_id)).getText().toString(),
				((EditText)findViewById(R.id.relic_edit_street_id)).getText().toString(),
				((EditText)findViewById(R.id.relic_edit_place_id)).getText().toString(),
				((EditText)findViewById(R.id.relic_edit_commune_id)).getText().toString(),
				((EditText)findViewById(R.id.relic_edit_district_id)).getText().toString(),
				voivodship, 
				((EditText)findViewById(R.id.relic_edit_longitude_id)).getText().toString(),
				((EditText)findViewById(R.id.relic_edit_latitude_id)).getText().toString(),
				opinion);
		return true;
	}


	
    
     
	private class SingleScan implements MediaScannerConnectionClient {
	    private String path;
	    private String mimeType;
	    MediaScannerConnection connection;

	    public SingleScan(String path, String mimeType) {
	        this.path = path;
	        this.mimeType = mimeType; 
	    }

	    @Override
	    public void onMediaScannerConnected() {
	        connection.scanFile(path, mimeType);
	    }

	    @Override
	    public void onScanCompleted(String path, Uri uri) {
	        connection.disconnect();
	        Log.i("Otwarte Zabytki", "Scan for single file completed");
  
	    }
	}
}
