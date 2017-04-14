package com.alwaysrejoice.hexengine.dto;

import android.util.Log;

/**
 * This contains info that will uniquely identify a TileType
 * This enables you to store a "link" or ID so you can lookup a tile
 */
public class TileTypeLink {

  public TileType.TILE_TYPE tileType;
  public String id;

  /**
   * Looks up a tile in the game that matches the link
   * @param link  uniquely identifies a tile
   * @return  the found tile
   */
  public static TileType getTile(TileTypeLink link, Game game) {
    if (TileType.TILE_TYPE.BACKGROUND.equals(link.getTileType())) {
      for (String tileId : game.getBgTiles().keySet()) {
        BgTile tile = game.getBgTiles().get(tileId);
        if (link.getId().equals(tile.getId())) {
          return tile;
        }
      } // for
    } else if (TileType.TILE_TYPE.UNIT_TILE.equals(link.getTileType())) {
      for (String tileId : game.getUnitTiles().keySet()) {
        UnitTile tile = game.getUnitTiles().get(tileId);
        if (link.getId().equals(tile.getId())) {
          return tile;
        }
      } // for
    }
    Log.e("tileTypeLink", "Error! Could not find tile of type="+link.getTileType()+" with id="+link.getId());
    return null;
  }

  public TileTypeLink() {}

  public TileTypeLink(TileType.TILE_TYPE tileType, String id) {
    this.tileType = tileType;
    this.id = id;
  }

  public TileType.TILE_TYPE getTileType() {
    return tileType;
  }

  public void setTileType(TileType.TILE_TYPE tileType) {
    this.tileType = tileType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "TileTypeLink{" +
        "tileType=" + tileType +
        ", id='" + id + '\'' +
        '}';
  }
}
