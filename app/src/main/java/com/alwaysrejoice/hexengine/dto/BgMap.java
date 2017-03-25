package com.alwaysrejoice.hexengine.dto;

public class BgMap {
  private int col;
  private int row;
  private String name;

  public BgMap() {}

  public BgMap(int col, int row, String name) {
    this.col = col;
    this.row = row;
    this.name = name;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "BgMap{" +
        "col=" + col +
        ", row=" + row +
        ", name='" + name + '\'' +
        '}';
  }
}
