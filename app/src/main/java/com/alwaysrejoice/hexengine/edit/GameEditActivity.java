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
import com.alwaysrejoice.hexengine.dto.BgMap;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.UnitMap;
import com.alwaysrejoice.hexengine.util.FileUtils;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.List;

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
      nameInput.setText(game.getGameInfo().getName());

      EditText sizeInput = (EditText) findViewById(R.id.game_size);
      sizeInput.setText(Integer.toString(game.getGameInfo().getSize()));

      TextView title = (TextView) findViewById(R.id.title);
      title.setText("Edit Game");
    }
  }

  /**
   * Called when the user clicks "Save" on the edit/new game screen
   */
  public void saveGame(View view) {
    EditText nameInput = (EditText) findViewById(R.id.edit_game_name);
    String gameName = nameInput.getText().toString();

    EditText sizeInput = (EditText) findViewById(R.id.game_size);
    int size = Utils.stringToInt(sizeInput.getText().toString());
    size = Math.min(Math.max(size, Game.MIN_GAME_SIZE), Game.MAX_GAME_SIZE);

    Game game = GameUtils.getGame();
    if ("".equals(gameName)) {
      showError("You must specify a game name");
      return;
    }

    // Creating a new game
    if (game == null) {
      game = new Game();
      Game.setupNewGame(game);
    }

    // Validate that a file with the new name does not already exist
    String origGameName = game.getGameInfo().getName();
    if (!gameName.equals(origGameName)) {
      String[] files = FileUtils.getExtPath(FileUtils.GAME_DIR).list();
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

    // If we are changing the game size to a smaller one
    if (size < game.getGameInfo().getSize()) {
      int backgroundSize = GameUtils.getBackgroundSize(size);
      // Remove any tiles that won't fit into the new game size
      List<BgMap> bgMaps = game.getBgMaps();
      for (int i=bgMaps.size()-1; i>=0; i--) {
        BgMap bgMap = bgMaps.get(i);
        if (!GameUtils.tileFitsInBackground(backgroundSize, bgMap.getRow(), bgMap.getCol())) {
          Log.d("gameEdit", "Removing bg "+bgMap);
          bgMaps.remove(i);
        }
      } // for
      List<UnitMap> unitMaps = game.getUnitMaps();
      for (int i=unitMaps.size()-1; i>=0; i--) {
        UnitMap unitMap = unitMaps.get(i);
        if (!GameUtils.tileFitsInBackground(backgroundSize, unitMap.getRow(), unitMap.getCol())) {
          Log.d("gameEdit", "Removing unit "+unitMap);
          unitMaps.remove(i);
        }
      } // for
    }

    game.getGameInfo().setName(gameName);
    game.getGameInfo().setSize(size);
    Log.d("chooseFile", "editGame gameName="+gameName+" size="+size);
    GameUtils.saveGame(game);
    Intent myIntent = new Intent(GameEditActivity.this, GameListActivity.class);
    startActivity(myIntent);
  }

  public void showError(String errMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errMsg);
    errView.setTextColor(Color.RED);
  }

}
