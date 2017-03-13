package com.alwaysrejoice.hexengine.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.alwaysrejoice.hexengine.dto.Game;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {

  /** Returns the path to the file containing the specified game */
  public static File getGameFile(String gameName) {
    File file =  null;
    if (!isExternalStorageWritable()) {
      Log.e("ERROR", "External Storage is not writable");
    }
    try {
      String filePath = ""+gameName+".json";
      //file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), filePath);
      file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), filePath);
      if (!file.exists()) {
        if (file.mkdirs()) {
          Log.d("init", "created dirs");
        } else Log.d("init", "didn't make dirs");
        /*
        if (file.createNewFile()) {
          Log.d("init", "created file");
        } else Log.d("init", "failed to create file");
        */
      }
    } catch (Exception e) {
      Log.e("Utils", "Error creating file for "+gameName+" file="+file, e);
    }
    return file;
  }

  /**
   * Loads a game from a resource file and returns it
   * @param gameName name of game
   */
  public static Game loadGame(String gameName, Context context) {
    Gson gson = new Gson();
    InputStream inputStream;
    Game game = null;
    try {
      //File file = Util.getGameFile(gameName);
      //inputStream = assetManager.open("games/"+gameName+".json");
      inputStream = context.openFileInput(gameName+".json");
      String jsonGame = IOUtils.toString(inputStream, "UTF-8");
      inputStream.close();
      game = gson.fromJson(jsonGame, Game.class);
    } catch (IOException e) {
      Log.e("IO Error", "Error in loadGame loading "+gameName, e);
    }
    return game;

  }

  /**
   * Writes out a game to a file, based on the gameName
   */
  public static void saveGame(Game game, Context context) {
    Gson gson = new Gson();
    OutputStream outputStream;
    try {
      String gameJson = gson.toJson(game);
      //File file = Util.getGameFile(game.getName());
      //Log.d("Util", "saveGame JSON="+gameJson+"\n file="+file);
      //FileUtils.writeStringToFile(file, gameJson, "UTF-8");

      outputStream = context.openFileOutput(game.getName()+".json", Context.MODE_PRIVATE);
      outputStream.write(gameJson.getBytes());
      outputStream.close();

    } catch (IOException e) {
      Log.e("IO Error", "Error in loadGame loading "+game.getName(), e);
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

  /* Checks if external storage is available to at least read */
  public static boolean isExternalStorageReadable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state) ||
        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      return true;
    }
    return false;
  }

}
