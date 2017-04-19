package com.alwaysrejoice.hexengine.dto;

import android.graphics.Bitmap;

import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Effect implements TileType, Comparable {

  public String id;
  public String effectTileId;
  public String name;
  public Bitmap bitmap;
  public int duration = 0;
  public List<Action> onRun = new ArrayList<>();
  public List<Action> onEnd = new ArrayList<>();
  public boolean stackable = true;

  public Effect() {}

  public Effect(String id) {
    this.id = id;
  }

  public Effect(EffectTile effectTile) {
    this.id = Utils.generateUniqueId();
    this.effectTileId = effectTile.getId();
    this.name = effectTile.getName();
    this.bitmap = effectTile.getBitmap();
    this.duration = effectTile.getDuration();
    this.onRun.addAll(effectTile.getOnRun());
    this.onEnd.addAll(effectTile.getOnEnd());
    this.stackable = effectTile.isStackable();
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

  public String getEffectTileId() {
    return effectTileId;
  }

  public void setEffectTileId(String effectTileId) {
    this.effectTileId = effectTileId;
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
    return this.getName().toLowerCase().compareTo(((Effect)o).getName().toLowerCase());
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

  /**
   * @return a duplicate object of this one
   */
  public Effect clone() {
    Effect clone = new Effect();
    clone.setId(Utils.generateUniqueId());
    clone.setEffectTileId(effectTileId);
    clone.setName(name);
    clone.setBitmap(bitmap);
    clone.setDuration(duration);
    clone.getOnRun().addAll(onRun);
    clone.getOnEnd().addAll(onEnd);
    clone.setStackable(stackable);
    return clone;
  }
}
