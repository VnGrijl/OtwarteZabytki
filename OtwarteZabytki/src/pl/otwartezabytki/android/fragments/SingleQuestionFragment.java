package pl.otwartezabytki.android.fragments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.actionbarsherlock.app.SherlockFragment;
import pl.otwartezabytki.android.CheckListActivity;
import pl.otwartezabytki.android.R;
import pl.otwartezabytki.android.tools.ResolutionDependent;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class SingleQuestionFragment extends SherlockFragment {
	
	private static int CAMERA_OFFSET = 500;
	private static int GALLERY_OFFSET = 400;
	private static int QUESTION_OFFSET = 100;
	// Configuration parameters
	//private int index;
	//private ArrayList<String> qa;
	
	// Layout elements
	private ImageView checklist_item_img;
	//private ImageButton checklist_button_camera;
	//private ImageButton checklist_button_gallery;
	private TextView checklist_item_question;
	private TextView checklist_item_answer;
	private RelativeLayout checklist_item_up;
	
	// Response contents
	private int ID;
	private String question;
	private String answer;
	private String possibleAnswers;
	private String mediaFilePath="";
	private Uri mediaFileUri;
	
	CheckListActivity parentActivity;
	BitmapFactory.Options bitmapOptions;
	int imageSize;
	
	public static SingleQuestionFragment newInstance(int ID, String question, String answer, String mediaFilePath, String possibleAnswers) {
		SingleQuestionFragment f = new SingleQuestionFragment();

        Bundle args = new Bundle();
        args.putInt("ID", ID);
        args.putString("question", question);
        args.putString("answer", answer);
        args.putString("mediaFilePath", mediaFilePath);
        args.putString("possibleAnswers", possibleAnswers);
        f.setArguments(args);

        return f;
    }

	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ID = getArguments() != null ? getArguments().getInt("ID") : 1;
        question = getArguments().getString("question");
        answer = getArguments().getString("answer");
        mediaFilePath = getArguments().getString("mediaFilePath");
        possibleAnswers = getArguments().getString("possibleAnswers");
        
        parentActivity = (CheckListActivity) getActivity();
    }

    
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.checklist_item, container, false);
        
        
        checklist_item_img = (ImageView) v.findViewById(R.id.checklist_item_img);
        checklist_item_img.setId(ID+GALLERY_OFFSET);
        checklist_item_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("Otwarte Zabytki", "Picture picker id: " + v.getId());
				DialogFragment newFragment = PictureMenuFragment.newInstance();
				newFragment.setTargetFragment(SingleQuestionFragment.this, GALLERY_OFFSET );
		        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");				
			}
		});     
        if(!mediaFilePath.equals("")){
        	setPicture(ID);
        }
        
        checklist_item_question = (TextView) v.findViewById(R.id.checklist_item_question);
        checklist_item_question.setText(question);
        
        checklist_item_answer = (TextView) v.findViewById(R.id.checklist_item_answer);
        if(!answer.equals("")){
        	setAnswer(answer);
        }
        
        checklist_item_up = (RelativeLayout) v.findViewById(R.id.checklist_item_upleft);
        checklist_item_up.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
				Log.d("Otwarte Zabytki", "Question picked id: " + v.getId());
				DialogFragment newFragment = AnswerListFragment.newInstance(ID, question, possibleAnswers);
				newFragment.setTargetFragment(SingleQuestionFragment.this, QUESTION_OFFSET );
		        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
				
			}
		});
        imageSize = ResolutionDependent.getThumbnailDimensions(getResources().getDisplayMetrics().densityDpi);
    	bitmapOptions = new BitmapFactory.Options();
    	bitmapOptions.inSampleSize=8;
        return v;
    }
    
    public void onPictureMenuResult(int item){
    	switch(item){
        case 0:
        	startCameraActivity();
        	return;
        case 1:
        	startGalleryActivity();
        	return;
        default:
            return;
		}
    }
    
    public void startGalleryActivity(){
    	Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    	startActivityForResult(gallery, ID+GALLERY_OFFSET );
    }
    
    public void startCameraActivity(){
    	mediaFileUri = Uri.fromFile(new File(mediaFilePath = getOutputMediaFilePath())); 				    
    	Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	camera.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
        startActivityForResult(camera, ID+CAMERA_OFFSET );
    }
    
    public void setAnswer(String answer){
		checklist_item_answer.setText(answer);
		checklist_item_answer.setTextColor(getResources().getColor(R.color.green_answer));
	}
    
    public void setPicture(int questionID){
    	Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mediaFilePath, bitmapOptions), imageSize, imageSize);
    	checklist_item_img.setImageBitmap(ThumbImage);
    	parentActivity.setPictureForQuestion(questionID, mediaFilePath);
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode >= CAMERA_OFFSET) {
	        if (resultCode == Activity.RESULT_OK) {
	        	Log.d("Otwarte Zabytki", "Camera Result");
	        	SingleScan singleScan = new SingleScan(mediaFilePath, "image/jpeg");
	        	MediaScannerConnection connection = new MediaScannerConnection(getActivity(), singleScan);
	        	singleScan.connection = connection;
	        	connection.connect(); 
	        	setPicture(requestCode-CAMERA_OFFSET);
	        	return;
	        }
    	}    	
    	if (requestCode >= GALLERY_OFFSET){
	    	if (resultCode == Activity.RESULT_OK) {
	    		Log.d("Otwarte Zabytki", "Gallery Result");
	    		Uri uri = data.getData();
	    		mediaFilePath = getPathFromUri(uri);
	    		setPicture(requestCode-GALLERY_OFFSET);
	        	return;
	    	}
	    }    	   	
	}
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    
    private String getPathFromUri(Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null,
                null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
    
    private String getOutputMediaFilePath(){

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
        
	private class SingleScan implements MediaScannerConnectionClient {
	    private String path;
	    private String mimeType;
	    MediaScannerConnection connection;

	    public SingleScan(String path, String mimeType) {
	        this.path = path;
	        this.mimeType = mimeType; 
	    }

	    @Override
	    public void onMediaScannerConnected() {
	        connection.scanFile(path, mimeType);
	    }

	    @Override
	    public void onScanCompleted(String path, Uri uri) {
	        connection.disconnect();
	        Log.i("Otwarte Zabytki", "Scan for single file completed");
  
	    }
	}
}

