package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.TileGroup;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

/**
 * Displays and handles events for the list of tile groups screen
 */
public class TileGroupListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  TileGroupListAdapter adapter;
  List<TileGroup> tileGroups;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tile_group_list);
    Game game = GameUtils.getGame();
    list = (ListView) findViewById(R.id.tile_group_list_view);
    tileGroups = game.getTileGroups();
    adapter = new TileGroupListAdapter(this, tileGroups);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("bgList", "onItemClick ");
    TileGroup tile = (TileGroup) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(TileGroupListActivity.this, TileGroupEditActivity.class);
    myIntent.putExtra(TileGroupEditActivity.EXTRA_TILE_GROUP_ID, tile.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    int position = (int) view.getTag();
    adapter.removeItem(position);
    GameUtils.saveGame();
    adapter.notifyDataSetChanged();
    Log.d("tileGroupList", "deleted pos="+position);
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoSettings(View view) {
    Log.d("tileGroupList", "goto Settings");
    Intent myIntent = new Intent(TileGroupListActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void newTileGroup(View view) {
    Log.d("tileGroupList", "Create New");
    Intent myIntent = new Intent(TileGroupListActivity.this, TileGroupEditActivity.class);
    myIntent.putExtra(TileGroupEditActivity.EXTRA_TILE_GROUP_ID, "");
    startActivity(myIntent);
  }

}
