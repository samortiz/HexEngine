package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.AbilityTile;

import java.util.List;

public class AbilityListAdapter extends BaseAdapter {

  private Context context;
  private List<AbilityTile> abilities;

  public AbilityListAdapter(Context context, List<AbilityTile> abilities) {
    this.context = context;
    this.abilities = abilities;
  }

  public int getCount() {
    return abilities.size();
  }

  public AbilityTile getItem(int arg0) {
    return abilities.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    abilities.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.ability_list_row, parent, false);
    AbilityTile abilityTile = this.abilities.get(position);

    ImageView imageView = (ImageView) row.findViewById(R.id.row_img);
    imageView.setImageBitmap(abilityTile.getBitmap());

    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    nameView.setText(abilityTile.getName());

    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);
    return (row);
  }
}
