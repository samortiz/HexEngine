package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Damage;

import java.util.List;

public class DefenceListAdapter extends BaseAdapter {

  private Context context;
  private List<Damage> damages;

  public DefenceListAdapter(Context context, List<Damage> damages) {
    this.context = context;
    this.damages = damages;
  }

  public int getCount() {
    return damages.size();
  }

  public Damage getItem(int arg0) {
    return damages.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    damages.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.defence_list_row, parent, false);
    Damage damage = this.damages.get(position);

    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    nameView.setText(damage.getDisplayText());

    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);
    return (row);
  }
}
