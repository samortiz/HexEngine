package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;

/**
 * Displays and handles events for the list of games screen
 */
public class ActionListActivity extends Activity implements AdapterView.OnItemClickListener {

  // IN Return location (which calling class to return to)
  public static final String RETURN_LOC = "ACTION_LIST_RETURN_LOC";
  public static final String RETURN_LOC_EFFECT_ONRUN = "RETURN_LOC_EFFECT_ONRUN";
  public static final String RETURN_LOC_EFFECT_ONEND = "RETURN_LOC_EFFECT_ONEND";
  public static final String RETURN_LOC_ABILITY = "RETURN_LOC_ABILITY";
  public static final String RETURN_LOC_ABILITY_APPLIES = "RETURN_LOC_ABILITY_APPLIES";
  public static final String RETURN_LOC_TRIGGER_START_WORLD = "RETURN_LOC_TRIGGER_START_WORLD";
  public static final String RETURN_LOC_TRIGGER_START_TURN = "RETURN_LOC_TRIGGER_START_TURN";
  public static final String RETURN_LOC_TRIGGER_END_TURN = "RETURN_LOC_TRIGGER_END_TURN";
  public static final String RETURN_LOC_TRIGGER_ABILITY_USED = "RETURN_LOC_TRIGGER_ABILITY_USED";

  // IN+OUT calling class object (will be passed back to caller as is)
  public static final String CALLING_OBJ = "ACTION_CALLING_OBJ";

  // IN+OUT value of actions chosen
  public static final String ACTION_LIST = "ACTION_LIST";

  ListView list;
  ActionListAdapter adapter;
  ArrayList<Action> actions;
  String callingObjJson;
  String returnLoc;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.action_list);
    Bundle bundle = getIntent().getExtras();

    list = (ListView) findViewById(R.id.action_list_view);
    String actionListJson = (String) bundle.get(ACTION_LIST);
    if (actionListJson != null) {
      actions = GameUtils.jsonToActionList(actionListJson);
    } else {
      actions = new ArrayList<>();
    }

    // Inputs
    callingObjJson = (String) bundle.get(CALLING_OBJ);
    returnLoc = (String) bundle.get(RETURN_LOC);
    if (returnLoc == null) {
      throw new RuntimeException("Error! You must specify a RETURN_LOC when calling ActionListActivity");
    }

    adapter = new ActionListAdapter(this, actions);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("actionList", "onItemClick ");
    Action action = (Action) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(ActionListActivity.this, ActionEditActivity.class);
    myIntent.putExtra(ActionEditActivity.SELECTED_ACTION_INDEX, Integer.toString(position));
    // These will be passed back to maintain the state of this class
    myIntent.putExtra(RETURN_LOC, returnLoc);
    myIntent.putExtra(CALLING_OBJ, callingObjJson);
    myIntent.putExtra(ACTION_LIST, GameUtils.toJson(actions));
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    Game game = GameUtils.getGame();
    int position = (int) view.getTag();
    Action action = (Action) list.getItemAtPosition(position);
    adapter.removeItem(position);
    adapter.notifyDataSetChanged();
    Log.d("actionList", "deleted "+action.getModId());
  }

  /**
   * Called when the user clicks Settings
   */
  public void goBack(View view) {
    Log.d("actionList", "goto Settings");
    Intent myIntent = null;
    if (RETURN_LOC_EFFECT_ONRUN.equals(returnLoc) ||
        RETURN_LOC_EFFECT_ONEND.equals(returnLoc)) {
      myIntent = new Intent(ActionListActivity.this, EffectEditActivity.class);
      myIntent.putExtra(CALLING_OBJ, callingObjJson);
      myIntent.putExtra(ACTION_LIST, GameUtils.toJson(actions));
      myIntent.putExtra(RETURN_LOC, returnLoc);
    } else if (RETURN_LOC_ABILITY.equals(returnLoc)) {
      myIntent = new Intent(ActionListActivity.this, AbilityEditActivity.class);
      myIntent.putExtra(CALLING_OBJ, callingObjJson);
      myIntent.putExtra(ACTION_LIST, GameUtils.toJson(actions));
      myIntent.putExtra(RETURN_LOC, returnLoc);
    } else if (RETURN_LOC_TRIGGER_START_WORLD.equals(returnLoc) ||
        RETURN_LOC_TRIGGER_START_TURN.equals(returnLoc) ||
        RETURN_LOC_TRIGGER_END_TURN.equals(returnLoc) ||
        RETURN_LOC_TRIGGER_ABILITY_USED.equals(returnLoc)) {
      myIntent = new Intent(ActionListActivity.this, TriggersActivity.class);
      myIntent.putExtra(CALLING_OBJ, "");
      myIntent.putExtra(ACTION_LIST, GameUtils.toJson(actions));
      myIntent.putExtra(RETURN_LOC, returnLoc);
    } else {
      throw new RuntimeException("Error in ActionListActivity.goBack() Unknown returnLoc="+returnLoc);
    }
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Log.d("actionList", "Create New");
    Intent myIntent = new Intent(ActionListActivity.this, ActionEditActivity.class);
    myIntent.putExtra(ActionEditActivity.SELECTED_ACTION_INDEX, "-1");
    // Maintain the state of this object
    myIntent.putExtra(RETURN_LOC, returnLoc);
    myIntent.putExtra(CALLING_OBJ, callingObjJson);
    myIntent.putExtra(ACTION_LIST, GameUtils.toJson(actions));
    startActivity(myIntent);
  }

}
