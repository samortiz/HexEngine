package com.alwaysrejoice.hexengine.dto;

import java.util.ArrayList;
import java.util.List;

public class TileGroup {
  private String id;
  private String name = "";
  List<TileTypeLink> tileLinks = new ArrayList<>();

  public TileGroup() {}

  public TileGroup(String id) {
    this.id = id;
  }

  public TileGroup(String id, String name, List<TileTypeLink> tileLinks) {
    this.id = id;
    this.name = name;
    this.tileLinks = tileLinks;
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

  public List<TileTypeLink> getTileLinks() {
    return tileLinks;
  }

  public void setTileLinks(List<TileTypeLink> tileLinks) {
    this.tileLinks = tileLinks;
  }

  @Override
  public String toString() {
    return "TileGroup{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", tileLinks=" + tileLinks +
        '}';
  }
}
