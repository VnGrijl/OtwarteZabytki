package pl.otwartezabytki.android.database;

import java.util.ArrayList;
import java.util.List;

import pl.otwartezabytki.android.dataobjects.DataRelicDetails;
import pl.otwartezabytki.android.dataobjects.DataRelicHighlights;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalRelicDetailsHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "localOtwarteZabytki";
	private static final String TABLE_RELICS = "relic_details";

	private static final String KEY_ID = "_id"; // Local PKEY
	private static final String KEY_RELIC_ID = "relic_id"; // Relics PKEY
	private static final String KEY_ANDROID_ID = "android_id"; // Remote PKEY
	private static final String KEY_NAME = "identification";
	private static final String KEY_PIC_URL = "photo_url";
	private static final String KEY_PIC_PATH = "photo_path";
	private static final String KEY_DESC = "description";
	private static final String KEY_STATE = "state";
	private static final String KEY_DATING = "dating";
	private static final String KEY_LNG = "longitude";
	private static final String KEY_LAT = "latitude";
	private static final String KEY_STREET = "street";
	private static final String KEY_PLACE = "place";
	private static final String KEY_COMMUNE = "commune";
	private static final String KEY_DISTRICT = "district";
	private static final String KEY_VOIVOD = "voivodship";
	private static final String KEY_OPINION = "opinion";
	private static final String KEY_TIMESTAMP = "timestamp";

	public LocalRelicDetailsHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
		
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_RELICS_TABLE = "CREATE TABLE " + TABLE_RELICS + "("
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_RELIC_ID + " INTEGER," + KEY_ANDROID_ID + " INTEGER,"
					+ KEY_NAME + " TEXT," + KEY_PIC_URL + " TEXT," + KEY_PIC_PATH + " TEXT,"
					+ KEY_DESC + " TEXT," + KEY_STATE + " TEXT," + KEY_DATING + " TEXT,"
					+ KEY_STREET + " TEXT," + KEY_PLACE + " TEXT," + KEY_COMMUNE + " TEXT," + KEY_DISTRICT + " TEXT," + KEY_VOIVOD + " TEXT,"
					+ KEY_LNG + " TEXT," + KEY_LAT + " TEXT," + KEY_OPINION + " TEXT," + KEY_TIMESTAMP + " TEXT" + ")";
		db.execSQL(CREATE_RELICS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RELICS);
		onCreate(db);
	}
	
	public List<DataRelicHighlights> getRelics() {
		List<DataRelicHighlights> relicList = new ArrayList<DataRelicHighlights>();
		String selectQuery = "SELECT  * FROM " + TABLE_RELICS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				DataRelicHighlights relic = new DataRelicHighlights(
						cursor.getLong(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(4),
						cursor.getString(5), cursor.getString(3), cursor.getString(9),
						cursor.getString(10), cursor.getString(13), cursor.getString(17));				
				relicList.add(relic);
				} while (cursor.moveToNext());
		}		
		return relicList;
	}
	
	public int getRelicCount() {
		String selectQuery = "SELECT count(*) FROM " + TABLE_RELICS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		cursor.moveToFirst();
		return cursor.getInt(0);
	}
	
	public DataRelicDetails getRelicDetailsByRelicId(int relic_id){
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_RELICS + " WHERE "+KEY_RELIC_ID+"=" + String.valueOf(relic_id)+";";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();

			DataRelicDetails relic = new DataRelicDetails(
					cursor.getLong(0), cursor.getInt(1), cursor.getInt(2),
					cursor.getString(4), cursor.getString(5), cursor.getString(3),
					cursor.getString(6), cursor.getString(7), cursor.getString(8),
					cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), 
					cursor.getString(14), cursor.getString(15), cursor.getString(16)
					);
			return relic;
		}else{
			return null;
		}
	}
	
	public DataRelicDetails getRelicDetailsById(long id){
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_RELICS + " WHERE "+KEY_ID+"=" + String.valueOf(id)+";";
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor != null && cursor.getCount()!=0){
			cursor.moveToFirst();

			DataRelicDetails relic = new DataRelicDetails(
					cursor.getLong(0), cursor.getInt(1), cursor.getInt(2),
					cursor.getString(4), cursor.getString(5), cursor.getString(3),
					cursor.getString(6), cursor.getString(7), cursor.getString(8),
					cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), 
					cursor.getString(14), cursor.getString(15), cursor.getString(16)
					);
			return relic;
		}else{
			return null;
		}
	}
	
	public long addRelicDetail(DataRelicDetails relic) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_RELIC_ID, relic.getRelic_id());
		values.put(KEY_ANDROID_ID, relic.getAndroid_id());
		values.put(KEY_PIC_URL, relic.getMainPhotoURL());
		values.put(KEY_PIC_PATH, relic.getMainPhotoPath());
		values.put(KEY_DESC, relic.getDescription());
		values.put(KEY_NAME, relic.getIdentification());
		values.put(KEY_STATE, relic.getState()); 
		values.put(KEY_DATING,relic.getDating());
		values.put(KEY_STREET, relic.getStreet());
		values.put(KEY_PLACE, relic.getPlace());
		values.put(KEY_COMMUNE, relic.getCommune());
		values.put(KEY_DISTRICT, relic.getDistrict());
		values.put(KEY_VOIVOD, relic.getVoivodeship());
		values.put(KEY_LNG, relic.getLongitude());
		values.put(KEY_LAT, relic.getLatitude());
		values.put(KEY_OPINION, relic.getOpinion());
		
		long insertId = db.insert(TABLE_RELICS, null, values);
		db.close(); 
		return insertId;
	}
	
	public int updateRelicDetail(DataRelicDetails relic) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_RELIC_ID, relic.getRelic_id());
		values.put(KEY_ANDROID_ID, relic.getAndroid_id());
		values.put(KEY_PIC_URL, relic.getMainPhotoURL());
		values.put(KEY_PIC_PATH, relic.getMainPhotoPath());
		values.put(KEY_DESC, relic.getDescription());
		values.put(KEY_NAME, relic.getIdentification());
		values.put(KEY_STATE, relic.getState()); 
		values.put(KEY_DATING,relic.getDating());
		values.put(KEY_STREET, relic.getStreet());
		values.put(KEY_PLACE, relic.getPlace());
		values.put(KEY_COMMUNE, relic.getCommune());
		values.put(KEY_DISTRICT, relic.getDistrict());
		values.put(KEY_VOIVOD, relic.getVoivodeship());
		values.put(KEY_LNG, relic.getLongitude());
		values.put(KEY_LAT, relic.getLatitude());
		values.put(KEY_OPINION, relic.getOpinion());

		// updating row
		return db.update(TABLE_RELICS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(relic.getId()) });
	}
	
	public void deleteRelicDetail(DataRelicDetails relic) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RELICS, KEY_ID + " = ?",
				new String[] { String.valueOf(relic.getId()) });
		db.close();
	}
}
