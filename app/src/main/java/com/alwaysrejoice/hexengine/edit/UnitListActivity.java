package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
 * Displays and handles events for the list of units screen
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
    for (String unitId: game.getUnitTiles().keySet()) {
      UnitTile unit = game.getUnitTiles().get(unitId);
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
    myIntent.putExtra(UnitEditActivity.SELECTED_UNIT_ID, unit.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    final Game game = GameUtils.getGame();
    final int position = (int) view.getTag();
    final UnitTile unit = (UnitTile) list.getItemAtPosition(position);
    new AlertDialog.Builder(this)
      .setTitle("Confirm Delete")
      .setMessage("Are you sure you want to delete "+unit.getName()+"?")
      .setIcon(android.R.drawable.ic_dialog_alert)
      .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
          // delete all the links pointing to this unit
          for (int i=game.getUnitMaps().size()-1; i>=0; i--) {
            UnitMap unitMap = game.getUnitMaps().get(i);
            if (unit.getId().equals(unitMap.getId())) {
              game.getUnitMaps().remove(i);
            }
          } //for
          for (TileGroup group : game.getTileGroups()) {
            for (int i = group.getTileLinks().size()-1; i>=0; i--) {
              TileTypeLink link = group.getTileLinks().get(i);
              if (unit.getId().equals(link.getId()) &&
                  (link.getTileType() == TileType.TILE_TYPE.UNIT)) {
                group.getTileLinks().remove(i);
              }
            }
          } // for
          Map<String, UnitTile> unitTiles = game.getUnitTiles();
          unitTiles.remove(unit.getId());
          GameUtils.saveGame();
          adapter.removeItem(position);
          Collections.sort(units);
          adapter.notifyDataSetChanged();
          Log.d("unitList", "deleted "+unit.getName());
        }})
      .setNegativeButton("Cancel", null).show();
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
    myIntent.putExtra(UnitEditActivity.SELECTED_UNIT_ID, "");
    startActivity(myIntent);
  }

}
