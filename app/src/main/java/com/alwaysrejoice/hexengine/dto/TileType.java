package com.alwaysrejoice.hexengine.dto;

/**
 * All the things that can be put into a TileGroup
 * These are items in the popup popup menu
 */
public interface TileType {

  public static enum TILE_TYPE {BACKGROUND, UNIT, SYSTEM};

  public TILE_TYPE getTileType();

  public String getName();
  public void setName(String name);

  public Image getImg();
  public void setImg(Image img);

}
