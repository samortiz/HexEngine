package com.alwaysrejoice.hexengine.dto;

import java.util.List;

public class Background {
  private int width;
  private int height;
  private Color backgroundColor;
  private List<TileType> tileTypes;
  private List<BackgroundTile> tiles;

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public List<TileType> getTileTypes() {
    return tileTypes;
  }

  public void setTileTypes(List<TileType> tileTypes) {
    this.tileTypes = tileTypes;
  }

  public List<BackgroundTile> getTiles() {
    return tiles;
  }

  public void setTiles(List<BackgroundTile> tiles) {
    this.tiles = tiles;
  }
}