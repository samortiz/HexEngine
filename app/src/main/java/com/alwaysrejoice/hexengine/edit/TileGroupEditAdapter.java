package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

public class TileGroupEditAdapter extends BaseAdapter {

  private Context context;
  private List<TileType> tiles;

  public TileGroupEditAdapter(Context context, List<TileType> tiles) {
    this.context = context;
    this.tiles= tiles;
  }

  public int getCount() {
    return tiles.size();
  }

  public TileType getItem(int arg0) {
    return tiles.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    tiles.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.tile_group_edit_row, parent, false);
    TileType tile = this.tiles.get(position);
    ImageView imageView = (ImageView) row.findViewById(R.id.row_img);
    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    imageView.setImageBitmap(tile.getBitmap());
    nameView.setText(tile.getName());
    return (row);
  }
}
