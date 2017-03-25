package com.alwaysrejoice.hexengine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.edit.GameListActivity;
import com.alwaysrejoice.hexengine.edit.ImagePickerActivity;
import com.alwaysrejoice.hexengine.play.MapActivity;
import com.alwaysrejoice.hexengine.util.FileUtils;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.io.File;

/**
 * Home Screen
 */
public class MainActivity extends Activity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Show the home screen
    setContentView(R.layout.home);


    /*
    //DEBUG - Clear all the images from external Storage
    // This will copy them all from assets again
    File dir = FileUtils.getImagePathExt();
    for (String fileName : dir.list()) {
      File file = new File(dir, fileName);
      file.delete();
      Log.d("Deleting", "file="+fileName);
    };
    dir.delete();
    */

    // Setup the external storage as necessary
    FileUtils.initDisk(getAssets());
    Log.d("init", "MainActivity.onCreate");

  }

  public void showEdit(View view){
    Intent myIntent = new Intent(MainActivity.this, GameListActivity.class);
    startActivity(myIntent);
    Log.d("click", "mainActivity showEdit");
  }

  public void showPlay(View view){
    Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
    startActivity(myIntent);
    Log.d("click", "mainActivity showPlay");
  }

}
