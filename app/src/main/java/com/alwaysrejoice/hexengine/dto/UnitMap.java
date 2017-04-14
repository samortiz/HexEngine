package com.alwaysrejoice.hexengine.dto;

public class UnitMap {
  private Position pos;
  private String unitTileId;

  public UnitMap() {}

  public UnitMap(int row, int col, String unitTileId) {
    this.pos = new Position(row, col);
    this.unitTileId = unitTileId;
  }

  public UnitMap(Position pos, String unitTileId) {
    this.pos = pos;
    this.unitTileId = unitTileId;
  }

  public Position getPos() {
    return pos;
  }

  public void setPos(Position pos) {
    this.pos = pos;
  }

  public String getUnitTileId() {
    return unitTileId;
  }

  public void setUnitTileId(String unitTileId) {
    this.unitTileId = unitTileId;
  }

  public int getRow() {
    return pos.getRow();
  }

  public int getCol() {
    return pos.getCol();
  }

  @Override
  public String toString() {
    return "UnitMap{" +
        "pos=" + pos +
        ", unitTileId='" + unitTileId + '\'' +
        '}';
  }
}
