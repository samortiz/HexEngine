package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

public class TileType {
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
