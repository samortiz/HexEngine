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

public class ChooseFileListAdapter extends BaseAdapter {

  private Context context;
  private List<String>  names;

  public ChooseFileListAdapter(Context context, List<String> names) {
    this.context = context;
    this.names = names;
  }

  public int getCount() {
    return names.size();
  }

  public String getItem(int arg0) {
    return names.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    names.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.edit_list_row, parent, false);
    TextView name = (TextView) row.findViewById(R.id.edit_list_row_name);
    name.setText(names.get(position));

    ImageView editImg = (ImageView) row.findViewById(R.id.edit_list_row_edit);
    editImg.setTag(position);

    ImageView deleteImg = (ImageView) row.findViewById(R.id.edit_list_row_delete);
    deleteImg.setTag(position);

    Log.d("choose", "name="+name.getText());
    return (row);
  }
}
