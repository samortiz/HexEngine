package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

public class UnitTile implements TileType {

  private String name;
  private String type;
  private Bitmap bitmap;
  private int visibleRange;
  private int moveRange;

  public UnitTile() {}

  public UnitTile(String name, String type, Bitmap bitmap) {
    this.name = name;
    this.type = type;
    this.bitmap = bitmap;
  }

  public TILE_TYPE getTileType() {
    return TILE_TYPE.UNIT;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  public int getVisibleRange() {
    return visibleRange;
  }

  public void setVisibleRange(int visibleRange) {
    this.visibleRange = visibleRange;
  }

  public int getMoveRange() {
    return moveRange;
  }

  public void setMoveRange(int moveRange) {
    this.moveRange = moveRange;
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
    return "UnitTile{" +
        "name='" + name + '\'' +
        ", type='" + type + '\'' +
        ", bitmap=" + bitmap +
        ", visibleRange=" + visibleRange +
        ", moveRange=" + moveRange +
        '}';
  }
}
