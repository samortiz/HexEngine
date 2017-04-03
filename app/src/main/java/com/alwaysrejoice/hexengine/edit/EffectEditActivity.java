package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.Effect;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;
import java.util.Map;

public class EffectEditActivity extends Activity {
  public static final String SELECTED_EFFECT = "SELECTED_EFFECT";
  Effect effect; // The effect we are currently editing (Is the Effect)
  String origName = "";  // name when the edit screen was first invoked
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
      effect = GameUtils.jsonToEffectTile(effectJson);
      Log.d("effectEdit", "Tile chosen! " + effect.getName());
    }

    // Load the effect we are editing from the game
    String effectName = (String) bundle.get(EffectEditActivity.SELECTED_EFFECT);
    if ((effectName != null) && !"".equals(effectName)) {
      Game game = GameUtils.getGame();
      effect = game.getEffects().get(effectName);
      origName = effectName;
      Log.d("effectEdit", "begin editing selected effect "+effectName);
    }

    if (effect == null) {
      effect = new Effect();
    }

    // Check if we have a List<Action> returned from ActionListActivity
    String listActionJson = (String) bundle.get(ActionListActivity.ACTION_LIST);
    if (listActionJson != null) {
      List<Action> actions = GameUtils.jsonToActionList(listActionJson);
      effect.setOnRun(actions);
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
    Map<String, Effect> effectTiles = game.getEffects();

    if ("".equals(effect.getName())) {
      showError("You must enter a name.");
      hasError = true;
    }

    // TODO: If name is changing update all the links
    if (!hasError) {
      effectTiles.remove(origName);
      effectTiles.put(effect.getName(), effect);
      GameUtils.saveGame();
      Log.d("effectEdit", "saving Effect name=" + effect.getName() + " orig=" + origName);
    }
  }

  /**
   * Called when the user clicks on the bitmap to change it
   */
  public void chooseBitmap(View view) {
    saveData();
    if (!hasError) {
      Intent myIntent = new Intent(EffectEditActivity.this, ImagePickerActivity.class);
      myIntent.putExtra(ImagePickerActivity.EXTRA_RETURN, ImagePickerActivity.RETURN_EFFECT);
      myIntent.putExtra(ImagePickerActivity.EXTRA_TILE, GameUtils.toJson(effect));
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
      myIntent.putExtra(ActionListActivity.RETURN_LOC, ActionListActivity.RETURN_LOC_EFFECT);
      myIntent.putExtra(ActionListActivity.CALLING_OBJ, GameUtils.toJson(effect));
      myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(effect.getOnRun()));
      startActivity(myIntent);
    }
  }

  /**
   * Loads the information from the UI input components into the effect
   */
  private void loadEffectFromUi() {
    EditText nameInput = (EditText) findViewById(R.id.effect_name);
    EditText durationInput = (EditText) findViewById(R.id.effect_duration);
    effect.setName(nameInput.getText().toString().trim());
    try {
      effect.setDuration(Integer.parseInt(durationInput.getText().toString()));
    } catch (Exception e) {
      showError(durationInput.getText()+" is not a valid integer.");
    }
  }

  /**
   * Updates the UI to match the data in the effect
   */
  private void setUiFromEffect() {
    ImageView imgInput = (ImageView) findViewById(R.id.effect_img);
    EditText nameInput = (EditText) findViewById(R.id.effect_name);
    EditText durationInput = (EditText) findViewById(R.id.effect_duration);
    TextView onRun = (TextView) findViewById(R.id.effect_onrun);

    if (effect.getBitmap() != null) {
      imgInput.setImageBitmap(effect.getBitmap());
      imgInput.getLayoutParams().height = 200;
      imgInput.setScaleType(ImageView.ScaleType.FIT_CENTER);
      imgInput.setAdjustViewBounds(true);
      imgInput.setCropToPadding(false);
      imgInput.requestLayout();
    }
    nameInput.setText(effect.getName());
    durationInput.setText(Integer.toString(effect.getDuration()));
    String onRunText = "";
    if (effect.getOnRun() != null) {
      for (Action action : effect.getOnRun()) {
        onRunText += " "+action.getModName();
      }
    }
    onRun.setText(onRunText);

  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
    hasError = true;
  }

}
