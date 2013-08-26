package pl.otwartezabytki.android;

import java.util.ArrayList;

import pl.otwartezabytki.android.fragments.SearchContentListFragment;
import pl.otwartezabytki.android.fragments.SearchStateListFragment;
import pl.otwartezabytki.android.fragments.SearchTypeListFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;
import android.content.res.TypedArray;

public class SearchActivity extends SherlockFragmentActivity {
	
	public final static String STATEARRAY = "pl.otwartezabytki.android.STATEARRAY";
	public final static String CONTENTARRAY = "pl.otwartezabytki.android.CONTENTARRAY";
	public final static String TYPEARRAY = "pl.otwartezabytki.android.TYPEARRAY";
	public final static String WHAT = "pl.otwartezabytki.android.WHAT";
	public final static String WHERE = "pl.otwartezabytki.android.WHERE";
	public final static String FROM = "pl.otwartezabytki.android.FROM";
	public final static String TO = "pl.otwartezabytki.android.TO";
	LinearLayout filterLayout;
	LinearLayout dateLayout;
	LinearLayout stateLayout;
	LinearLayout contentLayout;
	LinearLayout typeLayout;
	TextView statesText;
	TextView contentsText;
	TextView typesText;
	RelativeLayout filterParamsLabelLayout;
	RelativeLayout dateParamsLabelLayout;
	ImageView filterFoldButton;
	ImageView dateFoldButton;
	boolean filterParamsShown = false;
	boolean dateParamsShown = false;
	boolean[] states = {false,false,false};
	boolean[] contents = {false,false,false,false};
	boolean[] types = {false,false};
	ArrayList<String> statesValues;
	ArrayList<String> contentsValues;
	ArrayList<String> typesValues;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff2e444e));
		
		stateLayout = (LinearLayout) findViewById(R.id.search_filter_params_state_layout);
		stateLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = SearchStateListFragment.newInstance(states);
				newFragment.show(getSupportFragmentManager(), "StatesDialog");	
			}
		});
		
		contentLayout = (LinearLayout) findViewById(R.id.search_filter_params_content_layout);
		contentLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = SearchContentListFragment.newInstance(contents);
				newFragment.show(getSupportFragmentManager(), "ContentsDialog");			
			}
		});
		
		typeLayout = (LinearLayout) findViewById(R.id.search_filter_params_type_layout);
		typeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = SearchTypeListFragment.newInstance(types);
				newFragment.show(getSupportFragmentManager(), "TypesDialog");			
			}
		});
		
		filterLayout = (LinearLayout) findViewById(R.id.search_filter_params);
		filterFoldButton = (ImageView) findViewById(R.id.search_filter_label_icon);
		filterParamsLabelLayout = (RelativeLayout) findViewById(R.id.search_filter_label_layout_id);
		filterParamsLabelLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(filterParamsShown){
					filterLayout.setVisibility(View.GONE);
					filterFoldButton.setImageResource(R.drawable.ic_arrow_unfold);
					filterParamsShown = false;
				}else{
					filterLayout.setVisibility(View.VISIBLE);
					filterFoldButton.setImageResource(R.drawable.ic_arrow_fold);
					filterParamsShown = true;
				}		
			}
		});

		dateLayout = (LinearLayout) findViewById(R.id.search_date_params);
		dateFoldButton = (ImageView) findViewById(R.id.search_date_label_icon);
		dateParamsLabelLayout = (RelativeLayout) findViewById(R.id.search_date_label_layout_id);
		dateParamsLabelLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(dateParamsShown){
					dateLayout.setVisibility(View.GONE);
					dateFoldButton.setImageResource(R.drawable.ic_arrow_unfold);
					dateParamsShown = false;
				}else{
					dateLayout.setVisibility(View.VISIBLE);
					dateFoldButton.setImageResource(R.drawable.ic_arrow_fold);
					dateParamsShown = true;
				}		
			}
		});
		statesText = (TextView) findViewById(R.id.search_filter_params_state);
		contentsText = (TextView) findViewById(R.id.search_filter_params_content);
		typesText = (TextView) findViewById(R.id.search_filter_params_type);
		setStateItemsValues(states);
		setContentItemsValues(contents);
		setTypeItemsValues(types);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.search, menu);
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
	
	public void setStateItemsValues(boolean[] states){
		this.states = states;
		TypedArray statesArray = getResources().obtainTypedArray(R.array.relic_states);
		statesValues = new ArrayList<String>();
		statesText.setText("");
		for(int i=0;i<states.length;i++){
			if(states[i]){
				statesValues.add(statesArray.getString(i));				
			}
		}
		for (int i = 0; i < statesValues.size(); i++) {
			
		    if(i!=0){
		    	String tmp = (String) statesText.getText();
		    	statesText.setText(tmp + ", "+statesValues.get(i));
		    }else{
		    	statesText.setText(statesValues.get(i));
		    }
		}		
		statesArray.recycle();
	}
	
	public void setContentItemsValues(boolean[] contents){
		this.contents = contents;
		TypedArray contentsArray = getResources().obtainTypedArray(R.array.relic_contents);
		contentsValues = new ArrayList<String>();
		contentsText.setText("");
		for(int i=0;i<contents.length;i++){
			if(contents[i]){
				contentsValues.add(contentsArray.getString(i));				
			}
		}
		for (int i = 0; i < contentsValues.size(); i++) {
			
		    if(i!=0){
		    	String tmp = (String) contentsText.getText();
		    	contentsText.setText(tmp + ", "+contentsValues.get(i));
		    }else{
		    	contentsText.setText(contentsValues.get(i));
		    }
		}		
		contentsArray.recycle();
	}
	
	public void setTypeItemsValues(boolean[] types){
		this.types = types;
		TypedArray typesArray = getResources().obtainTypedArray(R.array.relic_types);
		typesValues = new ArrayList<String>();
		typesText.setText("");
		for(int i=0;i<types.length;i++){
			if(types[i]){
				typesValues.add(typesArray.getString(i));				
			}
		}
		for (int i = 0; i < typesValues.size(); i++) {
			
		    if(i!=0){
		    	String tmp = (String) typesText.getText();
		    	typesText.setText(tmp + ", "+typesValues.get(i));
		    }else{
		    	typesText.setText(typesValues.get(i));
		    }
		}		
		typesArray.recycle();
	}
	
	public void startSettingsActivity(){
		Intent intent = new Intent(SearchActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void startSearchResults(View view) { 		
			Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
			intent.putStringArrayListExtra(STATEARRAY, statesValues);
			intent.putStringArrayListExtra(CONTENTARRAY, contentsValues);
			intent.putStringArrayListExtra(TYPEARRAY, typesValues);
			intent.putExtra(WHAT,((EditText)findViewById(R.id.search_what_id)).getText().toString());
			intent.putExtra(WHERE, ((EditText)findViewById(R.id.search_where_id)).getText().toString());
			intent.putExtra(FROM, ((EditText)findViewById(R.id.search_from_id)).getText().toString());
			intent.putExtra(TO, ((EditText)findViewById(R.id.search_to_id)).getText().toString());
			startActivity(intent);
		
	}
	
	public void startHomeActivity(){
		Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
