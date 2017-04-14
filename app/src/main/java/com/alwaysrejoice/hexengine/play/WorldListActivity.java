package com.alwaysrejoice.hexengine.play;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.alwaysrejoice.hexengine.MainActivity;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.World;
import com.alwaysrejoice.hexengine.util.FileUtils;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.StringListAdapter;
import com.alwaysrejoice.hexengine.util.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Displays and handles events for the list of worlds screen
 */
public class WorldListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView list;
  StringListAdapter adapter;
  ArrayList<String> worldNames;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.world_list);
    Log.d("worldList", "onCreate");

    list = (ListView) findViewById(R.id.world_list_view);
    worldNames = new ArrayList<>();

    String[] files = FileUtils.getExtPath(FileUtils.WORLDS_DIR).list();
    if (files == null) files = new String[0];
    for (String file : files) {
      String  worldName = file.substring(0, file.length()-5); // remove .json
      worldNames.add(worldName);
      //Log.d("setup world", "found file "+file+" worldName="+worldName);
    }

    ArrayList<String> gameNames = new ArrayList<>();
    String[] gameFiles = FileUtils.getExtPath(FileUtils.GAME_DIR).list();
    if (gameFiles == null) gameFiles = new String[0];
    for (String file : gameFiles) {
      String  gameName = file.substring(0, file.length()-5); // remove .json
      gameNames.add(gameName);
      //Log.d("setup world", "found game "+gameName);
    }
    Utils.setupSpinner((Spinner)findViewById(R.id.world_spinner), gameNames);

    adapter = new StringListAdapter(this, worldNames);
    list.setAdapter(adapter);
    list.setOnItemClickListener(this);
  }

  /**
   * Called when a particular world row is clicked in the list
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("worldList", "onItemClick ");
    String worldName = (String) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(WorldListActivity.this, WorldActivity.class);
    myIntent.putExtra(WorldActivity.SELECTED_WORLD_NAME, worldName);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    final int position = (int) view.getTag();
    final String worldName = (String) list.getItemAtPosition(position);
    new AlertDialog.Builder(this)
      .setTitle("Confirm Delete")
      .setMessage("Are you sure you want to delete "+worldName+"?")
      .setIcon(android.R.drawable.ic_dialog_alert)
      .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
          File worldFile = FileUtils.getWorldFile(worldName);
          worldFile.delete();
          adapter.removeItem(position);
          adapter.notifyDataSetChanged();
          Log.d("worldList", "deleted "+worldName);
        }})
      .setNegativeButton("Cancel", null).show();
  }

  /**
   * Called when the user clicks Home
   */
  public void gotoHome(View view) {
    Log.d("worldList", "goto Home");
    Intent myIntent = new Intent(WorldListActivity.this, MainActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void createNew(View view) {
    Log.d("worldList", "Create New");
    String gameName = Utils.getSpinnerValue((Spinner)findViewById(R.id.world_spinner));
    Log.d("worldList", "creating world from game="+gameName);
    Game game = GameUtils.getGame(gameName);
    World world = new World(game);
    GameUtils.saveWorld(world);
    worldNames.add(world.getName());
    adapter.notifyDataSetChanged();
  }

}
