package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Damage;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.List;

public class DefenceEditActivity extends Activity {
  public static final String SELECTED_UNIT_NAME = "DEF_EDIT_SELECTED_UNIT_NAME";
  public static final String SELECTED_DAMAGE_INDEX = "DEF_EDIT_SELECTED_DAMAGE_INDEX";
  Damage damage;
  String selectedUnitName;
  int selectedDamageIndex;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.defence_edit);
    Bundle bundle = getIntent().getExtras();

    selectedUnitName = (String) bundle.get(SELECTED_UNIT_NAME);
    selectedDamageIndex = Integer.parseInt((String) bundle.get(SELECTED_DAMAGE_INDEX));

    if (selectedDamageIndex >= 0) {
      damage = GameUtils.getGame().getUnitTiles().get(selectedUnitName).getDefence().get(selectedDamageIndex);
    } else {
      damage = new Damage();
    }

    List<String> damageTypes = GameUtils.getGame().getDamageTypes();
    damageTypes.add(0, "All"); // Special type for defence only

    // Setup the UI
    LayoutInflater inflator = LayoutInflater.from(getBaseContext());
    View damageView = inflator.inflate(R.layout.action_damage_row, null);
    Spinner damageTypeSpinner = (Spinner) damageView.findViewById(R.id.damage_type_spinner);
    ArrayAdapter<String> dmgAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, damageTypes);
    dmgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    damageTypeSpinner.setAdapter(dmgAdapter);
    int selectedDamageTypeIndex = Math.max(0, dmgAdapter.getPosition(damage.getType()));
    damageTypeSpinner.setSelection(selectedDamageTypeIndex);
    EditText countInput = (EditText) damageView.findViewById(R.id.count_input);
    countInput.setText(Utils.intToString(damage.getCount()));
    EditText sizeInput = (EditText) damageView.findViewById(R.id.size_input);
    sizeInput.setText(Utils.intToString(damage.getSize()));
    EditText bonusInput = (EditText) damageView.findViewById(R.id.bonus_input);
    bonusInput.setText(Utils.doubleToString(damage.getBonus()));

    LinearLayout dmgRowContainer = (LinearLayout) findViewById(R.id.damage_row_container);
    dmgRowContainer.addView(damageView);
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    // Load the damage object from the UI
    EditText count = (EditText) findViewById(R.id.count_input);
    EditText size = (EditText) findViewById(R.id.size_input);
    EditText bonus = (EditText) findViewById(R.id.bonus_input);
    Spinner dmgTypeSpinner = (Spinner) findViewById(R.id.damage_type_spinner);
    ArrayAdapter<String> dmgAdapter = (ArrayAdapter<String>)dmgTypeSpinner.getAdapter();
    damage.setCount(Utils.stringToInt(count.getText().toString()));
    damage.setSize(Utils.stringToInt(size.getText().toString()));
    damage.setBonus(Utils.stringToDouble(bonus.getText().toString()));
    damage.setType(dmgAdapter.getItem(dmgTypeSpinner.getSelectedItemPosition()));

    if (selectedDamageIndex < 0) {
      GameUtils.getGame().getUnitTiles().get(selectedUnitName).getDefence().add(damage);
    }
    // else if the damage is being edited, just calling save is enough (object already exists in the list)
    GameUtils.saveGame();

    Log.d("damageEdit", "finished editing "+damage);
    // Go to the defence list
    Intent myIntent = new Intent(DefenceEditActivity.this, DefenceListActivity.class);
    myIntent.putExtra(DefenceListActivity.SELECTED_UNIT_NAME, selectedUnitName);
    startActivity(myIntent);
  }

}
