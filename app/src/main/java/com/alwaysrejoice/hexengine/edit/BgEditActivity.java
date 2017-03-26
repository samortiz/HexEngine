package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.BgMap;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.TileGroup;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.TileTypeLink;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.Map;

public class BgEditActivity extends Activity {
  public static final String SELECTED_TILE = "BGEDIT_SELECTED_TILE";
  BgTile tile; // The tile we are currently editing
  String origTileName = "";  // Tile name when the edit screen was first invoked

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("bgEdit", "onCreate");
    setContentView(R.layout.bg_edit);
    Bundle bundle = getIntent().getExtras();

    // Check if we are coming back from an ImagePickerActivity
    String tileJson = (String) bundle.get(ImagePickerActivity.EXTRA_TILE);
    if (tileJson != null) {
      tile = GameUtils.toBgTile(tileJson);
      setUiFromTile();
      Log.d("bgEdit", "Tile chosen! " + tile.getName());
    }

    // Load the file we are editing
    String tileName = (String) bundle.get(BgEditActivity.SELECTED_TILE);
    if (tileName != null) {
      Game game = GameUtils.getGame();
      tile = game.getBgTiles().get(tileName);
      setUiFromTile();
      origTileName = tileName;
      Log.d("bgEdit", "begin editing selected tile "+tileName);
    }

    if (tile == null) {
      tile = new BgTile();
    }

  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    loadTileFromUi();
    Game game = GameUtils.getGame();
    Map<String, BgTile> bgTiles = game.getBgTiles();

    if ("".equals(tile.getName())) {
      showError("You must enter a name.");
      return;
    }

    // If name is changing update all the links
    if (!tile.getName().equals(origTileName)) {
      for (BgMap bgMap : game.getBgMaps()) {
        if (origTileName.equals(bgMap.getName())) {
          bgMap.setName(tile.getName());
        }
      } //for
      for (TileGroup group : game.getTileGroups()) {
        for (TileTypeLink link : group.getTileLinks()) {
          if ( origTileName.equals(link.getName()) &&
              (link.getTileType() == TileType.TILE_TYPE.BACKGROUND)) {
            link.setName(tile.getName());
          }
        }
      } // for
    }

    bgTiles.remove(origTileName);
    bgTiles.put(tile.getName(), tile);
    GameUtils.saveGame();
    Log.d("bgEdit", "saving bgTile name="+tile.getName()+" type="+tile.getType()+" orig="+origTileName);
    // Go to the list
    Intent myIntent = new Intent(BgEditActivity.this, BgListActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on the bitmap to change it
   */
  public void chooseBitmap(View view) {
    loadTileFromUi();
    Intent myIntent = new Intent(BgEditActivity.this, ImagePickerActivity.class);
    myIntent.putExtra(ImagePickerActivity.EXTRA_RETURN, ImagePickerActivity.RETURN_BG);
    myIntent.putExtra(ImagePickerActivity.EXTRA_TILE, GameUtils.toJson(tile));
    startActivity(myIntent);
  }

  /**
   * Loads the information from the UI input components into the tile
   */
  private void loadTileFromUi() {
    EditText nameInput = (EditText) findViewById(R.id.bg_name);
    EditText typeInput = (EditText) findViewById(R.id.bg_type);
    tile.setName(nameInput.getText().toString().trim());
    tile.setType(typeInput.getText().toString().trim());
  }

  /**
   * Updates the UI to match the data in the tile
   */
  private void setUiFromTile() {
    EditText nameInput = (EditText) findViewById(R.id.bg_name);
    EditText typeInput = (EditText) findViewById(R.id.bg_type);
    ImageView imgInput = (ImageView) findViewById(R.id.bg_img);
    nameInput.setText(tile.getName());
    typeInput.setText(tile.getType());
    if (tile.getBitmap() != null) {
      imgInput.setImageBitmap(tile.getBitmap());
      imgInput.getLayoutParams().height = 200;
      imgInput.setScaleType(ImageView.ScaleType.FIT_CENTER);
      imgInput.setAdjustViewBounds(true);
      imgInput.setCropToPadding(false);
      imgInput.requestLayout();
    }
  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
  }


}
