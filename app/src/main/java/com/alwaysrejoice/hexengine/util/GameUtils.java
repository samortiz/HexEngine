package com.alwaysrejoice.hexengine.util;

import android.util.Log;
import com.alwaysrejoice.hexengine.dto.AI;
import com.alwaysrejoice.hexengine.dto.AbilityTile;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Damage;
import com.alwaysrejoice.hexengine.dto.EffectTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.Team;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.UnitTile;
import com.alwaysrejoice.hexengine.edit.EditMapView;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;

public class GameUtils {
  // Static singleton instances for the current game in progress
  private static Game game = null;

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
   * Loads a game from an external file and returns it
   * @param gameName name of game
   */
  public static Game loadGame(String gameName) {
    InputStream inputStream = null;
    try {
      File file = FileUtils.getGameFile(gameName);
      inputStream = new FileInputStream(file);
      String jsonGame = IOUtils.toString(inputStream, "UTF-8");
      inputStream.close();
      game = Utils.gson.fromJson(jsonGame, Game.class);
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
      String gameJson = Utils.gson.toJson(game);
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

  public static BgTile jsonToBgTile(String json) {
    return Utils.gson.fromJson(json, BgTile.class);
  }

  public static UnitTile jsonToUnitTile(String json) {
    return Utils.gson.fromJson(json, UnitTile.class);
  }

  public static EffectTile jsonToEffectTile(String json) {
    return Utils.gson.fromJson(json, EffectTile.class);
  }

  public static AbilityTile jsonToAbility(String json) {
    return Utils.gson.fromJson(json, AbilityTile.class);
  }

  public static ArrayList<Action> jsonToActionList(String json) {
    return Utils.gson.fromJson(json, new TypeToken<ArrayList<Action>>(){}.getType());
  }

  public static Action jsonToAction(String json) {
    return Utils.gson.fromJson(json, Action.class);
  }

  public static Damage jsonToDamage(String json) {
    return Utils.gson.fromJson(json, Damage.class);
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
    for (AbilityTile abilityTile : getGame().getAbilities().values()) {
      if (abilityName.equals(abilityTile.getName())) {
        return abilityTile.getId();
      }
    };
    return null;
  }

  public static Team getTeamById(String teamId) {
    if (teamId == null) {
      return null;
    }
    for (Team t : game.getTeams()) {
      if (teamId.equals(t.getId())) {
        return t;
      }
    } // for
    return null;
  }

  public static String getTeamIdFromName(String teamName) {
    if (teamName == null) {
      return null;
    }
    for (Team t : game.getTeams()) {
      if (teamName.equals(t.getName())) {
        return t.getId();
      }
    } // for
    return null;
  }

  public static String getTeamNameFromId(String teamId) {
    if (teamId == null) {
      return null;
    }
    for (Team t : game.getTeams()) {
      if (teamId.equals(t.getId())) {
        return t.getName();
      }
    } // for
    return null;
  }

  public static AI getAiById(String aiId) {
    if (aiId == null) {
      return null;
    }
    for (AI ai : game.getAis()) {
      if (aiId.equals(ai.getId())) {
        return ai;
      }
    } // for
    return null;
  }

  public static String getAiIdFromName(String aiName) {
    if (aiName == null) {
      return null;
    }
    for (AI ai : game.getAis()) {
      if (aiName.equals(ai.getName())) {
        return ai.getId();
      }
    } // for
    return null;
  }

  public static String getAiNameFromId(String aiId) {
    if (aiId == null) {
      return null;
    }
    for (AI ai : game.getAis()) {
      if (aiId.equals(ai.getId())) {
        return ai.getName();
      }
    } // for
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


  public static String damagesToString(List<Damage> list) {
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
   * Bounds checking for a tile fitting into the background
   * @param backgroundSize : width and height of the background (in pixels)
   * @param col axial position of the tile
   * @param row axial position of the tile
   * @return true if the tile fits on the map without drawing off the border
   */
  public static boolean tileFitsInBackground(int backgroundSize, int row, int col) {
    int x = (backgroundSize / 2) + Math.round(EditMapView.HEX_SIZE * 1.5f  * col) - (EditMapView.TILE_WIDTH / 2);
    int y = (backgroundSize / 2) + Math.round(EditMapView.HEX_SIZE * EditMapView.SQRT_3 * (row + (col / 2f))) - (EditMapView.TILE_HEIGHT / 2);
    boolean fits = ((x > 1) && (y > 1) &&
        ((x + EditMapView.TILE_WIDTH) < backgroundSize-1) &&
        ((y + EditMapView.TILE_HEIGHT) < backgroundSize-1));
    Log.d("fits", "x="+x+" y="+y+" fits="+fits+" bgSize="+backgroundSize+" TILE_WIDTH="+EditMapView.TILE_WIDTH);
    return fits;
  }

}
