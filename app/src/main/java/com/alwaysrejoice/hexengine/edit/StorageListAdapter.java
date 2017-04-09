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
import java.util.Map;

public class StorageListAdapter extends BaseAdapter {

  private Context context;
  private List<String> keys;
  private Map<String, String> storage;

  public StorageListAdapter(Context context, List<String> keys, Map<String, String> storage) {
    this.context = context;
    this.keys = keys;
    this.storage = storage;
  }

  public int getCount() {
    return keys.size();
  }

  public String getItem(int position) {
    return keys.get(position);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    keys.remove(position);
  }

  public void addItem(String key, String value) {
    keys.add(key);
    storage.put(key, value);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.storage_list_row, parent, false);
    String key = keys.get(position);
    String value = storage.get(key);

    TextView storageText = (TextView) row.findViewById(R.id.storage);
    storageText.setText(key+" = "+value);

    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);
    return (row);
  }
}
