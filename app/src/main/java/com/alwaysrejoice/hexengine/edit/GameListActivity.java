package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.MainActivity;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.FileUtils;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;

/**
 * Displays and handles events for the list of games screen
 */
public class GameListActivity extends Activity implements AdapterView.OnItemClickListener {
  ListView gameList;
  GameListAdapter adapter;
  Game chosenGame = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("GameListActivity", "onCreate");
    setContentView(R.layout.game_list);

    gameList = (ListView) findViewById(R.id.edit_list_view);
    String[] files = FileUtils.getExtPath(FileUtils.GAME_DIR).list();
    if (files == null) files = new String[0];
    ArrayList<String> games = new ArrayList<String>();
    for (String file : files) {
      String  gameName = file.substring(0, file.length()-5); // remove .json
      games.add(gameName);
      Log.d("chooseFile", "found file "+file+" gameName="+gameName);
    }

    adapter = new GameListAdapter(this, games);
    gameList.setAdapter(adapter);
    gameList.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("GameListActivity", "onItemClick ");
    String gameName = (String) arg0.getItemAtPosition(position);
    GameUtils.getGame(gameName); // This will set the currently active game
    Intent myIntent = new Intent(GameListActivity.this, EditMapActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks "Home" on the edit list screen
   * Takes the user to the create a new game screen
   */
  public void gotoHome(View view) {
    Intent myIntent = new Intent(GameListActivity.this, MainActivity.class);
    startActivity(myIntent);
    Log.d("chooseFile", "home");
  }

  /**
   * Called when the user clicks on the delete icon on a row
   */
  public void deleteGame(View view) {
    final int position = (int) view.getTag();
    final String gameName = gameList.getItemAtPosition(position).toString();
    new AlertDialog.Builder(this)
        .setTitle("Confirm Delete")
        .setMessage("Are you sure you want to delete "+gameName+"?")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            if (GameUtils.deleteGame(gameName)) {
              adapter.removeItem(position);
              adapter.notifyDataSetChanged();
              GameUtils.setGame(null);
              Log.d("gameList", "Deleted gameName="+gameName);
            } else {
              Log.e("gameList", "unable to delete gameName="+gameName);
            }
          }})
        .setNegativeButton("Cancel", null).show();
  }

  /**
   * Called when the user clicks on the edit icon on a row (goes to edit/add screen)
   */
  public void startEditGame(View view) {
    int position = (int) view.getTag();
    String gameName = gameList.getItemAtPosition(position).toString();
    chosenGame = GameUtils.getGame(gameName); // save this as the currently loaded game
    Intent myIntent = new Intent(GameListActivity.this, GameEditActivity.class);
    startActivity(myIntent);
    Log.d("gameList", "Edit gameName="+gameName+" position="+position);
  }

  /**
   * Called when the user clicks "New Game" on the game list screen
   * Takes the user to the create a new game screen
   */
  public void startNewGame(View view) {
    this.chosenGame = null;
    GameUtils.setGame(null); // This will cause the edit screen to make a new game
    Intent myIntent = new Intent(GameListActivity.this, GameEditActivity.class);
    startActivity(myIntent);
    Log.d("gameList", "newGame");
  }

  /**
   * Called when the user clicks on the copy icon on a row
   */
  public void copyGame(View view) {
    int position = (int) view.getTag();
    String gameName = gameList.getItemAtPosition(position).toString();
    Game sourceGame = GameUtils.getGame(gameName); // save this as the currently loaded game
    String newName = "Copy of "+sourceGame.getGameInfo().getName();
    sourceGame.getGameInfo().setName(newName); //Changing the name will save it as a new file
    GameUtils.saveGame(sourceGame);
    this.chosenGame = null;
    adapter.addItem(position+1, newName);
    adapter.notifyDataSetChanged();
    Log.d("gameList", "Copy gameName="+gameName+" position="+position);
  }

}
