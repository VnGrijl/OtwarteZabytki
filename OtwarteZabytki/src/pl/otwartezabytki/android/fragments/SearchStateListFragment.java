package pl.otwartezabytki.android.fragments;

import pl.otwartezabytki.android.R;
import pl.otwartezabytki.android.SearchActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class SearchStateListFragment extends SherlockDialogFragment {

	boolean[] checkedItems;
	SearchActivity parentActivity;
	
    public static SearchStateListFragment newInstance(boolean[] states) {
    	SearchStateListFragment frag = new SearchStateListFragment();
        Bundle args = new Bundle();
        args.putBooleanArray("itemsValues", states);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
    	parentActivity = (SearchActivity) getActivity();       
        checkedItems = getArguments().getBooleanArray("itemsValues");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getResources().getString(R.string.search_filter_state_fragment_title));
        builder.setMultiChoiceItems(R.array.relic_states, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				checkedItems[which]=isChecked;
				
			}
		});
                 
        builder.setNegativeButton(R.string.action_cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
        
        builder.setPositiveButton(R.string.action_ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				parentActivity.setStateItemsValues(checkedItems);
				
			}
		});
        return builder.create();
    }
}