package pl.otwartezabytki.android;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;


public class MapShowActivity extends SherlockFragmentActivity {

	private GoogleMap map;
	private double longitude;
	private double latitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_show);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		String[] longlat = getIntent().getStringArrayExtra(RelicDetailsActivity.EXTRA_LONGLAT);
		longitude = Double.parseDouble(longlat[0]);
		latitude = Double.parseDouble(longlat[1]);
		 if (map == null) {
		    	map = ((SupportMapFragment ) getSupportFragmentManager().findFragmentById(R.id.map))
		                            .getMap();
		    	 if (map != null) {
		    		 map.getUiSettings().setMyLocationButtonEnabled(true);
		    		 map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),13));
		    		 
		    		 final LatLng RELIC = new LatLng(latitude, longitude);
		    		 //Marker relic = 
		    		 map.addMarker(new MarkerOptions()
		    		               	.position(RELIC)
		    		               	.title(getResources().getString(R.string.relic_details_coordinates))
		    		 				.snippet(longlat[0]+", "+longlat[1]));


			     }
		 }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.map_show, menu);
		return true;
	}

}
