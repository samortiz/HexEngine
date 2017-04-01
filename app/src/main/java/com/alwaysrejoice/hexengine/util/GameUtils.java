package com.alwaysrejoice.hexengine.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.BitmapGsonAdapter;
import com.alwaysrejoice.hexengine.dto.Effect;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.alwaysrejoice.hexengine.util.FileUtils.MOD_DIR;

public class GameUtils {
  private static Game game = null;

  // Custom GSON serialize/deserializer
  // This will allow my DTO to store/load Bitmaps as Base64
  public static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Bitmap.class, new BitmapGsonAdapter()).create();


  /**
   * Returns the currently loaded game
   * If there is no game loaded this will be null
   */
  public static Game getGame() {
    //Log.d("game", "returning current game "+game);
    return game;
  }

  /**
   * Returns the game with the specified name
   * @return a cached Game instance or a newly loaded one
   */
  public static Game getGame(String gameName) {
    if ((game == null) || !game.getGameInfo().getName().equals(gameName)) {
      Log.d("game", "loading " +gameName);
      game = GameUtils.loadGame(gameName);
    }
    //Log.d("game", "returning game "+game);
    return game;
  }


  /**
   * Saves the game currently opened.
   * NOTE : If no game is opened this does nothing
   */
  public static void saveGame() {
    if (game != null) {
      GameUtils.saveGame(game);
    }
    Log.d("game", "saving current game "+game);
  }

  public static void setGame(Game gameToSet) {
    game = gameToSet;
  }

  /**
   * Loads a game from a resource file and returns it
   * @param gameName name of game
   */
  public static Game loadGame(String gameName) {
    InputStream inputStream = null;
    Game game = null;
    try {
      File file = FileUtils.getGameFile(gameName);
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
  public static void saveGame(Game gameToSave) {
    try {
      game = gameToSave;
      String gameJson = gson.toJson(game);
      File gameFile = FileUtils.getGameFile(game.getGameInfo().getName());
      org.apache.commons.io.FileUtils.writeStringToFile(gameFile, gameJson, "UTF-8");
    } catch (IOException e) {
      Log.e("IO Error", "Error in saveGame saving '"+game.getGameInfo().getName()+"'", e);
    }
  }

  /**
   * Deletes the specified game from the file system
   */
  public static boolean deleteGame(String gameName) {
    File gameFile = FileUtils.getGameFile(gameName);
    if (gameFile != null) {
      return gameFile.delete();
    }
    return false;
  }

  public static String toJson(Object obj) {
    return gson.toJson(obj);
  }

  public static BgTile toBgTile(String json) {
    return gson.fromJson(json, BgTile.class);
  }

  public static UnitTile toUnitTile(String json) {
    return gson.fromJson(json, UnitTile.class);
  }

  public static Effect toEffectTile(String json) {
    return gson.fromJson(json, Effect.class);
  }

  public static ArrayList<Action> jsonToActionList(String json) {
    return gson.fromJson(json, new TypeToken<ArrayList<Action>>(){}.getType());
  }

  public static Action toAction(String json) {
    return gson.fromJson(json, Action.class);
  }

  /**
   * Loads a mod from the mod storage directory
   * @param fileName name of mod file in MOD_DIR
   */
  public static Mod loadMod(String fileName) {
    InputStream inputStream = null;
    Mod mod = null;
    try {
      File file = new File(FileUtils.getExtPath(MOD_DIR), fileName);
      inputStream = new FileInputStream(file);
      String jsonMod = IOUtils.toString(inputStream, "UTF-8");
      inputStream.close();
      mod = gson.fromJson(jsonMod, Mod.class);
    } catch (IOException e) {
      Log.e("IO Error", "Error in loadMod loading "+fileName, e);
      if (inputStream != null) try { inputStream.close(); } catch (IOException e1) { e.printStackTrace();}
    }
    Log.d("GameUtils", "Loaded mod "+mod.getName()+" from file "+fileName);
    return mod;
  }

}
