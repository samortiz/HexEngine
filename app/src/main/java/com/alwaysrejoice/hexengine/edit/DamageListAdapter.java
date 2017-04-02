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

public class DamageListAdapter extends BaseAdapter {

  private Context context;
  private List<String> damageTypes;

  public DamageListAdapter(Context context, List<String> damageTypes) {
    this.context = context;
    this.damageTypes = damageTypes;
  }

  public int getCount() {
    return damageTypes.size();
  }

  public String getItem(int arg0) {
    return damageTypes.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    damageTypes.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.damage_list_row, parent, false);
    String damageType = this.damageTypes.get(position);
    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    nameView.setText(damageType);
    deleteImg.setTag(position);
    return (row);
  }
}
