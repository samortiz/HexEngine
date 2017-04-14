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
import com.alwaysrejoice.hexengine.dto.AbilityTile;
import com.alwaysrejoice.hexengine.dto.EffectTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Displays and handles events for the list of games screen
 */
public class EffectListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  EffectListAdapter adapter;
  ArrayList<EffectTile> effectTiles;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.effect_list);
    Game game = GameUtils.getGame();

    list = (ListView) findViewById(R.id.effect_list_view);
    effectTiles = new ArrayList<>();
    for (String id : game.getEffects().keySet()) {
      EffectTile effectTile = game.getEffects().get(id);
      effectTiles.add(effectTile);
    }
    Collections.sort(effectTiles);
    adapter = new EffectListAdapter(this, effectTiles);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    EffectTile effectTile = (EffectTile) arg0.getItemAtPosition(position);
    Log.d("effectList", "onItemClick effectTile="+ effectTile);
    Intent myIntent = new Intent(EffectListActivity.this, EffectEditActivity.class);
    myIntent.putExtra(EffectEditActivity.SELECTED_EFFECT_ID, effectTile.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    final int position = (int) view.getTag();
    final EffectTile effectTile = (EffectTile) list.getItemAtPosition(position);
    new AlertDialog.Builder(this)
        .setTitle("Confirm Delete")
        .setMessage("Are you sure you want to delete "+ effectTile.getName()+"?")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            final Game game = GameUtils.getGame();
            // Delete all the links pointing to this effectTile
            for (UnitTile unit : game.getUnitTiles().values()) {
              unit.getEffectIds().remove(effectTile.getId());
            } // for
            for (AbilityTile abilityTile : game.getAbilities().values()) {
              if (effectTile.getId().equals(abilityTile.getEffectId())) {
                abilityTile.setEffectId(null);
              }
            }  // for
            Map<String, EffectTile> effectMap = game.getEffects();
            effectMap.remove(effectTile.getId());
            GameUtils.saveGame();
            adapter.removeItem(position);
            adapter.notifyDataSetChanged();
            Log.d("effectList", "deleted "+ effectTile.getName());
          }})
        .setNegativeButton("Cancel", null).show();
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoSettings(View view) {
    Log.d("effectList", "goto Settings");
    Intent myIntent = new Intent(EffectListActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Log.d("effectList", "Create New");
    Intent myIntent = new Intent(EffectListActivity.this, EffectEditActivity.class);
    myIntent.putExtra(EffectEditActivity.SELECTED_EFFECT_ID, "");
    startActivity(myIntent);
  }

}
