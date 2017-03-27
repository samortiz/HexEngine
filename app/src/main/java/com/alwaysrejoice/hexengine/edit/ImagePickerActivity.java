package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.util.FileUtils;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.io.File;

/**
 * Displays and handles events for the image picker screen
 */
public class ImagePickerActivity extends Activity {

  public static final String EXTRA_RETURN = "IMAGE_PICKER_RETURN";
  public static final String EXTRA_TILE = "IMAGE_PICKER_TILE";

  public static final String RETURN_BG = "BG";
  public static final String RETURN_UNIT = "UNIT";

  ListView list;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.image_picker);
    TableLayout table = (TableLayout) findViewById(R.id.image_table);

    // Set the page title
    final String returnLoc = getIntent().getStringExtra(EXTRA_RETURN);
    String desc = RETURN_BG.equals(returnLoc) ? "Choose Image for Background" : "Choose Image for Unit";
    TextView titleView = (TextView) findViewById(R.id.title);
    titleView.setText(desc);


    // Get the extra data passed in with the intent
    String tileJson = getIntent().getExtras().getString(EXTRA_TILE);
    TileType tempTile = null;
    if (RETURN_BG.equals(returnLoc)) {
      tempTile = GameUtils.toBgTile(tileJson);
    } else if (RETURN_UNIT.equals(returnLoc)) {
      tempTile = GameUtils.toUnitTile(tileJson);
    } else {
      Log.e("imagePicker", "Error! Unknown returnLoc="+returnLoc);
    }
    final TileType tile = tempTile;
    Log.d("imagePicker", "tileJson="+tileJson+" tile="+tile.getName());


    // Get the screen size
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    int screenWidth = displayMetrics.widthPixels;
    int imagesPerRow = 4;
    int imageSize = screenWidth / imagesPerRow;

    // Go through all the images files in external storage
    File imageDir = FileUtils.getExtPath(FileUtils.IMAGE_DIR);
    String[] imageFiles = imageDir.list();
    TableRow row = null;
    int rowNum = 0;
    for (int i = 0; i < imageFiles.length; i++) {
      final String imageFileName = imageFiles[i];
      final Bitmap bitmap = FileUtils.loadBitmap(imageFileName);

      // New row (first iteration should be a new row)
      if ((i % imagesPerRow) == 0) {
        row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
        table.addView(row, rowNum);
        rowNum += 1;
        Log.d("picker", "new row i="+i+" imagesPerRow="+imagesPerRow);
      }
      // Setup the image
      ImageView iv = new ImageView(this);
      TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(imageSize, imageSize);
      iv.setLayoutParams(layoutParams);
      iv.setImageBitmap(bitmap);
      iv.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          tile.setBitmap(bitmap);
          Log.d("imagePicker", "Clicked on "+imageFileName);
          Intent myIntent = null;
          if (RETURN_BG.equals(returnLoc)) {
            myIntent = new Intent(ImagePickerActivity.this, BgEditActivity.class);
          } else if (RETURN_UNIT.equals(returnLoc)) {
            myIntent = new Intent(ImagePickerActivity.this, UnitEditActivity.class);
          }
          if (myIntent != null) {
            Log.d("imagePicker", "returning tile="+tile);
            myIntent.putExtra("IMAGE_PICKER_TILE", GameUtils.toJson(tile));
            startActivity(myIntent);
          } else {
            Log.e("imagePicker", "unknown returnLoc="+returnLoc);
          }
        } // onClick
      });

      row.addView(iv);
      Log.d("imagePicker", "added file "+imageFileName);
    } // for
  }

}
