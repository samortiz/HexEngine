package com.alwaysrejoice.hexengine.dto;

public class BgMap {
  private Position pos;
  private String bgTileId;

  public BgMap() {}

  public BgMap(int row, int col, String bgTileId) {
    this.pos = new Position(row, col);
    this.bgTileId = bgTileId;
  }

  public BgMap(Position pos, String bgTileId) {
    this.pos = pos;
    this.bgTileId = bgTileId;
  }

  public Position getPos() {
    return pos;
  }

  public void setPos(Position pos) {
    this.pos = pos;
  }

  public String getBgTileId() {
    return bgTileId;
  }

  public void setBgTileId(String bgTileId) {
    this.bgTileId = bgTileId;
  }

  public int getCol() {
    return pos.getCol();
  }

  public int getRow() {
    return pos.getRow();
  }

  @Override
  public String toString() {
    return "BgMap{" +
        "pos=" + pos +
        ", bgTileId='" + bgTileId + '\'' +
        '}';
  }
}
