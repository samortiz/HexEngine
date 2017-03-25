package com.alwaysrejoice.hexengine.dto;

import android.util.Log;

/**
 * This contains info that will uniquely identify a TileType
 * This enables you to store a "link" or ID so you can lookup a tile
 */
public class TileTypeLink {

  public TileType.TILE_TYPE tileType;
  public String name;

  /**
   * Looks up a tile in the game that matches the link
   * @param link  uniquely identifies a tile
   * @return  the found tile
   */
  public static TileType getTile(TileTypeLink link, Game game) {
    if (TileType.TILE_TYPE.BACKGROUND.equals(link.getTileType())) {
      for (String tileName : game.getBgTiles().keySet()) {
        BgTile tile = game.getBgTiles().get(tileName);
        if (link.getName().equals(tile.getName())) {
          return tile;
        }
      } // for
    } else if (TileType.TILE_TYPE.UNIT.equals(link.getTileType())) {
      for (String tileName : game.getUnitTiles().keySet()) {
        UnitTile tile = game.getUnitTiles().get(tileName);
        if (link.getName().equals(tile.getName())) {
          return tile;
        }
      } // for
    }
    Log.e("tileTypeLink", "Error! Could not find tile of type="+link.getTileType()+" with name="+link.getName());
    return null;
  }

  public TileTypeLink() {}

  public TileTypeLink(TileType.TILE_TYPE tileType, String name) {
    this.tileType = tileType;
    this.name = name;
  }

  public TileType.TILE_TYPE getTileType() {
    return tileType;
  }

  public void setTileType(TileType.TILE_TYPE tileType) {
    this.tileType = tileType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "TileTypeLink{" +
        "tileType=" + tileType +
        ", name='" + name + '\'' +
        '}';
  }
}
