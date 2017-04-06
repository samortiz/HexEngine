package com.alwaysrejoice.hexengine.dto;

public class BgMap {
  private int col;
  private int row;
  private String id;

  public BgMap() {}

  public BgMap(int col, int row, String id) {
    this.col = col;
    this.row = row;
    this.id = id;
  }

  public int getCol() {
    return col;
  }

  public void setCol(int col) {
    this.col = col;
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "BgMap{" +
        "col=" + col +
        ", row=" + row +
        ", id='" + id + '\'' +
        '}';
  }
}
