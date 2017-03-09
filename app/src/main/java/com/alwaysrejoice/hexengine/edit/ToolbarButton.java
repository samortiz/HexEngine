package com.alwaysrejoice.hexengine.edit;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class ToolbarButton {

  public Bitmap img;
  Rect position;
  public boolean selected;

  public ToolbarButton(Bitmap img, Rect position) {
    this.img = img;
    this.position = position;
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

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
