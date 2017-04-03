package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.ModParam;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.Map;

import static com.alwaysrejoice.hexengine.util.Utils.setListViewHeight;

public class ModEditActivity extends Activity {
  public static final String SELECTED_MOD = "SELECTED_MOD";
  public static final String[] MOD_TYPES = {"Mod", "Rule", "ModLoc", "RuleLoc"};

  Mod mod; // The mod we are currently editing
  String origModName = "";  // name when the edit screen was first invoked
  int typeSelectedIndex = 0; // which type is currently selected

  ListView paramList;
  ModParamListAdapter paramAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("modEdit", "onCreate");
    setContentView(R.layout.mod_edit);
    Bundle bundle = getIntent().getExtras();

    Spinner modTypeSpinner = (Spinner) findViewById(R.id.mod_type_spinner);
    ArrayAdapter<String> modTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MOD_TYPES);
    modTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    modTypeSpinner.setAdapter(modTypeAdapter);

    // Load the mod we are editing
    String modName = (String) bundle.get(ModEditActivity.SELECTED_MOD);
    if ((modName != null) && !"".equals(modName)) {
      Game game = GameUtils.getGame();
      mod = game.getMods().get(modName);
      setUiFromMod();
      origModName = modName;
      Log.d("modEdit", "begin editing selected mod "+modName);
    }

    if (mod == null) {
      mod = new Mod();
      typeSelectedIndex = 0;
    }

    // Setup the params list
    paramAdapter = new ModParamListAdapter(this, mod.getParams());
    paramList = (ListView) findViewById(R.id.param_list);
    paramList.setAdapter(paramAdapter);
    Utils.setListViewHeight(paramList);

    Spinner typeSpinner = (Spinner) findViewById(R.id.add_param_type);
    ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ModParam.getTypesAsString());
    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    typeSpinner.setAdapter(typeAdapter);

  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    loadModFromUi();
    Game game = GameUtils.getGame();
    Map<String, Mod> modMap = game.getMods();

    if ("".equals(mod.getName())) {
      showError("You must enter a name.");
      return;
    }

    // TODO: If name is changing update all the links

    // Update the mod in the game
    modMap.remove(origModName);
    modMap.put(mod.getName(), mod);
    GameUtils.saveGame();
    Log.d("modEdit", "saving Mod name="+mod.getName()+" type="+mod.getType()+" orig="+ origModName);
    // Go to the list
    Intent myIntent = new Intent(ModEditActivity.this, ModListActivity.class);
    startActivity(myIntent);
  }

  /**
   * Loads the information from the UI input components into the mod
   */
  private void loadModFromUi() {
    EditText nameInput = (EditText) findViewById(R.id.mod_name);
    mod.setName(nameInput.getText().toString().trim());

    Spinner modTypeSpinner = (Spinner) findViewById(R.id.mod_type_spinner);
    ArrayAdapter<String> modTypeAdapter = (ArrayAdapter<String>)modTypeSpinner.getAdapter();
    String modType = modTypeAdapter.getItem(modTypeSpinner.getSelectedItemPosition());
    mod.setType(modType);

    TextView scriptInput = (TextView) findViewById(R.id.mod_script);
    mod.setScript(scriptInput.getText().toString());
  }

  /**
   * Updates the UI to match the data in the mod
   */
  private void setUiFromMod() {
    EditText nameInput = (EditText) findViewById(R.id.mod_name);
    nameInput.setText(mod.getName());

    Spinner modTypeSpinner = (Spinner) findViewById(R.id.mod_type_spinner);
    ArrayAdapter<String> modTypeAdapter = (ArrayAdapter<String>)modTypeSpinner.getAdapter();
    int selectedIndex = Math.max(0, modTypeAdapter.getPosition(mod.getType()));
    modTypeSpinner.setSelection(selectedIndex);

    TextView scriptInput = (TextView) findViewById(R.id.mod_script);
    scriptInput.setText(mod.getScript());
  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
  }

  /**
   * When "Add" button is clicked to add a new parameter
   */
  public void addParam(View view) {
    Log.d("modEditActivity", "addParam");
    Spinner typeSpinner = (Spinner) findViewById(R.id.add_param_type);
    EditText varText = (EditText) findViewById(R.id.new_param_var);
    ModParam.TYPE type = ModParam.TYPE.valueOf(typeSpinner.getSelectedItem().toString());
    String var = varText.getText().toString();
    mod.getParams().add(new ModParam(var, type));
    setListViewHeight(paramList); // recalc list height
    typeSpinner.setSelection(0);
    varText.setText("");
    paramAdapter.notifyDataSetChanged();
  }

  /**
   * When delete button is clicked beside a parameter
   */
  public void deleteParam(View view) {
    Log.d("modEditActivity", "deleteParam");
    int position = (int) view.getTag();
    paramAdapter.removeItem(position);
    setListViewHeight(paramList); // recalc list height
    paramAdapter.notifyDataSetChanged();
  }

  public void editParam(View view) {
    Intent myIntent = new Intent(ModEditActivity.this, ModParamEditActivity.class);
    int position = (int) view.getTag();
    ModParam param = paramAdapter.getItem(position);
    myIntent.putExtra(ModParamEditActivity.SELECTED_MOD, mod.getName());
    myIntent.putExtra(ModParamEditActivity.SELECTED_PARAM, param.getVar());
    startActivity(myIntent);
  }

}
