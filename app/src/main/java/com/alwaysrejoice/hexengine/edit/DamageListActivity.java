package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

/**
 * Displays and handles events for the list of games screen
 */
public class DamageListActivity extends Activity {
  ListView list;
  DamageListAdapter adapter;
  List<String> damageTypes;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.damage_list);
    damageTypes = GameUtils.getGame().getDamageTypes();
    list = (ListView) findViewById(R.id.damage_list_view);
    adapter = new DamageListAdapter(this, damageTypes);
    list.setAdapter(adapter);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    int position = (int) view.getTag();
    String damageType = (String) list.getItemAtPosition(position);
    // Hmmm... Maybe we should delete all links to this damage type?
    // The links would be broken (not selectable), but it seems too
    // destructive to delete the links, things will keep working as long
    // as the damage isn't brought up in the editor, then it will be wiped
    adapter.removeItem(position);
    GameUtils.saveGame();
    adapter.notifyDataSetChanged();
    Log.d("damageList", "deleted "+damageType);
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoSettings(View view) {
    Log.d("damageList", "goto Settings");
    Intent myIntent = new Intent(DamageListActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Log.d("damageList", "Create New");
    EditText newDamageInput = (EditText) findViewById(R.id.new_damage_input);
    damageTypes.add(newDamageInput.getText().toString().trim());
    GameUtils.saveGame();
    newDamageInput.setText("");
    adapter.notifyDataSetChanged();
  }

}
