package com.alwaysrejoice.hexengine.dto;

public class UnitTile implements TileType {
  private String name;
  private String type;
  private Image img;

  private int visibleRange;
  private int moveRange;

  public UnitTile() {}

  public UnitTile(String name, String type, Image img) {
    this.name = name;
    this.type = type;
    this.img = img;
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

  @Override
  public Image getImg() {
    return img;
  }

  @Override
  public void setImg(Image img) {
    this.img = img;
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
}
