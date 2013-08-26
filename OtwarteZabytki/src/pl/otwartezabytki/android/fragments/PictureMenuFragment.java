package pl.otwartezabytki.android.fragments;

import pl.otwartezabytki.android.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class PictureMenuFragment extends SherlockDialogFragment {

	SingleQuestionFragment parentFragment;
	
    public static PictureMenuFragment newInstance() {
    	PictureMenuFragment frag = new PictureMenuFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
    	parentFragment = (SingleQuestionFragment) getTargetFragment();      
        CharSequence[] items = new String[2];
        items[0] = getResources().getString(R.string.action_camera);
        items[1] = getResources().getString(R.string.action_gallery);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setItems(items, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int item) {
        		parentFragment.onPictureMenuResult(item);     			   
            }
        });
        return builder.create();
    }
}