package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Ability;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Displays and handles events for the list abilities screen
 */
public class AbilityChooserActivity extends Activity {
  public static final String SELECTED_UNIT_ID = "ABILITY_CHOOSER_SELECTED_UNIT_ID";

  String unitId;
  ListView list;
  AbilityListAdapter adapter;
  List<Ability> abilities;
  UnitTile unit;
  List<String> allAbilityNames;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ability_chooser);
    Game game = GameUtils.getGame();
    Bundle bundle = getIntent().getExtras();

    // Get the unit we are editing
    unitId = (String) bundle.get(AbilityChooserActivity.SELECTED_UNIT_ID);
    if ((unitId != null) && !"".equals(unitId)) {
      unit = game.getUnitTiles().get(unitId);
    }
    if (unit == null) {
      throw new IllegalArgumentException("Error! You must specify a SELECTED_UNIT_ID when calling AbilityChooser unitId="+unitId);
    }

    list = (ListView) findViewById(R.id.ability_list_view);
    abilities = new ArrayList<>();
    for (String abilityId : unit.getAbilityIds()) {
      Ability ability = game.getAbilities().get(abilityId);
      abilities.add(ability);
    }
    adapter = new AbilityListAdapter(this, abilities);
    list.setAdapter(adapter);

    allAbilityNames = new ArrayList<>();
    for (Ability ability : game.getAbilities().values()) {
      if (!abilities.contains(ability)) {
        allAbilityNames.add(ability.getName());
      }
    }
    resetAbilitySpinner();
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    final int position = (int) view.getTag();
    final Ability ability = (Ability) list.getItemAtPosition(position);
    allAbilityNames.add(ability.getName());
    resetAbilitySpinner();
    adapter.removeItem(position);
    adapter.notifyDataSetChanged();
    Log.d("abilityChooser", "deleted "+ability.getName());
  }

  /**
   * Called when the user clicks Settings
   */
  public void save(View view) {
    Log.d("abilityChooser", "Save");
    List<String> abilityIds = unit.getAbilityIds();
    abilityIds.clear();
    for (Ability ability : abilities) {
      abilityIds.add(ability.getId());
    } // for
    GameUtils.saveGame();
    Intent myIntent = new Intent(AbilityChooserActivity.this, UnitEditActivity.class);
    myIntent.putExtra(UnitEditActivity.SELECTED_UNIT_ID, unitId);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Add"
   */
  public void add(View view) {
    Log.d("abilityChooser", "Add");
    String abilityName = Utils.getSpinnerValue((Spinner) findViewById(R.id.ability_spinner));
    String abilityId = GameUtils.getAbilityIdFromName(abilityName);
    Ability ability = GameUtils.getGame().getAbilities().get(abilityId);
    allAbilityNames.remove(abilityName);
    resetAbilitySpinner();
    if (!abilities.contains(ability)) {
      abilities.add(ability);
    }
    adapter.notifyDataSetChanged();
  }

  private void resetAbilitySpinner() {
    Collections.sort(allAbilityNames, String.CASE_INSENSITIVE_ORDER);
    Spinner abilitySpinner = (Spinner) findViewById(R.id.ability_spinner);
    Utils.setupSpinner(abilitySpinner, allAbilityNames);
  }

}
