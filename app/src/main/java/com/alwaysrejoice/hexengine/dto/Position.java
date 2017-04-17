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
   * Convert to cube coordinates (three axis)
   */
  public int getX() {
    return col;
  }

  /**
   * Convert to cube coordinates (three axis)
   */
  public int getY() {
    return -col-row;
  }

  /**
   * Convert to cube coordinates (three axis)
   */
  public int getZ() {
    return row;
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

  /**
   * @return The position that is nearest from the list
   */
  public Position getNearest(List<Position> posList) {
    int minDistance = -1;
    Position closestPos = null;
    for (Position pos : posList) {
      int distance = this.distanceTo(pos);
      if ((distance < minDistance) || (minDistance == -1)) {
        minDistance = distance;
        closestPos = pos;
      }
    } // for pos
    return closestPos;
  }


  /**
   * @return all the positions within range of this one
   */
  public List<Position> posInRange(int range) {
    List<Position> area = new ArrayList<>();
    for (int dx=-range; dx<=range; dx++) {
      for (int dy=Math.max(-range, -dx-range); dy<=Math.min(range, -dx+range); dy++) {
        int dz = -dx-dy;
        area.add(this.add(new Position(dz, dx)));
      }
    }
    return area;
  }

  /**
   * Finds all the hexagons on the path to the specified position
   */
  public List<Position> lineTo(Position pos) {
    List<Position> line = new ArrayList<>();
    int distance = distanceTo(pos);
    for (int i=0; i<=distance; i++) {
      line.add(toPosition(cubeLerp(this, pos, (1.0f/distance * i))));
    }
    return line;
  }


  // -------------------------------------- Private helper methods ---------------------------------------------

  /** Helper function : Linear interpolation */
  private float lerp(float a, float b, float t) {
    return a + (b - a) * t;
  }

  private float[] cubeLerp(Position a, Position b, float t) {
    // Contains x,y,z as floats
    float[] cube = new float[3];
    cube[0] = lerp(a.getX(), b.getX(), t);
    cube[1] = lerp(a.getY(), b.getY(), t);
    cube[2] = lerp(a.getZ(), b.getZ(), t);
    return cube;
  }

  /** Rounds a floating point cube into an axial int */
  private Position toPosition(float[] pos) {
    int rx = Math.round(pos[0]); // x
    int ry = Math.round(pos[1]); // y
    int rz = Math.round(pos[2]); // z
    float x_diff = Math.abs(rx - pos[0]); // x
    float  y_diff = Math.abs(ry - pos[1]); // y
    float z_diff = Math.abs(rz - pos[2]); //z
    if ((x_diff > y_diff) && (x_diff > z_diff)) {
      rx = -ry-rz;
    } else if (y_diff > z_diff) {
      ry = -rx-rz;
    } else {
      rz = -rx-ry;
    }
    // Convert cube to axial
    return new Position(rz, rx);
  }


}
