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
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Displays and handles events for the list of games screen
 */
public class ModListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  ModListAdapter adapter;
  ArrayList<Mod> mods = new ArrayList<Mod>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mod_list);

    // Load the mods from the game
    mods.addAll(GameUtils.getGame().getMods().values());
    Collections.sort(mods);

    adapter = new ModListAdapter(this, mods);
    list = (ListView) findViewById(R.id.mod_list_view);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular mod row is clicked (goto edit the mod)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Mod mod = (Mod) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(ModListActivity.this, ModEditActivity.class);
    myIntent.putExtra(ModEditActivity.SELECTED_MOD, mod.getName());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    Game game = GameUtils.getGame();
    int position = (int) view.getTag();
    Mod mod = (Mod) list.getItemAtPosition(position);

    // Remove from the game
    Map<String, Mod> modMap = game.getMods();
    modMap.remove(mod.getName());

    // TODO : Clear all matching mod links in the game

    GameUtils.saveGame();

    // Remove from this screen
    adapter.removeItem(position);
    adapter.notifyDataSetChanged();
    Log.d("modList", "deleted "+mod.getName());
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoSettings(View view) {
    Log.d("modList", "goto Settings");
    Intent myIntent = new Intent(ModListActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Log.d("modList", "Create New");
    Intent myIntent = new Intent(ModListActivity.this, ModEditActivity.class);
    myIntent.putExtra(ModEditActivity.SELECTED_MOD, "");
    startActivity(myIntent);
  }

}
