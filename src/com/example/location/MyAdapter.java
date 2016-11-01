package com.example.location;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	Context context;
	List<City> city;
	public MyAdapter(Context context, List<City> city) {
		// TODO Auto-generated constructor stub
		this.city = city;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return city.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return city.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView textView;
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
			textView = (TextView) convertView.findViewById(android.R.id.text1);
			textView.setTag(textView);
		}else{
			textView = (TextView) convertView.getTag();
		}
		textView.setText(city.get(position).name);
		return convertView;
	}
}
