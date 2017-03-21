package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.MainActivity;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Color;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.GameInfo;
import com.alwaysrejoice.hexengine.dto.TileTypeBackup;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;

/**
 * Displays and handles events for the list of games screen
 */
public class GameList extends Activity implements AdapterView.OnItemClickListener {
  ListView gameList;
  ChooseFileListAdapter adapter;
  Game chosenGame = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("GameList", "onCreate");
    setContentView(R.layout.edit_list);

    gameList = (ListView) findViewById(R.id.edit_list_view);
    String[] files = Utils.getGamePath().list();
    if (files == null) files = new String[0];
    ArrayList<String> games = new ArrayList<String>();
    for (String file : files) {
      String  gameName = file.substring(0, file.length()-5); // remove .json
      games.add(gameName);
      Log.d("chooseFile", "found file "+file+" gameName="+gameName);
    }

    adapter = new ChooseFileListAdapter(this, games);
    gameList.setAdapter(adapter);
    gameList.setOnItemClickListener(this);
  }

  /**
   * Called when a particular game row is clicked on the edit screen (choose file screen)
   */
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    Log.d("GameList", "onItemClick ");
    String gameName = (String) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(GameList.this, EditActivity.class);
    myIntent.putExtra("GAME_NAME", gameName);
    startActivity(myIntent);
  }


  /**
   * Called when the user clicks "Save" on the edit/new game screen
   */
  public void saveGame(View view) {
    EditText nameInput = (EditText) findViewById(R.id.edit_game_name);
    EditText widthInput = (EditText) findViewById(R.id.edit_game_width);
    EditText heightInput = (EditText) findViewById(R.id.edit_game_height);
    String gameName = nameInput.getText().toString();
    int width = getInputInt(widthInput, 10);
    int height = getInputInt(heightInput, 10);

    // TODO : Validate that the game is not too large or too small
    // TODO : Validate that the old game does not have tiles in excess of the new game size

    if (chosenGame == null) {
      chosenGame = new Game();
      // Do the initial setup, so new games don't start totally blank
      // TODO : Load the default tiles from assets

    } else {
      // The user changed the name of the game, this will need a new filename too
      if (!gameName.equals(chosenGame.getGameInfo().getName())) {
        Utils.deleteGame(chosenGame.getGameInfo().getName());
      }
    }
    chosenGame.getGameInfo().setName(gameName);
    chosenGame.getGameInfo().setWidth(width);
    chosenGame.getGameInfo().setHeight(height);
    Log.d("chooseFile", "editGame gameName="+gameName+" width="+width+" height="+height);
    Utils.saveGame(chosenGame);

    Intent myIntent = new Intent(GameList.this, EditActivity.class);
    myIntent.putExtra("GAME_NAME", gameName);
    startActivity(myIntent);
  }

  /**
   * Gets a value from a text input and returns it as a String
   * @return int value of input, or defaultVal if there was any problem
   */
  public int getInputInt(EditText input, int defaultVal) {
    String val = input.getText().toString();
    if (val.length() > 0) {
      return Integer.parseInt(val);
    }
    return defaultVal;
  }

  /**
   * Called when the user clicks "Home" on the edit list screen
   * Takes the user to the create a new game screen
   */
  public void gotoHome(View view) {
    Intent myIntent = new Intent(GameList.this, MainActivity.class);
    startActivity(myIntent);
    Log.d("chooseFile", "home");
  }

  /**
   * Called when the user clicks on the delete icon on a row
   */
  public void deleteGame(View view) {
    int position = (int) view.getTag();
    String gameName = gameList.getItemAtPosition(position).toString();
    if (Utils.deleteGame(gameName)) {
      adapter.removeItem(position);
      adapter.notifyDataSetChanged();
      Log.d("chooseFile", "Deleted gameName="+gameName);
    } else {
      Log.e("chooseFile", "unable to delete gameName="+gameName);
    }
  }

  /**
   * Called when the user clicks on the edit icon on a row (goes to edit/add screen)
   */
  public void startEditGame(View view) {
    int position = (int) view.getTag();
    String gameName = gameList.getItemAtPosition(position).toString();
    chosenGame = Utils.loadGame(gameName);
    setContentView(R.layout.edit_game);

    EditText nameInput = (EditText) findViewById(R.id.edit_game_name);
    EditText widthInput = (EditText) findViewById(R.id.edit_game_width);
    EditText heightInput = (EditText) findViewById(R.id.edit_game_height);
    nameInput.setText(gameName);
    widthInput.setText(Integer.toString(chosenGame.getGameInfo().getWidth()));
    heightInput.setText(Integer.toString(chosenGame.getGameInfo().getHeight()));
    TextView title = (TextView) findViewById(R.id.edit_game_title);
    title.setText("Edit Game");
    Log.d("chooseFile", "Edit gameName="+gameName+" position="+position);
  }

  /**
   * Called when the user clicks "New Game" on the game list screen
   * Takes the user to the create a new game screen
   */
  public void startNewGame(View view) {
    this.chosenGame = null;
    setContentView(R.layout.edit_game);
    Log.d("chooseFile", "newGame");
  }


}
