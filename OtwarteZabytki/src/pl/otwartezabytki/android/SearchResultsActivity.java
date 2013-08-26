package pl.otwartezabytki.android;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.*;

import pl.otwartezabytki.android.dataobjects.DataSearchResult;
import pl.otwartezabytki.android.tools.NetworkInfoProvider;
import pl.otwartezabytki.android.tools.OtwarteZabytkiRestClient;
import pl.otwartezabytki.android.tools.AdapterSearchResults;

import com.loopj.android.http.*;


import com.actionbarsherlock.app.SherlockListActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SearchResultsActivity extends SherlockListActivity {

	public final static String SEARCH2RELIC = "pl.otwartezabytki.android.SEARCH2RELIC";
	private static final String BASE_URL = "http://otwartezabytki.pl/";
	private static final String API_KEY = "2WxArdodbNDLx7qpCTWp";
	private int total_pages;
	private int current_page=0;
	private int total_count;
	
	private RequestParams params;
	private ProgressBar progressBar;
	private ProgressBar progressBarMore;
	private View footerView;
	private TextView showMore;
	private TextView noResults;
	private LinearLayout errorLayout;
	private LinearLayout noconnectionLayout;
	
	private List<DataSearchResult> searchResults = new ArrayList<DataSearchResult>();
	private AdapterSearchResults adapter;
	SharedPreferences sharedPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		getSupportActionBar().setHomeButtonEnabled(true);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		getListView().setVisibility(View.INVISIBLE);
		
		params = new RequestParams();
		params.put("query", getIntent().getStringExtra(SearchActivity.WHAT));
		if(getIntent().getStringExtra(SearchActivity.WHERE).equals("")){
			params.put("place", "kraków");
		}else{
			params.put("place", getIntent().getStringExtra(SearchActivity.WHERE)); 
		}		
		//params.put("place", getIntent().getStringExtra(SearchActivity.WHERE)); 
		params.put("categories", "przemyslowy_poprzemyslowy,budynek_gospodarczy,mieszkalny,uzytecznosci_publicznej,architektura_inzynieryjna,mala_architektura,dworski_palacowy_zamek,militarny,sportowy_kulturalny_edukacyjny,uklad_urbanistyczny_zespol_budowlany,sakralny,katolicki,prawoslawny,protestancki,zydowski,lemkowski,muzulmanski,unicki");
		params.put("from", getIntent().getStringExtra(SearchActivity.FROM));
		params.put("to", getIntent().getStringExtra(SearchActivity.TO));
		params.put("state", translateStates(getIntent().getStringArrayListExtra(SearchActivity.STATEARRAY)));
		params.put("existence", translateTypes(getIntent().getStringArrayListExtra(SearchActivity.TYPEARRAY)));
		params.put("has_photos", calculateHasPhoto(getIntent().getStringArrayListExtra(SearchActivity.CONTENTARRAY)));
		params.put("has_description", calculateHasDescription(getIntent().getStringArrayListExtra(SearchActivity.CONTENTARRAY)));
		params.put("api_key", API_KEY);
		 
		getSearchResults(params);
			
		footerView = ((LayoutInflater) SearchResultsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_search_footer, null, false);
		showMore = (TextView) footerView.findViewById(R.id.search_show_more_results_id);
		showMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showMoreSearchResults();
				
			}
		});
		getListView().addFooterView(footerView);
		
		errorLayout = (LinearLayout) findViewById(R.id.search_result_connection_error_layout);
		noconnectionLayout = (LinearLayout) findViewById(R.id.search_result_no_connection_layout);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar_search);
		progressBarMore = (ProgressBar) footerView.findViewById(R.id.search_show_more_progress_bar);
		noResults = (TextView) findViewById(R.id.search_no_results_id);
	}
	
	public void repeatSearchResults(View v) throws ConnectTimeoutException{
		Log.i("Otwarte Zabytki", "Repeat");
		
		getSearchResults(params);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.search_results, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	startSettingsActivity();
	            return true;
	        case android.R.id.home:
	        	startHomeActivity();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public String calculateHasPhoto(ArrayList<String> contents){
		String hasPhotos = "";
		boolean has = contents.contains("ze zdjêciem");
		boolean hasnt = contents.contains("brak zdjêcia");
		if(has && hasnt){
			return "";
		}
		else if(has){
			return "true";
		}
		else if(hasnt){
			return "false";
		}
		return hasPhotos;
	}
	
	public String calculateHasDescription(ArrayList<String> contents){
		String hasDescription = "";
		boolean has = contents.contains("z opisem");
		boolean hasnt = contents.contains("brak opisu");
		if(has && hasnt){
			return "";
		}
		else if(has){
			return "true";
		}
		else if(hasnt){
			return "false";
		}
		return hasDescription;
	}
	
	public String translateStates(ArrayList<String> states){
		String statesParam = "";
		for (int i = 0; i < states.size(); i++) {			
		    if(i!=0){
		    	statesParam = statesParam + ","+states.get(i);
		    }else{
		    	statesParam = states.get(i);
		    }
		}
		statesParam = statesParam.replaceAll("uzupe³niony","filled");
		statesParam = statesParam.replaceAll("sprawdzony","checked");
		statesParam = statesParam.replaceAll("niesprawdzony","unchecked");
		return statesParam;
	}
	
	public String translateTypes(ArrayList<String> types){
		String typesParam = "";
		for (int i = 0; i < types.size(); i++) {			
		    if(i!=0){
		    	typesParam = typesParam + ","+types.get(i);
		    }else{
		    	typesParam = types.get(i);
		    }
		}
		typesParam = typesParam.replaceAll("wpisany do rejestru zabytków","existed");
		typesParam = typesParam.replaceAll("dodany spo³ecznoœciowo","social");
		//typesParam = typesParam + ",archived";
		return typesParam;
	}
	
	public void getSearchResults(RequestParams parameters) {
		OtwarteZabytkiRestClient.get("relics.json", parameters, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
            	Log.d("Otwarte Zabytki", "on Success received JSONArray");
            }
            
            @Override
            public void onSuccess(JSONObject response) {
            	getListView().setVisibility(View.VISIBLE);
            	Log.i("Otwarte Zabytki", "on Success received JSONObject");
            	try {
            		JSONObject meta = response.getJSONObject("meta");
            		total_pages = meta.getInt("total_pages");
            		current_page = meta.getInt("current_page");
					total_count = meta.getInt("total_count");
					if(total_count>0){
						getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_search_results)+" ("+total_count+")");
					}
					
					processJSONRelics(response.getJSONArray("relics"));
            	} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }
            
            @Override
            public void onStart() {
            	Log.d("Otwarte Zabytki", "on Start send REST get");
            	errorLayout.setVisibility(View.GONE);
            	
            }
            
            @Override
        	public void onFailure(Throwable error, String content) {
            	Log.d("Otwarte Zabytki", "on Failure received String " + error.getClass().getCanonicalName().toString());
            	if ( error.getCause() instanceof ConnectTimeoutException ) {
            		Log.d("Otwarte Zabytki","Connection timeout!");
            		errorLayout.setVisibility(View.VISIBLE);
            		getListView().setVisibility(View.INVISIBLE);
            	}else if(error.getClass().getCanonicalName().equals("java.net.UnknownHostException")){
            		Log.d("Otwarte Zabytki","Unknown host!");
            		if(NetworkInfoProvider.getInstance(SearchResultsActivity.this).isOnline(SearchResultsActivity.this)){
            			errorLayout.setVisibility(View.VISIBLE);
                		getListView().setVisibility(View.INVISIBLE);
            		}else{
            			noconnectionLayout.setVisibility(View.VISIBLE);
                		getListView().setVisibility(View.INVISIBLE);
            		}
            	}else{
            		errorLayout.setVisibility(View.VISIBLE);
            		getListView().setVisibility(View.INVISIBLE);
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
            	Log.d("Otwarte Zabytki", "on Finish send REST get");
            	progressBar.setVisibility(View.GONE);
            	progressBarMore.setVisibility(View.INVISIBLE);
            }
        });
    }

	public void processJSONRelics(JSONArray relics) throws JSONException{
		for (int i = 0; i < relics.length(); i++) {
		    JSONObject singleRelic = relics.getJSONObject(i);
		    JSONObject mainPhoto = singleRelic.getJSONObject("main_photo");
		    String icon = null;
		    if(!mainPhoto.isNull("id")){
		    	icon = BASE_URL + mainPhoto.getJSONObject("file").getJSONObject("maxi").getString("url");
		    }
		    searchResults.add(new DataSearchResult(
		    		singleRelic.getInt("id"),
		    		singleRelic.getString("identification"),
		    		singleRelic.getString("place_name"),
		    		singleRelic.getString("state"),
		    		singleRelic.getString("street"),
		    		singleRelic.getString("voivodeship_name"),
		    		icon));
		    
		    
		}
		if(current_page == 1){
			initiateAdapter();
		}
		else{
			extendAdapter();
		}
		if(current_page >= total_pages){
			showMore.setVisibility(View.GONE);
		}else{
			showMore.setVisibility(View.VISIBLE);
		}
		if(total_count==0){
			noResults.setVisibility(View.VISIBLE);
		}
	}
	
	private void initiateAdapter(){
		adapter = new AdapterSearchResults(SearchResultsActivity.this, searchResults);
		setListAdapter(adapter);
		int dpi = getResources().getDisplayMetrics().densityDpi;
		if(sharedPrefs.getBoolean("get_pictures", true)){
			for (DataSearchResult s : searchResults) { 
	            s.loadImage(adapter,dpi);
			}
		}
		
	}
	
	private void extendAdapter(){
		adapter.notifyDataSetChanged();
		int dpi = getResources().getDisplayMetrics().densityDpi;
		if(sharedPrefs.getBoolean("get_pictures", true)){
			for (DataSearchResult s : searchResults) { 
	            s.loadImage(adapter,dpi);
			}
		}
	}
	
	public void showMoreSearchResults(){
		showMore.setVisibility(View.GONE);
		progressBarMore.setVisibility(View.VISIBLE);
		params.put("page", Integer.toString(current_page+1));
		getSearchResults(params);
						
	}
	
	public void startHomeActivity(){
		Intent intent = new Intent(SearchResultsActivity.this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void startSettingsActivity(){
		Intent intent = new Intent(SearchResultsActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	
		if(position == total_count || position == current_page*30){
			return;
		}
		Log.d("Otwarte Zabytki","Picked #"+position+" from listview");
		DataSearchResult chosenRelic = searchResults.get(position);
		Intent intent = new Intent(SearchResultsActivity.this, RelicDetailsActivity.class);
		intent.putExtra(SEARCH2RELIC, chosenRelic.getId());
		startActivity(intent);
		
	}
	
}
