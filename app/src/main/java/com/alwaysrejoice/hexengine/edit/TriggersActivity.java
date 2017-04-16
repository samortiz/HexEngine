package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.Triggers;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

public class TriggersActivity extends Activity {

  Triggers triggers;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("triggers", "onCreate");
    setContentView(R.layout.triggers_edit);
    Bundle bundle = getIntent().getExtras();
    triggers = GameUtils.getGame().getTriggers();

    // Check if we have a List<Action> returned from ActionListActivity
    if (bundle != null) {
      String listActionJson = (String) bundle.get(ActionListActivity.ACTION_LIST);
      if (listActionJson != null) {
        List<Action> actions = GameUtils.jsonToActionList(listActionJson);
        String returnLoc = (String) bundle.get(ActionListActivity.RETURN_LOC);
        if (ActionListActivity.RETURN_LOC_TRIGGER_START_WORLD.equals(returnLoc)) {
          triggers.setStartWorld(actions);
        } else if (ActionListActivity.RETURN_LOC_TRIGGER_START_TURN.equals(returnLoc)) {
          triggers.setStartTurn(actions);
        } else if (ActionListActivity.RETURN_LOC_TRIGGER_END_TURN.equals(returnLoc)) {
          triggers.setEndTurn(actions);
        } else if (ActionListActivity.RETURN_LOC_TRIGGER_ABILITY_USED.equals(returnLoc)) {
          triggers.setAbilityUsed(actions);
        }
        Log.d("triggers", "found actionList "+actions);
      }
    }
    setUiFromEffect();
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    GameUtils.saveGame();
    Intent myIntent = new Intent(TriggersActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks to edit startWorld
   */
  public void chooseStartWorldAction(View view) {
    Intent myIntent = new Intent(TriggersActivity.this, ActionListActivity.class);
    myIntent.putExtra(ActionListActivity.RETURN_LOC, ActionListActivity.RETURN_LOC_TRIGGER_START_WORLD);
    myIntent.putExtra(ActionListActivity.CALLING_OBJ, "");
    myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(triggers.getStartWorld()));
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks to edit startWorld
   */
  public void chooseStartTurnAction(View view) {
    Intent myIntent = new Intent(TriggersActivity.this, ActionListActivity.class);
    myIntent.putExtra(ActionListActivity.RETURN_LOC, ActionListActivity.RETURN_LOC_TRIGGER_START_TURN);
    myIntent.putExtra(ActionListActivity.CALLING_OBJ, "");
    myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(triggers.getStartTurn()));
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks to edit startWorld
   */
  public void chooseEndTurnAction(View view) {
    Intent myIntent = new Intent(TriggersActivity.this, ActionListActivity.class);
    myIntent.putExtra(ActionListActivity.RETURN_LOC, ActionListActivity.RETURN_LOC_TRIGGER_END_TURN);
    myIntent.putExtra(ActionListActivity.CALLING_OBJ, "");
    myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(triggers.getEndTurn()));
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks to edit startWorld
   */
  public void chooseAbilityUsedAction(View view) {
    Intent myIntent = new Intent(TriggersActivity.this, ActionListActivity.class);
    myIntent.putExtra(ActionListActivity.RETURN_LOC, ActionListActivity.RETURN_LOC_TRIGGER_ABILITY_USED);
    myIntent.putExtra(ActionListActivity.CALLING_OBJ, "");
    myIntent.putExtra(ActionListActivity.ACTION_LIST, GameUtils.toJson(triggers.getAbilityUsed()));
    startActivity(myIntent);
  }

  /**
   * Updates the UI to match the data in triggers
   */
  private void setUiFromEffect() {
    TextView startWorldText = (TextView) findViewById(R.id.trigger_start_world);
    startWorldText.setText(GameUtils.actionsToCSV(triggers.getStartWorld()));

    TextView startTurnText = (TextView) findViewById(R.id.trigger_start_turn);
    startTurnText.setText(GameUtils.actionsToCSV(triggers.getStartTurn()));

    TextView endTurnText = (TextView) findViewById(R.id.trigger_end_turn);
    endTurnText.setText(GameUtils.actionsToCSV(triggers.getEndTurn()));

    TextView abilityUsedText = (TextView) findViewById(R.id.trigger_ability_used);
    abilityUsedText.setText(GameUtils.actionsToCSV(triggers.getAbilityUsed()));
  }

}
