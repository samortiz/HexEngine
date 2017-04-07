package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Ability;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Displays and handles events for the list abilities screen
 */
public class AbilityListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  AbilityListAdapter adapter;
  List<Ability> abilities;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ability_list);
    Game game = GameUtils.getGame();

    list = (ListView) findViewById(R.id.ability_list_view);
    abilities = new ArrayList<>();
    for (String abilityId: game.getAbilities().keySet()) {
      Ability ability = game.getAbilities().get(abilityId);
      abilities.add(ability);
    }
    Collections.sort(abilities);
    adapter = new AbilityListAdapter(this, abilities);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("abilityList", "onItemClick ");
    Ability ability = (Ability) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(AbilityListActivity.this, AbilityEditActivity.class);
    myIntent.putExtra(AbilityEditActivity.SELECTED_ABILITY_ID, ability.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    Game game = GameUtils.getGame();
    int position = (int) view.getTag();
    Ability ability = (Ability) list.getItemAtPosition(position);
    Map<String, Ability> abilityTiles = game.getAbilities();
    abilityTiles.remove(ability.getId());
    // TODO: Delete all the links pointing to this ability
    GameUtils.saveGame();
    adapter.removeItem(position);
    adapter.notifyDataSetChanged();
    Log.d("abilityList", "deleted "+ability.getName());
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoSettings(View view) {
    Log.d("abilityList", "goto Settings");
    Intent myIntent = new Intent(AbilityListActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Log.d("abilityList", "Create New");
    Intent myIntent = new Intent(AbilityListActivity.this, AbilityEditActivity.class);
    myIntent.putExtra(AbilityEditActivity.SELECTED_ABILITY_ID, "");
    startActivity(myIntent);
  }

}
