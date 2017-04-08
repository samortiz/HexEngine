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
import com.alwaysrejoice.hexengine.dto.Ability;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.Effect;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.DialogMultiSelect;
import com.alwaysrejoice.hexengine.util.DialogMultiSelectListener;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.alwaysrejoice.hexengine.edit.ActionListActivity.ACTION_LIST;
import static com.alwaysrejoice.hexengine.edit.ActionListActivity.CALLING_OBJ;

public class AbilityEditActivity extends Activity {
  public static final String SELECTED_ABILITY_ID = "SELECTED_ABILITY_ID";
  Ability ability; // The ability we are currently editing
  DialogMultiSelect rangeRestrictDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("abilityEdit", "onCreate");
    setContentView(R.layout.ability_edit);
    Bundle bundle = getIntent().getExtras();
    Game game = GameUtils.getGame();

    // Check if we are coming back from an ImagePickerActivity
    String abilityJson = (String) bundle.get(ImagePickerActivity.EXTRA_TILE);
    if (abilityJson != null) {
      ability = GameUtils.jsonToAbility(abilityJson);
      Log.d("abilityEdit", "Tile chosen! " + ability.getName());
    }

    // Check if we are coming back from choosing Actions for onStart
    String abilityId = (String) bundle.get(CALLING_OBJ);
    // Load the ability we are editing (if one is selected from the ability list)
    if (abilityId == null) {
      abilityId = (String) bundle.get(AbilityEditActivity.SELECTED_ABILITY_ID);
    }
    if ((abilityId != null) && !"".equals(abilityId)) {
      ability = game.getAbilities().get(abilityId);
      Log.d("abilityEdit", "begin editing selected ability "+ability.getName());
    }

    // No ability loaded create a new one
    if (ability == null) {
      ability = new Ability(Utils.generateUniqueId());
    }

    String returnLoc = (String) bundle.get(ActionListActivity.RETURN_LOC);
    // Check if we have a List<Action> returned from ActionListActivity (onStart)
    String listActionJson = (String) bundle.get(ACTION_LIST);
    if (listActionJson != null) {
      List<Action> actions = GameUtils.jsonToActionList(listActionJson);
      if (ActionListActivity.RETURN_LOC_ABILITY.equals(returnLoc)) {
        ability.setOnStart(actions);
      } else if (ActionListActivity.RETURN_LOC_ABILITY_APPLIES.equals(returnLoc)) {
        ability.setApplies(actions.get(0));
      } else {
        Log.e("abilityEdit", "Error, found an actionList but of an unknown returnLoc="+returnLoc);
      }
    }

    List<String> effectNames = new ArrayList<>();
    effectNames.add("None");
    for (String effectId : game.getEffects().keySet()) {
      effectNames.add(game.getEffects().get(effectId).getName());
    }
    Utils.setupSpinner((Spinner)findViewById(R.id.effect_spinner), effectNames);

    rangeRestrictDialog = new DialogMultiSelect(this, "Range Restrictions", game.getBgTypes(), ability.getRangeRestrict());

    // Load the UI with the ability data
    setUiFromAbility();
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    loadAbilityFromUi();
    Game game = GameUtils.getGame();
    Map<String, Ability> abilities = game.getAbilities();
    abilities.put(ability.getId(), ability);
    GameUtils.saveGame();
    Log.d("abilityEdit", "saved Ability name="+ability.getName());
    // Go to the list
    Intent myIntent = new Intent(AbilityEditActivity.this, AbilityListActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on the bitmap to change it
   */
  public void chooseBitmap(View view) {
    loadAbilityFromUi();
    Intent myIntent = new Intent(AbilityEditActivity.this, ImagePickerActivity.class);
    myIntent.putExtra(ImagePickerActivity.EXTRA_RETURN, ImagePickerActivity.RETURN_ABILITY);
    myIntent.putExtra(ImagePickerActivity.EXTRA_TILE, GameUtils.toJson(ability));
    startActivity(myIntent);
  }

  /**
   * Loads the information from the UI input components into the ability
   */
  private void loadAbilityFromUi() {
    // bitmap is set by the ImagePicker

    EditText nameInput = (EditText) findViewById(R.id.ability_name);
    ability.setName(nameInput.getText().toString().trim());

    EditText rangeInput = (EditText) findViewById(R.id.range_input);
    ability.setRange(Utils.stringToInt(rangeInput.getText().toString().trim()));

    // RangeRestrict is set by the dialog

    EditText actionInput = (EditText) findViewById(R.id.action_cost_input);
    ability.setActionCost(Utils.stringToDouble(actionInput.getText().toString().trim()));

    ability.setEffectId(null);
    String effectName = Utils.getSpinnerValue((Spinner)findViewById(R.id.effect_spinner));
    Map<String, Effect> effects = GameUtils.getGame().getEffects();
    for (String effectId : effects.keySet()) {
      Effect effect = effects.get(effectId);
      if (effect.getName().equals(effectName)) {
        ability.setEffectId(effect.getId());
      }
    } // for

  }

  /**
   * Updates the UI to match the data in the ability
   */
  private void setUiFromAbility() {
    ImageView imgInput = (ImageView) findViewById(R.id.ability_img);
    if (ability.getBitmap() != null) {
      imgInput.setImageBitmap(ability.getBitmap());
      imgInput.getLayoutParams().height = 200;
      imgInput.setScaleType(ImageView.ScaleType.FIT_CENTER);
      imgInput.setAdjustViewBounds(true);
      imgInput.setCropToPadding(false);
      imgInput.requestLayout();
    }

    EditText nameInput = (EditText) findViewById(R.id.ability_name);
    nameInput.setText(ability.getName());

    TextView appliesText = (TextView) findViewById(R.id.applies);
    Action applies = ability.getApplies();
    if (applies != null) {
      appliesText.setText(GameUtils.getModDisplayFromId(applies.getModId()));
    }

    EditText rangeInput = (EditText) findViewById(R.id.range_input);
    rangeInput.setText(Utils.intToString(ability.getRange()));

    TextView rangeRestrictText = (TextView) findViewById(R.id.range_restrict);
    rangeRestrictText.setText(Utils.toCSV(ability.getRangeRestrict()));

    EditText actionInput = (EditText) findViewById(R.id.action_cost_input);
    actionInput.setText(Utils.doubleToString(ability.getActionCost()));

    TextView onStart = (TextView) findViewById(R.id.on_start);
    onStart.setText(GameUtils.actionsToCSV(ability.getOnStart()));

    if (ability.getEffectId() != null) {
      Utils.setSpinnerValue((Spinner) findViewById(R.id.effect_spinner),
          GameUtils.getGame().getEffects().get(ability.getEffectId()).getName());
    }

  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
  }

  /**
   * Called when the user clicks on the pencil to edit the range restrictions
   */
  public void chooseRangeRestrict(View view) {
    rangeRestrictDialog.showDialog(new DialogMultiSelectListener() {
      @Override
      public void onOK() {
        TextView textView = (TextView) findViewById(R.id.range_restrict);
        textView.setText(Utils.toCSV(ability.getRangeRestrict()));
      }
    });
  }

  public void editApplies(View view) {
    Log.d("abilityEdit", "Edit Applies");
    save(view);
    ArrayList<Action> actions = new ArrayList<>();
    if (ability.getApplies() != null) {
      actions.add(ability.getApplies());
    }

    Intent myIntent = new Intent(AbilityEditActivity.this, ActionEditActivity.class);
    myIntent.putExtra(ActionEditActivity.SELECTED_ACTION_INDEX, Integer.toString(actions.size() -1));
    myIntent.putExtra(ActionListActivity.RETURN_LOC, ActionListActivity.RETURN_LOC_ABILITY_APPLIES);
    myIntent.putExtra(ActionListActivity.CALLING_OBJ, ability.getId());
    myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(actions));
    startActivity(myIntent);
  }

  public void editOnStart(View view) {
    save(view);
    Intent myIntent = new Intent(AbilityEditActivity.this, ActionListActivity.class);
    myIntent.putExtra(ActionListActivity.RETURN_LOC, ActionListActivity.RETURN_LOC_ABILITY);
    myIntent.putExtra(ActionListActivity.CALLING_OBJ, ability.getId());
    myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(ability.getOnStart()));
    startActivity(myIntent);
  }

}
