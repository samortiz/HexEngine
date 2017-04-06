package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.List;
import java.util.Map;

public class BgEditActivity extends Activity {
  public static final String SELECTED_TILE_ID = "BGEDIT_SELECTED_TILE_ID";
  BgTile tile; // The tile we are currently editing

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.bg_edit);
    Bundle bundle = getIntent().getExtras();

    List<String> bgTypes = GameUtils.getGame().getBgTypes();
    Spinner spinner = (Spinner) findViewById(R.id.bg_type_spinner);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bgTypes);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);

    // Check if we are coming back from an ImagePickerActivity
    String tileJson = (String) bundle.get(ImagePickerActivity.EXTRA_TILE);
    if (tileJson != null) {
      tile = GameUtils.jsonToBgTile(tileJson);
      setUiFromTile();
      Log.d("bgEdit", "Tile chosen! " + tile.getName());
    }

    // Load the file we are editing
    String tileId = (String) bundle.get(BgEditActivity.SELECTED_TILE_ID);
    if ((tileId != null) && !"".equals(tileId)) {
      tile = GameUtils.getGame().getBgTiles().get(tileId);
      setUiFromTile();
      Log.d("bgEdit", "begin editing selected tile "+tile.getName());
    }

    if (tile == null) {
      tile = new BgTile(Utils.generateUniqueId());
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

    bgTiles.put(tile.getId(), tile);
    GameUtils.saveGame();
    Log.d("bgEdit", "saving bgTile name="+tile.getName()+" type="+tile.getType());
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
    tile.setName(nameInput.getText().toString().trim());

    Spinner typeSpinner = (Spinner) findViewById(R.id.bg_type_spinner);
    ArrayAdapter<String> typeAdapter = (ArrayAdapter<String>)typeSpinner.getAdapter();
    String type = typeAdapter.getItem(typeSpinner.getSelectedItemPosition());
    tile.setType(type);
  }

  /**
   * Updates the UI to match the data in the tile
   */
  private void setUiFromTile() {
    EditText nameInput = (EditText) findViewById(R.id.bg_name);
    nameInput.setText(tile.getName());

    ImageView imgInput = (ImageView) findViewById(R.id.bg_img);
    if (tile.getBitmap() != null) {
      imgInput.setImageBitmap(tile.getBitmap());
      imgInput.getLayoutParams().height = 200;
      imgInput.setScaleType(ImageView.ScaleType.FIT_CENTER);
      imgInput.setAdjustViewBounds(true);
      imgInput.setCropToPadding(false);
      imgInput.requestLayout();
    }

    Spinner typeSpinner = (Spinner) findViewById(R.id.bg_type_spinner);
    ArrayAdapter<String> typeAdapter = (ArrayAdapter<String>)typeSpinner.getAdapter();
    int selectedTypeIndex = Math.max(0, typeAdapter.getPosition(tile.getType()));
    typeSpinner.setSelection(selectedTypeIndex);
  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
  }

}
