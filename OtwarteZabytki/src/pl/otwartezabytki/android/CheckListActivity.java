package pl.otwartezabytki.android;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pl.otwartezabytki.android.dataobjects.DataCheckListItem;
import pl.otwartezabytki.android.fragments.OpinionFragment;
import pl.otwartezabytki.android.fragments.SingleQuestionFragment;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CheckListActivity extends SherlockFragmentActivity {

	private static int checkListFile = R.raw.checklist;
	public final static String EXTRA_CHECKLISTRESULT = "pl.otwartezabytki.android.CHECKLISTRESULT";
	public final static String EXTRA_OPINION = "pl.otwartezabytki.android.CHECKLISTOPINION";
	private static String tagPart = "FRAGMENT_";
	private static String tagOpinion = "OPINION";
	ViewGroup viewGroup;
	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;
	OpinionFragment opinionFragment;
	
	ArrayList<ArrayList<String>> staticQA;
	ArrayList<DataCheckListItem> checkListData;
	String opinion;
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_list);
		setCustomActionBar();
		opinion = getIntent().getStringExtra(RelicDetailsActivity.EXTRA_OPINION);
		checkListData = getIntent().getParcelableArrayListExtra(RelicDetailsActivity.EXTRA_CHECKLISTDATA);
		if(checkListData == null){
			staticQA = getStaticConfiguration(checkListFile);
			checkListData = new ArrayList<DataCheckListItem>();
			for(int i=0;i<staticQA.size();i++){
				ArrayList<String> singleLineQA = staticQA.get(i);
				String answer = "", mediaFilePath = "";
				checkListData.add(new DataCheckListItem(
						Long.valueOf(0),								// local db id
						0, 												// relicID
						Integer.parseInt(singleLineQA.get(0)), 
						Integer.parseInt(singleLineQA.get(1)),
						singleLineQA.get(2), 
						singleLineQA.get(3), 
						singleLineQA.get(4),
						answer,											
						mediaFilePath));										
			}
		}
		
		viewGroup = (ViewGroup)findViewById(R.id.vg);
        fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		for(DataCheckListItem checkListItem: checkListData){
			SingleQuestionFragment fragment = SingleQuestionFragment.newInstance(
					checkListItem.getQuestionID(),
					checkListItem.getQuestion(),
					checkListItem.getAnswer(),
					checkListItem.getMediaFilePath(),
					checkListItem.getPossibleAnswers());
			fragmentTransaction.add(R.id.vg, fragment, tagPart+checkListItem.getQuestionID());		
			if(checkListItem.getParentID() > 0){
				String tmpParentAnswer = checkListData.get(checkListItem.getParentID()).getAnswer();
				if(!checkListItem.getParentAnswer().equals(tmpParentAnswer) || tmpParentAnswer == null){
					fragmentTransaction.hide(fragment);
				}
				
			}
		}
		// Add general opinion fragment
		opinionFragment = OpinionFragment.newInstance(opinion);
		fragmentTransaction.add(R.id.vg, opinionFragment, tagOpinion);
		fragmentTransaction.commit();
		
	}
	
	private void setCustomActionBar(){
		final LayoutInflater inflater = (LayoutInflater) getSupportActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_done_cancel, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	Intent result = new Intent();
                    	result.putExtra(CheckListActivity.EXTRA_OPINION, opinionFragment.getOpinion());
                		result.putExtra(CheckListActivity.EXTRA_CHECKLISTRESULT, checkListData);
                		setResult(RESULT_OK, result);
                        finish();
                    }
                });
        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	setResult(RESULT_CANCELED,new Intent());
                        finish();
                    }
                });
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
	}

	public void setAnswerForQuestion(int checkListItemID, String answer){
		((SingleQuestionFragment)fragmentManager.findFragmentByTag(tagPart+checkListItemID)).setAnswer(answer);
		checkListData.get(checkListItemID).setAnswer(answer);
		fragmentTransaction = fragmentManager.beginTransaction();
		for(DataCheckListItem checkListItem: checkListData){			
			if(checkListItem.getParentID() > 0){
				if(!checkListItem.getParentAnswer().equals(checkListData.get(checkListItem.getParentID()).getAnswer())){
					fragmentTransaction.hide(fragmentManager.findFragmentByTag(tagPart+checkListItem.getQuestionID()));
				}else{
					fragmentTransaction.show(fragmentManager.findFragmentByTag(tagPart+checkListItem.getQuestionID()));
				}
				
			}
		}
		fragmentTransaction.commit();				
	}
	
	public void setPictureForQuestion(int checkListItemID, String mediaFilePath){
		checkListData.get(checkListItemID).setMediaFilePath(mediaFilePath);	
	}	
		
	private ArrayList<ArrayList<String>> getStaticConfiguration(int fileresource){
		InputStream ins = getResources().openRawResource(fileresource);
		InputStreamReader isr = new InputStreamReader(ins);
		BufferedReader br = new BufferedReader(isr);
		
		ArrayList<ArrayList<String>> map = new ArrayList<ArrayList<String>>();
        
		try{
			String read = br.readLine();

			while(read != null) {
				ArrayList<String> qa = new ArrayList<String>();
				String[] temp;
				String delimiter = ";";
				temp = read.split(delimiter);
				for(int i=0; i < temp.length; i++){
					qa.add(temp[i]);
				}
				map.add(qa);
			    read = br.readLine();
			}
			
			ins.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return map;
	}
	
	
}
