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
import com.alwaysrejoice.hexengine.dto.Ability;
import com.alwaysrejoice.hexengine.dto.Effect;
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
  ArrayList<Effect> effects;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.effect_list);
    Game game = GameUtils.getGame();

    list = (ListView) findViewById(R.id.effect_list_view);
    effects = new ArrayList<>();
    for (String id : game.getEffects().keySet()) {
      Effect effect = game.getEffects().get(id);
      effects.add(effect);
    }
    Collections.sort(effects);
    adapter = new EffectListAdapter(this, effects);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Effect effect = (Effect) arg0.getItemAtPosition(position);
    Log.d("effectList", "onItemClick effect="+effect);
    Intent myIntent = new Intent(EffectListActivity.this, EffectEditActivity.class);
    myIntent.putExtra(EffectEditActivity.SELECTED_EFFECT_ID, effect.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    final int position = (int) view.getTag();
    final Effect effect = (Effect) list.getItemAtPosition(position);
    new AlertDialog.Builder(this)
        .setTitle("Confirm Delete")
        .setMessage("Are you sure you want to delete "+effect.getName()+"?")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            final Game game = GameUtils.getGame();
            // Delete all the links pointing to this effect
            for (UnitTile unit : game.getUnitTiles().values()) {
              unit.getEffectIds().remove(effect.getId());
            } // for
            for (Ability ability : game.getAbilities().values()) {
              if (effect.getId().equals(ability.getEffectId())) {
                ability.setEffectId(null);
              }
            }  // for
            Map<String, Effect> effectMap = game.getEffects();
            effectMap.remove(effect.getId());
            GameUtils.saveGame();
            adapter.removeItem(position);
            adapter.notifyDataSetChanged();
            Log.d("effectList", "deleted "+effect.getName());
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
