package com.alwaysrejoice.hexengine.dto;

public class GameInfo {
  private String name = "";
  private int width = 12;
  private int height = 12;
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

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
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
        ", height=" + height +
        ", backgroundColor=" + backgroundColor +
        '}';
  }
}
