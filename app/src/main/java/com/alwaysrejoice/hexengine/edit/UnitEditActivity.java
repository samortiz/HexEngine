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
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.TileGroup;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.TileTypeLink;
import com.alwaysrejoice.hexengine.dto.UnitMap;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.Map;

public class UnitEditActivity extends Activity {
  public static final String SELECTED_UNIT = "UNIT_EDIT_SELECTED_TILE";
  UnitTile tile; // The tile we are currently editing
  String origTileName = "";  // Tile name when the edit screen was first invoked

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("unitEdit", "onCreate");
    setContentView(R.layout.unit_edit);
    Bundle bundle = getIntent().getExtras();

    // Check if we are coming back from an ImagePickerActivity
    String tileJson = (String) bundle.get(ImagePickerActivity.EXTRA_TILE);
    if (tileJson != null) {
      tile = GameUtils.toUnitTile(tileJson);
      setUiFromTile();
      Log.d("unitEdit", "Tile chosen! " + tile.getName());
    }

    // Load the file we are editing
    String tileName = (String) bundle.get(UnitEditActivity.SELECTED_UNIT);
    if ((tileName != null) && !"".equals(tileName)) {
      Game game = GameUtils.getGame();
      tile = game.getUnitTiles().get(tileName);
      setUiFromTile();
      origTileName = tileName;
      Log.d("unitEdit", "begin editing selected tile "+tileName);
    }

    if (tile == null) {
      tile = new UnitTile();
    }

  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    loadTileFromUi();
    Game game = GameUtils.getGame();
    Map<String, UnitTile> unitTiles = game.getUnitTiles();

    if ("".equals(tile.getName())) {
      showError("You must enter a name.");
      return;
    }

    // If name is changing update all the links
    if (!tile.getName().equals(origTileName)) {
      for (UnitMap unitMap : game.getUnitMaps()) {
        if (origTileName.equals(unitMap.getName())) {
          unitMap.setName(tile.getName());
        }
      } //for
      for (TileGroup group : game.getTileGroups()) {
        for (TileTypeLink link : group.getTileLinks()) {
          if ( origTileName.equals(link.getName()) &&
              (link.getTileType() == TileType.TILE_TYPE.UNIT)) {
            link.setName(tile.getName());
          }
        }
      } // for
    }

    unitTiles.remove(origTileName);
    unitTiles.put(tile.getName(), tile);
    GameUtils.saveGame();
    Log.d("unitEdit", "saving UnitTile name="+tile.getName()+" orig="+origTileName);
    // Go to the list
    Intent myIntent = new Intent(UnitEditActivity.this, UnitListActivity.class);
    startActivity(myIntent);
  }

  /**
   * Called when the user clicks on the bitmap to change it
   */
  public void chooseBitmap(View view) {
    loadTileFromUi();
    Intent myIntent = new Intent(UnitEditActivity.this, ImagePickerActivity.class);
    myIntent.putExtra(ImagePickerActivity.EXTRA_RETURN, ImagePickerActivity.RETURN_UNIT);
    myIntent.putExtra(ImagePickerActivity.EXTRA_TILE, GameUtils.toJson(tile));
    startActivity(myIntent);
  }

  /**
   * Loads the information from the UI input components into the tile
   */
  private void loadTileFromUi() {
    EditText nameInput = (EditText) findViewById(R.id.unit_name);
    tile.setName(nameInput.getText().toString().trim());
  }

  /**
   * Updates the UI to match the data in the tile
   */
  private void setUiFromTile() {
    EditText nameInput = (EditText) findViewById(R.id.unit_name);
    ImageView imgInput = (ImageView) findViewById(R.id.unit_img);
    nameInput.setText(tile.getName());
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
