package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.ModParam;
import com.alwaysrejoice.hexengine.util.GameUtils;

public class ModParamEditActivity extends Activity {
  public static final String SELECTED_MOD = "SELECTED_MOD";
  public static final String SELECTED_PARAM = "SELECTED_PARAM";

  Mod mod;
  ModParam param; // The param we are currently editing
  String origParamVar = "";  // param var when the edit screen was first invoked

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("modEdit", "onCreate");
    setContentView(R.layout.mod_param_edit);
    Bundle bundle = getIntent().getExtras();

    // Setup the type spinner
    Spinner typeSpinner = (Spinner) findViewById(R.id.param_type);
    ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ModParam.getTypesAsString());
    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    typeSpinner.setAdapter(typeAdapter);

    // Load the param we are editing
    String modName = (String) bundle.get(ModParamEditActivity.SELECTED_MOD);
    String varName = (String) bundle.get(ModParamEditActivity.SELECTED_PARAM);
    if ((modName != null) && !"".equals(modName) &&
        (varName != null) && !"".equals(varName)) {
      Game game = GameUtils.getGame();
      mod = game.getMods().get(modName);
      for (ModParam p : mod.getParams()) {
        if (varName.equals(p.getVar())) {
          param = p;
          break;
        }
      } // for
      // Set the UI from the param
      EditText varInput = (EditText) findViewById(R.id.var_input);
      varInput.setText(param.getVar());
      typeSpinner.setSelection(typeAdapter.getPosition(param.getType().name()));
      origParamVar = varName;
      Log.d("modParamEdit", "begin editing selected var "+varName);
    }

    if (param == null) {
      param = new ModParam();
    }

  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    EditText varInput = (EditText) findViewById(R.id.var_input);
    String var = varInput.getText().toString();

    if ("".equals(var)) {
      showError("You must enter a variable name.");
      return;
    }

    // If changing the name, the new name must not match any existing param var
    if (!var.equals(origParamVar)) {
      for (ModParam p : mod.getParams()) {
        if (var.equals(p.getVar())) {
          showError("The variable " + var + " already exists.");
          return;
        }
      } // for
    }

    Spinner typeSpinner = (Spinner) findViewById(R.id.param_type);
    ArrayAdapter<String> typeAdapter = (ArrayAdapter<String>)typeSpinner.getAdapter();
    String type = typeAdapter.getItem(typeSpinner.getSelectedItemPosition());

    param.setVar(var);
    param.setType(ModParam.TYPE.valueOf(type));
    GameUtils.saveGame();
    Log.d("modParamEdit", "saving param var="+var+" type="+type);
    // Go to the mod edit screen
    Intent myIntent = new Intent(ModParamEditActivity.this, ModEditActivity.class);
    myIntent.putExtra(ModEditActivity.SELECTED_MOD, mod.getName());
    startActivity(myIntent);
  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
  }

}
