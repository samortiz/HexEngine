package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.AI;
import java.util.List;

public class AiListAdapter extends BaseAdapter {

  private Context context;
  private List<AI> ais;

  public AiListAdapter(Context context, List<AI> ais) {
    this.context = context;
    this.ais = ais;
  }

  public int getCount() {
    return ais.size();
  }

  public AI getItem(int arg0) {
    return ais.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    ais.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.ai_list_row, parent, false);
    AI ai = this.ais.get(position);

    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    nameView.setText(ai.getName());

    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);
    return (row);
  }
}
