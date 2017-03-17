package com.alwaysrejoice.hexengine.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.alwaysrejoice.hexengine.dto.Game;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

  /**
   * @return The storage location of the games
   */
  public static File getGamePath() {
    return new File(Environment.getExternalStorageDirectory(), "HexEngine/games");
  }

  /**
   * @return the path to the file containing the specified game
   */
  public static File getGameFile(String gameName) {
    File file =  null;
    if (!isExternalStorageWritable()) {
      Log.e("ERROR", "External Storage is not writable");
      return null;
    }
    try {
      String filePath = ""+gameName+".json";
      File gameDir = Utils.getGamePath();
      gameDir.mkdirs();
      file = new File(gameDir, gameName+".json");
    } catch (Exception e) {
      Log.e("Utils", "Error creating file for "+gameName, e);
    }
    return file;
  }

  /**
   * Loads a game from a resource file and returns it
   * @param gameName name of game
   */
  public static Game loadGame(String gameName) {
    Gson gson = new Gson();
    InputStream inputStream = null;
    Game game = null;
    try {
      File file = Utils.getGameFile(gameName);
      inputStream = new FileInputStream(file);
      String jsonGame = IOUtils.toString(inputStream, "UTF-8");
      inputStream.close();
      game = gson.fromJson(jsonGame, Game.class);
    } catch (IOException e) {
      Log.e("IO Error", "Error in loadGame loading "+gameName, e);
      if (inputStream != null) try { inputStream.close(); } catch (IOException e1) { e.printStackTrace();}
    }
    return game;

  }

  /**
   * Writes out a game to a file, based on the gameName
   */
  public static void saveGame(Game game) {
    Gson gson = new Gson();
    try {
      String gameJson = gson.toJson(game);
      File gameFile = Utils.getGameFile(game.getName());
      FileUtils.writeStringToFile(gameFile, gameJson, "UTF-8");
    } catch (IOException e) {
      Log.e("IO Error", "Error in saveGame saving '"+game.getName()+"'", e);
    }
  }

  /* Checks if external storage is available for read and write */
  public static boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }

  /**
   * Loads a bitmap from the inputStream
   */
  public static Bitmap loadBitmap(AssetManager assetManager, String filename) {
    InputStream inputStream = null;
    Bitmap bitmap = null;
    try {
      //Log.d("util", "Opening bitmap");
      inputStream = assetManager.open(filename);
      bitmap = BitmapFactory.decodeStream(inputStream);
    } catch (IOException e) {
      Log.e("utils", "Error reading bitmap from assets ", e);
    } finally {
      try {
        inputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return bitmap;
  }


}
