package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;

import java.util.List;

public class BgListAdapter extends BaseAdapter {

  private Context context;
  private List<String> name;

  public BgListAdapter(Context context, List<String> name) {
    this.context = context;
    this.name = name;
  }

  public int getCount() {
    return name.size();
  }

  public String getItem(int arg0) {
    return name.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    name.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.bg_list_row, parent, false);
    TextView name = (TextView) row.findViewById(R.id.row_name);
    name.setText(this.name.get(position));

    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);

    Log.d("choose", "name="+name.getText());
    return (row);
  }
}
