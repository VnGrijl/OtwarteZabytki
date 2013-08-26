package pl.otwartezabytki.android.dataobjects;

import pl.otwartezabytki.android.tools.AdapterRelicHighlights;
import pl.otwartezabytki.android.tools.ImageDownloader;
import pl.otwartezabytki.android.tools.ResolutionDependent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;

public class DataRelicHighlights {

	long id;
	int relic_id;
	int android_id;
	private String mainPhotoURL="";
	private String mainPhotoPath="";
	private String identification="";
	private String street="";
	private String place="";
	private String voivodeship="";
	private String timestamp="";
	private Bitmap image;

	private AdapterRelicHighlights adapter;
	private int density;

	public DataRelicHighlights(long id, int relic_id, int android_id, String mainPhotoURL,
			String mainPhotoPath, String identification, String street, String place,
			String voivodeship, String timestamp ) {
		this.id = id;
		this.relic_id = relic_id;
		this.android_id = android_id;
		this.mainPhotoURL = mainPhotoURL;
		this.mainPhotoPath = mainPhotoPath;
		this.identification = identification;
		this.street = street;
		this.place = place;
		this.voivodeship = voivodeship;
		this.timestamp = timestamp;
	}

	

	public int getAndroid_id() {
		return android_id;
	}



	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public int getRelic_id() {
		return relic_id;
	}



	public void setRelic_id(int relic_id) {
		this.relic_id = relic_id;
	}



	public String getMainPhotoURL() {
		return mainPhotoURL;
	}



	public void setMainPhotoURL(String mainPhotoURL) {
		this.mainPhotoURL = mainPhotoURL;
	}



	public String getMainPhotoPath() {
		return mainPhotoPath;
	}



	public void setMainPhotoPath(String mainPhotoPath) {
		this.mainPhotoPath = mainPhotoPath;
	}



	public String getIdentification() {
		return identification;
	}



	public void setIdentification(String identification) {
		this.identification = identification;
	}



	public String getStreet() {
		return street;
	}



	public void setStreet(String street) {
		this.street = street;
	}



	public String getVoivodeship() {
		return voivodeship;
	}



	public void setVoivodeship(String voivodeship) {
		this.voivodeship = voivodeship;
	}



	public String getPlace() {
		return place;
	}



	public void setPlace(String place) {
		this.place = place;
	}



	public Bitmap getImage() {
		return image;
	}



	public void setImage(Bitmap image) {
		int size = ResolutionDependent.getThumbnailDimensions(getDensity());
		Bitmap resizedImage = ThumbnailUtils.extractThumbnail(image, size, size);
		this.image = resizedImage;
	}



	public String getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}



	public int getDensity() {
		return density;
	}



	public void setDensity(int density) {
		this.density = density;
	}



	public AdapterRelicHighlights getAdapter() {
		return adapter;
	}

	public void setAdapter(AdapterRelicHighlights adapter) {
		this.adapter = adapter;
	}

	public void loadImage(AdapterRelicHighlights adapter, int densityDpi) {
		this.adapter = adapter;
		setDensity(densityDpi);
		if (!mainPhotoPath.equals("")){
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	    	bitmapOptions.inSampleSize=8;
			Bitmap ThumbImage = BitmapFactory.decodeFile(mainPhotoPath,bitmapOptions);
    		setImage(ThumbImage);
		}
		else if (mainPhotoURL != null && !mainPhotoURL.equals("")) {
			new ImageLoadTask().execute(mainPhotoURL);
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
				Log.i("Otwarte Zabytki", "Successfully loaded " + identification + " image");
				setImage(ret);
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			} else {
				Log.e("Otwarte Zabytki", "Failed to load " + identification + " image");
			}
		}
	}

	

}
