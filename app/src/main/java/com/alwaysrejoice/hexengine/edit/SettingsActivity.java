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

  public void gotoEditMap(View view) {
    Intent myIntent = new Intent(SettingsActivity.this, EditMapActivity.class);
    startActivity(myIntent);
    Log.d("settings", "goto EditMap");
  }

}
