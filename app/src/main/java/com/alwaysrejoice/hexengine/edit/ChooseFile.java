package com.alwaysrejoice.hexengine.edit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.MainActivity;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Color;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;

public class ChooseFile extends Activity implements AdapterView.OnItemClickListener {
  ListView gameList;
  ChooseFileListAdapter adapter;
  Game chosenGame = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("ChooseFile", "onCreate");
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
    Log.d("ChooseFile", "onItemClick ");
    String gameName = (String) arg0.getItemAtPosition(position);
    Intent myIntent = new Intent(ChooseFile.this, EditActivity.class);
    myIntent.putExtra("GAME_NAME", gameName);
    startActivity(myIntent);
  }


  /**
   * Called when the user clicks "Create New Game" on the new game screen
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
      chosenGame.setTileTypes(new ArrayList());
      chosenGame.setTiles(new ArrayList());
      chosenGame.setBackgroundColor(new Color(00, 50, 50));
    }
    chosenGame.setName(gameName);
    chosenGame.setWidth(width);
    chosenGame.setHeight(height);
    Log.d("chooseFile", "editGame gameName="+chosenGame.getName()+" width="+width+" height="+height);
    Utils.saveGame(chosenGame);

    Intent myIntent = new Intent(ChooseFile.this, EditActivity.class);
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
    Intent myIntent = new Intent(ChooseFile.this, MainActivity.class);
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
   * Called when the user clicks on the delete icon on a row
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
    widthInput.setText(Integer.toString(chosenGame.getWidth()));
    heightInput.setText(Integer.toString(chosenGame.getHeight()));
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
