package pl.otwartezabytki.android.fragments;

import pl.otwartezabytki.android.CheckListActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;

public class AnswerListFragment extends SherlockDialogFragment {

	private int ID;
	private String question;
	private String[] possibleAnswers;
	CheckListActivity parentActivity;
	
    public static AnswerListFragment newInstance(int ID, String question, String possibleAnswers) {
    	AnswerListFragment frag = new AnswerListFragment();
        Bundle args = new Bundle();
        args.putInt("ID", ID);
        args.putString("question", question);
        args.putStringArray("possibleAnswers", possibleAnswers.split("\\^"));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        parentActivity = (CheckListActivity) getActivity();       
        ID = getArguments().getInt("ID");
        question = getArguments().getString("question");
        possibleAnswers = getArguments().getStringArray("possibleAnswers");
        
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(question);
        builder.setItems(possibleAnswers, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int item) {
            	parentActivity.setAnswerForQuestion(ID,possibleAnswers[item]);       			   
            }
        });
        
         
        return builder.create();
    }
}