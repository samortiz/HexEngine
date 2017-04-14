package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class EffectTile implements TileType, Comparable {

  private String id;
  private String name;
  private Bitmap bitmap;
  private int duration = 0;
  private List<Action> onRun = new ArrayList<>();
  private List<Action> onEnd = new ArrayList<>();
  private boolean stackable = false;

  public EffectTile() {}

  public EffectTile(String id) {
    this.id = id;
  }

  @Override
  public TILE_TYPE getTileType() {
    return TILE_TYPE.EFFECT;
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

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public List<Action> getOnRun() {
    return onRun;
  }

  public void setOnRun(List<Action> onRun) {
    this.onRun = onRun;
  }

  public List<Action> getOnEnd() {
    return onEnd;
  }

  public void setOnEnd(List<Action> onEnd) {
    this.onEnd = onEnd;
  }

  public boolean isStackable() {
    return stackable;
  }

  public void setStackable(boolean stackable) {
    this.stackable = stackable;
  }

  @Override
  public int compareTo(Object o) {
    if (o == null) return 0;
    return this.getName().toLowerCase().compareTo(((EffectTile)o).getName().toLowerCase());
  }

  @Override
  public String toString() {
    return "EffectTile{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", bitmap=" + bitmap +
        ", duration=" + duration +
        ", onRun=" + onRun +
        ", onEnd=" + onEnd +
        ", stackable=" + stackable +
        '}';
  }
}
