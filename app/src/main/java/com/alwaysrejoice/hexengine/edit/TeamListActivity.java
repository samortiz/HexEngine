package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Team;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.util.GameUtils;
import java.util.ArrayList;
import java.util.List;

public class TeamListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  TeamListAdapter adapter;
  List<Team> teams = new ArrayList<Team>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.team_list);

    // Load the teams from the game
    teams = GameUtils.getGame().getTeams();
    adapter = new TeamListAdapter(this, teams);
    list = (ListView) findViewById(R.id.team_list_view);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular team row is clicked (goto edit the team)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Team team = (Team) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(TeamListActivity.this, TeamEditActivity.class);
    myIntent.putExtra(TeamEditActivity.SELECTED_TEAM_ID, team.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    final int position = (int) view.getTag();
    final Team team = (Team) list.getItemAtPosition(position);

    // Remove all the links to this team in unitTile
    for (UnitTile unitTile : GameUtils.getGame().getUnitTiles().values()) {
      if (team.getId().equals(unitTile.getTeamId())) {
        unitTile.setTeamId(null);
      }
    }

    // The adapter uses the game list
    adapter.removeItem(position);
    GameUtils.saveGame();
    adapter.notifyDataSetChanged();
    Log.d("teamList", "deleted "+team.getName());
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoSettings(View view) {
    Log.d("teamList", "goto Settings");
    Intent myIntent = new Intent(TeamListActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Log.d("teamList", "Create New");
    Intent myIntent = new Intent(TeamListActivity.this, TeamEditActivity.class);
    myIntent.putExtra(TeamEditActivity.SELECTED_TEAM_ID, "");
    startActivity(myIntent);
  }

}
