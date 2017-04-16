package com.alwaysrejoice.hexengine.util;

import android.util.Log;
import com.alwaysrejoice.hexengine.dto.AI;
import com.alwaysrejoice.hexengine.dto.BgMap;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Damage;
import com.alwaysrejoice.hexengine.dto.Position;
import com.alwaysrejoice.hexengine.dto.Team;
import com.alwaysrejoice.hexengine.dto.Unit;
import com.alwaysrejoice.hexengine.dto.World;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class WorldUtils {
  // Static singleton instances for the current world in progress
  private static World world = null;

  /**
   * Writes out a world to a file, based on the worldName
   */
  public static void saveWorld(World worldToSave) {
    try {
      world = worldToSave;
      String worldJson = Utils.gson.toJson(worldToSave);
      File worldFile = FileUtils.getWorldFile(world.getName());
      Log.d("gameUtils", "saving world "+worldFile+" => "+worldJson);
      org.apache.commons.io.FileUtils.writeStringToFile(worldFile, worldJson, "UTF-8");
    } catch (IOException e) {
      Log.e("IO Error", "Error in saveWorld saving '"+world.getName()+"'", e);
    }
  }

  /**
   * Saves the world currently in progress.
   * NOTE : If no world is currently open this does nothing
   */
  public static void saveWorld() {
    if (world != null) {
      WorldUtils.saveWorld(world);
      Log.d("GameUtils", "Saving world "+world);
    }
  }

  /**
   * Returns the currently loaded world
   * If there is no world loaded this will be null
   */
  public static World getWorld() {
    return world;
  }

  /**
   * Returns the game with the specified name
   * @return a cached Game instance or a newly loaded one
   */
  public static World getWorld(String worldName) {
    if ((world == null) || !world.getName().equals(worldName)) {
      Log.d("GameUtils", "loading " +worldName);
      world = WorldUtils.loadWorld(worldName);
    }
    return world;
  }

  /**
   * Loads a world from a JSON file and returns it
   * @param worldName name of world (and filename without extension)
   */
  public static World loadWorld(String worldName) {
    InputStream inputStream = null;
    try {
      File file = FileUtils.getWorldFile(worldName);
      inputStream = new FileInputStream(file);
      String jsonWorld = IOUtils.toString(inputStream, "UTF-8");
      inputStream.close();
      world = Utils.gson.fromJson(jsonWorld, World.class);
    } catch (IOException e) {
      Log.e("IO Error", "Error in loadWorld loading "+worldName, e);
      if (inputStream != null) try { inputStream.close(); } catch (IOException e1) { e.printStackTrace();}
    }
    return world;
  }

  /**
   * Converts a list of damages into a readable string
   */
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

  /**
   * Gets the team name given the id
   */
  public static String getTeamNameFromId(String teamId) {
    if (teamId == null) {
      return null;
    }
    for (Team t : world.getTeams()) {
      if (teamId.equals(t.getId())) {
        return t.getName();
      }
    } // for
    return null;
  }

  /**
   * Looks up an AI by id
   */
  public static AI getAiById(String aiId) {
    if (aiId == null) {
      return null;
    }
    for (AI ai : world.getAis()) {
      if (aiId.equals(ai.getId())) {
        return ai;
      }
    } // for
    return null;
  }

  /**
   * Lookup all the valid positions given the restrictions
   * @return a list of all the valid posisions
   */
  public static List<Position> validMovePositions(Position origin, int range, List<String> restrict, World world) {
    List<Position> validPositions = new ArrayList<>();
    // nStepsAway[n] contains all the positions that are n steps away from origin
    List<List<Position>> fringes = new ArrayList<>(range);
    List<Position> root = new ArrayList<>();
    root.add(origin);
    fringes.add(root); // index 0

    for (int n=1; n<=range; n++) {
      List<Position> fringe = new ArrayList<>();
      fringes.add(fringe); // index n
      // Go through all the previous step's fringe
      for (Position pos : fringes.get(n-1)) {
        for (Position neighbor : pos.getNeighbors()) {
          if (!validPositions.contains(neighbor) && isValidMovePosition(neighbor, restrict, world)) {
            validPositions.add(neighbor);
            fringe.add(neighbor);
          }
        }  // for neighbor
      } // for pos
    } // for n
    return validPositions;
  }


  /**
   * Looks up the bgTile at the specified position and returns true if the tile
   * is a valid position given the terrain type restrictions
   * @return true if the position is valid, false if not or if there was an error
   */
  public static boolean isValidMovePosition(Position pos, List<String> restrict, World world) {
    String bgTileId = null;
    for (BgMap bgMap : world.getBgMaps()) {
      if ((bgMap.getRow() == pos.getRow()) && (bgMap.getCol() == pos.getCol())) {
        bgTileId = bgMap.getBgTileId();
      }
    } // for
    if (bgTileId == null) {
      return false;
    }
    BgTile bgTile = world.getBgTiles().get(bgTileId);
    if (bgTile == null) {
      Log.d("GameUtils", "Error! Unable to find bgTileId="+bgTileId+" in the list of tiles. The game file is corrupt.");
      return false;
    }
    // You are not allowed to move to that kind of terrain
    if (restrict.contains(bgTile.getType())) {
      return false;
    }
    // Already a unit at that location - you can't move there
    for (Unit unit : world.getUnits()) {
      if (pos.equals(unit.getPos())) {
        return false;
      }
    } // for
    return true;
  }

  /**
   * Finds the first (and only) unit at the selected position. If no unit is there, this will return null
   */
  public static Unit getUnitAt(Position pos, World world) {
    for (Unit unit : world.getUnits()) {
      if (unit.getPos().equals(pos)) {
        return unit;
      }
    }// for
    return null;
  }

}
