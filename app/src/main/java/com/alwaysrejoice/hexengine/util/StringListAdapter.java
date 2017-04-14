package com.alwaysrejoice.hexengine.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;

import java.util.List;

public class StringListAdapter extends BaseAdapter {

  private Context context;
  private List<String> strings;

  public StringListAdapter(Context context, List<String> strings) {
    this.context = context;
    this.strings = strings;
  }

  public int getCount() {
    return strings.size();
  }

  public String getItem(int arg0) {
    return strings.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    strings.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.string_list_row, parent, false);
    String str = strings.get(position);
    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    nameView.setText(str);
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);
    return (row);
  }
}
