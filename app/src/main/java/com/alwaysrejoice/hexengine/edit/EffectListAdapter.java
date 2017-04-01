package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Effect;

import java.util.List;

public class EffectListAdapter extends BaseAdapter {

  private Context context;
  private List<Effect> effects;

  public EffectListAdapter(Context context, List<Effect> effects) {
    this.context = context;
    this.effects = effects;
  }

  public int getCount() {
    return effects.size();
  }

  public Effect getItem(int arg0) {
    return effects.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    effects.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.effect_list_row, parent, false);
    Effect effect = this.effects.get(position);

    ImageView imageView = (ImageView) row.findViewById(R.id.row_img);
    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);

    imageView.setImageBitmap(effect.getBitmap());
    nameView.setText(effect.getName()+"("+effect.getDuration()+")");
    deleteImg.setTag(position);
    return (row);
  }
}
