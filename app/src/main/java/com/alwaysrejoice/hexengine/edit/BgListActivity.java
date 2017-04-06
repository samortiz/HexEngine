package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.BgMap;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.TileGroup;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.TileTypeLink;
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
  ArrayList<BgTile> tiles;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.bg_list);
    Game game = GameUtils.getGame();

    list = (ListView) findViewById(R.id.bg_list_view);
    tiles = new ArrayList<>();
    for (String id : game.getBgTiles().keySet()) {
      tiles.add(game.getBgTiles().get(id));
    }
    Collections.sort(tiles);
    adapter = new BgListAdapter(this, tiles);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("bgList", "onItemClick ");
    BgTile tile = (BgTile) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(BgListActivity.this, BgEditActivity.class);
    myIntent.putExtra(BgEditActivity.SELECTED_TILE_ID, tile.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void deleteBg(View view) {
    Game game = GameUtils.getGame();
    int position = (int) view.getTag();
    BgTile tile = (BgTile) list.getItemAtPosition(position);
    Map<String, BgTile> bgTiles = game.getBgTiles();
    bgTiles.remove(tile.getId());

    // delete all the links pointing to this bgTile
    for (int i=game.getBgMaps().size()-1; i>=0; i--) {
      BgMap bgMap = game.getBgMaps().get(i);
      if (tile.getId().equals(bgMap.getId())) {
        game.getBgMaps().remove(i);
      }
    } //for
    for (TileGroup group : game.getTileGroups()) {
      for (int i = group.getTileLinks().size()-1; i>=0; i--) {
        TileTypeLink link = group.getTileLinks().get(i);
        if (tile.getId().equals(link.getId()) &&
            (link.getTileType() == TileType.TILE_TYPE.BACKGROUND)) {
          group.getTileLinks().remove(i);
        }
      }
    } // for

    GameUtils.saveGame();
    adapter.removeItem(position);
    adapter.notifyDataSetChanged();
    Log.d("bgList", "deleted "+tile.getName());
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
    myIntent.putExtra(BgEditActivity.SELECTED_TILE_ID, "");
    startActivity(myIntent);
  }

}
