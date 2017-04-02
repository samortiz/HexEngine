package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;

import java.util.List;

public class ListStringAdapter extends BaseAdapter {

  private Context context;
  private List<String> types;

  public ListStringAdapter(Context context, List<String> types) {
    this.context = context;
    this.types = types;
  }

  public int getCount() {
    return types.size();
  }

  public String getItem(int arg0) {
    return types.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    types.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.list_string_row, parent, false);
    String type = this.types.get(position);
    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    nameView.setText(type);
    deleteImg.setTag(position);
    return (row);
  }
}
