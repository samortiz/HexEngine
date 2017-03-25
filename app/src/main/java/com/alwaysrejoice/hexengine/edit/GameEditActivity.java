package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
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

    // If game is null we are in create new game mode
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

    // TODO : Validate that the game is not too large or too small
    // TODO : Validate that the old game does not have tiles in excess of the new game size
    // TODO : Validate the name is not blank

    if (game == null) {
      // TODO : Validate that a file with the same name does not already exist

      // TODO : Instead of the below, we could copy a template game
      game = new Game();
    } else {
      // The user changed the name of the game, this will need a new filename too
      if (!gameName.equals(game.getGameInfo().getName())) {
        GameUtils.deleteGame(game.getGameInfo().getName());
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
