package pl.otwartezabytki.android.fragments;

import pl.otwartezabytki.android.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockDialogFragment;

public class NoConnectionDialogFragment extends SherlockDialogFragment {

	
    public static NoConnectionDialogFragment newInstance() {
    	NoConnectionDialogFragment frag = new NoConnectionDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {       
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle(question);
        builder.setMessage(R.string.search_no_connection);
        builder.setNeutralButton(R.string.action_ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
				
			}
		});        
        return builder.create();
    }
}