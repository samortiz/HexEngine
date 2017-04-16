package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alwaysrejoice.hexengine.R;

/**
 * Settings Screen
 */
public class SettingsActivity extends Activity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings);
  }

  public void gotoBgList(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, BgListActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto BG List");
  }

  public void gotoUnitList(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, UnitListActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto Unit List");
  }

  public void gotoAbilityList(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, AbilityListActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto AbilityTile List");
  }

  public void gotoModList(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, ModListActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto Mod List");
  }

  public void gotoEffectsList(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, EffectListActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto EffectTile List");
  }

  public void gotoTriggers(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, TriggersActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto Triggers");
  }

  public void gotoListEditor(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, ListEditorActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto List Editor");
  }

  public void gotoTileGroups(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, TileGroupListActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto TileGroups");
  }

  public void clear(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, ClearActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto Clear");
  }

  public void gotoTeamList(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, TeamListActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto Team List");
  }

  public void gotoAiList(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, AiListActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto AI list");
  }

  public void gotoEditMap(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, EditMapActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto EditMap");
  }

}
