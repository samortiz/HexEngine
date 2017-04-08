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
import android.widget.Toast;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Ability;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.Effect;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Displays and handles events for the list of games screen
 */
public class ModListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  ModListAdapter adapter;
  List<Mod> mods = new ArrayList<Mod>();

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
    myIntent.putExtra(ModEditActivity.SELECTED_MOD_ID, mod.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    final Map<String, Mod> modMap = GameUtils.getGame().getMods();
    final int position = (int) view.getTag();
    final Mod mod = (Mod) list.getItemAtPosition(position);

    // You are not allowed to delete the last mod or rule
    // If the last one is deleted it will cause the spinners to break
    int matchingTypeCount = 0;
    for (Mod m : modMap.values()) {
      if (mod.getType().equals(m.getType())) {
        matchingTypeCount++;
      }
    } // for
    if (matchingTypeCount <= 1) {
      Toast.makeText(ModListActivity.this, "You cannot delete all mods of type "+mod.getType()+" create a new one first.", Toast.LENGTH_SHORT).show();
      return;
    }

    new AlertDialog.Builder(this)
        .setTitle("Confirm Delete")
        .setMessage("Deleting will remove the mod from any actions where it is assigned.")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Clear all matching mod links in the game
            for (Ability ability : GameUtils.getGame().getAbilities().values()) {
              deleteMod(mod.getId(), ability.getOnStart());
            }
            for (Effect effect : GameUtils.getGame().getEffects().values()) {
              deleteMod(mod.getId(), effect.getOnRun());
              deleteMod(mod.getId(), effect.getOnEnd());
            }
            // Remove the mod from master list
            modMap.remove(mod.getId());
            GameUtils.saveGame();
            // Remove from this screen
            adapter.removeItem(position);
            adapter.notifyDataSetChanged();
            Log.d("modList", "deleted "+mod.getName());
          }})
        .setNegativeButton("Cancel", null).show();
  }

  // Searches the list for items with matching ModId and removes them
  private void deleteMod(String modId, Collection<Action> actions) {
    for (Iterator<Action> it = actions.iterator(); it.hasNext();) {
      Action action = it.next();
      if (modId.equals(action.getModId())) {
        it.remove();
      }
    } // for
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
    myIntent.putExtra(ModEditActivity.SELECTED_MOD_ID, "");
    startActivity(myIntent);
  }

}
