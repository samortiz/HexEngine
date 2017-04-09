package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Displays and handles events for the list storage screen
 */
public class StorageListActivity extends Activity {
  public static final String SELECTED_UNIT_ID = "STORAGE_SELECTED_UNIT_ID";

  String unitId; // set by extra and passed out by extra
  ListView list;
  StorageListAdapter adapter;
  List<String> keys;
  Map<String, String> storage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.storage_list);
    Game game = GameUtils.getGame();
    Bundle bundle = getIntent().getExtras();

    // Get the unit we are editing
    UnitTile unit = null;
    unitId = (String) bundle.get(StorageListActivity.SELECTED_UNIT_ID);
    if ((unitId != null) && !"".equals(unitId)) {
      unit = game.getUnitTiles().get(unitId);
    }
    if (unit == null) {
      throw new IllegalArgumentException("Error! You must specify a SELECTED_UNIT_ID when calling StorageListActivity unitId="+unitId);
    }

    storage = unit.getStorage();
    List<String> keys = new ArrayList<>();
    keys.addAll(storage.keySet());
    list = (ListView) findViewById(R.id.storage_list_view);
    adapter = new StorageListAdapter(this, keys, storage);
    list.setAdapter(adapter);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    final Game game = GameUtils.getGame();
    final int position = (int) view.getTag();
    final String key = (String) list.getItemAtPosition(position);
    storage.remove(key);
    adapter.removeItem(position);
    adapter.notifyDataSetChanged();
  }

  /**
   * Called when the user clicks Save
   */
  public void save(View view) {
    Log.d("storageList", "Save");
    GameUtils.saveGame();
    Intent myIntent = new Intent(StorageListActivity.this, UnitEditActivity.class);
    myIntent.putExtra(UnitEditActivity.SELECTED_UNIT_ID, unitId);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Log.d("storageList", "Create New");
    EditText keyInput = (EditText) findViewById(R.id.storage_key);
    String key = keyInput.getText().toString();
    keyInput.setText("");
    EditText valueInput = (EditText) findViewById(R.id.storage_value);
    String value = valueInput.getText().toString();
    valueInput.setText("");
    adapter.addItem(key, value);
    adapter.notifyDataSetChanged();
  }

}
