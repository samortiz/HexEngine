package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.EffectTile;

import java.util.List;

public class EffectListAdapter extends BaseAdapter {

  private Context context;
  private List<EffectTile> effectTiles;

  public EffectListAdapter(Context context, List<EffectTile> effectTiles) {
    this.context = context;
    this.effectTiles = effectTiles;
  }

  public int getCount() {
    return effectTiles.size();
  }

  public EffectTile getItem(int arg0) {
    return effectTiles.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    effectTiles.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.effect_list_row, parent, false);
    EffectTile effectTile = this.effectTiles.get(position);

    ImageView imageView = (ImageView) row.findViewById(R.id.row_img);
    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);

    imageView.setImageBitmap(effectTile.getBitmap());
    nameView.setText(effectTile.getName()+"("+ effectTile.getDuration()+")");
    deleteImg.setTag(position);
    return (row);
  }
}
