package com.alwaysrejoice.hexengine.edit;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.alwaysrejoice.hexengine.dto.TileType;

import java.util.ArrayList;
import java.util.List;

public class ToolbarButton {

  // NOTE : This id is NOT the unique identifier for this ToolbarButton
  // It is the id of the tile contained in this button.
  // That means there could be two toolbar buttons with the same ID
  // You need to do a compare like (buttonA == buttonB) to determine identity
  private String id;
  private String name;
  private TileType.TILE_TYPE type;
  private Bitmap img;
  private Rect position;
  private ToolbarButton parent = null;
  private List<ToolbarButton> children = new ArrayList<ToolbarButton>();
  private Rect popupPosition;
  private boolean longAlternateLayout = false;

  // Constructors
  public ToolbarButton() { }

  public ToolbarButton(String id, String name, TileType.TILE_TYPE type, Bitmap img, Rect position, ToolbarButton parent) {
    this.id = id;
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public boolean isLongAlternateLayout() {
    return longAlternateLayout;
  }

  public void setLongAlternateLayout(boolean longAlternateLayout) {
    this.longAlternateLayout = longAlternateLayout;
  }

  @Override
  public String toString() {
    return "ToolbarButton{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", type=" + type +
        ", img=" + img +
        ", position=" + position +
        ", parentName=" + ((parent != null) ? parent.getName() : "none") +
        ", children =" + getChildrenNames()+ // Prevent infinite loop by printing parent
        ", popupPosition=" + popupPosition +
        ", longAlternateLayout="+ longAlternateLayout+
        '}';
  }

  /** Returns the names of the child buttons */
  public List<String> getChildrenNames() {
    List<String> childNames = new ArrayList<>();
    for (ToolbarButton child : getChildren()) {
      childNames.add(child.getName());
    }
    return childNames;
  }

}
