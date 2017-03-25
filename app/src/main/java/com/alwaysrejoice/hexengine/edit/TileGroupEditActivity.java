package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.TileGroup;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.TileTypeLink;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Displays and handles events for the list of games screen
 */
public class TileGroupEditActivity extends Activity {
  public static final String EXTRA_TILE_GROUP_NAME = "TILE_GROUP_NAME";

  TileGroup tileGroup;
  ListView allTilesView;
  List<TileType> allTiles;
  TileGroupEditAdapter allAdapter;

  ListView selectedTilesView;
  List<TileType> selectedTiles;
  TileGroupEditAdapter selectedAdapter;

  boolean newTileGroup = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tile_group_edit);
    Game game = GameUtils.getGame();

    // Find the name of the tileGroup we are editing (passed as an extra from TileGroupListActivity)
    Bundle bundle = getIntent().getExtras();
    String groupName = (String) bundle.get(EXTRA_TILE_GROUP_NAME);
    for (TileGroup group : game.getTileGroups()) {
      if (groupName.equals(group.getName())) {
        tileGroup = group;
        break;
      }
    } // for

    // No tilegroup found
    if (tileGroup == null) {
      tileGroup =  new TileGroup();
      tileGroup.setName("");
      newTileGroup = true;
    }

    EditText nameInput = (EditText) findViewById(R.id.tile_group_name);
    nameInput.setText(tileGroup.getName());

    // Tiles that are in this group
    selectedTilesView = (ListView) findViewById(R.id.tile_group_edit_selected);
    selectedTiles = new ArrayList<>();
    for (TileTypeLink link : tileGroup.getTileLinks()) {
      selectedTiles.add(TileTypeLink.getTile(link, game));
    }
    selectedAdapter = new TileGroupEditAdapter(this, selectedTiles);
    selectedTilesView.setAdapter(selectedAdapter);
    AdapterView.OnItemClickListener selectedGroupsClickListener = new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        TileType tile = (TileType) arg0.getItemAtPosition(position);
        Log.d("tileGroupEdit", "onItemClick selected tile="+tile.getName());
        allTiles.add(selectedTiles.get(position));
        selectedTiles.remove(position);
        selectedAdapter.notifyDataSetChanged();
      }
    };
    selectedTilesView.setOnItemClickListener(selectedGroupsClickListener);

    // All tiles in the system
    allTilesView = (ListView) findViewById(R.id.tile_group_edit_all);
    allTiles = new ArrayList<>();
    for (String name: game.getBgTiles().keySet()) {
      BgTile bgTile = game.getBgTiles().get(name);
      if (!selectedTiles.contains(bgTile)) {
        allTiles.add(bgTile);
      }
    }
    Collections.sort(allTiles);
    allAdapter = new TileGroupEditAdapter(this, allTiles);
    allTilesView.setAdapter(allAdapter);
    AdapterView.OnItemClickListener allGroupsClickListener = new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        TileType tile = (TileType) arg0.getItemAtPosition(position);
        Log.d("tileGroupEdit", "onItemClick all tile="+tile.getName());
        selectedTiles.add(allTiles.get(position));
        allTiles.remove(position);
        allAdapter.notifyDataSetChanged();
      }
    };
    allTilesView.setOnItemClickListener(allGroupsClickListener);

  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void save(View view) {
    Log.d("tileGroupEdit", "Save");
    Game game = GameUtils.getGame();

    EditText nameInput = (EditText) findViewById(R.id.tile_group_name);
    String tileGroupName = nameInput.getText().toString();

    if (newTileGroup) {
      game.getTileGroups().add(tileGroup);
    }
    List<TileTypeLink> tileLinks = new ArrayList<>();
    for (TileType tile : selectedTiles) {
      tileLinks.add(new TileTypeLink(tile.getTileType(), tile.getName()));
    }
    tileGroup.setTileLinks(tileLinks);
    tileGroup.setName(tileGroupName);
    Log.d("tileGroupEdit", "Saved "+tileGroup);
    GameUtils.saveGame();
    // Go to the List view
    Intent myIntent = new Intent(TileGroupEditActivity.this, TileGroupListActivity.class);
    startActivity(myIntent);
  }

}
