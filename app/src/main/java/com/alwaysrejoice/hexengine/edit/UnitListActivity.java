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
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.TileTypeLink;
import com.alwaysrejoice.hexengine.dto.UnitMap;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Displays and handles events for the list of games screen
 */
public class UnitListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  UnitListAdapter adapter;
  ArrayList<UnitTile> units;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.unit_list);
    Game game = GameUtils.getGame();

    list = (ListView) findViewById(R.id.unit_list_view);
    units = new ArrayList<>();
    for (String unitName : game.getUnitTiles().keySet()) {
      UnitTile unit = game.getUnitTiles().get(unitName);
      units.add(unit);
    }
    Collections.sort(units);
    adapter = new UnitListAdapter(this, units);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("unitList", "onItemClick ");
    UnitTile unit = (UnitTile) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(UnitListActivity.this, UnitEditActivity.class);
    myIntent.putExtra(UnitEditActivity.SELECTED_UNIT, unit.getName());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    Game game = GameUtils.getGame();
    int position = (int) view.getTag();
    UnitTile unit = (UnitTile) list.getItemAtPosition(position);
    Map<String, UnitTile> unitTiles = game.getUnitTiles();
    unitTiles.remove(unit.getName());

    // delete all the links pointing to this unit
    for (int i=game.getUnitMaps().size()-1; i>=0; i--) {
      UnitMap unitMap = game.getUnitMaps().get(i);
      if (unit.getName().equals(unitMap.getName())) {
        game.getUnitMaps().remove(i);
      }
    } //for
    for (TileGroup group : game.getTileGroups()) {
      for (int i = group.getTileLinks().size()-1; i>=0; i--) {
        TileTypeLink link = group.getTileLinks().get(i);
        if (unit.getName().equals(link.getName()) &&
            (link.getTileType() == TileType.TILE_TYPE.UNIT)) {
          group.getTileLinks().remove(i);
        }
      }
    } // for

    GameUtils.saveGame();
    adapter.removeItem(position);
    Collections.sort(units);
    adapter.notifyDataSetChanged();
    Log.d("unitList", "deleted "+unit.getName());
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoSettings(View view) {
    Log.d("unitList", "goto Settings");
    Intent myIntent = new Intent(UnitListActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Log.d("unitList", "Create New");
    Intent myIntent = new Intent(UnitListActivity.this, UnitEditActivity.class);
    myIntent.putExtra(UnitEditActivity.SELECTED_UNIT, "");
    startActivity(myIntent);
  }

}
