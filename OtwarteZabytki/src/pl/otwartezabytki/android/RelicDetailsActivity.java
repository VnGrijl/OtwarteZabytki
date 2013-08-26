package pl.otwartezabytki.android;

import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.otwartezabytki.android.database.LocalCheckListHandler;
import pl.otwartezabytki.android.database.LocalRelicDetailsHandler;
import pl.otwartezabytki.android.dataobjects.DataCheckListItem;
import pl.otwartezabytki.android.dataobjects.DataRelicDetails;
import pl.otwartezabytki.android.tools.ImageDownloader;
import pl.otwartezabytki.android.tools.NetworkInfoProvider;
import pl.otwartezabytki.android.tools.OtwarteZabytkiRestClient;
import pl.otwartezabytki.android.tools.ResolutionDependent;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RelicDetailsActivity extends SherlockActivity {

	private static final String BASE_URL = "http://otwartezabytki.pl/";
	private static final String API_KEY = "2WxArdodbNDLx7qpCTWp";
	public final static String EXTRA_LONGLAT = "pl.otwartezabytki.android.LONGLAT";
	public final static String EXTRA_CHECKLISTDATA = "pl.otwartezabytki.android.CHECKLISTDATA";
	public final static String EXTRA_OPINION = "pl.otwartezabytki.android.CHECKLISTOPINION";
	public final static int CHECKLIST = 1234;
	public final static int EDITRELIC = 1235;
	private RequestParams params;
	private ProgressBar progressBar;
	private ProgressBar progressBarMap;
	private LinearLayout errorLayout;
	private LinearLayout noconnectionLayout;
	ImageView staticMap;
	ImageView mainPhoto;
	TextView relic_details_checklist_button;
	boolean isFromSearch;
	
	// relic details
	private long id;
	private int relic_id;
	private int android_id = 0;
	private String mainPhotoURL="";
	private String mainPhotoPath="";
	private String identification="";
	private String description="";
	private String state="";
	private String dating="";
	private String street="";
	private String place="";
	private String commune="";
	private String district="";
	private String voivodeship="";
	private String longitude="";
	private String latitude="";
	private String opinion="";
	ArrayList<DataCheckListItem> checkListData;
	DataRelicDetails relicDetailsData;
	LocalRelicDetailsHandler localRelicDetailsDB;
	LocalCheckListHandler localCheckListDB;
	BitmapFactory.Options bitmapOptions;
	int imageSize;
	SharedPreferences sharedPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_relic_details);
		getSupportActionBar().setHomeButtonEnabled(true);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		progressBar = (ProgressBar) findViewById(R.id.relic_details_progress);
		progressBarMap = (ProgressBar) findViewById(R.id.relic_details_progress_map);
		errorLayout = (LinearLayout) findViewById(R.id.relic_details_connection_error_layout);
		noconnectionLayout = (LinearLayout) findViewById(R.id.relic_details_no_connection_layout);
		relic_details_checklist_button = (TextView) findViewById(R.id.relic_details_checklist_button);
		relic_details_checklist_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCheckListActivity();				
			}
		});
		staticMap = (ImageView) findViewById(R.id.relic_details_static_map);
		mainPhoto = (ImageView) findViewById(R.id.relic_details_img);
		localRelicDetailsDB = new LocalRelicDetailsHandler(this);
		localCheckListDB = new LocalCheckListHandler(this);
		
		imageSize = ResolutionDependent.getDetailImageDimensions(getResources().getDisplayMetrics().densityDpi);
    	bitmapOptions = new BitmapFactory.Options();
    	bitmapOptions.inSampleSize=8;
		
		relic_id = getIntent().getIntExtra(SearchResultsActivity.SEARCH2RELIC,0);
		id = getIntent().getLongExtra(HomeActivity.HOME2RELIC,0);
		if(relic_id == 0){
			// Started from HomeActivity
			setIsFromSearch(false);
			fetchRelicDataById(id);
			updateRelicParameters();
		}else{
			// Started from SearchResultsActivity
			if(fetchRelicDataByRelicId(relic_id)){
				setIsFromSearch(false);
				updateRelicParameters();
			}else{
				setIsFromSearch(true);
				params = new RequestParams();
				params.put("api_key", API_KEY);
				try {
					startJSONRequest(params);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(Menu.NONE, 0, Menu.NONE, getResources().getString(R.string.action_edit))
						.setIcon(R.drawable.ic_action_edit)
						.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		if (!isFromSearch){
	       	menu.add(Menu.NONE, 1, Menu.NONE, getResources().getString(R.string.action_delete))
	       				.setIcon(R.drawable.ic_action_delete)
	                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    }else{
	    	menu.add(Menu.NONE, 2, Menu.NONE, getResources().getString(R.string.action_save_noedit))
            			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	    }

	    menu.add(Menu.NONE, 3, Menu.NONE, getResources().getString(R.string.action_settings))
	                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	       
		//getSupportMenuInflater().inflate(R.menu.relic_details, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case 0:
	        	startEditActivity();
	            return true;
	        case 3:
	        	startSettingsActivity();
	            return true;
	        case 2:
	        	relicDetailsData = new DataRelicDetails(id, relic_id, android_id, mainPhotoURL, mainPhotoPath, identification, description, state, dating, street, place, commune, district, voivodeship, longitude, latitude, opinion);
	        	storeRelicData(relicDetailsData);
	        	Toast.makeText(getApplicationContext(), R.string.relic_details_save_confirm, Toast.LENGTH_SHORT).show();
	            return true;
	        case 1:
	        	deleteRelicData();
	            return true;
	        case android.R.id.home:
	        	startHomeActivity();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == EDITRELIC) {
	        if (resultCode == Activity.RESULT_OK) {
	        	Log.d("Otwarte Zabytki", "EditRelic Result");
	        	relicDetailsData = data.getParcelableExtra(RelicEditActivity.EXTRA_EDITRELIC);
	        	updateRelicParameters();	        	
	        	storeRelicData(relicDetailsData);
	        	return;
	        }
    	}
	    if (requestCode == CHECKLIST) {
	        if (resultCode == Activity.RESULT_OK) {
	        	Log.d("Otwarte Zabytki", "CheckList Result");
	        	checkListData = data.getParcelableArrayListExtra(CheckListActivity.EXTRA_CHECKLISTRESULT);
	        	opinion = data.getStringExtra(CheckListActivity.EXTRA_OPINION);
	        	relicDetailsData.setOpinion(opinion);
	        	storeCheckListAndRelicData(checkListData);
	        	return;
	        }
    	}
	}
	
	// Local DB
	private boolean fetchRelicDataByRelicId(int relic_id){
		relicDetailsData = localRelicDetailsDB.getRelicDetailsByRelicId(relic_id);
		if(relicDetailsData==null){
			return false;
		}
		return true;
	}
	
	private boolean fetchRelicDataById(long id){
		relicDetailsData = localRelicDetailsDB.getRelicDetailsById(id);
		if(relicDetailsData==null){
			return false;
		}
		return true;
	}
	
	private ArrayList<DataCheckListItem> fetchCheckListData(long id){
		return localCheckListDB.getCheckListData(id);
	}
	
	private void storeRelicData(DataRelicDetails relicDetails){
		if(id==0){
			id = localRelicDetailsDB.addRelicDetail(relicDetails);
			relicDetails.setId(id);
			setIsFromSearch(false);
		}else{
			localRelicDetailsDB.updateRelicDetail(relicDetails);
		}
		
	}
	
	private void storeCheckListAndRelicData(ArrayList<DataCheckListItem> checkList){
		if(id==0){
			relicDetailsData = new DataRelicDetails(id, relic_id, android_id, mainPhotoURL, mainPhotoPath, identification, description, state, dating, street, place, commune, district, voivodeship, longitude, latitude, opinion);
			id = localRelicDetailsDB.addRelicDetail(relicDetailsData);
			relicDetailsData.setId(id);
			localCheckListDB.addCheckListData(checkList, id);
			setIsFromSearch(false);
		}else{
			if(!opinion.equals("")){
				localRelicDetailsDB.updateRelicDetail(relicDetailsData);				
			}			
			if(checkList.get(0).getId()==0){
				localCheckListDB.addCheckListData(checkList, relicDetailsData.getId());
			}else{
				localCheckListDB.updateCheckListData(checkList);
			}
			
		}
	}
	
	private void deleteRelicData(){
		localCheckListDB.deleteCheckListData(relicDetailsData.getId());
		localRelicDetailsDB.deleteRelicDetail(relicDetailsData);
		finish();
	}
	
	// Set
	public void setIsFromSearch(boolean isFromSearch){
		this.isFromSearch = isFromSearch;
		if(!isFromSearch){
			supportInvalidateOptionsMenu();
		}	
	}
	
	public void setStaticMap(Bitmap b){
		staticMap.setImageBitmap(b);
		staticMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				startMapShowActivity();
			}
		});
	}
	
	public void setMainPhotoFromBitmap(Bitmap b){
		Log.d("Otwarte Zabytki","Main Photo Size: "+imageSize);
		mainPhoto.setImageBitmap(ThumbnailUtils.extractThumbnail(b,imageSize,imageSize));		
	}
	
	public void setMainPhotoFromPath(String mediaFilePath){
		mainPhoto.setImageBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mediaFilePath,bitmapOptions), imageSize, imageSize));
	}
	
	public void setRelicParameters(){
		
		// Name of object
		if (identification.equals("Dom") && !street.equals("")){
    		((TextView)findViewById(R.id.relic_details_identification)).setText(identification+", "+street);
    	}else{
    		((TextView)findViewById(R.id.relic_details_identification)).setText(identification);
    	} 
		
		
		// Description
		if(!description.equals("")){
			description = description.trim();
			description = description.replaceAll("\\s(\\S)\\s"," $1\u00A0");
			description = description.replaceAll("<p>","");
			description = description.replaceAll("</p>","");
			description = description.replaceAll("<br>","");
			description = description.replaceAll("&nbsp;","");
			if(description.indexOf("<span")>0){	
				description = description.substring(0,description.indexOf("<span"));
			}
			((TextView)findViewById(R.id.relic_details_description)).setText(description);
			((TextView)findViewById(R.id.relic_details_description)).setVisibility(View.VISIBLE);	
			((View)findViewById(R.id.relic_details_description_divider)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.relic_details_description_label)).setVisibility(View.VISIBLE);
		}else{
			((TextView)findViewById(R.id.relic_details_description)).setVisibility(View.GONE);	
			((View)findViewById(R.id.relic_details_description_divider)).setVisibility(View.GONE);
			((TextView)findViewById(R.id.relic_details_description_label)).setVisibility(View.GONE);
		}
		
		
		// Location of object
		if(!street.equals("")){
			((TextView)findViewById(R.id.relic_details_location_1)).setText(street);
			((TextView)findViewById(R.id.relic_details_location_1)).setVisibility(View.VISIBLE);
		}else{((TextView)findViewById(R.id.relic_details_location_1)).setVisibility(View.GONE);	}
		if(!place.equals("") || !commune.equals("")){
			if(!place.equals("") && !commune.equals("")){
				((TextView)findViewById(R.id.relic_details_location_2)).setText(place+", gm. "+commune);
			}
			if(!place.equals("") && commune.equals("")){
				((TextView)findViewById(R.id.relic_details_location_2)).setText(place);
			}
			if(place.equals("") && !commune.equals("")){
				((TextView)findViewById(R.id.relic_details_location_2)).setText("gm. "+commune);
			}
			((TextView)findViewById(R.id.relic_details_location_2)).setVisibility(View.VISIBLE);
		}else{((TextView)findViewById(R.id.relic_details_location_2)).setVisibility(View.GONE);	}
		if(!district.equals("")){
			((TextView)findViewById(R.id.relic_details_location_3)).setText("powiat "+district);
			((TextView)findViewById(R.id.relic_details_location_3)).setVisibility(View.VISIBLE);	
		}else{((TextView)findViewById(R.id.relic_details_location_3)).setVisibility(View.GONE);	}
		if(!voivodeship.equals("")){
			((TextView)findViewById(R.id.relic_details_location_4)).setText("woj. "+voivodeship);
			((TextView)findViewById(R.id.relic_details_location_4)).setVisibility(View.VISIBLE);
		}else{((TextView)findViewById(R.id.relic_details_location_4)).setVisibility(View.GONE);	}
    	
    	if(!longitude.equals("") && !latitude.equals("")){
    		startLoadStaticMap(longitude, latitude);
    	}else{
    		//Za³aduj domyslny obrazek mapy
    	}
    	
    	
    	// Dating of object
    	if(!dating.equals("")){
    		String tmp = getResources().getString(R.string.relic_details_dating_label);
        	((TextView)findViewById(R.id.relic_details_dating)).setText(tmp +" "+ dating);
        	((TextView)findViewById(R.id.relic_details_dating)).setVisibility(View.VISIBLE);
    	}else{
    		((TextView)findViewById(R.id.relic_details_dating)).setVisibility(View.GONE);
    	}
    	
    	// State of object
    	if(!state.equals("")){
    		if(state.equals("unchecked")){
    			((TextView)findViewById(R.id.relic_details_state)).setText(getResources().getString(R.string.relic_details_state_unchecked));
    			((TextView)findViewById(R.id.relic_details_state)).setBackgroundResource(R.drawable.draw_state_3_rectangle);
    		}
    		else if(state.equals("checked")){
    			((TextView)findViewById(R.id.relic_details_state)).setText(getResources().getString(R.string.relic_details_state_checked));
    			((TextView)findViewById(R.id.relic_details_state)).setBackgroundResource(R.drawable.draw_state_2_rectangle);
    		}
    		else if(state.equals("filled")){
    			((TextView)findViewById(R.id.relic_details_state)).setText(getResources().getString(R.string.relic_details_state_filled));
    			((TextView)findViewById(R.id.relic_details_state)).setBackgroundResource(R.drawable.draw_state_1_rectangle);
    		}
    		else{
    			((TextView)findViewById(R.id.relic_details_state)).setVisibility(View.GONE);
    		}
    	}else{
    		((TextView)findViewById(R.id.relic_details_state)).setVisibility(View.GONE);
    	}
    	
    	
	}
	
	public void updateRelicParameters(){
		relic_id = relicDetailsData.getRelic_id();
		mainPhotoURL = relicDetailsData.getMainPhotoURL();
		mainPhotoPath = relicDetailsData.getMainPhotoPath();
		if(!mainPhotoPath.equals("")){
			setMainPhotoFromPath(mainPhotoPath);
		}else if(!mainPhotoURL.equals("")){
			startLoadMainPhoto(mainPhotoURL);
		}else{
			mainPhoto.setImageResource(R.drawable.ic_default_relic);
		}
		identification = relicDetailsData.getIdentification();
		description = relicDetailsData.getDescription();
		state = relicDetailsData.getState();
		dating = relicDetailsData.getDating();
		street = relicDetailsData.getStreet();
		place = relicDetailsData.getPlace();
		commune = relicDetailsData.getCommune();
		district = relicDetailsData.getDistrict();
		voivodeship = relicDetailsData.getVoivodeship();
		longitude = relicDetailsData.getLongitude();
		latitude = relicDetailsData.getLatitude();
		opinion = relicDetailsData.getOpinion();
		setRelicParameters();		
	}
	
	public void restartJSONRequest(View v){
		try {
			startJSONRequest(params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Start Activities, Tasks
	public void startCheckListActivity(){
		if(isFromSearch){
			checkListData = null;
		}else{
			checkListData = fetchCheckListData(id);
		}
		Intent intent = new Intent(RelicDetailsActivity.this, CheckListActivity.class);
		intent.putExtra(EXTRA_OPINION, opinion);
		intent.putParcelableArrayListExtra(EXTRA_CHECKLISTDATA, checkListData);
		startActivityForResult(intent,CHECKLIST);	
	}
	
	
	public void startEditActivity(){
		relicDetailsData = new DataRelicDetails(id, relic_id, android_id, mainPhotoURL, mainPhotoPath, identification, description, state, dating, street, place, commune, district, voivodeship, longitude, latitude, opinion);
		Intent intent = new Intent(RelicDetailsActivity.this, RelicEditActivity.class);
		intent.putExtra(RelicEditActivity.EDITORADD, true);
		intent.putExtra(RelicEditActivity.EXTRA_EDITRELIC, relicDetailsData);
		startActivityForResult(intent,EDITRELIC);
	}
	
	public void startSettingsActivity(){
		Intent intent = new Intent(RelicDetailsActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void startHomeActivity(){
		Intent intent = new Intent(RelicDetailsActivity.this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void startMapShowActivity(){
		Intent intent = new Intent(RelicDetailsActivity.this, MapShowActivity.class);
		intent.putExtra(EXTRA_LONGLAT, new String[]{longitude,latitude});
		startActivity(intent);	
	}
	

	public void startLoadStaticMap(String longitude, String latitude) {
		progressBarMap.setVisibility(View.VISIBLE);
		int size = ResolutionDependent.getStaticMapDimensions(getResources().getDisplayMetrics().densityDpi);
		String url = "http://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude+"&zoom=15&size="+size+"x"+size+"&markers=color:red|"+latitude+","+longitude+"&sensor=false";
		new StaticMapLoadTask().execute(url);		
	}
	
	
	public void startLoadMainPhoto(String url){
		if(sharedPrefs.getBoolean("get_pictures", true)){
			new MainPhotoLoadTask().execute(url);
		}
	}
	
	
	public void startJSONRequest(RequestParams parameters) throws JSONException {
		OtwarteZabytkiRestClient.get("relics/" + relic_id + ".json", parameters, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
            	Log.d("Otwarte Zabytki", "on Success received JSONArray");
            }
            
            @Override
            public void onSuccess(JSONObject response) {
            	Log.i("Otwarte Zabytki", "on Success received JSONObject");
            	
            	try {
            		findViewById(R.id.relic_details_header).setVisibility(View.VISIBLE);
            		findViewById(R.id.relic_details_content).setVisibility(View.VISIBLE);
            		findViewById(R.id.relic_details_buttons).setVisibility(View.VISIBLE);
            		identification = response.getString("identification");
            		description = response.getString("description");
            		state = response.getString("state");
            		dating = response.getString("dating_of_obj");
            		street = response.getString("street");
            		place = response.getString("place_name");
            		commune = response.getString("commune_name");
            		district = response.getString("district_name");
            		voivodeship = response.getString("voivodeship_name");
            		longitude = response.getString("longitude");
            		latitude = response.getString("latitude");
            		
            		JSONObject mainPhoto = response.getJSONObject("main_photo");
        		    if(!mainPhoto.isNull("id")){
        		    	mainPhotoURL = BASE_URL + mainPhoto.getJSONObject("file").getJSONObject("maxi").getString("url");
        		    	startLoadMainPhoto(mainPhotoURL);
        		    }
					
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
            	
            	setRelicParameters();
            	
            }
            
            @Override
            public void onStart() {
            	findViewById(R.id.relic_details_header).setVisibility(View.GONE);
        		findViewById(R.id.relic_details_content).setVisibility(View.GONE);
        		findViewById(R.id.relic_details_buttons).setVisibility(View.GONE);
        		progressBar.setVisibility(View.VISIBLE);
            	Log.d("Otwarte Zabytki", "on Start send REST get");
            }
            
            @Override
        	public void onFailure(Throwable error, String content) {
            	Log.d("Otwarte Zabytki", "on Failure received String");
            	if ( error.getCause() instanceof ConnectTimeoutException ) {
            		Log.d("Otwarte Zabytki","Connection timeout!");
            		errorLayout.setVisibility(View.VISIBLE);            		
            	}else if(error.getClass().getCanonicalName().equals("java.net.UnknownHostException")){
            		Log.d("Otwarte Zabytki","Unknown host!");
            		if(NetworkInfoProvider.getInstance(RelicDetailsActivity.this).isOnline(RelicDetailsActivity.this)){
            			errorLayout.setVisibility(View.VISIBLE);               		
            		}else{
            			noconnectionLayout.setVisibility(View.VISIBLE);               		
            		}
            	}else{
            		errorLayout.setVisibility(View.VISIBLE);            		
            	}
        	}
            
            @Override
            public void onFailure(Throwable e,  JSONObject response) {
            	Log.d("Otwarte Zabytki", "on Failure received JSONObject");
            }
            
            @Override
            public void onFailure(Throwable e,  JSONArray response) {
            	Log.d("Otwarte Zabytki", "on Failure received JSONArray");
            }

            @Override
            public void onFinish() {
            	Log.i("Otwarte Zabytki", "on Start send REST get");
            	progressBar.setVisibility(View.GONE);
            }
        });
	}
	
	// AsyncTasks
	
	
	private class StaticMapLoadTask extends AsyncTask<String, String, Bitmap> {

		@Override
		protected void onPreExecute() {
			Log.d("Otwarte Zabytki", "Loading map...");
		}

		protected Bitmap doInBackground(String... param) {
			Log.d("Otwarte Zabytki", "Attempting to load map URL: " + param[0]);
			try {
				Bitmap b = ImageDownloader.getBitmapFromURL(param[0]);
				return b;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		protected void onProgressUpdate(String... progress) {
			
		}

		protected void onPostExecute(Bitmap b) {
			if (b != null) {
				Log.i("Otwarte Zabytki", "Successfully loaded static map");
				progressBarMap.setVisibility(View.GONE);
				setStaticMap(b);
				
			} else {
				Log.e("Otwarte Zabytki", "Failed to load static map");
			}
		}
	}
	
		
	private class MainPhotoLoadTask extends AsyncTask<String, String, Bitmap> {

		@Override
		protected void onPreExecute() {
			Log.i("Otwarte Zabytki", "Loading image...");
		}

		protected Bitmap doInBackground(String... param) {
			Log.i("Otwarte Zabytki", "Attempting to load image URL: " + param[0]);
			try {
				Bitmap b = ImageDownloader.getBitmapFromURL(param[0]);
				return b;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		protected void onProgressUpdate(String... progress) {
			
		}

		protected void onPostExecute(Bitmap b) {
			if (b != null) {
				Log.i("Otwarte Zabytki", "Successfully loaded image");
				setMainPhotoFromBitmap(b);
			} else {
				Log.e("Otwarte Zabytki", "Failed to load image");
			}
		}
	}
		
}
