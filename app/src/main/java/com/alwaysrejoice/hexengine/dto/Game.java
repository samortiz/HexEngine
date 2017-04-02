package com.alwaysrejoice.hexengine.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
  public static final int MAX_GAME_WIDTH = 100;
  public static final int MAX_GAME_HEIGHT = 100;
  public static final int MIN_GAME_WIDTH = 10;
  public static final int MIN_GAME_HEIGHT = 10;


  private GameInfo gameInfo =  new GameInfo();
  private List<BgMap> bgMaps = new ArrayList();
  private Map<String, BgTile> bgTiles = new HashMap(); // keyed on name
  private List<UnitMap> unitMaps = new ArrayList();
  private Map<String, UnitTile> unitTiles = new HashMap(); // keyed on name
  private List<TileGroup> tileGroups = new ArrayList();
  private Map<String, Mod> mods = new HashMap(); // keyed on name
  private Map<String, Effect> effects = new HashMap(); // keyed on name

  // Lists of Strings
  private List<String> teams = new ArrayList<>();
  private List<String> bgTypes = new ArrayList<>();
  private List<String> damageTypes = new ArrayList<>();
  private List<String> attr = new ArrayList<>();

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

  public Map<String, Mod> getMods() {
    return mods;
  }

  public void setMods(Map<String, Mod> mods) {
    this.mods = mods;
  }

  public Map<String, Effect> getEffects() {
    return effects;
  }

  public void setEffects(Map<String, Effect> effects) {
    this.effects = effects;
  }

  public List<String> getTeams() {
    return teams;
  }

  public void setTeams(List<String> teams) {
    this.teams = teams;
  }

  public List<String> getBgTypes() {
    return bgTypes;
  }

  public void setBgTypes(List<String> bgTypes) {
    this.bgTypes = bgTypes;
  }

  public List<String> getDamageTypes() {
    return damageTypes;
  }

  public void setDamageTypes(List<String> damageTypes) {
    this.damageTypes = damageTypes;
  }

  public List<String> getAttr() {
    return attr;
  }

  public void setAttr(List<String> attr) {
    this.attr = attr;
  }

  public String toString() {
    return "Game{" +
        "gameInfo=" + gameInfo +
        ", bgMaps=" + bgMaps +
        ", bgTiles=" + bgTiles +
        ", unitMaps=" + unitMaps +
        ", unitTiles=" + unitTiles +
        ", tileGroups=" + tileGroups +
        ", mods=" + mods +
        ", effects=" + effects +
        ", teams=" + teams +
        ", bgTypes=" + bgTypes +
        ", damageTypes=" + damageTypes +
        ", attr=" + attr +
        '}';
  }
}
