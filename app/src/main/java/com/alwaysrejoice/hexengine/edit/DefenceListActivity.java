package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Damage;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

/**
 * Displays and handles events for the list of games screen
 */
public class DefenceListActivity extends Activity implements AdapterView.OnItemClickListener {
  public static final String SELECTED_UNIT_ID = "DEFENCE_SELECTED_UNIT_ID";
  ListView list;
  DefenceListAdapter adapter;
  List<Damage> damages;
  UnitTile unit;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.defence_list);
    Bundle bundle = getIntent().getExtras();
    Game game = GameUtils.getGame();

    // Load the unit from the game
    String unitId = (String) bundle.get(SELECTED_UNIT_ID);
    unit = game.getUnitTiles().get(unitId);
    if (unit == null) throw new IllegalArgumentException("You must pass the DefenceListActivity.SELECTED_UNIT_ID");

    list = (ListView) findViewById(R.id.defence_list_view);
    damages = unit.getDefence();
    adapter = new DefenceListAdapter(this, damages);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("unitList", "onItemClick ");
    Intent myIntent = new Intent(DefenceListActivity.this, DefenceEditActivity.class);
    myIntent.putExtra(DefenceEditActivity.SELECTED_UNIT_ID, unit.getId());
    myIntent.putExtra(DefenceEditActivity.SELECTED_DAMAGE_INDEX, Integer.toString(position));
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    int position = (int) view.getTag();
    adapter.removeItem(position);
    GameUtils.saveGame();
    adapter.notifyDataSetChanged();
    Log.d("unitList", "deleted ");
  }

  /**
   * Called when the user clicks Settings
   */
  public void save(View view) {
    // The game is already saved, we just need to go to the Unit editor
    Intent myIntent = new Intent(DefenceListActivity.this, UnitEditActivity.class);
    myIntent.putExtra(UnitEditActivity.SELECTED_UNIT_ID, unit.getId());
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    Intent myIntent = new Intent(DefenceListActivity.this, DefenceEditActivity.class);
    myIntent.putExtra(DefenceEditActivity.SELECTED_UNIT_ID, unit.getId());
    myIntent.putExtra(DefenceEditActivity.SELECTED_DAMAGE_INDEX, "-1");
    startActivity(myIntent);
  }

}
