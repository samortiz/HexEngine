package com.alwaysrejoice.hexengine.dto;

import java.util.ArrayList;
import java.util.List;

public class TileGroup {
  private String name;
  List<TileTypeLink> tileLinks = new ArrayList<>();

  public TileGroup() {}

  public TileGroup(String name, List<TileTypeLink> tileLinks) {
    this.name = name;
    this.tileLinks = tileLinks;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TileTypeLink> getTileLinks() {
    return tileLinks;
  }

  public void setTileLinks(List<TileTypeLink> tileLinks) {
    this.tileLinks = tileLinks;
  }

  @Override
  public String toString() {
    return "TileGroup{" +
        "name='" + name + '\'' +
        ", tileLinks=" + tileLinks +
        '}';
  }
}
