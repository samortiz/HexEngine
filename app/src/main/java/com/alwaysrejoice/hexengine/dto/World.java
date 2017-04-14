package com.alwaysrejoice.hexengine.dto;

import com.alwaysrejoice.hexengine.util.FileUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

  private String id;
  private String name;
  private GameInfo gameInfo =  new GameInfo();
  private List<BgMap> bgMaps = new ArrayList();
  private Map<String, BgTile> bgTiles = new HashMap(); // keyed on id
  private List<Unit> units = new ArrayList();
  private Map<String, Mod> mods = new HashMap(); // keyed on id

  public World() {
  }

  public World(Game game) {
    this.id = Utils.generateUniqueId();
    // Find a name that doesn't exist on the file system yet
    String gameName = game.getGameInfo().getName();
    this.name = gameName;
    int counter = 2;
    while (FileUtils.fileExists(FileUtils.WORLDS_DIR, name+".json")) {
      name = gameName+"-"+counter;
      counter ++;
    }
    this.gameInfo = game.getGameInfo();
    this.bgMaps.addAll(game.getBgMaps());
    this.bgTiles.putAll(game.getBgTiles());
    for (UnitMap unitMap : game.getUnitMaps()) {
      this.units.add(new Unit(unitMap, game));
    }
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

  public List<Unit> getUnits() {
    return units;
  }

  public void setUnits(List<Unit> units) {
    this.units = units;
  }

  public Map<String, Mod> getMods() {
    return mods;
  }

  public void setMods(Map<String, Mod> mods) {
    this.mods = mods;
  }

  @Override
  public String toString() {
    return "World{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", gameInfo=" + gameInfo +
        ", bgMaps=" + bgMaps +
        ", bgTiles=" + bgTiles +
        ", units=" + units +
        ", mods=" + mods +
        '}';
  }
}
