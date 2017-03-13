package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Color;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.Util;

import java.io.IOException;
import java.util.ArrayList;

public class ChooseFile extends Activity implements AdapterView.OnItemClickListener {
  ListView gameList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("ChooseFile", "onCreate");
    setContentView(R.layout.edit_list);

    gameList = (ListView) findViewById(R.id.edit_list_view);
    String[] files = new String[0];
    try {
      files = getAssets().list("games");
    } catch (IOException e) {
      e.printStackTrace();
    }
    ArrayList<String> games = new ArrayList<String>();
    for (String file : files) {
      String  gameName = file.substring(0, file.length()-5); // remove .json
      games.add(gameName);
      Log.d("chooseFile", "found file "+file+" gameName="+gameName);
    }

    Log.d("chooseFile", "Games size = "+games.size());

    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, games);
    gameList.setAdapter(arrayAdapter);
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
   * Called when the user clicks "New Game" on the main edit screen
   * Takes the user to the create a new game screen
   */
  public void newGame(View view) {
    setContentView(R.layout.create_new_game);
    Log.d("chooseFile", "newGame");
  }

  /**
   * Called when the user clicks "Create New Game" on the new game screen
   */
  public void createNewGame(View view) {
    EditText nameInput = (EditText) findViewById(R.id.nameInput);
    EditText widthInput = (EditText) findViewById(R.id.widthInput);
    EditText heightInput = (EditText) findViewById(R.id.heightInput);
    String gameName = nameInput.getText().toString();
    int width = Integer.parseInt(widthInput.getText().toString());
    int height = Integer.parseInt(heightInput.getText().toString());
    Game game = new Game();
    game.setName(gameName);
    game.setWidth(width);
    game.setHeight(height);
    game.setTileTypes(new ArrayList());
    game.setTiles(new ArrayList());
    game.setBackgroundColor(new Color(00, 50, 50));
    Log.d("chooseFile", "createNewGame gameName="+game.getName()+" width="+width+" height="+height);
    Util.saveGame(game, getApplicationContext());

    Intent myIntent = new Intent(ChooseFile.this, EditActivity.class);
    myIntent.putExtra("GAME_NAME", gameName);
    startActivity(myIntent);
  }

}
