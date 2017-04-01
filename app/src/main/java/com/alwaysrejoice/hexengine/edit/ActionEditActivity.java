package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.ModParam;
import com.alwaysrejoice.hexengine.dto.ModParamValue;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    returnLoc = (String) bundle.get(ActionListActivity.RETURN_LOC);
    callingObj = (String) bundle.get(ActionListActivity.CALLING_OBJ);
    String actionListJson = (String) bundle.get(ActionListActivity.ACTION_LIST);
    actionList = GameUtils.jsonToActionList(actionListJson);

    actionIndex = Integer.parseInt(bundle.get(SELECTED_ACTION_INDEX).toString());
    if (actionIndex >= 0) {
      action = actionList.get(actionIndex);
    } else {
      action = new Action();
      actionList.add(action);
    }

    List<String> modNames = new ArrayList();
    for (String modName : GameUtils.getGame().getMods().keySet()) {
      modNames.add(modName);
    }
    Collections.sort(modNames, String.CASE_INSENSITIVE_ORDER);

    Spinner modSpinner = (Spinner) findViewById(R.id.mod_name_spinner);
    ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modNames);
    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    modSpinner.setAdapter(typeAdapter);
    int selectedModTypeIndex = typeAdapter.getPosition(action.getModName());
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
    String modName = typeAdapter.getItem(typeSpinner.getSelectedItemPosition());
    Mod mod = GameUtils.getGame().getMods().get(modName);

    TableLayout inputTable = (TableLayout) findViewById(R.id.input_table);
    TableRow modNameRow = (TableRow) findViewById(R.id.mod_name_row);
    TableLayout.LayoutParams tableParams = (TableLayout.LayoutParams) modNameRow.getLayoutParams();
    TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

    // Clear the added rows (leave the first row with the spinner)
    inputViews.clear();
    int addedRowCount = inputTable.getChildCount();
    if (addedRowCount > 1) {
      inputTable.removeViews(1, addedRowCount-1);
    }
    int sp5 = toPixel(TypedValue.COMPLEX_UNIT_SP, 5);
    Map<String, ModParamValue> values = action.getValues();

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
        if (value.getValue() != null) {
          valueStr = value.getValue().toString();
        }
        editText.setText(valueStr);
        if (ModParam.TYPE.String == paramType) {
          editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (ModParam.TYPE.Number == paramType) {
          editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (ModParam.TYPE.Integer == paramType) {
          editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        inputViews.put(var, editText);
        tableRow.addView(editText);

      } else if (ModParam.TYPE.Boolean == paramType) {
        CheckBox checkbox = new CheckBox(typeSpinner.getContext());
        checkbox.setLayoutParams(rowParams);// TableRow is the parent view
        if (value.getValue() != null) {
          checkbox.setChecked((Boolean) value.getValue());
        }
        inputViews.put(var, checkbox);
        tableRow.addView(checkbox);

      } else if (ModParam.TYPE.Damage == paramType) {
        // TODO Layout damage
        Log.d("actionEdit", "damage");
      }
    }

    Log.d("actionEdit", "Updating the layout");
  }

  private int toPixel(int unit, float size) {
    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    return (int)TypedValue.applyDimension(unit, size, metrics);
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    Spinner typeSpinner = (Spinner) findViewById(R.id.mod_name_spinner);
    ArrayAdapter<String> typeAdapter = (ArrayAdapter<String>)typeSpinner.getAdapter();
    String modName = typeAdapter.getItem(typeSpinner.getSelectedItemPosition());
    action.setModName(modName);

    // Load the selected param values
    Map<String, ModParamValue> values = action.getValues();
    for (String var : values.keySet()) {
      ModParamValue paramValue = values.get(var);
      View inputView = inputViews.get(var);
      if (inputView != null) {
        Object value = null;
        if ((ModParam.TYPE.String == paramValue.getType()) ||
            (ModParam.TYPE.Number == paramValue.getType()) ||
            (ModParam.TYPE.Integer == paramValue.getType())) {
          value = ((EditText) inputView).getText().toString().trim();
        } else if (ModParam.TYPE.Boolean == paramValue.getType()) {
          value = new Boolean(((CheckBox)inputView).isChecked());
        } else if (ModParam.TYPE.Damage == paramValue.getType()) {

          Log.d("", "TODO Save damage");

        } else {
          Log.d("actionEdit", "Unknown type :"+paramValue.getType());
        }
        paramValue.setValue(value);
      } else {
        paramValue.setValue(null);
        Log.d("actionEdit", "InputView is null var="+var);
      }
    } // for

    if ("".equals(action.getModName())) {
      showError("You must choose a mod.");
      return;
    }
    Log.d("actionEdit", "Done with Action modName="+action.getModName());

    // Go to the action list (with all the required data to restore it's state passed)
    Intent myIntent = new Intent(ActionEditActivity.this, ActionListActivity.class);
    myIntent.putExtra(ActionListActivity.RETURN_LOC, returnLoc);
    myIntent.putExtra(ActionListActivity.CALLING_OBJ, callingObj);
    myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(actionList));
    startActivity(myIntent);
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
