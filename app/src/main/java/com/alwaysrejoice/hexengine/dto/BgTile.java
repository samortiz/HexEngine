package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

import java.io.Serializable;

public class BgTile implements TileType {

  private String name;
  private String type;
  private Bitmap bitmap;

  public BgTile() {}

  public BgTile(String name, String type, Bitmap bitmap) {
    this.name = name;
    this.type = type;
    this.bitmap = bitmap;
  }

  public TILE_TYPE getTileType() {
    return TILE_TYPE.BACKGROUND;
  }

  public String getName() {
    return name;
  }

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

  @Override
  public String toString() {
    return "BgTile{" +
        "name='" + name + '\'' +
        ", type='" + type + '\'' +
        '}';
  }

  @Override
  public int compareTo(Object o) {
    if (o == null) {
      return 0;
    }
    return this.getName().compareTo(((TileType)o).getName());
  }

}
