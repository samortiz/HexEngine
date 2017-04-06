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

import java.util.List;

public class BgListAdapter extends BaseAdapter {

  private Context context;
  private List<BgTile> tiles;

  public BgListAdapter(Context context, List<BgTile> tiles) {
    this.context = context;
    this.tiles = tiles;
  }

  public int getCount() {
    return tiles.size();
  }

  public BgTile getItem(int arg0) {
    return tiles.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    tiles.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    BgTile tile= this.tiles.get(position);

    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.bg_list_row, parent, false);

    ImageView imageView = (ImageView) row.findViewById(R.id.row_img);
    imageView.setImageBitmap(tile.getBitmap());

    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    nameView.setText(tile.getName()+" ("+tile.getType()+")");

    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);
    return (row);
  }
}
