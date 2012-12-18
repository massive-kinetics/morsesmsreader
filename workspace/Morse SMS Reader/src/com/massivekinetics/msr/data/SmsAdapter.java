package com.massivekinetics.msr.data;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.massivekinetics.msr.R;

public class SmsAdapter extends BaseAdapter {
	List<SmsEntity> items;
	SimpleDateFormat formatter = new SimpleDateFormat(
			"dd MMM yyyy hh:mm:ss zzz");
	DateFormat aDateFormat, aTimeFormat;

	public SmsAdapter(Context context, List<SmsEntity> items) {
		aDateFormat = android.text.format.DateFormat.getDateFormat(context);
		aTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
		
		
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.sms_item, null);
		updateView(view, position);
		return view;
	}

	private void updateView(View view, int position) {
		SmsEntity sms = (SmsEntity) getItem(position);
		TextView fromAndTime = (TextView) view.findViewById(R.id.fromAndTime);
		TextView body = (TextView) view.findViewById(R.id.body);
		Date dateTime = new Date(sms.timeStamp);
		String dateTimeStr = aDateFormat.format(dateTime) + " : " + aTimeFormat.format(dateTime);
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(dateTime);

		fromAndTime.setText("From " + sms.from + " at " + currentDateTimeString);
		body.setText(sms.body);
	}

}
