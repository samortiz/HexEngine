package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AbilityTile implements TileType, Comparable {

  private String id;
  private String name;
  private Bitmap bitmap;
  private Action applies;
  private int range;
  private List<String> rangeRestrict = new ArrayList<>();
  private double actionCost;
  private List<Action> onStart = new ArrayList<>();
  private String effectId;

  public AbilityTile() {}

  public AbilityTile(String id) {
    this.id = id;
  }

  @Override
  public TILE_TYPE getTileType() {
    return TileType.TILE_TYPE.ABILITY;
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

  public Action getApplies() {
    return applies;
  }

  public void setApplies(Action applies) {
    this.applies = applies;
  }

  public int getRange() {
    return range;
  }

  public void setRange(int range) {
    this.range = range;
  }

  public List<String> getRangeRestrict() {
    return rangeRestrict;
  }

  public void setRangeRestrict(List<String> rangeRestrict) {
    this.rangeRestrict = rangeRestrict;
  }

  public double getActionCost() {
    return actionCost;
  }

  public void setActionCost(double actionCost) {
    this.actionCost = actionCost;
  }

  public List<Action> getOnStart() {
    return onStart;
  }

  public void setOnStart(List<Action> onStart) {
    this.onStart = onStart;
  }

  public String getEffectId() {
    return effectId;
  }

  public void setEffectId(String effectId) {
    this.effectId = effectId;
  }

  @Override
  public int compareTo(@NonNull Object o) {
    if (o == null) return 0;
    return name.toLowerCase().compareTo(((AbilityTile)o).getName().toLowerCase());
  }

  @Override
  public String toString() {
    return "AbilityTile{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", bitmap=" + bitmap +
        ", applies=" + applies +
        ", range=" + range +
        ", rangeRestrict=" + rangeRestrict +
        ", actionCost=" + actionCost +
        ", onStart=" + onStart +
        ", effectId='" + effectId + '\'' +
        '}';
  }
}
