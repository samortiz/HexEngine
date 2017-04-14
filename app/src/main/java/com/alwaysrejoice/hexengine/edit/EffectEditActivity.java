package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.EffectTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.List;
import java.util.Map;

public class EffectEditActivity extends Activity {
  public static final String SELECTED_EFFECT_ID = "SELECTED_EFFECT_ID";
  EffectTile effectTile; // The effectTile we are currently editing (Is the EffectTile)
  boolean hasError = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("effectEdit", "onCreate");
    setContentView(R.layout.effect_edit);
    Bundle bundle = getIntent().getExtras();

    // Check if we are coming back from an ImagePickerActivity
    String effectJson = (String) bundle.get(ImagePickerActivity.EXTRA_TILE);
    if (effectJson == null) {
      // Check if we're coming back from a ActionListActivity
      effectJson = (String) bundle.get(ActionListActivity.CALLING_OBJ);
    }
    if (effectJson != null) {
      effectTile = GameUtils.jsonToEffectTile(effectJson);
      Log.d("effectEdit", "effectTile loaded:"+ effectTile.getName());
    }

    // Load the effectTile we are editing from the game
    String effectId = (String) bundle.get(EffectEditActivity.SELECTED_EFFECT_ID);
    if ((effectId != null) && !"".equals(effectId)) {
      Game game = GameUtils.getGame();
      effectTile = game.getEffects().get(effectId);
      Log.d("effectEdit", "begin editing selected effectTile "+ effectTile.getName());
    }

    if (effectTile == null) {
      effectTile = new EffectTile(Utils.generateUniqueId());
    }

    // Check if we have a List<Action> returned from ActionListActivity
    String listActionJson = (String) bundle.get(ActionListActivity.ACTION_LIST);
    if (listActionJson != null) {
      List<Action> actions = GameUtils.jsonToActionList(listActionJson);
      String returnLoc = (String) bundle.get(ActionListActivity.RETURN_LOC);
      if (ActionListActivity.RETURN_LOC_EFFECT_ONRUN.equals(returnLoc)) {
        effectTile.setOnRun(actions);
      } else {
        effectTile.setOnEnd(actions);
      }
      Log.d("effectEdit", "found actionList "+actions);
    }

    setUiFromEffect();
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    saveData();
    if (!hasError) {
      // Go back to the list
      Intent myIntent = new Intent(EffectEditActivity.this, EffectListActivity.class);
      startActivity(myIntent);
    }
  }

  /**
   * Saves all the data (sets hasError if error an error is found)
   */
  private void saveData() {
    hasError = false; // we will re-validate in this method
    loadEffectFromUi();
    Game game = GameUtils.getGame();
    Map<String, EffectTile> effectTiles = game.getEffects();

    if ("".equals(effectTile.getName())) {
      showError("You must enter a name.");
    }

    if (hasError) {
      return;
    }
    effectTiles.put(effectTile.getId(), effectTile);
    GameUtils.saveGame();
    Log.d("effectEdit", "saving EffectTile name=" + effectTile.getName());
  }

  /**
   * Called when the user clicks on the bitmap to change it
   */
  public void chooseBitmap(View view) {
    saveData();
    if (!hasError) {
      Intent myIntent = new Intent(EffectEditActivity.this, ImagePickerActivity.class);
      myIntent.putExtra(ImagePickerActivity.EXTRA_RETURN, ImagePickerActivity.RETURN_EFFECT);
      myIntent.putExtra(ImagePickerActivity.EXTRA_TILE, GameUtils.toJson(effectTile));
      startActivity(myIntent);
    }
  }

  /**
   * Called when the user clicks to edit onRun
   */
  public void chooseOnRunAction(View view) {
    saveData();
    if (!hasError) {
      Intent myIntent = new Intent(EffectEditActivity.this, ActionListActivity.class);
      myIntent.putExtra(ActionListActivity.RETURN_LOC, ActionListActivity.RETURN_LOC_EFFECT_ONRUN);
      myIntent.putExtra(ActionListActivity.CALLING_OBJ, GameUtils.toJson(effectTile));
      myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(effectTile.getOnRun()));
      startActivity(myIntent);
    }
  }

  /**
   * Called when the user clicks to edit onRun
   */
  public void chooseOnEndAction(View view) {
    saveData();
    if (!hasError) {
      Intent myIntent = new Intent(EffectEditActivity.this, ActionListActivity.class);
      myIntent.putExtra(ActionListActivity.RETURN_LOC, ActionListActivity.RETURN_LOC_EFFECT_ONEND);
      myIntent.putExtra(ActionListActivity.CALLING_OBJ, GameUtils.toJson(effectTile));
      myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(effectTile.getOnEnd()));
      startActivity(myIntent);
    }
  }

  /**
   * Loads the information from the UI input components into the effectTile
   */
  private void loadEffectFromUi() {
    EditText nameInput = (EditText) findViewById(R.id.effect_name);
    EditText durationInput = (EditText) findViewById(R.id.effect_duration);
    CheckBox stackable = (CheckBox) findViewById(R.id.effect_stackable);

    effectTile.setName(nameInput.getText().toString().trim());
    effectTile.setDuration(Utils.stringToInt(durationInput.getText().toString()));
    effectTile.setStackable(stackable.isChecked());
  }

  /**
   * Updates the UI to match the data in the effectTile
   */
  private void setUiFromEffect() {
    ImageView imgInput = (ImageView) findViewById(R.id.effect_img);
    EditText nameInput = (EditText) findViewById(R.id.effect_name);
    EditText durationInput = (EditText) findViewById(R.id.effect_duration);
    TextView onRun = (TextView) findViewById(R.id.effect_onrun);
    TextView onEnd = (TextView) findViewById(R.id.effect_onend);
    CheckBox stackable = (CheckBox) findViewById(R.id.effect_stackable);
    if (effectTile.getBitmap() != null) {
      imgInput.setImageBitmap(effectTile.getBitmap());
      imgInput.getLayoutParams().height = 200;
      imgInput.setScaleType(ImageView.ScaleType.FIT_CENTER);
      imgInput.setAdjustViewBounds(true);
      imgInput.setCropToPadding(false);
      imgInput.requestLayout();
    }
    nameInput.setText(effectTile.getName());
    durationInput.setText(Utils.intToString(effectTile.getDuration()));
    onRun.setText(GameUtils.actionsToCSV(effectTile.getOnRun()));
    onEnd.setText(GameUtils.actionsToCSV(effectTile.getOnEnd()));
    stackable.setChecked(effectTile.isStackable());
  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
    hasError = true;
  }

}
