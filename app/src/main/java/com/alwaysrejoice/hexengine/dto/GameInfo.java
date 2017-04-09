package com.alwaysrejoice.hexengine.dto;

import static android.R.attr.width;

public class GameInfo {
  private String name = "";
  private int size = 12;
  private Color backgroundColor = new Color(00, 50, 50);

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getWidth() {
    return width;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  @Override
  public String toString() {
    return "GameInfo{" +
        "name='" + name + '\'' +
        ", width=" + width +
        ", size=" + size +
        ", backgroundColor=" + backgroundColor +
        '}';
  }
}
