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
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

public class BgListAdapter extends BaseAdapter {

  private Context context;
  private List<String> names;

  public BgListAdapter(Context context, List<String> names) {
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
    View row = inflater.inflate(R.layout.bg_list_row, parent, false);

    String tileName = this.names.get(position);
    BgTile tile = GameUtils.getGame().getBgTiles().get(tileName);
    ImageView imageView = (ImageView) row.findViewById(R.id.row_img);
    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    imageView.setImageBitmap(tile.getBitmap());

    nameView.setText(tile.getName()+" ("+tile.getType()+")");
    deleteImg.setTag(position);
    return (row);
  }
}
