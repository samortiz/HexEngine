package com.alwaysrejoice.hexengine.dto;

public class UnitMap {
  private int col;
  private int row;
  private String name;

  public UnitMap() {}

  public UnitMap(int col, int row, String name) {
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

}
