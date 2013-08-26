package pl.otwartezabytki.android;

import java.util.List;

import pl.otwartezabytki.android.database.LocalRelicDetailsHandler;
import pl.otwartezabytki.android.dataobjects.DataRelicDetails;
import pl.otwartezabytki.android.dataobjects.DataRelicHighlights;
import pl.otwartezabytki.android.tools.AdapterRelicHighlights;
import com.actionbarsherlock.app.SherlockListActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends SherlockListActivity {

	public final static int ADDRELIC = 3235;
	public final static String HOME2RELIC = "pl.otwartezabytki.android.HOME2RELIC";
	DataRelicDetails relicDetailsData;
	LocalRelicDetailsHandler localDB;
	List<DataRelicHighlights> relicList;
	AdapterRelicHighlights adapter;
	private int myRelicsCount = 0;
	SharedPreferences sharedPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		localDB = new LocalRelicDetailsHandler(this);
		calculateLayout();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(myRelicsCount!=getRelicCount()){
			Log.d("Otwarte Zabytki", "calculating");
			calculateLayout();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		     
		getSupportMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_search:
	        	startSearchActivity();
	            return true;
	        case R.id.action_add:
	        	startAddActivity();
	            return true;
	        case R.id.action_settings:
	        	startSettingsActivity();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("Otwarte Zabytki","Picked #"+position+" from listview");
		startRelicDetailsActivity(position);	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == ADDRELIC) {
	        if (resultCode == Activity.RESULT_OK) {
	        	Log.d("Otwarte Zabytki", "EditRelic Result");
	        	relicDetailsData = data.getParcelableExtra(RelicEditActivity.EXTRA_EDITRELIC);
	        	storeRelicData(relicDetailsData);
	        	calculateLayout();
	        	return;
	        }
    	}
	}

	private void storeRelicData(DataRelicDetails relicDetails){
		localDB.addRelicDetail(relicDetails);
	}
	
	private void calculateLayout(){
		relicList = localDB.getRelics();
		myRelicsCount = relicList.size();
		if(myRelicsCount > 0){
			(( RelativeLayout ) findViewById(R.id.home_splash_id)).setVisibility(View.GONE);
			getListView().setVisibility(View.VISIBLE);
			initiateAdapter();
		}else{
			(( RelativeLayout ) findViewById(R.id.home_splash_id)).setVisibility(View.VISIBLE);
			getListView().setVisibility(View.INVISIBLE);
			initiateAdapter();
		}
	}
	
	private int getRelicCount(){
		return localDB.getRelicCount();
	}
		
	private void initiateAdapter(){
		adapter = new AdapterRelicHighlights(HomeActivity.this, relicList);
		setListAdapter(adapter);
		int dpi = getResources().getDisplayMetrics().densityDpi;
		if(sharedPrefs.getBoolean("get_pictures", true)){
			for (DataRelicHighlights s : relicList) { 
				s.loadImage(adapter,dpi);
			}
		}
		
	}
	
	

	public void startSearchActivity(){
		Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
		startActivity(intent);
	}
	
	public void startRelicDetailsActivity(int position){
		DataRelicHighlights chosenRelic = relicList.get(position);
		Intent intent = new Intent(HomeActivity.this, RelicDetailsActivity.class);
		intent.putExtra(HOME2RELIC, chosenRelic.getId());
		startActivity(intent);	
	}
	
	public void startAddActivity(){
		relicDetailsData = null;
		Intent intent = new Intent(HomeActivity.this, RelicEditActivity.class);
		intent.putExtra(RelicEditActivity.EDITORADD, false);
		intent.putExtra(RelicEditActivity.EXTRA_EDITRELIC, relicDetailsData);
		startActivityForResult(intent,ADDRELIC);
	}
	
	public void startSettingsActivity(){
		Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

}
