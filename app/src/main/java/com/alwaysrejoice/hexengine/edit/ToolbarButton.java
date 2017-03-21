package com.alwaysrejoice.hexengine.edit;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.alwaysrejoice.hexengine.dto.TileType;

import java.util.ArrayList;
import java.util.List;

public class ToolbarButton {

  private String name;
  private TileType.TILE_TYPE type;
  private Bitmap img;
  private Rect position;
  private ToolbarButton parent = null;
  private List<ToolbarButton> children = new ArrayList<ToolbarButton>();
  private Rect popupPosition;

  // Constructors
  public ToolbarButton() { }

  public ToolbarButton(String name, TileType.TILE_TYPE type, Bitmap img, Rect position, ToolbarButton parent) {
    this.name = name;
    this.type = type;
    this.img = img;
    this.position = position;
    this.parent = parent;
  }

  // Custom
  public void addChild(ToolbarButton child) {
    children.add(child);
  }

  /**
   * @return true if this button has children (top level button)
   */
  public boolean hasChildren() {
    return children.size() > 0;
  }


  // Getter / Setter
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TileType.TILE_TYPE getType() {
    return type;
  }

  public void setType(TileType.TILE_TYPE type) {
    this.type = type;
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

  public ToolbarButton getParent() {
    return parent;
  }

  public void setParent(ToolbarButton parent) {
    this.parent = parent;
  }

  public List<ToolbarButton> getChildren() {
    return children;
  }

  public void setChildren(List<ToolbarButton> children) {
    this.children = children;
  }

  public Rect getPopupPosition() {
    return popupPosition;
  }

  public void setPopupPosition(Rect popupPosition) {
    this.popupPosition = popupPosition;
  }
}
