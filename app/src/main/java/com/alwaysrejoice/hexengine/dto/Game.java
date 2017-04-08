package com.alwaysrejoice.hexengine.dto;

import com.alwaysrejoice.hexengine.util.Utils;

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
  private Map<String, BgTile> bgTiles = new HashMap(); // keyed on id
  private List<UnitMap> unitMaps = new ArrayList();
  private Map<String, UnitTile> unitTiles = new HashMap(); // keyed on id
  private List<TileGroup> tileGroups = new ArrayList();
  private Map<String, Mod> mods = new HashMap(); // keyed on id
  private Map<String, Effect> effects = new HashMap(); // keyed on id
  private Map<String, Ability> abilities = new HashMap(); // keyed on id

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

  public Map<String, Ability> getAbilities() {
    return abilities;
  }

  public void setAbilities(Map<String, Ability> abilities) {
    this.abilities = abilities;
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

  @Override
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
        ", abilities=" + abilities +
        ", teams=" + teams +
        ", bgTypes=" + bgTypes +
        ", damageTypes=" + damageTypes +
        ", attr=" + attr +
        '}';
  }

  /**
   * Creates the basic minimum data required for a game to work
   */
  public static void setupNewGame(Game game) {
    game.setTeams(Utils.makeList("Player", "Computer"));
    game.setAttr(Utils.makeList("Living", "Flies"));
    game.setDamageTypes(Utils.makeList("Slash", "Poison"));
    game.setBgTypes(Utils.makeList("Grass", "Water"));

    // Default Mods
    List<ModParam> params = new ArrayList<>();
    params.add(new ModParam("damage", ModParam.TYPE.Damage));
    Mod dmg = new Mod(Utils.generateUniqueId(), "Damage", Mod.TYPE_MOD, params, "applyDamage(self, target, damage);");
    game.getMods().put(dmg.getId(), dmg);

    params = new ArrayList<>();
    params.add(new ModParam("damage", ModParam.TYPE.Damage));
    params.add(new ModParam("range", ModParam.TYPE.Integer));
    Mod areaDmg = new Mod(Utils.generateUniqueId(), "Area Damage", Mod.TYPE_MOD_LOC, params, "for (Unit target : allUnitsInRange(x,y,range) { applyDamage(self, target, damage); }");
    game.getMods().put(areaDmg.getId(), areaDmg);

    Mod rule = new Mod(Utils.generateUniqueId(), "All", Mod.TYPE_RULE, new ArrayList<ModParam>(), "return true");
    game.getMods().put(rule.getId(), rule);

    Mod ruleLoc = new Mod(Utils.generateUniqueId(), "Anywhere", Mod.TYPE_RULE_LOC, new ArrayList<ModParam>(), "return true");
    game.getMods().put(ruleLoc.getId(), ruleLoc);

  }


}
