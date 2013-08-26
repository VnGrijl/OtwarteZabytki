package pl.otwartezabytki.android.tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class MediaUriHelper {

	public static String getPathFromUri(Context context, Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null,
                null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
	
	public static String getOutputMediaFilePath(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        		Environment.DIRECTORY_PICTURES), "OtwarteZabytki");
        
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.e("Otwarte Zabytki", "Failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg";
    }
}
