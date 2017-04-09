package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.util.DialogMultiSelect;
import com.alwaysrejoice.hexengine.util.DialogMultiSelectListener;
import com.alwaysrejoice.hexengine.util.DialogMultiSelectTileType;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UnitEditActivity extends Activity {
  public static final String SELECTED_UNIT_ID = "SELECTED_UNIT_ID";
  UnitTile unit; // The unit we are currently editing

  DialogMultiSelect attrDialog;
  DialogMultiSelect moveRestrictDialog;
  DialogMultiSelect sightRestrictDialog;

  DialogMultiSelectTileType abilityDialog;
  List<TileType> unitAbilities; // Actually contains abilities

  DialogMultiSelectTileType effectDialog;
  List<TileType> unitEffects; // Actually contains effects

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("unitEdit", "onCreate");
    setContentView(R.layout.unit_edit);
    Bundle bundle = getIntent().getExtras();
    Game game = GameUtils.getGame();

    // Check if we are coming back from an ImagePickerActivity
    String unitJson = (String) bundle.get(ImagePickerActivity.EXTRA_TILE);
    if (unitJson != null) {
      unit = GameUtils.jsonToUnitTile(unitJson);
      Log.d("unitEdit", "Tile chosen! " + unit.getName());
    }

    // Load the file we are editing
    String unitId = (String) bundle.get(UnitEditActivity.SELECTED_UNIT_ID);
    if ((unitId != null) && !"".equals(unitId)) {
      unit = game.getUnitTiles().get(unitId);
      Log.d("unitEdit", "begin editing selected unit "+unit.getName());
    }

    if (unit == null) {
      unit = new UnitTile(Utils.generateUniqueId());
    }

    Spinner teamSpinner = (Spinner) findViewById(R.id.team_spinner);
    Utils.setupSpinner(teamSpinner, game.getTeams());

    attrDialog = new DialogMultiSelect(this, "Attributes", game.getAttr(), unit.getAttr());
    moveRestrictDialog = new DialogMultiSelect(this, "Move Restrictions", game.getBgTypes(), unit.getMoveRestrict());
    sightRestrictDialog = new DialogMultiSelect(this, "Sight Restrictions", game.getBgTypes(), unit.getSightRestrict());

    List<TileType> abilities = new ArrayList<>();
    abilities.addAll(game.getAbilities().values());
    Collections.sort(abilities);
    unitAbilities = new ArrayList<>();
    for (String abilityId : unit.getAbilityIds()) {
      unitAbilities.add(game.getAbilities().get(abilityId));
    }
    abilityDialog = new DialogMultiSelectTileType(this, "Abilities", abilities, unitAbilities);


    List<TileType> effects = new ArrayList<>();
    effects.addAll(game.getEffects().values());
    Collections.sort(effects);
    unitEffects = new ArrayList<>();
    for (String effectId : unit.getEffectIds()) {
      unitEffects.add(game.getEffects().get(effectId));
    }
    effectDialog = new DialogMultiSelectTileType(this, "Effects", effects, unitEffects);

    // Load the UI with the unit data
    setUiFromUnit();
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    loadUnitFromUi();
    Game game = GameUtils.getGame();
    Map<String, UnitTile> unitTiles = game.getUnitTiles();

    // Clear and re-add all the selected effect ids
    unit.getEffectIds().clear();
    for (TileType t : unitEffects) {
      unit.getEffectIds().add(t.getId());
    }

    unitTiles.put(unit.getId(), unit);
    GameUtils.saveGame();
    Log.d("unitEdit", "saving UnitTile name="+unit.getName());
    // Go to the list
    Intent myIntent = new Intent(UnitEditActivity.this, UnitListActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on the bitmap to change it
   */
  public void chooseBitmap(View view) {
    loadUnitFromUi();
    Intent myIntent = new Intent(UnitEditActivity.this, ImagePickerActivity.class);
    myIntent.putExtra(ImagePickerActivity.EXTRA_RETURN, ImagePickerActivity.RETURN_UNIT);
    myIntent.putExtra(ImagePickerActivity.EXTRA_TILE, GameUtils.toJson(unit));
    startActivity(myIntent);
  }

  /**
   * Loads the information from the UI input components into the unit
   *  NOTE : bitmap is already taken care of by the ImagePicker code
   */
  private void loadUnitFromUi() {
    EditText nameInput = (EditText) findViewById(R.id.unit_name);
    unit.setName(nameInput.getText().toString().trim());

    unit.setTeam(Utils.getSpinnerValue((Spinner)findViewById(R.id.team_spinner)));

    EditText hpInput = (EditText) findViewById(R.id.hp_input);
    unit.setHpMax(Utils.stringToDouble(hpInput.getText().toString().trim()));

    EditText actionInput = (EditText) findViewById(R.id.action_input);
    unit.setActionMax(Utils.stringToDouble(actionInput.getText().toString().trim()));

    // Attr is set in the unit by DialogMultiSelect

    EditText moveRangeInput = (EditText) findViewById(R.id.move_range_input);
    unit.setMoveRange(Utils.stringToInt(moveRangeInput.getText().toString().trim()));
    // MoveRestrict is set by the dialog

    EditText sightRangeInput = (EditText) findViewById(R.id.sight_range_input);
    unit.setSightRange(Utils.stringToInt(sightRangeInput.getText().toString().trim()));
    // SightRestrict is set by the dialog

  }

  /**
   * Updates the UI to match the data in the unit
   */
  private void setUiFromUnit() {
    ImageView imgInput = (ImageView) findViewById(R.id.unit_img);
    if (unit.getBitmap() != null) {
      imgInput.setImageBitmap(unit.getBitmap());
      imgInput.getLayoutParams().height = 200;
      imgInput.setScaleType(ImageView.ScaleType.FIT_CENTER);
      imgInput.setAdjustViewBounds(true);
      imgInput.setCropToPadding(false);
      imgInput.requestLayout();
    }

    EditText nameInput = (EditText) findViewById(R.id.unit_name);
    nameInput.setText(unit.getName());

    Utils.setSpinnerValue((Spinner) findViewById(R.id.team_spinner), unit.getTeam());

    EditText hpInput = (EditText) findViewById(R.id.hp_input);
    hpInput.setText(Utils.doubleToString(unit.getHpMax()));

    EditText actionInput = (EditText) findViewById(R.id.action_input);
    actionInput.setText(Utils.doubleToString(unit.getActionMax()));

    TextView attrText = (TextView) findViewById(R.id.selected_attr);
    attrText.setText(Utils.toCSV(unit.getAttr()));

    EditText moveRangeInput = (EditText) findViewById(R.id.move_range_input);
    moveRangeInput.setText(Utils.intToString(unit.getMoveRange()));

    TextView moveRestrictText = (TextView) findViewById(R.id.move_restrict);
    moveRestrictText.setText(Utils.toCSV(unit.getMoveRestrict()));

    EditText sightRangeInput = (EditText) findViewById(R.id.sight_range_input);
    sightRangeInput.setText(Utils.intToString(unit.getSightRange()));

    TextView sightRestrictText = (TextView) findViewById(R.id.sight_restrict);
    sightRestrictText.setText(Utils.toCSV(unit.getSightRestrict()));

    TextView abilityText = (TextView) findViewById(R.id.abilities);
    abilityText.setText(GameUtils.tileTypeToCSV(unitAbilities));

    TextView defenceText = (TextView) findViewById(R.id.defence);
    defenceText.setText(GameUtils.damageToCSV(unit.getDefence()));

    TextView effectText = (TextView) findViewById(R.id.effects);
    effectText.setText(GameUtils.tileTypeToCSV(unitEffects));

  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
  }


  /**
   * Called when the user clicks on the edit icon by attributes
   * this will show the multi-select dialog for attributes
   */
  public void chooseAttr(View view) {
    attrDialog.showDialog(new DialogMultiSelectListener() {
      @Override
      public void onOK() {
        TextView textView = (TextView) findViewById(R.id.selected_attr);
        textView.setText(Utils.toCSV(unit.getAttr()));
      }
    });
  }

  /**
   * Called when the user clicks on the pencil to edit the move restrictions
   */
  public void chooseMoveRestrict(View view) {
    moveRestrictDialog.showDialog(new DialogMultiSelectListener() {
      @Override
      public void onOK() {
        TextView textView = (TextView) findViewById(R.id.move_restrict);
        textView.setText(Utils.toCSV(unit.getMoveRestrict()));
      }
    });
  }

  /**
   * Called when the user clicks on the pencil to edit the sight restrictions
   */
  public void chooseSightRestrict(View view) {
    sightRestrictDialog.showDialog(new DialogMultiSelectListener() {
      @Override
      public void onOK() {
        TextView textView = (TextView) findViewById(R.id.sight_restrict);
        textView.setText(Utils.toCSV(unit.getSightRestrict()));
      }
    });
  }

  public void editAbilities(View view) {
    save(view);
    Log.d("unitEdit", "Edit abilities");
    Intent myIntent = new Intent(UnitEditActivity.this, AbilityChooserActivity.class);
    myIntent.putExtra(AbilityChooserActivity.SELECTED_UNIT_ID, unit.getId());
    startActivity(myIntent);
  }

  /**
   * Goes to the edit defence view
   *  - Saves unit in the game
   *  - The defence editor will load the unit, modify it and save it
   *  - Then the defence editor will reload the unit edit screen
   */
  public void editDefence(View view) {
    save(view);
    Intent myIntent = new Intent(UnitEditActivity.this, DefenceListActivity.class);
    myIntent.putExtra(DefenceListActivity.SELECTED_UNIT_ID, unit.getId());
    startActivity(myIntent);
    Log.d("unitEdit", "goto defence list");
  }

  public void editEffects(View view) {
    Log.d("unitEdit", "Edit effects");
    effectDialog.showDialog(new DialogMultiSelectListener() {
      @Override
      public void onOK() {
        TextView textView = (TextView) findViewById(R.id.effects);
        textView.setText(GameUtils.tileTypeToCSV(unitEffects));
      }
    });
  }

  public void editStorage(View view) {
    // TODO
    Log.d("unitEdit", "Edit storage");
  }

}
