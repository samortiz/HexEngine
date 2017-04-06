package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

public class ActionListAdapter extends BaseAdapter {

  private Context context;
  private List<Action> actions;

  public ActionListAdapter(Context context, List<Action> actions) {
    this.context = context;
    this.actions = actions;
  }

  public int getCount() {
    return actions.size();
  }

  public Action getItem(int arg0) {
    return actions.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    actions.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.action_list_row, parent, false);
    Action action = this.actions.get(position);

    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);

    nameView.setText(GameUtils.getModNameFromId(action.getModId()));
    deleteImg.setTag(position);
    return (row);
  }
}
