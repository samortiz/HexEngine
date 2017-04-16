package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.AI;
import com.alwaysrejoice.hexengine.dto.Team;
import com.alwaysrejoice.hexengine.util.GameUtils;
import java.util.ArrayList;
import java.util.List;

public class AiListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  AiListAdapter adapter;
  List<AI> ais = new ArrayList<AI>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ai_list);

    // Load the ais from the game
    ais = GameUtils.getGame().getAis();
    adapter = new AiListAdapter(this, ais);
    list = (ListView) findViewById(R.id.ai_list_view);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular ai row is clicked (goto edit the ai)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    AI ai = (AI) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(AiListActivity.this, AiEditActivity.class);
    myIntent.putExtra(AiEditActivity.SELECTED_AI_ID, ai.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    final int position = (int) view.getTag();
    final AI ai = (AI) list.getItemAtPosition(position);

    // Remove all the links to this AI in teams
    for (Team team : GameUtils.getGame().getTeams()) {
      if (ai.getId().equals(team.getId())) {
        team.setAiId(null);
      }
    } // for

    // The adapter uses the game list
    adapter.removeItem(position);
    GameUtils.saveGame();
    adapter.notifyDataSetChanged();
    Log.d("aiList", "deleted "+ai.getName());
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoSettings(View view) {
    Log.d("aiList", "goto Settings");
    Intent myIntent = new Intent(AiListActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Log.d("aiList", "Create New");
    Intent myIntent = new Intent(AiListActivity.this, AiEditActivity.class);
    myIntent.putExtra(AiEditActivity.SELECTED_AI_ID, "");
    startActivity(myIntent);
  }

}
