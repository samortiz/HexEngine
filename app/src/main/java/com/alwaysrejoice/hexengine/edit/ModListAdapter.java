package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Mod;

import java.util.List;

public class ModListAdapter extends BaseAdapter {

  private Context context;
  private List<Mod> mods;

  public ModListAdapter(Context context, List<Mod> mods) {
    this.context = context;
    this.mods = mods;
  }

  public int getCount() {
    return mods.size();
  }

  public Mod getItem(int arg0) {
    return mods.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    mods.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.mod_list_row, parent, false);
    Mod mod = this.mods.get(position);

    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);

    nameView.setText(mod.getName());
    deleteImg.setTag(position);
    return (row);
  }
}
