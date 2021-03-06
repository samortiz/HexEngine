package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Unit implements TileType {

  public String id;
  public Position pos;
  public String unitTileId;
  public String name;
  public Bitmap bitmap;
  public String teamId;
  public double hp;
  public double hpMax;
  public double action;
  public double actionMax;
  public List<String> attr = new ArrayList<>();
  public int moveRange;
  public double moveActionCost;
  public List<String> moveRestrict = new ArrayList<>();
  public int sightRange;
  public List<String> sightRestrict = new ArrayList<>();
  public List<Ability> abilities = new ArrayList<>();
  public List<Damage> defence = new ArrayList<>();
  public List<Effect> effects = new ArrayList<>();
  public Map<String, String> storage = new HashMap<>();

  public Unit() {
  }

  public Unit(UnitMap unitMap, Game game) {
    this.id = Utils.generateUniqueId();
    this.pos = unitMap.getPos();
    UnitTile unitTile = game.getUnitTiles().get(unitMap.getUnitTileId());
    this.unitTileId = unitTile.getId();
    this.name = unitTile.getName();
    this.bitmap = unitTile.getBitmap();
    this.teamId = unitTile.getTeamId();
    this.hp = unitTile.getHpMax();
    this.hpMax = unitTile.getHpMax();
    this.action = unitTile.getActionMax();
    this.actionMax = unitTile.getActionMax();
    this.attr.addAll(unitTile.getAttr());
    this.moveRange = unitTile.getMoveRange();
    this.moveRestrict.addAll(unitTile.getMoveRestrict());
    this.moveActionCost = unitTile.getMoveActionCost();
    this.sightRange = unitTile.getSightRange();
    this.sightRestrict.addAll(unitTile.getSightRestrict());
    Map<String, AbilityTile> allAbilities = game.getAbilities();
    for (String abilityId : unitTile.getAbilityIds()) {
      this.abilities.add(new Ability(allAbilities.get(abilityId), game));
    }
    this.defence.addAll(unitTile.getDefence());
    for (String effectTileId : unitTile.getEffectIds()) {
      this.effects.add(new Effect(game.getEffects().get(effectTileId)));
    }
    this.storage.putAll(unitTile.getStorage());
  }

  @Override
  public TILE_TYPE getTileType() {
    return TileType.TILE_TYPE.UNIT;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Position getPos() {
    return pos;
  }

  public void setPos(Position pos) {
    this.pos = pos;
  }

  public String getUnitTileId() {
    return unitTileId;
  }

  public void setUnitTileId(String unitTileId) {
    this.unitTileId = unitTileId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Bitmap getBitmap() {
    return bitmap;
  }

  @Override
  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  public double getHp() {
    return hp;
  }

  public void setHp(double hp) {
    this.hp = hp;
  }

  public double getHpMax() {
    return hpMax;
  }

  public void setHpMax(double hpMax) {
    this.hpMax = hpMax;
  }

  public double getAction() {
    return action;
  }

  public void setAction(double action) {
    this.action = action;
  }

  public double getActionMax() {
    return actionMax;
  }

  public void setActionMax(double actionMax) {
    this.actionMax = actionMax;
  }

  public List<String> getAttr() {
    return attr;
  }

  public void setAttr(List<String> attr) {
    this.attr = attr;
  }

  public int getMoveRange() {
    return moveRange;
  }

  public void setMoveRange(int moveRange) {
    this.moveRange = moveRange;
  }

  public double getMoveActionCost() {
    return moveActionCost;
  }

  public void setMoveActionCost(double moveActionCost) {
    this.moveActionCost = moveActionCost;
  }

  public List<String> getMoveRestrict() {
    return moveRestrict;
  }

  public void setMoveRestrict(List<String> moveRestrict) {
    this.moveRestrict = moveRestrict;
  }

  public int getSightRange() {
    return sightRange;
  }

  public void setSightRange(int sightRange) {
    this.sightRange = sightRange;
  }

  public List<String> getSightRestrict() {
    return sightRestrict;
  }

  public void setSightRestrict(List<String> sightRestrict) {
    this.sightRestrict = sightRestrict;
  }

  public List<Ability> getAbilities() {
    return abilities;
  }

  public void setAbilities(List<Ability> abilities) {
    this.abilities = abilities;
  }

  public List<Damage> getDefence() {
    return defence;
  }

  public void setDefence(List<Damage> defence) {
    this.defence = defence;
  }

  public List<Effect> getEffects() {
    return effects;
  }

  public void setEffects(List<Effect> effects) {
    this.effects = effects;
  }

  public Map<String, String> getStorage() {
    return storage;
  }

  public void setStorage(Map<String, String> storage) {
    this.storage = storage;
  }

  @Override
  public String toString() {
    return "Unit{" +
        "id='" + id + '\'' +
        ", unitTileId='" + unitTileId + '\'' +
        ", name='" + name + '\'' +
        ", bitmap=" + bitmap +
        ", teamId='" + teamId + '\'' +
        ", hp=" + hp +
        ", hpMax=" + hpMax +
        ", action=" + action +
        ", actionMax=" + actionMax +
        ", attr=" + attr +
        ", moveRange=" + moveRange +
        ", moveRestrict=" + moveRestrict +
        ", sightRange=" + sightRange +
        ", sightRestrict=" + sightRestrict +
        ", abilities=" + abilities +
        ", defence=" + defence +
        ", effects=" + effects +
        ", storage=" + storage +
        '}';
  }

  @Override
  public int compareTo(@NonNull Object o) {
    if (o == null) return 0;
    return ((Unit)o).getName().toLowerCase().compareTo(name.toLowerCase());
  }
}
