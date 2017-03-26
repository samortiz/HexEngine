package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

public class UnitListAdapter extends BaseAdapter {

  private Context context;
  private List<UnitTile> units;

  public UnitListAdapter(Context context, List<UnitTile> units) {
    this.context = context;
    this.units = units;
  }

  public int getCount() {
    return units.size();
  }

  public UnitTile getItem(int arg0) {
    return units.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    units.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.unit_list_row, parent, false);
    UnitTile unit = this.units.get(position);
    UnitTile tile = GameUtils.getGame().getUnitTiles().get(unit.getName());

    ImageView imageView = (ImageView) row.findViewById(R.id.row_img);
    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);

    imageView.setImageBitmap(tile.getBitmap());
    nameView.setText(tile.getName());
    deleteImg.setTag(position);
    return (row);
  }
}
