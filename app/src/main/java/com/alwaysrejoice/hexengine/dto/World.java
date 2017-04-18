package com.alwaysrejoice.hexengine.dto;

import com.alwaysrejoice.hexengine.util.FileUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

  public String id;
  public String name;
  public int turnCounter = 0;
  public GameInfo gameInfo =  new GameInfo();
  public List<BgMap> bgMaps = new ArrayList();
  public Map<String, BgTile> bgTiles = new HashMap(); // keyed on id
  public List<Unit> units = new ArrayList();
  public Map<String, Mod> mods = new HashMap(); // keyed on id
  public List<Team> teams = new ArrayList<>();
  public List<AI> ais =  new ArrayList<>();
  public Map<String, Object> storage = new HashMap(); // script object storage
  public Triggers triggers = new Triggers();
  public boolean victory = false; // set to true to end the game
  public boolean defeat = false; // set to true to end the game
  public String myTeamId = null; // Set at runtime when loading WorldView This is calculated

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
    this.mods.putAll(game.getMods());
    this.teams.addAll(game.getTeams());
    this.ais.addAll(game.getAis());
    this.triggers = game.getTriggers();
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

  public int getTurnCounter() {
    return turnCounter;
  }

  public void setTurnCounter(int turnCounter) {
    this.turnCounter = turnCounter;
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

  public Map<String, Object> getStorage() {
    return storage;
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

  public void setStorage(Map<String, Object> storage) {
    this.storage = storage;
  }

  public Triggers getTriggers() {
    return triggers;
  }

  public void setTriggers(Triggers triggers) {
    this.triggers = triggers;
  }

  public boolean isVictory() {
    return victory;
  }

  public void setVictory(boolean victory) {
    this.victory = victory;
  }

  public boolean isDefeat() {
    return defeat;
  }

  public void setDefeat(boolean defeat) {
    this.defeat = defeat;
  }

  public String getMyTeamId() {
    return myTeamId;
  }

  public void setMyTeamId(String myTeamId) {
    this.myTeamId = myTeamId;
  }

  @Override
  public String toString() {
    return "World{"+
        "id='"+id+'\''+
        ", name='"+name+'\''+
        ", turnCounter="+turnCounter+
        ", gameInfo="+gameInfo+
        ", bgMaps="+bgMaps+
        ", bgTiles="+bgTiles+
        ", units="+units+
        ", mods="+mods+
        ", teams="+teams+
        ", ais="+ais+
        ", storage="+storage+
        ", triggers="+triggers+
        ", victory="+victory+
        ", defeat="+defeat+
        ", myTeamId='"+myTeamId+'\''+
        '}';
  }
}
