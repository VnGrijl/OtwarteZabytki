package pl.otwartezabytki.android.database;

import java.util.ArrayList;
import pl.otwartezabytki.android.dataobjects.DataCheckListItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalCheckListHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "checklistOtwarteZabytki";
	private static final String TABLE_CHECKLIST = "check_list";

	private static final String KEY_ID = "_id"; // Local PKEY
	private static final String KEY_RELIC_ID = "relic_id"; // Relics PKEY
	private static final String KEY_QUESTION_ID = "question_id"; 
	private static final String KEY_PARENT_ID = "parent_id"; 
	private static final String KEY_PARENT_ANSWER = "parent_answer";
	private static final String KEY_QUESTION = "question";
	private static final String KEY_POSSIBLE_ANSWERS = "possible_answers";
	private static final String KEY_ANSWER = "answer";
	private static final String KEY_PIC_PATH = "photo_path";
	
	
	
	public LocalCheckListHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
		
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CHECKLIST_TABLE = "CREATE TABLE " + TABLE_CHECKLIST + "("
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_RELIC_ID + " INTEGER," + KEY_QUESTION_ID + " INTEGER,"
					+ KEY_PARENT_ID + " INTEGER," + KEY_PARENT_ANSWER + " TEXT," + KEY_QUESTION + " TEXT,"
					+ KEY_POSSIBLE_ANSWERS + " TEXT," + KEY_ANSWER + " TEXT," + KEY_PIC_PATH + " TEXT" + ")";
		db.execSQL(CREATE_CHECKLIST_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKLIST);
		onCreate(db);
	}
		
			
	public ArrayList<DataCheckListItem> getCheckListData(long relicID){
		ArrayList<DataCheckListItem> checkListData = new ArrayList<DataCheckListItem>();
		Log.e("Otwarte Zabytki", "Czytam dla z fk "+ relicID);
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_CHECKLIST + " WHERE "+KEY_RELIC_ID+"=" + String.valueOf(relicID) + " ORDER BY " +KEY_QUESTION_ID + " ;";
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()) {
			do {
				DataCheckListItem checkListItem = new DataCheckListItem(
						cursor.getLong(0), cursor.getLong(1), cursor.getInt(2), cursor.getInt(3), 
						cursor.getString(4), cursor.getString(5), cursor.getString(6), 
						cursor.getString(7), cursor.getString(8));				
				checkListData.add(checkListItem);
				} while (cursor.moveToNext());
			return checkListData;
		}else{
			return null;
		}
	}
	
	
	public void addCheckListData(ArrayList<DataCheckListItem> checkListData, long relicID) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.e("Otwarte Zabytki", "Zapisuje z fk "+ relicID);
		for(DataCheckListItem checkListItem: checkListData){
			ContentValues values = new ContentValues();
			values.put(KEY_RELIC_ID, relicID);
			values.put(KEY_QUESTION_ID, checkListItem.getQuestionID());
			values.put(KEY_PARENT_ID, checkListItem.getParentID());
			values.put(KEY_PARENT_ANSWER, checkListItem.getParentAnswer());
			values.put(KEY_QUESTION, checkListItem.getQuestion());			
			values.put(KEY_POSSIBLE_ANSWERS, checkListItem.getPossibleAnswers());
			values.put(KEY_ANSWER, checkListItem.getAnswer());
			values.put(KEY_PIC_PATH, checkListItem.getMediaFilePath());
			
			db.insert(TABLE_CHECKLIST, null, values);
		}		
		db.close(); 		
	}
	
	public void updateCheckListData(ArrayList<DataCheckListItem> checkListData) {
		SQLiteDatabase db = this.getWritableDatabase();

		for(DataCheckListItem checkListItem: checkListData){
			ContentValues values = new ContentValues();
			values.put(KEY_RELIC_ID, checkListItem.getRelic_id());
			values.put(KEY_QUESTION_ID, checkListItem.getQuestionID());
			values.put(KEY_PARENT_ID, checkListItem.getParentID());
			values.put(KEY_PARENT_ANSWER, checkListItem.getParentAnswer());
			values.put(KEY_QUESTION, checkListItem.getQuestion());			
			values.put(KEY_POSSIBLE_ANSWERS, checkListItem.getPossibleAnswers());
			values.put(KEY_ANSWER, checkListItem.getAnswer());
			values.put(KEY_PIC_PATH, checkListItem.getMediaFilePath());
			
			db.update(TABLE_CHECKLIST, values, KEY_ID + " = ?",
					new String[] { String.valueOf(checkListItem.getId()) });
		}		
		db.close(); 
	}
	
	public void deleteCheckListData(long relicID) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_CHECKLIST, KEY_RELIC_ID + " = ?",
				new String[] { String.valueOf(relicID) });
		db.close();
	}
}
