package com.alwaysrejoice.hexengine.dto;

public class TileType {
  // Built-in tile types (assets)
  public static final TileType[] SYSTEM_TILE_TYPES = {
      new TileType("system-hand", "system", "hand.png"),
      new TileType("system-eraser", "system", "eraser.png"),
      new TileType("system-save", "system", "void.png"),
      new TileType("system-exit", "system", "green.png"),
      new TileType("grass", "grass", "grass.png"),
      new TileType("grass2", "grass", "grass2.png"),
      new TileType("green", "grass", "green.png"),
      new TileType("tree", "tree", "tree.png"),
      new TileType("tree2", "tree", "tree2.png"),
      new TileType("water", "water", "water.png"),
      new TileType("mountain", "mountain", "mountain.png"),
  };

  public static final TileType SYSTEM_HAND = SYSTEM_TILE_TYPES[0];
  public static final TileType SYSTEM_ERASER = SYSTEM_TILE_TYPES[1];
  public static final TileType SYSTEM_SAVE = SYSTEM_TILE_TYPES[2];
  public static final TileType SYSTEM_EXIT = SYSTEM_TILE_TYPES[3];

  private String name;
  private String type;
  private String fileName;


  public TileType() { }

  public TileType(String name, String type, String fileName) {
    this.name = name;
    this.type = type;
    this.fileName = fileName;
  }

  public String getName() { return name; }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
}
