package com.alwaysrejoice.hexengine.edit;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class ToolbarButton {

  public String name;
  public Bitmap img;
  Rect position;

  public ToolbarButton() { }

  public ToolbarButton(String name, Bitmap img, Rect position) {
    this.name = name;
    this.img = img;
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Bitmap getImg() {
    return img;
  }

  public void setImg(Bitmap img) {
    this.img = img;
  }

  public Rect getPosition() {
    return position;
  }

  public void setPosition(Rect position) {
    this.position = position;
  }
}
