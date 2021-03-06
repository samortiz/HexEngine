package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Ability implements TileType, Comparable {

  private String id;
  private String abilityTileId;
  private String name;
  private Bitmap bitmap;
  private Action applies;
  private int range;
  private List<String> rangeRestrict = new ArrayList<>();
  private double actionCost;
  private List<Action> onStart = new ArrayList<>();
  private Effect effect = null;

  public Ability() {}

  public Ability(AbilityTile abilityTile, Game game) {
    this.id = Utils.generateUniqueId();
    this.abilityTileId = abilityTile.getId();
    this.name = abilityTile.getName();
    this.bitmap = abilityTile.getBitmap();
    this.applies = abilityTile.getApplies();
    this.range = abilityTile.getRange();
    this.rangeRestrict.addAll(abilityTile.getRangeRestrict());
    this.actionCost = abilityTile.getActionCost();
    this.onStart.addAll(abilityTile.getOnStart());
    EffectTile effectTile = game.getEffects().get(abilityTile.getEffectId());
    if (effectTile != null) {
      this.effect = new Effect(effectTile);
    }
  }

  @Override
  public TILE_TYPE getTileType() {
    return TILE_TYPE.ABILITY;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAbilityTileId() {
    return abilityTileId;
  }

  public void setAbilityTileId(String abilityTileId) {
    this.abilityTileId = abilityTileId;
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

  public Effect getEffect() {
    return effect;
  }

  public void setEffect(Effect effect) {
    this.effect = effect;
  }

  @Override
  public int compareTo(@NonNull Object o) {
    if (o == null) return 0;
    return name.toLowerCase().compareTo(((Ability)o).getName().toLowerCase());
  }

  @Override
  public String toString() {
    return "Ability{" +
        "id='" + id + '\'' +
        ", abilityTileId='" + abilityTileId + '\'' +
        ", name='" + name + '\'' +
        ", bitmap=" + bitmap +
        ", applies=" + applies +
        ", range=" + range +
        ", rangeRestrict=" + rangeRestrict +
        ", actionCost=" + actionCost +
        ", onStart=" + onStart +
        ", effect=" + effect +
        '}';
  }
}
