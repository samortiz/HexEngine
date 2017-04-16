package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitTile implements TileType {

  private String id;
  private String name;
  private Bitmap bitmap;
  private String teamId;
  private double hpMax;
  private double actionMax;
  private List<String> attr = new ArrayList<>();
  private int moveRange;
  private List<String> moveRestrict = new ArrayList<>();
  private double moveActionCost;
  private int sightRange;
  private List<String> sightRestrict = new ArrayList<>();
  private List<String> abilityIds = new ArrayList<>(); // ability ids
  private List<Damage> defence = new ArrayList<>();
  private List<String> effectIds = new ArrayList<>(); // effect ids
  private Map<String, String> storage = new HashMap<>();

  public UnitTile() {
  }

  public UnitTile(String id) {
    this.id = id;
  }

  public UnitTile(String id, String name, Bitmap bitmap) {
    this.id = id;
    this.name = name;
    this.bitmap = bitmap;
  }

  public TILE_TYPE getTileType() {
    return TILE_TYPE.UNIT_TILE;
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

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
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

  public List<String> getMoveRestrict() {
    return moveRestrict;
  }

  public void setMoveRestrict(List<String> moveRestrict) {
    this.moveRestrict = moveRestrict;
  }

  public double getMoveActionCost() {
    return moveActionCost;
  }

  public void setMoveActionCost(double moveActionCost) {
    this.moveActionCost = moveActionCost;
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

  public List<String> getAbilityIds() {
    return abilityIds;
  }

  public void setAbilityIds(List<String> abilityIds) {
    this.abilityIds = abilityIds;
  }
  public List<Damage> getDefence() {
    return defence;
  }

  public void setDefence(List<Damage> defence) {
    this.defence = defence;
  }

  public List<String> getEffectIds() {
    return effectIds;
  }

  public void setEffectIds(List<String> effectIds) {
    this.effectIds = effectIds;
  }

  public Map<String, String> getStorage() {
    return storage;
  }

  public void setStorage(Map<String, String> storage) {
    this.storage = storage;
  }

  @Override
  public int compareTo(Object o) {
    if (o == null) return 0;
    return this.getName().compareTo(((TileType) o).getName());
  }

  @Override
  public String toString() {
    return "UnitTile{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", bitmap=" + bitmap +
        ", teamId='" + teamId + '\'' +
        ", hpMax=" + hpMax +
        ", actionMax=" + actionMax +
        ", attr=" + attr +
        ", moveRange=" + moveRange +
        ", moveRestrict=" + moveRestrict +
        ", moveActionCost=" + moveActionCost +
        ", sightRange=" + sightRange +
        ", sightRestrict=" + sightRestrict +
        ", abilityIds=" + abilityIds +
        ", defence=" + defence +
        ", effectIds=" + effectIds +
        ", storage=" + storage +
        '}';
  }
}
