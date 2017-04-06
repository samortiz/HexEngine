package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Effect;
import com.alwaysrejoice.hexengine.dto.Game;
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
    Game game = GameUtils.getGame();
    int position = (int) view.getTag();
    Effect effect = (Effect) list.getItemAtPosition(position);
    Map<String, Effect> effectMap = game.getEffects();
    effectMap.remove(effect.getId());

    // TODO : Delete all the links pointing to this effect

    GameUtils.saveGame();
    adapter.removeItem(position);
    adapter.notifyDataSetChanged();
    Log.d("effectList", "deleted "+effect.getName());
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
