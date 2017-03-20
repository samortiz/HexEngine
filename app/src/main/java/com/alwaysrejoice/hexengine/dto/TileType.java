package com.alwaysrejoice.hexengine.dto;

public class TileType {
  // Built-in tile types (assets)
  public static final TileType[] SYSTEM_TILE_TYPES = {
      new TileType("system-hand", "system", "hand.png"),
      new TileType("system-eraser", "system", "eraser.png"),
      new TileType("system-save", "system", "save.png"),
      new TileType("system-exit", "system", "exit.png"),
      new TileType("grass", "grass", "grass.png"),
      new TileType("grass2", "grass", "grass2.png"),
      new TileType("grass3", "grass", "grass3.png"),
      new TileType("grass4", "grass", "grass4.png"),
      new TileType("grass5", "grass", "grass5.png"),
      new TileType("grass6", "grass", "flower.png"),
      new TileType("grass7", "grass", "flower2.png"),
      new TileType("forest", "forest", "forest.png"),
      new TileType("forest2", "forest", "forest2.png"),
      new TileType("forest3", "forest", "tree.png"),
      new TileType("forest4", "forest", "tree2.png"),
      new TileType("water", "water", "water.png"),
      new TileType("water2", "water", "water2.png"),
      new TileType("mountain", "mountain", "mountain.png"),
      new TileType("mountain2", "mountain", "mountain2.png"),
      new TileType("bridge", "road", "bridge.png"),
      new TileType("bridge2", "road", "bridge2.png"),
      new TileType("bridge3", "road", "bridge3.png"),
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
