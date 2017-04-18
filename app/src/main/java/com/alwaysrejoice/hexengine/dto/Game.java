package com.alwaysrejoice.hexengine.dto;

import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
  public static final int MAX_GAME_SIZE = 50;
  public static final int MIN_GAME_SIZE = 5;

  private GameInfo gameInfo =  new GameInfo();
  private List<BgMap> bgMaps = new ArrayList();
  private Map<String, BgTile> bgTiles = new HashMap(); // keyed on id
  private List<UnitMap> unitMaps = new ArrayList();
  private Map<String, UnitTile> unitTiles = new HashMap(); // keyed on id
  private List<TileGroup> tileGroups = new ArrayList();
  private Map<String, Mod> mods = new HashMap(); // keyed on id
  private Map<String, EffectTile> effects = new HashMap(); // keyed on id
  private Map<String, AbilityTile> abilities = new HashMap(); // keyed on id
  private Triggers triggers = new Triggers();
  private List<Team> teams = new ArrayList<>();
  private List<AI> ais = new ArrayList<>();

  // Lists of Strings
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

  public Map<String, EffectTile> getEffects() {
    return effects;
  }

  public void setEffects(Map<String, EffectTile> effects) {
    this.effects = effects;
  }

  public Map<String, AbilityTile> getAbilities() {
    return abilities;
  }

  public void setAbilities(Map<String, AbilityTile> abilities) {
    this.abilities = abilities;
  }

  public Triggers getTriggers() {
    return triggers;
  }

  public void setTriggers(Triggers triggers) {
    this.triggers = triggers;
  }

  public List<Team> getTeams() {
    return teams;
  }

  public void setTeams(List<Team> teams) {
    this.teams = teams;
  }

  public List<AI> getAis() {
    return ais;
  }

  public void setAis(List<AI> ais) {
    this.ais = ais;
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
    return "Game{"+
        "gameInfo="+gameInfo+
        ", bgMaps="+bgMaps+
        ", bgTiles="+bgTiles+
        ", unitMaps="+unitMaps+
        ", unitTiles="+unitTiles+
        ", tileGroups="+tileGroups+
        ", mods="+mods+
        ", effects="+effects+
        ", abilities="+abilities+
        ", triggers="+triggers+
        ", teams="+teams+
        ", ais="+ais+
        ", bgTypes="+bgTypes+
        ", damageTypes="+damageTypes+
        ", attr="+attr+
        '}';
  }

  /**
   * Creates the basic minimum data required for a game to work
   */
  public static void setupNewGame(Game game) {
    game.setAttr(Utils.makeList("Living", "Flies"));
    game.setDamageTypes(Utils.makeList(new String[]{"Slash", "Pierce", "Bludgeon", "Poison", "Fire"}));
    game.setBgTypes(Utils.makeList(new String[]{"Grass", "Water", "Forest", "Mountain"}));

    // Setup default AIs
    AI humanAI = new AI(Utils.generateUniqueId(), "Human", "");
    game.getAis().add(humanAI);
    AI aggressiveAI = new AI(Utils.generateUniqueId(), "Aggressive", "");
    game.getAis().add(aggressiveAI);

    // Setup default teams
    game.getTeams().add(new Team(Utils.generateUniqueId(), "Player", humanAI.getId()));
    game.getTeams().add(new Team(Utils.generateUniqueId(), "Enemy", aggressiveAI.getId()));

    // Default Mods
    List<ModParam> params = new ArrayList<>();
    params.add(new ModParam("damage", ModParam.TYPE.Damage));
    Mod dmg = new Mod(Utils.generateUniqueId(), "Damage", Mod.TYPE_MOD, params, "tools.applyDamage(self, target, damage);");
    game.getMods().put(dmg.getId(), dmg);

    params = new ArrayList<>();
    params.add(new ModParam("amount", ModParam.TYPE.Damage));
    Mod heal= new Mod(Utils.generateUniqueId(), "Heal", Mod.TYPE_MOD, params, "tools.heal(target, damage);");
    game.getMods().put(heal.getId(), heal);

    params = new ArrayList<>();
    params.add(new ModParam("damage", ModParam.TYPE.Damage));
    params.add(new ModParam("range", ModParam.TYPE.Integer));
    Mod areaDmg = new Mod(Utils.generateUniqueId(), "Damage Area", Mod.TYPE_MOD_LOC, params,
          "log('TODO: area damage')");
    game.getMods().put(areaDmg.getId(), areaDmg);

    Mod entangle = new Mod(Utils.generateUniqueId(), "Entangle", Mod.TYPE_MOD, params,
        "if (target.storage.get('origActionMax') == null) {" +
        "  target.storage.put('origActionMax', target.actionMax); "+
        "}" +
        "target.actionMax = 0;" +
        "target.action = 0;");
    game.getMods().put(entangle.getId(), entangle);

    Mod entangleRelease = new Mod(Utils.generateUniqueId(), "Entangle Release", Mod.TYPE_MOD, params,
        "if (target.storage.get('origActionMax') != null) {" +
        "  target.actionMax = target.storage.get('origActionMax');" +
        "  target.action = target.actionMax;" +
        "}");
    game.getMods().put(entangleRelease.getId(), entangleRelease);

    // Rules
    Mod enemies = new Mod(Utils.generateUniqueId(), "Enemy", Mod.TYPE_RULE, new ArrayList<ModParam>(), "self.teamId != target.teamId");
    game.getMods().put(enemies.getId(), enemies);

    Mod self = new Mod(Utils.generateUniqueId(), "Self", Mod.TYPE_RULE, new ArrayList<ModParam>(), "target.id == self.id");
    game.getMods().put(self.getId(), self);

    Mod friend = new Mod(Utils.generateUniqueId(), "Friend", Mod.TYPE_RULE, new ArrayList<ModParam>(), "(self.id != target.id) && (self.teamId == target.teamId)");
    game.getMods().put(friend.getId(), friend);

    Mod ruleAnyone = new Mod(Utils.generateUniqueId(), "Anyone", Mod.TYPE_RULE, new ArrayList<ModParam>(), "true");
    game.getMods().put(ruleAnyone.getId(), ruleAnyone);

    Mod ruleLoc = new Mod(Utils.generateUniqueId(), "Anywhere", Mod.TYPE_RULE_LOC, new ArrayList<ModParam>(), "true");
    game.getMods().put(ruleLoc.getId(), ruleLoc);

    // Triggers
    Mod allEnemiesDefeated = new Mod(Utils.generateUniqueId(), "All Enemies Defeated", Mod.TYPE_TRIGGER, new ArrayList<ModParam>(),
         "if (tools.getAllOthers(teamId).size() == 0) {" +
             " world.victory = true;" +
             "} else if (tools.getTeamUnits(teamId) == 0) {" +
             " world.defeat = true;" +
             "}");
    game.getMods().put(allEnemiesDefeated.getId(), allEnemiesDefeated);


  }

}
