package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Displays and handles events for the list of games screen
 */
public class BgListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  BgListAdapter adapter;
  ArrayList<String> tileNames;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.bg_list);
    Game game = GameUtils.getGame();

    list = (ListView) findViewById(R.id.bg_list_view);
    tileNames = new ArrayList<>();
    for (String name: game.getBgTiles().keySet()) {
      tileNames.add(name);
    }
    Collections.sort(tileNames, String.CASE_INSENSITIVE_ORDER);
    adapter = new BgListAdapter(this, tileNames);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("bgList", "onItemClick ");
    String tileName = (String) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(BgListActivity.this, BgEditActivity.class);
    myIntent.putExtra(BgEditActivity.SELECTED_TILE, tileName);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void deleteBg(View view) {
    int position = (int) view.getTag();
    String tileName = list.getItemAtPosition(position).toString();
    Map<String, BgTile> bgTiles = GameUtils.getGame().getBgTiles();
    bgTiles.remove(tileName);
    GameUtils.saveGame();
    adapter.removeItem(position);
    Collections.sort(tileNames, String.CASE_INSENSITIVE_ORDER);
    adapter.notifyDataSetChanged();
    Log.d("bgList", "deleted "+tileName);
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoSettings(View view) {
    Log.d("bgList", "goto Settings");
    Intent myIntent = new Intent(BgListActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void createNewBg(View view) {
    Log.d("bgList", "Create New");
    Intent myIntent = new Intent(BgListActivity.this, BgEditActivity.class);
    myIntent.putExtra("BG_NAME", "");
    startActivity(myIntent);
  }

}