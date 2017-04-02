package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Map;

public class UnitTile implements TileType {

  private String name;
  private Bitmap bitmap;
  private String team;
  private double hpMax;
  private double actionMax;
  private Map<String, Boolean> attributes;
  private int moveRange;
  private List<String> moveRestrict;
  private int sightRange;
  private List<String> sightRestrict;
  List<Ability> abilities;
  List<Damage> defence;
  private List<Effect> effects;
  private Map<String, String> storage;

  public UnitTile() {
  }

  public UnitTile(String name, Bitmap bitmap) {
    this.name = name;
    this.bitmap = bitmap;
  }

  public TILE_TYPE getTileType() {
    return TILE_TYPE.UNIT;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  public String getTeam() {
    return team;
  }

  public void setTeam(String team) {
    this.team = team;
  }

  public double getHpMax() {
    return hpMax;
  }

  public void setHpMax(double hpMax) {
    this.hpMax = hpMax;
  }

  public double getActionMax() {
    return actionMax;
  }

  public void setActionMax(double actionMax) {
    this.actionMax = actionMax;
  }

  public Map<String, Boolean> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Boolean> attributes) {
    this.attributes = attributes;
  }

  public int getMoveRange() {
    return moveRange;
  }

  public void setMoveRange(int moveRange) {
    this.moveRange = moveRange;
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
  public int compareTo(Object o) {
    if (o == null) {
      return 0;
    }
    return this.getName().compareTo(((TileType) o).getName());
  }

  @Override
  public String toString() {
    return "UnitTile{" +
        "name='" + name + '\'' +
        ", bitmap=" + bitmap +
        ", team='" + team + '\'' +
        ", hpMax=" + hpMax +
        ", actionMax=" + actionMax +
        ", attributes=" + attributes +
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
}
