package pl.otwartezabytki.android.fragments;

import com.actionbarsherlock.app.SherlockFragment;
import pl.otwartezabytki.android.CheckListActivity;
import pl.otwartezabytki.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class OpinionFragment extends SherlockFragment {
	
	private String opinion;	
	CheckListActivity parentActivity;
	EditText opinion_item_opinion;
	
	public static OpinionFragment newInstance(String opinion) {
		OpinionFragment f = new OpinionFragment();
        Bundle args = new Bundle();
        args.putString("opinion", opinion);
        f.setArguments(args);

        return f;
    }

	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        opinion = getArguments().getString("opinion");
        parentActivity = (CheckListActivity) getActivity();
    }

    
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.opinion_item, container, false);
        
        opinion_item_opinion = (EditText) v.findViewById(R.id.opinion_item_opinion);
        opinion_item_opinion.setText(opinion);
                
        return v;
    }
  
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    
    
    public String getOpinion(){
    	return opinion_item_opinion.getText().toString();
    }
        
	
}

