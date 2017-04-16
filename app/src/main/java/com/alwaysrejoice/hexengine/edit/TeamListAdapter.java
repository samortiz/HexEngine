package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Team;
import java.util.List;

public class TeamListAdapter extends BaseAdapter {

  private Context context;
  private List<Team> teams;

  public TeamListAdapter(Context context, List<Team> teams) {
    this.context = context;
    this.teams = teams;
  }

  public int getCount() {
    return teams.size();
  }

  public Team getItem(int arg0) {
    return teams.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    teams.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.team_list_row, parent, false);
    Team team = this.teams.get(position);

    TextView nameView = (TextView) row.findViewById(R.id.row_name);
    nameView.setText(team.getName());

    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);
    return (row);
  }
}
