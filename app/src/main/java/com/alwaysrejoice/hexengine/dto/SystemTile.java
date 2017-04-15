package com.alwaysrejoice.hexengine.dto;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is for system used and stored tiles
 * These are not saved in any game files and are stored in assets
 */
public class SystemTile implements TileType {

  // Holds all the system tile names
  public static enum NAME {
    HAND ("hand"),
    ERASER ("eraser"),
    SETTINGS ("settings"),
    SAVE ("save"),
    EXIT ("exit"),
    SELECTED ("selected"),
    MOVE_DOT ("greendot"),
    LOOP ("loop");
    private final String name;
    private NAME(String s) {
      name = s;
    }
    public boolean equalsName(String otherName) {
      return name.equals(otherName);
    }
    public String toString() {
      return this.name;
    }
  }
  // Cached SystemTile values
  private static Map<NAME, SystemTile> tiles = null;

  private String name;
  private Bitmap bitmap;

  /**
   * Loads the system tiles. This should be called before constructing any SystemTiles
   */
  public static void init(AssetManager assetManager) {
    if (tiles != null) {
      // initialization is already done
      return;
    }
    tiles = new HashMap();
    InputStream inputStream = null;
    try {
      for (NAME name : NAME.values()) {
        String fileName = "images/system/" + name.toString() + ".png";
        inputStream = assetManager.open(fileName);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        tiles.put(name, new SystemTile(name.toString(), bitmap));
      } // for
    } catch (Exception e) {
      Log.e("SystemTile", "Error loading system tiles", e);
    }
  }

  public static SystemTile getTile(NAME name) {
    if (tiles == null) throw new RuntimeException("Error! You must call SystemTile.init before calling SystemTile.getTile");
    return tiles.get(name);
  }

  // ----------------- Instance Methods -------------------------------------

  public SystemTile(String name, Bitmap bitmap) {
    if (tiles == null) throw new RuntimeException("Error! You must call SystemTile.init before creating tiles");
    this.name = name;
    this.bitmap = bitmap;
  }

  public TILE_TYPE getTileType() {
    return TILE_TYPE.SYSTEM;
  }

  @Override
  public String getId() {
    return name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Bitmap getBitmap() {
    return bitmap;
  }

  @Override
  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  @Override
  public int compareTo(Object o) {
    if (o == null) {
      return 0;
    }
    return this.getName().compareTo(((TileType)o).getName());
  }

  @Override
  public String toString() {
    return "SystemTile{" +
        "name='" + name + '\'' +
        ", bitmap=" + bitmap +
        '}';
  }
}
