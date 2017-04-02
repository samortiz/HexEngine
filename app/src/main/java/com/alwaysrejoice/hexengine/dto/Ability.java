package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Ability implements Comparable {

  private String name = "";
  private Bitmap bitmap;
  private Action applies;
  private int range;
  private List<String> rangeRestrict;
  private double actionCost;
  private List<Action> onStart = new ArrayList<>();
  private Effect effect = null;

  public Ability() {}

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
    return name.toLowerCase().compareTo(((Ability)o).getName().toLowerCase());
  }

  @Override
  public String toString() {
    return "Ability{" +
        "name='" + name + '\'' +
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
