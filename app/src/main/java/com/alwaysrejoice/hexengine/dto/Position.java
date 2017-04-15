package com.alwaysrejoice.hexengine.dto;

import java.util.ArrayList;
import java.util.List;

public class Position {
  public static final Position[] DIRECTIONS = {
      new Position( 1,  0),
      new Position( 1, -1),
      new Position( 0, -1),
      new Position(-1,  0),
      new Position(-1,  1),
      new Position( 0,  1)
  };

  int row = 0;
  int col = 0;

  public Position() {}

  public Position(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public int getCol() {
    return col;
  }

  public void setCol(int col) {
    this.col = col;
  }

  @Override
  public String toString() {
    return "("+row + "," + col+")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Position)) {
      return false;
    }
    Position position = (Position) o;
    if (row != position.row) {
      return false;
    }
    return col == position.col;
  }

  public boolean equals(int row, int col) {
    return (this.row == row) && (this.col == col);
  }

  @Override
  public int hashCode() {
    int result = row;
    result = 31 * result + col;
    return result;
  }

  /**
   * Add this position with another (assuming one is an offset)
   */
  public Position add(Position pos) {
    return new Position(row+pos.row, col+pos.col);
  }

  /**
   * Gets all the hexagons neighboring this one
   */
  public List<Position> getNeighbors() {
    List<Position> neighbors = new ArrayList<>();
    for (Position dir : DIRECTIONS) {
      neighbors.add(this.add(dir));
    }
    return neighbors;
  }

  /**
   * @return The distance (in steps) between this position and pos
   */
  public int distanceTo(Position pos) {
    return (Math.abs(col - pos.col) +
           Math.abs(col + row - pos.col - pos.row) +
           Math.abs(row - pos.row)) / 2;
  }
}
