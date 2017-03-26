package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.FileUtils;
import com.alwaysrejoice.hexengine.util.GameUtils;

/**
 * Game Edit Screen
 */
public class GameEditActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game_edit);
    Game game = GameUtils.getGame();
    Log.d("GameEditActivity", "onCreate game="+game);

    // Editing existing game
    if (game != null) {
      EditText nameInput = (EditText) findViewById(R.id.edit_game_name);
      EditText widthInput = (EditText) findViewById(R.id.edit_game_width);
      EditText heightInput = (EditText) findViewById(R.id.edit_game_height);
      nameInput.setText(game.getGameInfo().getName());
      widthInput.setText(Integer.toString(game.getGameInfo().getWidth()));
      heightInput.setText(Integer.toString(game.getGameInfo().getHeight()));
      TextView title = (TextView) findViewById(R.id.title);
      title.setText("Edit Game");
    }
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
    Game game = GameUtils.getGame();

    width = Math.min(Math.max(width, Game.MIN_GAME_WIDTH), Game.MAX_GAME_WIDTH);
    height = Math.min(Math.max(height, Game.MIN_GAME_HEIGHT), Game.MAX_GAME_HEIGHT);

    if ("".equals(gameName)) {
      showError("You must specify a game name");
      return;
    }

    // Validation Problem. Validate that the old game does not have tiles in excess of the new game size
    // The corners make this hard, we need to translate the row,col into X,Y position and determine
    // if it exceeds the bounds.

    // Creating a new game
    if (game == null) {
      game = new Game();
    }

    // Validate that a file with the new name does not already exist
    String origGameName = game.getGameInfo().getName();
    if (!gameName.equals(origGameName)) {
      String[] files = FileUtils.getGamePath().list();
      for (String file : files) {
        String existingFileName = file.substring(0, file.length() - 5); // remove .json
        if (gameName.equals(existingFileName)) {
          showError("There is already a game with the name "+gameName);
          return;
        }
      } // for
      // The user changed the name of the game, so we have a new filename
      // we need to delete the old file or we will have a duplicate
      if (!"".equals(origGameName)) {
        GameUtils.deleteGame(origGameName);
      }
    }

    game.getGameInfo().setName(gameName);
    game.getGameInfo().setWidth(width);
    game.getGameInfo().setHeight(height);
    Log.d("chooseFile", "editGame gameName="+gameName+" width="+width+" height="+height);
    GameUtils.saveGame(game);
    Intent myIntent = new Intent(GameEditActivity.this, GameListActivity.class);
    startActivity(myIntent);
  }

  public void showError(String errMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errMsg);
    errView.setTextColor(Color.RED);
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

}
