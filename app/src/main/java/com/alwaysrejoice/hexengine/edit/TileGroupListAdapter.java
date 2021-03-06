package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.TileGroup;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.TileTypeLink;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

public class TileGroupListAdapter extends BaseAdapter {

  private Context context;
  private List<TileGroup> tileGroups;

  public TileGroupListAdapter(Context context, List<TileGroup> tileGroups) {
    this.context = context;
    this.tileGroups= tileGroups;
  }

  public int getCount() {
    return tileGroups.size();
  }

  public TileGroup getItem(int arg0) {
    return tileGroups.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    tileGroups.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.tile_group_list_row, parent, false);
    TileGroup tileGroup = this.tileGroups.get(position);
    ImageView imageView = (ImageView) row.findViewById(R.id.row_img);
    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    imageView.setImageBitmap(getFirstImg(tileGroup));
    nameView.setText(tileGroup.getName());
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);
    return (row);
  }

  public Bitmap getFirstImg(TileGroup tileGroup) {
    if ((tileGroup != null) &&
        (tileGroup.getTileLinks() != null) &&
        (tileGroup.getTileLinks().size() > 0)) {
      TileType tile = TileTypeLink.getTile(tileGroup.getTileLinks().get(0), GameUtils.getGame());
      if (tile != null) {
        return tile.getBitmap();
      }
    }
    return null;
  }

}
