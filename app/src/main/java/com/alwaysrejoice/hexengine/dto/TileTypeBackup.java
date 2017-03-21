package com.alwaysrejoice.hexengine.dto;

public class TileTypeBackup {
  // Built-in tile types (assets)
  public static final TileTypeBackup[] SYSTEM_TILE_TYPES = {
      new TileTypeBackup("system-hand", "system", "hand.png"),
      new TileTypeBackup("system-eraser", "system", "eraser.png"),
      new TileTypeBackup("system-save", "system", "save.png"),
      new TileTypeBackup("system-exit", "system", "exit.png"),
      new TileTypeBackup("grass", "grass", "grass.png"),
      new TileTypeBackup("grass2", "grass", "grass2.png"),
      new TileTypeBackup("grass3", "grass", "grass3.png"),
      new TileTypeBackup("grass4", "grass", "grass4.png"),
      new TileTypeBackup("grass5", "grass", "grass5.png"),
      new TileTypeBackup("grass6", "grass", "flower.png"),
      new TileTypeBackup("grass7", "grass", "flower2.png"),
      new TileTypeBackup("forest", "forest", "forest.png"),
      new TileTypeBackup("forest2", "forest", "forest2.png"),
      new TileTypeBackup("forest3", "forest", "tree.png"),
      new TileTypeBackup("forest4", "forest", "tree2.png"),
      new TileTypeBackup("water", "water", "water.png"),
      new TileTypeBackup("water2", "water", "water2.png"),
      new TileTypeBackup("mountain", "mountain", "mountain.png"),
      new TileTypeBackup("mountain2", "mountain", "mountain2.png"),
      new TileTypeBackup("bridge", "road", "bridge.png"),
      new TileTypeBackup("bridge2", "road", "bridge2.png"),
      new TileTypeBackup("bridge3", "road", "bridge3.png"),
  };

  public static final TileTypeBackup SYSTEM_HAND = SYSTEM_TILE_TYPES[0];
  public static final TileTypeBackup SYSTEM_ERASER = SYSTEM_TILE_TYPES[1];
  public static final TileTypeBackup SYSTEM_SAVE = SYSTEM_TILE_TYPES[2];
  public static final TileTypeBackup SYSTEM_EXIT = SYSTEM_TILE_TYPES[3];

  private String name;
  private String type;
  private String fileName;


  public TileTypeBackup() { }

  public TileTypeBackup(String name, String type, String fileName) {
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
