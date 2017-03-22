package com.alwaysrejoice.hexengine.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
  private GameInfo gameInfo =  new GameInfo();
  private List<BgMap> bgMaps = new ArrayList();
  private Map<String, BgTile> bgTiles = new HashMap();
  private List<UnitMap> unitMaps = new ArrayList();
  private Map<String, UnitTile> unitTiles = new HashMap();
  private List<TileGroup> tileGroups = new ArrayList();

  public String toString() {
    return gameInfo.getName()+" "+gameInfo.getWidth()+"x"+gameInfo.getHeight();
  }

  public GameInfo getGameInfo() {
    return gameInfo;
  }

  public void setGameInfo(GameInfo gameInfo) {
    this.gameInfo = gameInfo;
  }

  public List<BgMap> getBgMaps() {
    return bgMaps;
  }

  public void setBgMaps(List<BgMap> bgMaps) {
    this.bgMaps = bgMaps;
  }

  public Map<String, BgTile> getBgTiles() {
    return bgTiles;
  }

  public void setBgTiles(Map<String, BgTile> bgTiles) {
    this.bgTiles = bgTiles;
  }

  public List<UnitMap> getUnitMaps() {
    return unitMaps;
  }

  public void setUnitMaps(List<UnitMap> unitMaps) {
    this.unitMaps = unitMaps;
  }

  public Map<String, UnitTile> getUnitTiles() {
    return unitTiles;
  }

  public void setUnitTiles(Map<String, UnitTile> unitTiles) {
    this.unitTiles = unitTiles;
  }

  public List<TileGroup> getTileGroups() {
    return tileGroups;
  }

  public void setTileGroups(List<TileGroup> tileGroups) {
    this.tileGroups = tileGroups;
  }
}
