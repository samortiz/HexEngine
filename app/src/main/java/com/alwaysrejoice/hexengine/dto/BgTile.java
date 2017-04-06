package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

public class BgTile implements TileType {

  private String id;
  private String name;
  private String type;
  private Bitmap bitmap;

  public BgTile() {}

  public BgTile(String id) {
    this.id = id;
  }

  public BgTile(String id, String name, String type, Bitmap bitmap) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.bitmap = bitmap;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", type='" + type + '\'' +
        ", bitmap=" + bitmap +
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
