package com.alwaysrejoice.hexengine.dto;

public class BgTile implements TileType {
  private String name;
  private String type;
  private Image img;

  public BgTile() {}

  public BgTile(String name, String type, Image img) {
    this.name = name;
    this.type = type;
    this.img = img;
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

  public Image getImg() {
    return img;
  }

  public void setImg(Image img) {
    this.img = img;
  }

}
