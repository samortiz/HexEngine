package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.Damage;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.ModParam;
import com.alwaysrejoice.hexengine.dto.ModParamValue;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alwaysrejoice.hexengine.edit.ActionListActivity.RETURN_LOC_EFFECT_ONEND;
import static com.alwaysrejoice.hexengine.edit.ActionListActivity.RETURN_LOC_EFFECT_ONRUN;
import static com.alwaysrejoice.hexengine.util.Utils.doubleToString;
import static com.alwaysrejoice.hexengine.util.Utils.intToString;

public class ActionEditActivity extends Activity {
  // in - the currently selected action index (in actionList)
  public static final String SELECTED_ACTION_INDEX = "SELECTED_ACTION_INDEX";

  // NOTE : This method will also receive and return these
  // ActionListActivity extras : RETURN_LOC CALLING_OBJ ACTION_LIST
  String returnLoc;
  String callingObj;

  // This method will update the action list
  List<Action> actionList;
  int actionIndex = -1; // index into actionList;
  Action action; // The action we are currently editing

  Map<String, View> inputViews = new HashMap<>(); // Contains the input view objects currently on the screen

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("actionEdit", "onCreate");
    setContentView(R.layout.action_edit);
    Bundle bundle = getIntent().getExtras();
    Game game = GameUtils.getGame();

    returnLoc = (String) bundle.get(ActionListActivity.RETURN_LOC);
    callingObj = (String) bundle.get(ActionListActivity.CALLING_OBJ);
    String actionListJson = (String) bundle.get(ActionListActivity.ACTION_LIST);
    actionList = GameUtils.jsonToActionList(actionListJson);

    actionIndex = Integer.parseInt(bundle.get(SELECTED_ACTION_INDEX).toString());
    if (actionIndex >= 0) {
      action = actionList.get(actionIndex);
    } else {
      action = new Action(Utils.generateUniqueId());
      actionList.add(action);
    }

    List<String> modDisplayStrs = new ArrayList();
    for (String modId : game.getMods().keySet()) {
      Mod mod = game.getMods().get(modId);
      String modType = mod.getType();
      if (ActionListActivity.RETURN_LOC_ABILITY_APPLIES.equals(returnLoc)) {
        // For applies we filter for rules
        if (Mod.TYPE_RULE.equals(modType) || Mod.TYPE_RULE_LOC.equals(modType)) {
          modDisplayStrs.add(mod.getDisplayString());
        }
      } else if (ActionListActivity.RETURN_LOC_TRIGGER_START_WORLD.equals(returnLoc) ||
                 ActionListActivity.RETURN_LOC_TRIGGER_START_TURN.equals(returnLoc) ||
                 ActionListActivity.RETURN_LOC_TRIGGER_END_TURN.equals(returnLoc) ||
                 ActionListActivity.RETURN_LOC_TRIGGER_ABILITY_USED.equals(returnLoc)) {
        if (Mod.TYPE_TRIGGER.equals(modType)) {
          // Filter for triggers
          modDisplayStrs.add(mod.getDisplayString());
        }
      } else if (RETURN_LOC_EFFECT_ONRUN.equals(returnLoc) ||
                 RETURN_LOC_EFFECT_ONEND.equals(returnLoc)) {
        // filter for mods
        if (Mod.TYPE_MOD.equals(modType) || Mod.TYPE_MOD_LOC.equals(modType)) {
          modDisplayStrs.add(mod.getDisplayString());
        }
      } else {
        Log.e("ActionEditActivity", "Error! Unknown returnLoc="+returnLoc);
      }
    }
    Collections.sort(modDisplayStrs, String.CASE_INSENSITIVE_ORDER);
    Log.d("ActionEdit", "modDisplayStrs="+modDisplayStrs);
    Spinner modSpinner = (Spinner) findViewById(R.id.mod_name_spinner);
    ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modDisplayStrs);
    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    modSpinner.setAdapter(typeAdapter);
    Mod selectedMod = game.getMods().get(action.getModId());
    String selectedModDisplayStr = "";
    if (selectedMod != null) {
      selectedModDisplayStr = selectedMod.getDisplayString();
    }
    int selectedModTypeIndex = typeAdapter.getPosition(selectedModDisplayStr);
    if (selectedModTypeIndex < 0) {
      selectedModTypeIndex = 0;
    }
    modSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        updateLayout();
      }
      public void onNothingSelected(AdapterView<?> parentView) { }
    });
    modSpinner.setSelection(selectedModTypeIndex);
  }

  // Updates the current layout to match the selected Mod
  public void updateLayout() {
    Spinner typeSpinner = (Spinner) findViewById(R.id.mod_name_spinner);
    ArrayAdapter<String> typeAdapter = (ArrayAdapter<String>)typeSpinner.getAdapter();
    String modDisplayString = typeAdapter.getItem(typeSpinner.getSelectedItemPosition());
    Log.d("actionEdit", "modDispStr="+modDisplayString);
    Mod mod = GameUtils.getModByDisplayString(modDisplayString);
    LayoutInflater inflator = LayoutInflater.from(getBaseContext());
    TableLayout inputTable = (TableLayout) findViewById(R.id.input_table);
    TableRow modNameRow = (TableRow) findViewById(R.id.mod_name_row);
    TableLayout.LayoutParams tableParams = (TableLayout.LayoutParams) modNameRow.getLayoutParams();
    TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
    List<String> damageTypes = GameUtils.getGame().getDamageTypes();

    // Clear the added rows (leave the first row with the spinner)
    inputViews.clear();
    int addedRowCount = inputTable.getChildCount();
    if (addedRowCount > 1) {
      inputTable.removeViews(1, addedRowCount-1);
    }
    int sp5 = Utils.spToPixel(5);
    Map<String, ModParamValue> values = action.getParamValues();

    // Add rows for the params
    for (ModParam param : mod.getParams()) {
      String var = param.getVar();
      ModParam.TYPE paramType = param.getType();
      ModParamValue value = values.get(var);
      // No value found, create one
      if (value == null) {
         value = new ModParamValue(var, paramType);
        values.put(var, value);
      }

      TableRow tableRow = new TableRow(typeSpinner.getContext());
      tableRow.setLayoutParams(tableParams);// TableLayout is the parent view
      inputTable.addView(tableRow);
      // Label
      TextView textView = new TextView(typeSpinner.getContext());
      textView.setText(var);
      textView.setLayoutParams(rowParams);// TableRow is the parent view
      textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
      textView.setPadding(sp5, sp5, sp5, sp5);
      tableRow.addView(textView);
      // Input
      if ((ModParam.TYPE.Integer == paramType) ||
          (ModParam.TYPE.Number == paramType) ||
          (ModParam.TYPE.String == paramType)) {
        EditText editText = new EditText(typeSpinner.getContext());
        editText.setLayoutParams(rowParams);// TableRow is the parent view
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        editText.setHint(var);
        String valueStr = "";
        if (ModParam.TYPE.String == paramType) {
          editText.setInputType(InputType.TYPE_CLASS_TEXT);
          valueStr = value.getValueString();
        } else if (ModParam.TYPE.Number == paramType) {
          editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
          valueStr = doubleToString(value.getValueDouble());
        } else if (ModParam.TYPE.Integer == paramType) {
          editText.setInputType(InputType.TYPE_CLASS_NUMBER);
          valueStr = intToString(value.getValueInt());
        }
        editText.setText(valueStr);
        inputViews.put(var, editText);
        tableRow.addView(editText);

      } else if (ModParam.TYPE.Boolean == paramType) {
        CheckBox checkbox = new CheckBox(typeSpinner.getContext());
        checkbox.setLayoutParams(rowParams);// TableRow is the parent view
        checkbox.setChecked(value.getValueBoolean());
        inputViews.put(var, checkbox);
        tableRow.addView(checkbox);

      } else if (ModParam.TYPE.Damage == paramType) {
        Damage dmg = value.getValueDamage();
        View damageView = inflator.inflate(R.layout.action_damage_row, null);
        Spinner damageTypeSpinner = (Spinner) damageView.findViewById(R.id.damage_type_spinner);
        ArrayAdapter<String> dmgAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, damageTypes);
        dmgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        damageTypeSpinner.setAdapter(dmgAdapter);
        int selectedDamageTypeIndex = dmgAdapter.getPosition(dmg.getType());
        if (selectedDamageTypeIndex < 0) {
          selectedDamageTypeIndex = 0;
        }
        damageTypeSpinner.setSelection(selectedDamageTypeIndex);
        EditText countInput = (EditText) damageView.findViewById(R.id.count_input);
        countInput.setText(intToString(dmg.getCount()));
        EditText sizeInput = (EditText) damageView.findViewById(R.id.size_input);
        sizeInput.setText(intToString(dmg.getSize()));
        EditText bonusInput = (EditText) damageView.findViewById(R.id.bonus_input);
        bonusInput.setText(doubleToString(dmg.getBonus()));

        inputViews.put(var, damageView);
        tableRow.addView(damageView);
      }
    }

    Log.d("actionEdit", "Updating the layout");
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    Spinner typeSpinner = (Spinner) findViewById(R.id.mod_name_spinner);
    ArrayAdapter<String> typeAdapter = (ArrayAdapter<String>)typeSpinner.getAdapter();
    String modDisplayString = typeAdapter.getItem(Math.max(0, typeSpinner.getSelectedItemPosition()));
    String modId = GameUtils.getModIdByDisplayString(modDisplayString);
    action.setModId(modId);

    // Load the selected param values
    Map<String, ModParamValue> values = action.getParamValues();
    for (String var : values.keySet()) {
      ModParamValue paramValue = values.get(var);
      View inputView = inputViews.get(var);
      if (inputView != null) {
        if (ModParam.TYPE.String == paramValue.getType()) {
          String value = ((EditText) inputView).getText().toString().trim();
          paramValue.setValueString(value);
        } else if (ModParam.TYPE.Number == paramValue.getType()) {
          String value = ((EditText) inputView).getText().toString().trim();
          paramValue.setValueDouble(Utils.stringToDouble(value));
        } else if (ModParam.TYPE.Integer == paramValue.getType()) {
          String value = ((EditText) inputView).getText().toString().trim();
          paramValue.setValueInt(Utils.stringToInt(value));
        } else if (ModParam.TYPE.Boolean == paramValue.getType()) {
          paramValue.setValueBoolean(((CheckBox)inputView).isChecked());
        } else if (ModParam.TYPE.Damage == paramValue.getType()) {
          EditText count = (EditText) inputView.findViewById(R.id.count_input);
          EditText size = (EditText) inputView.findViewById(R.id.size_input);
          EditText bonus = (EditText) inputView.findViewById(R.id.bonus_input);
          Spinner dmgTypeSpinner = (Spinner) inputView.findViewById(R.id.damage_type_spinner);
          ArrayAdapter<String> dmgAdapter = (ArrayAdapter<String>)dmgTypeSpinner.getAdapter();
          Damage dmg = (Damage) paramValue.getValueDamage();
          dmg.setCount(Utils.stringToInt(count.getText().toString()));
          dmg.setSize(Utils.stringToInt(size.getText().toString()));
          dmg.setBonus(Utils.stringToDouble(bonus.getText().toString()));
          dmg.setType(dmgAdapter.getItem(dmgTypeSpinner.getSelectedItemPosition()));
        } else {
          Log.e("actionEdit", "Unknown type :"+paramValue.getType());
        }
      } else {
        Log.e("actionEdit", "Error! Cannot save, because inputView is null! var="+var);
      }
    } // for

    if ("".equals(modId)) {
      showError("You must choose a mod.");
      return;
    }
    Log.d("actionEdit", "Saved Action "+action);
    Log.d("", "actionList = "+actionList);

    if (ActionListActivity.RETURN_LOC_ABILITY_APPLIES.equals(returnLoc)) {
      // Go to the abilityTile editor (we are choosing an applies rule)
      Intent myIntent = new Intent(ActionEditActivity.this, AbilityEditActivity.class);
      myIntent.putExtra(ActionListActivity.RETURN_LOC, returnLoc);
      myIntent.putExtra(ActionListActivity.CALLING_OBJ, callingObj);
      myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(actionList));
      startActivity(myIntent);
    } else {
      // Go to the action list (with all the required data to restore it's state passed)
      Intent myIntent = new Intent(ActionEditActivity.this, ActionListActivity.class);
      myIntent.putExtra(ActionListActivity.RETURN_LOC, returnLoc);
      myIntent.putExtra(ActionListActivity.CALLING_OBJ, callingObj);
      myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(actionList));
      startActivity(myIntent);
    }
  }

  /**
   * Display an error to the user
   */
  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
  }

}
