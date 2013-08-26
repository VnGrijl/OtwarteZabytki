package pl.otwartezabytki.android.tools;

import java.util.ArrayList;
import java.util.List;

import pl.otwartezabytki.android.R;
import pl.otwartezabytki.android.dataobjects.DataSearchResult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AdapterSearchResults extends BaseAdapter {

	private LayoutInflater inflater;

	private List<DataSearchResult> items = new ArrayList<DataSearchResult>();

	public AdapterSearchResults(Context context, List<DataSearchResult> items) {
		inflater = LayoutInflater.from(context);
		this.items = items;
	}

	public int getCount() {
		return items.size();
	}

	public DataSearchResult getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		DataSearchResult s = items.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_search_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.search_item_title);
			holder.place_name = (TextView) convertView.findViewById(R.id.search_item_desc);
			holder.img = (ImageView) convertView.findViewById(R.id.search_item_img);
			holder.state = convertView.findViewById(R.id.search_item_state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(s.getTitle().equals("Dom") && !s.getStreet().equals("") ){
			holder.title.setText(s.getTitle()+", "+s.getStreet());
		}else{
			holder.title.setText(s.getTitle());
		}
		holder.state.setBackgroundResource(s.getState());
		if(!s.getPlaceName().equals("") && !s.getVoivodeship_name().equals("")){
			holder.place_name.setText(s.getPlaceName()+", woj. "+s.getVoivodeship_name());
		}else if(!s.getPlaceName().equals("") && s.getVoivodeship_name().equals("")){
			holder.place_name.setText(s.getPlaceName());
		}else if(s.getPlaceName().equals("") && !s.getVoivodeship_name().equals("")){
			holder.place_name.setText("woj. "+s.getVoivodeship_name());
		}else{
			holder.place_name.setText("");
		}
		
		if (s.getImage() != null) {
			holder.img.setImageBitmap(s.getImage());
		} else {
			holder.img.setImageResource(R.drawable.ic_default_list);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView title;
		TextView place_name;
		ImageView img;
		View state;
	}

}
