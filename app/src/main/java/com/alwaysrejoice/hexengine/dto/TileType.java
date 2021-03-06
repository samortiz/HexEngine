package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

/**
 * All the things that can be put into a TileGroup
 * These are items in the popup popup menu
 */
public interface TileType extends Comparable {
  public static enum TILE_TYPE {BACKGROUND, UNIT_TILE, SYSTEM, EFFECT, ABILITY, UNIT};

  public TILE_TYPE getTileType();

  public String getId();

  public String getName();
  public void setName(String name);

  public Bitmap getBitmap();
  public void setBitmap(Bitmap bitmap);

}
