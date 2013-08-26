package pl.otwartezabytki.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapGetActivity extends SherlockFragmentActivity {

	private final static double MIDDLE_POLAND_LAT = 52.069167;
	private final static double MIDDLE_POLAND_LON = 19.480556;
	private final static int MIDDLE_POLAND_ZOOM = 6;
	private final static int RELIC_ZOOM = 13;
	public final static String LATLNG = "pl.otwartezabytki.android.LATLNG";
	
	private ActionMode mode;
	private GoogleMap map;
	private LatLng point;
	private Marker marker;
	private double longitude;
	private double latitude;
	private int zoom;
	private boolean isNew;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_get);
		getSupportActionBar().setSubtitle(R.string.subtitle_activity_map_get);
		
		String[] longlat = getIntent().getStringArrayExtra(RelicEditActivity.EXTRA_LONGLAT);
		if(!longlat[0].equals("") && longlat[0] != null && !longlat[1].equals("") && longlat[1] != null){
			longitude = Double.parseDouble(longlat[0]);
			latitude = Double.parseDouble(longlat[1]);
			point = new LatLng(latitude, longitude);
			isNew = false;
			zoom = RELIC_ZOOM;
		}
		else{
			point = new LatLng(MIDDLE_POLAND_LAT, MIDDLE_POLAND_LON);
			isNew=true;
			zoom = MIDDLE_POLAND_ZOOM;
		}
		 if (map == null) {
		    	map = ((SupportMapFragment ) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		    	 if (map != null) {
		    		 map.getUiSettings().setMyLocationButtonEnabled(true);
		    		 map.moveCamera(CameraUpdateFactory.newLatLngZoom(point,zoom));
		    		 
		    		 if(!isNew){
		    			 marker = map.addMarker(new MarkerOptions()
 		               	.position(point)
 		               	.title(getResources().getString(R.string.relic_details_coordinates))
 		 				.snippet(longlat[0]+", "+longlat[1]));
		    		 }		    		
			     }
		 }
		 map.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng newPoint) {
				if(marker!=null){
					marker.remove();
				}
				if(mode==null){
					mode = startActionMode(new ActionModeSubmitLatLng());
				}
				marker = map.addMarker(new MarkerOptions()
               	.position(newPoint)
               	.title(getResources().getString(R.string.relic_details_coordinates))
 				.snippet(newPoint.latitude+", "+newPoint.longitude));
				Intent intent = new Intent();
				intent.putExtra(LATLNG,new double[] {newPoint.latitude, newPoint.longitude});
				setResult(RESULT_OK, intent);		
				
			}
		 });
		 
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
	    if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
	    	setResult(RESULT_CANCELED, new Intent());
	    }
	    return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.map_get, menu);
		return true;
	}

	private final class ActionModeSubmitLatLng implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        	finish();
        }
    }
}
