package pl.otwartezabytki.android.dataobjects;

import pl.otwartezabytki.android.R;
import pl.otwartezabytki.android.tools.AdapterSearchResults;
import pl.otwartezabytki.android.tools.ImageDownloader;
import pl.otwartezabytki.android.tools.ResolutionDependent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;

public class DataSearchResult {

	private int id;
	private String title;
	private String place_name;
	private String state;
	private String image_url;
	private String voivodeship_name;
	private String street;
	private Bitmap image;

	private AdapterSearchResults adapter;
	private int density;

	public DataSearchResult(int id, String title, String place_name, String state, String street, String voivodeship_name, String image_url) {
		this.id = id;
        this.title = title;
        this.place_name = place_name;
        this.state = state;
        this.street = street;
        this.voivodeship_name = voivodeship_name;
        this.image_url = image_url;
        this.image = null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPlaceName() {
		return place_name;
	}

	public String getImgUrl() {
		return image_url;
	}
	
	public String getVoivodeship_name() {
		return voivodeship_name;
	}

	public String getStreet() {
		return street;
	}
	
	public int getState() {
		if(state.equals("unchecked")){
			return R.drawable.draw_state_3_rectangle;
		}else if(state.equals("checked")){
			return R.drawable.draw_state_2_rectangle;
		}else{
			return R.drawable.draw_state_1_rectangle;
		}
	}

	public void setImgUrl(String image_url) {
		this.image_url = image_url;
	}

    public Bitmap getImage() {
		return image;
	}

	public int getDensity() {
		return density;
	}

	public void setDensity(int density) {
		this.density = density;
	}

	public void setImage(Bitmap image) {
		int size = ResolutionDependent.getThumbnailDimensions(getDensity());
		Bitmap resizedImage = ThumbnailUtils.extractThumbnail(image, size, size);
		this.image = resizedImage;
	}

	public AdapterSearchResults getAdapter() {
		return adapter;
	}

	public void setAdapter(AdapterSearchResults adapter) {
		this.adapter = adapter;
	}

	public void loadImage(AdapterSearchResults adapter, int densityDpi) {
		this.adapter = adapter;
		setDensity(densityDpi);
		if (image_url != null && !image_url.equals("")) {
			new ImageLoadTask().execute(image_url);
		}
	}

	private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {

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

		protected void onPostExecute(Bitmap ret) {
			if (ret != null) {
				Log.i("Otwarte Zabytki", "Successfully loaded " + title + " image");
				setImage(ret);
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			} else {
				Log.e("Otwarte Zabytki", "Failed to load " + title + " image");
			}
		}
	}

}
