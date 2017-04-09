package com.alwaysrejoice.hexengine.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.alwaysrejoice.hexengine.dto.Ability;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.BitmapGsonAdapter;
import com.alwaysrejoice.hexengine.dto.Damage;
import com.alwaysrejoice.hexengine.dto.Effect;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.edit.EditMapView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.alwaysrejoice.hexengine.edit.EditMapView.TILE_HEIGHT;
import static com.alwaysrejoice.hexengine.edit.EditMapView.TILE_WIDTH;

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

  public static BgTile jsonToBgTile(String json) {
    return gson.fromJson(json, BgTile.class);
  }

  public static UnitTile jsonToUnitTile(String json) {
    return gson.fromJson(json, UnitTile.class);
  }

  public static Effect jsonToEffectTile(String json) {
    return gson.fromJson(json, Effect.class);
  }

  public static Ability jsonToAbility(String json) {
    return gson.fromJson(json, Ability.class);
  }

  public static ArrayList<Action> jsonToActionList(String json) {
    return gson.fromJson(json, new TypeToken<ArrayList<Action>>(){}.getType());
  }

  public static Action jsonToAction(String json) {
    return gson.fromJson(json, Action.class);
  }

  public static Damage jsonToDamage(String json) {
    return gson.fromJson(json, Damage.class);
  }

  /**
   * Looks up a mod by it's name instead of id
   */
  public static Mod getModByDisplayString(String displayString) {
    Map<String, Mod> allMods = getGame().getMods();
    for (Mod mod : allMods.values()) {
      if (displayString.equals(mod.getDisplayString())) {
        return mod;
      }
    }// for
    return null;
  }

  public static String getModIdByDisplayString(String displayString) {
    Mod mod = getModByDisplayString(displayString);
    if (mod == null) {
      return "";
    }
    return mod.getId();
  }

  public static String getModDisplayFromId(String modId) {
    Mod mod = getGame().getMods().get(modId);
    if (mod == null) {
      return "";
    }
    return mod.getDisplayString();
  }

  public static String getAbilityIdFromName(String abilityName) {
    for (Ability ability : getGame().getAbilities().values()) {
      if (abilityName.equals(ability.getName())) {
        return ability.getId();
      }
    };
    return null;
  }


  public static String actionsToCSV(List<Action> actions) {
    if (actions == null) {
      return "";
    }
    Map<String, Mod> mods = GameUtils.getGame().getMods();
    StringBuffer str = new StringBuffer();
    for (int i=0; i<actions.size(); i++) {
      Action action = actions.get(i);
      Mod mod = mods.get(action.getModId());
      if (i > 0) str.append("\n");
      str.append(mod.getDisplayString());
    }
    return str.toString();
  }


  public static String damageToCSV(List<Damage> list) {
    if (list == null) return "";
    StringBuilder csv = new StringBuilder();
    for (int i=0; i<list.size(); i++) {
      if (i != 0) csv.append("\n");
      Damage dmg = list.get(i);
      if (dmg != null) {
        csv.append(dmg.getDisplayText());
      }
    }
    return csv.toString();
  }

  public static String tileTypeToCSV(List<TileType> list) {
    if (list == null) return "";
    StringBuilder csv = new StringBuilder();
    for (int i=0; i<list.size(); i++) {
      if (i != 0) csv.append(", ");
      TileType t = list.get(i);
      if (t != null) {
        csv.append(t.getName());
      }
    }
    return csv.toString();
  }

  /**
   * Calculates the backgroundSize (X and Y) given a game size
   * @param size GameInfo.size, roughly how many tiles fit vertically into the map
   * @return The size in PX of the background
   */
  public static int getBackgroundSize(int size) {
    return size * TILE_HEIGHT;
  }

  /**
   * Bounds checking for a tile fitting into the background
   * @param backgroundSize : width and height of the background (in pixels)
   * @param col axial position of the tile
   * @param row axial position of the tile
   * @return true if the tile fits on the map without drawing off the border
   */
  public static boolean tileFitsInBackground(int backgroundSize, int row, int col) {
    int x = (backgroundSize / 2) + Math.round(EditMapView.HEX_SIZE * 1.5f  * col) - (TILE_WIDTH / 2);
    int y = (backgroundSize / 2) + Math.round(EditMapView.HEX_SIZE * EditMapView.SQRT_3 * (row + (col / 2f))) - (EditMapView.TILE_HEIGHT / 2);
    boolean fits = ((x > 1) && (y > 1) &&
        ((x + TILE_WIDTH) < backgroundSize-1) &&
        ((y + EditMapView.TILE_HEIGHT) < backgroundSize-1));
    Log.d("fits", "x="+x+" y="+y+" fits="+fits+" bgSize="+backgroundSize+" TILE_WIDTH="+TILE_WIDTH);
    return fits;
  }

}
