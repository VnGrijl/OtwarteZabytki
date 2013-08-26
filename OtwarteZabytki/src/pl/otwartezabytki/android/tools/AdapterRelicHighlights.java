package pl.otwartezabytki.android.tools;

import java.util.ArrayList;
import java.util.List;

import pl.otwartezabytki.android.R;
import pl.otwartezabytki.android.dataobjects.DataRelicHighlights;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AdapterRelicHighlights extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;

	private List<DataRelicHighlights> items = new ArrayList<DataRelicHighlights>();

	public AdapterRelicHighlights(Context context, List<DataRelicHighlights> items) {
		inflater = LayoutInflater.from(context);
		this.items = items;
		this.context = context;
	}

	public int getCount() {
		return items.size();
	}

	public DataRelicHighlights getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		DataRelicHighlights s = items.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_highlights_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.highlight_item_title);
			holder.place_name = (TextView) convertView.findViewById(R.id.highlight_item_desc);
			holder.img = (ImageView) convertView.findViewById(R.id.highlight_item_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(s.getIdentification().equals("Dom")){
			holder.title.setText(s.getIdentification()+", "+s.getStreet());
		}else{
			holder.title.setText(s.getIdentification());
		}
		if(!s.getPlace().equals("") && !s.getVoivodeship().equals("")){
			holder.place_name.setText(s.getPlace()+", woj. "+s.getVoivodeship());
		}else if(!s.getPlace().equals("") && s.getVoivodeship().equals("")){
			holder.place_name.setText(s.getPlace());
		}else if(s.getPlace().equals("") && !s.getVoivodeship().equals("")){
			holder.place_name.setText("woj. "+s.getVoivodeship());
		}else{
			holder.place_name.setText("");
		}
		
		
		if (s.getImage() != null) {
			holder.img.setImageBitmap(s.getImage());
		} else {
			holder.img.setImageResource(R.drawable.ic_default_list);
		}
		if (s.getAndroid_id()>0){
			holder.sent.setText("Wys³ano "+ s.getTimestamp());
			holder.sent.setTextColor(context.getResources().getColor(R.color.green_answer));
		}
		return convertView;
	}

	static class ViewHolder {
		TextView title;
		TextView place_name;
		ImageView img;
		TextView sent;
	}

}
