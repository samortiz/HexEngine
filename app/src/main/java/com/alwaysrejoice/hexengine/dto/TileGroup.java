package com.alwaysrejoice.hexengine.dto;

import java.util.List;

public class TileGroup {
  private String name;
  List<TileType> tiles;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TileType> getTiles() {
    return tiles;
  }

  public void setTiles(List<TileType> tiles) {
    this.tiles = tiles;
  }
}
